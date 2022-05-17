package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 9.3.2
 * RouteBean
 * <p>
 * Classe étant un bean JavaFX regroupant les propriétés relatives aux points
 * de passage et à l'itinéraire correspondant.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public final class RouteBean {

    /**
     * Constante représentant la taille du cache mémoire des itinéraires.
     */
    private static final int MEMORY_CACHE_SIZE = 50;

    /**
     * Constance représentant la distance maximale entre deux échantillons.
     */
    private static final double MAX_STEP_LENGTH = 5;

    /**
     * Attribut représentant la liste observable des points de passage.
     */
    private final ObservableList<Waypoint> waypoints;

    /**
     * Attribut représentant l'itinéraire permettant de relier les points de passage,
     * en lecture seule.
     */
    private final ObjectProperty<Route> route;

    /**
     * Attribut représentant la position mise en évidence.
     */
    private final DoubleProperty highlightedPosition;

    /**
     * Attribut représentant le profil de l'itinéraire, mise en lecture seule.
     */
    private final ObjectProperty<ElevationProfile> elevationProfile;

    /**
     * Attribut représentant le calculateur d'itinéraire.
     */
    private final RouteComputer routeComputer;


    /**
     * Attribut représentant une table associant à une paire de nœuds le meilleur itinéraire
     * (simple) les reliant.
     */
    private final LinkedHashMap<Pair<Integer, Integer>, Route> routeCacheMemory;

    /**
     * Constructeur initialisant le calcul d'itinéraire à celui passé en paramètres, et les autres attributs à leurs
     * valeurs de base.
     * @param routeComputer calculateur d'itinéraire, de type RouteComputer, utilisé pour déterminer le meilleur
     *                      itinéraire reliant deux points de passage.
     */
    public RouteBean(RouteComputer routeComputer) {
        this.routeComputer = routeComputer;
        this.waypoints = FXCollections.observableArrayList();
        this.highlightedPosition = new SimpleDoubleProperty();
        this.route = new SimpleObjectProperty<>();
        this.elevationProfile = new SimpleObjectProperty<>();
        this.routeCacheMemory = new LinkedHashMap<>(MEMORY_CACHE_SIZE, 0.75f, true);

        waypoints.addListener((ListChangeListener<? super Waypoint>) e -> computeNewRouteAndProfile());
        //TODO pas sur
        route.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue == null) setHighlightedPosition(Double.NaN);
            //TODO surtout pas dans la version final
            if (oldValue == null && newValue != null) setHighlightedPosition(1000);
        });
    }

    private void computeNewRouteAndProfile() {
        List<Waypoint> waypoints = getWaypoints();
        if(isValidRoute()) {
            List<Route> routeList = new ArrayList<>();
            for (int i = 0; i < waypoints.size() - 1; i++) {
                if (waypoints.get(i).nodeId() != waypoints.get(i + 1).nodeId()) {
                    Route route = getRouteFromCacheMemory(waypoints.get(i), waypoints.get(i + 1));
                    routeList.add(route);
                    computeElevationProfile(route);
                }
            }
            route.set(new MultiRoute(routeList));
            elevationProfile.set(computeElevationProfile(route.get()));
        }


    }

    private boolean isValidRoute() {
        List<Waypoint> waypointList = getWaypoints();
        if (waypointList.size() < 2) {
            route.set(null);
            elevationProfile.set(null);
            return false;
        }
        for (int i = 0; i < waypointList.size() - 1; i++) {
            if(!isRouteExisting(waypointList.get(i), waypointList.get(i + 1))) {
                route.set(null);
                elevationProfile.set(null);
                return false;
            }
        }
        return true;
    }

    private Route getRouteFromCacheMemory(Waypoint firstWaypoint, Waypoint secondWaypoint) {
        Pair<Integer, Integer> pair = new Pair<>(firstWaypoint.nodeId(), secondWaypoint.nodeId());
        if (routeCacheMemory.containsKey(pair)) {
            return routeCacheMemory.get(pair);
        } else {
            Route route = routeComputer.bestRouteBetween(firstWaypoint.nodeId(), secondWaypoint.nodeId());
            routeCacheMemory.put(pair, route);
            return route;
        }
    }

    private boolean isRouteExisting(Waypoint firstWaypoint, Waypoint secondWaypoint) {
        // Cas ou il y a 2 waypoint qui se suivent au même endroit
        if (firstWaypoint.nodeId() == secondWaypoint.nodeId()) return true;
        return getRouteFromCacheMemory(firstWaypoint, secondWaypoint) != null;
    }

    private ElevationProfile computeElevationProfile(Route route) {
        return ElevationProfileComputer.elevationProfile(route, MAX_STEP_LENGTH);
    }


    // accesseur et modicateur
    // PS check si on n'a pas utilisé le mot get/ set dans des commentaires en français

    //TODO ask jean : normal violer l'immuabilité ?
    /**
     * Accesseur retournant la propriété de la liste observable de points de passage.
     * @return la propriété de la liste observable de points de passage.
     */
    public ObservableList<Waypoint> waypointsProperty() {
        // Pas immuable, pas grave, car on offre dans tous les cas un setter.
        return waypoints;
    }

    /**
     * Accesseur retournant la liste observable de points de passage.
     * @return la liste observable de points de passage.
     */
    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    /**
     * Modificateur de la liste observable de points de passage.
     * @param newWaypoints nouvelle liste de points de passage.
     */
    public void setWaypoints(ObservableList<Waypoint> newWaypoints) {
        waypoints.clear();
        waypoints.addAll(newWaypoints);
    }

    /**
     * Accesseur retournant la propriété de l'itinéraire permettant de relier les points
     * de passage.
     * @return La propriété de l'itinéraire permettant de relier les points de passage. .
     */
    public ReadOnlyObjectProperty<Route> routeProperty() {
        return route;
    }

    /**
     * Accesseur retournant l'itinéraire permettant de relier les points de passage.
     * (Ok vis-à-vis de l'immuabilité, car toutes les classes implémentant Route sont immuables.(finales))
     * @return L'itinéraire permettant de relier les points de passage.
     */
    public Route getRoute() {
        return route.get();
    }

    /**
     * Accesseur retournant la propriété de la position mise en évidence.
     * @return La propriété de la position mise en évidence.
     */
    public DoubleProperty highlightedPositionProperty() {
        // Pas immuable, pas grave, car on offre dans tous les cas un setter.
        return highlightedPosition;
    }

    /**
     * Accesseur retournant la position mise en évidence.
     * @return la position mise en évidence.
     */
    public double getHighlightedPosition() {
        return highlightedPosition.get();
    }

    /**
     * Modificateur de la propriété de la position mise en évidence.
     * @param newHighlightedPosition nouvelle position mise en évidence.
     */
    public void setHighlightedPosition(double newHighlightedPosition) {
        this.highlightedPosition.set(newHighlightedPosition);
    }

    /**
     * Accesseur retournant la propriété du profil de l'itinéraire.
     * @return la propriété du profil de l'itinéraire.
     */
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty() {
        return elevationProfile;
    }

    /**
     * Accesseur retournant le profil de l'itinéraire.
     * (Ok vis-à-vis de l'immuabilité, car elevationProfile est immuable.(finales))
     * @return le profil de l'itinéraire.
     */
    public ElevationProfile getElevationProfile() {
        return elevationProfile.get();
    }

    public int indexOfNonEmptySegmentAt(double position) {
        int index = route.get().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).nodeId();
            int n2 = waypoints.get(i + 1).nodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }
}
