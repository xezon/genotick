package com.alphatica.genotick.ui;

import com.alphatica.genotick.genotick.MainSettings;
import com.alphatica.genotick.data.DataFactory;
import com.alphatica.genotick.data.DataLoader;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.exceptions.ExecutionException;
import com.alphatica.genotick.genotick.MainInterface;

class ExternalInput implements UserInput {
    
    private MainAppData assetData = null;
    private final UserOutput output;
    
    ExternalInput(UserOutput output) {
        this.output = output;
    }
    
    @Override
    public MainSettings getSettings() throws ExecutionException {
        MainSettings settings = MainInterface.getCurrentSettings();
        if (settings == null) {
            throw new ExecutionException("External Input cannot retrieve main settings. Did you use External Input without using the c++ interface?");
        }
        return settings;
    }
    
    @Override
    public MainAppData getData(String... sources) throws ExecutionException {
        if (hasSources(sources)) {
            return loadData(sources);
        }
        MainAppData data = MainInterface.getCurrentData();
        if (data == null) {
            throw new ExecutionException("External Input cannot retrieve asset data. Did you use External Input without using the c++ interface?");
        }
        return data;
    }
    
    @Override
    public void clearCache() {
        assetData = null;
    }
    
    private boolean hasSources(String... sources) {
        for (String source : sources) {
            if (!source.isEmpty())
                return true;
        }
        return false;
    }
    
    private MainAppData loadData(String... sources) {
        if (assetData == null) {
            DataLoader loader = DataFactory.getDefaultLoader(output);
            assetData = loader.loadAll(sources);
        }
        return assetData;
    }
}
