package ch.epfl.javelo.routing;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class ElevationProfileTest {

    @Test
    void ElevationProfileThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile evp1 = new ElevationProfile(0, new float[]{
                    100, 105, 120, 134, 112, 94
            });
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile evp1 = new ElevationProfile(-10, new float[]{
                    100, 105, 120, 134, 112, 94
            });
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile evp1 = new ElevationProfile(10, new float[]{
                    100,
            });
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile evp1 = new ElevationProfile(10, new float[]{});
        });
    }

    @Test
    void ElevationProfileWorkOnLength() {
        ElevationProfile evp1 = new ElevationProfile(10, new float[]{
                100, 105, 120, 134, 112, 94
        });
        assertEquals(10, evp1.length());

        ElevationProfile evp2 = new ElevationProfile(1398.23, new float[]{
                100, 105, 120, 134, 112, 94 , 1212, 0, 23892, 23, 3289,
                120, 134, 112, 94 , 1212, 0, 23892, 23, 3289, 287323, 232,
                1212, 0, 23892, 23, 3289, 100, 105, 120, 134, 112, 94 , 1212
        });
        assertEquals(1398.23, evp2.length());

        ElevationProfile evp = new ElevationProfile(1131, new float[]{
                100, 105, 120, 134, 112, 94
        });
        assertEquals(1131, evp.length());
    }

    @Test
    void ElevationProfileWorkOnMinElevation() {
        ElevationProfile evp1 = new ElevationProfile(10, new float[]{
                100, 105, 120, 134, 112, 94
        });
        assertEquals(94, evp1.minElevation());

        ElevationProfile evp2 = new ElevationProfile(1398.23, new float[]{
                100, 105, 120, 134, 112, 94 , 1212, 0, 23892, 23, 3289,
                120, 134, 112, 94 , 1212, 0, 23892, 23, 3289, 287323, 232,
                1212, 0, 23892, 23, 3289, 100, 105, 120, 134, 112, 94 , 1212
        });
        assertEquals(0, evp2.minElevation());

        ElevationProfile evp = new ElevationProfile(1131, new float[]{
                100, 105, 120, 4334, 112, 94, 23
        });
        assertEquals(23, evp.minElevation());
    }

    @Test
    void ElevationProfileWorkOnMaxElevation() {
        ElevationProfile evp1 = new ElevationProfile(10, new float[]{
                100, 105, 120, 134, 112, 94
        });
        assertEquals(134, evp1.maxElevation());

        ElevationProfile evp2 = new ElevationProfile(1398.23, new float[]{
                100, 105, 120, 134, 112, 94 , 1212, 0, 23892, 23, 3289,
                120, 134, 112, 94 , 1212, 0, 23892, 23, 3289, 287323, 232,
                1212, 0, 23892, 23, 3289, 100, 105, 120, 134, 112, 94 , 1212
        });
        assertEquals(287323, evp2.maxElevation());

        ElevationProfile evp = new ElevationProfile(1131, new float[]{
                100, 105, 120, 4334, 112, 94
        });
        assertEquals(4334, evp.maxElevation());
    }

    @Test
    void ElevationProfileWorkOnTotalAscent() {
        ElevationProfile evp1 = new ElevationProfile(10, new float[]{
                100, 105, 120, 134, 112, 94
        });
        assertEquals(34, evp1.totalAscent());

        ElevationProfile evp2 = new ElevationProfile(1398.23, new float[]{
                100, 105, 120, 134, 112, 94 , 1212, 0, 23892, 23, 3289,
                120, 134, 112, 94 , 1212, 0, 23892, 23, 3289, 287323, 232,
                1212, 0, 23892, 23, 3289, 100, 105, 120, 134, 112, 94 , 1212
        });
        assertEquals(369924, evp2.totalAscent());

        ElevationProfile evp = new ElevationProfile(1131, new float[]{
                100, 105, 120, 4334, 112, 200, 115, 140, 94
        });
        assertEquals(4347, evp.totalAscent());
    }

    @Test
    void ElevationProfileWorkOnTotalDescent() {
        ElevationProfile evp1 = new ElevationProfile(10, new float[]{
                100, 105, 120, 134, 112, 94
        });
        assertEquals(40, evp1.totalDescent());

        ElevationProfile evp2 = new ElevationProfile(1398.23, new float[]{
                100, 105, 120, 134, 112, 94 , 1212, 0, 23892, 23, 3289,
                120, 134, 112, 94 , 1212, 0, 23892, 23, 3289, 287323, 232,
                1212, 0, 23892, 23, 3289, 100, 105, 120, 134, 112, 94 , 1212
        });
        assertEquals(368812, evp2.totalDescent());

        ElevationProfile evp = new ElevationProfile(1131, new float[]{
                100, 105, 120, 4334, 112, 200, 115, 140, 94
        });
        assertEquals(4353, evp.totalDescent());
    }

    @Test
    void ElevationProfileWorkOnElevationAt() {
        ElevationProfile evp1 = new ElevationProfile(10, new float[]{
                100, 105, 120, 134, 112, 94
        });
        assertEquals(100, evp1.elevationAt(-12));
        assertEquals(100, evp1.elevationAt(-1));
        assertEquals(100, evp1.elevationAt(0));
        assertEquals(127, evp1.elevationAt(5));
        assertEquals(120, evp1.elevationAt(4));
        assertEquals(123, evp1.elevationAt(7));
        assertEquals(94, evp1.elevationAt(10));
        assertEquals(94, evp1.elevationAt(100));
        assertEquals(94, evp1.elevationAt(1000));
    }
}