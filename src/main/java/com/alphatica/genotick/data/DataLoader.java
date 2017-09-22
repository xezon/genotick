package com.alphatica.genotick.data;

public interface DataLoader {
    MainAppData loadAll(String... sources);
    DataSet load(String fileName);
}
