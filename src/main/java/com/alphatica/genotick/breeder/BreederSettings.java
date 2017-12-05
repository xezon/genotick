package com.alphatica.genotick.breeder;

import com.alphatica.genotick.genotick.MainSettings;
import com.alphatica.genotick.genotick.WeightMode;

public class BreederSettings {
    public final int minimumOutcomesBetweenBreeding;
    public final int minimumOutcomesToAllowBreeding;
    public final WeightMode weightMode;
    public final double weightExponent;
    public final double inheritedWeightPercent;
    public final InheritedWeightMode inheritedWeightMode;
    public final double randomRobots;
    public final int maximumDataOffset;
    public final int ignoreColumns;
    public final int maximumRobotInstructions;
    public final int minimumRobotInstructions;
    public final long randomSeed;

    public BreederSettings(final MainSettings settings) {
        this.minimumOutcomesBetweenBreeding = settings.minimumOutcomesBetweenBreeding;
        this.minimumOutcomesToAllowBreeding = settings.minimumOutcomesToAllowBreeding;
        this.weightMode = settings.weightMode;
        this.weightExponent = settings.weightExponent;
        this.inheritedWeightPercent = settings.inheritedChildWeight;
        this.inheritedWeightMode = settings.inheritedChildWeightMode;
        this.randomRobots = settings.randomRobotsAtEachUpdate;
        this.maximumDataOffset = settings.maximumDataOffset;
        this.ignoreColumns = settings.ignoreColumns;
        this.maximumRobotInstructions = settings.maximumRobotInstructions;
        this.minimumRobotInstructions = settings.minimumRobotInstructions;
        this.randomSeed = settings.randomSeed;
    }
}
