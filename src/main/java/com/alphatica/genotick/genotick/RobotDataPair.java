package com.alphatica.genotick.genotick;

import java.util.Objects;
import java.util.function.Consumer;

public class RobotDataPair {

    private final RobotData originalData;
    private final RobotData reversedData;
    
    RobotDataPair(RobotData originalData, RobotData reversedData) {
        Objects.requireNonNull(originalData);
        this.originalData = originalData;
        this.reversedData = reversedData;
    }
    
    public void forEach(Consumer<? super RobotData> action) {
        Objects.requireNonNull(action);
        action.accept(originalData);
        if (reversedData != null) {
            action.accept(reversedData);
        }
    }
    
    public RobotData getOriginal() {
        return originalData;
    }
    
    public RobotData getReversed() {
        return reversedData;
    }
}
