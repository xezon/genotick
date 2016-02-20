package com.alphatica.genotick.breeder;

public class BreederSettings {
    public final long outcomesBetweenBreeding;
    public final double inheritedWeightPercent;
    public final long minimumOutcomesToAllowBreeding;
    public final double randomRobots;
    public final int dataMaximumOffset;

    public BreederSettings(long timeBetweenChildren, double inheritedWeightPercent, long minimumParentAge, double randomRobots, int dataMaximumOffset) {
        this.outcomesBetweenBreeding = timeBetweenChildren;
        this.inheritedWeightPercent = inheritedWeightPercent;
        this.minimumOutcomesToAllowBreeding = minimumParentAge;
        this.randomRobots = randomRobots;
        this.dataMaximumOffset = dataMaximumOffset;
    }
}
