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
            final DataSeries emptyLookbackData = new DataSeries(0, 0);
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
                updateLookbackData(ohlcDataSource, ohlcLookbackData, bar);
                updatedRobotDataList.add(robotData);
            }
        });
    }
    
    private void updateLookbackData(
            final DataSeries ohlcDataSource,
            final DataSeries ohlcLookbackData,
            final int bar) {
        final int expectedBarCount = (bar > maxBars) ? maxBars : bar;
        final int allocatedBarCount = ohlcLookbackData.barCount();
        final int expectedcolumnCount = ohlcDataSource.columnCount();
        final int allocatedColumnCount = ohlcLookbackData.columnCount();
        if ((expectedBarCount != allocatedBarCount) || (expectedcolumnCount != allocatedColumnCount)) {
            ohlcLookbackData.allocate(expectedcolumnCount, expectedBarCount);
        }
        fillLookbackData(ohlcDataSource, ohlcLookbackData, bar, expectedBarCount);
    }
    
    private static void fillLookbackData(
            final DataSeries ohlcDataSource,
            final DataSeries ohlcLookbackData,
            final int bar,
            final int barCount) {
        final int columnCount = ohlcDataSource.columnCount();
        for (int column = 0; column < columnCount; ++column) {
            final double[] src = ohlcDataSource.get(column);
            final double[] dst = ohlcLookbackData.get(column);
            for (int b = 0; b < barCount; ++b) {
                dst[b] = src[bar - b];
            }
        }
    } 
}
