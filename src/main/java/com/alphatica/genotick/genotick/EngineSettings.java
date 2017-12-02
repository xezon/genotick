package com.alphatica.genotick.genotick;

import com.alphatica.genotick.chart.GenoChartMode;
import com.alphatica.genotick.timepoint.TimePoint;

public class EngineSettings {
    public final TimePoint startTimePoint;
    public final TimePoint endTimePoint;
    public final boolean performTraining;
    public final int maximumDataOffset;
    public final boolean killNonPredictingRobots;
    public final boolean requireSymmetricalRobots;
    public final double resultThreshold;
    public final GenoChartMode chartMode;
    
    public EngineSettings(final MainSettings settings) {
        this.startTimePoint = settings.startTimePoint;
        this.endTimePoint = settings.endTimePoint;
        this.performTraining = settings.performTraining;
        this.maximumDataOffset = settings.maximumDataOffset;
        this.killNonPredictingRobots = settings.killNonPredictingRobots;
        this.requireSymmetricalRobots = settings.requireSymmetricalRobots;
        this.resultThreshold = settings.resultThreshold;
        this.chartMode = settings.chartMode;
    }
}
