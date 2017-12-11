package com.alphatica.genotick.data;

import com.alphatica.genotick.reversal.Reversal;
import com.alphatica.genotick.timepoint.TimePoint;
import com.alphatica.genotick.timepoint.TimePoints;
import com.alphatica.genotick.utility.JniExport;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO change nulls to optional
public class MainAppData {
    
    private final Map<DataSetName, DataSet> dataSetMap;
    private final Map<DataSetName, DataSetName> reversedNameMap;
    private final TimePoints timePoints;

    public MainAppData() {
        dataSetMap = new HashMap<>();
        reversedNameMap = new HashMap<>();
        timePoints = new TimePoints(false);
    }

    public void put(DataSet set) {
        DataSetName dataSetName = set.getName();
        dataSetMap.put(dataSetName, set);
        set.fetchMergedTimePoints(timePoints);
        if (!dataSetName.isReversed()) {
            Reversal reversal = new Reversal(set);
            DataSetName reversedName = reversal.getReversedName();
            reversedNameMap.put(dataSetName, reversedName);
        }
    }

    @JniExport
    void put(String name, DataLines tohlcLines) {
        DataSet set = new DataSet(name, tohlcLines);
        put(set);
    }

    public TimePoint getFirstTimePoint() {
        return timePoints.getOldest();
    }

    public TimePoint getLastTimePoint() {
        return timePoints.getNewest();
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

    public int getTimePointCount() {
        return timePoints.size();
    }
    
    public TimePoints createTimePointsCopy(TimePoint startTime, TimePoint endTime) {
        return timePoints.createSelectionCopy(startTime, endTime);
    }
    
    public Stream<TimePoint> getTimePoints(TimePoint startTime, TimePoint endTime) {
        return timePoints.getSelection(startTime, endTime);
    }
    
    public Set<DataSetName> getDataSetNames() {
        return dataSetMap.keySet();
    }

    public Collection<DataSet> getDataSets() {
        return dataSetMap.values();
    }
    
    public Collection<DataSet> getOriginalDataSets() {
        return dataSetMap.values().stream().filter(dataSet -> { return !dataSet.getName().isReversed(); }).collect(Collectors.toList());
    }
    
    public DataSetName getReversedName(DataSetName name) {
        return reversedNameMap.get(name);
    }

    public DataSet getDataSet(DataSetName name) {
        return dataSetMap.get(name);
    }
    
    public DataSet getReversedDataSet(DataSetName name) {
        DataSetName reversedName = getReversedName(name);
        if (reversedName != null) {
            return dataSetMap.get(reversedName);
        }
        return null;
    }

    public boolean containsDataSet(DataSetName name) {
        return dataSetMap.containsKey(name);
    }

    public boolean isEmpty() {
        return dataSetMap.isEmpty();
    }
    
    public int getMaximumColumnCount() {
        return dataSetMap.values().stream().mapToInt(DataSet::getColumnCount).max().orElse(0);
    }
}
