package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SwissBoundsTest {
    @Test
    void swissBoundsAreCorrect() {
        assertEquals(2_485_000, SwissBounds.MIN_E);
        assertEquals(2_834_000, SwissBounds.MAX_E);
        assertEquals(1_075_000, SwissBounds.MIN_N);
        assertEquals(1_296_000, SwissBounds.MAX_N);
        assertEquals(349000.0, SwissBounds.WIDTH);
        assertEquals(221000.0, SwissBounds.HEIGHT);
    }
}