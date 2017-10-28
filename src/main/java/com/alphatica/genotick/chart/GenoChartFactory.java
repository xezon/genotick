package com.alphatica.genotick.chart;

public class GenoChartFactory {

    private GenoChartFactory() {}
    
    public static GenoChart create(GenoChartMode mode) {
        if (mode.contains(GenoChartMode.JFREECHART)) {
            return new GenoJFreeChart(mode);
        }
        return new GenoBlankChart();
    }
}
