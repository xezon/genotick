package com.alphatica.genotick.genotick;

import com.alphatica.genotick.timepoint.TimePointExecutor;
import com.alphatica.genotick.ui.UserOutput;
import com.alphatica.genotick.breeder.RobotBreeder;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.killer.RobotKiller;
import com.alphatica.genotick.population.Population;

class EngineFactory {
    public static Engine getDefaultEngine(EngineSettings engineSettings,
                                          MainAppData data, TimePointExecutor timePointExecutor,
                                          RobotKiller killer,
                                          RobotBreeder breeder,
                                          Population population,
                                          MainInterface.SessionResult sessionResult,
                                          UserOutput output) {
        Engine engine = SimpleEngine.getInstance(output);
        engine.setSettings(engineSettings, timePointExecutor, data, killer, breeder, population, sessionResult);
        return engine;
    }
}
