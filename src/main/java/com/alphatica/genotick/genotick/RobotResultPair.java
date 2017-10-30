package com.alphatica.genotick.genotick;

import java.util.Objects;
import java.util.function.Consumer;

public class RobotResultPair {
    
    private final RobotResult originalResult;
    private final RobotResult reversedResult;
    
    RobotResultPair(RobotResult originalResult, RobotResult reversedResult) {
        Objects.requireNonNull(originalResult);
        this.originalResult = originalResult;
        this.reversedResult = reversedResult;
    }
    
    public void forEach(Consumer<? super RobotResult> action) {
        Objects.requireNonNull(action);
        action.accept(originalResult);
        if (reversedResult != null) {
            action.accept(reversedResult);
        }
    }
    
    public RobotResult getOriginal() {
        return originalResult;
    }
    
    public RobotResult getReversed() {
        return reversedResult;
    }
}
