package com.alphatica.genotick.genotick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alphatica.genotick.data.DataSeries;
import com.alphatica.genotick.data.DataSet;
import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.timepoint.TimePoint;
import com.alphatica.genotick.utility.Friendship;

public class RobotDataManager extends Friendship {
        
    private final MainAppData data;
    private final int maxBars;
    private final List<RobotData> robotDataList;
    private final List<RobotData> updatedRobotDataList;

    RobotDataManager(MainAppData data, int maxBars) {
        this.data = data;
        this.maxBars = maxBars;
        this.robotDataList = new ArrayList<>();
        this.updatedRobotDataList = Collections.synchronizedList(new ArrayList<>());
        for (DataSet dataSet : data.getDataSets()) {
            final DataSetName name = dataSet.getName();
            final DataSeries emptyLookbackData = new DataSeries(true);
            robotDataList.add(RobotData.create(name, emptyLookbackData));
        }
    }
    
    List<RobotData> getUpdatedRobotDataList() {
        return updatedRobotDataList;
    }
    
    void update(TimePoint timePoint) {
        updatedRobotDataList.clear();
        robotDataList.parallelStream().forEach(robotData -> {
            final DataSetName name = robotData.getName();
            final DataSet dataSet = data.getDataSet(name);
            final int bar = dataSet.getBar(timePoint);
            if (bar >= 0) {
                final DataSeries ohlcDataSource = dataSet.getOhlcData(befriend);
                final DataSeries ohlcLookbackData = robotData.getOhlcLookbackData(befriend);
                ohlcLookbackData.copySection(ohlcDataSource, bar, maxBars);
                updatedRobotDataList.add(robotData);
            }
        });
    }
}
