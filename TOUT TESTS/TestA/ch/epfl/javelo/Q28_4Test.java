package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Q28_4Test {

    double DELTA = 1e-7;

    @Test
    void asDoubleWorkA() {
        double expected = 6.25;
        assertEquals(expected, Q28_4.asDouble(100),DELTA);
    }
    @Test
    void asFloatWorkA() {
        float expected = 6.25f;
        assertEquals(expected, Q28_4.asDouble(100),DELTA);
    }

    @Test
    public void ofIntOnKnownValuesA() {
        var actual1 = Q28_4.ofInt(1);
        var expected1 = 16;
        assertEquals(actual1, expected1);
    }

    @Test
    public void asDoubleOnKnownValuesA() {
        var actual1 = Q28_4.asDouble(1);
        var expected1 = 1.0/16.0;
        assertEquals(expected1, actual1);
    }

    @Test
    public void asFloatOnKnownValuesA() {
        var actual1 = Q28_4.asFloat(1);
        var expected1 = 1.0f/16.0f;
        assertEquals(expected1, actual1);
    }
}
