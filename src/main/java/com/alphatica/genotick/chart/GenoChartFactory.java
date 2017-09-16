package com.alphatica.genotick.chart;

public class GenoChartFactory {
    
    private static GenoChart INSTANCE;
    
    private GenoChartFactory() {}
    
    public static void initialize(GenoChartMode mode) {
        if (GenoChartMode.NONE == mode) {
            INSTANCE = new GenoBlankChart();
        }
        else {
            INSTANCE = new GenoJFreeChart(mode);
        }
    }
    
    public static GenoChart get() {
        return INSTANCE;
    }
}
