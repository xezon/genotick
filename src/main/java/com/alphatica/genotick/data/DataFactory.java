package com.alphatica.genotick.data;

public class DataFactory {
    public static DataLoader getDefaultLoader() {
        return new FileSystemDataLoader();
    }
    
    public static DataSaver getDefaultSaver() {
        return new FileSystemDataSaver();
    }
}
