package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.Column;
import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.processor.NotEnoughDataException;

import java.util.List;

public class RobotData {
    private final DataSetName name;
    private final List<double[]> ohlcLookbackData;

    public static RobotData create(DataSetName name, List<double[]> ohlcLookbackData) {
        return new RobotData(name, ohlcLookbackData);
    }

    private RobotData(DataSetName name, List<double[]> ohlcLookbackData) {
        this.name = name;
        this.ohlcLookbackData = ohlcLookbackData;
    }

    public DataSetName getName() {
        return name;
    }

    public List<double[]> getOhlcLookbackData(RobotDataManager.Friend friend) {
        return ohlcLookbackData;
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

    private int getLookbackDataLength(int column) {
        return ohlcLookbackData.get(column).length;
    }

    double getLastPriceOpen() {
        return ohlcLookbackData.get(Column.OHLCV.OPEN)[0];
    }

    public double getLastPriceChange() {
        if (getLookbackDataLength(Column.OHLCV.OPEN) < 2) {
            return 0.0;
        }
        final double currentOpen = ohlcLookbackData.get(Column.OHLCV.OPEN)[0];
        final double previousOpen = ohlcLookbackData.get(Column.OHLCV.OPEN)[1];
        return currentOpen - previousOpen;
    }
}
