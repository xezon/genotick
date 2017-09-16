package com.alphatica.genotick.chart;

public interface GenoChart {
    void addXYLineChart(String chartTitle, String xLabel, String yLabel, String seriesTitle, double xValue, double yValue);
    void plot(String chartTitle);
    void plotAll();
}
