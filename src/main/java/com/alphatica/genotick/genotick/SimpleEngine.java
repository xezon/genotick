package com.alphatica.genotick.genotick;

import com.alphatica.genotick.account.Account;
import com.alphatica.genotick.account.ProfitRecorder;
import com.alphatica.genotick.breeder.RobotBreeder;
import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.killer.RobotKiller;
import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.Robot;
import com.alphatica.genotick.population.RobotInfo;
import com.alphatica.genotick.population.RobotName;
import com.alphatica.genotick.timepoint.TimePoint;
import com.alphatica.genotick.timepoint.TimePointExecutor;
import com.alphatica.genotick.timepoint.TimePointResult;
import com.alphatica.genotick.ui.UserInputOutputFactory;
import com.alphatica.genotick.ui.UserOutput;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class SimpleEngine implements Engine {
    private EngineSettings engineSettings;
    private TimePointExecutor timePointExecutor;
    private RobotKiller killer;
    private RobotBreeder breeder;
    private Population population;
    private MainAppData data;
    private final UserOutput output = UserInputOutputFactory.getUserOutput();
    private final ProfitRecorder profitRecorder = new ProfitRecorder(output);
    private final Account account = new Account(BigDecimal.valueOf(100_000L), output, profitRecorder);

    static Engine getEngine() {
        return new SimpleEngine();
    }

    @Override
    public void start() {
        Thread.currentThread().setName("Main engine execution thread");
        initPopulation();
        final Stream<TimePoint> filteredTimePoints = data.getTimePoints(
                engineSettings.startTimePoint,
                engineSettings.endTimePoint);
        filteredTimePoints.forEach(this::executeTimePoint);
        if (engineSettings.performTraining) {
            savePopulation();
        }
        account.closeAccount();
        profitRecorder.onFinish();
    }

    @Override
    public void setSettings(EngineSettings engineSettings,
                            TimePointExecutor timePointExecutor,
                            MainAppData data,
                            RobotKiller killer,
                            RobotBreeder breeder,
                            Population population) {
        this.engineSettings = engineSettings;
        this.timePointExecutor = timePointExecutor;
        this.killer = killer;
        this.breeder = breeder;
        this.population = population;
        this.data = data;
    }

    private String getSavedPopulationDirName() {
        return "savedPopulation_" + Tools.getPidString();
    }

    private void initPopulation() {
        if (population.getSize() == 0 && engineSettings.performTraining) {
            breeder.breedPopulation(population, Collections.emptyList());
        }
    }

    private void savePopulation() {
        if (!population.saveOnDisk()) {
            String path = getSavedPopulationDirName();
            population.saveToFolder(path);
        }
    }

    // TODO To simplify algorithms consider working with bars instead of time points
    
    private void executeTimePoint(TimePoint timePoint) {
        final int bar = data.getBar(timePoint);
        final List<RobotData> robotDataList = data.createRobotDataList(timePoint);
        if (!robotDataList.isEmpty()) {
            output.reportStartingTimePoint(timePoint);
            updateAccount(robotDataList);
            List<RobotInfo> list = population.getRobotInfoList();
            recordMarketChangesInRobots(robotDataList);
            Map<RobotName, List<RobotResult>> map = timePointExecutor.execute(robotDataList, population);
            updatePredictions(list, map);
            recordRobotsPredictions(map);
            TimePointResult timePointResult = new TimePointResult(map);
            timePointResult.listDataSetResults().forEach(dataSetResult -> {
                Prediction prediction = dataSetResult.getCumulativePrediction(engineSettings.resultThreshold);
                account.addPendingOrder(dataSetResult.getName(), prediction);
                output.showPrediction(timePoint, dataSetResult, prediction);
            });
            checkTraining(list);
            output.reportFinishedTimePoint(timePoint, account.getValue());
        }
        profitRecorder.onUpdate(bar);
    }

    private void updatePredictions(List<RobotInfo> list, Map<RobotName, List<RobotResult>> map) {
        list.parallelStream().forEach(info -> {
            List<RobotResult> results = map.get(info.getName());
            if(results != null) {
                results.stream()
                        .filter(result -> result.getPrediction() != Prediction.OUT).findFirst()
                        .ifPresent(result -> info.setPredicting(true));
            }
        });
    }

    private void recordRobotsPredictions(Map<RobotName, List<RobotResult>> map) {
        if(engineSettings.performTraining) {
            map.keySet().forEach(name -> {
                Robot robot = population.getRobot(name);
                map.get(name).forEach(robot::recordPrediction);
                population.saveRobot(robot);
            });
        }
    }

    private void checkTraining(List<RobotInfo> list) {
        if (engineSettings.performTraining) {
            updatePopulation(list);
        }
    }

    private void updateAccount(List<RobotData> robotDataList) {
        Map<DataSetName, Double> map = robotDataList.stream().collect(Collectors.toMap(RobotData::getName, RobotData::getLastPriceOpen));
        account.closeTrades(map);
        account.openTrades(map);
    }

    private void recordMarketChangesInRobots(List<RobotData> robotDataList) {
        if(engineSettings.performTraining) {
            population.listRobotsNames().forEach(robotName -> {
                Robot robot = population.getRobot(robotName);
                robotDataList.forEach(robot::recordMarketChange);
                population.saveRobot(robot);
            });
        }
    }

    private void updatePopulation(List<RobotInfo> list) {
        killer.killRobots(population, list);
        breeder.breedPopulation(population, list);
        output.debugMessage("averageAge=" + population.getAverageAge());
    }
}
