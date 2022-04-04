package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

import java.util.Objects;

import static ch.epfl.javelo.Math2.norm;
import static ch.epfl.javelo.Math2.squaredNorm;

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
        Preconditions.checkArgument(SwissBounds.containsEN(e, n));
    }

    /**
     *
     * @param that Deuxième point avec lequel il faut calculer la distance au carré depuis l'instance courante (this).
     * @return La distance au carré entre les deux points.
     */

    public double squaredDistanceTo(PointCh that) {
        return squaredNorm(that.e - this.e,that.n - this.n);
    }

    /**
     *
     * @param that Deuxième point avec lequel il faut calculer la distance depuis l'instance courante (this).
     * @return La distance entre les deux points.
     */

    public double distanceTo(PointCh that) {
        return norm(that.e - this.e,that.n - this.n);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointCh pointCh = (PointCh) o;
        return Double.compare(pointCh.e, e) == 0 && Double.compare(pointCh.n, n) == 0;
    }


    @Override
    public int hashCode() {
        return Objects.hash(e, n);
    }
}