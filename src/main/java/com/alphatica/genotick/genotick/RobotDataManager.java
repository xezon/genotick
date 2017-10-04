package com.alphatica.genotick.genotick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alphatica.genotick.data.Column;
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
            final List<double[]> emptyLookbackData = new ArrayList<>();
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
                final List<double[]> ohlcColumnsOfData = dataSet.getOhlcColumnsOfData(befriend);
                final List<double[]> ohlcLookbackData = robotData.getOhlcLookbackData(befriend);
                updateLookbackData(ohlcColumnsOfData, ohlcLookbackData, bar);
                updatedRobotDataList.add(robotData);
            }
        });
    }
        
    private void updateLookbackData(
            final List<double[]> ohlcColumnsOfData,
            final List<double[]> ohlcLookbackData,
            final int bar) {
        final int barCount = (bar >= maxBars) ? maxBars : bar + 1;
        final int allocatedBarCount = getBarCount(ohlcLookbackData);
        final int columnCount = getColumnCount(ohlcColumnsOfData);
        final int allocatedColumnCount = getColumnCount(ohlcLookbackData);
        if ((barCount != allocatedBarCount) || (columnCount != allocatedColumnCount)) {
            allocateLookbackData(ohlcLookbackData, columnCount, barCount);
        }
        fillLookbackData(ohlcColumnsOfData, ohlcLookbackData, bar, barCount);
    }
    
    private static int getColumnCount(final List<double[]> ohlcData) {
        return ohlcData.size();
    }
    
    private static int getBarCount(final List<double[]> ohlcData) {
        return !ohlcData.isEmpty() ? ohlcData.get(Column.OHLCV.OPEN).length : 0;
    }
    
    private static void allocateLookbackData(
            final List<double[]> ohlcLookbackData,
            final int columnCount,
            final int barCount) {
        ohlcLookbackData.clear();
        for (int i = 0; i < columnCount; ++i) {
            ohlcLookbackData.add(new double[barCount]);
        }
    }
    
    private static void fillLookbackData(
            final List<double[]> ohlcColumnsOfData,
            final List<double[]> ohlcLookbackData,
            final int bar,
            final int barCount) {
        final int columnCount = ohlcColumnsOfData.size();
        for (int column = 0; column < columnCount; ++column) {
            final double[] src = ohlcColumnsOfData.get(column);
            final double[] dst = ohlcLookbackData.get(column);
            for (int b = 0; b < barCount; ++b) {
                dst[b] = src[bar - b];
            }
        }
    } 
}
