package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.DataSetName;

import static com.alphatica.genotick.utility.Assert.gassert;

public class DataSetResult {
    private final DataSetName name;
    private double weightUp;
    private double weightDown;
    private int countUp;
    private int countDown;

    public DataSetResult(DataSetName name) {
        this.name = name;
        this.weightUp = 0.0;
        this.weightDown = 0.0;
        this.countUp = 0;
        this.countDown = 0;
    }
    
    public DataSetName getName() {
        return name;
    }
    
    public double getWeightDown() {
        return weightDown;
    }
    
    public double getWeightUp() {
        return weightUp;
    }
    
    public int getCountDown() {
        return countDown;
    }
    
    public int getCountUp() {
        return countUp;
    }

    public void addResult(RobotResult robotResult) {
        double weight = robotResult.getWeight();
        if (weight > 0.0) {
            switch (robotResult.getPrediction()) {
                case UP: recordUp(robotResult.getWeight()); break;
                case DOWN: recordDown(robotResult.getWeight()); break;
                case OUT: /*does nothing*/ break;
            }
        }
        else if (weight < 0.0) {
            switch (robotResult.getPrediction()) {
                case UP: recordDown(-robotResult.getWeight()); break;
                case DOWN: recordUp(-robotResult.getWeight()); break;
                case OUT: /*does nothing*/ break;
            }
        }
    }


    private void recordDown(double weight) {
        weightDown += weight;
        countDown++;
    }

    private void recordUp(double weight) {
        weightUp += weight;
        countUp++;
    }

    Prediction getCumulativePrediction(double threshold) {
        gassert(threshold >= 1.0);
        final double factor = 1.0 / threshold;
        if (weightUp * factor > weightDown) {
            return Prediction.UP;
        }
        else if (weightDown * factor > weightUp) {
            return Prediction.DOWN;
        }
        else {
            return Prediction.OUT;
        }
    }
}
