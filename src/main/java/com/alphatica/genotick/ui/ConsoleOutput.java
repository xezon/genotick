package com.alphatica.genotick.ui;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.genotick.Debug;
import com.alphatica.genotick.genotick.Prediction;
import com.alphatica.genotick.timepoint.TimePoint;

class ConsoleOutput implements UserOutput {
    @Override
    public void errorMessage(String message) {
        Debug.d("Error:",message);
    }

    @Override
    public void warningMessage(String message) {
        Debug.d("Warning:", message);
    }

    @Override
    public void reportProfitForTimePoint(TimePoint timePoint, double cumulativeProfit, double timePointProfit) {
        System.out.println("Profit for " + timePoint.toString() + ": "
                + "Cumulative profit: " + cumulativeProfit + " "
                + "TimePoint's profit: " + timePointProfit);
    }

    @Override
    public void showPrediction(TimePoint timePoint, DataSetName name, Prediction prediction) {
        System.out.println(String.format("%s prediction on %s for the next trade: %s",
                name.toString(),timePoint.toString(),prediction.toString()));
    }

}
