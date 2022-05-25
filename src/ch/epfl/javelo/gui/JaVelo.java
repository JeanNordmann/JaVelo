
package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

import static javafx.beans.binding.Bindings.createDoubleBinding;


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

        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph, tileManager,
                routeBean, errorManager::displayError);
        ElevationProfileManager elevationProfileManager =
                new ElevationProfileManager(routeBean.elevationProfileProperty(),
                        routeBean.highlightedPositionProperty());
        SplitPane splitPane = new SplitPane(annotatedMapManager.pane());
        splitPane.setOrientation(Orientation.VERTICAL);
        //TODO essayer de modifier avec :
        //Bindings.when et .then c'est hyper stylé.
        routeBean.highlightedPositionProperty().bind(createDoubleBinding(() ->{
        if (annotatedMapManager.mousePositionOnRouteProperty().get() >= 0) {
            return annotatedMapManager.mousePositionOnRouteProperty().get();
        } else {
            return elevationProfileManager.mousePositionOnProfileProperty().get();
        }}, elevationProfileManager.mousePositionOnProfileProperty(),
                annotatedMapManager.mousePositionOnRouteProperty()));

        //Auditeur permettant d'ajouter ou enlever le profil
        routeBean.elevationProfileProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && oldValue == null) {
                splitPane.getItems().add(elevationProfileManager.pane());
                SplitPane.setResizableWithParent(elevationProfileManager.pane(), false);
            }
            if (newValue == null) {
                splitPane.getItems().remove(elevationProfileManager.pane());
            }
        });

        MenuItem menuItem = new MenuItem("Exporter GPX");
        Menu menu = new Menu("Fichier");
        MenuBar menuBar = new MenuBar();

        menu.getItems().add(menuItem);
        menuBar.getMenus().add(menu);

        //La barre est rendue invisible si la route est nulle.
        menuItem.disableProperty().bind(routeBean.routeProperty().isNull());

        menuItem.setOnAction(event -> {
            try {
                GpxGenerator.writeGpx("javelo.gpx", routeBean.getRoute(),
                        routeBean.getElevationProfile());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });

        StackPane pane = new StackPane(splitPane, errorManager.pane());
        BorderPane mainPane = new BorderPane();
        mainPane.setTop(menuBar);
        mainPane.setCenter(pane);

        primaryStage.getIcons().add(new Image("icon.jpg"));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("JaVelo");
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }
}



