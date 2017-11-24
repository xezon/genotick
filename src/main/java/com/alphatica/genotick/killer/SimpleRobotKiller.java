package com.alphatica.genotick.killer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alphatica.genotick.genotick.RandomGenerator;
import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.RobotInfo;
import com.alphatica.genotick.ui.UserOutput;

class SimpleRobotKiller implements RobotKiller {
    private RobotKillerSettings settings;
    private RandomGenerator random;
    private final UserOutput output;

    private SimpleRobotKiller(UserOutput output) {
        this.output = output;
    }

    static RobotKiller create(UserOutput output) {
        return new SimpleRobotKiller(output);
    }

    @Override
    public void setSettings(RobotKillerSettings killerSettings) {
        this.settings = killerSettings;
        this.random = RandomGenerator.create(killerSettings.randomSeed);
    }

    @Override
    public void killRobots(Population population, List<RobotInfo> robotInfos) {
        int before = population.getSize(), after;
        killNonPredictingRobots(population, robotInfos);
        after = population.getSize();
        output.debugMessage("killedByPrediction=" + (before - after));
        before = after;
        killNonSymmetricalRobots(population, robotInfos);
        after = population.getSize();
        output.debugMessage("killedBySymmetry=" + (before - after));
        List<RobotInfo> listCopy = new ArrayList<>(robotInfos);
        removeProtectedRobots(population,listCopy);
        output.debugMessage("protectedRobots=" + (population.getSize() - listCopy.size()));
        before = population.getSize();
        killRobotsByWeight(population, listCopy, robotInfos);
        after = population.getSize();
        output.debugMessage("killedByWeight=" + (before - after));
        before = after;
        killRobotsByAge(population, listCopy, robotInfos);
        after = population.getSize();
        output.debugMessage("killedByAge=" + (before - after));
    }

    private void killNonSymmetricalRobots(Population population, List<RobotInfo> robotInfos) {
        if(settings.requireSymmetricalRobots) {
            for(int i = robotInfos.size() - 1; i >= 0; i--) {
                RobotInfo info = robotInfos.get(i);
                if(info.getBias() != 0) {
                    robotInfos.remove(i);
                    population.removeRobot(info.getName());
                }
            }
        }
    }

    private void killNonPredictingRobots(Population population, List<RobotInfo> robotInfos) {
        if(settings.killNonPredictingRobots) {
            for(int i = robotInfos.size() - 1; i >= 0; i--) {
                RobotInfo info = robotInfos.get(i);
                if(!info.isPredicting()) {
                    robotInfos.remove(i);
                    population.removeRobot(info.getName());
                }
            }
        }
    }

    private void removeProtectedRobots(Population population, List<RobotInfo> robotInfos) {
        protectUntilOutcomes(robotInfos);
        protectBest(population, robotInfos);
    }

    private void protectBest(Population population, List<RobotInfo> robotInfos) {
        if(settings.protectBestRobots > 0) {
            robotInfos.sort(RobotInfo.comparatorByAbsoluteWeight);
            int i = (int)Math.round(settings.protectBestRobots * population.getDesiredSize());
            while(i-- > 0) {
                RobotInfo robotInfo = getLastFromList(robotInfos);
                if(robotInfo == null)
                    break;
            }
        }
    }

    private void protectUntilOutcomes(List<RobotInfo> robotInfos) {
        for(int i = robotInfos.size()-1; i >= 0; i--) {
            RobotInfo robotInfo = robotInfos.get(i);
            if(robotInfo.getTotalOutcomes() < settings.protectRobotsUntilOutcomes)
                robotInfos.remove(i);
        }
    }

    private void killRobotsByAge(Population population, List<RobotInfo> listCopy, List<RobotInfo> originalList) {
        listCopy.sort(RobotInfo.comparatorByAge);
        int numberToKill = (int)Math.round(settings.maximumDeathByAge * originalList.size());
        killRobots(listCopy,originalList,numberToKill,population,settings.probabilityOfDeathByAge);
    }

    private void killRobotsByWeight(Population population, List<RobotInfo> listCopy, List<RobotInfo> originalList) {
        listCopy.sort(RobotInfo.comparatorByAbsoluteWeight);
        Collections.reverse(listCopy);

        int numberToKill = (int) Math.round(settings.maximumDeathByWeight * originalList.size());
        killRobots(listCopy,originalList,numberToKill,population,settings.probabilityOfDeathByWeight);
    }

    private int killRobots(List<RobotInfo> listCopy, List<RobotInfo> originalList, int numberToKill, Population population, double probability) {
        int numberKilled = 0;
        while(numberToKill-- > 0) {
            RobotInfo toKill = getLastFromList(listCopy);
            if(toKill == null)
                return numberKilled;
            if(random.nextDouble() < probability) {
                population.removeRobot(toKill.getName());
                originalList.remove(toKill);
                numberKilled++;
            }
        }
        return numberKilled;
    }

    private RobotInfo getLastFromList(List<RobotInfo> robotInfos) {
        int size = robotInfos.size();
        if(size == 0)
            return null;
        return robotInfos.remove(size-1);
    }
}
