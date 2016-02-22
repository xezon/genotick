package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.processor.NotEnoughDataException;

import java.util.ArrayList;
import java.util.List;

public class RobotData {
    private final List<double[]> data;
    private final Double actualChange;
    private final DataSetName name;

    public static RobotData createData(List<double[]> newData, DataSetName name, Double actualChange) {
        return new RobotData(newData,name,actualChange);
    }

    public static RobotData createEmptyData(DataSetName name) {
        List<double []> list = new ArrayList<>();
        list.add(new double[0]);
        return createData(list,name,Double.NaN);
    }

    private RobotData(List<double[]> newData, DataSetName name, Double actualChange) {
        data = newData;
        this.name = name;
        this.actualChange = actualChange;
    }

    public double getData(int dataColumn, int dataOffset) {
/*
        int tableIndex = dataColumn; // normalize(dataColumn,data.size());
        assert tableIndex >= 0: "tableIndex";
        if(tableIndex < 0 || tableIndex >= data.size()) {
            System.out.println("TableIndex is " + tableIndex + " data.size = " + data.size() +
                    " normalize: " + normalize(dataColumn,data.size()));
            System.exit(0);
        }
  */
        if (dataOffset >= data.get(dataColumn).length)
            throw new NotEnoughDataException();
        else
            return data.get(dataColumn)[dataOffset];
    }

    private int normalize(long number, int max) {
        if(number == 0 || max == 1)
            return 0;
        long positive = number > 0 ? number : -number;
        return (int)(positive < max ? positive : positive % max);
    }

    public DataSetName getName() {
        return name;
    }

    public boolean isEmpty() {
        return data.get(0).length == 0;
    }

    public Double getActualChange() {
        return actualChange;
    }

    public int getColumns() {
        return data.size();
    }
}
