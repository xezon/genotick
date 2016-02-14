package com.alphatica.genotick.ui;

import com.alphatica.genotick.timepoint.TimePoint;

public interface UserOutput {
    void errorMessage(String message);

    void warningMessage(String message);

    void reportProfitForTimePoint(TimePoint timePoint, double cumulativeProfit, double timePointProfit);
}
