package com.alphatica.genotick.genotick;

import com.alphatica.genotick.breeder.BreederSettings;
import com.alphatica.genotick.breeder.RobotBreeder;
import com.alphatica.genotick.breeder.RobotBreederFactory;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.killer.RobotKiller;
import com.alphatica.genotick.killer.RobotKillerFactory;
import com.alphatica.genotick.killer.RobotKillerSettings;
import com.alphatica.genotick.mutator.Mutator;
import com.alphatica.genotick.mutator.MutatorFactory;
import com.alphatica.genotick.mutator.MutatorSettings;
import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.PopulationDAO;
import com.alphatica.genotick.population.PopulationDAOFactory;
import com.alphatica.genotick.population.PopulationSettings;
import com.alphatica.genotick.population.RobotExecutorSettings;
import com.alphatica.genotick.processor.RobotExecutorFactory;
import com.alphatica.genotick.timepoint.TimePointExecutor;
import com.alphatica.genotick.timepoint.TimePointExecutorFactory;
import com.alphatica.genotick.ui.UserOutput;
import com.alphatica.genotick.utility.TimeCounter;
import com.alphatica.genotick.utility.Tools;

import static java.lang.String.format;
import java.util.concurrent.TimeUnit;

public class Simulation {

    private final UserOutput output;
    
    @SuppressWarnings("WeakerAccess")
    public Simulation(UserOutput output) {
        this.output = output;
    }

    public void start(MainSettings mainSettings, MainAppData data, MainInterface.SessionResult sessionResult, int simulationIteration) throws IllegalAccessException {
        output.setIdentifier(Tools.getProcessThreadIdString() + "_" + simulationIteration);
        TimeCounter simulationRunTime = new TimeCounter("Simulation Run Time", false);
        if(validateSettings(mainSettings)) {
            logSettings(mainSettings);
            RobotKiller killer = createRobotKiller(mainSettings);
            Mutator mutator = createMutator(mainSettings);
            RobotBreeder breeder = createRobotBreeder(mainSettings, mutator);
            Population population = createPopulation(mainSettings);
            Engine engine = createEngine(mainSettings, data, killer, breeder, population, sessionResult);
            engine.start();
        }
        System.out.println(format("Simulation %s finished in %d seconds", output.getIdentifier(), simulationRunTime.stop(TimeUnit.SECONDS)));
    }

    private boolean validateSettings(MainSettings settings) {
        try {
            settings.validate();
            return true;
        } catch(IllegalArgumentException ex) {
            ex.printStackTrace();
            output.errorMessage(ex.getMessage());
            return false;
        }
    }

    private Engine createEngine(MainSettings mainSettings, MainAppData data, RobotKiller killer,
                                RobotBreeder breeder, Population population, MainInterface.SessionResult sessionResult) {
        EngineSettings engineSettings = new EngineSettings(mainSettings);
        TimePointExecutor timePointExecutor = createTimePointExecutor(mainSettings);
        return EngineFactory.getDefaultEngine(engineSettings, data, timePointExecutor, killer, breeder, population, sessionResult, output);
    }

    private TimePointExecutor createTimePointExecutor(MainSettings settings) {
        DataSetExecutor dataSetExecutor = new SimpleDataSetExecutor();
        RobotExecutorSettings robotExecutorSettings = new RobotExecutorSettings(settings);
        RobotExecutorFactory robotExecutorFactory = new RobotExecutorFactory(robotExecutorSettings);
        return TimePointExecutorFactory.getDefaultExecutor(dataSetExecutor, robotExecutorFactory);
    }

    private Population createPopulation(MainSettings settings) {
        PopulationSettings populationSettings = new PopulationSettings(settings);
        PopulationDAO dao = PopulationDAOFactory.getDefaultDAO(populationSettings);
        return PopulationFactory.getDefaultPopulation(populationSettings, dao);
    }

    private RobotBreeder createRobotBreeder(MainSettings settings, Mutator mutator) {
        BreederSettings breederSettings = new BreederSettings(settings);
        return RobotBreederFactory.getDefaultBreeder(breederSettings, mutator, output);
    }

    private RobotKiller createRobotKiller(MainSettings settings) {
        RobotKillerSettings killerSettings = new RobotKillerSettings(settings);
        return RobotKillerFactory.getDefaultRobotKiller(killerSettings, output);
    }

    private Mutator createMutator(MainSettings settings) {
        MutatorSettings mutatorSettings = new MutatorSettings(settings);
        return MutatorFactory.getDefaultMutator(mutatorSettings);
    }

    private void logSettings(MainSettings settings) throws IllegalAccessException {
        String settingsString = settings.getString();
        output.infoMessage(settingsString);
    }

}
