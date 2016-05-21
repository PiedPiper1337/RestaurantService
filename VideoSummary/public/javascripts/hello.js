var gCurrentTime = 0; // Horrible global state for now, I'm so sorry
var gTimerid;
var gSlices;
var gSliceIndex = 0;

/**
 * The player object that allows us to interact with the iframe
 * */
//var player;
//function onYouTubeIframeAPIReady() {
//    player = new YT.Player('videoDiv', {
//        height: '450',
//        width: '800',
//        videoId: window.vvv,
//        playervars:{
//            "enablejsapi":1,
//            "origin":document.domain,
//            "rel":0
//        },
//        events: {
//            'onReady': onPlayerReady,
//            'onStateChange': onPlayerStateChange
//        }
//    });
//}

function setSummarizationStatus(str) {
    $("#summarize-status").text(str);
}

function highlightPlaylistIndex(index) {
    $($("#playlist-div").children()[index]).addClass("sel");}

function removeHighlightPlaylistIndex(index) {
    $($("#playlist-div").children()[gSliceIndex]).removeClass("sel");
}

function nextSlice() {
    if (gSliceIndex < gSlices.length) {
        console.log("gSliceIndex " + gSliceIndex + " length" + gSlices.length);
        player.seekTo(gSlices[gSliceIndex + 1].startTimeSeconds);
    } else {
        stopSummarization();
        stopVideo();
        setSummarizationStatus("Stopped");
    }
}

/**
 * Function that is executed once the iframe is ready to play
 * */
//function onPlayerReady(event) {
//    //event.target.playVideo();
//    //playSlices(testSlices);
//}

/**
 * Function that is executed when the state of the player changes
 * */
function onPlayerStateChange(event) {
    if (event.data == YT.PlayerState.ENDED) {
        clearInterval(gTimerid);
        setSummarizationStatus("Stopped");
    }
}

function stopSummarization() {
    clearInterval(gTimerid);
    $($("#playlist-div").children()[gSliceIndex]).removeClass("sel");
    //gSliceIndex = 0;
}

function stopVideo() {
    player.stopVideo();
}

/**
 * Function to poll the video periodically, getting the updated progress of the video
 * Don't run this directly
 * */
function checkCurrentTime(timeSlices) {
    if (player.getPlayerState() == YT.PlayerState.PLAYING && player.getCurrentTime() > timeSlices[0].endTimeSeconds) {
        timeSlices.splice(0, 1);

        if (timeSlices.length > 0) {
            player.pauseVideo();
            player.seekTo(timeSlices[0].startTimeSeconds);
            console.log("Seeking to " + timeSlices[0].startTimeSeconds);
            player.playVideo();
            removeHighlightPlaylistIndex(gSliceIndex);
            gSliceIndex++;
            highlightPlaylistIndex(gSliceIndex);
        } else {
            // Reset everything and stop the video
            stopSummarization();
            stopVideo();
            setSummarizationStatus("Stopped");
            console.log("Length is zero and things are playing");
        }
    }
}

document.addEventListener("DOMContentLoaded", function () {
    $("#summarizeButton").on('click', function() {
        $("#summarizeButton").prop("disabled", true);
        setSummarizationStatus("Summarizing...");
        var apiRequest = $.post("/times/" + window.vvv, function(resp) {
            $("#summarizeButton").prop("disabled", false);
            var response =  JSON.parse(resp);
            var groups = response.Groups;
            var wordcloud = response.WordCloud;
            var histogram = response.Histogram;
            console.log(JSON.stringify(groups));
            var slices = groups;
            gSlices = groups.slice();
            setSummarizationStatus("Successfully retrieved summary, playing...");
            if (slices.length > 0) {
                $("#playlist-div").empty(); // Clear the playlist
                for (var i = 0; i < slices.length; i++) {
                    $("#playlist-div").append('<div class="section">' + (i+1) + '.) ' +
                        slices[i].startTime + ' - ' + slices[i].endTime + '<br/>' +
                        slices[i].wordsSpoken.substring(0, 50) +
                        '...</div>');
                }

                highlightPlaylistIndex(gSliceIndex); // Highlight the current playlist index

                player.pauseVideo();
                player.seekTo(slices[0].startTimeSeconds);
                player.playVideo();
            }

            gTimerid = setInterval(checkCurrentTime, 500, slices);

            var werds = [];

            for (var key in wordcloud) {
                if (wordcloud.hasOwnProperty(key)) {
                    werds.push({
                        text: key,
                        size: wordcloud[key]
                    });
                }
            }

            /*
            * Add the analytics section of the page
            * */
            $("#wordcloud-div").remove();
            $("#histogram-div").remove();
            $("body").append('<div id="wordcloud-div"></div>');
            $("body").append('<div id="histogram-div"></div>');

            /*
            * Add the word cloud
            * */
            var fill = d3.scale.category20();

            d3.layout.cloud().size([500, 500])
                .words(werds)
                .rotate(function() { return ~~(Math.random() * 2) * 90; })
                .font("Impact")
                .fontSize(function(d) { return d.size; })
                .on("end", draw)
                .start();

            function draw(words) {
                d3.select("#wordcloud-div").append("svg")
                    .attr("width", 500)
                    .attr("height", 500)
                    .append("g")
                    .attr("transform", "translate(250,250)")
                    .selectAll("text")
                    .data(words)
                    .enter().append("text")
                    .style("font-size", function(d) { return d.size + "px"; })
                    .style("font-family", "Impact")
                    .style("fill", function(d, i) { return fill(i); })
                    .attr("text-anchor", "middle")
                    .attr("transform", function(d) {
                        return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
                    })
                    .text(function(d) { return d.text; });
            }

            var histogramX = [];
            var histogramY = [];

            for (var i = 0; i < histogram.length; i++) {
                histogramY.push(histogram[i].importance);
                histogramX.push(histogram[i].startTimeSeconds);
            }

            var data = [
                {
                    x: histogramX,
                    y: histogramY,
                    mode: 'lines+markers',
                    type: 'scatter'
                }
            ];

            var layout = {
                title: "Importance vs. Time",
                xaxis: {title: "Seconds"},
                yaxis: {title: "Importance"}
            };

            Plotly.newPlot('histogram-div', data, layout);
        });

        apiRequest.fail(function(data) {
            $("#summarizeButton").prop("disabled", false);
            setSummarizationStatus("Response from server was a failure " + JSON.stringify(data)); // TODO create better error response
        });
    });

    $("#stopButton").on('click', function() {
        stopSummarization();
        setSummarizationStatus("Stopped");
        stopVideo();
    });

    $("#nextButton").on('click', function() {
       nextSlice();
    });
}, false);