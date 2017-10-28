package com.alphatica.genotick.population;

import com.alphatica.genotick.genotick.MainSettings;

public class PopulationSettings {
    
    public final int desiredSize;
    public final PopulationDaoOption daoOption;
    public final String daoPath;
    public final long randomSeed;
    
    public PopulationSettings() {
        this(MainSettings.DEFAULT_DATA_ACCESS,
             MainSettings.DEFAULT_DESIRED_SIZE,
             MainSettings.DEFAULT_RANDOM_SEED);
    }
    
    public PopulationSettings(final MainSettings settings) {
        this(settings.populationDAO,
             settings.populationDesiredSize,
             settings.randomSeed);
    }
    
    public PopulationSettings(final String dataAccess, final int desiredSize, final long randomSeed) {
        this.desiredSize = desiredSize;
        this.daoOption = PopulationDaoOption.getOption(dataAccess);
        this.daoPath = PopulationDaoOption.getPath(dataAccess);
        this.randomSeed = randomSeed;
    }
}
