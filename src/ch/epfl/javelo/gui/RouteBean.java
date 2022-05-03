package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private ObjectProperty<ObservableList<Waypoint>> waypoints;

    /**
     * Attribut représentant l'itinéraire permettant de relier les points de passage,
     * en lecture seule.
     */
    private ObjectProperty<Route> route;

    /**
     * Attribut représentant la position mise en évidence.
     */
    private DoubleProperty highlightedPosition;

    /**
     * Attribut représentant le profil de l'itinéraire, mise en lecture seule.
     */
    private ObjectProperty<ElevationProfile> elevationProfile;

    /**
     * Attribut représentant le calculateur d'itinéraire.
     */
    private RouteComputer routeComputer;

    /**
     * Attribut représentant une table associant à une paire de nœuds le meilleur itinéraire
     * (simple) les reliant.
     */
    private LinkedHashMap<Pair<Integer, Integer>, Route> routeCacheMemory;

    //TODO Constructeur j'en sais rien des paramètres pour l'instant.
    public RouteBean() {
        waypoints.addListener((observable, oldValue, newValue) -> computeNewRouteAndProfile());

        routeCacheMemory = new LinkedHashMap<>(MEMORY_CACHE_SIZE, 0.75f, true);
    }

    /**
     * Accesseur retournant la propriété de la liste observable de points de passage.
     * @return la propriété de la liste observable de points de passage.
     */
    public ObjectProperty<ObservableList<Waypoint>> waypointsProperty() {
        return waypoints;
    }

    /**
     * Accesseur retournant la liste observable de points de passage.
     * @return la liste observable de points de passage.
     */
    public ObservableList<Waypoint> getWaypoints() {
        return waypoints.get();
    }

    /**
     * Modificateur de la liste observable de points de passage.
     * @param newWaypoints nouvelle liste de points de passage.
     */
    public void setWaypoints(ObservableList<Waypoint> newWaypoints) {
        this.waypoints.set(newWaypoints);
    }

    /**
     * Accesseur retournant la propriété de l'itinéraire permettant de relier les points
     * de passage.
     * @return la propriété de l'itinéraire permettant de relier les points de passage. .
     */
    public ReadOnlyObjectProperty<Route> routeProperty() {
        return route;
    }

    /**
     * Accesseur retournant l'itinéraire permettant de relier les points de passage.
     * @return l'itinéraire permettant de relier les points de passage.
     */
    public Route getRoute() {
        return route.get();
    }

    /**
     * Accesseur retournant la propriété de la position mise en évidence.
     * @return la propriété de la position mise en évidence.
     */
    public DoubleProperty highlightedPositionProperty() {
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
     * @return le profil de l'itinéraire.
     */
    public ElevationProfile getElevationProfile() {
        return elevationProfile.get();
    }


    public void computeNewRouteAndProfile() {
        ObservableList<Waypoint> waypoints = getWaypoints();
        if(isValidRoute()) {
            List<Route> routes = new ArrayList<>();
            for (int i = 0; i < waypoints.size() - 1; i++) {
                routes.add(getRouteFromCacheMemory(waypoints.get(i), waypoints.get(i + 1)));
            }
        }
    }

    private boolean isValidRoute() {
        ObservableList<Waypoint> waypointList = getWaypoints();
        if (waypointList.size() < 2) {
            route = null;
            elevationProfile = null;
            return false;
        }
        for (int i = 0; i < waypointList.size() - 1; i++) {
            if(!isRouteExisting(waypointList.get(i), waypointList.get(i + 1))) {
                route = null;
                elevationProfile = null;
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
        return getRouteFromCacheMemory(firstWaypoint, secondWaypoint) != null;
    }

    private ElevationProfile computeElevationProfile(Route route) {
        return ElevationProfileComputer.elevationProfile(route, MAX_STEP_LENGTH);
    }


}
