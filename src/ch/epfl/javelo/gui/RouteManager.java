package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableListValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * 9.3.2
 * RouteManager
 * <p>
 * Classe gérant l'affichage de l'itinéraire et (une partie de) l'interaction avec lui.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public final class RouteManager {
    /**
     * Constante définissant le rayon du disque représentant la position mise
     * en évidence.
     */
    private static final double RADIUS_HIGHLIGHTED_POINT = 5;

    /**
     * Attribut représentant le bean de l'itinéraire.
     */
    private final RouteBean routeBean;

    /**
     * Attribut représentant une propriété JavaFX, en lecture seule, contenant
     * les paramètres de la carte affichée.
     */
    private final ReadOnlyObjectProperty<MapViewParameters> mapViewParameters;

    /**
     * Attribut représentant la polyline.
     */
    private final Polyline polyline;

    /**
     * Attribut représentant le disque de la position mise en évidence.
     */
    private final Circle highlightCircle;

    /**
     * Attribut représentant le panneau contenant
     */
    private final Pane pane;

    /**
     * Constructeur public prenant en argument le bean de l'itinéraire, une
     * propriété JavaFX, en lecture seule, contenant les paramètres de la
     * carte affichée et un consommateur d'erreurs, permettant de signaler
     * une erreur.
     * @param routeBean Bean de l'itinéraire.
     * @param mapViewParameters Propriété JavaFX, en lecture seule, contenant
     *                          les paramètres de la carte affichée.
     */

    public RouteManager(RouteBean routeBean, ObjectProperty<MapViewParameters> mapViewParameters) {
        this.routeBean = routeBean;
        this.mapViewParameters = mapViewParameters;
        this.polyline = new Polyline();
        polyline.setId("route");
        this.highlightCircle = new Circle(RADIUS_HIGHLIGHTED_POINT);
        highlightCircle.setId("highlight");

        pane = new Pane();
        pane.setPickOnBounds(false);

        constructPolyline();
        constructMarker();
        setUpListeners();
    }

    /**
     * Méthode publique retournant le panneau de la classe.
     * @return Le panneau de la classe, de type Pane.
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Méthode privée construisant la polyline.
     */
    private void constructPolyline() {
        pane.getChildren().remove(polyline);
        List<Double> coordinates = new ArrayList<>();
        try {
            List<PointCh> pointChList = routeBean.getRoute().points();
            MapViewParameters actualMVP = mapViewParameters.get();
            // Permet de mettre le premier de l'itinéraire à la coordonnée (0,0) de la polyline.
            // Attention → il faut encore la placer à la bonne position sur l'écran.
            double xOffset = actualMVP.viewX(PointWebMercator.ofPointCh(pointChList.get(0))),
                    yOffset = actualMVP.viewX(PointWebMercator.ofPointCh(pointChList.get(0)));
            for (PointCh pointCh : pointChList) {
                PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(pointCh);
                coordinates.add(actualMVP.viewX(pointWebMercator) - xOffset);
                coordinates.add(actualMVP.viewY(pointWebMercator) - yOffset);
            }
            polyline.getPoints().setAll(coordinates);
            polyline.setLayoutX(xOffset);
            polyline.setLayoutY(yOffset);
            pane.getChildren().add(polyline);
        } catch (NullPointerException ignored) {}
    }

    private void constructMarker() {
        if (routeBean.getRoute() != null) {
            pane.getChildren().remove(highlightCircle);
            MapViewParameters actualMVP = mapViewParameters.get();
            PointWebMercator highlightedPWM = PointWebMercator.ofPointCh(routeBean.getRoute().pointAt(routeBean.getHighlightedPosition()));
            highlightCircle.setLayoutX(actualMVP.viewX(highlightedPWM));
            highlightCircle.setLayoutY(actualMVP.viewY(highlightedPWM));
            pane.getChildren().add(highlightCircle);
        }
    }

    private void clickOnHighlightMarker() {
        //TODO pas supprimé : Point2D eventPosition = highlightCircle.localToParent(e.getX(), e.getY()); et Point2D highlightCirclePosition = new Point2D(highlightCircle.getLayoutX(), highlightCircle.getLayoutY());
        // Point2D eventPosition = highlightCircle.localToParent(e.getX(), e.getY());
        // Point2D highlightCirclePosition = new Point2D(highlightCircle.getLayoutX(), highlightCircle.getLayoutY());
//TODO idéalement (lu sur piazza) devrait pas prendre coordonné centre cercle, mais ou on a cliqué exactement
        Waypoint waypointToAdd = new Waypoint(routeBean.getRoute().pointAt(routeBean.getHighlightedPosition()),
                routeBean.getRoute().nodeClosestTo(routeBean.getHighlightedPosition()));
        List<Waypoint> waypointList = routeBean.getWaypoints();

            int indexOfNewWaypoint =
                    routeBean.indexOfNonEmptySegmentAt(routeBean.getHighlightedPosition()) + 1;
            waypointList.add(indexOfNewWaypoint, waypointToAdd);

    }


    /**
     * Méthode privée configurant les auditeurs, afin de positionner et/ou
     * rendre (in)visible le disque indiquant la position mise en évidence
     * lorsque celle-ci change, lorsque la route change, ou lorsque les
     * paramètres de la carte changent, ensuite de reconstruire totalement
     * et/ou rendre (in)visible la polyline représentant l'itinéraire
     * lorsque ce dernier change, enfin de repositionner -sans la reconstruire-
     * la polyline lorsque la carte a été glissée, mais que son niveau de zoom
     * n'a pas changé.
     */
    private void setUpListeners() {

        // Listener nous permettant d'ajouter un point si on clique sur le marqueur.
        highlightCircle.setOnMouseClicked(e -> clickOnHighlightMarker());
        // Listener nous permettant de redessiner la polyligne et le marqueur si on change le zoomLevel du mapViewParameter.
        routeBean.routeProperty().addListener(e -> constructPolyline());
        mapViewParameters.addListener((p, oldS, newS) -> {
            if (oldS.zoomLevel() != newS.zoomLevel()) {
                constructPolyline();
                constructMarker();
            }
        });
        // Listener nous permettant de déplacer la polyligne et le marqueur si on bouge la carte (mais pas le zoom level,
        // car s'il est aussi modifié l'itinéraire est de toute façon redessiner.)
        mapViewParameters.addListener((p, oldS, newS) -> {
            if (!oldS.topLeft().equals(newS.topLeft()) && oldS.zoomLevel() == newS.zoomLevel()) {
                polyline.setLayoutX(polyline.getLayoutX() + oldS.topLeft().getX() - newS.topLeft().getX());
                polyline.setLayoutY(polyline.getLayoutY() + oldS.topLeft().getY() - newS.topLeft().getY());
                highlightCircle.setLayoutX(highlightCircle.getLayoutX() + oldS.topLeft().getX() - newS.topLeft().getX());
                highlightCircle.setLayoutY(highlightCircle.getLayoutY() + oldS.topLeft().getY() - newS.topLeft().getY());
            }
        });

        // Listener nous permettant de d'actualiser la visibilité de la polyligne et du marqueur, affin qu'ils
        // deviennent invisibles s'il n'y a pas d'itinéraire.
        routeBean.routeProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null && newValue != null) {
                polyline.setVisible(true);
                highlightCircle.setVisible(true);
            }
            if (oldValue != null && newValue == null) {
                polyline.setVisible(false);
                highlightCircle.setVisible(false);
            }
        });

        // Listener nous permettant de rendre la route invisible si sa valeur est Nan
        routeBean.highlightedPositionProperty().addListener((observable, oldValue, newValue) -> {
            //TODO demander à jean P s'ils ont réussi à utiliser la méthode isNan qui est plus clean !
            if (oldValue.equals(Double.NaN) && !newValue.equals(Double.NaN)) highlightCircle.setVisible(true);
            // TODO maxime j'ai un doute jsp si ça fait sens de check aussi l'ancienne valeur, de plus on set la visibilité
            // du higlighted dans 2 listeners ducoup jsp si ça peut faire de la merde
            if (!oldValue.equals(Double.NaN) && newValue.equals(Double.NaN)) highlightCircle.setVisible(false);
        });
        // Listener nous permettant de redessiner le marqueur si sa position sur l'itinéraire change.
        routeBean.highlightedPositionProperty().addListener(e -> constructMarker());
        // Listener nous permettant de redessiner le marqueur si la route change.
        routeBean.routeProperty().addListener(e -> constructMarker());
    }
}
