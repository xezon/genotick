package com.alphatica.genotick.timepoint;

import com.alphatica.genotick.genotick.Outcome;
import com.alphatica.genotick.genotick.Prediction;

import java.io.Serializable;

public class SetStats implements Serializable{
    private static final long serialVersionUID = 4443286273783452188L;
    private double totalPercentPredicted;
    private double totalPercentMispredicted;
    private Outcome outcome = Outcome.OUT;

    public Outcome getOutcome() {
        return outcome;
    }

    /**
     * Updates stats for SetStats.name
     *
     * @param actualFutureChange actual change of data in the future
     * @param prediction         predicted direction (up for positive values, down for negative)
     */
    void update(Double actualFutureChange, Prediction prediction) {
        switch (Outcome.getOutcome(prediction, actualFutureChange)) {
            case CORRECT:
                totalPercentPredicted += Math.abs(actualFutureChange);
                outcome = Outcome.CORRECT;
                break;
            case INCORRECT:
                totalPercentMispredicted += Math.abs(actualFutureChange);
                outcome = Outcome.INCORRECT;
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
