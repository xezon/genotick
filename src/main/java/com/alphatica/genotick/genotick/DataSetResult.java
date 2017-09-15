package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.DataSetName;

public class DataSetResult {
    private final DataSetName name;
    private double weightUp;
    private double weightDown;

    public DataSetResult(DataSetName name) {
        this.name = name;
        this.weightUp = 0.0;
        this.weightDown = 0.0;
    }
    
    public DataSetName getName() {
        return name;
    }

    public void addResult(RobotResult robotResult) {
        double weight = robotResult.getWeight();
        if (weight > 0.0) {
            switch (robotResult.getPrediction()) {
                case UP: recordUp(robotResult.getWeight()); break;
                case DOWN: recordDown(robotResult.getWeight()); break;
                case OUT: /* does nothing */ break;
            }
        }
        else if (weight < 0.0) {
            switch (robotResult.getPrediction()) {
                case UP: recordDown(-robotResult.getWeight()); break;
                case DOWN: recordUp(-robotResult.getWeight()); break;
                case OUT: /* does nothing */ break;
            }
        }
    }

    private void recordDown(double weight) {
        weightDown += weight;
    }

    private void recordUp(double weight) {
        weightUp += weight;
    }
    
    Prediction getCumulativePrediction(double threshold) {
        assert(threshold >= 1.0);
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
