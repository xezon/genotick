package com.alphatica.genotick.ui;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.genotick.DataSetResult;
import com.alphatica.genotick.genotick.Prediction;
import com.alphatica.genotick.timepoint.TimePoint;

import java.math.BigDecimal;

public interface UserOutput {
	
    String getOutDir();
    
    void errorMessage(String message);

    void warningMessage(String message);
    
    void debugMessage(String message);

    void showPrediction(TimePoint timePoint, DataSetResult result, Prediction prediction);

    void reportAccountOpening(BigDecimal balance);

    void reportPendingTrade(DataSetName name, Prediction prediction);

    void reportOpeningTrade(DataSetName name, BigDecimal quantity, Double price);

    void reportClosingTrade(DataSetName name, BigDecimal quantity, BigDecimal price, BigDecimal profit, BigDecimal balance);

    void reportAccountClosing(BigDecimal balance);

    void infoMessage(String message);

    void reportStartedTimePoint(TimePoint timePoint);

    void reportFinishedTimePoint(TimePoint timePoint, BigDecimal equity);
}
