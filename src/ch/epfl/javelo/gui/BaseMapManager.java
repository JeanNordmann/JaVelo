package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * 8.3.2
 * BaseMapManager
 * <p>
 * Classe gérant l'affichage et l'interaction avec le fond de carte.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public final class BaseMapManager {

    private static final int TILE_SIZE = 256;
    private static final int MIN_ZOOM_LEVEL = 8;
    private static final int MAX_ZOOM_LEVEL = 19;

    private final TileManager tileManager;
    private final WaypointsManager waypointsManager;
    private final ObjectProperty<MapViewParameters> mapViewParameters;
    private final Pane pane;
    private final Canvas canvas;
    private boolean redrawNeeded;

    /**
     * Constructeur public du BaseMapManager gérant l'interaction avec le fond de carte.
     *
     * @param tileManager       Gestionnaire de tuile : nous permet de récupérer les tuiles.
     * @param waypointsManager  Gestionnaire de l'affichage et de l'interaction avec les points de passage.
     * @param mapViewParameters MapViewParameters observable dans une ObjectProperty → observable et permet la mise
     *                          à jour du fond de carte.
     */
    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager,
                          ObjectProperty<MapViewParameters> mapViewParameters) {
        this.tileManager = tileManager;
        this.waypointsManager = waypointsManager;
        this.mapViewParameters = mapViewParameters;
        this.canvas = new Canvas();
        this.pane = new Pane(canvas);
        previousCoordsOnScreen = new SimpleObjectProperty<>(new Point2D(0, 0));
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        redrawOnNextPulse();
        addMouseDragging();
    }

    /**
     * Méthode nous permettant d'accéder à l'attribut panneau de BaseMapManager.
     * @return Le panneau servant de conteneur à ses nœuds enfants notamment le canevas.
     */

    public Pane pane() {
        return pane;
    }

    /**
     * Méthode privée permettant de dessiner la carte sur le canvas, qui est dans le panneau.
     */

    private void drawMap() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        MapViewParameters actualMVP = mapViewParameters.get();
        //Coordonnées du point en haut à gauche de la fenêtre.
        Point2D topLeft = actualMVP.topLeft();
        //Coordonnées du point en bas à droite de la fenêtre.
        Point2D bottomRight = topLeft.add(canvas.getWidth(), canvas.getHeight());
        int zoomLevel = actualMVP.zoomLevel();
        //Coordonnées des tuiles minimales et maximales à draw (le rectangle de tuiles depuis la tuile
        //de coordonnée (xMin, yMin) à la tuile (xMax, yMax).
        int xMin = (int) topLeft.getX() / TILE_SIZE;
        int xMax = (int) bottomRight.getX() / TILE_SIZE;
        int yMin = (int) topLeft.getY() / TILE_SIZE;
        int yMax = (int) bottomRight.getY() / TILE_SIZE;

        //Position Y de destination du coin haut-gauche de la tuile à dessiner sur le canevas.
        int destinationY  = (int) -topLeft.getY() % TILE_SIZE;
        for (int y = yMin; y <= yMax; y++) {
            //Position X de destination du coin haut-gauche de la tuile à dessiner sur le canevas.
            int destinationX = (int) - topLeft.getX() % TILE_SIZE;
            for (int x = xMin; x <= xMax; x++) {
                try {
                    //Dessine la tuile actuelle, au niveau de zoom demandé, et à partir du pixel
                    //du bord du canevas, ce qui permet d'avoir des bouts de tuile, et non seulement
                    //des tuiles entières.
                    gc.drawImage(tileManager.imageForTileAt(new TileManager.TileId(zoomLevel, x, y)),
                            destinationX, destinationY);
                } catch (IOException ignored) {}
                //Incrémente les positions des valeurs X et Y de la longueur/largeur des tuiles.
                destinationX += TILE_SIZE;
            }
            destinationY += TILE_SIZE;
        }
    }

    /**
     * Méthode redessinant la carte si le booléen le demande.
     */

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        drawMap();
        redrawOnNextPulse();
    }

    /**
     * Méthode changeant la valeur du booléen chargé de savoir si un nouveau dessin est nécessaire à
     * true, et demandant une nouvelle pulsation à la plateforme.
     */

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

}
