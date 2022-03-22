package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.Objects;

import static java.lang.Double.POSITIVE_INFINITY;

/**
 * 4.3.5
 * ElevationProfile
 *
 * Enregistrement offrant une gestion du profil des arêtes avec un tableau de float et la longueur du segment.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */


public record RoutePoint(PointCh point, double position, double distanceToReference) {

    public static final RoutePoint NONE = new RoutePoint(null, Double.NaN, POSITIVE_INFINITY);

    /**
     *
     * @param positionDifference différence de position
     * @return un point identique au récepteur (this) mais dont la position est décalée de la différence donnée,
     * qui peut être positive ou négative.
     */
    public RoutePoint withPositionShiftedBy(double positionDifference){
        return new RoutePoint(this.point, position + positionDifference, distanceToReference);
    }

    /**
     *
     * @param that RoutePoint avec le quel on veut comparer
     * @return this si sa distance à la référence est inférieure ou égale à celle de that, et that sinon
     */
    public RoutePoint min(RoutePoint that){
        return this.distanceToReference <= that.distanceToReference() ? this : that;
    }

    /**
     *
     * @param thatPoint pointCh à comparer
     * @param thatPosition position
     * @param thatDistanceToReference distance de reference
     * @return this si sa distance à la référence est inférieure ou égale à thatDistanceToReference,
     * et une nouvelle instance de RoutePoint dont les attributs sont les arguments passés à min sinon.
     */

    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference){
        return this.distanceToReference <= thatDistanceToReference ? this :
                new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoutePoint that = (RoutePoint) o;
        return Double.compare(that.position, position) == 0 && Double.compare(that.distanceToReference, distanceToReference) == 0 && Objects.equals(point, that.point);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point, position, distanceToReference);
    }
}
