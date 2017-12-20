package com.alphatica.genotick.chart;

import java.util.Date;

class GenoBlankChart implements GenoChart {

    @Override
    public void addXYLineChart(String chartTitle, String xLabel, String yLabel, String seriesTitle, double xValue, double yValue) {
    }

    @Override
    public void addCandlestickChart(String chartTitle, String seriesTitle, Date date, double open, double high, double low, double close) {
    }
    
    @Override
    public void addTimeSeriesChart(String chartTitle, String seriesTitle, Date date, double value) {
    }
    
    @Override
    public void plot(String chartTitle) {
    }

    @Override
    public void plotAll() {
    }
}
