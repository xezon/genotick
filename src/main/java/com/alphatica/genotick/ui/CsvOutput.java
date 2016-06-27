package com.alphatica.genotick.ui;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.genotick.Debug;
import com.alphatica.genotick.genotick.Prediction;
import com.alphatica.genotick.timepoint.TimePoint;

import java.lang.management.ManagementFactory;

public class CsvOutput implements UserOutput {
    private final ConsoleOutput console;
    private final Debug.Instance profitWriter;
    private final Debug.Instance predictionWriter;
    private final String pidString;

    public CsvOutput() {
        console = new ConsoleOutput();
        pidString = getPidString();

        /* Profit Writer */
        profitWriter = Debug.getInstance();
        profitWriter.setShowTime(false);
        setProfitWriterFile();

        /* Prediction Writer */
        predictionWriter = Debug.getInstance();
        predictionWriter.setShowTime(false);
        setPredictionWriterFile();
    }

    @Override
    public void errorMessage(String message) {
        console.errorMessage(message);
    }

    @Override
    public void warningMessage(String message) {
        console.warningMessage(message);
    }

    @Override
    public void reportProfitForTimePoint(TimePoint timePoint, double cumulativeProfit, double timePointProfit) {
        String line = timePoint.toString() + "," + String.valueOf(cumulativeProfit) + "," + String.valueOf(timePointProfit);
        profitWriter.d(line);
    }

    @Override
    public void showPrediction(TimePoint timePoint, DataSetName name, Prediction prediction) {
        String line = String.format("%s,%s,%s",timePoint.toString(),name.toString(),prediction.toString());
        predictionWriter.d(line);
    }

    @Override
    public Thread.UncaughtExceptionHandler createExceptionHandler() {
        return console.createExceptionHandler();
    }

    private void setProfitWriterFile() {
        String fileName = "profit_" + pidString + ".csv";
        profitWriter.toFile(fileName);
        profitWriter.d("<Time Point>,<Cumulative Profit>,<Time Point Profit>");
    }

    private void setPredictionWriterFile() {
        String fileName = "predictions_" + pidString + ".csv";
        predictionWriter.toFile(fileName);
        predictionWriter.d("<Time Point>,<Name>,<Prediction>");
    }
    private String getPidString() {
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        if (pid.contains("@")) {
            return pid.substring(0, pid.indexOf('@'));
        } else {
            return pid;
        }
    }

}
