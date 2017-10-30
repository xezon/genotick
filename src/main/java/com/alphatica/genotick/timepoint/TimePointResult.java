package com.alphatica.genotick.timepoint;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.genotick.DataSetResult;
import com.alphatica.genotick.genotick.Prediction;
import com.alphatica.genotick.genotick.RobotResult;
import com.alphatica.genotick.genotick.RobotResultPair;
import com.alphatica.genotick.population.RobotName;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class TimePointResult {
    private final Map<DataSetName, DataSetResult> dataSetResultMap;

    public TimePointResult(Map<RobotName, List<RobotResultPair>> robotResultMap, boolean requireSymmetricalRobots) {
        dataSetResultMap = new HashMap<>();
        Stream<List<RobotResultPair>> stream = robotResultMap.values().stream();
        if (requireSymmetricalRobots) {
            stream.filter(results -> resultsAreSymmetrical(results)).flatMap(Collection::stream).forEach(this::addRobotResult);
        }
        else {
            stream.flatMap(Collection::stream).forEach(this::addRobotResult);
        }
    }

    public Collection<DataSetResult> listDataSetResults() {
        return dataSetResultMap.values();
    }

    private boolean resultsAreSymmetrical(List<RobotResultPair> results) {
        for (RobotResultPair pair : results) {
            RobotResult originalResult = pair.getOriginal();
            RobotResult reversedResult = pair.getReversed();
            Objects.requireNonNull(reversedResult);
            Prediction originalPred = originalResult.getPrediction();
            Prediction reversedPred = reversedResult.getPrediction();
            if (originalPred != Prediction.getOpposite(reversedPred)) {
                return false;
            }
        }
        return true;
    }

    private void addRobotResult(RobotResultPair pair) {
        pair.forEach(this::addRobotResult);
    }
    
    private void addRobotResult(RobotResult robotResult) {
        DataSetName name = robotResult.getDataSetName();
        DataSetResult dataSetResult = getDataSetResult(name);
        dataSetResult.addResult(robotResult);
    }

    private DataSetResult getDataSetResult(DataSetName name) {
        return dataSetResultMap.computeIfAbsent(name, DataSetResult::new);
    }

}
