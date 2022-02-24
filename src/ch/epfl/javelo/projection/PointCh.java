package ch.epfl.javelo.projection;

/**
 * 1.3.7
 * PointCh
 *
 * Enregistrement représentant un point dans le système de coordonnées suisse.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public record PointCh(double e, double n) {

    /**
     * Constructeur compact levant une exception si les coordonnées fournies ne sont pas dans les limites suisses,
     * définies par SwissBounds.
     */

    public PointCh {
        if (!SwissBounds.containsEN(e, n)) throw new IllegalArgumentException();
    }

    /**
     *
     * @param that Deuxième point avec lequel il faut calculer la distance au carré depuis l'instance courante (this).
     * @return La distance au carré entre les deux points.
     */

    public double squaredDistanceTo(PointCh that) {
        return Math.pow(that.e - this.e, 2) + Math.pow(that.n - this.n, 2);
    }

    /**
     *
     * @param that Deuxième point avec lequel il faut calculer la distance depuis l'instance courante (this).
     * @return La distance entre les deux points.
     */

    public double distanceTo(PointCh that) {
        return Math.sqrt(squaredDistanceTo(that));
    }

    /**
     * @return La longitude du point, dans le système WGS84, en radians.
     */

    public double lon() {
        return Ch1903.lon(e, n);
    }

    /**
     * @return La latitude du point, dans le système WGS84, en radians.
     */

    public double lat() {
        return Ch1903.lat(e, n);
    }
}