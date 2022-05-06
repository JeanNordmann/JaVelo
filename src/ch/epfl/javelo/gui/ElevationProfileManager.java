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
    /**
     * Attribut représentant le panneau contenant le dessin du profil.
     */
    private final BorderPane borderPane;

    private ReadOnlyDoubleProperty mousePositionOnProfile;

    private int numberOfTextsNeeded;
    private  ObjectProperty<Rectangle2D> rectangle2D;
    private ObjectProperty<Transform> screenToWorldTransform;
    private ObjectProperty<Transform> worldToScreenTransform;
    private final Insets insets;
    private final int[] POS_STEPS =
            { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
    private final int[] ELE_STEPS =
            { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };
    private final double MIN_PIXEL_POS = 50;
    private final double MIN_PIXEL_ELE = 25;
    private final IntegerProperty posStep;
    private final IntegerProperty eleStep;


    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfile,
                                   ReadOnlyDoubleProperty highlightedPosition) {
        this.elevationProfile = elevationProfile;
        this.highlightedPosition = highlightedPosition;
        borderPane = new BorderPane();

    }


    private void setTransformation() {
        // à la place du Listener on devrait bind
        Affine affine = new Affine();
        // Décale au coin au gauche du rectangle
        affine.prependTranslation(-insets.getLeft(), -insets.getTop());
        // inverse les coordonnées de l'altitude. Puis Ajout de la hauteur
        // Dans le but que pour une une hauteur de 50
        // 50->400      deviennent   0 -> 400
        // 25->800      deviennent   25-> 800
        // 0->1200      deviennent   50-> 1200
        affine.prependScale(1, -1);
        affine.prependTranslation(0, rectangle2D.get().getHeight());
        // changement d'échelle
        double deltaElevation = (elevationProfile.get().maxElevation() - elevationProfile.get().minElevation());
        affine.prependScale(1 / rectangle2D.get().getWidth(), 1 / rectangle2D.get().getHeight());
        affine.prependScale(elevationProfile.get().length(), deltaElevation);
        // décalement de l'altitude
        affine.prependTranslation(0,elevationProfile.get().minElevation());
        screenToWorldTransform.set(affine);
        try {
            worldToScreenTransform.set(affine.createInverse());
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
        }

    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePositionOnProfile;
    }

    private void setUpListener() {

        elevationProfile.addListener((p,oldV,newV) -> {
            if (oldV.minElevation() != newV.minElevation() || oldV.maxElevation() != newV.maxElevation())
            setTransformation();
            });

        rectangle2D.addListener(e -> setTransformation());

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


    /**
     * Méthode publique retournant le panneau de la classe.
     * @return Le panneau contenant le dessin du profil, de type Pane.
     */
    public BorderPane pane() {
        return borderPane;
    }

    private void bindHighlightedPosition() {
   /*     line.setLayoutX(Bindings.createDoubleBinding( , highlightedPosition);*/
        Bindings.select(rectangle2D, "minY");

    }

    private int computeVerticalLinesSpacing() {
        for (int pos_step : POS_STEPS) {
            double minPixel =
                    rectangle2D.get().getWidth() / (elevationProfile.get().length() / pos_step);
            if (minPixel >= MIN_PIXEL_POS) {
                return pos_step;
            }
        }
        // pour get le dernier.
        return POS_STEPS[POS_STEPS.length - 1];
    }

    private int computeHorizontalLinesSpacing() {
        for (int ele_step : ELE_STEPS) {
            double minPixel = rectangle2D.get().getHeight()
                    / ((elevationProfile.get().maxElevation() - elevationProfile.get().minElevation()) / ele_step);
            if (minPixel >= MIN_PIXEL_ELE) {
                return ele_step;
            }
        }
        // pour get le dernier.
        return ELE_STEPS[ELE_STEPS.length - 1];
    }

    private int numberOfHorizontalLine() {
        return 0;
    }
    private int numberOfVerticalLine() {
     return 0;
    }

    private void formatStatistics() {
        //TODO set les layouts du truc
        statisticsText.setText(String.format("Longueur : %.1f km" +
                "     Montée : %.0f m" +
                "     Descente : %.0f m" +
                "     Altitude : de %.0f m à %.0f m",
                elevationProfile.get().length(),
                elevationProfile.get().totalAscent(),
                elevationProfile.get().totalDescent(),
                elevationProfile.get().minElevation(),
                elevationProfile.get().maxElevation()));
    }

    private void labelsValues() {

    }

    private void setUpEventHandlers() {
        pane.setOnMouseMoved(event -> {
            if (isInBlueRectangle(event.getX(), event.getY())) {
                Point2D worldCoordinates = screenToWorldTransform.get().transform(event.getX(),
                        event.getY());
                mousePositionOnProfile.set(worldCoordinates.getX());

            } else {
                mousePositionOnProfile.set(Double.NaN);
            }
        });
        pane.setOnMouseExited(event -> mousePositionOnProfile.set(Double.NaN));
    }

    private double[] getBlueRectangleDimensions() {
        double[] dimensions = new double[4];
        dimensions[0] = insets.getLeft();
        dimensions[1] = insets.getTop();
        dimensions[2] = pane.getWidth() - insets.getRight();
        dimensions[3] = pane.getHeight() - insets.getBottom();
        return dimensions;
    }

    private boolean isInBlueRectangle(double x, double y) {
        return x >= insets.getLeft() && x <= pane.getWidth() - insets.getRight()
                && y >= insets.getTop() && y <= pane.getHeight() - insets.getBottom();
    }
}
