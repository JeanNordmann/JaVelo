package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Q28_4Test {

    double DELTA = 1e-7;

    @Test
    void ofIntWork() {
        int expected = 1;
        int expected1 = 3;

        assertEquals(expected, Q28_4.ofInt(16),DELTA);
        assertEquals(expected, Q28_4.ofInt(17),DELTA);
        assertEquals(expected, Q28_4.ofInt(31),DELTA);
        assertEquals(expected1, Q28_4.ofInt(50),DELTA);
    }


    @Test
    void asDoubleWork() {
        double expected = 6.25;
        assertEquals(expected, Q28_4.asDouble(100),DELTA);
    }
    @Test
    void asFloatWork() {
        float expected = 6.25f;
        assertEquals(expected, Q28_4.asDouble(100),DELTA);
    }
}
