package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.*;

import java.util.List;

interface DataSetExecutor {

    List<ProgramResult> execute(List<ProgramData> programDataList, Program program, ProgramExecutor programExecutor);

}
