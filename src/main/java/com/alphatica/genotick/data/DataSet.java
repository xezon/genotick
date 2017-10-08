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

    public DataSet(DataSetName name, List<Number[]> tohlcLines) {
        verifyData(tohlcLines);
        final int tohlcBarCount = tohlcLines.size();
        final int tohlcColumnCount = tohlcLines.get(0).length;
        final int ohlcColumnCount = tohlcColumnCount - 1;
        this.name = name;
        this.timePoints = new TimePoints(tohlcBarCount, false);
        this.ohlcData = new DataSeries(ohlcColumnCount, tohlcBarCount, false);
        copy(tohlcLines);
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
    
    private static void verifyData(List<Number[]> tohlcLines) {
        final int lineCount = tohlcLines.size();
        gassert(lineCount > 0, "The given asset data is empty");
        final int firstColumnCount = tohlcLines.get(0).length;
        gassert(firstColumnCount > 1, "The given asset data does not have enough columns to fill time points and data series");
        for (int lineNumber = 0; lineNumber < lineCount; ++lineNumber) {
            final Number[] tohlcLine = tohlcLines.get(lineNumber);
            gassert(tohlcLine.length == firstColumnCount, String.format("Column count '%d' in line '%d' does not match the expected column count '%d'",
                    tohlcLine.length, lineNumber + 1, firstColumnCount));
            if (lineNumber > 0) {
                final long currentTimeValue = tohlcLine[Column.TOHLCV.TIME].longValue();
                final long previousTimeValue = tohlcLines.get(lineNumber-1)[Column.TOHLCV.TIME].longValue();
                gassert(currentTimeValue > previousTimeValue, String.format("Time value '%d' in line '%d' is not greater than previous time value '%d'",
                        currentTimeValue, lineNumber + 1, previousTimeValue));
            }
        }
    }
}
