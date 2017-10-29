package com.alphatica.genotick.ui;

import com.alphatica.genotick.genotick.MainInterface;
import com.alphatica.genotick.genotick.MainSettings;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.exceptions.ExecutionException;

class ExternalInput implements UserInput {
    
    private final MainSettings settings;
    private final MainAppData data;
    
    ExternalInput(MainInterface.Session session) throws ExecutionException {
        if (session == null) {
            throw new ExecutionException("External Input cannot be initialized. Did you use External Input without using the C++ interface?");
        }
        this.settings = session.settings;
        this.data = session.data;
    }
    
    @Override
    public MainSettings getSettings() throws ExecutionException {
        return settings;
    }
    
    @Override
    public MainAppData getData(String... sources) throws ExecutionException {
        return data;
    }
}
