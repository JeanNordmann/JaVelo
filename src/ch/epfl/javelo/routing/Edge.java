package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.Objects;
import java.util.function.DoubleUnaryOperator;

/**
 * 4.3.4
 * Edge
 * <p>
 * Enregistrement représentant une arête d'un itinéraire. Son but est de collecter toutes
 * informations relatives à une arête d'itinéraire, qui pourraient être obtenues par des
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */


public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint,
                   double length, DoubleUnaryOperator profile) {

    /**
     * Méthode statique facilitant la construction d'une instance.
     *
     * @param graph      Graphe donné.
     * @param edgeId     Identité de l'arête dans le graphe.
     * @param fromNodeId Identité du nœud de départ de l'arête.
     * @param toNodeId   Identité du nœud d'arrivée de l'arête.
     * @return Une nouvelle instance avec ces attributs.
     */

    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId),
                graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    /**
     * Retourne la position le long de l'arête, en mètres, qui se trouve la plus proche du
     * point donné.
     * @param point Point donné.
     * @return Retourne la position le long de l'arête, en mètres, qui se trouve la plus
     * proche du point donné.
     */

    public double positionClosestTo(PointCh point) {
        return Math2.projectionLength(fromPoint.e(), fromPoint.n(), toPoint.e(),
                toPoint.n(), point.e(), point.n());
    }

    /**
     * Retourne le point se trouvant à la position donnée sur l'arête, exprimée
     * en mètres.
     * @param position Position donnée sur l'arête.
     * @return Retourne le point se trouvant à la position donnée sur l'arête,
     * exprimée en mètres.
     */

    public PointCh pointAt(double position) {
        // ne dois jamais arriver, mais parfois il arrive que certaines edges aillent une
        // longueur nulle, pour éviter des exceptions (voir piazza @1848_f2)
        if (length == 0) return fromPoint;
        double x = position / length;
        return new PointCh(Math2.interpolate(fromPoint.e(), toPoint.e(), x),
                Math2.interpolate(fromPoint.n(), toPoint.n(), x));
    }

    /**
     * Retourne l'altitude, en mètres, à la position donnée sur l'arête.
     * @param position Position donnée sur l'arête.
     * @return Retourne l'altitude, en mètres, à la position donnée sur l'arête.
     */

    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }


    //Méthode pour comparer des objets dans nos tests.
    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return fromNodeId == edge.fromNodeId && toNodeId == edge.toNodeId
                && Double.compare(edge.length, length) == 0 && Objects.equals(fromPoint, edge.fromPoint)
                && Objects.equals(toPoint, edge.toPoint) && Objects.equals(profile, edge.profile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromNodeId, toNodeId, fromPoint, toPoint, length, profile);
    }
}
