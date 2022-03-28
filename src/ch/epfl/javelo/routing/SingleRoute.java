package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 5.3.2
 * SingleRoute
 *
 * Classe publique et immuable, représentant un itinéraire
 * simple, c'est-à-dire reliant un point de départ à un
 * d'arrivée, sans point de passage intermédiaire.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */


public final class SingleRoute implements Route {

    /**
     * Deux attributs inhérents à la classe SingleRoute.
     * Le premier, edges, est la liste d'arêtes demandée.
     * Le second, est le tableau des positions qui est
     * initialisé à la construction, pour ne pas avoir à le
     * recalculer à chaque fois, puisqu'il reste toujours le
     * même.
     */

    private final List<Edge> edges;
    private final double[] positionsTab;

    /**
     * Constructeur public, initialisant une SingleRoute,
     * donc sa liste d'arêtes, et son tableau de positions.
     * @param edges Liste d'arêtes donnée.
     */

    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(!edges.isEmpty());
        this.edges = List.copyOf(edges);
        positionsTab = new double[edges.size() + 1];
        positionsTab[0] = 0;
        for (int i = 1; i <= edges.size(); i++) {
            positionsTab[i] = edges.get(i - 1).length() + positionsTab[i - 1];
        }
    }

    /**
     * @param position Position donnée.
     * @return L'index de l'itinéraire contenant la position
     * donnée, qui vaut toujours 0 dans le cas d'un itinéraire
     * simple.
     */

    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    /**
     * @return La longueur de l'itinéraire, en mètres.
     */

    @Override
    public double length() {
        double totalLength = 0;
        for (Edge edge : edges) {
            totalLength += edge.length();
        }
        return totalLength;
    }

    /**
     * @return La totalité des arêtes de l'itinéraire,
     * et pour protéger l'immuabilité de la classe, on
     * renvoie une copie du tableau.
     */

    @Override
    public List<Edge> edges() {
        return new ArrayList<>(edges);
    }
    //TODO faire la même pour multiroute

    /**
     * @return Retourne la totalité des points situés aux
     * extrémités des arêtes de l'itinéraire.
     */

    @Override
    public List<PointCh> points() {
        List<PointCh> pointChList = new ArrayList<>();
        for (Edge edge : edges) {
            pointChList.add(edge.pointAt(0));
        }
        pointChList.add(edges.get(edges.size() - 1).pointAt(edges.get(edges.size() - 1).length()));
        return pointChList;
    }

    /**
     * @param position Position donnée.
     * @return Retourne le point se trouvant à la position
     * donnée le long de l'itinéraire.
     */

    @Override
    public PointCh pointAt(double position) {
        int index, absoluteResult;
        index = Arrays.binarySearch(positionsTab, Math2.clamp(0, position, length()));
        if (index >= 0) {
            if (index == edges.size()) {
                return edges.get(edges.size() - 1).pointAt(edges.get(edges.size() - 1).length());
            } else {
                return edges.get(index).pointAt(0);
            }
        } else {
            absoluteResult = Math.abs(index + 2);
            return edges.get(absoluteResult).pointAt(position - positionsTab[absoluteResult]);
        }
    }

    /**
     * @param position Position donnée.
     * @return Retourne l'altitude à la position donnée le long
     * de l'itinéraire, qui peut valoir NaN si l'arête contenant
     * cette position n'a pas de profil.
     */

    @Override
    public double elevationAt(double position) {
        int index, absoluteResult;
        index = Arrays.binarySearch(positionsTab, Math2.clamp(0, position, length()));
        if(index == 0) {
            return edges.get(index).elevationAt(0);
        }

        if (index > 0) {
            if (index == edges.size()) {
                return edges.get(edges.size() - 1).elevationAt(edges.get(edges.size() - 1).length());
            } else {
                if (Float.isNaN((float)edges.get(index).elevationAt(0))) return edges.get(index - 1).elevationAt(edges.get(index - 1).length());
                return edges.get(index).elevationAt(0);
            }
        } else {
            absoluteResult = Math.abs(index + 2);
            return edges.get(absoluteResult).elevationAt(position - positionsTab[absoluteResult]);
        }
    }

    /**
     * @param position Position donnée.
     * @return Retourne l'identité du nœud appartenant à l'itinéraire,
     * et se trouvant le plus proche de la position donnée.
     */

    @Override
    public int nodeClosestTo(double position) {
        int index = Arrays.binarySearch(positionsTab, Math2.clamp(0, position, length()));
        if (index >= 0) return index;
        double fstNodePos = positionsTab[-index - 2];
        double sndNodePos = positionsTab[-index - 1];

        return (position - fstNodePos) <= ((sndNodePos - fstNodePos) / 2.0) ? -index - 2 : -index - 1;
    }

    /**
     * @param point Point de référence donné.
     * @return Retourne le point de l'itinéraire se trouvant le plus
     * proche du point de référence donné.
     */

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint routePointTemp, routePoint = RoutePoint.NONE ;
        double position, clampedPosition, previousLengths =0;
        for (Edge edge : edges) {
            position = edge.positionClosestTo(point);
            clampedPosition = Math2.clamp(0, position, edge.length());
            PointCh pointCh = new PointCh(edge.pointAt(clampedPosition).e(), edge.pointAt(clampedPosition).n());
            routePointTemp = new RoutePoint(pointCh, clampedPosition + previousLengths , pointCh.distanceTo(point));
            routePoint = routePoint.min(routePointTemp);
            previousLengths += edge.length();
        }
        return routePoint;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleRoute that = (SingleRoute) o;
        return Objects.equals(edges, that.edges) && Arrays.equals(positionsTab, that.positionsTab);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(edges);
        result = 31 * result + Arrays.hashCode(positionsTab);
        return result;
    }
}
