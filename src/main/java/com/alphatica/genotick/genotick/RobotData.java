package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.processor.NotEnoughDataException;

import java.util.ArrayList;
import java.util.List;

public class RobotData {
    private final List<double[]> data;
    private final double lastChange;
    private final DataSetName name;

    public static RobotData createData(List<double[]> newData, DataSetName name) {
        return new RobotData(newData, name);
    }

    public static RobotData createEmptyData(DataSetName name) {
        List<double []> list = new ArrayList<>();
        list.add(new double[0]);
        return createData(list, name);
    }

    private RobotData(List<double[]> newData, DataSetName name) {
        data = newData;
        this.name = name;
        this.lastChange = calculateLastChange(newData);
    }

    public double getLastChange() {
        return lastChange;
    }

    public double getData(int dataColumn, int dataOffset) {
        if (dataOffset >= data.get(dataColumn).length)
            throw new NotEnoughDataException();
        else
            return data.get(dataColumn)[dataOffset];
    }

    public DataSetName getName() {
        return name;
    }

    public boolean isEmpty() {
        return data.get(0).length == 0;
    }

    public int getColumns() {
        return data.size();
    }

    public double getLastOpen() {
        return data.get(0)[0];
    }

    private static double calculateLastChange(List<double[]> newData) {
        if(newData.get(0).length < 2) {
            return 0;
        }
        double last = newData.get(0)[0];
        double previous = newData.get(0)[1];
        return calculateLastChange(last, previous);
    }
    
    public static double calculateLastChange(double last, double previous) {
        return 100 * (last - previous) / previous;
    }
}
