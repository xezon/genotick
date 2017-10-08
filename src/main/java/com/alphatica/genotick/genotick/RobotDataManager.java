package com.alphatica.genotick.genotick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alphatica.genotick.data.DataSeries;
import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.timepoint.TimePoint;

public class RobotDataManager {

    private final MainAppData data;
    private final int maxBars;
    private final List<RobotData> robotDataList;

    RobotDataManager(MainAppData data, int maxBars) {
        this.data = data;
        this.maxBars = maxBars;
        this.robotDataList = Collections.synchronizedList(new ArrayList<>());
    }
    
    List<RobotData> getUpdatedRobotDataList() {
        return robotDataList;
    }
    
    void update(TimePoint timePoint) {
        robotDataList.clear();
        data.getDataSets().parallelStream().forEach(dataSet -> {
            final DataSetName name = dataSet.getName();
            final int bar = dataSet.getBar(timePoint);
            if (bar >= 0) {
                final DataSeries newSeries = dataSet.createOhlcDataSection(bar, maxBars, true);
                robotDataList.add(RobotData.create(name, newSeries));
            }
        });
    }
}
