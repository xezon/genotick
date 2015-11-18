package com.alphatica.genotick.genotick;

import com.alphatica.genotick.timepointexecutor.TimePointExecutor;
import com.alphatica.genotick.timepointexecutor.TimePointExecutorFactory;
import com.alphatica.genotick.timepointexecutor.TimePointStats;
import com.alphatica.genotick.breeder.BreederSettings;
import com.alphatica.genotick.breeder.ProgramBreeder;
import com.alphatica.genotick.breeder.ProgramBreederFactory;
import com.alphatica.genotick.data.DataFactory;
import com.alphatica.genotick.data.DataLoader;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.killer.ProgramKiller;
import com.alphatica.genotick.killer.ProgramKillerFactory;
import com.alphatica.genotick.killer.ProgramKillerSettings;
import com.alphatica.genotick.mutator.Mutator;
import com.alphatica.genotick.mutator.MutatorFactory;
import com.alphatica.genotick.mutator.MutatorSettings;
import com.alphatica.genotick.population.*;
import com.alphatica.genotick.processor.ProgramExecutorFactory;
import com.alphatica.genotick.ui.UserOutput;

import java.util.List;

public class Application {
    private final UserOutput output;

    public Application(UserOutput output) {
        this.output = output;
    }

    public void start(MainSettings mainSettings, MainAppData data) {
        if(!validateSettings(mainSettings))
            return;
        logSettings(mainSettings);
        ProgramKiller killer = getProgramKiller(mainSettings);
        Mutator mutator = getMutator(mainSettings);
        ProgramBreeder breeder = wireProgramBreeder(mainSettings, mutator);
        Population population = wirePopulation(mainSettings);
        Engine engine = wireEngine(mainSettings, data, killer, breeder, population);
        List<TimePointStats> results = engine.start();
        showSummary(results);
    }

    public MainAppData createData(String dataSettings) {
        DataLoader dataLoader = DataFactory.getDefaultLoader(dataSettings);
        return dataLoader.createProgramData();
    }

    private boolean validateSettings(MainSettings settings) {
        try {
            settings.validate();
            return true;
        } catch(IllegalArgumentException ex) {
            output.errorMessage(ex.getMessage());
            return false;
        }
    }

    private Engine wireEngine(MainSettings mainSettings, MainAppData data, ProgramKiller killer,
                              ProgramBreeder breeder, Population population) {
        EngineSettings engineSettings = getEngineSettings(mainSettings);
        TimePointExecutor timePointExecutor = wireTimePointExecutor(mainSettings);
        return EngineFactory.getDefaultEngine(engineSettings, data, timePointExecutor, killer, breeder, population);
    }

    private TimePointExecutor wireTimePointExecutor(MainSettings settings) {
        DataSetExecutor dataSetExecutor = new SimpleDataSetExecutor();
        ProgramExecutorSettings programExecutorSettings = new ProgramExecutorSettings(settings);
        ProgramExecutorFactory programExecutorFactory = new ProgramExecutorFactory(programExecutorSettings);
        return TimePointExecutorFactory.getDefaultExecutor(dataSetExecutor,programExecutorFactory);
    }

    private Population wirePopulation(MainSettings settings) {
        PopulationDAO dao = PopulationDAOFactory.getDefaultDAO(settings.populationDAO);
        Population population = PopulationFactory.getDefaultPopulation(dao);
        population.setDesiredSize(settings.populationDesiredSize);
        return population;
    }

    private ProgramBreeder wireProgramBreeder(MainSettings settings, Mutator mutator) {
        BreederSettings breederSettings = new BreederSettings(
                settings.minimumOutcomesBetweenBreeding,
                settings.inheritedChildWeight,
                settings.minimumOutcomesToAllowBreeding,
                settings.randomProgramsAtEachUpdate,
                settings.dataMaximumOffset);
        return ProgramBreederFactory.getDefaultBreeder(breederSettings, mutator);
    }

    private ProgramKiller getProgramKiller(MainSettings settings) {
        ProgramKillerSettings killerSettings = new ProgramKillerSettings();
        killerSettings.maximumDeathByAge = settings.maximumDeathByAge;
        killerSettings.maximumDeathByWeight = settings.maximumDeathByWeight;
        killerSettings.probabilityOfDeathByAge = settings.probabilityOfDeathByAge;
        killerSettings.probabilityOfDeathByWeight = settings.probabilityOfDeathByWeight;
        killerSettings.protectProgramUntilOutcomes = settings.protectProgramUntilOutcomes;
        killerSettings.protectBestPrograms = settings.protectBestPrograms;
        killerSettings.killNonPredictingPrograms = settings.killNonPredictingPrograms;
        killerSettings.requireSymmetricalPrograms = settings.requireSymmetricalPrograms;
        return ProgramKillerFactory.getDefaultProgramKiller(killerSettings);
    }

    private Mutator getMutator(MainSettings settings) {
        MutatorSettings mutatorSettings = new MutatorSettings(
                settings.instructionMutationProbability,
                settings.newInstructionProbability,
                settings.skipInstructionProbability);
        return MutatorFactory.getDefaultMutator(mutatorSettings);
    }


    private EngineSettings getEngineSettings(MainSettings settings) {
        EngineSettings engineSettings = new EngineSettings();
        engineSettings.startTimePoint = settings.startTimePoint;
        engineSettings.endTimePoint = settings.endTimePoint;
        engineSettings.executionOnly = settings.executionOnly;
        return engineSettings;
    }

    private void logSettings(MainSettings settings) {
        String settingsString = settings.getString();
        Debug.d(settingsString);
    }

    private void showSummary(List<TimePointStats> list) {
        double result = 1;
        for (TimePointStats stats : list) {
            double percent = stats.getPercentEarned();
            result *= percent / 100.0 + 1;
        }
        Debug.d("Final result for genotic:", result);
    }

}
