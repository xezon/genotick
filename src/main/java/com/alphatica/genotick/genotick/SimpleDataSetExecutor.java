package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.*;
import com.alphatica.genotick.processor.ProgramExecutorFactory;

import java.util.ArrayList;
import java.util.List;

public class  SimpleDataSetExecutor implements DataSetExecutor {

    private ProgramExecutorSettings settings;

    @Override
    public ProgramResult execute(ProgramData programData, ProgramName programName, Population population) {
        Program program = population.getProgram(programName);
        assert program != null;
        ProgramResult result = execute(programData,program);
        population.saveProgram(program);
        return result;
    }

    @Override
    public List<ProgramResult> execute(List<ProgramData> programDataList, Program program, ProgramExecutor programExecutor) {
        List<ProgramResult> programResultList = new ArrayList<>(programDataList.size());
        for(ProgramData programData: programDataList) {
            Prediction prediction = programExecutor.executeProgram(programData,program);
            program.recordPrediction(prediction);
            ProgramResult result = new ProgramResult(prediction,program,programData);
            programResultList.add(result);
        }
        return programResultList;
    }

    @Override
    public void setExecutorFactory(ProgramExecutorSettings settings) {
        this.settings = settings;
    }

    private ProgramResult execute(ProgramData programData, Program program) {
        ProgramExecutor executor = ProgramExecutorFactory.getDefaultProgramExecutor(settings);
        Prediction prediction = executor.executeProgram(programData,program);
        ProgramResult result = new ProgramResult(prediction, program, programData);
        program.recordPrediction(result.getPrediction());
        return result;
    }


}
