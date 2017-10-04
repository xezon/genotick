package com.alphatica.genotick.data;

import com.alphatica.genotick.timepoint.TimePoint;
import com.alphatica.genotick.timepoint.TimePoints;

import static com.alphatica.genotick.utility.Assert.gassert;

import java.util.ArrayList;
import java.util.List;

public class DataSet {
    private final DataSetName name;
    private final TimePoints timePoints;
    private final DataSeries ohlcData;

    public DataSet(String name, List<Number[]> tohlcLines) {
        this(new DataSetName(name), tohlcLines);
    }

    public DataSet(String name, TimePoints timePoints, DataSeries ohlcData) {
        this(new DataSetName(name), timePoints, ohlcData);
    }

    public DataSet(DataSetName name, List<Number[]> tohlcLines) {
        final int tohlcBarCount = tohlcLines.size();
        gassert(tohlcBarCount > 0);
        final int tohlcColumnCount = tohlcLines.get(0).length;
        gassert(tohlcColumnCount > 1);
        final int ohlcColumnCount = tohlcColumnCount - 1;
        this.name = name;
        this.timePoints = new TimePoints(tohlcBarCount, false);
        this.ohlcData = new DataSeries(ohlcColumnCount, tohlcBarCount, false);
        copy(tohlcLines);
        this.timePoints.verifyOrder();
    }

    public DataSet(DataSetName name, TimePoints timePoints, DataSeries ohlcData) {
        verifyData(timePoints, ohlcData);
        this.name = name;
        this.timePoints = new TimePoints(timePoints, false);
        this.ohlcData = new DataSeries(ohlcData, false);
    }

    public void add(TimePoints timePoints, DataSeries ohlcData) {
        verifyData(timePoints, ohlcData);
        this.timePoints.add(timePoints);
        this.ohlcData.add(ohlcData);
    }
    
    public void add(DataSet dataSet) {
        add(dataSet.timePoints, dataSet.ohlcData);
    }

    public DataSetName getName() {
        return name;
    }

    public void fetchMergedTimePoints(TimePoints timePoints) {
        timePoints.merge(this.timePoints);
    }

    public void fetchOhlcDataSection(DataSeries ohlcData, int bar, int maxBars) {
        ohlcData.copySection(this.ohlcData, bar, maxBars);
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
    
    private void copy(List<Number[]> tohlcLines) {
        final int tohlcColumnCount = tohlcLines.get(0).length;
        final int tohlcBarCount = tohlcLines.size();
        final int ohlcColumnCount = ohlcData.columnCount();
        final int ohlcBarCount = ohlcData.barCount();
        final int timePointSize = timePoints.size();
        gassert(ohlcColumnCount + 1 == tohlcColumnCount);
        gassert(ohlcBarCount == tohlcBarCount);
        gassert(ohlcBarCount == timePointSize);
        int bar = 0;
        for (Number[] tohlcLine : tohlcLines) {
            verifyNumberOfColumns(bar, tohlcLine, tohlcColumnCount);
            fillTimePoints(bar, tohlcLine);
            fillOhlcData(bar, tohlcLine, ohlcColumnCount);
            bar++;
        }
    }

    private void fillOhlcData(int bar, Number[] tohlcLine, int ohlcColumnCount) {
        for (int column = Column.OHLCV.OPEN; column < ohlcColumnCount; ++column) {
            ohlcData.set(column, bar, tohlcLine[column+1].doubleValue());
        }
    }

    private void fillTimePoints(int bar, Number[] tohlcLine) {
        final long timeValue = tohlcLine[Column.TOHLCV.TIME].longValue();
        timePoints.set(bar, new TimePoint(timeValue));
    }

    private void verifyNumberOfColumns(int bar, Number[] tohlcLine, int tohlcColumnCount) {
        if (tohlcLine.length != tohlcColumnCount) {
            throw new DataException("Invalid amount of columns in line: " + (bar+1));
        }
    }

    public int getLinesCount() {
        return timePoints.size();
    }

    public Number[] getLine(int lineNumber) {
        Number[] tohlcLine = new Number[1 + ohlcData.columnCount()];
        tohlcLine[Column.TOHLCV.TIME] = timePoints.get(lineNumber).getValue();
        for (int column = Column.TOHLCV.OPEN; column < tohlcLine.length; ++column) {
            tohlcLine[column] = ohlcData.get(column, lineNumber);
        }
        return tohlcLine;
    }

    public List<Number[]> getAllLines() {
        List<Number[]> tohlcLines = new ArrayList<>();
        for (int i = Column.OHLCV.OPEN, count = getLinesCount(); i < count; ++i) {
            tohlcLines.add(getLine(i));
        }
        return tohlcLines;
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
    
    private void verifyData(TimePoints timePoints, DataSeries ohlcData) {
        gassert(timePoints.firstTimeIsNewest() == ohlcData.firstBarIsNewest());
        gassert(timePoints.size() == ohlcData.barCount());
        timePoints.verifyOrder();
    }
}
