package ch.epfl.javelo.gui;
import ch.epfl.javelo.data.Graph;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

public final class AnnotatedMapManager {

    private final StackPane pane;

    private BaseMapManager baseMapManager;

    private WaypointsManager waypointsManager;

    private RouteManager routeManager;

    private Graph graph;

    private TileManager tileManager;

    private RouteBean bean;

    private Consumer<String> consumer;

    private DoubleProperty mousePositionOnRouteProperty;

    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean bean,
                               Consumer<String> consumer) {
        this.graph = graph;
        this.tileManager = tileManager;
        this.bean = bean;
        this.consumer = consumer;
        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);
        ObjectProperty<MapViewParameters> mapViewParametersP =
                new SimpleObjectProperty<>(mapViewParameters);
        ObservableList<Waypoint> waypoints =  FXCollections.observableArrayList();
        waypointsManager = new WaypointsManager(graph, mapViewParametersP, waypoints, consumer);
        baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersP);
        routeManager = new RouteManager(bean, mapViewParametersP);
        pane = new StackPane(baseMapManager.pane(), waypointsManager.pane(), routeManager.pane());
    }

    public StackPane pane() {
        return pane;
    }

    public DoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteProperty;
    }


}
