package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class FunctionsTest {
    @Test
    void constantDoesReturnConstant() {
        // try 0, -20, 19, 4, 20000
        double[] values = {0, -20.90, 19.4829, -4, 20000};
        for (double v : values) {
            DoubleUnaryOperator y = Functions.constant(v);
            double[] operands = {-1, -3.0, 0, 5.3290, 10, 20};
            for (double o : operands) {
                assertEquals(y.applyAsDouble(o), v);
            }
        }
    }

    @Test
    void sampledDoesThrowArgumentException() {
        float[] samples1 = {2};
        assertThrows(IllegalArgumentException.class, () -> {
            Functions.sampled(samples1, 10);
        });

        assertThrows(IllegalArgumentException.class, () -> {
           Functions.sampled(samples1, 0);
        });

        float[] samples2 = {2, 3, 4};
        assertThrows(IllegalArgumentException.class, () -> {
            Functions.sampled(samples2, 0);
        });
    }

    @Test
    void sampledWorksWithNiceValues() {
        float[] samples = {1, 2, 0, 1, 2, 1};
        float[] intervals = {0, 1, 2, 3, 4, 5};
        double xMax = 5;
        // intervals of 1 ==> xMax = 6
        DoubleUnaryOperator f = Functions.sampled(samples, xMax);

        for (int i = 0; i < samples.length; i++) {
            assertEquals(samples[i], f.applyAsDouble(intervals[i]), 10E-6);
        }

        double[] expectedValues = {1, 1.5, 1.75, 1, 0.4, 1.5, 1.5, 1};
        double[] xValues = {-10.5, 0.5, 0.75, 1.5, 1.8, 3.5, 4.5, 5.20};

        assertEquals(xValues.length, expectedValues.length);

        for (int i = 0; i < expectedValues.length; i++) {
           assertEquals(expectedValues[i], f.applyAsDouble(xValues[i]), TestManager.DOUBLE_DELTA);
        }
    }

    @Test
    void sampledWorksWithIdentity() {
        float[] samples = {0, 2, 4}; // same as intervals
        double xMax = 4;

        DoubleUnaryOperator f = Functions.sampled(samples, xMax);
        for (int i = 0; i < samples.length; i++) {
            assertEquals(samples[i], f.applyAsDouble(samples[i]), TestManager.DOUBLE_DELTA);
        }
    }
}