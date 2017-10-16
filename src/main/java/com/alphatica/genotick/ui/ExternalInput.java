package com.alphatica.genotick.ui;

import com.alphatica.genotick.genotick.MainSettings;
import com.alphatica.genotick.data.DataFactory;
import com.alphatica.genotick.data.DataLoader;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.genotick.MainInterface;

class ExternalInput implements UserInput {
    private MainAppData assetData = null;
    
    @Override
    public MainSettings getSettings() {
        return MainInterface.getSettings(MainInterface.getCurrentSessionId());
    }
    
    @Override
    public MainAppData getData(String... sources) {
        if (hasSources(sources)) {
            return loadData(sources);
        }
        return MainInterface.getData(MainInterface.getCurrentSessionId());
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
            DataLoader loader = DataFactory.getDefaultLoader();
            assetData = loader.loadAll(sources);
        }
        return assetData;
    }
}
