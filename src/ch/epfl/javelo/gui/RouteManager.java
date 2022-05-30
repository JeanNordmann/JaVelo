package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;

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

        //Permet d'éviter de bloquer l'interaction avec les panneaux en arrière-plan.
        pane.setPickOnBounds(false);

        //Construit le marqueur une première fois.
        constructMarker();

        //Configure les auditeurs.
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
            //Permet de mettre le premier de l'itinéraire à la coordonnée (0,0) de la polyline.
            //Attention → il faut encore la placer à la bonne position sur l'écran.
            double xOffset = actualMVP.viewX(PointWebMercator.ofPointCh(pointChList.get(0))),
                    yOffset = actualMVP.viewX(PointWebMercator.ofPointCh(pointChList.get(0)));
            for (PointCh pointCh : pointChList) {
                PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(pointCh);
                coordinates.add(actualMVP.viewX(pointWebMercator) - xOffset);
                coordinates.add(actualMVP.viewY(pointWebMercator) - yOffset);
            }
            //Modifie toutes les coordonnées de la polyline.
            polyline.getPoints().setAll(coordinates);
            //Modifie les coordonnées X et Y sur l'écran de la ligne à afficher.
            polyline.setLayoutX(xOffset);
            polyline.setLayoutY(yOffset);
            //Ajoute la nouvelle ligne au panneau.
            pane.getChildren().add(polyline);
        } catch (NullPointerException ignored) {}
    }

    /**
     * Méthode privée construisant le marqueur.
     */
    private void constructMarker() {
        if (routeBean.getRoute() != null) {
            pane.getChildren().remove(highlightCircle);
            MapViewParameters actualMVP = mapViewParameters.get();
            PointWebMercator highlightedPWM = PointWebMercator.ofPointCh(routeBean.getRoute()
                    .pointAt(routeBean.getHighlightedPosition()));
            highlightCircle.setLayoutX(actualMVP.viewX(highlightedPWM));
            highlightCircle.setLayoutY(actualMVP.viewY(highlightedPWM));
            pane.getChildren().add(highlightCircle);
        }
    }

    /**
     * Méthode privée permettant d'ajouter un point de passage sur le marqueur surligné.
     */
    private void clickOnHighlightMarker() {
        Waypoint waypointToAdd = new Waypoint(routeBean.getRoute()
                .pointAt(routeBean.getHighlightedPosition()),
                routeBean.getRoute().nodeClosestTo(routeBean.getHighlightedPosition()));

        List<Waypoint> waypointList = routeBean.getWaypoints();
        int indexOfNewWaypoint = routeBean.indexOfNonEmptySegmentAt(routeBean
                    .getHighlightedPosition()) + 1;

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

        //Auditeur nous permettant d'ajouter un point si on clique sur le marqueur.
        highlightCircle.setOnMouseClicked(e -> clickOnHighlightMarker());

        mapViewParameters.addListener((p, oldS, newS) -> {
            if (oldS.zoomLevel() != newS.zoomLevel()) {
                //Pour éviter de redessiner la ligne de l'itinéraire si la route est nulle.
                if (routeBean.getRoute() != null) constructPolyline();

                constructMarker();
            }
        });
        //Auditeur nous permettant de déplacer la polyline et le marqueur si on bouge la carte
        //(mais pas le niveau de zoom, car s'il est aussi modifié l'itinéraire est de toute façon
        //redessiné).
        mapViewParameters.addListener((p, oldS, newS) -> {
            if (!oldS.topLeft().equals(newS.topLeft()) && oldS.zoomLevel() == newS.zoomLevel()) {
                polyline.setLayoutX(polyline.getLayoutX() + oldS.topLeft().getX()
                        - newS.topLeft().getX());
                polyline.setLayoutY(polyline.getLayoutY() + oldS.topLeft().getY()
                        - newS.topLeft().getY());
                highlightCircle.setLayoutX(highlightCircle.getLayoutX() + oldS.topLeft().getX()
                        - newS.topLeft().getX());
                highlightCircle.setLayoutY(highlightCircle.getLayoutY() + oldS.topLeft().getY()
                        - newS.topLeft().getY());
            }
        });

        //Auditeur nous permettant de d'actualiser la visibilité de la polyline et du marqueur,
        //afin qu'ils deviennent invisibles s'il n'y a pas d'itinéraire.
        routeBean.routeProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                //Permet de rendre visible la ligne et le marqueur si le nouvel itinéraire n'est
                //plus nul.
                constructPolyline();
                polyline.setVisible(true);
                highlightCircle.setVisible(true);
            }
            if (oldValue != null && newValue == null) {
                //Permet de rendre invisible la ligne et le marqueur si le nouvel itinéraire est
                //devenu nul.
                polyline.setVisible(false);
                highlightCircle.setVisible(false);
            }
        });

        //Auditeur permettant de rendre la route invisible si sa valeur est NaN.
        routeBean.highlightedPositionProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.equals(Double.NaN) && !newValue.equals(Double.NaN))
                highlightCircle.setVisible(true);
            if (!oldValue.equals(Double.NaN) && newValue.equals(Double.NaN))
                highlightCircle.setVisible(false);
        });

        //Auditeur nous permettant de redessiner le marqueur si sa position sur l'itinéraire change.
        routeBean.highlightedPositionProperty().addListener((v,ov,nv) -> constructMarker());

        //Auditeur nous permettant de redessiner le marqueur si la route change.
        routeBean.routeProperty().addListener((p, oldS, newS) -> constructMarker());
    }
}
