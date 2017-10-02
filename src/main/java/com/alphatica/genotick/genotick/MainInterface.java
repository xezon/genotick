package com.alphatica.genotick.genotick;

import java.io.IOException;

public class MainInterface {
    private static final int INTERFACE_VERSION = 1;
    private static MainSettings settings = MainSettings.getSettings();
    
    public static int getInterfaceVersion() {
        return INTERFACE_VERSION;
    }
    
    public static int start(String[] args) throws IOException, IllegalAccessException {
        System.out.println("Starting with arguments:");
        for (String arg : args) {
            System.out.println(arg);
        }
        final ErrorCode error = Main.init(args);
        return error.getValue();
    }
        
    public static MainSettings getSettings() {        
        return settings;
    }
}
