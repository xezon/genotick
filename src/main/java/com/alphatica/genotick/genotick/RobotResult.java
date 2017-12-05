package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.population.Robot;
import com.alphatica.genotick.population.RobotName;

public class RobotResult {

    private final RobotName robotName;
    private final DataSetName dataSetName;
    private final Prediction prediction;
    private final double weight;

    public RobotResult(Prediction prediction, Robot robot, RobotData data) {
        this.robotName = robot.getName();
        this.dataSetName = data.getName();
        this.prediction = prediction;
        this.weight = robot.getWeight();
    }

    @Override
    public String toString() {
        return "RobotName: " + robotName.toString() + " DataSet: " + dataSetName.getName()
            + " Prediction: " + prediction.toString() + " Weight: " + String.valueOf(weight);
    }

    public RobotName getName() {
        return robotName;
    }
    
    public DataSetName getDataSetName() {
        return dataSetName;
    }

    public Prediction getPrediction() {
        return prediction;
    }

    public double getWeight() {
        return weight;
    }
}
