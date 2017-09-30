package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.Column;
import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.processor.NotEnoughDataException;

import java.util.List;

public class RobotData {
    private final List<double[]> ohlcLookbackData;
    private final DataSetName name;
    private final double lastPriceChange;

    public static RobotData create(List<double[]> ohlcLookbackData, DataSetName name) {
        return new RobotData(ohlcLookbackData, name);
    }

    private RobotData(List<double[]> ohlcLookbackData, DataSetName name) {
        this.ohlcLookbackData = ohlcLookbackData;
        this.name = name;
        this.lastPriceChange = calculateLastPriceChange();
    }

    public DataSetName getName() {
        return name;
    }

    public double getPriceData(int column, int offset) {
        if (offset >= ohlcLookbackData.get(column).length)
            throw new NotEnoughDataException();
        else
            return ohlcLookbackData.get(column)[offset];
    }
    
    public int getColumnCount() {
        return ohlcLookbackData.size();
    }

    private int getAssetDataLength(int column) {
        return ohlcLookbackData.get(column).length;
    }

    public boolean isEmpty() {
        return getAssetDataLength(Column.OHLCV.OPEN) == 0;
    }
    
    double getLastPriceOpen() {
        return ohlcLookbackData.get(Column.OHLCV.OPEN)[0];
    }

    public double getLastPriceChange() {
        return lastPriceChange;
    }

    private double calculateLastPriceChange() {
        if (getAssetDataLength(Column.OHLCV.OPEN) < 2) {
            return 0.0;
        }
        final double currentOpen = ohlcLookbackData.get(Column.OHLCV.OPEN)[0];
        final double previousOpen = ohlcLookbackData.get(Column.OHLCV.OPEN)[1];
        return currentOpen - previousOpen;
    }
}
