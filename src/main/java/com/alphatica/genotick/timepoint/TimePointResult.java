package com.alphatica.genotick.timepoint;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.genotick.DataSetResult;
import com.alphatica.genotick.genotick.RobotResult;
import com.alphatica.genotick.genotick.RobotResultPair;
import com.alphatica.genotick.population.RobotName;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimePointResult {
    private final Map<DataSetName, DataSetResult> dataSetResultMap;

    public TimePointResult(Map<RobotName, List<RobotResultPair>> robotResultMap) {
        dataSetResultMap = new HashMap<>();
        robotResultMap.values().stream().flatMap(Collection::stream).forEach(this::addRobotResult);
    }

    public Collection<DataSetResult> get() {
        return dataSetResultMap.values();
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
