package ch.epfl.javelo.routing;

import ch.epfl.javelo.TestManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileTest {
    @Test
    void constructorDoesThrowInvalidArgument() {
        assertThrows(IllegalArgumentException.class, () -> {
          ElevationProfile ep = new ElevationProfile(-1, new float[] {2f});
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile ep = new ElevationProfile(0, new float[] {});
        });


        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile ep = new ElevationProfile(-1, new float[] {2f, 1f, 3f});
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile ep = new ElevationProfile(0, new float[] {2f, 1f, 3f});
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile ep = new ElevationProfile(3, new float[] {2f});
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile ep = new ElevationProfile(3, new float[] {});
        });

        assertDoesNotThrow(() -> {
            ElevationProfile ep = new ElevationProfile(1, new float[] {2f, 1f});
        });
    }

    @Test
    void elevationProfileWorksWithExample() {
       float[] profiles =  new float[]{1.5f, 3f, 2.25f, -4.75f, 5f};
       ElevationProfile profile = new ElevationProfile(10, profiles);

       assertEquals(10, profile.length());
       assertEquals(-4.75, profile.minElevation());
       assertEquals(5, profile.maxElevation());
       assertEquals(11.25, profile.totalAscent());
       assertEquals(7.75, profile.totalDescent());

       assertEquals(1.5, profile.elevationAt(0), TestManager.DOUBLE_DELTA);
       assertEquals(5, profile.elevationAt(10), TestManager.DOUBLE_DELTA);
       assertEquals(3, profile.elevationAt(2.5), TestManager.DOUBLE_DELTA);
    }

}