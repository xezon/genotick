package com.alphatica.genotick.breeder;

public class BreederSettings {
    public final int minimumOutcomesBetweenBreeding;
    public final int minimumOutcomesToAllowBreeding;
    public final double inheritedWeightPercent;
    public final InheritedWeightMode inheritedWeightMode;
    public final double randomRobots;
    public final int dataMaximumOffset;
    public final int ignoreColumns;

    public BreederSettings(
            int minimumOutcomesBetweenBreeding,
            int minimumOutcomesToAllowBreeding,
            double inheritedWeightPercent,
            InheritedWeightMode inheritedWeightMode,
            double randomRobots,
            int dataMaximumOffset,
            int ignoreColumns) {
        this.minimumOutcomesBetweenBreeding = minimumOutcomesBetweenBreeding;
        this.minimumOutcomesToAllowBreeding = minimumOutcomesToAllowBreeding;
        this.inheritedWeightPercent = inheritedWeightPercent;
        this.inheritedWeightMode = inheritedWeightMode;
        this.randomRobots = randomRobots;
        this.dataMaximumOffset = dataMaximumOffset;
        this.ignoreColumns = ignoreColumns;
    }
}
