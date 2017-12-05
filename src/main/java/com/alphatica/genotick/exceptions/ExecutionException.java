package com.alphatica.genotick.exceptions;

public class ExecutionException extends RuntimeException {
    private static final long serialVersionUID = 8573014540753638906L;

    public ExecutionException(String s) {
        super(s);
    }
    
    public ExecutionException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
