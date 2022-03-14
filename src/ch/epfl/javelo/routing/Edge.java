package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import java.util.function.DoubleUnaryOperator;
import ch.epfl.javelo.data.Graph;

/**
 * 4.3.4
 * Edge
 *
 * Enregistrement représentant une arête d'un itinéraire. Son but est de collecter toutes
 * informations relatives à une arête d'itinéraire, qui pourraient être obtenues par des
 *
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */


public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint,
        double length, DoubleUnaryOperator profile) {

    /**
     * Méthode statique facilitant la construction d'une instance.
     * @param graph Graphe donné.
     * @param edgeId Identité de l'arête dans le graphe.
     * @param fromNodeId Identité du nœud de départ de l'arête.
     * @param toNodeId Identité du nœud d'arrivée de l'arête.
     * @return Une nouvelle instance avec ces attributs.
     */

    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId),
                        graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    /**
     * @param point Point donné.
     * @return Retourne la position le long de l'arête, en mètres, qui se trouve la plus
     * proche du point donné.
     */

    public double positionClosestTo(PointCh point) {
        return Math2.projectionLength(fromPoint.e(), fromPoint.n(), toPoint.e(), toPoint.n(), point.e(), point.n());
    }

    /**
     * @param position Position donnée sur l'arête.
     * @return Retourne le point se trouvant à la position donnée sur l'arête,
     * exprimée en mètres.
     */

    public PointCh pointAt(double position) {
        double x = position/length;
        return new PointCh(Math2.interpolate(fromPoint.e(), toPoint.e(), x),
                            Math2.interpolate(fromPoint.n(), toPoint.n(), x));
    }

    /**
     * @param position Position donnée sur l'arête.
     * @return Retourne l'altitude, en mètres, à la position donnée sur l'arête.
     */

    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }
}
