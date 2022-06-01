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

    //Constante représentant le facteur de chargement du cache mémoire à donner à la
    //construction de ce cache.
    private static final float ROUTE_CACHE_LOAD_FACTOR = 0.75f;

    //Constante représentant la taille du cache mémoire des itinéraires.
    private static final int MEMORY_CACHE_SIZE = 50;

    //Constante représentant la distance maximale entre deux échantillons.
    private static final double MAX_STEP_LENGTH = 5;

    /**
     * Attribut représentant la liste observable des points de passage.
     */
    private final ObservableList<Waypoint> waypoints;

    /**
     * Attribut représentant l'itinéraire permettant de relier les points de passage.
     */
    private final ObjectProperty<Route> route;

    /**
     * Attribut représentant la position mise en évidence.
     */
    private final DoubleProperty highlightedPosition;

    /**
     * Attribut représentant le profil de l'itinéraire.
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
     * Constructeur initialisant le calculateur d'itinéraire à celui passé en paramètres et les
     * autres attributs à leurs valeurs de base.
     * @param routeComputer Calculateur d'itinéraire, de type RouteComputer, utilisé pour déterminer
     *                      le meilleur itinéraire reliant deux points de passage.
     */
    public RouteBean(RouteComputer routeComputer) {
        this.routeComputer = routeComputer;
        this.waypoints = FXCollections.observableArrayList();
        this.highlightedPosition = new SimpleDoubleProperty();
        this.route = new SimpleObjectProperty<>();
        this.elevationProfile = new SimpleObjectProperty<>();
        this.routeCacheMemory = new LinkedHashMap<>(MEMORY_CACHE_SIZE, ROUTE_CACHE_LOAD_FACTOR,
                true);

        waypoints.addListener((ListChangeListener<? super Waypoint>) e ->
                computeNewRouteAndProfile());
    }

    /**
     * Accesseur retournant la propriété de la liste observable de points de passage.
     * @return La propriété de la liste observable de points de passage.
     */
    public ObservableList<Waypoint> waypointsProperty() {
        // Pas immuable, pas grave, car on offre dans tous les cas un setter.
        return waypoints;
    }

    /**
     * Accesseur retournant la liste observable de points de passage.
     * @return La liste observable de points de passage.
     */
    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    /**
     * Accesseur retournant la propriété de l'itinéraire permettant de relier les points
     * de passage, en lecture seule.
     * @return La propriété de l'itinéraire permettant de relier les points de passage.
     */
    public ReadOnlyObjectProperty<Route> routeProperty() {
        return route;
    }

    /**
     * Accesseur retournant l'itinéraire permettant de relier les points de passage.
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
        return highlightedPosition;
    }

    /**
     * Accesseur retournant la position mise en évidence.
     * @return La position mise en évidence.
     */
    public double getHighlightedPosition() {
        return highlightedPosition.get();
    }


    /**
     * Accesseur retournant la propriété du profil de l'itinéraire, en lecture seule.
     * @return La propriété du profil de l'itinéraire.
     */
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty() {
        return elevationProfile;
    }

    /**
     * Accesseur retournant le profil de l'itinéraire.
     * @return Le profil de l'itinéraire.
     */
    public ElevationProfile getElevationProfile() {
        return elevationProfile.get();
    }

    /**
     * Méthode privée (donnée sur l'énoncé), retournant l'index du segment contenant une position
     * le long de l'itinéraire donnée en paramètre, en ignorant les segments vides.
     * @param position Position donnée le long de l'itinéraire.
     * @return Retourne l'index du segment contenant la position le long de l'itinéraire donnée
     * en paramètre, en ignorant les segments vides.
     */
    public int indexOfNonEmptySegmentAt(double position) {
        int index = route.get().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).nodeId();
            int n2 = waypoints.get(i + 1).nodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }

    /**
     * Méthode privée calculant la nouvelle route et son profil, si la route est valide.
     */
    private void computeNewRouteAndProfile() {
        List<Waypoint> waypoints = getWaypoints();
        if (isValidRoute()) {
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

    /**
     * Méthode privée retournant si la route entre les points de passage est jugée valide.
     * @return Retourne vrai si et seulement si la route est jugée valide.
     */
    private boolean isValidRoute() {
        List<Waypoint> waypointList = getWaypoints();
        //Si la liste contient moins de deux points de passage, alors il n'y a aucune route et
        //aucun profil. Ainsi, la route n'est pas valide.
        if (waypointList.size() < 2) {
            route.set(null);
            elevationProfile.set(null);
            return false;
        }

        //Si aucune route n'existe entre deux points de passage consécutifs de la liste, alors
        //l'attribut route et celui du profil sont mis à une valeur nulle. Ainsi, la route n'est
        //toujours pas valide.
        for (int i = 0; i < waypointList.size() - 1; i++) {
            if (!isRouteExisting(waypointList.get(i), waypointList.get(i + 1))) {
                route.set(null);
                elevationProfile.set(null);
                return false;
            }
        }

        //Si les deux précédents cas sont évités, alors la route est bien valide.
        return true;
    }

    /**
     * Méthode privée accédant au cache mémoire et retournant la meilleure route calculée entre les
     * deux points de passage donnés en paramètres. Elle la retourne directement si elle est déjà
     * présente dans le cache, sinon la calcule, l'ajoute au cache et la retourne.
     * @param firstWaypoint Premier point de passage et point de départ de la route à calculer.
     * @param secondWaypoint Second point de passage et point d'arrivée de la route à calculer.
     * @return Retourne la meilleure route calculée entre les deux points de passage passés en
     * paramètres.
     */
    private Route getRouteFromCacheMemory(Waypoint firstWaypoint, Waypoint secondWaypoint) {
        Pair<Integer, Integer> pair = new Pair<>(firstWaypoint.nodeId(), secondWaypoint.nodeId());
        return routeCacheMemory.computeIfAbsent(pair,
                s -> routeComputer.bestRouteBetween(s.getKey(), s.getValue()));
    }

    /**
     * Méthode privée vérifiant si la route entre les deux points de passage passés en paramètres
     * est valide ou non.
     * @param firstWaypoint Premier point de passage et point de départ de la route à vérifier.
     * @param secondWaypoint Second point de passage et point d'arrivée de la route à vérifier.
     * @return Retourne vrai si et seulement si la route entre les deux points de passage passés
     * en paramètres est valide.
     */
    private boolean isRouteExisting(Waypoint firstWaypoint, Waypoint secondWaypoint) {
        //Cas où deux points de passage se suivent.
        if (firstWaypoint.nodeId() == secondWaypoint.nodeId()) return true;
        return getRouteFromCacheMemory(firstWaypoint, secondWaypoint) != null;
    }

    /**
     * Méthode privée calculant le profil d'une route passée en paramètre de la fonction.
     * @param route Route de laquelle il faut calculer le profil.
     * @return Retourne le profil de la route donnée en paramètre.
     */
    private ElevationProfile computeElevationProfile(Route route) {
        return ElevationProfileComputer.elevationProfile(route, MAX_STEP_LENGTH);
    }
}
