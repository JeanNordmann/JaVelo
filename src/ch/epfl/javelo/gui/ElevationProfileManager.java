package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

public final class ElevationProfileManager {

    /**
     * Attribut représentant une propriété, accessible en lecture seule,
     * contenant le profil à afficher.
     */
    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfile;

    /**
     * Propriété, accessible en lecture seule, contenant la position le long
     * du profil à mettre en évidence.
     */
    private final ReadOnlyDoubleProperty highlightedPosition;
//TODO pas 2 waypoint au meme endroit
    // TODO utiliser indexOF

    /**
     * Attribut représentant le panneau contenant le dessin du profil.
     */
    private final BorderPane borderPane;

    private ReadOnlyDoubleProperty mousePositionOnProfile;

    private int numberOfTextsNeeded;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfile,
                                   ReadOnlyDoubleProperty highlightedPosition) {
        this.elevationProfile = elevationProfile;
        this.highlightedPosition = highlightedPosition;
        borderPane = new BorderPane();

    }

    /**
     * Méthode publique retournant le panneau de la classe.
     * @return Le panneau contenant le dessin du profil, de type Pane.
     */
    public BorderPane pane() {
        return borderPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePositionOnProfile;
    }

    private void setUpProfileDisplay() {

        Path path = new Path();
        path.setId("grid");

        Group group = new Group();
        Text text = new Text();
        text.getStyleClass().add("grid_label");
        text.getStyleClass().add("horizontal");
        group.getChildren().add(text);
        Text text1 = new Text();
        text1.getStyleClass().add("grid_label");
        text1.getStyleClass().add("vertical");
        group.getChildren().add(text1);

        Polygon polygon = new Polygon();
        polygon.setId("profile");

        Line line = new Line();

        Pane pane = new Pane();

        pane.getChildren().add(path);
        pane.getChildren().add(group);
        pane.getChildren().add(polygon);
        pane.getChildren().add(line);




        borderPane.getStylesheets().set(0, "elevation_profile.css");

    }
}
