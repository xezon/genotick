package com.alphatica.genotick.genotick;

import java.util.Random;

public class RandomGenerator {

    private RandomGenerator() {}

    public static Random create(long seed) {
        String seedString = System.getenv("GENOTICK_RANDOM_SEED");
        if (seedString != null && !seedString.isEmpty()) {
            seed = Long.parseLong(seedString);
        }
        Random random = new Random();
        if (seed != 0) {
            random.setSeed(seed);
        }
        return random;
    }
}
