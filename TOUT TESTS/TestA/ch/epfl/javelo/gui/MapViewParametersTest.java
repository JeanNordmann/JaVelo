package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MapViewParametersTest {
// Ces test ne sont pas très exaustif et qualitatof, c'est simplement un moyen rapide de vérifier
// que avec d'autres gens nous avons codé les méthodes de la même manière !
    //
    private final double DELTA = 1E-5;
    @Test
    void MapViewParametersThrowsIAE() {

        assertThrows(IllegalArgumentException.class, () -> {
            MapViewParameters m = new MapViewParameters(-1, 0, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            MapViewParameters m = new MapViewParameters(0, -1, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            MapViewParameters m = new MapViewParameters(0, 0, -1);
        });
            MapViewParameters m = new MapViewParameters(0, 0, 0);
    }

    @Test
    void topLeftWorks() {
        assertEquals(new Point2D(2,8),new MapViewParameters(84,2,8).topLeft());
    }

    @Test
    void withMinXYWorks() {
        MapViewParameters init = new MapViewParameters(32, 20, 40);
        assertEquals(new MapViewParameters(32, 60, 60), init.withMinXY(60, 60));
    }

    @Test
    void pointAtWork() {
        MapViewParameters init = new MapViewParameters(2, 0, 1);
        assertEquals(new PointWebMercator(4.8828125E-4,0.00146484375),
                init.pointAt(0.5, 0.5));
    }

    @Test
    void viewX() {
        MapViewParameters init = new MapViewParameters(1, 0.5, 0.5);
        PointWebMercator p = new PointWebMercator(0.1, 0.1);
        assertEquals(50.7,init.viewX(p));
    }
    @Test
    void viewY() {
        MapViewParameters init = new MapViewParameters(1, 0.6, 0.7);
        PointWebMercator p = new PointWebMercator(0.1, 0.1);
        assertEquals(50.5,init.viewY(p));
    }
}
