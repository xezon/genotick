package com.alphatica.genotick.data;

import java.util.Optional;

import com.alphatica.genotick.timepoint.TimePoint;
import com.alphatica.genotick.timepoint.TimePoints;

public class DataSet {

    private final DataSetName name;
    private final DataLines tohlcLines;
    private final TimePoints timePoints;
    private final DataSeries ohlcData;
    private Optional<DataSeries> filteredOhlcData;
    private Optional<FilterSettings> filter;
    
    public DataSet(String name, DataLines tohlcLines) {
        this(new DataSetName(name), tohlcLines);
    }
    
    public DataSet(DataSetName name, DataLines tohlcLines) {
        this.name = name;
        this.tohlcLines = new DataLines(tohlcLines, false);
        this.timePoints = this.tohlcLines.createTimePoints();
        this.ohlcData = this.tohlcLines.createDataSeries();
        this.filteredOhlcData = Optional.empty();
        this.filter = Optional.empty();
    }

    public DataSetName getName() {
        return name;
    }

    public void fetchMergedTimePoints(TimePoints timePoints) {
        timePoints.merge(this.timePoints);
    }

    public DataSeries createOhlcDataSection(int barBegin, int maxBars, boolean firstBarIsNewest, boolean useFiltered) {
        DataSeries data = getOhlcData(useFiltered);
        return new DataSeries(data, barBegin, maxBars, firstBarIsNewest);
    }
    
    public DataSeries createOhlcDataSection(TimePoint timeBegin, TimePoint timeEnd, boolean firstBarIsNewest, boolean useFiltered) {
        int barBegin = timePoints.getNearestIndex(timeBegin);
        int barEnd = timePoints.getNearestIndex(timeEnd);
        return createOhlcDataSection(barBegin, barEnd - barBegin, firstBarIsNewest, useFiltered);
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

    public boolean isValidBar(int bar) {
        return timePoints.isValidIndex(bar);
    }

    public DataLines getDataLinesCopy() {
        return tohlcLines.createCopy();
    }
    
    public void setFilterSettings(FilterSettings filter) {
        boolean useFilter = filter.filterOption != FilterOption.NONE;
        this.filteredOhlcData = Optional.ofNullable(useFilter ? ohlcData.createCopy() : null);
        this.filter = Optional.of(filter);
    }
    
    public void updateFilteredOhlcData(int barBegin, int barEnd) {
        switch (filter.get().filterOption) {
            case NONE: break;
            case EMA: Filters.applyEMA(filteredOhlcData.get(), barBegin, barEnd, 20); break;
            case EMA_ZEROLAG: Filters.applyEMAZeroLag(filteredOhlcData.get(), barBegin, barEnd, 20, 50); break;
        }
    }
    
    public int getColumnCount() {
    	return ohlcData.columnCount();
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
    
    private DataSeries getOhlcData(boolean useFiltered) {
        return (filteredOhlcData.isPresent() && useFiltered) ? filteredOhlcData.get() : ohlcData;
    }
}
