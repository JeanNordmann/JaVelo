package ch.epfl.javelo;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Tolerance constants to simplify testing
 *
 * @author Youssef Boughizane (327665)
 * @author Julian Marmier (327410)
 */
public class TestManager {
    public static final double DOUBLE_DELTA = 10E-10;
    public static final float FLOAT_DELTA = 10E-6f;

    public static int generateRandomIntInBounds(double a, double b) {
        assert a <= b;
        return (int) Math.floor(Math.random() * (b - a + 1) + a);
    }

    public static double generateRandomDoubleInBounds(double a, double b) {
        assert a <= b;
        return (Math.random() * (b - a) + a);
    }

    public static int generateRandomEAsQ28_4() {
        return generateRandomIntInBounds(Q28_4.ofInt((int) SwissBounds.MIN_E), Q28_4.ofInt((int) SwissBounds.MAX_E));
    }

    public static int generateRandomNAsQ28_4() {
        return generateRandomIntInBounds(Q28_4.ofInt((int) SwissBounds.MIN_N), Q28_4.ofInt((int) SwissBounds.MAX_N));
    }


    public static PointCh offSetBy(double e , double n ){
        return new PointCh(SwissBounds.MIN_E + e , SwissBounds.MIN_N + n  );
    }
    @Test
    void testIntGenerator() {
        System.out.println(generateRandomIntInBounds(1, 10));
        System.out.println(generateRandomIntInBounds(30, 31));
        System.out.println(generateRandomIntInBounds(-10, -9));
    }

    public static Graph loadLausanne() {
        try {
            Graph g = Graph.loadFrom(Path.of("lausanne"));
            return g;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
