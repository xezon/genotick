package com.alphatica.genotick.population;

public class PopulationSettings {
    public static final String DEFAULT_DATA_ACCESS = "";
    public static final int DEFAULT_DESIRED_SIZE = 1_000;
    private final int desiredSize;
    private final PopulationDaoOption daoOption;
    private final String daoPath;
    
    public PopulationSettings() {
        this(DEFAULT_DATA_ACCESS, DEFAULT_DESIRED_SIZE);
    }
    
    public PopulationSettings(String dataAccess, int desiredSize) {
        this.desiredSize = desiredSize;
        this.daoOption = PopulationDaoOption.getOption(dataAccess);
        this.daoPath = PopulationDaoOption.getPath(dataAccess);
    }
    
    public int desiredSize() {
        return desiredSize;
    }
    
    public PopulationDaoOption daoOption() {
        return daoOption;
    }
    
    public String daoPath() {
        return daoPath;
    }
}
