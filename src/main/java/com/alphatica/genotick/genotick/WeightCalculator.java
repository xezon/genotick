package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.Robot;

import static com.alphatica.genotick.utility.Assert.gassert;

import java.io.Serializable;

public class WeightCalculator implements Serializable {

    private static final long serialVersionUID = 3856847185557738171L;
    private WeightMode mode = WeightMode.WIN_COUNT;
    private double exponent = 1.0;

    public void setWeightMode(WeightMode mode) {
        this.mode = mode;
    }

    public void setWeightExponent(double exponent) {
        this.exponent = exponent;
    }

    public double calculateWeight(Robot robot) {
        Double weight = 0.0;
        switch (this.mode) {
            case WIN_COUNT: weight = getWinCount(robot); break;
            case WIN_RATE: weight = getWinRate(robot); break;
            case PROFIT_COUNT: weight = getProfitCount(robot); break;
            case PROFIT_FACTOR: weight = getProfitFactor(robot); break;
        }
        if (weight >= 0.0) {
            weight = Math.pow(weight, this.exponent);
        }
        else {
            weight = -Math.pow(-weight, this.exponent);
        }
        gassert(!weight.isNaN());
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
