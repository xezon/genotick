package com.alphatica.genotick.utility;

public class Assert {
    public static void gassert(boolean condition) {
        if (!condition)
            throw new AssertionError();
    }
    
    public static void gassert(boolean condition, Object message) {
        if (!condition)
            throw new AssertionError(message);
    }
}
