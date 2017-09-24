package com.alphatica.genotick.breeder;

public class BreederSettings {
    public final int minimumOutcomesBetweenBreeding;
    public final int minimumOutcomesToAllowBreeding;
    public final double inheritedWeightPercent;
    public final InheritedWeightMode inheritedWeightMode;
    public final double randomRobots;
    public final int dataMaximumOffset;
    public final int ignoreColumns;
    public final int robotInstructionLimit;
    public final int robotInstructionMinCount;

    public BreederSettings(
            int minimumOutcomesBetweenBreeding,
            int minimumOutcomesToAllowBreeding,
            double inheritedWeightPercent,
            InheritedWeightMode inheritedWeightMode,
            double randomRobots,
            int dataMaximumOffset,
            int ignoreColumns,
            int robotInstructionLimit,
            int robotInstructionMinCount) {
        this.minimumOutcomesBetweenBreeding = minimumOutcomesBetweenBreeding;
        this.minimumOutcomesToAllowBreeding = minimumOutcomesToAllowBreeding;
        this.inheritedWeightPercent = inheritedWeightPercent;
        this.inheritedWeightMode = inheritedWeightMode;
        this.randomRobots = randomRobots;
        this.dataMaximumOffset = dataMaximumOffset;
        this.ignoreColumns = ignoreColumns;
        this.robotInstructionLimit = robotInstructionLimit;
        this.robotInstructionMinCount = robotInstructionMinCount;
    }
}
