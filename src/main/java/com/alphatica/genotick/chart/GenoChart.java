package com.alphatica.genotick.chart;

import java.util.Date;

public interface GenoChart {
    
    void addXYLineChart(String chartTitle, String xLabel, String yLabel, String seriesTitle, double xValue, double yValue);
    
    void addCandlestickChart(String chartTitle, String seriesTitle, Date date, double open, double high, double low, double close);
    
    void addTimeSeriesChart(String chartTitle, String seriesTitle, Date date, double value);
    
    void plot(String chartTitle);
    
    void plotAll();
}
