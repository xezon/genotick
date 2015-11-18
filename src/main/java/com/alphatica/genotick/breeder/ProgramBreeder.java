package com.alphatica.genotick.breeder;

import com.alphatica.genotick.mutator.Mutator;
import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.ProgramInfo;

import java.util.List;

public interface ProgramBreeder {

    void breedPopulation(Population population, List<ProgramInfo> programInfos);

    void setSettings(BreederSettings breederSettings, Mutator mutator);

}
