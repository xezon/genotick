package com.alphatica.genotick.utility;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    
    private static String getCurrentDateTimeString(String format) {
        return new SimpleDateFormat(format, Locale.US).format(new Date());
    }
    
    public static String generateCommonIdentifier() {
        String time = Tools.getCurrentDateTimeString("yyyyMMdd'T'HHmmssSSS");
        String process = Tools.getProcessIdString();
        String thread = getThreadIdString();
        return "p" + process + "_t" + thread + "_" + time;
    }
}
