package com.alphatica.genotick.genotick;

import com.alphatica.genotick.timepoint.TimePoint;

class EngineSettings {
    public final TimePoint startTimePoint;
    public final TimePoint endTimePoint;
    public final boolean performTraining;
    public final int maximumDataOffset;
    public final boolean requireSymmetricalRobots;
    public final double resultThreshold;
    
    public EngineSettings(final MainSettings settings) {
        this.startTimePoint = settings.startTimePoint;
        this.endTimePoint = settings.endTimePoint;
        this.performTraining = settings.performTraining;
        this.maximumDataOffset = settings.maximumDataOffset;
        this.requireSymmetricalRobots = settings.requireSymmetricalRobots;
        this.resultThreshold = settings.resultThreshold;
    }
}
