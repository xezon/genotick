package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.Robot;

public class WeightCalculator {

    private WeightCalculator() {}

    public static double calculateWeight(Robot robot) {
        Double weight = getWinsDeltaSquared(robot);
        assert(!weight.isNaN());
        return weight;
    }

    private static double getWinsDelta(Robot robot) {
        int total = robot.getTotalPredictions();
        if(total == 0)
            return 0.0;
        double wins = robot.getCorrectPredictions();
        double losses = robot.getTotalPredictions() - wins;
        return wins - losses;
    }

    private static double getWinsDeltaSquared(Robot robot) {
        double delta = getWinsDelta(robot);
        double deltaSqr = delta * delta;
        return (delta >= 0.0) ? deltaSqr : -deltaSqr;
    }
}
