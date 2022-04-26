package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.List;
import java.util.function.Consumer;

public final class WaypointsManager {

    private Graph graph;
    private ObjectProperty<MapViewParameters> mapViewParameters;
    private ObservableList<Waypoint> waypointList;
    private Consumer<String> stringConsumer;
    private Pane pane;
    private Group group;


    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParameters,
                            ObservableList<Waypoint> waypointList, Consumer<String> stringConsumer) {
        this.graph = graph;
        this.mapViewParameters = mapViewParameters;
        this.waypointList = waypointList;
        this.stringConsumer = stringConsumer;
        SVGPath outsideBorder = new SVGPath();
        outsideBorder.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
        SVGPath insideBorder = new SVGPath();
        insideBorder.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
        group = new Group(outsideBorder, insideBorder);
        pane = new Pane(group);




    }

    public Pane pane() { return pane; }

    public void addWaypoint(double x, double y) {
        PointCh pCh = new PointCh(x, y);
        waypointList.add(new Waypoint(pCh, graph.nodeClosestTo(pCh, 500)));
    }
}
