package com.alphatica.genotick.genotick;

import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

public class RandomGenerator {

    private static long SEED = 0;
    private RandomGenerator() {}

    public static Random create(long seed) {
        String seedString = System.getenv("GENOTICK_RANDOM_SEED");
        if (seedString != null && !seedString.isEmpty()) {
            seed = Long.parseLong(seedString);
        }
        SEED = seed;
        Random random = 0 != SEED ? new Random() : ThreadLocalRandom.current();
        if (seed != 0) {
            random.setSeed(seed);
        }
        return random;
    }
    
    public static Random get() {
	    return create(SEED);
    }
    
    public static long getSeed() {
	  		return SEED;
    }
}
