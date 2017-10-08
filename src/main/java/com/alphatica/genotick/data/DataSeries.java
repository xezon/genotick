package com.alphatica.genotick.data;

import com.alphatica.genotick.processor.NotEnoughDataException;

public class DataSeries {
    private double[][] data;
    private final boolean firstBarIsNewest;
    
    public DataSeries(int columnCount, int barCount, boolean firstBarIsNewest) {
        this.data = new double[columnCount][barCount];
        this.firstBarIsNewest = firstBarIsNewest;
    }
    
    public DataSeries(DataSeries other, int firstBar, int maxBars, boolean firstBarIsNewest) {
        this.firstBarIsNewest = firstBarIsNewest;
        copySection(other, firstBar, maxBars);
    }
    
    public DataSeries(DataSeries other) {
        this.firstBarIsNewest = other.firstBarIsNewest;
        copy(other);
    }
    
    public DataSeries(DataSeries other, boolean firstBarIsNewest) {
        this.firstBarIsNewest = firstBarIsNewest;
        copy(other);
    }
    
    private void copy(DataSeries other) {
        if (firstBarIsNewest == other.firstBarIsNewest) {
            copyStraight(other);
        }
        else {
            copyReversed(other);
        }
    }
    
    private void copySection(DataSeries other, int firstBar, int maxBars) {
        if (firstBarIsNewest == other.firstBarIsNewest) {
            copyStraightSection(other, firstBar, maxBars);
        }
        else {
            copyReversedSection(other, firstBar, maxBars);
        }
    }
    
    public double get(int column, int bar) {
        if (bar < data[column].length) {
            return data[column][bar];
        }
        throw new NotEnoughDataException();
    }
    
    public void set(int column, int bar, double value) {
        data[column][bar] = value;
    }
    
    public int columnCount() {
        return data.length;
    }
    
    public int barCount() {
        return data.length > 0 ? data[0].length : 0;
    }
    
    public boolean firstBarIsNewest() {
        return firstBarIsNewest;
    }
    
    private void resize(int columnCount, int barCount) {
        data = new double[columnCount][barCount];
    }
    
    private void copyStraight(final int toBar, final DataSeries other) {
        final int columnCount = other.columnCount();
        final int barCount = other.barCount();
        for (int column = 0; column < columnCount; ++column) {
            for (int fromBar = 0; fromBar < barCount; ++fromBar) {
                Double value = other.get(column, fromBar);
                this.set(column, toBar+fromBar, value);
            }
        }
    }
        
    private void copyReversed(final int toBar, final DataSeries other) {
        final int columnCount = other.columnCount();
        final int barCount = other.barCount();
        for (int column = 0; column < columnCount; ++column) {
            for (int fromBar = 0; fromBar < barCount; ++fromBar) {
                Double value = other.get(column, barCount - fromBar - 1);
                this.set(column, toBar+fromBar, value);
            }
        }
    }
    
    private void copyStraight(DataSeries other) {
        final int expectedColumnCount = other.columnCount();
        final int expectedBarCount = other.barCount();
        resize(expectedColumnCount, expectedBarCount);
        copyStraight(0, other);
    }
    
    private void copyReversed(DataSeries other) {
        final int expectedColumnCount = other.columnCount();
        final int expectedBarCount = other.barCount();
        resize(expectedColumnCount, expectedBarCount);
        copyReversed(0, other);
    }
    
    private void copyStraightSection(DataSeries other, int firstBar, int maxBars) {
        final int expectedColumnCount = other.columnCount();
        final int otherBarCount = other.barCount();
        final int expectedBarCount = (firstBar < otherBarCount - maxBars) ? maxBars : otherBarCount - firstBar;
        resize(expectedColumnCount, expectedBarCount);
        for (int column = 0; column < expectedColumnCount; ++column) {
            for (int bar = 0; bar < expectedBarCount; ++bar) {
                Double value = other.get(column, firstBar + bar);
                this.set(column, bar, value);
            }
        }
    }
    
    private void copyReversedSection(DataSeries other, int firstBar, int maxBars) {
        final int expectedColumnCount = other.columnCount();
        final int expectedBarCount = (firstBar >= maxBars) ? maxBars : firstBar + 1;
        resize(expectedColumnCount, expectedBarCount);
        for (int column = 0; column < expectedColumnCount; ++column) {
            for (int bar = 0; bar < expectedBarCount; ++bar) {
                Double value = other.get(column, firstBar - bar);
                this.set(column, bar, value);
            }
        }
    }
}
