package com.alphatica.genotick.account;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.genotick.Prediction;
import com.alphatica.genotick.ui.UserOutput;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class Account {
    private Map<DataSetName, Prediction> pendingOrders = new HashMap<>();
    private Map<DataSetName, Trade> trades = new HashMap<>();

    private BigDecimal cash;
    private final UserOutput output;
    private final ProfitRecorder profitRecorder;

    public Account(BigDecimal cash, UserOutput output, ProfitRecorder profitRecorder) {
        this.cash = cash;
        this.output = output;
        this.profitRecorder = profitRecorder;
        output.reportAccountOpening(cash);
    }

    public BigDecimal getValue() {
        return cash.add(trades.values().stream().map(Trade::value).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    public void openTrades(Map<DataSetName, Double> prices) {
        if(!pendingOrders.isEmpty()) {
            BigDecimal cashPerTrade = cash.divide(BigDecimal.valueOf(pendingOrders.size()), MathContext.DECIMAL128);
            prices.forEach((name, price) -> openTrade(cashPerTrade, name, BigDecimal.valueOf(price)));
        }
    }

    public void closeTrades(Map<DataSetName, Double> prices) {
        prices.forEach((name, price) -> closeTrade(name, BigDecimal.valueOf(price)));
    }

    public void closeAccount() {
        List<DataSetName> openedTradesNames = new ArrayList<>(trades.keySet());
        openedTradesNames.forEach(name -> closeTrade(name, trades.get(name).getPrice()));
        output.reportAccountClosing(cash);
        profitRecorder.outputWinRateForAllRecords();
    }

    public void addPendingOrder(DataSetName name, Prediction prediction) {
        validateAddPending(name);
        if(prediction != Prediction.OUT) {
            output.reportPendingTrade(name, prediction);
            pendingOrders.put(name, prediction);
        }
    }

    BigDecimal getCash() {
        return cash;
    }

    private void openTrade(BigDecimal cashPerTrade, DataSetName name, BigDecimal price) {
        validateOpenTrade(name);
        ofNullable(pendingOrders.get(name)).ifPresent(prediction -> {
            BigDecimal quantity = cashPerTrade.divide(price, MathContext.DECIMAL128);
            if(prediction == Prediction.DOWN) {
                quantity = quantity.negate();
            }
            output.reportOpeningTrade(name, quantity, price.doubleValue());
            Trade trade = new Trade(quantity, price);
            trades.put(name, trade);
            pendingOrders.remove(name);
            cash = cash.subtract(cashPerTrade);
        });
    }

    private void closeTrade(DataSetName name, BigDecimal price) {
        ofNullable(trades.get(name)).ifPresent(trade -> {
            BigDecimal priceDifference = price.subtract(trade.getPrice());
            BigDecimal profit = trade.getQuantity().multiply(priceDifference);
            BigDecimal initial = trade.getQuantity().abs().multiply(trade.getPrice());
            cash = cash.add(profit).add(initial);
            output.reportClosingTrade(name, trade.getQuantity(), price, profit, cash);
            profitRecorder.addTradeResult(name, profit.doubleValue());
            trades.remove(name);
        });
    }

    private void validateAddPending(DataSetName name) {
        if(pendingOrders.containsKey(name)) {
            throw new AccountException("results exist");
        }
    }

    private void validateOpenTrade(DataSetName name) {
        if(trades.containsKey(name)) {
            throw new AccountException("Previous trade not closed");
        }
    }
}
