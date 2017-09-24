package com.alphatica.genotick.breeder;

public class BreederSettings {
    public final int minimumOutcomesBetweenBreeding;
    public final int minimumOutcomesToAllowBreeding;
    public final double inheritedWeightPercent;
    public final InheritedWeightMode inheritedWeightMode;
    public final double randomRobots;
    public final int maximumDataOffset;
    public final int ignoreColumns;
    public final int maximumRobotInstructions;
    public final int minimumRobotInstructions;

    public BreederSettings(
            int minimumOutcomesBetweenBreeding,
            int minimumOutcomesToAllowBreeding,
            double inheritedWeightPercent,
            InheritedWeightMode inheritedWeightMode,
            double randomRobots,
            int maximumDataOffset,
            int ignoreColumns,
            int maximumRobotInstructions,
            int minimumRobotInstructions) {
        this.minimumOutcomesBetweenBreeding = minimumOutcomesBetweenBreeding;
        this.minimumOutcomesToAllowBreeding = minimumOutcomesToAllowBreeding;
        this.inheritedWeightPercent = inheritedWeightPercent;
        this.inheritedWeightMode = inheritedWeightMode;
        this.randomRobots = randomRobots;
        this.maximumDataOffset = maximumDataOffset;
        this.ignoreColumns = ignoreColumns;
        this.maximumRobotInstructions = maximumRobotInstructions;
        this.minimumRobotInstructions = minimumRobotInstructions;
    }
}
