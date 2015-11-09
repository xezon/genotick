package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.*;

import java.util.List;

public interface DataSetExecutor {

    ProgramResult execute(ProgramData programData, ProgramName programName, Population population);

    List<ProgramResult> execute(List<ProgramData> programDataList, Program program, ProgramExecutor programExecutor);
    void setExecutorFactory(ProgramExecutorSettings settings);

}
