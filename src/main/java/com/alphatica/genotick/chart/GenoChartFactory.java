package com.alphatica.genotick.chart;

import com.alphatica.genotick.ui.UserOutput;

public class GenoChartFactory {

    private GenoChartFactory() {}
    
    public static GenoChart create(GenoChartMode mode, UserOutput output) {
        if (mode.contains(GenoChartMode.JFREECHART)) {
            return new GenoJFreeChart(mode, output);
        }
        return new GenoBlankChart();
    }
}
