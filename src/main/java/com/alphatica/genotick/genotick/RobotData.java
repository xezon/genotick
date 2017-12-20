package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.Column;
import com.alphatica.genotick.data.DataSeries;
import com.alphatica.genotick.data.DataSetName;

public class RobotData {
    private final DataSetName name;
    private final DataSeries ohlcTrainingData;
    private final DataSeries ohlcAssetData;

    public static RobotData create(DataSetName name, DataSeries ohlcTrainingData, DataSeries ohlcAssetData) {
        return new RobotData(name, ohlcTrainingData, ohlcAssetData);
    }

    private RobotData(DataSetName name, DataSeries ohlcTrainingData, DataSeries ohlcAssetData) {
        this.name = name;
        this.ohlcTrainingData = ohlcTrainingData;
        this.ohlcAssetData = ohlcAssetData;
    }

    public DataSetName getName() {
        return name;
    }

    public double getTrainingPriceData(int column, int offset) {
        return ohlcTrainingData.get(column, offset);
    }

    public int getTrainingColumnCount() {
        return ohlcTrainingData.columnCount();
    }

    double getLastAssetPriceOpen() {
        return ohlcAssetData.get(Column.OHLC.OPEN, 0);
    }

    public double getLastTrainingPriceChange() {
        if (ohlcTrainingData.barCount() < 2) {
            return 0.0;
        }
        final double currentOpen = ohlcTrainingData.get(Column.OHLC.OPEN, 0);
        final double previousOpen = ohlcTrainingData.get(Column.OHLC.OPEN, 1);
        return currentOpen - previousOpen;
    }
}
