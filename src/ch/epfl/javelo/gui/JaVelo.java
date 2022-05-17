
package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;


public class JaVelo extends Application {

    public static void main(String[] args) { launch(args); }

    public void start(Stage primaryStage) throws Exception {
        // création et chargement du graph
        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        Path cacheBasePath = Path.of("./osm-cache");
        String tileServerHost = "tile.openstreetmap.org";
        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);
        ErrorManager errorManager = new ErrorManager();

        RouteBean routeBean = new RouteBean(new RouteComputer(graph, new CityBikeCF(graph)));
        //création AnnotatedMapManager
        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph, tileManager,
                routeBean, errorManager::displayError);
        //création ElevationProfileManager
        ElevationProfileManager elevationProfileManager =
                new ElevationProfileManager(routeBean.elevationProfileProperty(),
                        routeBean.highlightedPositionProperty());

        SplitPane splitPane = new SplitPane(annotatedMapManager.pane());
        splitPane.setOrientation(Orientation.VERTICAL);

        routeBean.elevationProfileProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && oldValue == null) {
                splitPane.getItems().add(elevationProfileManager.pane());
                SplitPane.setResizableWithParent(elevationProfileManager.pane(), false);
            }
            if (newValue == null) {
                splitPane.getItems().remove(elevationProfileManager.pane());
            }
        });

        StackPane mainPane = new StackPane(splitPane, errorManager.pane());
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("JaVelo");
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }

}



