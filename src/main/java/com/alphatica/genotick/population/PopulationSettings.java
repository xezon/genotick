package com.alphatica.genotick.population;

import com.alphatica.genotick.genotick.MainSettings;

public class PopulationSettings {
    
    public final int desiredSize;
    public final PopulationDaoOption daoOption;
    public final String daoPath;
    public final long randomSeed;
    public boolean killNonPredictingRobots;
    public double minimumScoreToSaveToDisk;
    
    public PopulationSettings() {
        this(MainSettings.DEFAULT_DATA_ACCESS,
             MainSettings.DEFAULT_DESIRED_SIZE,
             MainSettings.DEFAULT_RANDOM_SEED,
             MainSettings.DEFAULT_KILL_NON_PREDICTING_ROBOTS,
             MainSettings.DEFAULT_MINIMUM_SCORE_TO_SAVE_TO_DISK);
    }
    
    public PopulationSettings(final MainSettings settings) {
        this(settings.populationDAO,
             settings.populationDesiredSize,
             settings.randomSeed,
             settings.killNonPredictingRobots,
             settings.minimumScoreToSaveToDisk);
    }
    
    public PopulationSettings(final String dataAccess, final int desiredSize, final long randomSeed, 
        final boolean killNonPredictingRobots, final double minimumScoreToSaveToDisk) {
        this.desiredSize = desiredSize;
        this.daoOption = PopulationDaoOption.getOption(dataAccess);
        this.daoPath = PopulationDaoOption.getPath(dataAccess);
        this.randomSeed = randomSeed;
        this.killNonPredictingRobots = killNonPredictingRobots;
        this.minimumScoreToSaveToDisk = minimumScoreToSaveToDisk;
    }
}
