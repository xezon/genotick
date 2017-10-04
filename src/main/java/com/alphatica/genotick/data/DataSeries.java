package com.alphatica.genotick.data;

import com.alphatica.genotick.processor.NotEnoughDataException;

public class DataSeries {
    private double[][] data;
    private final boolean firstBarIsNewest;
        
    public DataSeries(boolean firstElementIsNewest) {
        this(0, 0, firstElementIsNewest);
    }
    
    public DataSeries(int columnCount, int barCount, boolean firstBarIsNewest) {
        this.firstBarIsNewest = firstBarIsNewest;
        resize(columnCount, barCount);
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
    
    public void add(DataSeries other) {
        if (firstBarIsNewest == other.firstBarIsNewest) {
            addStraight(other);
        }
        else {
            addReversed(other);
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
    
    private void resize(int columnCount, int barCount) {
        data = new double[columnCount][barCount];
    }
    
    private void resizeIfNecessary(int expectedColumnCount, int expectedBarCount) {
        if ((columnCount() != expectedColumnCount) || (barCount() != expectedBarCount)) {
            resize(expectedColumnCount, expectedBarCount);
        }
    }
    
    private void copyStraight(final int toBar, final DataSeries other) {
        final int columnCount = other.columnCount();
        final int barCount = other.barCount();
        for (int column = 0; column < columnCount; ++column) {
            for (int fromBar = 0; fromBar < barCount; ++fromBar) {
                data[column][toBar+fromBar] = other.data[column][fromBar];
            }
        }
    }
        
    private void copyReversed(final int toBar, final DataSeries other) {
        final int columnCount = other.columnCount();
        final int barCount = other.barCount();
        for (int column = 0; column < columnCount; ++column) {
            for (int fromBar = 0; fromBar < barCount; ++fromBar) {
                data[column][toBar+fromBar] = other.data[column][barCount - fromBar - 1];
            }
        }
    }
    
    private void moveBarsRight(int barCount, int moveCount) {
        final int columnCount = columnCount();
        for (int column = 0; column < columnCount; ++column) {
            for (int bar = barCount + moveCount - 1; bar >= barCount; --bar) {
                data[column][bar] = data[column][bar - barCount];
            }
        }
    }
    
    private void copyStraight(DataSeries other) {
        final int expectedColumnCount = other.columnCount();
        final int expectedBarCount = other.barCount();
        resizeIfNecessary(expectedColumnCount, expectedBarCount);
        copyStraight(0, other);
    }
    
    private void copyReversed(DataSeries other) {
        final int expectedColumnCount = other.columnCount();
        final int expectedBarCount = other.barCount();
        resizeIfNecessary(expectedColumnCount, expectedBarCount);
        copyReversed(0, other);
    }
    
    private void copyStraightSection(DataSeries other, int firstBar, int maxBars) {
        final int expectedColumnCount = other.columnCount();
        final int otherBarCount = other.barCount();
        final int expectedBarCount = (firstBar < otherBarCount - maxBars) ? maxBars : otherBarCount - firstBar;
        resizeIfNecessary(expectedColumnCount, expectedBarCount);
        for (int column = 0; column < expectedColumnCount; ++column) {
            for (int bar = 0; bar < expectedBarCount; ++bar) {
                data[column][bar] = other.data[column][firstBar + bar];
            }
        }
    }
    
    private void copyReversedSection(DataSeries other, int firstBar, int maxBars) {
        final int expectedColumnCount = other.columnCount();
        final int expectedBarCount = (firstBar >= maxBars) ? maxBars : firstBar + 1;
        resizeIfNecessary(expectedColumnCount, expectedBarCount);
        for (int column = 0; column < expectedColumnCount; ++column) {
            for (int bar = 0; bar < expectedBarCount; ++bar) {
                data[column][bar] = other.data[column][firstBar - bar];
            }
        }
    }
    
    private void addStraight(DataSeries other) {
        final int thisBarCount = barCount();
        final int otherBarCount = other.barCount();
        final int expectedBarCount = thisBarCount + otherBarCount;
        resizeIfNecessary(other.columnCount(), expectedBarCount);
        if (firstBarIsNewest) {
            moveBarsRight(thisBarCount, otherBarCount);
            copyStraight(0, other);
        }
        else {
            copyStraight(thisBarCount, other);
        }
    }
    
    private void addReversed(DataSeries other) {
        final int thisBarCount = barCount();
        final int otherBarCount = other.barCount();
        final int expectedBarCount = thisBarCount + otherBarCount;
        resizeIfNecessary(other.columnCount(), expectedBarCount);
        if (firstBarIsNewest) {
            moveBarsRight(thisBarCount, otherBarCount);
            copyReversed(0, other);
        }
        else {
            copyReversed(thisBarCount, other);
        }
    }
}
