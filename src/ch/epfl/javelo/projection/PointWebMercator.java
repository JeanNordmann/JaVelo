package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * 2.3.2
 * PointWebMercator
 *
 * Classe utile pour représenter un point dans le système Web Mercator
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public record PointWebMercator(double x, double y) {

    /**
     * Constructeur vérifiant si les coordonnées données sont entre 0 et 1.
     */

    public PointWebMercator {
        Preconditions.checkArgument(0 <= x && x <= 1 && 0 <= y && y <= 1);
    }

    /**
     * Retourne le point dont les coordonnées sont x et y au niveau de zoom zoomLevel.
     * @param zoomLevel Niveaux de zoom allant de 0 à 19.
     * @param x Coordonnée x.
     * @param y Coordonnée y.
     * @return Le point dont les coordonnées sont x et y au niveau de zoom zoomLevel.
     */

    public static PointWebMercator of(int zoomLevel, double x, double y) {
        Preconditions.checkArgument(0 <= zoomLevel && zoomLevel <= 20);
        return new PointWebMercator(Math.scalb(x, -8 - zoomLevel), Math.scalb(y, -8 - zoomLevel));
    }

    /**
     * Retourne le point Web Mercator correspondant au point du système de coordonnées suisse donné.
     * @param pointCh Point dans le système de coordonnées suisse.
     * @return Le point Web Mercator correspondant au point du système de coordonnées suisse donné.
     */

    public static PointWebMercator ofPointCh(PointCh pointCh) {
        return new PointWebMercator(WebMercator.x(pointCh.lon()), WebMercator.y(pointCh.lat()));
    }

    /**
     * Retourne la coordonnée x au niveau de zoom donné en paramètres.
     * @param zoomLevel niveaux de zoom de 0 à 19.
     * @return la coordonnée x au niveau de zoom donné.
     */

    public double xAtZoomLevel(int zoomLevel) {
        Preconditions.checkArgument(0 <= zoomLevel && zoomLevel <= 20);
        return Math.scalb(x, 8 + zoomLevel);
    }

    /**
     * Retourne la coordonnée y au niveau de zoom donné en paramètres.
     * @param zoomLevel niveaux de zoom de 0 à 19
     * @return La coordonnée y au niveau de zoom donné
     */

    public double yAtZoomLevel(int zoomLevel) {
        Preconditions.checkArgument(0 <= zoomLevel && zoomLevel <= 20);
        return Math.scalb(y, 8 + zoomLevel);
    }

    /**
     * Retourne la longitude du point, en radians.
     * @return La longitude du point, en radians.
     */

    public double lon() {
        return WebMercator.lon(x);
    }

    /**
     * Retourne la latitude du point, en radians.
     * @return La latitude du point, en radians.
     */

    public double lat() {
        return WebMercator.lat(y);
    }

    /**
     * Retourne le point de coordonnées suisses se trouvant à la même position que le récepteur (this)
     * ou null si ce point n'est pas dans les limites de la Suisse définies par la classe SwissBounds.
     * @return Le point de coordonnées suisses se trouvant à la même position que le récepteur (this)
     * ou null si ce point n'est pas dans les limites de la Suisse définies par SwissBounds.
     */

    public PointCh toPointCh() {
        if (!SwissBounds.containsEN(Ch1903.e(lon(),lat()),Ch1903.n(lon(),lat()))) return null;
        return new PointCh(Ch1903.e(lon(),lat()),Ch1903.n(lon(),lat()));
    }
}
