package com.alphatica.genotick.ui;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.exceptions.ExecutionException;
import com.alphatica.genotick.genotick.DataSetResult;
import com.alphatica.genotick.genotick.Prediction;
import com.alphatica.genotick.timepoint.TimePoint;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import static java.lang.String.format;

public class CsvOutput implements UserOutput {

    private final ConsoleOutput console;
    private File profitFile;
    private File predictionFile;
    private Boolean debug = false;

    public CsvOutput(String outdir) throws IOException {
        console = new ConsoleOutput(outdir);
        buildFileNames();
    }

    private void buildFileNames() {
        String outdir = console.getOutDir();
        String identifier = console.getIdentifier();
        profitFile = new File(outdir, "profit_" + identifier + ".csv");
        predictionFile = new File(outdir, "predictions_" + identifier + ".csv");
    }
    
    @Override
    public void setIdentifier(String identifier) {
        console.setIdentifier(identifier);
        buildFileNames();
    }
    
    @Override
    public String getIdentifier() {
        return console.getIdentifier();
    }
    
    @Override
    public String getOutDir() {
        return console.getOutDir();
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
        printPrediction(line);
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
    public void reportStartedTimePoint(TimePoint timePoint) {

    }

    @Override
    public void reportFinishedTimePoint(TimePoint timePoint, BigDecimal equity) {
        String line = format("%s,%s", timePoint.toString(), equity.toPlainString());
        printProfit(line);
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

    private void printProfit(String line) throws ExecutionException {
        printToFile(profitFile, line);
    }

    private void printPrediction(String line) throws ExecutionException {
        printToFile(predictionFile, line);
    }
    
    private void printToFile(File file, String line) throws ExecutionException {
        try {
            FileUtils.write(file, line + System.lineSeparator(), Charset.defaultCharset(), true);
        }
        catch (IOException e) {
            throw new ExecutionException(format("Unable to write to file %s", file.getAbsoluteFile()), e);
        }
    }
}
