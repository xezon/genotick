package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.ProgramInfo;
import com.alphatica.genotick.timepoint.TimePoint;
import com.alphatica.genotick.timepoint.TimePointExecutor;
import com.alphatica.genotick.timepoint.TimePointResult;
import com.alphatica.genotick.timepoint.TimePointStats;
import com.alphatica.genotick.breeder.ProgramBreeder;
import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.data.DataUtils;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.killer.ProgramKiller;
import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.ui.UserOutput;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class SimpleEngine implements Engine {
    private EngineSettings engineSettings;
    private TimePointExecutor timePointExecutor;
    private ProgramKiller killer;
    private ProgramBreeder breeder;
    private Population population;
    private MainAppData data;
    private ProfitRecorder profitRecorder;

    private SimpleEngine() {
        profitRecorder = new ProfitRecorder();
    }
    public static Engine getEngine() {
        return new SimpleEngine();
    }

    @Override
    public List<TimePointStats> start(UserOutput output) {
        Thread.currentThread().setName("Main engine execution thread");
        double result = 1;
        initPopulation();
        TimePoint timePoint = new TimePoint(engineSettings.startTimePoint);
        List<TimePointStats> timePointStats = new ArrayList<>();
        while(engineSettings.endTimePoint.compareTo(timePoint) >= 0) {
            TimePointStats stat = executeTimePoint(timePoint, output);
            if(stat != null) {
                timePointStats.add(stat);
                result *= (stat.getPercentEarned() / 100 + 1);
                profitRecorder.recordProfit(stat.getPercentEarned());
                output.reportProfitForTimePoint(timePoint,(result - 1) * 100, stat.getPercentEarned());
                Debug.d("Time:",timePoint,"Percent earned so far:",(result - 1) * 100);
            }
            timePoint = timePoint.next();
        }
        if(!engineSettings.executionOnly) {
            savePopulation();
        }
        showSummary();
        return timePointStats;
    }

    private void showSummary() {
        Debug.d("Total: Profit:",profitRecorder.getProfit(),"Drawdown:",profitRecorder.getMaxDrawdown(),
                "Profit / DD:",profitRecorder.getProfit() / profitRecorder.getMaxDrawdown());
        Debug.d("Second Half: Profit:",profitRecorder.getProfitSecondHalf(),"Drawdown:",profitRecorder.getMaxDrawdownSecondHalf(),
                "Profit / DD:",profitRecorder.getProfitSecondHalf() / profitRecorder.getMaxDrawdownSecondHalf());
    }

    private void savePopulation() {
        String dirName = getSavedPopulationDirName();
        File dirFile = new File(dirName);
        if(!dirFile.exists() && !dirFile.mkdirs()) {
            Debug.d("Unable to create directory",dirName);
        } else {
            population.savePopulation(dirName);
        }
    }

    @Override
    public void setSettings(EngineSettings engineSettings,
                            TimePointExecutor timePointExecutor,
                            MainAppData data,
                            ProgramKiller killer,
                            ProgramBreeder breeder,
                            Population population) {
        this.engineSettings = engineSettings;
        this.timePointExecutor = timePointExecutor;
        this.killer = killer;
        this.breeder = breeder;
        this.population = population;
        this.data = data;
    }

    private String getSavedPopulationDirName() {
        return "savedPopulation_" + DataUtils.getDateTimeString();
    }

    private void initPopulation() {
        if(population.getSize() == 0 && !engineSettings.executionOnly)
            breeder.breedPopulation(population, timePointExecutor.getProgramInfos());
    }

    private TimePointStats executeTimePoint(TimePoint timePoint, UserOutput output) {
        List<ProgramData> programDataList = data.prepareProgramDataList(timePoint);
        if(programDataList.isEmpty())
            return null;
        Debug.d("Starting TimePoint:", timePoint);
        TimePointResult timePointResult = timePointExecutor.execute(programDataList, population, !engineSettings.executionOnly);
        TimePointStats timePointStats = TimePointStats.getNewStats(timePoint);
        for(DataSetResult dataSetResult: timePointResult.listDataSetResults()) {
            Prediction prediction = dataSetResult.getCumulativePrediction();
            Debug.d(timePoint,"Prediction for:",dataSetResult.getName(),prediction);
            output.showPrediction(timePoint,dataSetResult.getName(),prediction);
            Double actualChange = data.getActualChange(dataSetResult.getName(),timePoint);
            if(!actualChange.isNaN()) {
                Debug.d("Actual change:", actualChange);
                printPercentEarned(dataSetResult.getName(), actualChange, prediction);
                timePointStats.update(dataSetResult.getName(), actualChange, prediction);
            }
        }
        if(!engineSettings.executionOnly) {
            updatePopulation();
        }
        Debug.d("Finished TimePoint:",timePoint);
        return timePointStats;
    }

    private void printPercentEarned(DataSetName name, Double actualChange, Prediction prediction) {
        double percent;
        if(prediction == Prediction.OUT) {
            Debug.d("No position");
            return;
        }
        if(prediction.isCorrect(actualChange))
            percent = Math.abs(actualChange);
        else
            percent = -Math.abs(actualChange);
        Debug.d("Profit for",name,percent);
    }

    private void updatePopulation() {
        List<ProgramInfo> list = timePointExecutor.getProgramInfos();
        killer.killPrograms(population,list);
        breeder.breedPopulation(population,list);
    }
}
