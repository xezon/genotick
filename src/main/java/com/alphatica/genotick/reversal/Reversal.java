package com.alphatica.genotick.reversal;

import com.alphatica.genotick.data.Column;
import com.alphatica.genotick.data.DataLines;
import com.alphatica.genotick.data.DataSet;
import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.data.MainAppData;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Reversal {

    private final DataSet originalSet;
    private DataSetName reversedName;
    private DataSet reversedSet;

    public Reversal(DataSet dataSet) {
        this.originalSet = dataSet;
        this.reversedName = null;
        this.reversedSet = null;
    }

    public boolean isReversed() {
        return originalSet.getName().isReversed();
    }
    
    public DataSetName getReversedName() {
        if (reversedName == null) {
            final DataSetName orginalName = originalSet.getName();
            reversedName = new DataSetName(getReversedDataPath(orginalName.getPath()));
        }
        return reversedName;
    }

    public DataSet getReversedDataSet() {
        if (reversedSet == null) {
            final DataLines dataLines = originalSet.getDataLinesCopy();
            reverseDataLines(dataLines);
            reversedSet = new DataSet(getReversedName(), dataLines);
        }
        return reversedSet;
    }

    public boolean addReversedDataSetTo(MainAppData data) {
        if (!isReversed()) {
            if (!data.containsDataSet(getReversedName())) {
                data.put(getReversedDataSet());
                return true;
            }
        }
        return false;
    }

    private static String getReversedDataPath(String path) {
        Path originalPath = Paths.get(path);
        Path fileNamePath = originalPath.getFileName();
        Path directoryPath = originalPath.getParent();
        String directoryString = (directoryPath != null) ? directoryPath.toString() : "";
        String newName = DataSetName.REVERSE_DATA_IDENTIFIER + fileNamePath.toString();
        Path reversedPath = Paths.get(directoryString, newName);
        return reversedPath.toString();
    }
    
    private static void reverseDataLines(DataLines dataLines) {
        final int lineCount = dataLines.lineCount();
        double[] lastOriginal = dataLines.getOhlcValuesCopy(0);
        double[] lastReversed = dataLines.getOhlcValuesCopy(0);
        for (int line = 0; line < lineCount; ++line) {
            double[] currentOriginal = dataLines.getOhlcValuesCopy(line);
            double[] currentReversed = createReversedOhlc(lastOriginal, currentOriginal, lastReversed);
            dataLines.setOhlcValues(line, currentReversed);
            lastOriginal = currentOriginal;
            lastReversed = currentReversed;
        }        
        normalize(dataLines);
    }
    
    private static double[] createReversedOhlc(double[] lastOriginal, double[] original, double[] lastReversed) {
        final double[] reversed = new double[original.length];
        final double openDiff = original[Column.OHLCV.OPEN] - lastOriginal[Column.OHLCV.CLOSE];
        final double highDiff = original[Column.OHLCV.HIGH] - original[Column.OHLCV.OPEN];
        final double lowDiff = original[Column.OHLCV.LOW] - original[Column.OHLCV.OPEN];
        final double closeDiff = original[Column.OHLCV.CLOSE] - original[Column.OHLCV.OPEN];
        reversed[Column.OHLCV.OPEN] = lastReversed[Column.OHLCV.CLOSE] - openDiff;
        reversed[Column.OHLCV.HIGH] = reversed[Column.OHLCV.OPEN] - lowDiff;
        reversed[Column.OHLCV.LOW] = reversed[Column.OHLCV.OPEN] - highDiff;
        reversed[Column.OHLCV.CLOSE] = reversed[Column.OHLCV.OPEN] - closeDiff;
        return reversed;
    }
    
    private static void normalize(DataLines dataLines) {
        final int lineCount = dataLines.lineCount();
        final double threshold = 0.01;
        double lowest = threshold;
        for (int line = 0; line < lineCount; ++line) {
            final double low = dataLines.getOhlcValue(line, Column.OHLCV.LOW);
            if (low < lowest)
                lowest = low;
        }
        if (lowest < threshold) {
            final double negOffset = lowest - threshold;
            for (int line = 0; line < lineCount; ++line) {
                for (int column : Column.Array.OHLC) {
                    final double value = dataLines.getOhlcValue(line, column);
                    dataLines.setOhlcValue(line, column, value - negOffset);
                }
            }
        }
    }
}
