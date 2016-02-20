package com.alphatica.genotick.killer;

import com.alphatica.genotick.genotick.Debug;
import com.alphatica.genotick.genotick.Main;
import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.RobotInfo;

import java.util.*;


class SimpleRobotKiller implements RobotKiller {
    private RobotKillerSettings settings;
    private final Random random;

    public static RobotKiller getInstance() {
        return new SimpleRobotKiller();
    }
    private SimpleRobotKiller() {
        random = Main.random;
    }

    @Override
    public void killRobots(Population population, List<RobotInfo> robotsInfos) {
        killNonPredictingRobots(population, robotsInfos);
        killNonSymmetricalRobots(population, robotsInfos);
        List<RobotInfo> listCopy = new ArrayList<>(robotsInfos);
        removeProtectedRobots(population,listCopy);
        killRobotsByWeight(population, listCopy, robotsInfos);
        killRobotsByAge(population, listCopy, robotsInfos);
        Debug.d("Population average weight:", RobotInfo.getAverageWeight(robotsInfos));
    }

    private void killNonSymmetricalRobots(Population population, List<RobotInfo> list) {
        if(!settings.requireSymmetricalRobots)
            return;
        int numberKilled = 0;
        Debug.d("Killing non symmetrical robots");
        for(int i = list.size() - 1; i >= 0; i--) {
            RobotInfo info = list.get(i);
            if(info.getBias() != 0) {
                list.remove(i);
                population.removeRobot(info.getName());
                numberKilled++;
            }
        }
        Debug.d("Finished killing non symmetrical robots. Killed:",numberKilled);
    }

    private void killNonPredictingRobots(Population population, List<RobotInfo> list) {
        if(!settings.killNonPredictingRobots)
            return;
        Debug.d("Killing non predicting robots");
        int numberKilled = 0;
        for(int i = list.size() - 1; i >= 0; i--) {
            RobotInfo info = list.get(i);
            if(info.getTotalPredictions() == 0) {
                list.remove(i);
                population.removeRobot(info.getName());
                numberKilled++;
            }
        }
        Debug.d("Finished killing non predicting robots. Killed:",numberKilled);
    }

    private void removeProtectedRobots(Population population, List<RobotInfo> list) {
        protectUntilOutcomes(list);
        protectBest(population, list);
    }

    private void protectBest(Population population, List<RobotInfo> list) {
        if(settings.protectBestRobots > 0) {
            Collections.sort(list, RobotInfo.comparatorByAbsoluteWeight);
            int i = (int)Math.round(settings.protectBestRobots * population.getDesiredSize());
            while(i-- > 0) {
                RobotInfo robotInfo = getLastFromList(list);
                if(robotInfo == null)
                    break;
            }
        }
    }

    private void protectUntilOutcomes(List<RobotInfo> list) {
        for(int i = list.size()-1; i >= 0; i--) {
            RobotInfo robotInfo = list.get(i);
            if(robotInfo.getTotalOutcomes() < settings.protectRobotsUntilOutcomes)
                list.remove(i);
        }
    }

    @Override
    public void setSettings(RobotKillerSettings killerSettings) {
        settings = killerSettings;
    }

    private void killRobotsByAge(Population population, List<RobotInfo> listCopy, List<RobotInfo> originalList) {
        Collections.sort(listCopy, RobotInfo.comparatorByAge);
        int numberToKill = (int)Math.round(settings.maximumDeathByAge * originalList.size());
        Debug.d("Killing max",numberToKill,"by age.");
        int numberKilled = killRobots(listCopy,originalList,numberToKill,population,settings.probabilityOfDeathByAge);
        Debug.d("Finished killing by age. Killed:",numberKilled);
    }

    private void killRobotsByWeight(Population population, List<RobotInfo> listCopy, List<RobotInfo> originalList) {
        if(population.haveSpaceToBreed())
            return;
        Collections.sort(listCopy, RobotInfo.comparatorByAbsoluteWeight);
        Collections.reverse(listCopy);
        int numberToKill = (int) Math.round(settings.maximumDeathByWeight * originalList.size());
        Debug.d("Killing max",numberToKill,"by weight");
        int numberKilled = killRobots(listCopy,originalList,numberToKill,population,settings.probabilityOfDeathByWeight);
        Debug.d("Finished killing by weight. Killed:",numberKilled);
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

    private RobotInfo getLastFromList(List<RobotInfo> list) {
        int size = list.size();
        if(size == 0)
            return null;
        return list.remove(size-1);
    }
}
