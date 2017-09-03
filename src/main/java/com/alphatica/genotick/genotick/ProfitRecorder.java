package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.DataSet;
import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.ui.UserInputOutputFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

//TODO move to Account. Engine should not be bothered with this
class ProfitRecorder {
    private final List<Double> profits = new ArrayList<>();
    private final Map<DataSetName, Integer> correctPredictions = new HashMap<>();
    private final Map<DataSetName, Integer> inCorrectPredictions = new HashMap<>();

    double getProfit() {
        return calculateProfit(profits);
    }

    double getProfitSecondHalf() {
        return calculateProfit(getSecondHalf(profits));
    }

    private List<Double> getSecondHalf(List<Double> profits) {
        int halfIndex = (int)Math.round(profits.size() / 2.0);
        return profits.subList(halfIndex,profits.size());
    }

    double getMaxDrawdown() {
        return calculateMaxDrawdown(profits);
    }

    double getMaxDrawdownSecondHalf() {
        return calculateMaxDrawdown(getSecondHalf(profits));
    }

    private double calculateProfit(List<Double> profits) {
        double account = 1;
        for(Double percentEarned: profits) {
            double change = (percentEarned / 100.0) + 1;
            account *= change;
        }
        return (account - 1) * 100;
    }

    private double calculateMaxDrawdown(List<Double> profits) {
        double account = 1;
        double maxAccount = account;
        double maxDrawdown = 0;

        for(Double percentEarned: profits) {
            double change = (percentEarned / 100) + 1;
            account *= change;
            if(account > maxAccount) {
                maxAccount = account;
                continue;
            }
            double drawdown = 100 * Math.abs(account - maxAccount) / maxAccount;
            if(drawdown > maxDrawdown) {
                maxDrawdown = drawdown;
            }
        }
        return maxDrawdown;
    }

    void showPercentCorrectPredictions(Collection<DataSet> dataSets) {
        dataSets.forEach(this::showPercentCorrectPredictions);
    }

    private void showPercentCorrectPredictions(DataSet dataSet) {
        int correct = ofNullable(correctPredictions.get(dataSet.getName())).orElse(0);
        int incorrect = ofNullable(inCorrectPredictions.get(dataSet.getName())).orElse(0);
        if(correct + incorrect > 0) {
            double percentCorrect = (double) correct / (correct + incorrect);
            UserInputOutputFactory.getUserOutput()
                    .infoMessage(format("Percent correct for %s: %f %%", dataSet.getName().getPath(), percentCorrect));
        }
    }
}
