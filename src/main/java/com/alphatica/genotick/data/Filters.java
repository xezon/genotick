package com.alphatica.genotick.data;

import java.util.function.Consumer;

import static com.alphatica.genotick.utility.Assert.gassert;

public class Filters {
    static private class Index {
        int column;
        int bar;
    }
    
    static private int normalizeBarBegin(int barBegin, int maxLookbackBars) {
        return Math.max(maxLookbackBars, barBegin);
    }
    
    static private void forEachBarAndColumn(DataSeries series, int barBegin, int barEnd, Consumer<Index> action) {
        // For simplicity sake all filters assume asset data with ascending time points
        gassert(series.firstBarIsNewest() == false);
        barEnd = Math.min(series.barCount(), barEnd);
        final Index idx = new Index();
        idx.column = series.columnCount();
        while (--idx.column >= 0) {
            idx.bar = barBegin - 1;
            while (++idx.bar < barEnd) {
                action.accept(idx);
            }
        }
    }
    
    static public void applyEMA(DataSeries series, int length) {
        applyEMA(series, 0, Integer.MAX_VALUE, length);
    }
    
    static public void applyEMA(DataSeries series, int barBegin, int barEnd, int length) {
        final double alpha = 2.0 / (length + 1);
        barBegin = normalizeBarBegin(barBegin, 1);
        forEachBarAndColumn(series, barBegin, barEnd, idx -> {
            double price0 = series.get(idx.column, idx.bar);
            double price1 = series.get(idx.column, idx.bar - 1);
            double ema = alpha * price0 + (1.0 - alpha) * price1;
            series.set(idx.column, idx.bar, ema);
        });
    }
    
    static public void applyEMAZeroLag(DataSeries series, int length, int gainLimit) {
        applyEMAZeroLag(series, 0, Integer.MAX_VALUE, length, gainLimit);
    }
    
    static public void applyEMAZeroLag(DataSeries series, int barBegin, int barEnd, int length, int gainLimit) {
        final double alpha = 2.0 / (length + 1);
        barBegin = normalizeBarBegin(barBegin, 1);
        forEachBarAndColumn(series, barBegin, barEnd, idx -> {
            double price0 = series.get(idx.column, idx.bar);
            double price1 = series.get(idx.column, idx.bar - 1);
            double ema = alpha * price0 + (1.0 - alpha) * price1;
            double leastError = Double.MAX_VALUE;
            double bestGain = 0.0;
            for (int igain = -gainLimit; igain <= gainLimit; ++igain) {
                double gain = (double)igain / 10.0;
                double ec = alpha * (ema + gain * (price0 - price1)) + (1 - alpha) * price1;
                double error = Math.abs(price0 - ec);
                if (error < leastError) {
                    leastError = error;
                    bestGain = gain;
                }
            }
            double ec = alpha * (ema + bestGain * (price0 - price1)) + (1 - alpha) * price1;
            series.set(idx.column, idx.bar, ec);
        });
    }
}
