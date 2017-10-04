package com.alphatica.genotick.data;

import com.alphatica.genotick.timepoint.TimePoint;
import com.alphatica.genotick.timepoint.TimePoints;
import com.alphatica.genotick.utility.Friendship;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

// TODO change nulls to optional
public class MainAppData extends Friendship {
    
    private final Map<DataSetName, DataSet> sets;
    private final TimePoints timePoints;

    public MainAppData() {
        sets = new HashMap<>();
        timePoints = new TimePoints(false);
    }
    
    public void put(DataSet set) {
        this.sets.put(set.getName(), set);
        this.timePoints.merge(set.getTimePoints(befriend));
    }
    
    public void put(DataSetName name, TimePoints timePoints, DataSeries ohlcData) {
        this.sets.put(name, new DataSet(name, timePoints, ohlcData));
        this.timePoints.merge(timePoints);
    }

    public void add(DataSet set) {
        final DataSet existingSet = sets.get(set.getName());
        if (existingSet == null) {
            put(set);
        }
        else {
            existingSet.add(set);
            this.timePoints.merge(set.getTimePoints(befriend));
        }
    }
    
    public void add(DataSetName name, TimePoints timePoints, DataSeries ohlcData) {
        final DataSet existingSet = sets.get(name);
        if (existingSet == null) {
            put(name, timePoints, ohlcData);
        }
        else {
            existingSet.add(timePoints, ohlcData);
            this.timePoints.merge(timePoints);
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
        return timePoints.getIndex(timePoint);
    }

    public int getNearestBar(TimePoint timePoint) {
        return timePoints.getNearestIndex(timePoint);
    }

    private boolean isValidBar(int bar) {
        return timePoints.isValidIndex(bar);
    }

    public Stream<TimePoint> getTimePoints(final TimePoint startTime, final TimePoint endTime) {
        return timePoints.getSelection(startTime, endTime);
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
