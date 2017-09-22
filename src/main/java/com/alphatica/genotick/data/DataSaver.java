package com.alphatica.genotick.data;

public interface DataSaver {
    boolean saveAll(MainAppData data);
    boolean save(DataSet set);
}
