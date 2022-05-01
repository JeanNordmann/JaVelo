package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.List;
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
        //TODO essayer avec un seul et même groupe.
        pane = new Pane();
        for (int i = 0; i < waypointList.size(); i++) {

            System.out.println("1 fois seulement");
            Group group = new Group(getAndSetOutsideBorder(), getAndSetInsideBorder());
            group.getStyleClass().add("pin");
            pane.getChildren().add(group);
        }

        //refreshGroups();
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

    /**
     * Méthode permettant d'ajouter une point de passage sur la carte à l'aide de ses coordonnées relatives
     * sur la carte affichée à l'écran.
     * @param x coordonnée X partante depuis le coin haut gauche
     * @param y coordonnée Y partante depuis le coin haut gauche
     */
    public void addWaypoint(double x, double y) {
        PointWebMercator pointWebMercator = mapViewParameters.get().pointAt(x, y);
        PointCh pointCh = pointWebMercator.toPointCh();
        int SEARCH_DISTANCE = 500;
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
            Group group = new Group(getAndSetOutsideBorder(), getAndSetInsideBorder());
            group.getStyleClass().add("pin");
            pane.getChildren().add(group);
            refreshGroups();
        }
    }

    public void moveWaypointOnMouseDragging(int index, double x, double y) {
        pane.getChildren().get(index).setLayoutX(x);
        pane.getChildren().get(index).setLayoutY(y);
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

    public void refreshGroups() {
        System.out.println("refresh groupe");
        if (pane.getChildren().size() > 0) {
            if (pane.getChildren().get(0).getStyleClass().size() == 1) {
                System.out.println("rien d'attecher j'attache pin");
                pane.getChildren().get(0).getStyleClass().add("first");
            } else  pane.getChildren().get(0).getStyleClass().set(1, "first");
            System.out.println(pane.getChildren().size());

        }
        if(pane.getChildren().size() > 1) {
            int i = pane.getChildren().size() - 1;
            if (pane.getChildren().get(i).getStyleClass().size() == 1) {
                pane.getChildren().get(i).getStyleClass().add("first");
            } else {
                pane.getChildren().get(i).getStyleClass().set(1, "last");
            }
        }
        for (int i = 1; i < pane.getChildren().size()-1; i++) {
            if (pane.getChildren().get(i).getStyleClass().size() == 1) {
            pane.getChildren().get(i).getStyleClass().add("middle");
        } else pane.getChildren().get(i).getStyleClass().set(1, "middle");

        }
    }

    public void removeWaypointJEAN(int i) {
        System.out.println("Je remove le WayPoint index : " + i);
        waypointList.remove(i);
        pane.getChildren().remove(i);

    }
    /**
     *
     */
    public void addMouseReleasing() {
        int j = 0;
        for (Waypoint waypoint : waypointList) {
            Group pin = (Group) pane.getChildren().get(j);
            pin.setOnMouseClicked(event -> {
                System.out.println("je suis sur le Waypoint");
                removeWaypointJEAN(waypointList.indexOf(waypoint));
            });
            j++;
        }
        refreshGroups();
        draw();
    }

    public void waypointDragging() {
        int j = 0;
        for (Waypoint waypoint : waypointList) {
            Group pin = (Group) pane.getChildren().get(j);
            pin.setOnMousePressed(event -> previousCoordsOnScreen.set(new Point2D(event.getX(), event.getY())));

            pin.setOnMouseDragged(event -> {
                System.out.println("je suis en train de bouger le waypoint zbi");
                moveWaypointOnMouseDragging(waypointList.indexOf(waypoint), event.getX(), event.getY());
            });
            ++j;
        }
    }

 }
