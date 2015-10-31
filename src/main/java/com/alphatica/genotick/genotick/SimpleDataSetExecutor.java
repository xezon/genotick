package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.Program;
import com.alphatica.genotick.population.ProgramExecutor;
import com.alphatica.genotick.population.ProgramName;

public class  SimpleDataSetExecutor implements DataSetExecutor {

    private ProgramExecutor executor;

    @Override
    public ProgramResult execute(ProgramData programData, Program program) {
        Prediction prediction = executor.executeProgram(programData,program);
        return new ProgramResult(prediction, program);
    }

    @Override
    public ProgramResult execute(ProgramData programData, ProgramName programName, Population population) {
        Program program = population.getProgram(programName);
        return execute(programData, program);
    }

    @Override
    public void setExecutor(ProgramExecutor e) {
        this.executor = e;
    }
}
