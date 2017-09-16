package com.alphatica.genotick.chart;

public enum GenoChartMode {
    NONE                (0),
    DRAW                (1<<0),
    SAVE                (1<<1),
    JFREECHART          (1<<2),
    JFREECHART_DRAW     (JFREECHART.value() | DRAW.value()),
    JFREECHART_SAVE     (JFREECHART.value() | SAVE.value()),
    JFREECHART_DRAW_SAVE(JFREECHART.value() | DRAW.value() | SAVE.value());
    
    private final int value;

    GenoChartMode(int value) {
        this.value = value;
    }
    
    private int value() {
        return value;
    }
    
    public boolean contains(GenoChartMode mode) {
        return (value & mode.value()) != 0;
    }
}
