package com.alphatica.genotick.data;

public class Column {
    public static class TOHLC {
        public static final int
        TIME   = 0,
        OPEN   = 1,
        HIGH   = 2,
        LOW    = 3,
        CLOSE  = 4,
        OTHER  = 5;
    }

    public static class OHLC {
        public static final int
        OPEN   = 0,
        HIGH   = 1,
        LOW    = 2,
        CLOSE  = 3,
        OTHER  = 4;
    }

    public static class Names {
        public static String TOHLC[] = {
                "time",
                "open",
                "high",
                "low",
                "close",
                "other",
        };
        
        public static String OHLC[] = {
                "open",
                "high",
                "low",
                "close",
                "other",
        };
    }
    
    public static class Array {
        public static final int OHLC[] = {
                Column.OHLC.OPEN,
                Column.OHLC.HIGH,
                Column.OHLC.LOW,
                Column.OHLC.CLOSE
                };
    }
}
