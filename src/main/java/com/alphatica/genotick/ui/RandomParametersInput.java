package com.alphatica.genotick.ui;

import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.genotick.MainSettings;
import com.alphatica.genotick.genotick.RandomGenerator;
import com.alphatica.genotick.genotick.WeightMode;
import com.alphatica.genotick.timepoint.TimePoint;
import com.alphatica.genotick.breeder.InheritedWeightMode;
import com.alphatica.genotick.chart.GenoChartMode;

class RandomParametersInput extends BasicUserInput {

    private RandomGenerator random;
    
    RandomParametersInput(UserOutput output) {
        super(output);
    }

    @Override
    public MainSettings getSettings() {
        MainSettings settings = getMainSettings();
        if (settings == null) {
            MainSettings defaults = MainSettings.getSettings();
            if (random == null) {
                random = RandomGenerator.create(defaults.randomSeed);
            }
            defaults.populationDAO = "";
            defaults.requireSymmetricalRobots = true;
            defaults.killNonPredictingRobots = true;
            defaults.performTraining = true;
            defaults.chartMode = GenoChartMode.NONE;
            MainAppData data = getData(defaults.dataDirectory);
            assignTimePoints(defaults, data);
            settings = assignRandom(defaults);
            setMainSettings(settings);
        }
        return settings;
    }

    private void assignTimePoints(MainSettings defaults, MainAppData data) {
        TimePoint first = data.getFirstTimePoint();
        TimePoint last = data.getLastTimePoint();
        long diff = last.getValue() - first.getValue();
        long count = Math.abs(random.nextLong() % diff);
        defaults.startTimePoint = new TimePoint(last.getValue() - count);
        defaults.endTimePoint = last;
    }
    
    private <E extends Enum<E>> E nextEnum(Class<E> enumType) {
        final E[] enumValues = enumType.getEnumConstants();
        final int index = random.nextInt(enumValues.length);
        return enumValues[index];
    }

    private MainSettings assignRandom(MainSettings settings) {
        settings.maximumDataOffset = random.nextInt(256) + 1;
        settings.minimumRobotInstructions = random.nextInt(256) + 16;
        settings.maximumRobotInstructions = random.nextInt(1024) + settings.minimumRobotInstructions;
        settings.maximumProcessorInstructionFactor = random.nextInt(256) + 1;
        settings.maximumDeathByAge = random.nextDouble();
        settings.maximumDeathByWeight = random.nextDouble();
        settings.probabilityOfDeathByAge = random.nextDouble();
        settings.probabilityOfDeathByWeight = random.nextDouble();
        settings.weightMode = nextEnum(WeightMode.class);
        settings.weightExponent = random.nextDouble() + 1.0;
        settings.inheritedChildWeight = random.nextDouble();
        settings.inheritedChildWeightMode = nextEnum(InheritedWeightMode.class);
        settings.protectRobotsUntilOutcomes = random.nextInt(100);
        settings.protectBestRobots = random.nextDouble();
        settings.newInstructionProbability = random.nextDouble();
        settings.instructionMutationProbability = random.nextDouble();
        settings.skipInstructionProbability = settings.newInstructionProbability;
        settings.minimumOutcomesToAllowBreeding = random.nextInt(50);
        settings.minimumOutcomesBetweenBreeding = random.nextInt(50);
        settings.randomRobotsAtEachUpdate = random.nextDouble();
        settings.resultThreshold = 1 + (random.nextDouble() * 9);
        return settings;
    }
}
