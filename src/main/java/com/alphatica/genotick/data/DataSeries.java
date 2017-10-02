package com.alphatica.genotick.data;

import com.alphatica.genotick.processor.NotEnoughDataException;

public class DataSeries {
    private double[][] data;
    
    public DataSeries(int columnCount, int barCount) {
        allocate(columnCount, barCount);
    }
    
    public void allocate(int columnCount, int barCount) {
        data = new double[columnCount][barCount];
    }
    
    public double get(int column, int bar) {
        if (bar < data[column].length) {
            return data[column][bar];
        }
        throw new NotEnoughDataException();
    }
    
    public double[] get(int column) {
        return data[column];
    }
    
    public void set(int column, int bar, double value) {
        data[column][bar] = value;
    }
    
    public int columnCount() {
        return data.length;
    }
    
    public int barCount() {
        return (columnCount() > 0) ? data[0].length : 0;
    }
}
