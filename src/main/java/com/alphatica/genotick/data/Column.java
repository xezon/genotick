package com.alphatica.genotick.data;

public class Column {
    public static class TOHLCV {
        public static final int
        TIME   = 0,
        OPEN   = 1,
        HIGH   = 2,
        LOW    = 3,
        CLOSE  = 4,
        VOLUME = 5,
        OTHER  = 6;
    }

    public static class OHLCV {
        public static final int
        OPEN   = 0,
        HIGH   = 1,
        LOW    = 2,
        CLOSE  = 3,
        VOLUME = 4;
    }

    public static class Names {
        public static String TOHLCV[] = {
                "time",
                "open",
                "high",
                "low",
                "close",
                "volume",
                "other",
        };
        
        public static String OHLCV[] = {
                "open",
                "high",
                "low",
                "close",
                "volume",
        };
    }
    
    public static class Array {
        public static final int OHLC[] = {
                Column.OHLCV.OPEN,
                Column.OHLCV.HIGH,
                Column.OHLCV.LOW,
                Column.OHLCV.CLOSE
                };
    }
}
