package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.Program;
import com.alphatica.genotick.population.ProgramExecutor;
import com.alphatica.genotick.population.ProgramName;
import com.alphatica.genotick.processor.ProgramExecutorFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

class SimpleTimePointExecutor implements TimePointExecutor {

    private final ExecutorService executorService;
    private DataSetExecutor dataSetExecutor;
    private ProgramExecutorFactory programExecutorFactory;


    public SimpleTimePointExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        executorService = Executors.newFixedThreadPool(cores * 2, new DaemonThreadFactory());
    }

    @Override
    public TimePointResult execute(List<ProgramData> programDataList,
                                   Population population, boolean updatePrograms) {
        TimePointResult timePointResult = new TimePointResult();
        if(programDataList.isEmpty())
            return timePointResult;
        List<Future<List<ProgramResult>>> tasks = submitTasks(programDataList,population,updatePrograms);
        getResults(timePointResult,tasks);
        return timePointResult;
    }

    private void getResults(TimePointResult timePointResult, List<Future<List<ProgramResult>>> tasks) {
        while(!tasks.isEmpty()) {
            try {
                int lastIndex = tasks.size() - 1;
                Future<List<ProgramResult>> future = tasks.get(lastIndex);
                List<ProgramResult> results = future.get();
                tasks.remove(lastIndex);
                for(ProgramResult result: results) {
                    timePointResult.addProgramResult(result);
                }
            } catch (InterruptedException ignore) {
                /* Do nothing, try again */
            } catch (ExecutionException e) {
                Debug.d(e);
            }
        }
    }


    @Override
    public void setSettings(DataSetExecutor dataSetExecutor, ProgramExecutorFactory programExecutorFactory) {
        this.dataSetExecutor = dataSetExecutor;
        this.programExecutorFactory = programExecutorFactory;
    }

    /*
       Return type here is as ugly as it gets and I'm not proud. However, it seems to be the quickest.
       */
    private List<Future<List<ProgramResult>>> submitTasks(List<ProgramData> programDataList,
                                                          Population population,
                                                          boolean updatePrograms) {
        List<Future<List<ProgramResult>>> tasks = new ArrayList<>();
        for(ProgramName programName: population.listProgramNames()) {
            Task task = new Task(programName, programDataList, population, updatePrograms);
            Future<List<ProgramResult>> future = executorService.submit(task);
            tasks.add(future);
        }
        return tasks;
    }

    private class Task implements Callable<List<ProgramResult>> {

        private final ProgramName programName;
        private final List<ProgramData> programDataList;
        private final Population population;
        private final boolean updatePrograms;

        public Task(ProgramName programName, List<ProgramData> programDataList, Population population, boolean updatePrograms) {
            this.programName = programName;
            this.programDataList = programDataList;
            this.population = population;
            this.updatePrograms = updatePrograms;
        }

        @Override
        public List<ProgramResult> call() throws Exception {
            ProgramExecutor programExecutor = programExecutorFactory.getDefaultProgramExecutor();
            Program program = population.getProgram(programName);
            List<ProgramResult> list = dataSetExecutor.execute(programDataList,program,programExecutor);
            if(updatePrograms) {
                updateProgram(program,list);
            }
            return list;
        }

        private void updateProgram(Program program,List<ProgramResult> list) {
            for(ProgramResult result: list) {
                program.recordPrediction(result.getPrediction());
                Outcome outcome = Outcome.getOutcome(result.getPrediction(),result.getData().getActualChange());
                List<Outcome> outcomes = new ArrayList<>();
                outcomes.add(outcome);
                program.recordOutcomes(outcomes);
            }
            population.saveProgram(program);
        }
    }
}

class DaemonThreadFactory implements ThreadFactory {
    private static int counter = 1;
    @Override
    public Thread newThread(@SuppressWarnings("NullableProblems") Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.setName("TimePointExecutor thread: " + String.valueOf(counter++));
        return thread;
    }
}

