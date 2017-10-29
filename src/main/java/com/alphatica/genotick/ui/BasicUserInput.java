package com.alphatica.genotick.ui;

import com.alphatica.genotick.data.DataFactory;
import com.alphatica.genotick.data.DataLoader;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.genotick.MainSettings;

@SuppressWarnings("WeakerAccess")
abstract public class BasicUserInput implements UserInput {
    
    private MainSettings mainSettings = null;
    private MainAppData assetData = null;
    private final UserOutput output;

    BasicUserInput(UserOutput output) {
        this.output = output;
    }
    
    @Override
    public MainAppData getData(String... sources) {
        return loadData(sources);
    }
    
    protected MainAppData loadData(String... sources) {
        if (assetData == null) {
            DataLoader loader = DataFactory.getDefaultLoader(output);
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
