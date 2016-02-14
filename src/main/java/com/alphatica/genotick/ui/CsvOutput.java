package com.alphatica.genotick.ui;

import com.alphatica.genotick.genotick.Debug;
import com.alphatica.genotick.timepoint.TimePoint;

import java.lang.management.ManagementFactory;

public class CsvOutput implements UserOutput {
    private final ConsoleOutput console;
    private final Debug.Instance profitWriter;

    public CsvOutput() {
        console = new ConsoleOutput();
        profitWriter = Debug.getInstance();
        profitWriter.setShowTime(false);
        setWriterFile();
    }

    private void setWriterFile() {
        String fileName = ManagementFactory.getRuntimeMXBean().getName();
        if (fileName.contains("@")) {
            fileName = fileName.substring(0, fileName.indexOf('@'));
        }
        fileName = "profit_" + fileName + ".csv";
        profitWriter.toFile(fileName);
        profitWriter.d("<Time Point>,<Cumulative Profit>,<Time Point Profit>");
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
}
