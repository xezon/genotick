package com.alphatica.genotick.utility;

import java.lang.management.ManagementFactory;

public class Tools {

    private Tools() {}

    private static String getProcessIdString() {
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        if (pid.contains("@")) {
            return pid.substring(0, pid.indexOf('@'));
        } else {
            return pid;
        }
    }
    
    private static String getThreadIdString() {
        long threadId = Thread.currentThread().getId();
        return String.valueOf(threadId);
    }
    
    public static String getProcessThreadIdString() {
        return getProcessIdString() + "_" + getThreadIdString();
    }
}
