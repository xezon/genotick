package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.ProgramName;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

class SimpleTimePointExecutor implements TimePointExecutor {

    private DataSetExecutor dataSetExecutor;
    private final ExecutorService executorService;


    public SimpleTimePointExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        executorService = Executors.newFixedThreadPool(cores * 2);
    }

    @Override
    public TimePointResult execute(TimePoint timePoint, List<ProgramData> programDataList, Population population) {
        TimePointResult timePointResult = new TimePointResult(timePoint);
        if(programDataList.isEmpty())
            return timePointResult;
        List<Future<ProgramResult>> tasks = submitTasks(programDataList,population);
        getResults(timePointResult,tasks);
        return timePointResult;
    }

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

    private void getResults(TimePointResult timePointResult, List<Future<ProgramResult>> tasks) {
        while(!tasks.isEmpty()) {
            try {
                Future<ProgramResult> future = tasks.get(tasks.size()-1);
                ProgramResult result = future.get();
                tasks.remove(tasks.size()-1);
                timePointResult.addProgramResult(result,result.getData().getName());
            } catch (InterruptedException ignore) {
                /* Do nothing, try again */
            } catch (ExecutionException e) {
                Debug.d(e);
            }
        }
    }

    private List<Future<ProgramResult>> submitTasks(List<ProgramData> programDataList, Population population) {
        List<Future<ProgramResult>> tasks = new ArrayList<>();
        for(ProgramName programName: population.listProgramNames()) {
            for(ProgramData programData: programDataList) {
                Task task = new Task(programName,programData,population);
                Future<ProgramResult> future = executorService.submit(task);
                tasks.add(future);
            }
        }
        return tasks;
    }

    @Override
    public void setSettings(DataSetExecutor dataSetExecutor) {
        this.dataSetExecutor = dataSetExecutor;
    }
}
