package com.alphatica.genotick.ui;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.exceptions.ExecutionException;
import com.alphatica.genotick.genotick.Prediction;
import com.alphatica.genotick.genotick.Tools;
import com.alphatica.genotick.timepoint.TimePoint;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;

import static java.lang.String.format;

class ConsoleOutput implements UserOutput {

    private File logFile = new File(format("genotick-log-%s.txt", Tools.getPidString()));
    private Boolean debugEnabled = false;

    @Override
    public void errorMessage(String message) {
        log("Error: " + message);
    }

    @Override
    public void warningMessage(String message) {
        log("Warning: " + message);
    }

    @Override
    public void reportProfitForTimePoint(TimePoint timePoint, double cumulativeProfit, double timePointProfit) {
        log("Profit for " + timePoint.toString() + ": "
                + "Cumulative profit: " + cumulativeProfit + " "
                + "TimePoint's profit: " + timePointProfit);
    }

    @Override
    public void showPrediction(TimePoint timePoint, DataSetName name, Prediction prediction) {
        log(format("%s prediction on %s for the next trade: %s",
                name.toString(),timePoint.toString(),prediction.toString()));
    }

    @Override
    public void reportAccountOpening(BigDecimal cash) {
        log(format("Openning account with %s cash", cash.toPlainString()));
    }

    @Override
    public void reportPendingTrade(DataSetName name, Prediction prediction) {
        log(format("Adding pending trade %s for market %s", prediction, name));
    }

    @Override
    public void reportOpeningTrade(DataSetName name, BigDecimal quantity, Double price) {
        log(format("Opening %s trade. Quantity: %s, price: %.4f", name, scale(quantity), price));
    }

    @Override
    public void reportClosingTrade(DataSetName name, BigDecimal quantity, BigDecimal price, BigDecimal profit, BigDecimal cash) {
        log(format("Closing %s trade. Quantity %s, price: %s, profit: %s, current cash: %s",
                name, scale(quantity), scale(price), scale(profit), scale(cash)));
    }

    @Override
    public void reportAccountClosing(BigDecimal cash) {
        log(format("Account closing with final value %s", scale(cash)));
    }

    @Override
    public void infoMessage(String s) {
        log(s);
    }

    @Override
    public void reportStartingTimePoint(TimePoint timePoint) {
        log(format("Starting time point %s", timePoint));
    }

    @Override
    public void reportFinishedTimePoint(TimePoint timePoint, BigDecimal value) {
        log(format("Finished time point %s with acount value %s", timePoint, scale(value)));
    }

    private void log(String string) {
        System.out.println(string);
        try {
            FileUtils.write(logFile, string + System.lineSeparator(), Charset.defaultCharset(),true);
        } catch (IOException e) {

            System.err.println("Unable to write to file " + logFile.getPath() + ": " + e.getMessage());

            throw new ExecutionException(format("Unable to write to file %s", logFile.getAbsoluteFile()), e);
        }
    }

    private String scale(BigDecimal bigDecimal) {
        return bigDecimal.setScale(4, BigDecimal.ROUND_HALF_DOWN).toPlainString();
    }

	@Override
	public void debugMessage(String message) {
		if(debugEnabled) {
            log(message);
        }
	}

}
