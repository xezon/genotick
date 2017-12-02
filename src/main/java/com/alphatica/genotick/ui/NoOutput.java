package com.alphatica.genotick.ui;

import java.math.BigDecimal;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.genotick.DataSetResult;
import com.alphatica.genotick.genotick.Prediction;
import com.alphatica.genotick.timepoint.TimePoint;
import com.alphatica.genotick.utility.Tools;

public class NoOutput implements UserOutput {

    private final String outdir;
    private String identifier;

    public NoOutput(String outdir) {
        this.outdir = outdir;
        this.identifier = Tools.getProcessThreadIdString();
    }

    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    @Override
    public String getIdentifier() {
        return identifier;
    }
    
    @Override
    public String getOutDir() {
        return this.outdir;
    }

    @Override
    public void errorMessage(String message) {
    }

    @Override
    public void warningMessage(String message) {
    }

    @Override
    public void showPrediction(TimePoint timePoint, DataSetResult result, Prediction prediction) {
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
    }

    @Override
    public void debugMessage(String message) {
    }

    @Override
    public void infoMessage(String message) {
    }
}
