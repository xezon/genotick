package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.Robot;

public class WeightCalculator {

    private static WeightMode MODE = WeightMode.WIN_COUNT;
    private static double EXPONENT = 1.0;

    private WeightCalculator() {}

    public static void setWeightMode(WeightMode mode) {
        MODE = mode;
    }

    public static void setWeightExponent(double exponent) {
        EXPONENT = exponent;
    }

    public static double calculateWeight(Robot robot) {
        Double weight = 0.0;
        switch (MODE) {
            case WIN_COUNT: weight = getWinCount(robot); break;
            case WIN_RATE: weight = getWinRate(robot); break;
            case PROFIT_COUNT: weight = getProfitCount(robot); break;
            case PROFIT_FACTOR: weight = getProfitFactor(robot); break;
        }
        double weightPow = Math.pow(weight, EXPONENT);
        weight = (weight >= 0.0) ? weightPow : -weightPow;
        assert(!weight.isNaN());
        return weight;
    }

    private static double getWinCount(Robot robot) {
        return robot.getCorrectPredictions() - robot.getIncorrectPredictions();
    }
    
    private static double getWinRate(Robot robot) {
        return getMirroredRate(robot.getCorrectPredictions(), robot.getIncorrectPredictions());
    }
    
    private static double getProfitCount(Robot robot) {
        return robot.getProfitablePriceMove() - robot.getUnprofitablePriceMove();
    }
    
    private static double getProfitFactor(Robot robot) {
        return getMirroredRate(robot.getProfitablePriceMove(), robot.getUnprofitablePriceMove());
    }
        
    private static double getMirroredRate(final double a, final double b) {
        if (a == b)
            return 0.0;
        else if (a > b)
            return (b == 0.0) ? 1.0 : (a / b);
        else
            return (a == 0.0) ? -1.0 : -(b / a);
    }
}
