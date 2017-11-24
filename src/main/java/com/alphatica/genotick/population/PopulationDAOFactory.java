package com.alphatica.genotick.population;

import com.alphatica.genotick.genotick.RandomGenerator;

public class PopulationDAOFactory {    
    
    public static PopulationDAO getDefaultDAO(PopulationSettings settings) {
        if (settings.daoOption.contains(PopulationDaoOption.RAM)) {
            RandomGenerator random = RandomGenerator.create(settings.randomSeed);
            return new PopulationDAORAM(random);
        }
        else if (settings.daoOption.contains(PopulationDaoOption.DISK)) {
            RandomGenerator random = RandomGenerator.create(settings.randomSeed);
            return new PopulationDAOFileSystem(random, settings.daoPath);
        }
        else {
            throw new DAOException("Unhandled PopulationDaoOption");
        }
    }
}
