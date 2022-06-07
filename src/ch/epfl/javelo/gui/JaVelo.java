
package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.GpxGenerator;
import ch.epfl.javelo.routing.RouteComputer;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;



/**
 * 11.3.4
 * JaVelo
 * <p>
 * Classe gérant le lancement de l'application JaVelo.
 *
 * @author Maxime Ducourau (329544)
 * @author Jean Nordmann (344692)
 */

public final class JaVelo extends Application {

    //Constante représentant la valeur minimale de la largeur de la fenêtre de l'application.
    private static final double MIN_WINDOW_WIDTH = 800;

    //Constante représentant la valeur minimale de la hauteur de la fenêtre de l'application.
    private static final double MIN_WINDOW_HEIGHT = 600;

    public static void main(String[] args) { launch(args); }

    /**
     * Méthode permettant le lancement de l'application sur l'emplacement donné en paramètres.
     * @param primaryStage Emplacement sur lequel les acteurs de l'application sont placés.
     * @throws Exception En cas d'erreur liée au lancement de l'application.
     */
    public void start(Stage primaryStage) throws Exception {
        //Création et chargement du graphe.
        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        Path cacheBasePath = Path.of("./osm-cache");
        String tileServerHost = "tile.openstreetmap.org";

        //Création du gestionnaire de tuiles.
        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);
        ErrorManager errorManager = new ErrorManager();
        RouteBean routeBean = new RouteBean(new RouteComputer(graph, new CityBikeCF(graph)));

        //Création du gestionnaire de carte annotée, avec un consommateur d'erreur liée à la
        //méthode displayError de ErrorManager.
        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph, tileManager,
                routeBean, errorManager::displayError);
        ElevationProfileManager elevationProfileManager =
                new ElevationProfileManager(routeBean.elevationProfileProperty(),
                        routeBean.highlightedPositionProperty());

        //Crée un panneau séparé du gestionnaire de carte annotée.
        SplitPane splitPane = new SplitPane(annotatedMapManager.pane());
        splitPane.setOrientation(Orientation.VERTICAL);

        //Lie la propriété de la position surlignée.
        routeBean.highlightedPositionProperty().bind(Bindings
                .when(annotatedMapManager.mousePositionOnRouteProperty().greaterThanOrEqualTo(0))
                .then(annotatedMapManager.mousePositionOnRouteProperty())
                .otherwise(elevationProfileManager.mousePositionOnProfileProperty()));

        //Auditeur permettant d'ajouter ou d'enlever le profil si la route existe ou non.
        routeBean.elevationProfileProperty().addListener((observable, oldValue, newValue) -> {
            //Dessine le nouveau profil si l'ancien profil était nul, et pas le nouveau.
            if (newValue != null && oldValue == null) {
                splitPane.getItems().add(elevationProfileManager.pane());
                //Permet de ne pas redimensionner le panneau du gestionnaire de profil avec le
                //panneau général.
                SplitPane.setResizableWithParent(elevationProfileManager.pane(), false);
            }

            //Retire le panneau du gestionnaire de profil s'il n'existe plus.
            /*if (newValue == null) {
                splitPane.getItems().remove(elevationProfileManager.pane());
            }*/
        });

        //Crée la barre de menu, le bouton "Fichier", ouvrant un item appelé "Exporter GPX".
        MenuItem menuItem = new MenuItem("Exporter GPX");
        Menu menu = new Menu("Fichier");
        MenuBar menuBar = new MenuBar();
        menu.getItems().add(menuItem);
        menuBar.getMenus().add(menu);

        //La barre est rendue invisible si la route est nulle.
        menuItem.disableProperty().bind(routeBean.routeProperty().isNull());

        //Si une route existe, alors se débloque la possibilité de l'exporter au format GPX grâce
        //à la méthode writeGpx de la classe GpxGenerator.
        menuItem.setOnAction(event -> {
            try {
                GpxGenerator.writeGpx("javelo.gpx", routeBean.getRoute(),
                        routeBean.getElevationProfile());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });

        //Crée le panneau empilé contenant le panneau du gestionnaire de carte annotée, et
        //par-dessus celui du gestionnaire d'erreurs.
        StackPane pane = new StackPane(splitPane, errorManager.pane());

        //Crée le panneau principal avec bordures, contenant le panneau empilé ci-dessus et la
        //barre de menus. (Seule manière trouvée sur Piazza de ne pas avoir la barre de menus en
        //plein milieu de la fenêtre).
        BorderPane mainPane = new BorderPane();
        mainPane.setTop(menuBar);
        mainPane.setCenter(pane);

        //Dimensions minimales de la fenêtre.
        primaryStage.setMinWidth(MIN_WINDOW_WIDTH);
        primaryStage.setMinHeight(MIN_WINDOW_HEIGHT);

        //Nom de l'application.
        primaryStage.setTitle("JaVelo");

        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }
}



