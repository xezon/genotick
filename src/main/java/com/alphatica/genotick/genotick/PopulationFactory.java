package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.PopulationDAO;
import com.alphatica.genotick.population.PopulationDaoOption;
import com.alphatica.genotick.population.PopulationSettings;
import com.alphatica.genotick.population.SimplePopulation;

class PopulationFactory {

    public static Population getDefaultPopulation(PopulationDAO dao) {
        PopulationSettings settings = new PopulationSettings();
        return getDefaultPopulation(settings, dao);
    }
    
    public static Population getDefaultPopulation(PopulationSettings settings, PopulationDAO dao) {
        Population population = new SimplePopulation();
        population.setSettings(settings);
        population.setDao(dao);
        if(settings.daoOption == PopulationDaoOption.EXPLICIT_RAM) {
            population.loadFromFolder(settings.daoPath);
        }
        return population;
    }
}
