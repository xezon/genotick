package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.DataSetName;

import java.util.ArrayList;
import java.util.List;

public class DataSetResult {
    private final DataSetName name;
    private double weightUp;
    private double weightOut;
    private int countOut;
    private double weightDown;
    private int countDown;
    private int countUp;

    public DataSetResult(DataSetName name) {
        this.name = name;
    }

    public void addResult(ProgramResult programResult) {
        Double weight = programResult.getWeight();
        if(weight.isNaN())
            return;
        processWeight(programResult);
    }

    private void processWeight(ProgramResult programResult) {
        if(programResult.getWeight() > 0) {
            switch (programResult.getPrediction()) {
                case UP: recordUp(programResult.getWeight()); break;
                case DOWN: recordDown(programResult.getWeight()); break;
                case OUT: recordOut(programResult.getWeight());
            }
        }
        if(programResult.getWeight() < 0) {
            switch (programResult.getPrediction()) {
                case UP: recordDown(-programResult.getWeight()); break;
                case DOWN: recordUp(-programResult.getWeight()); break;
                case OUT: recordOut(programResult.getWeight());
            }
        }
    }

    private void recordOut(double weight) {
        weightOut += weight;
        countOut++;
    }

    private void recordDown(double weight) {
        weightDown += weight;
        countDown++;
    }

    private void recordUp(double weight) {
        weightUp += weight;
        countUp++;
    }

    public Prediction getCumulativePrediction() {
        double direction = weightUp - weightDown;
        Debug.d("Up:",weightUp,countUp,"Down:",weightDown,countDown,"Out:",weightOut,countOut);
        return Prediction.getPrediction(direction);
    }

    public DataSetName getName() {
        return name;
    }

}
