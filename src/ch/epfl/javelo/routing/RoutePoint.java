package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.Objects;

import static java.lang.Double.POSITIVE_INFINITY;

/**
 * 4.3.5
 * ElevationProfile
 * <p>
 * Enregistrement offrant une gestion du profil des arêtes avec un tableau de float et la
 * longueur du segment.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */


public record RoutePoint(PointCh point, double position, double distanceToReference) {

    //Constante représentant un RoutePoint nul. (Placé nulle part, à une distance n'existant
    //pas, et avec une distance vers la référence infinie).
    public static final RoutePoint NONE = new RoutePoint(null, Double.NaN, POSITIVE_INFINITY);

    /**
     * Retourne un point identique au récepteur (this) mais dont la position est décalée de la différence donnée,
     * qui peut être positive ou négative.
     * @param positionDifference Différence de position.
     * @return Un point identique au récepteur (this) mais dont la position est décalée de la différence donnée,
     * qui peut être positive ou négative.
     */

    public RoutePoint withPositionShiftedBy(double positionDifference) {
        return positionDifference == 0 ? this : new RoutePoint(this.point,
                position + positionDifference, distanceToReference);
    }

    /**
     * Retourne this si sa distance à la référence est inférieure ou égale à celle de that, et that sinon.
     * @param that RoutePoint avec le quel on veut comparer.
     * @return this si sa distance à la référence est inférieure ou égale à celle de that, et that sinon.
     */

    public RoutePoint min(RoutePoint that) {
        return this.distanceToReference <= that.distanceToReference() ? this : that;
    }

    /**
     * Retourne this si sa distance à la référence est inférieure ou égale à
     * thatDistanceToReference, et une nouvelle instance de RoutePoint dont les attributs sont
     * les arguments passés à min sinon.
     * @param thatPoint PointCh à comparer.
     * @param thatPosition Position.
     * @param thatDistanceToReference Distance de référence.
     * @return this si sa distance à la référence est inférieure ou égale à thatDistanceToReference,
     * et une nouvelle instance de RoutePoint dont les attributs sont les arguments passés à
     * min sinon.
     */

    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {
        return this.distanceToReference <= thatDistanceToReference ? this :
                new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }
}
