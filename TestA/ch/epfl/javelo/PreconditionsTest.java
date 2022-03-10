package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PreconditionsTest {
    @Test
    void checkArgumentSucceedsForTrueA() {
        assertDoesNotThrow(() -> {
            Preconditions.checkArgument(true);
        });
    }

    @Test
    void checkArgumentThrowsForFalseA() {
        assertThrows(IllegalArgumentException.class, () -> {
            Preconditions.checkArgument(false);
        });
    }
}
