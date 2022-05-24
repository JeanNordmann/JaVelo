package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.function.Consumer;

public final class Stage8Test extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        Path cacheBasePath = Path.of(".");
        String tileServerHost = "tile.openstreetmap.org";
        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);

        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);
        ObjectProperty<MapViewParameters> mapViewParametersP =
                new SimpleObjectProperty<>(mapViewParameters);
        ObservableList<Waypoint> waypoints =
                FXCollections.observableArrayList(
                        new Waypoint(new PointCh(2581506, 1100769), 590576),
                        new Waypoint(new PointCh(2581286, 1098396), 593609));
        Consumer<String> errorConsumer = new ErrorConsumer();

        RouteBean routeBean = new RouteBean(new RouteComputer(graph, new CityBikeCF(graph)));
        routeBean.setWaypoints(waypoints);
        routeBean.setHighlightedPosition(1000);

        WaypointsManager waypointsManager =
                new WaypointsManager(graph,
                        mapViewParametersP,
                        routeBean.waypointsProperty(),
                        errorConsumer);
        BaseMapManager baseMapManager =
                new BaseMapManager(tileManager,
                        waypointsManager,
                        mapViewParametersP);
        RouteManager routeManager = new RouteManager(routeBean, mapViewParametersP);
        StackPane mainPane =
                new StackPane(baseMapManager.pane(),
                        waypointsManager.pane(),
                        routeManager.pane());

        RouteBean bean = new RouteBean(new RouteComputer(graph, new CityBikeCF(graph)));

        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph, tileManager,
                bean, null);
        mainPane.getStylesheets().add("map.css");




        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(300);
        primaryStage.setScene(new Scene(annotatedMapManager.pane()));
        primaryStage.show();
    }

    private static final class ErrorConsumer
            implements Consumer<String> {
        @Override
        public void accept(String s) { System.out.println(s); }
    }
    /*@Test
    void transormTest() {
        ReadOnlyObjectProperty<ElevationProfile> readOnlyObjectProperty =
                new SimpleObjectProperty<>(null);
        ReadOnlyObjectProperty<Double> d = new Read<Double>(2) {
        };
        ElevationProfileManager elevationProfile =
                new ElevationProfileManager(readOnlyObjectProperty, d);

    }*/
}
