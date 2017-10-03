package com.alphatica.genotick.data;

import com.alphatica.genotick.processor.NotEnoughDataException;

public class DataSeries {
    private double[][] data;
    private boolean firstBarIsNewest;
        
    public DataSeries(boolean firstElementIsNewest) {
        this(0, 0, firstElementIsNewest);
    }
    
    public DataSeries(int columnCount, int barCount, boolean firstBarIsNewest) {
        this.firstBarIsNewest = firstBarIsNewest;
        allocate(columnCount, barCount);
    }
    
    public DataSeries(DataSeries other) {
        this.firstBarIsNewest = other.firstBarIsNewest;
        copy(other);
    }
    
    public DataSeries(DataSeries other, boolean firstBarIsNewest) {
        this.firstBarIsNewest = firstBarIsNewest;
        copy(other);
    }
    
    public void copy(DataSeries other) {
        if (firstBarIsNewest == other.firstBarIsNewest) {
            copyStraight(other);
        }
        else {
            copyReversed(other);
        }
    }
    
    public void copySection(DataSeries other, int firstBar, int maxBars) {
        if (firstBarIsNewest == other.firstBarIsNewest) {
            copyStraightSection(other, firstBar, maxBars);
        }
        else {
            copyReversedSection(other, firstBar, maxBars);
        }
    }
    
    public DataSeries getCopy() {
        return new DataSeries(this);
    }
    
    public DataSeries getReversedCopy() {
        return new DataSeries(this, !firstBarIsNewest);
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
        return (columnCount() > 0) ? data[0].length : 0;
    }
    
    public boolean firstBarIsNewest() {
        return firstBarIsNewest;
    }
    
    private void allocate(int columnCount, int barCount) {
        data = new double[columnCount][barCount];
    }
    
    private void allocateIfNecessary(int expectedColumnCount, int expectedBarCount) {
        if ((columnCount() != expectedColumnCount) || (barCount() != expectedBarCount)) {
            allocate(expectedColumnCount, expectedBarCount);
        }
    }
    
    private void copyStraight(DataSeries other) {
        final int expectedColumnCount = other.columnCount();
        final int expectedBarCount = other.barCount();
        allocateIfNecessary(expectedColumnCount, expectedBarCount);
        for (int column = 0; column < expectedColumnCount; ++column) {
            for (int bar = 0; bar < expectedBarCount; ++bar) {
                data[column][bar] = other.data[column][bar];
            }
        }
    }
    
    private void copyReversed(DataSeries other) {
        final int expectedColumnCount = other.columnCount();
        final int expectedBarCount = other.barCount();
        allocateIfNecessary(expectedColumnCount, expectedBarCount);
        for (int column = 0; column < expectedColumnCount; ++column) {
            for (int bar = 0; bar < expectedBarCount; ++bar) {
                data[column][bar] = other.data[column][expectedBarCount - bar - 1];
            }
        }
    }
    
    private void copyStraightSection(DataSeries other, int firstBar, int maxBars) {
        final int expectedColumnCount = other.columnCount();
        final int otherBarCount = other.barCount();
        final int expectedBarCount = (firstBar < otherBarCount - maxBars) ? maxBars : otherBarCount - firstBar;
        allocateIfNecessary(expectedColumnCount, expectedBarCount);
        for (int column = 0; column < expectedColumnCount; ++column) {
            for (int bar = 0; bar < expectedBarCount; ++bar) {
                data[column][bar] = other.data[column][firstBar + bar];
            }
        }
    }
    
    private void copyReversedSection(DataSeries other, int firstBar, int maxBars) {
        final int expectedColumnCount = other.columnCount();
        final int expectedBarCount = (firstBar >= maxBars) ? maxBars : firstBar + 1;
        allocateIfNecessary(expectedColumnCount, expectedBarCount);
        for (int column = 0; column < expectedColumnCount; ++column) {
            for (int bar = 0; bar < expectedBarCount; ++bar) {
                data[column][bar] = other.data[column][firstBar - bar];
            }
        }
    }
}
