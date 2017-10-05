package com.alphatica.genotick.ui;

import com.alphatica.genotick.data.DataFactory;
import com.alphatica.genotick.data.DataLoader;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.genotick.MainSettings;

@SuppressWarnings("WeakerAccess")
abstract public class BasicUserInput implements UserInput {
    private MainSettings mainSettings = null;
    private MainAppData assetData = null;

    @Override
    public MainAppData getData(String... sources) {
        return loadData(sources);
    }
    
    @Override
    public void clearCache() {
        mainSettings = null;
        assetData = null;
    }
    
    protected MainAppData loadData(String... sources) {
        if (assetData == null) {
            DataLoader loader = DataFactory.getDefaultLoader();
            assetData = loader.loadAll(sources);
        }
        return assetData;
    }
    
    protected void setMainSettings(MainSettings mainSettings) {
        this.mainSettings = mainSettings;
    }
    
    protected MainSettings getMainSettings() {
        return mainSettings;
    }
}
