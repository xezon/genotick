package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.Program;
import com.alphatica.genotick.population.ProgramExecutor;

import java.util.ArrayList;
import java.util.List;

public class  SimpleDataSetExecutor implements DataSetExecutor {


    @Override
    public List<ProgramResult> execute(List<ProgramData> programDataList, Program program, ProgramExecutor programExecutor) {
        List<ProgramResult> programResultList = new ArrayList<>(programDataList.size());
        for(ProgramData programData: programDataList) {
            Prediction prediction = programExecutor.executeProgram(programData,program);
            ProgramResult result = new ProgramResult(prediction,program,programData);
            programResultList.add(result);
        }
        return programResultList;
    }

}
