package com.alphatica.genotick.utility;

import java.util.function.Consumer;
import java.util.Objects;
import java.util.stream.IntStream;

public class ParallelTasks {

    private ParallelTasks() {}

    public static void prepareDefaultThreadPool() {
    	int poolCores = Math.max(Runtime.getRuntime().availableProcessors()*2, 2);
    	System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", Integer.toString(poolCores));
    }
    
    public static void parallelNumberedTask(int count, Consumer<Integer> action) {
        Objects.requireNonNull(action);
        int cores =  Math.max(2, Runtime.getRuntime().availableProcessors());
        int taskSize = (int)Math.ceil((double)count / (double)cores);
        IntStream.range(0, count)
            .filter(blockIndex -> blockIndex % taskSize == 0)
            .parallel()
            .forEach(blockIndex -> action.accept(Math.min(taskSize, count-blockIndex)));
    }
}

