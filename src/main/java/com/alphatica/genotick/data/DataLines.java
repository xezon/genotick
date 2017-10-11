package com.alphatica.genotick.data;

import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.alphatica.genotick.timepoint.TimePoint;
import com.alphatica.genotick.timepoint.TimePoints;
import com.alphatica.genotick.ui.UserInputOutputFactory;
import com.alphatica.genotick.utility.JniExport;

public class DataLines {
    
    private final static int MIN_LINE_COUNT = 1;
    private final static int MIN_COLUMN_COUNT = 2;
    private final Number[][] data;
    private final boolean firstLineIsNewest;
    
    DataLines(File file, boolean firstLineIsNewest) throws DataException {
        final ArrayList<Number[]> dataFromFile = readData(file);
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
    public DataLines(int lineCount, int columnCount, boolean firstLineIsNewest) throws DataException {
        verifyLineAndColumnCount(lineCount, columnCount);
        this.data = new Number[lineCount][columnCount];
        this.firstLineIsNewest = firstLineIsNewest;
    }
    
    DataLines(DataLines other) {
        final int lineCount = other.lineCount();
        final int columnCount = other.tohlcColumnCount();
        this.data = new Number[lineCount][columnCount];
        this.firstLineIsNewest = other.firstLineIsNewest;
        for (int line = 0; line < lineCount; ++line) {
            for (int column = 0; column < columnCount; ++column) {
                this.data[line][column] = other.data[line][column];
            }
        }
    }
    
    DataLines createCopy() {
        return new DataLines(this);
    }
    
    public Number[] getColumnsCopy(int line) {
        final int columnCount = tohlcColumnCount();
        Number[] columnsCopy = new Number[columnCount];
        System.arraycopy(data[line], 0, columnsCopy, 0, columnCount);
        return columnsCopy;
    }
    
    public void setColumns(int line, Number[] columns) throws DataException {
        final int columnCount = tohlcColumnCount();
        if (columns.length != columnCount) {
            logAndThrow(format("Given column count '%d' for line '%d' does not match the expected column count '%d'.",
                    columns.length, line, columnCount));
        }
        System.arraycopy(columns, 0, data[line], 0, columnCount);
    }
    
    private long getTime(int line) {
        return data[line][Column.TOHLCV.TIME].longValue();
    }
    
    private double getOhlcValue(int line, int ohlcColumn) {
        return data[line][ohlcColumn + Column.TOHLCV.OPEN].doubleValue();
    }
    
    @JniExport
    void setTime(int line, long value) {
        data[line][Column.TOHLCV.TIME] = value;
    }
    
    @JniExport
    void setOpen(int line, double value) {
        data[line][Column.TOHLCV.OPEN] = value;
    }
    
    @JniExport
    void setHigh(int line, double value) {
        data[line][Column.TOHLCV.HIGH] = value;
    }
    
    @JniExport
    void setLow(int line, double value) {
        data[line][Column.TOHLCV.LOW] = value;
    }
    
    @JniExport
    void setClose(int line, double value) {
        data[line][Column.TOHLCV.CLOSE] = value;
    }
    
    @JniExport
    void setVolume(int line, double value) {
        data[line][Column.TOHLCV.VOLUME] = value;
    }
    
    @JniExport
    void setOther(int line, int otherColumn, double value) {
        data[line][otherColumn + Column.TOHLCV.OTHER] = value;
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
    
    private static ArrayList<Number[]> readData(File file) throws DataException {
        ArrayList<Number[]> dataLines = new ArrayList<>();
        int linesRead = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int columnCount = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                linesRead++;
                String[] rawColumns = getLineFields(line);
                if (rawColumns.length >= MIN_COLUMN_COUNT) {
                    columnCount = rawColumns.length;
                    Number[] columns = processLine(line);
                    dataLines.add(columns);
                    break;
                }
            }
            while ((line = reader.readLine()) != null) {
                linesRead++;
                Number[] columns = processLine(line);
                if (columns.length != columnCount) {
                    logAndThrow(format("Column count '%d' in line '%d' does not match the expected column count '%d'.",
                            columns.length, linesRead, columnCount));
                }
                final long currentTimeValue = columns[Column.TOHLCV.TIME].longValue();
                final long previousTimeValue = dataLines.get(dataLines.size()-1)[Column.TOHLCV.TIME].longValue();
                if (currentTimeValue <= previousTimeValue) {
                    logAndThrow(format("Time value '%d' in line '%d' is not greater than previous time value '%d'",
                            currentTimeValue, linesRead, previousTimeValue));
                }
                dataLines.add(columns);
            }
        }
        catch (IOException | NumberFormatException ex) {
            logAndThrow(format("Unable to process line '%d'", linesRead), ex);
        }
        return dataLines;
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
        return field.replaceAll("-", "");
    }
    
    private static void verifyLineAndColumnCount(int lineCount, int columnCount) {
        if (lineCount < MIN_LINE_COUNT || columnCount < MIN_COLUMN_COUNT) {
            logAndThrow(format("Line count %d and column count %d are insufficient"));
        }
    }
    
    private static void logAndThrow(String message, Throwable ex) {
        UserInputOutputFactory.getUserOutput().errorMessage(message);
        throw new DataException(message, ex);
    }
    
    private static void logAndThrow(String message) {
        UserInputOutputFactory.getUserOutput().errorMessage(message);
        throw new DataException(message);
    }
}
