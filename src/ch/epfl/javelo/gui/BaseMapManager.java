package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
    private boolean redrawNeeded = false;
    private final ObjectProperty<Point2D> previousCoordsOnScreen;
    // Attribut nous rendre moins sensible le zoom.
    private double scrollValue;

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

        canvas.heightProperty().addListener((p, oldS, newS) -> redrawOnNextPulse());
        canvas.widthProperty().addListener((p, oldS, newS) -> redrawOnNextPulse());
        addMouseScrolling();
        addMouseClicking();
        addMouseDragging();
        redrawOnNextPulse();
        removeWpOnClicking();
        moveWpOnDragging();
        //TODO faire une méthode aillant un arbre de géstion d'évenement
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
        //de coordonnée (xMin, yMin) à la tuile (xMax, yMax)).
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
        waypointsManager.addMouseReleasing();
        waypointsManager.draw();
    }

    /**
     * Méthode permettant de gérer l'interaction de zoom de la carte affiché.
     * C'est-à-dire qui adapte le zoom si la souris ou le trackpad zoom ou dézoome.
     */
    public void addMouseScrolling() {
        canvas.setOnScroll((e) -> {
            // Temporisation nous permettant de rendre moins sensible le zoom.
            scrollValue += e.getDeltaY();
            if(Math.abs(scrollValue) >= 30) {
                double xOnScreen = e.getX(), yOnScreen = e.getY();

                // Récupération du point en PointWebMercator de la souris
                PointWebMercator pointWebMercator = mapViewParameters.get().pointAt(xOnScreen, yOnScreen);

                // Calcul du nouveau niveau de zoom (+1 ou -1)
                int newZoomLevel = Math2.clamp(MIN_ZOOM_LEVEL, mapViewParameters.get().zoomLevel()
                        + (scrollValue > 0 ? 1 : -1),MAX_ZOOM_LEVEL);

                mapViewParameters.set(new MapViewParameters(newZoomLevel,
                        pointWebMercator.xAtZoomLevel(newZoomLevel)-xOnScreen,
                        pointWebMercator.yAtZoomLevel(newZoomLevel)-yOnScreen));
                scrollValue = 0;
                //TODO ??? pas ici ???
                redrawOnNextPulse();
            }
        });
    }

    /**
     * Méthode permettant de gérer l'interaction de déplacement de la carte
     */
    public void addMouseDragging() {
        // Prise de la coordonnée au début du clic.
        canvas.setOnMousePressed((e) -> previousCoordsOnScreen.set(new Point2D(e.getX(), e.getY())));

        // Calcul de la position actuelle de la carte affichée en fonction du déplacement.
        canvas.setOnMouseDragged((e) -> {

                double deltaX = e.getX() - previousCoordsOnScreen.get().getX(),
                        deltaY = e.getY() - previousCoordsOnScreen.get().getY();
                int zoomLevel = mapViewParameters.get().zoomLevel();
                PointWebMercator pointWebMercator = mapViewParameters.get().pointAt(0, 0);
                mapViewParameters.set(new MapViewParameters(zoomLevel,
                        pointWebMercator.xAtZoomLevel(zoomLevel) - deltaX,
                        pointWebMercator.yAtZoomLevel(zoomLevel) - deltaY));
                //TODO ??? V ????
                redrawOnNextPulse();
                // Mise à jour de la coordonnée actuelle.
            previousCoordsOnScreen.set(new Point2D(e.getX(), e.getY()));
            });

        canvas.setOnMouseReleased((e) -> {
            if(e.isStillSincePress()) waypointsManager.addWaypoint(e.getX(), e.getY());
            previousCoordsOnScreen.set(null);
        });
    }

    /**
     * Méthode nous permettant d'ajouter un Waypoint à la position du clic.
     */
    public void addMouseClicking() {
        canvas.setOnMouseClicked((e) -> {
            if (e.isStillSincePress()) {
                waypointsManager.addWaypoint(e.getX(), e.getY());
                redrawOnNextPulse();
            }
        });
    }

    /**
     * Méthode gérant l'événement nous permettant de supprimer un WayPoint en cliquant dessus.
     *
     */
    public void removeWpOnClicking() {


    }

    /**
     * Méthode gérant l'évènement où un Waypoint est deplacé, et met à jour sa position.
     */

    public void moveWpOnDragging() {

    }

    /**
     * Méthode nous permettant d'accéder à l'attribut panneau de BaseMapManager.
     * @return Le panneau servant de conteneur à ses nœuds enfants notamment le canevas.
     */

    public Pane pane() {
        return pane;
    }

    /**
     * Méthode redessinant la carte si le booléen le demande.
     */

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        drawMap();

    }

    /**
     * Méthode changeant la valeur du booléen chargé de savoir si un nouveau dessin est nécessaire à
     * true, et demandant une nouvelle pulsation à la plateforme. Dans le but qu'on redessine la carte
     * au maximum à une fréquence prédéfini, pour économiser des ressources.
     */

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
