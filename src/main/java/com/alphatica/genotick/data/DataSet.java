package com.alphatica.genotick.data;

import com.alphatica.genotick.genotick.RobotDataManager;
import com.alphatica.genotick.timepoint.TimePoint;
import com.alphatica.genotick.timepoint.TimePoints;

import java.util.ArrayList;
import java.util.List;

public class DataSet {
    private final DataSetName name;
    private final TimePoints timePoints;
    private final List<double[]> ohlcColumnsOfData;

    public DataSet(List<Number[]> tohlcLines, String fileName) {
        this(tohlcLines, new DataSetName(fileName));
    }

    public DataSet(List<Number[]> tohlcLines, DataSetName name) {
        this.name = name;
        this.timePoints = new TimePoints(tohlcLines.size());
        this.ohlcColumnsOfData = new ArrayList<>();
        final int tohlcColumnCount = tohlcLines.get(0).length;
        createColumnsOfData(tohlcLines.size(), tohlcColumnCount);
        int lineNumber = 0;
        for(Number[] tohlcLine: tohlcLines) {
            lineNumber++;
            checkNumberOfColumnsInLine(lineNumber, tohlcLine, tohlcColumnCount);
            fillTimePoints(lineNumber, tohlcLine);
            fillColumnsOfData(lineNumber, tohlcLine, tohlcColumnCount);
        }
    }

    public DataSetName getName() {
        return name;
    }

    TimePoints getTimePoints(MainAppData.Friend friend) {
        return timePoints;
    }

    public List<double[]> getOhlcColumnsOfData(RobotDataManager.Friend friend) {
        return ohlcColumnsOfData;
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

    private void fillColumnsOfData(int lineNumber, Number[] tohlcLine, int tohlcColumnCount) {
        for(int i = Column.TOHLCV.OPEN; i < tohlcColumnCount; i++)
            ohlcColumnsOfData.get(i-1)[lineNumber - 1] = tohlcLine[i].doubleValue();
    }

    private void fillTimePoints(int lineNumber, Number[] tohlcLine) {
        TimePoint timePoint = new TimePoint(tohlcLine[Column.TOHLCV.TIME].longValue());
        validateTimePoint(lineNumber, timePoint);
        timePoints.add(timePoint);
    }

    private void validateTimePoint(int lineNumber, TimePoint timePoint) {
        // Arrays start indexing from 0, but humans count text lines starting from 1.
        // New timePoint will be assigned to index = lineNumber -1, so
        // we have to check what happened two lines ago!
        if(lineNumber >= 2 &&  timePoint.compareTo(timePoints.get(lineNumber - 2)) <= 0)
            throw new DataException("Time (first number) is equal or less than previous. Line: " + lineNumber);
    }

    private void checkNumberOfColumnsInLine(int lineNumber, Number[] tohlcLine, int tohlcColumnCount) {
        if(tohlcLine.length != tohlcColumnCount)
            throw new DataException("Invalid amount of columns in line: " + lineNumber);
    }

    private void createColumnsOfData(int size, int tohlcColumnCount) {
        for(int i = Column.TOHLCV.OPEN; i < tohlcColumnCount; i++) {
            ohlcColumnsOfData.add(new double[size]);
        }
    }

    public int getLinesCount() {
        return timePoints.size();
    }

    public Number[] getLine(int lineNumber) {
        Number[] tohlcLine = new Number[1 + ohlcColumnsOfData.size()];
        tohlcLine[Column.TOHLCV.TIME] = timePoints.get(lineNumber).getValue();
        for(int i = Column.TOHLCV.OPEN; i < tohlcLine.length; i++) {
            tohlcLine[i] = ohlcColumnsOfData.get(i-1)[lineNumber];
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
