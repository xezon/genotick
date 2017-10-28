package com.alphatica.genotick.ui;

import com.alphatica.genotick.genotick.MainSettings;

class DefaultInputs extends BasicUserInput {

    DefaultInputs(UserOutput output) {
        super(output);
    }
    
    @Override
    public MainSettings getSettings() {
        return MainSettings.getSettings();
    }
}
