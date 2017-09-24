package com.alphatica.genotick.population;

import com.alphatica.genotick.genotick.MainSettings;

public class RobotExecutorSettings {
    public final int maximumProcessorInstructionFactor;

    public RobotExecutorSettings(MainSettings settings) {
        maximumProcessorInstructionFactor = settings.maximumProcessorInstructionFactor;
    }
}
