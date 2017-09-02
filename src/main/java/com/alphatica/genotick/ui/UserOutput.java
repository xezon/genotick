package com.alphatica.genotick.ui;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.genotick.Prediction;
import com.alphatica.genotick.timepoint.TimePoint;

import java.math.BigDecimal;

public interface UserOutput {
	
    void errorMessage(String message);

    void warningMessage(String message);
    
    void debugMessage(String message);

    void reportProfitForTimePoint(TimePoint timePoint, double cumulativeProfit, double timePointProfit);

    void showPrediction(TimePoint timePoint, DataSetName name, Prediction prediction);

    void reportAccountOpening(BigDecimal cash);

    void reportPendingTrade(DataSetName name, Prediction prediction);

    void reportOpeningTrade(BigDecimal cashPerTrade, DataSetName name, Prediction prediction, Double price);

    void reportClosingTrade(DataSetName name, BigDecimal quantity, BigDecimal price, BigDecimal profit, BigDecimal cash);

    void reportAccountClosing(BigDecimal cash);

    void infoMessage(String s);
}
