package com.alphatica.genotick.genotick;

import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

public class RandomGenerator {

    private RandomGenerator() {}

    public static Random create(long seed) {
        String seedString = System.getenv("GENOTICK_RANDOM_SEED");
        if (seedString != null && !seedString.isEmpty()) {
            seed = Long.parseLong(seedString);
        }
        Random random;
        if (seed != 0) {
            random = new Random();
            random.setSeed(seed);
        } else {
            random = ThreadLocalRandom.current();
        }
        return random;
    }
}