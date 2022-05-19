package ch.epfl.javelo.gui;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.SwissBounds;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
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

    private final ObjectProperty<MapViewParameters> mapViewParametersP;

    private DoubleProperty mousePositionOnRouteProperty;

    private ObjectProperty<Point2D> mousePosition;

    //private static final ObjectProperty<Point2D> NAN_POINT_2D = new Point2D(D);

    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean bean,
                               Consumer<String> consumer) {
        this.graph = graph;
        this.tileManager = tileManager;
        this.bean = bean;
        this.consumer = consumer;
        mapViewParametersP =
                new SimpleObjectProperty<>(new MapViewParameters(12, 543200, 370650));
        waypointsManager = new WaypointsManager(graph, mapViewParametersP, bean.waypointsProperty(), consumer);
        baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersP);
        routeManager = new RouteManager(bean, mapViewParametersP);
        pane = new StackPane(baseMapManager.pane(), waypointsManager.pane(), routeManager.pane());
        pane.getStylesheets().add("map.css");
        mousePositionOnRouteProperty = new SimpleDoubleProperty(Double.NaN);
        mousePosition = new SimpleObjectProperty<>(new Point2D(Double.NaN, Double.NaN));

        pane.setOnMouseExited(e -> mousePosition.set(null));
        pane.setOnMouseMoved(e-> mousePosition.set(new Point2D(e.getX(), e.getY())));

        // NE PAS SUPRMIé sinon ne marche pas ! car le binding ne se fais pas !
         mousePosition.addListener((a,b,c) -> mousePosition.get());

        mousePositionOnRouteProperty.bind(Bindings.createDoubleBinding(() -> {
            if(bean.getRoute() == null) return Double.NaN ;
            if (mousePosition.get() == null) return Double.NaN;
            //todo A CLEAN
            PointCh mousePointCh = mapViewParametersP.get().
                    pointAt(mousePosition.get().getX(), mousePosition.get().getY())
                    .toPointCh();
            if (mousePointCh == null) return Double.NaN;
            PointCh closestMousePointCh = bean.getRoute().pointClosestTo(mousePointCh).point();
            PointWebMercator closestMousePWM = PointWebMercator.ofPointCh(closestMousePointCh);
            Point2D closestMousePoint2D = new Point2D(mapViewParametersP.get().viewX(closestMousePWM),
                    mapViewParametersP.get().viewY(closestMousePWM));
            return mousePosition.get().distance(closestMousePoint2D) <= 15
                    ? bean.getRoute().pointClosestTo(closestMousePointCh).position()
                    : Double.NaN;
        }, mousePosition));

    }

    public StackPane pane() {
        return pane;
    }

    public DoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteProperty;
    }

}
