package com.alphatica.genotick.population;

import com.alphatica.genotick.genotick.MainSettings;

public class ProgramExecutorSettings {
    public final int instructionLimit;

    public ProgramExecutorSettings(MainSettings settings) {
        instructionLimit = settings.processorInstructionLimit;
    }
}
