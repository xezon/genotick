package com.alphatica.genotick.ui;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.exceptions.ExecutionException;
import com.alphatica.genotick.genotick.DataSetResult;
import com.alphatica.genotick.genotick.Prediction;
import com.alphatica.genotick.genotick.Tools;
import com.alphatica.genotick.timepoint.TimePoint;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import static java.lang.String.format;
import java.math.BigDecimal;
import java.nio.charset.Charset;


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
    public void showPrediction(TimePoint timePoint, DataSetResult result, Prediction prediction) {
        log(format("%s prediction on %s for the next trade: %s, count up/dn: %s/%s, weight up/dn: %.2f/%.2f",
                result.getName().toString(),timePoint.toString(),prediction.toString(),
                result.getCountUp(),result.getCountDown(),result.getWeightUp(),result.getWeightDown()
        ));
    }
    
    @Override
    public void reportAccountOpening(BigDecimal balance) {
        log(format("Opening account with %s balance", balance.toPlainString()));
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
    public void reportClosingTrade(DataSetName name, BigDecimal quantity, BigDecimal price, BigDecimal profit, BigDecimal balance) {
        log(format("Closing %s trade. Quantity %s, price: %s, profit: %s, current balance: %s",
                name, scale(quantity), scale(price), scale(profit), scale(balance)));
    }

    @Override
    public void reportAccountClosing(BigDecimal balance) {
        log(format("Closing account with %s balance", scale(balance)));
    }

    @Override
    public void infoMessage(String message) {
        log(message);
    }

    @Override
    public void reportStartingTimePoint(TimePoint timePoint) {
        log(format("Starting time point %s", timePoint));
    }

    @Override
    public void reportFinishedTimePoint(TimePoint timePoint, BigDecimal equity) {
        log(format("Finished time point %s with account value %s", timePoint, scale(equity)));
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
