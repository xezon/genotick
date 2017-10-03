package com.alphatica.genotick.data;

import com.alphatica.genotick.genotick.RobotDataManager;
import com.alphatica.genotick.timepoint.TimePoint;
import com.alphatica.genotick.timepoint.TimePoints;

import static com.alphatica.genotick.utility.Assert.gassert;

import java.util.ArrayList;
import java.util.List;

public class DataSet {
    private final DataSetName name;
    private final TimePoints timePoints;
    private final DataSeries ohlcData;

    public DataSet(List<Number[]> tohlcLines, String fileName) {
        this(tohlcLines, new DataSetName(fileName));
    }

    public DataSet(List<Number[]> tohlcLines, DataSetName name) {
        final int barCount = tohlcLines.size();
        gassert(barCount > 0);
        final int tohlcColumnCount = tohlcLines.get(0).length;
        gassert(tohlcColumnCount > 1);
        final int ohlcColumnCount = tohlcColumnCount - 1;
        this.name = name;
        this.timePoints = new TimePoints(barCount);
        this.ohlcData = new DataSeries(ohlcColumnCount, barCount, false);
        int lineNumber = 1;
        for (Number[] tohlcLine : tohlcLines) {
            checkNumberOfColumnsInLine(lineNumber, tohlcLine, tohlcColumnCount);
            fillTimePoints(lineNumber, tohlcLine);
            fillOhlcData(lineNumber, tohlcLine, ohlcColumnCount);
            lineNumber++;
        }
    }

    public DataSetName getName() {
        return name;
    }

    TimePoints getTimePoints(MainAppData.Friend friend) {
        return timePoints;
    }

    public DataSeries getOhlcData(RobotDataManager.Friend friend) {
        return ohlcData;
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

    private void fillOhlcData(int lineNumber, Number[] tohlcLine, int ohlcColumnCount) {
        final int bar = lineNumber - 1;
        for (int column = Column.OHLCV.OPEN; column < ohlcColumnCount; ++column) {
            ohlcData.set(column, bar, tohlcLine[column+1].doubleValue());
        }
    }

    private void fillTimePoints(int lineNumber, Number[] tohlcLine) {
        TimePoint timePoint = new TimePoint(tohlcLine[Column.TOHLCV.TIME].longValue());
        validateTimePoint(lineNumber, timePoint);
        timePoints.add(timePoint);
    }

    private void validateTimePoint(int lineNumber, TimePoint timePoint) {
        final int bar = lineNumber - 1;
        if (bar > 0) {
            final TimePoint previousTimePoint = timePoints.get(bar - 1);
            if (timePoint.compareTo(previousTimePoint) <= 0) {
                throw new DataException("Time (first number) is equal or less than previous. Line: " + lineNumber);
            }
        }
    }

    private void checkNumberOfColumnsInLine(int lineNumber, Number[] tohlcLine, int tohlcColumnCount) {
        if (tohlcLine.length != tohlcColumnCount)
            throw new DataException("Invalid amount of columns in line: " + lineNumber);
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
}
