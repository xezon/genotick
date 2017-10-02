package com.alphatica.genotick.genotick;

import com.alphatica.genotick.breeder.BreederSettings;
import com.alphatica.genotick.breeder.RobotBreeder;
import com.alphatica.genotick.breeder.RobotBreederFactory;
import com.alphatica.genotick.chart.GenoChartFactory;
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
import com.alphatica.genotick.ui.UserInputOutputFactory;

public class Simulation {

    @SuppressWarnings("WeakerAccess")
    public Simulation() {
    }

    public void start(MainSettings mainSettings, MainAppData data) throws IllegalAccessException {
        if(validateSettings(mainSettings)) {
            initRandomGenerator(mainSettings);
            initChart(mainSettings);
            initWeightCalculator(mainSettings);
            logSettings(mainSettings);
            RobotKiller killer = createRobotKiller(mainSettings);
            Mutator mutator = createMutator(mainSettings);
            RobotBreeder breeder = createRobotBreeder(mainSettings, mutator);
            Population population = createPopulation(mainSettings);
            Engine engine = createEngine(mainSettings, data, killer, breeder, population);
            engine.start();
        }
    }

    private boolean validateSettings(MainSettings settings) {
        try {
            settings.validate();
            return true;
        } catch(IllegalArgumentException ex) {
            ex.printStackTrace();
            UserInputOutputFactory.getUserOutput().errorMessage(ex.getMessage());
            return false;
        }
    }

    private void initRandomGenerator(MainSettings mainSettings) {
        RandomGenerator.suggestSeed(mainSettings.randomSeed);
    }
    
    private void initChart(MainSettings mainSettings) {
        GenoChartFactory.initialize(mainSettings.chartMode);
    }
    
    private void initWeightCalculator(MainSettings mainSettings) {
        WeightCalculator.setWeightMode(mainSettings.weightMode);
        WeightCalculator.setWeightExponent(mainSettings.weightExponent);
    }

    private Engine createEngine(MainSettings mainSettings, MainAppData data, RobotKiller killer,
                              RobotBreeder breeder, Population population) {
        EngineSettings engineSettings = new EngineSettings(mainSettings);
        TimePointExecutor timePointExecutor = createTimePointExecutor(mainSettings);
        return EngineFactory.getDefaultEngine(engineSettings, data, timePointExecutor, killer, breeder, population);
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
        Population population = PopulationFactory.getDefaultPopulation(populationSettings, dao);
        return population;
    }

    private RobotBreeder createRobotBreeder(MainSettings settings, Mutator mutator) {
        BreederSettings breederSettings = new BreederSettings(settings);
        return RobotBreederFactory.getDefaultBreeder(breederSettings, mutator);
    }

    private RobotKiller createRobotKiller(MainSettings settings) {
        RobotKillerSettings killerSettings = new RobotKillerSettings(settings);
        return RobotKillerFactory.getDefaultRobotKiller(killerSettings);
    }

    private Mutator createMutator(MainSettings settings) {
        MutatorSettings mutatorSettings = new MutatorSettings(settings);
        return MutatorFactory.getDefaultMutator(mutatorSettings);
    }

    private void logSettings(MainSettings settings) throws IllegalAccessException {
        String settingsString = settings.getString();
        UserInputOutputFactory.getUserOutput().infoMessage(settingsString);
    }

}
