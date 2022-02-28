package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WebMercatorTest {

    public static final double DELTA = 1e-7;

    @Test
    void xWorksOnNonTrivial1() {
        double expectedX1 = 0.579577472;
        assertEquals(expectedX1, WebMercator.x(0.5), DELTA);

        double expectedX2 = 0.6384648;
        assertEquals(expectedX2, WebMercator.x(0.87), DELTA);

    }

    @Test
    void yWorksOnNonTrivial2() {
        double expectedY1 = 0.4388668;
        assertEquals(expectedY1, WebMercator.y(0.375), DELTA);

        double expectedY2 = 0.28299351;
        assertEquals(expectedY2, WebMercator.y(1.07), DELTA);
    }
}
