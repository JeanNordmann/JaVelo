package ch.epfl.test;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public final class TestRandomizer {
    // Fix random seed to guarantee reproducibility.
    public final static long SEED = 2022;

    public final static int RANDOM_ITERATIONS = 1_000;

    public static RandomGenerator newRandom() {
        return RandomGeneratorFactory.getDefault().create(SEED);
    }
}
