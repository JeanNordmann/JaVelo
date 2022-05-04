package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableListValue;
import javafx.collections.ObservableList;
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
 * RouteBean
 * <p>
 * Classe étant un bean JavaFX regroupant les propriétés relatives aux points
 * de passage et à l'itinéraire correspondant.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public class RouteManager {
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
     * Attribut représentant une propriété JavaFX, en lectureseule, contenant
     * les paramètres de la carte affichée.
     */
    private final ReadOnlyObjectProperty<MapViewParameters> mapViewParameters;

    /**
     * Attribut représentant un consommateur d'erreurs, permettant de signaler une erreur.
     */
    private final Consumer<String> stringConsumer;

    /**
     * Attribut représentant la polyline.
     */
    private final Polyline polyline;

    /**
     * Attribut représentant le disque de la position mise en évidence.
     */
    private final Circle circle;

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
     * @param stringConsumer Consommateur d'erreurs permettant de signaler une
     *                       erreur.
     */

    public RouteManager(RouteBean routeBean, ObjectProperty<MapViewParameters> mapViewParameters, Consumer<String> stringConsumer) {
        this.routeBean = routeBean;
        this.mapViewParameters = mapViewParameters;
        this.stringConsumer = stringConsumer;
        this.polyline = new Polyline();
        polyline.setId("route");
        this.circle = new Circle(RADIUS_HIGHLIGHTED_POINT);
        circle.setId("highlight");

        pane = new Pane();
        pane.setPickOnBounds(false);

        //TODO no magique Number
        routeBean.setHighlightedPosition(1000);
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
            pane.getChildren().remove(circle);
            MapViewParameters actualMVP = mapViewParameters.get();
            PointWebMercator highlightedPWM = PointWebMercator.ofPointCh(routeBean.getRoute().pointAt(routeBean.getHighlightedPosition()));
            circle.setLayoutX(actualMVP.viewX(highlightedPWM));
            circle.setLayoutY(actualMVP.viewY(highlightedPWM));
            pane.getChildren().add(circle);
        }
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
        circle.setOnMouseClicked(e -> {
            Point2D circlePosition = circle.localToParent(e.getX(), e.getY());
            addWaypoint(circlePosition.getX(), circlePosition.getY());
            PointCh pointCh = routeBean.getRoute().pointAt(routeBean.getHighlightedPosition());
            ObservableList<Waypoint> waypointList ;
            waypointList.addAll(routeBean.getWaypoints());
            int indexWhereAddWP = routeBean.getRoute().indexOfSegmentAt(routeBean.getHighlightedPosition());
            waypointList.add(indexWhereAddWP, new Waypoint(pointCh, routeBean.getRoute().nodeClosestTo(routeBean.getHighlightedPosition())));
            routeBean.setWaypoints(waypointList);
        });
        routeBean.routeProperty().addListener(e -> constructPolyline());
        mapViewParameters.addListener((p, oldS, newS) -> {
            if (oldS.zoomLevel() != newS.zoomLevel()) {
                constructPolyline();
                constructMarker();
            }
        });
        mapViewParameters.addListener((p, oldS, newS) -> {
            if (!oldS.topLeft().equals(newS.topLeft()) && oldS.zoomLevel() == newS.zoomLevel()) {
                polyline.setLayoutX(polyline.getLayoutX() + oldS.topLeft().getX() - newS.topLeft().getX());
                polyline.setLayoutY(polyline.getLayoutY() + oldS.topLeft().getY() - newS.topLeft().getY());
                circle.setLayoutX(circle.getLayoutX() + oldS.topLeft().getX() - newS.topLeft().getX());
                circle.setLayoutY(circle.getLayoutY() + oldS.topLeft().getY() - newS.topLeft().getY());
            }
        });
        routeBean.routeProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null && newValue != null) polyline.setVisible(true);
            if (oldValue != null && newValue == null) polyline.setVisible(false);
        });
        routeBean.highlightedPositionProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.equals(Double.NaN) && !newValue.equals(Double.NaN)){ circle.setVisible(true);
                System.out.println("circle set true");}

            if (!oldValue.equals(Double.NaN) && newValue.equals(Double.NaN)){
                System.out.println("circle set false");
                circle.setVisible(false);}
        });
        routeBean.highlightedPositionProperty().addListener(e -> constructMarker());
        routeBean.routeProperty().addListener(e -> constructMarker());
    }
}
