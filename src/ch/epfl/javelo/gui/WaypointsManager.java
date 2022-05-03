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
import javafx.scene.Node;
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
    private  Point2D previousCoordsOnScreen;

    private Point2D initialCoordinatePoint2D;
    private Point2D actualCoordinatePoint2D;
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
        previousCoordsOnScreen = new Point2D(0, 0);
        initialCoordinatePoint2D= new Point2D(0, 0);
        actualCoordinatePoint2D = new Point2D(0, 0);
        pane = new Pane();

        waypointList.addListener((ListChangeListener<? super Waypoint>) e -> recreateGroups());

        recreateGroups();
        pane.setPickOnBounds(false);
    }

    /**
     *
     * @return
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

    public void recreateGroups() {
        pane.getChildren().clear();
        if (waypointList.size() > 0) {
            Group groupToAdd = new Group(getAndSetOutsideBorder(), getAndSetInsideBorder());
            groupToAdd.getStyleClass().add("pin");
            groupToAdd.getStyleClass().add("first");
            setUpListeners(groupToAdd);
            pane.getChildren().add(groupToAdd);
        }

        if (waypointList.size() > 2) {
            for (int i = 1; i < waypointList.size()-1; i++) {
                Group groupToAdd = new Group(getAndSetOutsideBorder(), getAndSetInsideBorder());
                groupToAdd.getStyleClass().add("pin");
                groupToAdd.getStyleClass().add("middle");
                setUpListeners(groupToAdd);
                pane.getChildren().add(groupToAdd);
            }
        }

        if (waypointList.size() > 1) {
            Group groupToAdd = new Group(getAndSetOutsideBorder(), getAndSetInsideBorder());
            groupToAdd.getStyleClass().add("pin");
            groupToAdd.getStyleClass().add("last");
            setUpListeners(groupToAdd);
            pane.getChildren().add(groupToAdd);
        }
        draw();
        System.out.println(waypointList);
    }

    /**
     * Méthode permettant d'ajouter une point de passage sur la carte à l'aide de ses coordonnées relatives
     * sur la carte affichée à l'écran.
     * @param x coordonnée X partante depuis le coin haut gauche
     * @param y coordonnée Y partante depuis le coin haut gauche
     */

    public boolean addWaypoint(double x, double y) {
        System.out.println("addWaypoint de waypoint manager est appelelé" );
        PointCh pointCh = mapViewParameters.get().pointAt(x, y).toPointCh();
        int idNodeClosestTo = graph.nodeClosestTo(pointCh, SEARCH_DISTANCE);
        if (idNodeClosestTo == -1) {
            // Pas de nœud dans la distance de recherche.
            //THROW ...
            stringConsumer.accept("Erreur pas de noeud dans la distance de recherche.");
            // stringConsumer.accept(() -> System.out.println("1 Erreur pas de nœud dans la distance de recherche.");
            System.out.println("Une exception devrait être affichée sur l'interface graphique");
            return false;
        } else {
            // Ajout du Waypoint à liste des Waypoint.
            waypointList.add(new Waypoint(graph.nodePoint(idNodeClosestTo), idNodeClosestTo));
            //recreateGroups();
            return true;
        }
    }


    public void moveWaypoint(MouseEvent event, Node pin) {
        Point2D translation = previousCoordsOnScreen.subtract(event.getX(), event.getY());
        pin.setLayoutX(pin.getLayoutX() - translation.getX());
        pin.setLayoutY(pin.getLayoutY() - translation.getY());
        //System.out.println("Point 2d de position actuelle " + event.getX() + " " + event.getY());
        actualCoordinatePoint2D = new Point2D(pin.getLayoutX() - translation.getX(), pin.getLayoutY() - translation.getY());
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

    public void waypointRemoving(Node pin) {
        /*pin.setOnMouseClicked(event -> {
            if (initialCoordinatePoint2D.equals(actualCoordinatePoint2D)) {
                System.out.println(" remove le WayPOint ");
                waypointList.remove(pane.getChildren().indexOf(pin));
            }
        })*/;
    }

    public void waypointDragging(Node pin) {
        pin.setOnMouseClicked(event -> {
            if (event.isStillSincePress())
            waypointList.remove(pane.getChildren().indexOf(pin));
        });

        pin.setOnMousePressed(event -> {
            if (!event.isStillSincePress()) {
                System.out.println("SetOnMousePressed");
                previousCoordsOnScreen = new Point2D(event.getX(), event.getY());
                initialCoordinatePoint2D = pin.localToParent(event.getX(), event.getY());
                actualCoordinatePoint2D = pin.localToParent(event.getX(), event.getY());
                //System.out.println(" previous cordinate = " + previousCoordsOnScreen);
                //System.out.println("initialCoordinatePoint2d= " + initialCoordinatePoint2D);
                //System.out.println("actualCoordinatePoint2D = " + actualCoordinatePoint2D);
            } else {

            }
        });

        pin.setOnMouseDragged(event -> {
            moveWaypoint(event, pin);
            });

        pin.setOnMouseReleased(event -> {
            if (!initialCoordinatePoint2D.equals(actualCoordinatePoint2D)) {

                System.out.println("SetOnmouseRelased");
                relocateWaypoint(new Point2D(actualCoordinatePoint2D.getX(), actualCoordinatePoint2D.getY()), initialCoordinatePoint2D, pin);
                //addWaypoint(event.getX(), event.getY());
                previousCoordsOnScreen = new Point2D(0, 0);
                initialCoordinatePoint2D = previousCoordsOnScreen;
                actualCoordinatePoint2D = previousCoordsOnScreen;
            }
        });
    }

    public void relocateWaypoint(Point2D actualPosition, Point2D intialPosition, Node pin) {
        //System.out.println("actualPosition dans relocate= " + actualPosition);
        //System.out.println("intialPosition dans relocate = " + intialPosition);
        PointCh pointCh = mapViewParameters.get().pointAt(actualPosition.getX(), actualPosition.getY()).toPointCh();
        int idNodeClosestTo = graph.nodeClosestTo(pointCh, SEARCH_DISTANCE);
        if (idNodeClosestTo == -1) {
            // Pas de nœud dans la distance de recherche.
            //THROW ...
            stringConsumer.accept("Déplacement2.0 refusé => retour pas de changement de Waypoint lIst et il faut remettre à la bonne position le waypoint sur l'affichage graphique");
            // stringConsumer.accept(() -> System.out.println("1 Erreur pas de nœud dans la distance de recherche.");
            //pin.setLayoutX(intialPosition.getX());
            //pin.setLayoutY(intialPosition.getY());
            draw();
        } else {
            // Ajout du Waypoint à liste des Waypoint.
            int position = pane.getChildren().indexOf(pin);
            //waypointList.remove(position);
            waypointList.set(position, new Waypoint(graph.nodePoint(idNodeClosestTo), idNodeClosestTo));
            //recreateGroups();
        }

    }

    public void setUpListeners(Node pin) {
        waypointRemoving(pin);
        waypointDragging(pin);
    }
 }
