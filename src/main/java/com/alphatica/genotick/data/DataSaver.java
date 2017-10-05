package com.alphatica.genotick.data;

public interface DataSaver {
    void saveAll(MainAppData data);
    void save(DataSet set);
}
