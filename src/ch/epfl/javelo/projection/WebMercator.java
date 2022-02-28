package ch.epfl.javelo.projection;

/**
 * 2.3.1
 * WebMercator
 *
 * (WebMercator ne valident pas leurs arguments, ce travail étant laissé aux classes représentant les points.)
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public final class WebMercator {

    /**
     * Constructeur privé, car cette classe n'est pas censée être instantiable.
     */

    private WebMercator() {}

    /**
     *
     * @param lon
     * @return la coordonnée x de la projection d'un point se trouvant à la longitude lon, donnée en radians.
     */
    public static double x(double lon) {
        return (1/(2*Math.PI)) * (lon + Math.PI);
    }

    /**
     *
     * @param lat
     * @return la coordonnée y de la projection d'un point se trouvant à la latitude lat, donnée en radians.
     */
    double y(double lat){
        return 0;
    }

    /**
     *
     * @param x
     * @return la longitude, en radians, d'un point dont la projection se trouve à la coordonnée x donnée.
     */
    public static double lon(double x) {
        return 2*Math.PI*x - Math.PI;
    }

    /**
     *
     * @param y
     * @return la latitude, en radians, d'un point dont la projection se trouve à la coordonnée y donnée.
     */

    public static double lat(double y) {
        return Math.atan(Math.sinh(Math.PI - 2*Math.PI*y));
    }
}
