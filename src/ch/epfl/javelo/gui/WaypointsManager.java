package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.function.Consumer;

/**
 * 8.3.3
 * WaypointManager
 * <p>
 * Classe gérant l'affichage et l'interaction avec les points de passage.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */
public final class WaypointsManager {

    private final int SEARCH_DISTANCE = 500;

    private final Graph graph;
    private final ObjectProperty<MapViewParameters> mapViewParameters;
    private final ObservableList<Waypoint> waypointList;
    private final Consumer<String> stringConsumer;
    private final ObjectProperty<Point2D> previousCoordsOnScreen;
    private final Pane pane;

    /**
     * Classe gérant l'affichage et l'interaction avec les points de passage.
     * @param graph graph du réseau routier.
     * @param mapViewParameters paramètres du fond de carte actuel.
     * @param waypointList liste observable de tous les points de passages.
     * @param stringConsumer consumer nous permettant de signaler les erreurs à afficher sur l'interface graphique.
     */
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParameters,
                            ObservableList<Waypoint> waypointList, Consumer<String> stringConsumer) {
        this.graph = graph;
        this.mapViewParameters = mapViewParameters;
        this.waypointList = waypointList;
        this.stringConsumer = stringConsumer;
        previousCoordsOnScreen = new SimpleObjectProperty<>(new Point2D(0, 0));
        pane = new Pane();

        waypointList.addListener((ListChangeListener<? super Waypoint>) e -> recreatGroups());

        recreatGroups();
        pane.setPickOnBounds(false);
    }



    /**
     *
     * @return ztzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz
     */
    public Pane pane() { return pane; }

    public void draw() {
        for (int i = 0; i < waypointList.size(); i++) {
            PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(waypointList.get(i).pointCh());
            double xWayPoint = mapViewParameters.get().viewX(pointWebMercator);
            double yWayPoint = mapViewParameters.get().viewY(pointWebMercator);
            pane.getChildren().get(i).setLayoutX(xWayPoint);
            pane.getChildren().get(i).setLayoutY(yWayPoint);
        }
        pane.setPickOnBounds(false);
    }

    public void recreatGroups() {
        if (waypointList.size() > 0) {
            Group groupToAdd = new Group(getAndSetOutsideBorder(), getAndSetInsideBorder());
            addListener(groupToAdd);
            groupToAdd.getStyleClass().add("pin");
            groupToAdd.getStyleClass().add("first");
            pane.getChildren().add(groupToAdd);
        }

        if (waypointList.size() > 2) {
            for (int i = 1; i < waypointList.size()-1; i++) {
                Group groupToAdd = new Group(getAndSetOutsideBorder(), getAndSetInsideBorder());
                addListener(groupToAdd);
                groupToAdd.getStyleClass().add("pin");
                groupToAdd.getStyleClass().add("middle");
                pane.getChildren().add(groupToAdd);
            }
        }

        if (waypointList.size() > 1) {
            Group groupToAdd = new Group(getAndSetOutsideBorder(), getAndSetInsideBorder());
            addListener(groupToAdd);
            groupToAdd.getStyleClass().add("pin");
            groupToAdd.getStyleClass().add("last");
            pane.getChildren().add(groupToAdd);
        }
        draw();
    }

    /**
     * Méthode permettant d'ajouter une point de passage sur la carte à l'aide de ses coordonnées relatives
     * sur la carte affichée à l'écran.
     * @param x coordonnée X partante depuis le coin haut gauche
     * @param y coordonnée Y partante depuis le coin haut gauche
     */

    public void addWaypoint(double x, double y) {
        PointCh pointCh = mapViewParameters.get().pointAt(x, y).toPointCh();
        int idNodeClosestTo = graph.nodeClosestTo(pointCh, SEARCH_DISTANCE);
        if (idNodeClosestTo == -1) {
            // Pas de nœud dans la distance de recherche.
            //THROW ...
            stringConsumer.accept("Erreur pas de noeud dans la distance de recherche.");
            // stringConsumer.accept(() -> System.out.println("1 Erreur pas de nœud dans la distance de recherche.");
            System.out.println("Une exception devrait être affichée sur l'interface graphique");
        } else {
            // Ajout du Waypoint à liste des Waypoint.
            waypointList.add(new Waypoint(graph.nodePoint(idNodeClosestTo), idNodeClosestTo));
            //recreatGroups();
        }
    }

    public void moveWaypoint(MouseEvent event, Group pin) {
        Point2D translation = previousCoordsOnScreen.get().subtract(event.getX(), event.getY());
        pin.setLayoutX(pin.getLayoutX() - translation.getX());
        pin.setLayoutY(pin.getLayoutY() - translation.getY());
    }

    public SVGPath getAndSetOutsideBorder() {
        SVGPath outsideBorder = new SVGPath();
        outsideBorder.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
        outsideBorder.getStyleClass().add("pin_outside");
        return outsideBorder;
    }

    public SVGPath getAndSetInsideBorder() {
        SVGPath insideBorder = new SVGPath();
        insideBorder.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
        insideBorder.getStyleClass().add("pin_inside");
        return insideBorder;
    }

    public void addListener(Group group) {
        group.setOnMouseClicked(event -> {
            if (event.isStillSincePress())
                removeWaypointANCIENaddMouseRelasing(pane.getChildren().indexOf(group));
        });
    }

    /**
     * RENOME FDP
     */
    public void removeWaypointANCIENaddMouseRelasing(int i) {
        waypointList.remove(i);
    }

    public void waypointDragging() {
        int j = 0;
        for (Waypoint waypoint : waypointList) {
            Group pin = (Group) pane.getChildren().get(j);

            pin.setOnMousePressed(event -> {
                previousCoordsOnScreen.set(new Point2D(event.getX(), event.getY()));
            });

            pin.setOnMouseDragged(event -> {
                moveWaypoint(event, pin);
            });

            pin.setOnMouseDragReleased(event -> {

            });
            ++j;
        }
    }


 }
