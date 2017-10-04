package com.alphatica.genotick.data;

import com.alphatica.genotick.genotick.RobotData;
import com.alphatica.genotick.timepoint.TimePoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO change nulls to optional
public class MainAppData {
    
    private final Map<DataSetName, DataSet> sets;
    private final Map<TimePoint, Integer> bars;
    private List<TimePoint> timePoints;

    public MainAppData() {
        sets = new HashMap<>();
        bars = new HashMap<>();
        timePoints = new ArrayList<>();
    }

    public void addDataSet(DataSet set) {
        sets.put(set.getName(), set);
        updateTimePoints(set.getTimePoints());
    }

    private void updateTimePoints(List<TimePoint> newTimePoints) {
        if (timePoints.isEmpty()) {
            timePoints.addAll(newTimePoints);
        }
        else {
            timePoints.addAll(newTimePoints);
            timePoints = timePoints.stream().distinct().collect(Collectors.toList());
            timePoints.sort(TimePoint::compareTo);
        }
        updateBars();
    }
    
    private void updateBars() {
        bars.clear();
        for (int bar = 0, size = timePoints.size(); bar < size; ++bar) {
            bars.put(timePoints.get(bar), bar);
        }
    }

    public List<RobotData> createRobotDataList(final TimePoint timePoint) {
        List<RobotData> list = Collections.synchronizedList(new ArrayList<>());
        sets.entrySet().parallelStream().forEach((Map.Entry<DataSetName, DataSet> entry) -> {
            RobotData robotData = entry.getValue().getRobotData(timePoint);
            if (!robotData.isEmpty())
                list.add(robotData);
        });
        return list;
    }

    public TimePoint getFirstTimePoint() {
        if (timePoints.isEmpty())
            return null;
        return timePoints.get(0);
    }

    public TimePoint getLastTimePoint() {
        if (timePoints.isEmpty())
            return null;
        return timePoints.get(timePoints.size() - 1);
    }
    
    public TimePoint getTimePoint(int bar) {
        return isValidBar(bar) ? timePoints.get(bar) : null;
    }

    public int getBar(TimePoint timePoint) {
        Integer bar = bars.get(timePoint);
        if (bar == null) {
            Comparator<TimePoint> comparator = (TimePoint a, TimePoint b) -> { return a.compareTo(b); };
            bar = Collections.binarySearch(timePoints, timePoint, comparator);
            if (bar < 0) {
                bar = -(bar + 1);
            }
        }  
        return bar;
    }

    public Stream<TimePoint> getTimePoints(final TimePoint startTime, final TimePoint endTime) {
        return timePoints.stream().filter(time -> time.isGreaterOrEqual(startTime) && time.isLessOrEqual(endTime));
    }

    public Collection<DataSet> getDataSets() {
        return sets.values();
    }

    public boolean containsDataSet(DataSetName name) {
        return sets.containsKey(name);
    }

    private boolean isValidBar(int bar) {
        return (bar >= 0) && (bar < timePoints.size());
    }

    boolean isEmpty() {
        return sets.isEmpty();
    }

}
