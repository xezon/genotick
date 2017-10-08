package com.alphatica.genotick.data;

import java.util.ArrayList;

import com.alphatica.genotick.processor.NotEnoughDataException;

public class DataSeries {
    private final ArrayList<ArrayList<Double>> data;
    private final boolean firstBarIsNewest;
    
    public DataSeries(int columnCount, int barCount, boolean firstBarIsNewest) {
        this(columnCount, barCount, firstBarIsNewest, true);
    }
    
    public DataSeries(DataSeries other, int firstBar, int maxBars, boolean firstBarIsNewest) {
        this(other.columnCount(), maxBars, firstBarIsNewest, true);
        copySection(other, firstBar, maxBars);
    }
    
    public DataSeries(DataSeries other) {
        this(other.columnCount(), other.barCount(), other.firstBarIsNewest, true);
        copy(other);
    }
    
    public DataSeries(DataSeries other, boolean firstBarIsNewest) {
        this(other.columnCount(), other.barCount(), firstBarIsNewest, true);
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
    
    public DataSeries createCopy() {
        return new DataSeries(this);
    }
    
    public DataSeries createReversedCopy() {
        return new DataSeries(this, !firstBarIsNewest);
    }
    
    public double get(int column, int bar) {
        if (bar < data.get(column).size()) {
            return data.get(column).get(bar);
        }
        throw new NotEnoughDataException();
    }
    
    public void set(int column, int bar, double value) {
        data.get(column).set(bar, value);
    }
    
    public int columnCount() {
        return data.size();
    }
    
    public int barCount() {
        return (columnCount() > 0) ? data.get(0).size() : 0;
    }
    
    public boolean firstBarIsNewest() {
        return firstBarIsNewest;
    }
    
    private DataSeries(int columnCapacity, int barCapacity, boolean firstBarIsNewest, boolean resize) {
        this.data = new ArrayList<ArrayList<Double>>(columnCapacity);
        this.firstBarIsNewest = firstBarIsNewest;
        if (resize) {
            resize(columnCapacity, barCapacity);
        }
    }
    
    private void resizeColumns(int columnCount, int barCount) {
        data.ensureCapacity(columnCount);
        while (columnCount() > columnCount) {
            data.remove(columnCount() - 1);
        }
        while (columnCount() < columnCount) {
            data.add(new ArrayList<Double>(barCount));
        }
    }
    
    private void resizeBars(int columnCount, int barCount) {
        for (int column = 0; column < columnCount; ++column) {
            final ArrayList<Double> bars = data.get(column);
            bars.ensureCapacity(barCount);
            while (bars.size() > barCount) {
                bars.remove(bars.size() - 1);
            }
            while (bars.size() < barCount) {
                bars.add(0.0);
            }
        }
    }
    
    private void resize(int columnCount, int barCount) {
        resizeColumns(columnCount, barCount);
        resizeBars(columnCount, barCount);
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
                Double value = other.get(column, firstBar + bar);
                this.set(column, bar, value);
            }
        }
    }
    
    private void copyReversedSection(DataSeries other, int firstBar, int maxBars) {
        final int expectedColumnCount = other.columnCount();
        final int expectedBarCount = (firstBar >= maxBars) ? maxBars : firstBar + 1;
        resizeIfNecessary(expectedColumnCount, expectedBarCount);
        for (int column = 0; column < expectedColumnCount; ++column) {
            for (int bar = 0; bar < expectedBarCount; ++bar) {
                Double value = other.get(column, firstBar - bar);
                this.set(column, bar, value);
            }
        }
    }
}
