package ch.epfl.javelo;

import java.util.Arrays;
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

    private Functions() {}

    /**
     * Retourne une fonction constante, dont la valeur est toujours y.
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
     * @return une fonction obtenue par interpolation linéaire entre les échantillons samples, espacés
     * régulièrement
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
         * Retourne la valeur constante y correspondant à l'abscisse x de la fonction.
         * @param x Valeur en abscisse de laquelle on souhaite obtenir l'ordonnée.
         * @return La valeur y correspondant à l'abscisse x de la fonction, dans ce cas une constante.
         */

        @Override
        public double applyAsDouble(double x) {
            return constant;
        }

        //Pour comparer des Constants dans les tests
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Constant constant1 = (Constant) o;
            return Double.compare(constant1.constant, constant) == 0;
        }
    }

    /**
     * Classe privée Sampled imbriquée dans la classe Functions.
     */

    private static final class Sampled implements DoubleUnaryOperator {

        /**
         * Attributs samples (tableau d'échantillons), et xMax (valeur maximale jusqu'à laquelle la
         * plage va).
         */

        private final float[] samples;
        private final double xMax;

        /**
         * Constructeur initialisant ses attributs (tableau d'échantillons et valeur maximale de la plage).
         * @param samples Tableau d'échantillons.
         * @param xMax Valeur maximale de la plage.
         */

        public Sampled(float[] samples, double xMax) {
            Preconditions.checkArgument(samples.length >= 2 && xMax > 0);
            this.samples = samples;
            this.xMax = xMax;
        }

        /**
         * Retourne l'interpolation linéaire voulue, en fonction des points échantillonnés, répartis
         * régulièrement entre 0 et xMax.
         * @param x Valeur en abscisse de laquelle on souhaite l'interpolation linéaire.
         * @return L'interpolation linéaire voulue, en fonction des points échantillonnés, répartis
         * régulièrement entre 0 et xMax.
         */

        @Override
        public double applyAsDouble(double x) {
            //Prend la première ou dernière valeur, en fonction des bornes.
            if(x >= xMax) return samples[samples.length-1];
            if(x <= 0) return samples[0];

            //Calcule l'espacement entre les points, ainsi que les bornes
            //d'interpolation.
            double spaceBetween2Points = xMax / (samples.length - 1);
            int firstPoint = (int)Math.floor(x/spaceBetween2Points);
            int secondPoint = (int)Math.ceil(x/spaceBetween2Points);

            //Cas particulier ou les points sont égaux.
            if (firstPoint == secondPoint) return samples[firstPoint];

            return Math2.interpolate(samples[firstPoint], samples[secondPoint], x/spaceBetween2Points - firstPoint);

        }

        //Pour comparer des Sampled dans les tests
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Sampled sampled = (Sampled) o;
            return Double.compare(sampled.xMax, xMax) == 0 && Arrays.equals(samples, sampled.samples);
        }
    }



}
