package com.alphatica.genotick.reversal;

import com.alphatica.genotick.data.Column;
import com.alphatica.genotick.data.DataLines;
import com.alphatica.genotick.data.DataSet;
import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.data.MainAppData;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Reversal {
    private static final String REVERSE_DATA_IDENTIFIER = "reverse_";
    private final DataSet originalSet;
    private DataSet reversedSet;
    private final DataSetName reversedName;
    private final boolean isReversed;

    public Reversal(DataSet dataSet) {
        final DataSetName name = dataSet.getName();
        this.originalSet = dataSet;
        this.reversedSet = null;
        this.reversedName = new DataSetName(getReversedDataPath(name.getPath()));
        this.isReversed = isReversedDataName(name.getName());
    }

    public DataSetName getReversedName() {
        return reversedName;
    }

    public boolean isReversed() {
        return isReversed;
    }

    public boolean addReversedDataSetTo(MainAppData data) {
        if (!isReversed) {
            if (!data.containsDataSet(reversedName)) {
                data.put(getReversedDataSet());
                return true;
            }
        }
        return false;
    }

    public DataSet getReversedDataSet() {
        if (null == reversedSet) {
            final DataLines dataLines = originalSet.getDataLinesCopy();
            reverseDataLines(dataLines);
            reversedSet = new DataSet(reversedName, dataLines);
        }
        return reversedSet;
    }

    private static boolean isReversedDataName(String name) {
        return name.startsWith(REVERSE_DATA_IDENTIFIER);
    }

    private static String getReversedDataPath(String path) {
        Path originalPath = Paths.get(path);
        Path fileNamePath = originalPath.getFileName();
        Path directoryPath = originalPath.getParent();
        String directoryString = (directoryPath != null) ? directoryPath.toString() : "";
        String newName = REVERSE_DATA_IDENTIFIER + fileNamePath.toString();
        Path reversedPath = Paths.get(directoryString, newName);
        return reversedPath.toString();
    }

    private static void reverseDataLines(DataLines dataLines) {
        final int lineCount = dataLines.lineCount();
        Number[] lastOriginal = null;
        Number[] lastReversed = null;
        for (int line = 0; line < lineCount; ++line) {
            Number[] currentOriginal = dataLines.getColumnsCopy(line);
            Number[] currentReversed = createReversedColumns(currentOriginal, lastOriginal, lastReversed);
            dataLines.setColumns(line, currentReversed);
            lastOriginal = currentOriginal;
            lastReversed = currentReversed;
        }
    }

    /*
     * This method is for reversing traditional open-high-low-close stock market data.
     * What happens with numbers (by column):
     * 0 - TimePoint: Doesn't change.
     * 1 - Open: It goes opposite direction to original, by the same percent.
     * 2 and 3 - High and Low: First of all they swapped. This is because data should be a mirror reflection of
     * original, so high becomes low and low becomes high. Change is calculated comparing to open column
     * (column 1). So it doesn't matter what High was in previous TimePoint, it matters how much higher it was comparing
     * to the open on the same line. When High becomes low - it goes down by same percent as original high was higher
     * than open.
     * 4 - Close. Goes opposite to original close by the same percent.
     * 5 and more - Volume, open interest or whatever. These don't change.
     */

    private static Number[] createReversedColumns(Number[] table, Number[] lastOriginal, Number[] lastReversed) {
        Number[] reversed = new Number[table.length];
        // Column 0 is unchanged
        reversed[Column.TOHLCV.TIME] = table[Column.TOHLCV.TIME];
        // Column 1. Rewrite if first line
        if(lastOriginal == null) {
            reversed[Column.TOHLCV.OPEN] = table[Column.TOHLCV.OPEN];
        } else {
            // Change by % if not first line
            reversed[Column.TOHLCV.OPEN] = getReverseValue(table[Column.TOHLCV.OPEN], lastOriginal[Column.TOHLCV.OPEN], lastReversed[Column.TOHLCV.OPEN]);
        }
        // Check if 4 columns here, because we need time, open, high, low to do swapping later.
        if(table.length < Column.TOHLCV.CLOSE)
            return reversed;
        // Column 2. Change by % comparing to open
        // Write into 3 - we swap 2 & 3
        reversed[Column.TOHLCV.LOW] = getReverseValue(table[Column.TOHLCV.HIGH], table[Column.TOHLCV.OPEN], reversed[Column.TOHLCV.OPEN]);
        // Column 3. Change by % comparing to open
        // Write into 2 - we swap 2 & 3
        reversed[Column.TOHLCV.HIGH] = getReverseValue(table[Column.TOHLCV.LOW], table[Column.TOHLCV.OPEN], reversed[Column.TOHLCV.OPEN]);
        if(table.length == Column.TOHLCV.CLOSE)
            return reversed;
        // Column 4. Change by % comparing to open.
        reversed[Column.TOHLCV.CLOSE] = getReverseValue(table[Column.TOHLCV.CLOSE], table[Column.TOHLCV.OPEN], reversed[Column.TOHLCV.OPEN]);
        // Rewrite rest
        System.arraycopy(table, Column.TOHLCV.VOLUME, reversed, Column.TOHLCV.VOLUME, table.length - Column.TOHLCV.VOLUME);
        return reversed;
    }

    private static Number getReverseValue(Number from, Number to, Number compare) {
        double diff = Math.abs((from.doubleValue() / to.doubleValue()) -2);
        return diff * compare.doubleValue();
    }
}
