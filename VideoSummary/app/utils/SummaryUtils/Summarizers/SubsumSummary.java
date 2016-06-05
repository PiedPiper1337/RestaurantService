package utils.SummaryUtils.Summarizers;

import com.fasterxml.jackson.databind.JsonNode;
import suk.code.SubjectiveLogic.MDS.SubSumGenericMDS;
import utils.SummaryUtils.SummaryTools.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by brianzhao on 6/4/16.
 */
public class SubsumSummary implements Summary {
    private final Transcript transcript;

    public SubsumSummary(Transcript transcript) {
        this.transcript = transcript;
    }

    @Override
    public List<Group> generateSummary() {
        //TODO change this away from 10 percent to something non hard coded?
        List<String> result = SubSumGenericMDS.getData(Collections.singletonList(transcript.getTranscriptWithoutTimeValues()), 10);
        List<List<TimeRegion>> potentialGroups = new ArrayList<>();
        double highestImportance = result.size();
        for (String sentence : result) {
            List<TimeRegion> correspondingTimeRegions = transcript.stringToTimeRegions(sentence);
            for (TimeRegion timeRegion : correspondingTimeRegions) {
                timeRegion.setImportance(highestImportance);
            }
            potentialGroups.add(correspondingTimeRegions);
            highestImportance -= 1;
        }


        //TODO maybe normalize on duration? is it necessary (does subsum do its own)?
        List<Group> toReturn = new ArrayList<>();
        Group currentGroup = new Group();
        for (List<TimeRegion> timeRegions : potentialGroups) {
            TimeRegion lastInGroup = currentGroup.getLast();
            if (lastInGroup == null) {
                for (TimeRegion timeRegion : timeRegions) {
                    currentGroup.add(timeRegion, false);
                }
            } else {
                if (lastInGroup.isAdjacentTo(timeRegions.get(0))) {
                    for (TimeRegion timeRegion : timeRegions) {
                        currentGroup.add(timeRegion, false);
                    }
                } else {
                    toReturn.add(currentGroup);
                    currentGroup = new Group();
                    for (TimeRegion timeRegion : timeRegions) {
                        currentGroup.add(timeRegion, false);
                    }
                }
            }
        }
        Collections.sort(toReturn, GroupComparators.startTime);
        return toReturn;
    }

    @Override
    public Map<String, Integer> generateWordCloud() {
        return null;
    }

    @Override
    public JsonNode histogram() {
        return null;
    }

    @Override
    public double cutOffValue() {
        return 0;
    }
}
