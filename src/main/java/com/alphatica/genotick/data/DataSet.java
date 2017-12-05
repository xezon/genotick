package com.alphatica.genotick.data;

import com.alphatica.genotick.timepoint.TimePoint;
import com.alphatica.genotick.timepoint.TimePoints;

public class DataSet {

    private final DataSetName name;
    private final DataLines tohlcLines;
    private final TimePoints timePoints;
    private final DataSeries ohlcData;

    public DataSet(String name, DataLines tohlcLines) {
        this(new DataSetName(name), tohlcLines);
    }

    public DataSet(DataSetName name, DataLines tohlcLines) {
        this.name = name;
        this.tohlcLines = tohlcLines;
        this.timePoints = tohlcLines.createTimePoints();
        this.ohlcData = tohlcLines.createDataSeries();
    }

    public DataSetName getName() {
        return name;
    }

    public void fetchMergedTimePoints(TimePoints timePoints) {
        timePoints.merge(this.timePoints);
    }

    public DataSeries createOhlcDataSection(int firstBar, int maxBars, boolean firstBarIsNewest) {
        return new DataSeries(ohlcData, firstBar, maxBars, firstBarIsNewest);
    }

    public int getBar(TimePoint timePoint) {
        return timePoints.getIndex(timePoint);
    }

    public int getNearestBar(TimePoint timePoint) {
        return timePoints.getNearestIndex(timePoint);
    }

    public boolean isValidBar(int bar) {
        return timePoints.isValidIndex(bar);
    }

    public DataLines getDataLinesCopy() {
        return tohlcLines.createCopy();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        DataSet dataSet = (DataSet)other;
        return name.equals(dataSet.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
