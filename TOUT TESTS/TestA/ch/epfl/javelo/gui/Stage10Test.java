package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;

import static javafx.application.Application.launch;

public  final class Stage10Test extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        CityBikeCF costFunction = new CityBikeCF(graph);
        RouteComputer routeComputer =
                new RouteComputer(graph, costFunction);

        int coo1 = (int) (Math.random() * 212000);
        int coo2 = (int) (Math.random() * 212000);
        Route route = routeComputer
                .bestRouteBetween(159049, 117669);
        ElevationProfile profile = ElevationProfileComputer
                .elevationProfile(route, 5);

        ObjectProperty<ElevationProfile> profileProperty =
                new SimpleObjectProperty<>(profile);
        DoubleProperty highlightProperty =
                new SimpleDoubleProperty(1500);

        ElevationProfileManager profileManager =
                new ElevationProfileManager(profileProperty,
                        highlightProperty);
       highlightProperty.bind(profileManager.mousePositionOnProfileProperty());
        Pane pane = profileManager.pane();
        Scene scene = new Scene(pane);

        //Pour attendre 1minute
        System.out.println("");
        System.out.println(" juste avant de redéfinir la tailler de de la fenetre");
        System.out.println("");
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(300);

        //Pour attendre 1minute
        System.out.println("");
        System.out.println(" juste après de redéfinir la tailler de de la fenetre");
        System.out.println("");
        primaryStage.setScene(scene);
        primaryStage.show();
        //Pour attendre 1minute

        System.out.println("");
        System.out.println("fin test 10");
        System.out.println("");
    }
}
