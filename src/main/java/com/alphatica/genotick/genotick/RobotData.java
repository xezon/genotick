package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.Column;
import com.alphatica.genotick.data.DataSeries;
import com.alphatica.genotick.data.DataSetName;

public class RobotData {
    private final DataSetName name;
    private final DataSeries ohlcLookbackData;

    public static RobotData create(DataSetName name, DataSeries ohlcLookbackData) {
        return new RobotData(name, ohlcLookbackData);
    }

    private RobotData(DataSetName name, DataSeries ohlcLookbackData) {
        this.name = name;
        this.ohlcLookbackData = ohlcLookbackData;
    }

    public DataSetName getName() {
        return name;
    }

    public double getPriceData(int column, int offset) {
        return ohlcLookbackData.get(column, offset);
    }

    public int getColumnCount() {
        return ohlcLookbackData.columnCount();
    }

    double getLastPriceOpen() {
        return ohlcLookbackData.get(Column.OHLC.OPEN, 0);
    }

    public double getLastPriceChange() {
        if (ohlcLookbackData.barCount() < 2) {
            return 0.0;
        }
        final double currentOpen = ohlcLookbackData.get(Column.OHLC.OPEN, 0);
        final double previousOpen = ohlcLookbackData.get(Column.OHLC.OPEN, 1);
        return currentOpen - previousOpen;
    }
}
