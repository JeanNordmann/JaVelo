package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.awt.*;
import java.io.IOException;
import java.util.Map;

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

    private TileManager tileManager;
    private WaypointsManager waypointsManager;
    private ObjectProperty<MapViewParameters> mapViewParameters;
    private Pane pane;
    private javafx.scene.canvas.Canvas canvas;
    private boolean redrawNeeded;

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty<MapViewParameters> mapViewParameters) throws IOException {
        this.tileManager = tileManager;
        this.waypointsManager = waypointsManager;
        this.mapViewParameters = mapViewParameters;
        canvas = new Canvas();
        pane = new Pane(canvas);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        drawMap();
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        redrawOnNextPulse();
    }

    public Pane pane() { return pane; }

    private void drawMap() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int zoomLevel = mapViewParameters.get().zoomLevel();
        double mvpX = mapViewParameters.get().x();
        double mvpY = mapViewParameters.get().y();
        int numberXofSectors = Math2.ceilDiv((int) canvas.getWidth(), 1 << zoomLevel);
        int numberYofSectors = Math2.ceilDiv((int) canvas.getHeight(), 1 << zoomLevel);

        for (double x = mvpX; x <= mvpX + numberXofSectors; x++) {
            for (double y = mvpY; y <= mvpY + numberYofSectors; y++) {
                try {
                    double viewX = mapViewParameters.get().viewX(new PointWebMercator(x, y));
                    double viewY = mapViewParameters.get().viewY(new PointWebMercator(x, y));
                    gc.drawImage(tileManager.imageForTileAt(new TileManager.TileId(mapViewParameters.get().zoomLevel(), (int) x, (int) y)),
                            viewX, viewY);
                } catch(IOException ignored) {
                }
            }
        }
    }

    private void redrawIfNeeded() {
        if(!redrawNeeded) return;
        redrawNeeded = false;
        drawMap();
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

}
