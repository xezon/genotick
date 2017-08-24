package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.processor.NotEnoughDataException;

import java.util.ArrayList;
import java.util.List;

public class RobotData {
    private final List<double[]> data;
    private final double futureChange;
    private final DataSetName name;

    public static RobotData createData(List<double[]> newData, DataSetName name, double futureChange) {
        return new RobotData(newData, name, futureChange);
    }

    public static RobotData createEmptyData(DataSetName name) {
        List<double []> list = new ArrayList<>();
        list.add(new double[0]);
        return createData(list,name,Double.NaN);
    }

    double getTodaysOpen() {
        return data.get(0)[0];
    }

    private RobotData(List<double[]> newData, DataSetName name, double futureChange) {
        data = newData;
        this.name = name;
        this.futureChange = futureChange;
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

    public Double getFutureChange() {
        return futureChange;
    }

    public int getColumns() {
        return data.size();
    }
}
