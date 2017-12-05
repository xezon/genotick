package com.alphatica.genotick.utility;

import static java.lang.String.format;
import java.util.concurrent.TimeUnit;

public class TimeCounter {
    
    private final String name;
    private final boolean printOnStop;
    private long startNanoSeconds;
    private long stopNanoSeconds;
    
    public TimeCounter() {
        this("", true);
    }
    
    public TimeCounter(String name) {
        this(name, true);
    }
    
    public TimeCounter(String name, boolean printOnStop) {
        this.name = name;
        this.printOnStop = printOnStop;
        start();
    }
        
    public void start() {
        startNanoSeconds = System.nanoTime();
        stopNanoSeconds = 0;
    }
    
    public long stop() {
        return stop(TimeUnit.NANOSECONDS);
    }
    
    public long stop(TimeUnit timeUnit) {
        stopNanoSeconds = System.nanoTime();
        final long elapsedNanoSeconds = getElapsedNanoSeconds();
        if (printOnStop) {
            print(elapsedNanoSeconds, 2);
        }
        return timeUnit.convert(elapsedNanoSeconds, TimeUnit.NANOSECONDS);
    }
    
    public long getElapsedNanoSeconds() {
        final long stopTime = (0 == stopNanoSeconds) ? System.nanoTime() : stopNanoSeconds;
        return stopTime - startNanoSeconds;
    }
    
    public void print(long elapsedNanoSeconds) {
        print(elapsedNanoSeconds, 2);
    }
    
    private void print(long elapsedNanoSeconds, int methodDepth) {        
        final double elapsedSeconds      = (double)elapsedNanoSeconds / 1000000000.0;
        final double elapsedMilliseconds = (double)elapsedNanoSeconds / 1000000.0;
        final String name = this.name.isEmpty() ? MethodName.get(methodDepth) : this.name;
        System.out.println(format("Timer '%s' elapsed time in seconds: [%.3f] in milliseconds: [%.3f]",
                name, elapsedSeconds, elapsedMilliseconds));
    }
}
