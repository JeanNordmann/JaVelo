package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * 2.3.5
 * Functions
 * Classe contenant des méthodes permettant de créer des objets représentant des fonctions mathématiques des réels
 * vers les réels.
 */

public final class Functions {

    /**
     * Constructeur privé vide, car la classe n'est pas instantiable.
     */

    private Functions() {
    }

/*    public static DoubleUnaryOperator constant(double y) {
        return Constant(y);
    }*/

    private static final class Constant implements DoubleUnaryOperator {

        private double constant;

        public Constant(double y) {
            this.constant = y;
        }

        @Override
        public double applyAsDouble(double x) {
            return x;
        }

    }

}
