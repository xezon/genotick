package com.alphatica.genotick.timepoint;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.genotick.DataSetResult;
import com.alphatica.genotick.genotick.RobotResult;
import com.alphatica.genotick.population.RobotName;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimePointResult {
    private final Map<DataSetName, DataSetResult> dataSetResultMap;

    public TimePointResult() {
        dataSetResultMap = new HashMap<>();
    }

    public DataSetResult[] listDataSetResults() {
        DataSetResult [] array = new DataSetResult[dataSetResultMap.size()];
        int i = 0;
        for(Map.Entry<DataSetName,DataSetResult> entry: dataSetResultMap.entrySet()) {
            array[i++] = entry.getValue();
        }
        return array;
    }

    public void build(Map<RobotName, List<RobotResult>> map) {
        map.values().stream().flatMap(Collection::stream).forEach(this::addRobotResult);
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
