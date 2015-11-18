package com.alphatica.genotick.killer;

import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.ProgramInfo;

import java.util.List;

public interface ProgramKiller {
    void killPrograms(Population population, List<ProgramInfo> programInfos);

    void setSettings(ProgramKillerSettings killerSettings);
}
