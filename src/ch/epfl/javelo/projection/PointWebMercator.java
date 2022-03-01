package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * 2.3.2
 * PointWebMercator
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public record PointWebMercator(double x, double y) {

    /**
     * Constructeur
     */

    public PointWebMercator {
        Preconditions.checkArgument((0<=x&&x<=1)&&(0<=y&&y<=1));
    }

    /**
     *
     * @param zoomLevel niveaux de zoom de 0 à 19
     * @param x coordonnée x
     * @param y coordonnée y
     * @return le point dont les coordonnées sont x et y au niveau de zoom zoomLevel
     */

    public static PointWebMercator PointWebMercatorof(int zoomLevel, double x, double y) {
        Preconditions.checkArgument(0<=zoomLevel&&zoomLevel<=20);
        return new PointWebMercator( Math.scalb(x, -8 - zoomLevel), Math.scalb(y, -8 - zoomLevel));
    }

    /**
     *
     * @param pointCh point dans le système de coordonnées suisse
     * @return le point Web Mercator correspondant au point du système de coordonnées suisse donné.
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {
        return new PointWebMercator(WebMercator.x(pointCh.lon()),WebMercator.y(pointCh.lat()));
    }

    /**
     *
     * @param zoomLevel niveaux de zoom de 0 à 19
     * @return la coordonnée x au niveau de zoom donné
     */
    public double xAtZoomLevel(int zoomLevel) {
        Preconditions.checkArgument(0<=zoomLevel&&zoomLevel<=20);
        return Math.scalb(x, 8 + zoomLevel);
    }

    /**
     *
     * @param zoomLevel niveaux de zoom de 0 à 19
     * @return la coordonnée y au niveau de zoom donné
     */
    public double yAtZoomLevel(int zoomLevel) {
        Preconditions.checkArgument(0<=zoomLevel&&zoomLevel<=20);
        return Math.scalb(y, 8 + zoomLevel);
    }

    /**
     *
     * @return la longitude du point, en radians
     */
    public double lon() {
        return WebMercator.lon(x);
    }

    /**
     *
     * @return  la latitude du point, en radians
     */
    public double lat() {
        return WebMercator.lat(y);
    }

    /**
     *
     * @return le point de coordonnées suisses se trouvant à la même position que le récepteur (this)
     * ou null si ce point n'est pas dans les limites de la Suisse définies par SwissBounds.
     */
    public PointCh toPointCh() {
        if (!SwissBounds.containsEN(Ch1903.e(lon(),lat()),Ch1903.n(lon(),lat()))) return null;
        return new PointCh(Ch1903.e(lon(),lat()),Ch1903.n(lon(),lat()));
    }
}