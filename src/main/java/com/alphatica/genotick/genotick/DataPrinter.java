package com.alphatica.genotick.genotick;

import java.util.Date;

import com.alphatica.genotick.chart.GenoChart;
import com.alphatica.genotick.chart.GenoChartFactory;
import com.alphatica.genotick.chart.GenoChartMode;
import com.alphatica.genotick.data.Column;
import com.alphatica.genotick.data.DataLoader;
import com.alphatica.genotick.data.DataSeries;
import com.alphatica.genotick.data.DataSet;
import com.alphatica.genotick.data.FileSystemDataLoader;
import com.alphatica.genotick.data.Filters;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.timepoint.TimePoint;
import com.alphatica.genotick.ui.UserOutput;

class DataPrinter {
    static void DrawData(UserOutput output, String dataDirectory, String beginString, String endString) {
        TimePoint timeBegin = (beginString != null) ? new TimePoint(beginString) : new TimePoint(0);
        TimePoint timeEnd = (beginString != null) ? new TimePoint(endString) : new TimePoint(Long.MAX_VALUE);
        DataLoader loader = new FileSystemDataLoader(output);
        MainAppData data = loader.loadAll(dataDirectory);
        GenoChart chart = GenoChartFactory.create(GenoChartMode.JFREECHART_DRAW, output);
        for (DataSet set : data.getDataSets()) {
            DataSeries series = set.createOhlcDataSection(timeBegin, timeEnd, false, false);
            DataSeries ema = series.createCopy();
            DataSeries emazl = series.createCopy();
            Filters.applyEMA(ema, 20);
            Filters.applyEMAZeroLag(emazl, 20, 50);
            String chartName = set.getName().getName();
            int barCount = series.barCount();
            if (series.columnCount() >= 4) {
                for (int bar = 0; bar < barCount; ++bar) {
                    TimePoint timePoint = set.getTimePoint(bar);
                    Date date = timePoint.asDate();
                    double open = series.get(Column.OHLC.OPEN, bar);
                    double high = series.get(Column.OHLC.HIGH, bar);
                    double low = series.get(Column.OHLC.LOW, bar);
                    double close = series.get(Column.OHLC.CLOSE, bar);
                    chart.addCandlestickChart(chartName, "ohlc", date, open, high, low, close);
                    chart.addTimeSeriesChart(chartName, "ema", date, ema.get(Column.OHLC.CLOSE, bar));
                    chart.addTimeSeriesChart(chartName, "emazl", date, emazl.get(Column.OHLC.CLOSE, bar));
                }
            }
        }
        chart.plotAll();
    }
}
