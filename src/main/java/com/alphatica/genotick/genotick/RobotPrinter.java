package com.alphatica.genotick.genotick;

import java.io.File;

import com.alphatica.genotick.population.PopulationDAOFileSystem;
import com.alphatica.genotick.population.Robot;

class RobotPrinter {
    
    public static void printRobot(String path) throws IllegalAccessException {
        String robotString = getRobotString(path);
        System.out.println(robotString);
    }

    private static String getRobotString(String path) throws IllegalAccessException {
        File file = new File(path);
        Robot robot = PopulationDAOFileSystem.getRobotFromFile(file);
        return robot.showRobot();
    }
}
