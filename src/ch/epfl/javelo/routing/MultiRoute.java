package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

/**
 * 6.3.1
 * MultiRoute
 *
 * Classe publique et immuable, représentant un itinéraire multiple,
 * c'est-à-dire composé d'une séquence d'itinéraires contigus nommés
 * segments. Elle implémente l'interface Route et possède un unique
 * constructeur public.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public final class MultiRoute implements Route {

    /**
     * Attribut représentant une liste de Route. Les éléments peuvent
     * être d'autres MultiRoute ou des SingleRoute, c'est pourquoi nous
     * ne pouvons pas être plus précis que la classe Route.
     */

    private final List<Route> segments;

    /**
     * @param segments Segments donnés.
     * Constructeur qui instancie un itinéraire multiple composé des
     * segments donnés, ou lève une IllegalArgumentException si la liste
     * des segments est vide.
     */

    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = segments;
    }

    /**
     * @param position Position donnée.
     * @return Retourne l'index du segment de l'itinéraire contenant la
     * position donnée.
     */

    //TODO FLEMME DE REFLECHIR LA
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    /**
     * @return La longueur de l'itinéraire en mètres.
     */

    @Override
    public double length() {
        double length = 0;
        for (Route segment : segments) {
            length += segment.length();
        }
        return length;
    }

    /**
     * @return La totalité des arêtes de l'itinéraire.
     */

    @Override
    public List<Edge> edges() {
        List<Edge> edgeList = new ArrayList<>();
        for (Route segment : segments) {
            edgeList.addAll(segment.edges());
        }
        return List.copyOf(edgeList);
    }

    /**
     * @return Retourne la totalité des points situés aux
     * extrémités des arêtes de l'itinéraire, sans doublons.
     */

    @Override
    public List<PointCh> points() {
        List<PointCh> pointChList = new ArrayList<>();
        for (Route segment : segments) {
            pointChList.addAll(segment.points());
            pointChList.remove(pointChList.size() - 1);
        }
        pointChList.add(segments.get(segments.size() - 1).pointAt(segments.get(segments.size() - 1).length()));
        return pointChList;
    }

    @Override
    public PointCh pointAt(double position) {
        return null;
    }

    @Override
    public double elevationAt(double position) {
        return 0;
    }

    @Override
    public int nodeClosestTo(double position) {
        return 0;
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        return null;
    }
}
