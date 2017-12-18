package com.alphatica.genotick.data;

import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.concurrent.atomic.AtomicBoolean;

import com.alphatica.genotick.data.Column.TOHLC;
import com.alphatica.genotick.timepoint.TimePoint;
import com.alphatica.genotick.timepoint.TimePoints;
import com.alphatica.genotick.utility.JniExport;

public class DataLines {
    
    private static final int MIN_LINE_COUNT = 1;
    @JniExport
    private static final int MIN_COLUMN_COUNT = TOHLC.CLOSE + 1;
    private final Number[][] data;
    private final boolean firstLineIsNewest;
    
    DataLines(File file, boolean firstLineIsNewest) {
        final ArrayList<Number[]> dataFromFile = parseData(file, firstLineIsNewest);
        final int lineCount = dataFromFile.size();
        final int columnCount = (lineCount > 0) ? dataFromFile.get(0).length : 0;
        verifyLineAndColumnCount(lineCount, columnCount);
        this.data = new Number[lineCount][columnCount];
        this.firstLineIsNewest = firstLineIsNewest;
        for (int line = 0; line < lineCount; ++line) {
            for (int column = 0; column < columnCount; ++column) {
                this.data[line][column] = dataFromFile.get(line)[column];
            }
        }
    }
    
    @JniExport
    DataLines(int lineCount, int columnCount, boolean firstLineIsNewest) {
        verifyLineAndColumnCount(lineCount, columnCount);
        this.data = new Number[lineCount][columnCount];
        this.firstLineIsNewest = firstLineIsNewest;
    }
    
    DataLines(DataLines other) {
        this(other, other.firstLineIsNewest);
    }
    
    DataLines(DataLines other, boolean firstLineIsNewest) {
        final int lineCount = other.lineCount();
        final int columnCount = other.tohlcColumnCount();
        this.data = new Number[lineCount][columnCount];
        this.firstLineIsNewest = firstLineIsNewest;
        copy(other);
    }
    
    DataLines createCopy() {
        return new DataLines(this);
    }
    
    DataLines createReversedCopy() {
        return new DataLines(this, !firstLineIsNewest);
    }
    
    private void copy(DataLines other) {
        if (firstLineIsNewest == other.firstLineIsNewest) {
            copyStraight(other);
        }
        else {
            copyReversed(other);
        }
    }
    
    private void copyStraight(DataLines other) {
        final int lineCount = other.lineCount();
        final int columnCount = other.tohlcColumnCount();
        for (int line = 0; line < lineCount; ++line) {
            for (int column = 0; column < columnCount; ++column) {
                this.data[line][column] = other.data[line][column];
            }
        }
    }
    
    private void copyReversed(DataLines other) {
        final int lineCount = other.lineCount();
        final int columnCount = other.tohlcColumnCount();
        for (int line = 0; line < lineCount; ++line) {
            for (int column = 0; column < columnCount; ++column) {
                this.data[line][column] = other.data[lineCount-line-1][column];
            }
        }
    }
    
    Number[] getColumnsCopy(int line) {
        final int columnCount = tohlcColumnCount();
        Number[] columnsCopy = new Number[columnCount];
        System.arraycopy(data[line], 0, columnsCopy, 0, columnCount);
        return columnsCopy;
    }
    
    void setColumns(int line, Number[] columns) {
        final int columnCount = tohlcColumnCount();
        if (columns.length != columnCount) {
            throw new DataException(format("Given column count '%d' for line '%d' does not match the expected column count '%d'.", columns.length, line, columnCount));
        }
        System.arraycopy(columns, 0, data[line], 0, columnCount);
    }
    
    public double[] getOhlcValuesCopy(int line) {
        double[] ohlcCopy = new double[Column.Array.OHLC.length];
        for (int column : Column.Array.OHLC) {
            ohlcCopy[column] = getOhlcValue(line, column);
        }
        return ohlcCopy;
    }
    
    public void setOhlcValues(int line, double[] ohlcValues) {
        if (ohlcValues.length != Column.Array.OHLC.length) {
            throw new DataException(format("Given column count '%d' for line '%d' does not match the expected column count '%d'.", ohlcValues.length, line, 4));
        }
        for (int column : Column.Array.OHLC) {
            setOhlcValue(line, column, ohlcValues[column]);
        }
    }
    
    private long getTime(int line) {
        return data[line][Column.TOHLC.TIME].longValue();
    }
    
    public double getOhlcValue(int line, int ohlcColumn) {
        return data[line][ohlcColumn + 1].doubleValue();
    }
    
    public void setOhlcValue(int line, int ohlcColumn, double value) {
        data[line][ohlcColumn + 1] = value;
    }
    
    @JniExport
    void setTime(int line, long value) {
        data[line][Column.TOHLC.TIME] = value;
    }
    
    @JniExport
    void setOpen(int line, double value) {
        data[line][Column.TOHLC.OPEN] = value;
    }
    
    @JniExport
    void setHigh(int line, double value) {
        data[line][Column.TOHLC.HIGH] = value;
    }
    
    @JniExport
    void setLow(int line, double value) {
        data[line][Column.TOHLC.LOW] = value;
    }
    
    @JniExport
    void setClose(int line, double value) {
        data[line][Column.TOHLC.CLOSE] = value;
    }
    
    @JniExport
    void setOther(int line, int otherColumn, double value) {
        data[line][otherColumn + Column.TOHLC.OTHER] = value;
    }
    
    public int lineCount() {
        return data.length;
    }
    
    public int tohlcColumnCount() {
        return data[0].length;
    }
    
    public int ohlcColumnCount() {
        return data[0].length - 1;
    }
    
    public boolean firstLineIsNewest() {
        return firstLineIsNewest;
    }
    
    TimePoints createTimePoints() {
        final int lineCount = lineCount();
        final TimePoints timePoints = new TimePoints(lineCount, firstLineIsNewest);
        for (int line = 0; line < lineCount; ++line) {
            timePoints.set(line, new TimePoint(getTime(line)));
        }
        return timePoints;
    }
    
    DataSeries createDataSeries() {
        final int lineCount = lineCount();
        final int columnCount = ohlcColumnCount();
        final DataSeries dataSeries = new DataSeries(columnCount, lineCount, firstLineIsNewest);
        for (int line = 0; line < lineCount; ++line) {
            for (int column = 0; column < columnCount; ++column) {
                dataSeries.set(column, line, getOhlcValue(line, column));
            }
        }
        return dataSeries;
    }
    
    DataSeries createDataSeries(int... ohlcColumns) {
        final int availableColumnCount = ohlcColumnCount();
        for (int column : ohlcColumns) {
            if (column < 0 || column >= availableColumnCount) {
                throw new DataException("Column " + column + " does not exist");
            }
        }
        final int lineCount = lineCount();
        final int columnCount = ohlcColumns.length;
        final DataSeries dataSeries = new DataSeries(columnCount, lineCount, firstLineIsNewest);
        for (int line = 0; line < lineCount; ++line) {
            for (int column = 0; column < columnCount; ++column) {
                dataSeries.set(column, line, getOhlcValue(line, ohlcColumns[column]));
            }
        }
        return dataSeries;
    }
    
    private static class DataLineParseResult
    {
        Number[] previousColumns = null;
        Number[] columns = null;
        int number = 0;
        int expectedColumnCount = MIN_COLUMN_COUNT;
    }
    
    private static void parseDataLines(File file, Predicate<DataLineParseResult> predicate) {
        DataLineParseResult line = new DataLineParseResult();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String rawLine;
            while ((rawLine = reader.readLine()) != null) {
                line.number++;
                line.previousColumns = line.columns;
                if (parseLine(line, rawLine)) {
                    if (!predicate.test(line)) {
                        return;
                    }
                }
            }
        }
        catch (IOException | NumberFormatException ex) {
            throw new DataException(format("Unable to process line '%d'", line.number), ex);
        }
    }
    
    private static boolean parseLine(DataLineParseResult line, String rawLine) {
        if (line.columns == null) {
            try {
                line.columns = processLine(rawLine);
                line.expectedColumnCount = line.columns.length;
            }
            catch (NumberFormatException ex) {
                // might be the CSV header
                return false;
            }
        }
        else {
            line.columns = processLine(rawLine);
        }
        return true;
    }
    
    static boolean isFirstLineNewestTimePoint(File file) {
        final AtomicBoolean firstLineIsNewest = new AtomicBoolean();
        parseDataLines(file, line -> {
            if (line.previousColumns != null) {
                final long currentTimeValue = line.columns[Column.TOHLC.TIME].longValue();
                final long previousTimeValue = line.previousColumns[Column.TOHLC.TIME].longValue();
                firstLineIsNewest.set(currentTimeValue < previousTimeValue);
                return false;
            }
            return true;
        });
        return firstLineIsNewest.get();
    }
    
    private static ArrayList<Number[]> parseData(File file, boolean firstLineIsNewest) {
        final ArrayList<Number[]> dataLines = new ArrayList<>();
        parseDataLines(file, line -> {
            verifyColumnCount(line);
            verifyTimePointOrder(line, firstLineIsNewest);
            dataLines.add(line.columns);
            return true;
        });
        return dataLines;
    }
    
    private static void verifyColumnCount(DataLineParseResult line) {
        if (line.columns.length != line.expectedColumnCount) {
            throw new DataException(format("Column count '%d' in line '%d' does not match the expected column count '%d'.",
                    line.columns.length, line.number, line.expectedColumnCount));
        }
    }
    
    private static void verifyTimePointOrder(DataLineParseResult line, boolean firstLineIsNewest) {
        if (line.previousColumns != null) {
            final long currentTimeValue = line.columns[Column.TOHLC.TIME].longValue();
            final long previousTimeValue = line.previousColumns[Column.TOHLC.TIME].longValue();
            final boolean isCorrectOrder = firstLineIsNewest ? (currentTimeValue < previousTimeValue) : (currentTimeValue > previousTimeValue);
            if (!isCorrectOrder) {
                throw new DataException(format("Time value '%d' in line '%d' is not %s than previous time value '%d'",
                        currentTimeValue, line.number, firstLineIsNewest ? "smaller" : "greater", previousTimeValue));
            }
        }
    }
    
    private static Number[] processLine(String line) {
        String[] fields = getLineFields(line);
        Number[] columns = new Number[fields.length];
        String timePointString = getTimePointString(fields[0]);
        columns[0] = Long.valueOf(timePointString);
        for(int i = 1; i < fields.length; i++) {
            columns[i] = Double.valueOf(fields[i]);
        }
        return columns;
    }
    
    private static String[] getLineFields(String line) {
        return line.split(",");
    }
    
    private static String getTimePointString(String field) {
        return field.replaceAll("[-.]", "");
    }
    
    private static void verifyLineAndColumnCount(int lineCount, int columnCount) {
        if (lineCount < MIN_LINE_COUNT || columnCount < MIN_COLUMN_COUNT) {
            throw new DataException(format("Line count %d and column count %d are insufficient", lineCount, columnCount));
        }
    }
}
