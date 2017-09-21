package com.alphatica.genotick.population;

import com.alphatica.genotick.genotick.MainSettings;

public class RobotExecutorSettings {
    public final int processorInstructionLimit;

    public RobotExecutorSettings(MainSettings settings) {
        processorInstructionLimit = settings.processorInstructionLimit;
    }
}
