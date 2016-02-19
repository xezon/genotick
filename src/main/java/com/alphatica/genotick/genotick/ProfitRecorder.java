package com.alphatica.genotick.genotick;

import java.util.ArrayList;
import java.util.List;

public class ProfitRecorder {
    private List<Double> profits = new ArrayList<>();

    public double getProfit() {
        return calculateProfit(profits);
    }

    public double getProfitSecondHalf() {
        return calculateProfit(getSecondHalf(profits));
    }

    private List<Double> getSecondHalf(List<Double> profits) {
        int halfIndex = (int)Math.round(profits.size() / 2.0);
        return profits.subList(halfIndex,profits.size());
    }

    public double getMaxDrawdown() {
        return calculateMaxDrawdown(profits);
    }

    public double getMaxDrawdownSecondHalf() {
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

    public void recordProfit(double percentEarned) {
        profits.add(percentEarned);

    }
}
