package com.alphatica.genotick.processor;

import com.alphatica.genotick.population.ProgramExecutor;
import com.alphatica.genotick.population.ProgramExecutorSettings;
import com.alphatica.genotick.processor.SimpleProcessor;

public class ProgramExecutorFactory {
    private final ProgramExecutorSettings settings;

    public static ProgramExecutor getDefaultProgramExecutor(ProgramExecutorSettings settings) {
        ProgramExecutor executor = SimpleProcessor.createProcessor();
        executor.setSettings(settings);
        return executor;
    }

    public ProgramExecutorFactory(ProgramExecutorSettings settings) {
        this.settings = settings;
    }

    public ProgramExecutor getDefaultProgramExecutor() {
        ProgramExecutor executor = SimpleProcessor.createProcessor();
        executor.setSettings(settings);
        return executor;
    }
}
