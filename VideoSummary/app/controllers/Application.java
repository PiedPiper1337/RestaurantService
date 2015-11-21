package controllers;

import com.google.inject.Inject;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.WordToSentenceProcessor;
import play.Logger;
import play.cache.CacheApi;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import utils.Constants;
import utils.Pipeline;
import utils.StringManip;
import utils.Summarizer.*;
import views.html.video;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


/**
 * HOW TO DEBUG USING PLAY FRAMEWORK + INTELLIJ: https://www.playframework.com/documentation/2.4.x/IDE
 */

public class Application extends Controller {
    private static final org.slf4j.Logger logger = Logger.of(Application.class).underlying();

    @Inject
    CacheApi cache;

    @With(IPAction.class)
    public Result index() {
        logger.trace("index method called");
        return ok(views.html.index.render());
    }

    /**
     * we should replace this with a fail whale picture when we deploy
     *
     * @param badResource
     * @return
     */
    public Result genericFailure(String badResource) {
        logger.trace("bad url attempt at {}", badResource);
        return badRequest("This page does not exist.");
    }

    public Result blank() {
        return ok();
    }

    public Result summaryTimes(String videoId) {
        logger.debug("got a POST");
        String result = cache.get(videoId);
        if (result == null) {
            Summary summaryResult = SummaryFactory.generateBasicSummary(videoId);
            if (summaryResult == null) {
                return internalServerError("Sorry but we had an error processing your video");
            }
            List<Group> summaryGroups = summaryResult.generateSummary();
            result = Json.toJson(summaryGroups).toString();
            cache.set(videoId, result, Constants.CACHE_TIME);
        }
        return ok(result);
    }

    /**
     * takes a http request with v parameter
     * checks if the url is directly inside 'v' or inside an entire url
     * displays youtube video
     *
     * @return
     */
    public Result displayVideo() {
        String videoId = request().getQueryString("v");
        if (videoId == null) {
            logger.debug("video query was null, redirecting to index");
//            return redirect(controllers.routes.Application.index());
            return redirect("/");
        }

        if (StringManip.isFullUrl(videoId)) {
            videoId = StringManip.extractParameter(videoId, "v");
        }

        String videoURLToEmbed = videoId; //Constants.EMBED_URL + videoId;
        logger.debug("The video ID is: {}", videoURLToEmbed);
        return ok(video.render(videoURLToEmbed));
    }

    public Result displayTranscript() {
        logger.trace("transcript method called");
        String vParameter = request().getQueryString("v");
        if (vParameter == null) {
            logger.debug("video query was null, redirecting to index");
//            return redirect(controllers.routes.Application.index());
            return redirect("/");
        }
        Transcript transcript = TranscriptFactory.getTranscript(vParameter);
        if (transcript == null) {
            return internalServerError("Sorry but we had an error processing your video");
        }
        return ok(transcript.toString());
    }

    public Result runNLP() throws Exception {
        logger.debug("Processing aScandalInBohemia");
        String title = "NLPData/aScandalInBohemia";
        String analyzed = Pipeline.pos(title);
        return ok(analyzed);
    }

    public Result summarize() {
        logger.trace("Summarization");
        String videoId = request().getQueryString("v");
        if (videoId == null) {
            logger.debug("summarization parameter query was null, redirecting to index");
            return redirect("/");
        }
        Summary summaryResult = SummaryFactory.generateBasicSummary(videoId);
        if (summaryResult == null) {
            return internalServerError("Sorry but we had an error processing your video");
        }
        List<Group> summaryGroups = summaryResult.generateSummary();

        cache.set(videoId, Json.toJson(summaryGroups).toString(), Constants.CACHE_TIME);
        return ok(summaryGroups.toString());
    }

    public Result testPunctuation(String videoId) {
        return ok(TranscriptFactory.getTranscript(videoId).getTimeRegions().toString());


//        String paragraph = "First sentence is good. Second sentence is here. I am hungry.";
//        List<String> sentenceList = new ArrayList<>();
//        List<CoreLabel> tokens = new ArrayList<CoreLabel>();
//        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<CoreLabel>(new StringReader(paragraph), new CoreLabelTokenFactory(), "");
//        while (tokenizer.hasNext()) {
//            tokens.add(tokenizer.next());
//        }
//// Split sentences from tokens
//        List<List<CoreLabel>> sentences = new WordToSentenceProcessor<CoreLabel>().process(tokens);
//// Join back together
//        int end;
//        int start = 0;
//        for (List<CoreLabel> sentence : sentences) {
//            end = sentence.get(sentence.size() - 1).endPosition();
//            sentenceList.add(paragraph.substring(start, end).trim());
//            start = end;
//        }
//        StringBuilder toReturn = new StringBuilder();
//        for (String s : sentenceList) {
//            toReturn.append(s).append("\n");
//        }
//        return ok(toReturn.toString());


//            Transcript transcript = TranscriptFactory.getTranscript(videoId);
//        return ok("" + transcript.isAutoGenerated());
//        return ok(result.toString());

    }

}
