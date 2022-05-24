package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
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

    /**
     * Constante représentant la taille en pixels d'une tuile.
     */
    private static final int TILE_SIZE = 256;

    /**
     * Constante représentant le niveau de zoom minimal de la carte, pour l'affichage.
     */
    private static final int MIN_ZOOM_LEVEL = 8;

    /**
     * Constante représentant le niveau de zoom maximal de la carte, pour l'affichage.
     */
    private static final int MAX_ZOOM_LEVEL = 19;

    /**
     * Attribut représentant le gestionnaire de tuiles.
     */
    private final TileManager tileManager;

    /**
     * Attributs représentant le gestionnaire de points de passage.
     */
    private final WaypointsManager waypointsManager;

    /**
     * Attribut représentant une propriété JavaFX contenant les paramètres de la carte affichée.
     */
    private final ObjectProperty<MapViewParameters> mapViewParameters;

    /**
     * Attribut représentant le panneau.
     */
    private final Pane pane;

    /**
     * Attribut représentant le canvas JavaFX.
     */
    private final Canvas canvas;

    /**
     * Booléen dont la valeur représente si un nouveau dessin est nécessaire.
     */
    private boolean redrawNeeded;

    /**
     * Attribut représentant une propriété JavaFX contenant un Point2D stockant
     * les précédentes coordonnées sur l'écran.
     */
    private final ObjectProperty<Point2D> previousCoordsOnScreen;


    /**
     * Constructeur public du BaseMapManager gérant l'interaction avec le fond de carte, et initialisant
     * les attributs à leurs valeurs données ou par défaut.
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

        //Initialise ces attributs à leurs valeurs par défaut.
        previousCoordsOnScreen = new SimpleObjectProperty<>(new Point2D(0, 0));
        redrawNeeded = false;

        //Lie les propriétés des largeurs et des hauteurs du canvas à celles du panneau.
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        //Configure les auditeurs.
        setUpListeners();

        //Configure les gestionnaires d'évènements.
        eventHandler();
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
        //Seul appel à updateWaypointsLocations depuis BaseMapManager.
        waypointsManager.updateWaypointsLocations();
    }

    /**
     * Méthode privée ajoutant les gestionnaires d'évènements correspondants au zoom, au glissement de la carte,
     * et à l'ajout d'un point de passage sur la carte.
     */

    private void eventHandler() {
        //Ajoute les gestionnaires d'évènements correspondants au zoom, au glissement de la carte,
        //et à l'ajout d'un point de passage sur la carte.
        addMouseDragging();
        addMouseClicking();
        addMouseScrolling();
    }

    private void setUpListeners() {

        //Ajoute un auditeur pour redessiner la carte que lorsque cela est nécessaire (donné par
        //le professeur).
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        //Ajoute les auditeurs demandant un nouveau dessin du canevas si ses dimensions changent.
        canvas.heightProperty().addListener((p, oldS, newS) -> redrawOnNextPulse());
        canvas.widthProperty().addListener((p, oldS, newS) -> redrawOnNextPulse());

        //Ajoute l'auditeur demandant un nouveau dessin si les paramètres de vue de la carte
        //changent.
        mapViewParameters.addListener((p, oldS, newS) -> redrawOnNextPulse());
    }

    /**
     * Méthode permettant de gérer l'interaction de zoom de la carte affichée.
     * Elle adapte le zoom si la souris ou le trackpad zoom ou dézoome.
     */

    private void addMouseScrolling() {
        //Gestionnaire d'évènements concertant le zoom et dé-zoom de la carte, à l'aide du
        //glissement de la roulette ou du trackpad. (Un changement possible toutes les 0,2
        //seconde).
        SimpleLongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(e -> {
            if (e.getDeltaY() == 0d) return;
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            double xOnScreen = e.getX(), yOnScreen = e.getY();
            // Récupération du point en PointWebMercator de la souris
            PointWebMercator pointWebMercator = mapViewParameters.get().pointAt(xOnScreen, yOnScreen);

            // Calcul du nouveau niveau de zoom (+1 ou -1)
            int newZoomLevel = Math2.clamp(MIN_ZOOM_LEVEL, mapViewParameters.get().zoomLevel()
                    + (zoomDelta > 0 ? 1 : -1), MAX_ZOOM_LEVEL);

            mapViewParameters.set(new MapViewParameters(newZoomLevel,
                    pointWebMercator.xAtZoomLevel(newZoomLevel) - xOnScreen,
                    pointWebMercator.yAtZoomLevel(newZoomLevel) - yOnScreen));
        });
    }

    /**
     * Méthode permettant de gérer l'interaction de déplacement de la carte.
     */
    private void addMouseDragging() {
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

                // Mise à jour de la coordonnée actuelle.
                previousCoordsOnScreen.set(new Point2D(e.getX(), e.getY()));
            });
    }

    /**
     * Méthode nous permettant d'ajouter un Waypoint à la position du clic.
     */
    private void addMouseClicking() {
        canvas.setOnMouseClicked((e) -> {
            if (e.isStillSincePress()) {
                waypointsManager.addWaypoint(e.getX(), e.getY());
            }
        });
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
     * true, et demandant une nouvelle pulsation à la plateforme. Dans le but qu'on redessine
     * la carte au maximum à une fréquence prédéfinie, pour économiser des ressources.
     */

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
