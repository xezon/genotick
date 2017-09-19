package com.alphatica.genotick.ui;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.genotick.DataSetResult;
import com.alphatica.genotick.genotick.Prediction;
import com.alphatica.genotick.genotick.Tools;
import com.alphatica.genotick.timepoint.TimePoint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

import static java.lang.String.format;

public class CsvOutput implements UserOutput {
    private final ConsoleOutput console;
    private final SimpleTextWriter profitWriter;
    private final SimpleTextWriter predictionWriter;
    private final String pidString;
    private Boolean debug = false;

    public CsvOutput() throws IOException {
        console = new ConsoleOutput();
        pidString = Tools.getPidString();
        profitWriter = new SimpleTextWriter("profit_" + pidString + ".csv");
        predictionWriter = new SimpleTextWriter("predictions_" + pidString + ".csv");
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
    public void showPrediction(TimePoint timePoint, DataSetResult result, Prediction prediction) {
        String line = format("%s,%s,%s,%s,%s,%.2f,%.2f",
                timePoint.toString(),result.getName().toString(),prediction.toString(),
                result.getCountUp(),result.getCountDown(),result.getWeightUp(),result.getWeightDown()
        );
        predictionWriter.writeLine(line);
    }

    @Override
    public void reportAccountOpening(BigDecimal balance) {

    }

    @Override
    public void reportPendingTrade(DataSetName name, Prediction prediction) {

    }

    @Override
    public void reportOpeningTrade(DataSetName name, BigDecimal quantity, Double price) {

    }

    @Override
    public void reportClosingTrade(DataSetName name, BigDecimal quantity, BigDecimal price, BigDecimal profit, BigDecimal balance) {

    }

    @Override
    public void reportAccountClosing(BigDecimal balance) {

    }

    @Override
    public void reportStartingTimePoint(TimePoint timePoint) {

    }

    @Override
    public void reportFinishedTimePoint(TimePoint timePoint, BigDecimal equity) {
        String line = format("%s,%s", timePoint.toString(), equity.toPlainString());
        profitWriter.writeLine(line);
    }

    @Override
	public void debugMessage(String message) {
		if(this.debug)
			console.infoMessage(message);
	}


    @Override
    public void infoMessage(String message) {
        console.infoMessage(message);
    }
}

class SimpleTextWriter {
    private final PrintWriter writer;
    SimpleTextWriter(String fileName) throws IOException {
        File file = new File(fileName);
        writer = new PrintWriter(new FileOutputStream(file));
    }

    void writeLine(String line) {
        writer.println(line);
        writer.flush();
    }
}
