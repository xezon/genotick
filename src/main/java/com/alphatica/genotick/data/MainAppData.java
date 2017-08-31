package com.alphatica.genotick.data;

import com.alphatica.genotick.genotick.RobotData;
import com.alphatica.genotick.timepoint.TimePoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.binarySearch;

public class MainAppData {
    private final Map<DataSetName, DataSet> sets;

    private final List<TimePoint> timePoints;

    public MainAppData() {
        sets = new HashMap<>();
        timePoints = new ArrayList<>();
    }

    public void addDataSet(DataSet set) {
        sets.put(set.getName(), set);
        updateTimePoints(set.getTimePoints());
    }

    private void updateTimePoints(List<TimePoint> newTimePoints) {
        Set<TimePoint> set = new HashSet<>(this.timePoints);
        set.addAll(newTimePoints);
        timePoints.clear();
        timePoints.addAll(set);
        timePoints.sort(TimePoint::compareTo);
    }

    public List<RobotData> prepareRobotDataList(final TimePoint timePoint) {
        List<RobotData> list = Collections.synchronizedList(new ArrayList<>());
        sets.entrySet().parallelStream().forEach((Map.Entry<DataSetName, DataSet> entry) -> {
            RobotData robotData = entry.getValue().getRobotData(timePoint);
            if (!robotData.isEmpty())
                list.add(robotData);

        });
        return list;
    }

    public TimePoint getFirstTimePoint() {
        if (sets.isEmpty())
            return null;
        return timePoints.get(0);
    }

    public TimePoint getNextTimePint(TimePoint now) {
        int index = binarySearch(timePoints, now);
        if(index < 0) {
            index = Math.abs(index + 1);
        }
        if(index > timePoints.size() - 2) {
            return null;
        } else  {
            return timePoints.get(index+1);
        }
    }

    public TimePoint getLastTimePoint() {
        if (sets.isEmpty())
            return null;
        return timePoints.get(timePoints.size()-1);
    }


    public Collection<DataSet> listSets() {
        return sets.values();
    }

    boolean isEmpty() {
        return sets.isEmpty();
    }

}
