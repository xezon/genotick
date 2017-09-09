package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.processor.NotEnoughDataException;

import java.util.ArrayList;
import java.util.List;

public class RobotData {
    private final List<double[]> priceData;
    private final double lastPriceChange;
    private final DataSetName name;

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
        this.lastPriceChange = calculateLastPriceChange(priceData);
    }

    public double getLastPriceChange() {
        return lastPriceChange;
    }

    public double getData(int dataColumn, int dataOffset) {
        if (dataOffset >= priceData.get(dataColumn).length)
            throw new NotEnoughDataException();
        else
            return priceData.get(dataColumn)[dataOffset];
    }

    public DataSetName getName() {
        return name;
    }

    public boolean isEmpty() {
        return priceData.get(0).length == 0;
    }

    public int getColumns() {
        return priceData.size();
    }

    public double getLastOpen() {
        return priceData.get(0)[0];
    }

    private static double calculateLastPriceChange(List<double[]> priceData) {
        if(priceData.get(0).length < 2) {
            return 0;
        }
        double last = priceData.get(0)[0];
        double previous = priceData.get(0)[1];
        return calculateLastPriceChange(last, previous);
    }
    
    public static double calculateLastPriceChange(double last, double previous) {
        return 100 * (last - previous) / previous;
    }
}
