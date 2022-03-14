package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

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
     * @param positionDifference
     * @return un point identique au récepteur (this) mais dont la position est décalée de la différence donnée,
     * qui peut être positive ou négative.
     */
    public RoutePoint withPositionShiftedBy(double positionDifference){
        return new RoutePoint(this.point, position + positionDifference, distanceToReference);
    }

    /**
     *
     * @param that
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
}
