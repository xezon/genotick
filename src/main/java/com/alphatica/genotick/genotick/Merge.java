package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.PopulationDAOFileSystem;
import com.alphatica.genotick.population.Robot;
import com.alphatica.genotick.population.RobotInfo;

import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

class Merge {
    private Merge() {}
    
    public static ErrorCode mergePopulations(String destination, String source) throws IllegalAccessException {
        File destinationPath = new File(destination);
        destinationPath.mkdirs();
        PopulationDAOFileSystem dao = new PopulationDAOFileSystem(destinationPath.getAbsolutePath());
        Population destinationPopulation = PopulationFactory.getDefaultPopulation(dao);
        List<RobotInfo> destinationRobots = destinationPopulation.getRobotInfoList();
        destinationRobots.sort(Comparator.comparing(RobotInfo::getScore));
        double initialScore = populationScore(destinationRobots);
        System.out.println(format("Current population size: %d desiredSize: %d population score: %.4f", 
            destinationPopulation.getSize(), destinationPopulation.getDesiredSize(), initialScore));
        System.out.println(format("Destination path: %s", destinationPath.getAbsolutePath()));
        try {
            Files.walk(Paths.get(source), 1)
                .filter(path -> Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS))
                .filter(path -> !destinationPath.getAbsolutePath().equals(new File(path.toString().replace("./", "")).getAbsolutePath()))
                .collect(Collectors.toList())
                .parallelStream()
                .forEach(directory -> mergeSource(destinationPopulation, destinationPath.getAbsolutePath(), destinationRobots, directory));
        } catch (IOException e) {
            // Falling thruogh here just ignores folders with any access errors.
        }
        destinationPopulation.saveOnDisk();
        double newScore = populationScore(destinationRobots);
        if(newScore > initialScore) {
            System.out.println(format("Success merging populations. New size: %d old score: %.4f new score: %.4f", 
                destinationPopulation.getSize(), initialScore, newScore));
            return ErrorCode.NO_ERROR;
        }
        if(newScore < initialScore) {
            System.out.println(format("Warning population score decreased after merge:%.4f", newScore));
        }
        return ErrorCode.NO_OUTPUT;
    }
    
    private static void mergeSource(Population destinationPopulation, String destination, List<RobotInfo> destinationRobots, Path sourcePath) {
        File sourceFile = new File(sourcePath.toString().replace("./", ""));
        String source = sourceFile.getAbsolutePath();
        PopulationDAOFileSystem daoSource = new PopulationDAOFileSystem(source);
        Population sourcePopulation = PopulationFactory.getDefaultPopulation(daoSource);
        if(sourcePopulation.getSize() < 1) {
            return;
        }
        System.out.println(format("Source path: %s", source));
        List<RobotInfo> sourceRobots = sourcePopulation.getRobotInfoList();
        sourceRobots .stream()
            .filter(robot -> robot.getWeight() == 0.0)
            .collect(Collectors.toList())
            .forEach(robot -> sourcePopulation.removeRobot(robot.getName()));
        sourceRobots.sort(Comparator.comparing(RobotInfo::getScore).reversed());
        while(!sourceRobots.isEmpty()) {
            RobotInfo best = sourceRobots.remove(0);

            synchronized(destinationPopulation) {
                RobotInfo worst = destinationRobots.isEmpty() ? null : destinationRobots.get(0);
                if(moreRobotsNeeded(destinationPopulation, best, worst)) {
                    // Take robot...
                    System.out.println(format("Adding robot %s to destination due to desination not full. Weight: %.4f new size: %d", 
                        best.getName(), best.getWeight(), destinationPopulation.getSize()+1));
                    if(!moveRobot(sourcePopulation, destinationPopulation, destinationRobots, best)) {
                        return;
                    }
                } else if(betterRobotFound(destinationPopulation, best, worst)) {
                    System.out.println(format("Adding robot %s to destination due to higher weight. Weight: %.4f population score: %.4f",
                        best.getName(), best.getWeight(), populationScore(destinationRobots)));
                    destinationRobots.remove(0);
                    destinationPopulation.removeRobot(worst.getName());
                    if(!moveRobot(sourcePopulation, destinationPopulation, destinationRobots, best)) {
                        return;
                    }
                }
            }
            break;
        }
        try {
            Files.walk(sourceFile.toPath())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        } catch (IOException e) {
            System.out.println(format("Exception clearing directory %s. Exception %s", sourceFile, e.toString()));
        }
    }
    
    private static boolean moreRobotsNeeded(Population destinationPopulation, RobotInfo best, RobotInfo worst) {
        return destinationPopulation.getSize() < destinationPopulation.getDesiredSize() 
            && best.getScore() > 0.0 
            && best.isPredicting();
    }
    
    private static boolean betterRobotFound(Population destinationPopulation, RobotInfo best, RobotInfo worst) {
        return worst != null 
            && best.getScore() > 0 
            && worst.getScore() < best.getScore()
            && (best.isPredicting() || !worst.isPredicting());
    }
    
    private static double populationScore(List<RobotInfo> robotInfoList) {
        return robotInfoList.stream().mapToDouble(RobotInfo::getScore).average().orElse(0);
    }
    
    private static boolean moveRobot(Population sourcePopulation, Population destinationPopulation, List<RobotInfo> destinationRobots, RobotInfo robot) {
        Robot movingRobot = sourcePopulation.getRobot(robot.getName());
        if(movingRobot == null) {
            return false;
        }
        sourcePopulation.removeRobot(robot.getName());
        movingRobot.setName(null);
        destinationPopulation.saveRobot(movingRobot);
        destinationRobots.add(robot);
        destinationRobots.sort(Comparator.comparing(RobotInfo::getScore));
        return true;
    }
}

