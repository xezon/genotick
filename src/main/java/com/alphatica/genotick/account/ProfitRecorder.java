package com.alphatica.genotick.account;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.ui.UserOutput;
import com.alphatica.genotick.chart.GenoChartFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

public class ProfitRecorder {

    private static final String PROFIT_CHART_NAME = "Profit Chart";
    private final List<Double> profits = new ArrayList<>();
    private final Map<DataSetName, Integer> wins = new HashMap<>();
    private final Map<DataSetName, Integer> losses = new HashMap<>();
    private final Set<DataSetName> names = new HashSet<>();
    private final UserOutput output;
    private double accumulatedProfit = 0.0;
    
    public ProfitRecorder(UserOutput output) {
        this.output = output;
    }
    
    public void onUpdate(final int bar) {
        GenoChartFactory.get().addXYLineChart(PROFIT_CHART_NAME, "Bar", "Profit", "Total", (double)bar, accumulatedProfit);
    }
    
    public void onFinish() {
        GenoChartFactory.get().plot(PROFIT_CHART_NAME);
    }
    
    void addTradeResult(DataSetName name, double profit) {
        accumulatedProfit += profit;
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
        double account = 1.0;
        for(Double percentEarned: profits) {
            double change = (percentEarned / 100.0) + 1.0;
            account *= change;
        }
        return (account - 1.0) * 100.0;
    }

    private double calculateMaxDrawdown(List<Double> profits) {
        double account = 1.0;
        double maxAccount = account;
        double maxDrawdown = 0.0;

        for(Double percentEarned: profits) {
            double change = (percentEarned / 100.0) + 1.0;
            account *= change;
            if(account > maxAccount) {
                maxAccount = account;
                continue;
            }
            double drawdown = 100.0 * Math.abs(account - maxAccount) / maxAccount;
            if(drawdown > maxDrawdown) {
                maxDrawdown = drawdown;
            }
        }
        return maxDrawdown;
    }
    
    void outputWinRateForAllRecords() {
        names.forEach(this::outputWinRate);
    }

    private void outputWinRate(DataSetName name) {
        int correct = ofNullable(wins.get(name)).orElse(0);
        int incorrect = ofNullable(losses.get(name)).orElse(0);
        if(correct + incorrect > 0) {
            double percentCorrect = (double) correct / (correct + incorrect) * 100;
            output.infoMessage(format("Win rate for %s: %.2f %%", name.getPath(), percentCorrect));
        }
        else {
            output.infoMessage(format("No win rate for %s", name.getPath()));
        }
    }

}
