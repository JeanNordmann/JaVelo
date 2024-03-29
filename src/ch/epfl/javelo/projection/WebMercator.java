package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * 2.3.1
 * WebMercator
 * <p>
 * Classe utile pour convertir entre les coordonnées WGS 84 et les coordonnées Web Mercator.
 * (WebMercator ne valident pas leurs arguments, ce travail étant laissé aux classes
 * représentant les points).
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
     * Retourne la coordonnée x de la projection d'un point se trouvant à la longitude lon,
     * donnée en radians.
     * @param lon Longitude en radians.
     * @return La coordonnée x de la projection d'un point se trouvant à la longitude lon,
     * donnée en radians.
     */
    public static double x(double lon) {
        return (1 / (2 * Math.PI)) * (lon + Math.PI);
    }

    /**
     * Retourne la coordonnée y de la projection d'un point se trouvant à la latitude lat,
     * donnée en radians.
     * @param lat Latitude en radians.
     * @return La coordonnée y de la projection d'un point se trouvant à la latitude lat,
     * donnée en radians.
     */

    public static double y(double lat) {
        return (1 / (Math.PI * 2)) * (Math.PI - Math2.asinh(Math.tan(lat)));
    }

    /**
     * Retourne la longitude, en radians, d'un point dont la projection se trouve à la coordonnée
     * x donnée.
     * @param x Coordonnée x du point.
     * @return La longitude, en radians, d'un point dont la projection se trouve à la coordonnée
     * x donnée.
     */
    public static double lon(double x) {
        return 2 * Math.PI * x - Math.PI;
    }

    /**
     * Retourne la latitude, en radians, d'un point dont la projection se trouve à la coordonnée
     * y donnée.
     * @param y Coordonnée y du point.
     * @return La latitude, en radians, d'un point dont la projection se trouve à la coordonnée
     * y donnée.
     */

    public static double lat(double y) {
        return Math.atan(Math.sinh(Math.PI - 2 * Math.PI * y));
    }
}
