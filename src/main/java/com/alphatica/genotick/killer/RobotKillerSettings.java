package com.alphatica.genotick.killer;

import com.alphatica.genotick.genotick.MainSettings;

public class RobotKillerSettings {
    public final double maximumDeathByAge;
    public final double maximumDeathByWeight;
    public final double probabilityOfDeathByAge;
    public final double probabilityOfDeathByWeight;
    public final int protectRobotsUntilOutcomes;
    public final double protectBestRobots;
    public final boolean killNonPredictingRobots;
    public final boolean requireSymmetricalRobots;
    public final long randomSeed;
    
    public RobotKillerSettings(final MainSettings settings) {
        this.maximumDeathByAge = settings.maximumDeathByAge;
        this.maximumDeathByWeight = settings.maximumDeathByWeight;
        this.probabilityOfDeathByAge = settings.probabilityOfDeathByAge;
        this.probabilityOfDeathByWeight = settings.probabilityOfDeathByWeight;
        this.protectRobotsUntilOutcomes = settings.protectRobotsUntilOutcomes;
        this.protectBestRobots = settings.protectBestRobots;
        this.killNonPredictingRobots = settings.killNonPredictingRobots;
        this.requireSymmetricalRobots = settings.requireSymmetricalRobots;
        this.randomSeed = settings.randomSeed;
    }
}
