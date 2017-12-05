package com.alphatica.genotick.genotick;

import com.alphatica.genotick.utility.JniExport;

public enum Prediction {
    UP(1),
    DOWN(-1),
    OUT(0);
    
    private final int value;

    Prediction(int value) {
        this.value = value;
    }
    
    public static Prediction getOpposite(Prediction prediction) {
        switch (prediction) {
            case UP : return DOWN;
            case DOWN : return UP;
            case OUT : default: return OUT;
        }
    }
    
    public static Prediction getPrediction(double change) {
        if(change > 0) {
            return UP;
        }
        if(change < 0) {
            return DOWN;
        }
        return OUT;
    }

    @JniExport
    public int getValue() {
        return value;
    }

    public double toProfit(double actualChange) {
        return actualChange * value;
    }

    public boolean isCorrect(double actualFutureChange) {
        return actualFutureChange * value > 0;
    }

    @Override
    public String toString() {
        return name();
    }
}
