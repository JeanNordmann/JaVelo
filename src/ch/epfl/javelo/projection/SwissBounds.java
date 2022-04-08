package ch.epfl.javelo.projection;

/**
 * 1.3.4
 * SwissBounds
 *
 * Définit des constantes et méthodes liées aux limites de la Suisse.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public final class SwissBounds {

    /**
     * Constructeur privé, car cette classe n'est pas censée être instantiable.
     */

    private SwissBounds() {}

    /**
     * Diverses constantes liées aux limites géographiques de la Suisse
     */

    public static final double MIN_E = 2485000;
    public static final double MAX_E = 2834000;
    public static final double MIN_N = 1075000;
    public static final double MAX_N = 1296000;
    public static final double WIDTH = MAX_E - MIN_E;
    public static final double HEIGHT = MAX_N - MIN_N;

    /**
     * Retourne vrai si et seulement si les coordonnées E et N données sont dans les
     * limites de la Suisse.
     * @param e Coordonnée E dans le système suisse
     * @param n Coordonnée N dans le système suisse
     * @return si les coordonnées sont bien dans les bordures suisses ou non.
     *
     */

    public static boolean containsEN(double e, double n) {
        return (e <= MAX_E && e >= MIN_E && n <= MAX_N && n >= MIN_N);
    }

}
