package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.processor.NotEnoughDataException;

import java.util.ArrayList;
import java.util.List;

public class RobotData {
    private final List<double[]> priceData;
    private final DataSetName name;
    private final double lastPriceChange;

    public static RobotData createData(List<double[]> priceData, DataSetName name) {
        return new RobotData(priceData, name);
    }

    public static RobotData createEmptyData(DataSetName name) {
        List<double []> list = new ArrayList<>();
        list.add(new double[0]);
        return createData(list, name);
    }

    private RobotData(List<double[]> priceData, DataSetName name) {
        this.priceData = priceData;
        this.name = name;
        this.lastPriceChange = calculateLastPriceChange();
    }

    public DataSetName getName() {
        return name;
    }

    public double getPriceData(int column, int offset) {
        if (offset >= priceData.get(column).length)
            throw new NotEnoughDataException();
        else
            return priceData.get(column)[offset];
    }
    
    public int getColumnCount() {
        return priceData.size();
    }

    private int getAssetDataLength(int column) {
        return priceData.get(column).length;
    }

    public boolean isEmpty() {
        return getAssetDataLength(0) == 0;
    }
    
    double getLastPriceOpen() {
        return priceData.get(0)[0];
    }

    public double getLastPriceChange() {
        return lastPriceChange;
    }

    private double calculateLastPriceChange() {
        if (getAssetDataLength(0) < 2) {
            return 0.0;
        }
        final double currentOpen = priceData.get(0)[0];
        final double previousOpen = priceData.get(0)[1];
        return currentOpen - previousOpen;
    }
}
