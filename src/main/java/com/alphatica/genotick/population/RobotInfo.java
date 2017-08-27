package com.alphatica.genotick.population;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;

public class RobotInfo {
    public static final Comparator<RobotInfo> comparatorByAge = new AgeComparator();
    public static final Comparator<RobotInfo> comparatorByAbsoluteWeight = new AbsoluteWeightComparator();
    private static final DecimalFormat format = new DecimalFormat("0.00");
    private final RobotName name;
    private final double weight;
    private final long lastChildOutcomes;
    private final long totalChildren;
    private final long length;
    private final int totalPredictions;
    private final int totalOutcomes;

    private boolean isPredicting;

    private final int bias;
    @Override
    public String toString() {
        return name.toString() + ": Outcomes: " + String.valueOf(totalPredictions) + " weight: " + format.format(weight) +
                " bias: " + String.valueOf(bias) + " length: " + String.valueOf(length) +
                " totalChildren: " + String.valueOf(totalChildren);
    }

    public RobotName getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    public void setPredicting(boolean predicting) {
        isPredicting = predicting;
    }

    public int getTotalPredictions() {
        return totalPredictions;
    }

    public RobotInfo(Robot robot) {
        name = new RobotName(robot.getName().getName());
        weight = robot.getWeight();
        lastChildOutcomes = robot.getOutcomesAtLastChild();
        totalChildren = robot.getTotalChildren();
        length = robot.getLength();
        totalPredictions = robot.getTotalPredictions();
        totalOutcomes = robot.getTotalOutcomes();
        bias = robot.getBias();
        isPredicting = robot.isPredicting();
    }


    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canBeParent(long minimumParentAge, long timeBetweenChildren) {
        if(totalPredictions < minimumParentAge)
            return false;
        long outcomesSinceLastChild = totalPredictions - lastChildOutcomes;
        return outcomesSinceLastChild >= timeBetweenChildren;
    }

    public int getTotalOutcomes() {
        return totalOutcomes;
    }

    public int getBias() {
        return bias;
    }

    public boolean isPredicting() {
        return isPredicting;
    }
}
