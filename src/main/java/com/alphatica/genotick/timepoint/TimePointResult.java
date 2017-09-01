package com.alphatica.genotick.timepoint;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.genotick.DataSetResult;
import com.alphatica.genotick.genotick.Prediction;
import com.alphatica.genotick.genotick.RobotResult;
import com.alphatica.genotick.population.RobotName;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimePointResult {
    private final Map<DataSetName, DataSetResult> dataSetResultMap;

    public TimePointResult(Map<RobotName, List<RobotResult>> map) {
        dataSetResultMap = new HashMap<>();
        map.values().stream().filter(this::resultsSymmetrical).flatMap(Collection::stream).forEach(this::addRobotResult);
    }

    public Collection<DataSetResult> listDataSetResults() {
        return dataSetResultMap.values();
    }

    private boolean resultsSymmetrical(List<RobotResult> results) {
        long votesUp = results.stream().filter(result -> result.getPrediction() == Prediction.UP).count();
        long votesDown = results.stream().filter(result -> result.getPrediction() == Prediction.DOWN).count();
        return votesUp == votesDown;
    }

    private void addRobotResult(RobotResult robotResult) {
        DataSetName name = robotResult.getData().getName();
        DataSetResult dataSetResult = getDataSetResult(name);
        dataSetResult.addResult(robotResult);
    }

    private DataSetResult getDataSetResult(DataSetName name) {
        DataSetResult dataSetResult = dataSetResultMap.get(name);
        if(dataSetResult == null) {
            dataSetResult = new DataSetResult(name);
            dataSetResultMap.put(name,dataSetResult);
        }
        return dataSetResult;
    }

}
