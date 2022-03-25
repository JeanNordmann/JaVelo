package ch.epfl.javelo.routing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileTest {
    @Test
    void elevationProfileTConstructorThrowsOnIllegalArguments(){
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(-2, new float[]{2f, 3f});
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(3, new float[]{1f});
        });
        assertDoesNotThrow(() -> {
            new ElevationProfile(3, new float[]{1f, 2f, 3f});
        });
    }

    @Test
    void minAndMaxElevationWorksOnRandomValues() {
        float[] test = new float[]{356, 376, 456, 101, 3456};
        assertEquals(101, new ElevationProfile(3, test).minElevation());
        assertEquals(3456, new ElevationProfile(3, test).maxElevation());
    }

    @Test
    void totalAscentAndDescentWorkOnNonTrivialValues() {
        float[] test = new float[]{1, 10, 13, 11, 18, 2, 4};
        assertEquals(21, new ElevationProfile(55, test).totalAscent());
        assertEquals(18, new ElevationProfile(56, test).totalDescent());
    }

    @Test
    void elevationAtWorksProperly(){
        float[] test1 = new float[]{2, 3, 4, 12, 14} ;
        ElevationProfile elevationProfile1 = new ElevationProfile(4, test1);
        assertEquals(3.5, elevationProfile1.elevationAt(1.5));
        assertEquals(12, elevationProfile1.elevationAt(3));
        assertEquals(2, elevationProfile1.elevationAt(-4));

        float[] test2 = new float[]{1, 10, 13, 11, 18, 2, 4};
        assertEquals(12, new ElevationProfile(12, test2).elevationAt(5));
        assertEquals(4, new ElevationProfile(12, test2).elevationAt(345));
    }
}