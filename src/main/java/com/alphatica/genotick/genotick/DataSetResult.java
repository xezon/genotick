package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.DataSetName;

import java.util.ArrayList;
import java.util.List;

public class DataSetResult {
    private static double threshold = 1;

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

    public static void setThreshold(double threshold) {
        DataSetResult.threshold = threshold;
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
        Debug.d("Before threshold: Up:",weightUp,countUp,"Down:",weightDown,countDown,"Out:",weightOut,countOut,
                "Direction:",direction);
        double directionAfterThreshold = applyThreshold(direction);
        if(direction * directionAfterThreshold < 0) {
            Debug.d("Not enough to pass threshold (",threshold,"): Up:",weightUp,"Down:",weightDown);
            return Prediction.OUT;
        }
        Debug.d("After threshold: Up:",weightUp,countUp,"Down:",weightDown,countDown,"Out:",weightOut,countOut,
                "Direction",directionAfterThreshold);
        return Prediction.getPrediction(directionAfterThreshold);
    }

    private double applyThreshold(double direction) {
        if(threshold == 1) {
            return direction;
        }
        double localWeightUp = weightUp;
        double localWeightDown = weightDown;
        if(direction > 0) {
            localWeightUp /= threshold;
        }
        if(direction < 0) {
            localWeightDown /= threshold;
        }
        return localWeightUp - localWeightDown;
    }

    public DataSetName getName() {
        return name;
    }

}
