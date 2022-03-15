package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

/**
 * 4.3.6
 * Route
 *
 * Interface route représentant un itinéraire. Implémentée par deux classes
 * écrites ultérieurement.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public interface Route {

    /**
     * @param position Position donnée.
     * @return Retourne l'index du segment à la position donnée (en mètres).
     */


    public int indexOfSegmentAt(double position);

    /**
     * @return Retourne la longueur de l'itinéraire (en mètres).
     */

    public double length();

    /**
     * @return Retourne la totalité des arêtes de l'itinéraire.
     */

    public List<Edge> edges();

    /**
     * @return Retourne la totalité des points situés aux
     * extrémités des arêtes de l'itinéraire.
     */

    public List<PointCh> points();

    /**
     * @param position Position donnée.
     * @return Retourne le point se trouvant à la position donnée
     * le long de l'itinéraire.
     */

    public PointCh pointAt(double position);

    /**
     * @param position Position donnée.
     * @return Retourne l'altitude à la position donnée
     * le long de l'itinéraire.
     */

    public double elevationAt(double position);

    /**
     * @param position Position donnée.
     * @return Retourne l'identité du nœud appartenant à l'itinéraire
     * et se trouvant le plus proche de la position donnée.
     */

    public int nodeClosestTo(double position);

    /**
     * @param point Point de référence donné.
     * @return Retourne le point de l'itinéraire se trouvant le plus
     * proche du point de référence donné.
     */

    public RoutePoint pointClosestTo(PointCh point);

}
