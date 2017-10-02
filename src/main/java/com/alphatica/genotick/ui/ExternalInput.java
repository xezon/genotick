package com.alphatica.genotick.ui;

import com.alphatica.genotick.genotick.MainSettings;
import com.alphatica.genotick.genotick.MainInterface;

class ExternalInput extends BasicUserInput {

    @Override
    public MainSettings getSettings() {
        return MainInterface.getSettings();
    }
}
