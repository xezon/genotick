package com.alphatica.genotick.data;

import com.alphatica.genotick.ui.UserOutput;

public class DataFactory {
    public static DataLoader getDefaultLoader(UserOutput output) {
        return new FileSystemDataLoader(output);
    }
    
    public static DataSaver getDefaultSaver(UserOutput output) {
        return new FileSystemDataSaver(output);
    }
}
