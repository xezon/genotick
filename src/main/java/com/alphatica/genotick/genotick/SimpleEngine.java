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
import com.alphatica.genotick.ui.UserOutput;
import com.alphatica.genotick.utility.Tools;

import static com.alphatica.genotick.utility.Assert.gassert;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SimpleEngine implements Engine {
    private EngineSettings engineSettings;
    private TimePointExecutor timePointExecutor;
    private RobotKiller killer;
    private RobotBreeder breeder;
    private Population population;
    private MainAppData data;
    private RobotDataManager robotDataManager;
    private final UserOutput output;
    private ProfitRecorder profitRecorder;
    private Account account;
    private MainInterface.SessionResult sessionResult;

    private SimpleEngine(UserOutput output) {
        this.output = output;
    }
    
    static Engine create(UserOutput output) {
        return new SimpleEngine(output);
    }

    @Override
    public void start() {
        changeThreadName();
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
                            Population population,
                            MainInterface.SessionResult sessionResult) {
        this.engineSettings = engineSettings;
        this.timePointExecutor = timePointExecutor;
        this.killer = killer;
        this.breeder = breeder;
        this.population = population;
        this.data = data;
        this.robotDataManager = new RobotDataManager(data, engineSettings.maximumDataOffset);
        this.profitRecorder = new ProfitRecorder(engineSettings.chartMode, output);
        this.account = new Account(BigDecimal.valueOf(100_000L), output, profitRecorder);
        this.sessionResult = sessionResult;
    }

    private void changeThreadName() {
        Thread currentThread =  Thread.currentThread();
        String threadName = String.format("Main engine execution thread %d", currentThread.getId());
        currentThread.setName(threadName);
    }
    
    private String getPopulationDirName() {
        final String path1 = output.getOutDir();
        final String path2 = "population_" + Tools.getProcessThreadIdString();
        return (path1 == null) ? path2 : Paths.get(path1, path2).toString();
    }

    private void initPopulation() {
        if (population.getSize() == 0 && engineSettings.performTraining) {
            breeder.breedPopulation(population, Collections.emptyList());
        }
    }

    private void savePopulation() {
        if (!population.saveOnDisk()) {
            String path = getPopulationDirName();
            population.saveToFolder(path);
        }
    }
    
    private void executeTimePoint(final TimePoint timePoint) {
        final int bar = data.getBar(timePoint);
        gassert(bar >= 0);
        robotDataManager.update(timePoint);
        final List<RobotDataPair> robotDataList = robotDataManager.getUpdatedRobotDataList();
        if (!robotDataList.isEmpty()) {
            output.reportStartedTimePoint(timePoint);
            updateAccount(robotDataList);
            recordMarketChangesInRobots(robotDataList);
            Map<RobotName, List<RobotResultPair>> robotResultMap = timePointExecutor.execute(robotDataList, population);
            recordRobotsPredictions(robotResultMap);
            TimePointResult timePointResult = new TimePointResult(robotResultMap);
            timePointResult.get().forEach(dataSetResult -> processDataSetResult(timePoint, dataSetResult));
            List<RobotInfo> robotInfoList = population.getRobotInfoList();
            performTraining(robotInfoList);
            output.reportFinishedTimePoint(timePoint, account.getEquity());
        }
        profitRecorder.onUpdate(bar);
    }
    
    private void processDataSetResult(TimePoint timePoint, DataSetResult dataSetResult) {
        Prediction prediction = dataSetResult.getCumulativePrediction(engineSettings.resultThreshold);
        DataSetName dataSetName = dataSetResult.getName();
        account.addPendingOrder(dataSetName, prediction);
        output.showPrediction(timePoint, dataSetResult, prediction);
        if (sessionResult != null) {
            sessionResult.savePrediction(timePoint, dataSetName, prediction);
        }
    }

    private void recordRobotsPredictions(Map<RobotName, List<RobotResultPair>> map) {
        if(engineSettings.performTraining) {
            map.entrySet().forEach(entry -> {
                Robot robot = population.getRobot(entry.getKey());
                entry.getValue().forEach(robot::recordPrediction);
                population.saveRobot(robot);
            });
        }
    }

    private void performTraining(List<RobotInfo> list) {
        if (engineSettings.performTraining) {
            killer.killRobots(population, list);
            breeder.breedPopulation(population, list);
            output.debugMessage("averageAge=" + population.getAverageAge());
        }
    }

    private void updateAccount(List<RobotDataPair> robotDataList) {
        Map<DataSetName, Double> map = new HashMap<>();
        for (RobotDataPair robotDataPair : robotDataList) {
            robotDataPair.forEach(robotData -> map.put(robotData.getName(), robotData.getLastPriceOpen()));
        }
        account.closeTrades(map);
        account.openTrades(map);
    }

    private void recordMarketChangesInRobots(List<RobotDataPair> robotDataList) {
        if(engineSettings.performTraining) {
            population.getRobots().forEach(robot -> {
                robotDataList.forEach(robot::recordMarketChange);
                population.saveRobot(robot);
            });
        }
    }
}
