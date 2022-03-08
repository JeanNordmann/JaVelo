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

    /**
     *
     * @param y Valeur constante désirée.
     * @return Une fonction constante, dont la valeur est toujours y.
     */

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

    /**
     * Classe privée Constant imbriquée dans la classe Functions
     */

    private static final class Constant implements DoubleUnaryOperator {
        /**
         * Attribut contenant la valeur de la constante de la fonction.
         */

        private final double constant;

        public Constant(double y) {
            this.constant = y;
        }

        /**
         *
         * @param x Valeur en abscisse de laquelle on souhaite obtenir l'ordonnée.
         * @return La valeur y correspondant à l'abscisse x de la fonction, dans ce cas une constante.
         */

        @Override
        public double applyAsDouble(double x) {
            return constant;
        }

    }

    /**
     * Classe privée Sampled imbriquée dans la classe Functions
     */

    private static final class Sampled implements DoubleUnaryOperator {

        /**
         * Attributs samples (tableau d'échantillons), et xMax (valeur maximale jusqu'à laquelle la
         * plage va).
         */

        private final float[] samples;
        private final double xMax;

        public Sampled(float[] samples, double xMax) {
            Preconditions.checkArgument(samples.length >= 2 && xMax > 0);
            this.samples = samples;
            this.xMax = xMax;
        }

        /**
         *
         * @param x Valeur en abscisse de laquelle on souhaite l'interpolation linéaire.
         * @return L'interpolation linéaire voulue, en fonction des points échantillonnés, répartis
         * régulièrement entre 0 et xMax.
         */

        @Override
        public double applyAsDouble(double x) {
            if(x >= xMax) return samples[samples.length-1];
            if(x <= 0) return samples[0];

            double spaceBetween2Points = xMax / (samples.length - 1);
            int firstPoint = (int)Math.floor(x/spaceBetween2Points);
            int secondPoint = (int)Math.ceil(x/spaceBetween2Points);

            if (firstPoint == secondPoint) return samples[firstPoint];

            return Math2.interpolate(samples[firstPoint], samples[secondPoint], x/spaceBetween2Points - firstPoint);

        }
    }

}
