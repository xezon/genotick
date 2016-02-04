package com.alphatica.genotick.timepoint;

import com.alphatica.genotick.genotick.Outcome;
import com.alphatica.genotick.genotick.Prediction;

public class SetStats {
    private double totalPercentPredicted;
    private double totalPercentMispredicted;

    public SetStats() {
    }

    /**
     * Updates stats for SetStats.name
     *
     * @param actualFutureChange actual change of data in the future
     * @param prediction         predicted direction (up for positive values, down for negative)
     */
    public void update(Double actualFutureChange, Prediction prediction) {
        Outcome outcome = Outcome.getOutcome(prediction, actualFutureChange);
        switch (outcome) {
            case CORRECT:
                totalPercentPredicted += Math.abs(actualFutureChange);
                break;
            case INCORRECT:
                totalPercentMispredicted += Math.abs(actualFutureChange);
                break;
        }
    }

    @Override
    public String toString() {
        return "Predicted %: " + String.valueOf(totalPercentPredicted - totalPercentMispredicted);
    }

    public double getTotalPercent() {
        return totalPercentPredicted - totalPercentMispredicted;
    }
}
