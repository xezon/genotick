package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.Program;
import com.alphatica.genotick.population.ProgramExecutor;
import com.alphatica.genotick.population.ProgramName;

public interface DataSetExecutor {
    ProgramResult execute(ProgramData programData, Program program);

    ProgramResult execute(ProgramData programData, ProgramName programName, Population population);

    void setExecutor(ProgramExecutor e);
}
