package com.alphatica.genotick.data;

import com.alphatica.genotick.timepoint.TimePoint;
import com.alphatica.genotick.utility.Friendship;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO change nulls to optional
public class MainAppData extends Friendship {
    
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
        updateTimePoints(set.getTimePoints(befriend));
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

    public TimePoint getFirstTimePoint() {
        return !timePoints.isEmpty() ? timePoints.get(0) : null;
    }

    public TimePoint getLastTimePoint() {
        return !timePoints.isEmpty() ? timePoints.get(timePoints.size() - 1) : null;
    }

    public TimePoint getTimePoint(int bar) {
        return isValidBar(bar) ? timePoints.get(bar) : null;
    }

    public int getBar(TimePoint timePoint) {
        Integer bar = bars.get(timePoint);
        if (bar == null) {
            Comparator<TimePoint> comparator = (TimePoint a, TimePoint b) -> { return a.compareTo(b); };
            bar = Collections.binarySearch(timePoints, timePoint, comparator);
        }  
        return bar;
    }

    public int getNearestBar(TimePoint timePoint) {
        int bar = getBar(timePoint);
        return (bar >= 0) ? bar : -(bar + 1);
    }

    private boolean isValidBar(int bar) {
        return (bar >= 0) && (bar < timePoints.size());
    }

    public Stream<TimePoint> getTimePoints(final TimePoint startTime, final TimePoint endTime) {
        return timePoints.stream().filter(time -> time.isGreaterOrEqual(startTime) && time.isLessOrEqual(endTime));
    }

    public Collection<DataSet> getDataSets() {
        return sets.values();
    }

    public DataSet getDataSet(DataSetName name) {
        return sets.get(name);
    }

    public boolean containsDataSet(DataSetName name) {
        return sets.containsKey(name);
    }

    boolean isEmpty() {
        return sets.isEmpty();
    }
}
