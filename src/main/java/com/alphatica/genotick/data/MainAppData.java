package com.alphatica.genotick.data;

import com.alphatica.genotick.genotick.RobotData;
import com.alphatica.genotick.timepoint.TimePoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO change nulls to optional
public class MainAppData {

    public static final int INVALID_BAR = -1;
    
    private final Map<DataSetName, DataSet> sets;
    private List<TimePoint> timePoints;
    private final Map<TimePoint, Integer> bars;

    public MainAppData() {
        sets = new HashMap<>();
        timePoints = new ArrayList<>();
        bars = new HashMap<TimePoint, Integer>();
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
    
    public boolean isValidBar(int bar) {
        return (bar >= 0) && (bar < timePoints.size());
    }
    
    public TimePoint getTimePoint(int bar) {
        return isValidBar(bar) ? timePoints.get(bar) : null;
    }
    
    public int getBar(TimePoint timePoint) {
        Integer bar = bars.get(timePoint);
        return (bar != null) ? bar.intValue() : INVALID_BAR;
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

    boolean isEmpty() {
        return sets.isEmpty();
    }

}
