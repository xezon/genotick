package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.PopulationDAOFileSystem;
import com.alphatica.genotick.population.Robot;
import com.alphatica.genotick.population.RobotInfo;

import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.stream.Collectors;

import static java.lang.String.format;

class Merge {
    private Merge() {}
    
    public static ErrorCode mergePopulations(String destination, String source) throws IllegalAccessException {
        File destinationPath = new File(destination);
        destinationPath.mkdirs();
        PopulationDAOFileSystem dao = new PopulationDAOFileSystem(destinationPath.getAbsolutePath());
        Population destinationPopulation = PopulationFactory.getDefaultPopulation(dao);
        double initialScore = populationScore(destinationPopulation);
        System.out.println(format("Current population size: %d desiredSize: %d population score: %.4f", 
            destinationPopulation.getSize(), destinationPopulation.getDesiredSize(), initialScore));
        try {
            System.out.println(destinationPath.getAbsolutePath());
            Files.walk(Paths.get(source), 1)
                .filter(path -> Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS))
                .collect(Collectors.toList())
                .parallelStream()
                .forEach(directory -> mergeSource(destinationPopulation, destinationPath.getAbsolutePath(), directory));
        } catch (IOException e) {
            // Falling thruogh here just ignores folders with any access errors.
        }
        destinationPopulation.saveOnDisk();
        double newScore = populationScore(destinationPopulation);
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
    
    private static void mergeSource(Population destinationPopulation, String destination, Path sourcePath) {
        String source = sourcePath.toString().replace("./", "");
        File sourceFile = new File(source);
        source = sourceFile.getAbsolutePath();
        if(destination.equals(source)) {
            return;
        }
        PopulationDAOFileSystem daoSource = new PopulationDAOFileSystem(source);
        Population sourcePopulation = PopulationFactory.getDefaultPopulation(daoSource);
        if(sourcePopulation.getSize() < 1) {
            return;
        }
        System.out.println(source);
        sourcePopulation.getRobotInfoList()
            .stream()
            .filter(robot -> robot.getWeight() == 0.0)
            .collect(Collectors.toList())
            .forEach(robot -> sourcePopulation.removeRobot(robot.getName()));
        
        while(sourcePopulation.getSize() > 0) {
            RobotInfo best = findBestPerformingRobot(sourcePopulation);
            if(best == null) {
                break;
            }
            
            synchronized(destinationPopulation) {
                RobotInfo worst = findWorstPerformingRobot(destinationPopulation);
                
                if(destinationPopulation.getSize() < destinationPopulation.getDesiredSize() 
                    && Math.abs(best.getWeight()) > 0.0 
                    && best.isPredicting()) {
                    // Take robot...
                    System.out.println(format("Adding robot %s to destination due to desination not full. Weight: %.4f new size: %d", 
                        best.getName(), best.getWeight(), destinationPopulation.getSize()+1));
                    if(moveRobot(sourcePopulation, destinationPopulation, best)) {
                        continue;
                    }
                } else if(worst != null 
                    && Math.abs(best.getWeight()) > 0 
                    && Math.abs(worst.getWeight()) < Math.abs(best.getWeight()) 
                    && (best.isPredicting() || !worst.isPredicting())) {
                    System.out.println(format("Adding robot %s to destination due to higher weight. Weight: %.4f population score: %.4f", 
                        best.getName(), best.getWeight(), populationScore(destinationPopulation)));
                    destinationPopulation.removeRobot(worst.getName());
                    if(moveRobot(sourcePopulation, destinationPopulation, best)) {
                        continue;
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
    
    private static double populationScore(Population population) {
        return population.getRobotInfoList().stream().mapToDouble((robot) -> {
            return Math.abs(robot.getWeight());
        }).average().orElse(0);
    }
    
    private static boolean moveRobot(Population sourcePopulation, Population destinationPopulation, RobotInfo robot) {
        Robot movingRobot = sourcePopulation.getRobot(robot.getName());
        if(movingRobot == null) {
            return false;
        }
        sourcePopulation.removeRobot(robot.getName());
        movingRobot.setName(null);
        destinationPopulation.saveRobot(movingRobot);
        return true;
    }
    
    private static RobotInfo findWorstPerformingRobot(Population population) {
        if(population.getRobotInfoList().isEmpty()) {
            return null;
        }
        return population.getRobotInfoList().stream().min((a,b) -> {
            return (int)(Math.abs(a.getWeight()) - Math.abs(b.getWeight()));
        }).get();
    }
    
    private static RobotInfo findBestPerformingRobot(Population population) {
        if(population.getRobotInfoList().isEmpty()) {
            return null;
        }
        return population.getRobotInfoList().stream().max((a,b) -> {
            return (int)(Math.abs(a.getWeight()) - Math.abs(b.getWeight()));
        }).get();
    }
}

