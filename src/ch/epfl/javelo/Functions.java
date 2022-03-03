package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * 2.3.5
 * Functions
 *
 * Classe contenant des méthodes permettant de créer des objets représentant des fonctions mathématiques des réels
 * vers les réels.
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public final class Functions {

    /**
     * Constructeur privé vide, car la classe n'est pas instantiable.
     */

    private Functions() {
    }

    public static DoubleUnaryOperator constant(double y) {
        return new Constant(y);
    }

    /**
     * Retourne une fonction obtenue par interpolation linéaire entre les échantillons samples, espacés
     * régulièrement et couvrant la plage allant de 0 à xMax ; lève IllegalArgumentException si le tableau
     * samples contient moins de deux éléments, ou si xMax est inférieur ou égal à 0.
     * @param samples Tableau d'échantillons desquels on veut faire l'interpolation.
     * @param xMax Valeur jusqu'à laquelle la plage est couverte.
     * @return
     */

    public static DoubleUnaryOperator sampled(float[] samples, double xMax) throws IllegalArgumentException {
        return new Sampled(samples, xMax);
    }

    private static final class Constant implements DoubleUnaryOperator {

        private final double constant;

        public Constant(double y) {
            this.constant = y;
        }

        @Override
        public double applyAsDouble(double x) {
            return constant;
        }

    }

    private static final class Sampled implements DoubleUnaryOperator {

        private final float[] samples;
        private final double xMax;

        public Sampled(float[] samples, double xMax) {
            this.samples = samples;
            this.xMax = xMax;
        }

        @Override
        public double applyAsDouble(double x) {
            Preconditions.checkArgument(samples.length >= 2 && xMax > 0);
            x = Math2.clamp(0, x, xMax);

            double spaceBetween2Points = xMax/(samples.length-1);
            int firstPoint = (int)Math.floor(x/spaceBetween2Points);
            int secondPoint = (int)Math.ceil(x/spaceBetween2Points);
            if (firstPoint == secondPoint) return samples[firstPoint];
            return Math2.interpolate(samples[firstPoint], samples[secondPoint], x/spaceBetween2Points - firstPoint);

        }
    }

}
