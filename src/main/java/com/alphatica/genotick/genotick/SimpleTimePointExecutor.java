package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.ProgramName;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

class SimpleTimePointExecutor implements TimePointExecutor {

    private DataSetExecutor dataSetExecutor;
    private final ExecutorService executorService = Executors.newFixedThreadPool(8);


    private class Task implements Callable<ProgramResult> {

        private final ProgramName programName;
        private final ProgramData programData;
        private final Population population;

        public Task(ProgramName programName, ProgramData programData, Population population) {

            this.programName = programName;
            this.programData = programData;
            this.population = population;
        }

        @Override
        public ProgramResult call() throws Exception {
            return dataSetExecutor.execute(programData,programName,population);
        }
    }

    @Override
    public TimePointResult execute(TimePoint timePoint, List<ProgramData> programDataList, Population population) {
        TimePointResult timePointResult = new TimePointResult(timePoint);
        if(programDataList.isEmpty())
            return timePointResult;
        List<Future<ProgramResult>> tasks = new ArrayList<>();
        for(ProgramName programName: population.listProgramNames()) {
            for(ProgramData programData: programDataList) {
                Task task = new Task(programName,programData,population);
                Future<ProgramResult> future = executorService.submit(task);
                tasks.add(future);
            }
        }

        for(Future<ProgramResult> future: tasks) {
            try {
                ProgramResult result = future.get();
                timePointResult.addProgramResult(result,result.getData().getName());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        return timePointResult;
    }

    @Override
    public void setSettings(DataSetExecutor dataSetExecutor) {
        this.dataSetExecutor = dataSetExecutor;
    }



}
