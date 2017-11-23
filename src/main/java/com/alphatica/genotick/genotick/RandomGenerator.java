package com.alphatica.genotick.genotick;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

public class RandomGenerator implements Serializable {

    private static final long serialVersionUID = -32164662984L;

    private Random random;
    private long seed;
    
    private RandomGenerator(long requestedSeed) {
        String seedString = System.getenv("GENOTICK_RANDOM_SEED");
        if (seedString != null && !seedString.isEmpty()) {
            requestedSeed = Long.parseLong(seedString);
        }
        Random random;
        if (requestedSeed != 0) {
            random = new Random();
            random.setSeed(requestedSeed);
        } else {
            random = null;
        }
        seed = requestedSeed;
    }
    
    public long getSeed() {
        return seed;
    }

    public static RandomGenerator create(long requestedSeed) {
        return new RandomGenerator(requestedSeed);
    }
    
    public int nextInt() {
        if(random != null) {
            return random.nextInt();
        } else {
            return ThreadLocalRandom.current().nextInt();
        }
    }
    
    public int nextInt(int bound) {
        if(random != null) {
            return random.nextInt(bound);
        } else {
            return ThreadLocalRandom.current().nextInt(bound);
        }
    }

    public long nextLong() {
        if(random != null) {
            return random.nextLong();
        } else {
            return ThreadLocalRandom.current().nextLong();
        }
    }

    public double nextDouble() {
        if(random != null) {
            return random.nextDouble();
        } else {
            return ThreadLocalRandom.current().nextDouble();
        }
    }
    
    public boolean nextBoolean() {
        if(random != null) {
            return random.nextBoolean();
        } else {
            return ThreadLocalRandom.current().nextBoolean();
        }
    }
}
