package com.alphatica.genotick.ui;

import com.alphatica.genotick.data.DataFactory;
import com.alphatica.genotick.data.DataLoader;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.genotick.MainSettings;
import com.alphatica.genotick.genotick.Simulation;

@SuppressWarnings("WeakerAccess")
abstract public class BasicUserInput implements UserInput {
    private MainSettings mainSettings = null;
    private MainAppData assetData = null;

    @Override
    public MainAppData getData(String dataDirectory) {
        if (assetData == null) {
            DataLoader dl = DataFactory.getDefaultLoader(dataDirectory);
            assetData = dl.createRobotData();
        }
        return assetData;
    }
    
    @Override
    public void clearCache() {
        mainSettings = null;
        assetData = null;
    }
    
    protected void setMainSettings(MainSettings mainSettings) {
        this.mainSettings = mainSettings;
    }
    
    protected MainSettings getMainSettings() {
        return mainSettings;
    }
}
