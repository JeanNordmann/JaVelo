package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionsTest {
    private static final double DELTA = 1e-7;
    @Test
    void SampledWork1() {
        float[] tab = {2,3,1,2,1.5f,1,2.5f};
        double expected = 1.5;
        assertEquals(expected, Functions.sampled(tab,6).applyAsDouble(2.5));
        assertEquals(1.25, Functions.sampled(tab,6).applyAsDouble(4.5));
        assertEquals(2.5, Functions.sampled(tab,6).applyAsDouble(6));
        assertEquals(2, Functions.sampled(tab,6).applyAsDouble(0));
        assertEquals(2, Functions.sampled(tab,6).applyAsDouble(1.5));
        assertEquals(2, Functions.sampled(tab,6).applyAsDouble(3));
        assertEquals(1.5, Functions.sampled(tab,6).applyAsDouble(4));
    }

    @Test
    void SampledWork2() {
        float[] tab2 = {1,1.5f,3,1,2};
        assertEquals(1, Functions.sampled(tab2,6).applyAsDouble(0));
        assertEquals(1.5, Functions.sampled(tab2,6).applyAsDouble(1.5));
        assertEquals(3, Functions.sampled(tab2,6).applyAsDouble(3));
        assertEquals(1, Functions.sampled(tab2,6).applyAsDouble(4.5));
        assertEquals(2, Functions.sampled(tab2,6).applyAsDouble(6));
        assertEquals(2, Functions.sampled(tab2,6).applyAsDouble(2));
        assertEquals(2.5, Functions.sampled(tab2,6).applyAsDouble(2.5));
    }
    @Test
    void SampledWork3() {

        float[] tab = {0,3,1.5f,3.5f};
        assertEquals(0, Functions.sampled(tab,7.5).applyAsDouble(0), DELTA);
        assertEquals(3, Functions.sampled(tab,7.5).applyAsDouble(2.5), DELTA);
        assertEquals(1.5, Functions.sampled(tab,7.5).applyAsDouble(5), DELTA);
        assertEquals(3.5, Functions.sampled(tab,7.5).applyAsDouble(7.5), DELTA);
        assertEquals(2.7, Functions.sampled(tab,7.5).applyAsDouble(3), DELTA);
        assertEquals(2.3, Functions.sampled(tab,7.5).applyAsDouble(6), DELTA);
        assertEquals(3.1, Functions.sampled(tab,7.5).applyAsDouble(7),DELTA);
    }
}
