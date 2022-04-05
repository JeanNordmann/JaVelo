package ch.epfl.javelo;

/**
 * 2.3.4
 * Q28_4
 *
 * Classe utile pour convertir des nombres entre la représentation Q28.4 et d'autres représentations.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */
public final class Q28_4 {

    /**
     * Constructeur privé, car cette classe n'est pas censée être instantiable.
     */

    private Q28_4() {}

    /**
     *
     * @param i Entier donné
     * @return La valeur en Q28.4 correspondant à l'entier donné
     */

    public static int ofInt(int i){
        return i << 4;
    }

    /**
     *
     * @param q28_4 Nombre de type double donné
     * @return La valeur de type double égale à la valeur Q28.4 donnée
     */

    public static double asDouble(int q28_4) {
        return Math.scalb((double)q28_4,-4);
    }

}
