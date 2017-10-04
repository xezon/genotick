package com.alphatica.genotick.genotick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alphatica.genotick.data.DataSeries;
import com.alphatica.genotick.data.DataSet;
import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.timepoint.TimePoint;

public class RobotDataManager {
    
    private class RobotDataContainer {
        public final DataSeries robotDataSeries;
        public final RobotData robotData;
        
        RobotDataContainer(DataSetName name) {
            robotDataSeries = new DataSeries(true);
            robotData = RobotData.create(name, robotDataSeries);
        }
    }
    
    private final MainAppData data;
    private final int maxBars;
    private final List<RobotDataContainer> robotDataList;
    private final List<RobotData> updatedRobotDataList;

    RobotDataManager(MainAppData data, int maxBars) {
        this.data = data;
        this.maxBars = maxBars;
        this.robotDataList = new ArrayList<>();
        this.updatedRobotDataList = Collections.synchronizedList(new ArrayList<>());
        for (DataSet dataSet : data.getDataSets()) {
            robotDataList.add(new RobotDataContainer(dataSet.getName()));
        }
    }
    
    List<RobotData> getUpdatedRobotDataList() {
        return updatedRobotDataList;
    }
    
    void update(TimePoint timePoint) {
        updatedRobotDataList.clear();
        robotDataList.parallelStream().forEach(container -> {
            final DataSetName name = container.robotData.getName();
            final DataSet dataSet = data.getDataSet(name);
            final int bar = dataSet.getBar(timePoint);
            if (bar >= 0) {
                dataSet.fetchOhlcDataSection(container.robotDataSeries, bar, maxBars);
                updatedRobotDataList.add(container.robotData);
            }
        });
    }
}
