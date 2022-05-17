
package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.ElevationProfileComputer;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

        //TODO BIND LA HIGHLIGHTED POSITION
        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph, tileManager,
                routeBean, errorManager::displayError);
        ElevationProfileManager elevationProfileManager =
                new ElevationProfileManager(routeBean.elevationProfileProperty(),
                        routeBean.highlightedPositionProperty());
        SplitPane splitPane = new SplitPane(annotatedMapManager.pane());
        splitPane.setOrientation(Orientation.VERTICAL);

        routeBean.elevationProfileProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && oldValue == null) {
                splitPane.getItems().add(elevationProfileManager.pane());
            }
            if (newValue == null) {
                splitPane.getItems().remove(elevationProfileManager.pane());
            }
        });

//TODO CREER UN SPLITPANE AVEC LA CARTE ET L ELEVATIONPROFILEMANAGER
        //Après il faut ajouter le errorManager au STACKPANE (et pas splitpane)
        //SPLIT --> diviser le panneau en 2, ce qu'on peut pour la carte et le profil
        //STACK --> on empile un truc par dessus un autre, ce qu'on veut pour mettre les erreurs
        // par dessus le reste de la carte/profil


        StackPane mainPane = new StackPane(splitPane, errorManager.pane());
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("JaVelo");
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }

}



