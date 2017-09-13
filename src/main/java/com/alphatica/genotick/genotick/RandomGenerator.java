package com.alphatica.genotick.genotick;

import java.util.Random;

public class RandomGenerator {

    private static long SEED = 0;

    private RandomGenerator() {}

    public static void suggestSeed(long seed) {
        String seedString = System.getenv("GENOTICK_RANDOM_SEED");
        if (seedString != null && !seedString.isEmpty()) {
            seed = Long.parseLong(seedString);
        }
        SEED = seed;
    }

    public static Random get() {
        Random random = new Random();
        if (0 != SEED) {
            random.setSeed(SEED);
        }
        return random;
    }
}
