package com.alphatica.genotick.account;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.ui.UserInputOutputFactory;
import com.alphatica.genotick.ui.UserOutput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

public class ProfitRecorder {

    private final List<Double> profits = new ArrayList<>();
    private final Map<DataSetName, Integer> wins = new HashMap<>();
    private final Map<DataSetName, Integer> losses = new HashMap<>();
    private final Set<DataSetName> names = new HashSet<>();
    private final UserOutput output;
    
    public ProfitRecorder(UserOutput output) {
        this.output = output;
    }
    
    void addTradeResult(DataSetName name, double profit) {
        names.add(name);
        profits.add(profit);
        if (profit > 0) {
            wins.merge(name, 1, Integer::sum);
        }
        else if (profit < 0) {
            losses.merge(name, 1, Integer::sum);
        }
    }
    
    Set<DataSetName> getRecordedDataSetNames() {
        return names;
    }
    
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
    
    void outputWinRateForAllRecords() {
        outputWinRate(names);
    }

    void outputWinRate(Collection<DataSetName> names) {
        names.forEach(this::outputWinRate);
    }

    private void outputWinRate(DataSetName name) {
        int correct = ofNullable(wins.get(name)).orElse(0);
        int incorrect = ofNullable(losses.get(name)).orElse(0);
        if(correct + incorrect > 0) {
            double percentCorrect = (double) correct / (correct + incorrect) * 100;
            output.infoMessage(getWinRateFormat(name, percentCorrect));
        }
        else {
            output.infoMessage(getNoWinRateFormat(name));
        }
    }
    
    public static String getWinRateFormat(DataSetName name, double percentCorrect) {
        return String.format("Win rate for %s: %.2f %%", name.getPath(), percentCorrect);
    }
    
    public static String getNoWinRateFormat(DataSetName name) {
        return String.format("No win rate for %s", name.getPath());
    }
}
