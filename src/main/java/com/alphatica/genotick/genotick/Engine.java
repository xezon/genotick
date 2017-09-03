package com.alphatica.genotick.genotick;

import com.alphatica.genotick.breeder.RobotBreeder;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.killer.RobotKiller;
import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.timepoint.TimePointExecutor;

public interface Engine {
    void start();

    void setSettings(EngineSettings engineSettings, TimePointExecutor timePointExecutor, MainAppData data, RobotKiller killer, RobotBreeder breeder, Population population);
}
