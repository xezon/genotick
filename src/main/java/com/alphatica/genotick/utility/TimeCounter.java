package com.alphatica.genotick.utility;

import com.alphatica.genotick.ui.UserInputOutputFactory;
import com.alphatica.genotick.ui.UserOutput;
import static java.lang.String.format;

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
        stopNanoSeconds = System.nanoTime();
        final long elapsedNanoSeconds = getElapsedNanoSeconds();
        if (printOnStop) {
            print(elapsedNanoSeconds, 2);
        }
        return elapsedNanoSeconds;
    }
    
    public long getElapsedNanoSeconds() {
        final long stopTime = (0 == stopNanoSeconds) ? System.nanoTime() : stopNanoSeconds;
        return stopTime - startNanoSeconds;
    }
    
    public void print(long elapsedNanoSeconds) {
        print(elapsedNanoSeconds, 2);
    }
    
    private void print(long elapsedNanoSeconds, int methodDepth) {        
        UserOutput output = UserInputOutputFactory.getUserOutput();
        final double elapsedSeconds = (double)elapsedNanoSeconds / 1000000000.0;
        final double elapsedMilliseconds = (double)elapsedNanoSeconds / 1000.0;
        final String name = this.name.isEmpty() ? MethodName.get(methodDepth) : this.name;
        output.infoMessage(format("Timer '%s' elapsed time in seconds: [%.3f] in milliseconds: [%.3f]",
                name, elapsedSeconds, elapsedMilliseconds));
    }
}
