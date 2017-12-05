package com.alphatica.genotick.ui;

import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.genotick.MainSettings;

public interface UserInput {
    MainSettings getSettings();
    MainAppData getData(String... sources);
}
