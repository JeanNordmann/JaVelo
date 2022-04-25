package ch.epfl.javelo.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.nio.file.Path;

public final class TestTileManager extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TileManager tm = new TileManager(
                Path.of("./diskMemory"), "tile.openstreetmap.org");
        for (int i = 185420; i < 185426; i++) {
            Image tileImage = tm.imageForTileAt(
                    new TileManager.TileId(19, 271725, i));
        }
        Image tileImage = tm.imageForTileAt(new TileManager.TileId(19, 271725, 185423));
        Image tileImage2 = tm.imageForTileAt(new TileManager.TileId(18, 23, 32));
        /*System.out.println(tm.memoryCache);*/
        Platform.exit();
    }
    // On a test sans écrire de test avec un plus petit cache (4)
    // Que les accèse cache disk et serveur se font dans les bons ordre en fonctions de l'arbre décisionnel
    // Que le cach se vide et s'actualise correctement
}
