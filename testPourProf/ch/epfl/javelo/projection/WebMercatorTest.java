package ch.epfl.javelo.projection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class WebMercatorTest {

    private static final double DELTA = 1e-7;

    @Test
    void webMercatorXWorksOnKnownValues() {
        var actual1 = WebMercator.x(Math.toRadians(-180));
        var expected1 = 0.0;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = WebMercator.x(Math.toRadians(-90));
        var expected2 = 0.25;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = WebMercator.x(Math.toRadians(-45));
        var expected3 = 0.375;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = WebMercator.x(Math.toRadians(0));
        var expected4 = 0.5;
        assertEquals(expected4, actual4, DELTA);

        var actual5 = WebMercator.x(Math.toRadians(45));
        var expected5 = 0.625;
        assertEquals(expected5, actual5, DELTA);

        var actual6 = WebMercator.x(Math.toRadians(90));
        var expected6 = 0.75;
        assertEquals(expected6, actual6, DELTA);

        var actual7 = WebMercator.x(Math.toRadians(180));
        var expected7 = 1.0;
        assertEquals(expected7, actual7, DELTA);

        var actual8 = WebMercator.x(Math.toRadians(12.3456));
        var expected8 = 0.5342933333333334;
        assertEquals(expected8, actual8, DELTA);
    }

    @Test
    void webMercatorYWorksOnKnownValues() {
        var actual1 = WebMercator.y(Math.toRadians(-85));
        var expected1 = 0.9983620852139422;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = WebMercator.y(Math.toRadians(-45));
        var expected2 = 0.640274963084795;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = WebMercator.y(Math.toRadians(0));
        var expected3 = 0.5;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = WebMercator.y(Math.toRadians(45));
        var expected4 = 0.35972503691520497;
        assertEquals(expected4, actual4, DELTA);

        var actual5 = WebMercator.y(Math.toRadians(85));
        var expected5 = 0.0016379147860541708;
        assertEquals(expected5, actual5, DELTA);

        var actual6 = WebMercator.y(Math.toRadians(12.3456));
        var expected6 = 0.46543818316651964;
        assertEquals(expected6, actual6, DELTA);
    }

    @Test
    void webMercatorLonWorksOnKnownValues() {
        var actual1 = WebMercator.lon(0);
        var expected1 = -3.141592653589793;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = WebMercator.lon(0.25);
        var expected2 = -1.5707963267948966;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = WebMercator.lon(0.5);
        var expected3 = 0.0;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = WebMercator.lon(0.75);
        var expected4 = 1.5707963267948966;
        assertEquals(expected4, actual4, DELTA);

        var actual5 = WebMercator.lon(1);
        var expected5 = 3.141592653589793;
        assertEquals(expected5, actual5, DELTA);

        var actual6 = WebMercator.lon(0.123456);
        var expected6 = -2.36589572830663;
        assertEquals(expected6, actual6, DELTA);
    }

    @Test
    void webMercatorLatWorksOnKnownValues() {
        var actual1 = WebMercator.lat(0);
        var expected1 = 1.4844222297453324;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = WebMercator.lat(0.25);
        var expected2 = 1.1608753909688045;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = WebMercator.lat(0.5);
        var expected3 = 0.0;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = WebMercator.lat(0.75);
        var expected4 = -1.1608753909688045;
        assertEquals(expected4, actual4, DELTA);

        var actual5 = WebMercator.lat(1);
        var expected5 = -1.4844222297453324;
        assertEquals(expected5, actual5, DELTA);

        var actual6 = WebMercator.lat(0.123456);
        var expected6 = 1.3836144040217428;
        assertEquals(expected6, actual6, DELTA);
    }
}