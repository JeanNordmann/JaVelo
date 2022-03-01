package ch.epfl.javelo;

public final class Q28_4 {

    /**
     * Constructeur privé, car cette classe n'est pas censée être instantiable.
     */
    private Q28_4() {}

    /**
     *
     * @param i
     * @return la valeur Q28.4 correspondant à l'entier donné
     */

    public static int ofInt(int i){
        return i << 4;
    }

    /**
     *
     * @param q28_4
     * @return la valeur de type double égale à la valeur Q28.4 donnée
     */

    public static double asDouble(int q28_4) {

        return Math.scalb(q28_4,-4);
    }

    /**
     *
     * @param q28_4
     * @return la valeur de type float correspondant à la valeur Q28.4 donnée
     */

    public static float asFloat(int q28_4) {
        return Math.scalb(q28_4,-4);
    }
}
