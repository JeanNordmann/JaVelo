package ch.epfl.javelo.gui;

import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.awt.*;
import java.io.IOException;
import java.util.Map;

import static javax.swing.plaf.nimbus.ImageScalingHelper.drawImage;

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
    private ObjectProperty<MapViewParameters> mapViewParametersObjectProperty;
    private Pane pane;
    private Canvas canvas;

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty<MapViewParameters> mapViewParametersObjectProperty) throws IOException {
        this.tileManager = tileManager;
        this.waypointsManager = waypointsManager;
        this.mapViewParametersObjectProperty = mapViewParametersObjectProperty;
        pane = new Pane();
        pane.prefWidthProperty().set(200);
        pane.heightProperty().add(100);
        pane.setMaxSize(1000, 1000);
        pane.setMinSize(100, 100);
        pane.setPrefSize(200, 200);
        canvas = new Canvas();
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        drawImage(tileManager.imageForTileAt(new TileManager.TileId(19, 271725, 185422)));

    }

        public Pane pane() {
            return pane;
        }
}
