package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.*;

import java.util.ArrayList;
import java.util.List;
//TODO
//STATIC DEVANT LES CONSTANTE
public final class ElevationProfileManager {
//TODO faire waypoint corectement !
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
    private Pane pane;
    private VBox vBox;
    private Path path;
    private Group group;
    private Polygon polygon;
    private Line line;
    private Text statisticsText;
    private Text text;
    private DoubleProperty mousePositionOnProfile;
    private int numberOfTextsNeeded;
    private ObjectProperty<Rectangle2D> rectangle2D;
    private ObjectProperty<Transform> screenToWorldTransform;
    private ObjectProperty<Transform> worldToScreenTransform;
    private final Insets insets = new Insets(10, 10, 20, 40);
    private final int[] POS_STEPS =
            { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
    private final int[] ELE_STEPS =
            { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };
    private static final double MIN_PIXEL_POS = 50;
    private static final double MIN_PIXEL_ELE = 25;
    private final IntegerProperty posStep;
    private final IntegerProperty eleStep;


    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfile,
                                   ReadOnlyDoubleProperty highlightedPosition) {
        this.elevationProfile = elevationProfile;
        this.highlightedPosition = highlightedPosition;
        borderPane = new BorderPane();
        borderPane.getStylesheets().add("elevation_profile.css");
        // Objet initialisé à des valeurs artificiellement triviales pour éviter des erreurs
        //TODO A CHANGER
        rectangle2D = new SimpleObjectProperty<>(new Rectangle2D(insets.getLeft(),
                insets.getTop(), insets.getLeft(), insets.getTop()));

        worldToScreenTransform = new SimpleObjectProperty<>(Transform.translate(0,0));
        screenToWorldTransform = new SimpleObjectProperty<>(Transform.translate(0,0));
        setUpProfileDisplay();
        rectangle2D.bind;
        borderPane.widthProperty().addListener((observable, oldValue, newValue) -> pane.setPrefWidth(borderPane.getWidth()));
        borderPane.heightProperty().addListener((observable, oldValue, newValue) -> pane.setPrefHeight(borderPane.getHeight()));
        posStep = new SimpleIntegerProperty(computeVerticalLinesSpacing());
        eleStep = new SimpleIntegerProperty(computeHorizontalLinesSpacing());
        setUpListener();
        //setRectangle2D();
        computePolygon();
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

    private void setUpListener() {
        // Listeners liés à l'interface graphique.
        borderPane.widthProperty().addListener(e -> setRectangle2D());
        borderPane.heightProperty().addListener(e -> setRectangle2D());

        //TODO idée mettre en attribut les steps et les actualiser...
        rectangle2D.addListener(e -> {
            initializeGridAndLabels();
            computePolygon();
        });


        elevationProfile.addListener((p,oldV,newV) -> {
            if (oldV.minElevation() != newV.minElevation() || oldV.maxElevation() != newV.maxElevation())
            setTransformation();
            computePolygon();
            });

        rectangle2D.addListener(e -> setTransformation());
    }

    private void setUpProfileDisplay() {


        path = new Path();
        group = new Group();
        polygon = new Polygon();
        polygon.setId("profile");
        line = new Line();
        pane = new Pane();
        initializeGridAndLabels();

        statisticsText = new Text();
        vBox = new VBox();
        vBox.setId("profile_data");
        vBox.getChildren().add(statisticsText);


        pane.getChildren().add(path);
        pane.getChildren().add(group);
        pane.getChildren().add(polygon);
        pane.getChildren().add(line);
        borderPane.setCenter(pane);
        borderPane.setBottom(vBox);
    }

    private void bindHighlightedPosition() {
   /*     line.setLayoutX(Bindings.createDoubleBinding( , highlightedPosition);*/
        Bindings.select(rectangle2D, "minY");

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



    private boolean isInBlueRectangle(double x, double y) {
        return x >= insets.getLeft() && x <= pane.getWidth() - insets.getRight()
                && y >= insets.getTop() && y <= pane.getHeight() - insets.getBottom();
    }

    private void initializeGridAndLabels() {
        int minElevation =  (int) elevationProfile.get().minElevation();
        int maxElevation = (int) elevationProfile.get().maxElevation();

        double elevationLength = elevationProfile.get().length();

        int spaceBetween2HLines = computeHorizontalLinesSpacing();
        int spaceBetween2VLines = computeVerticalLinesSpacing();

        int numberOfHLines = numberOfHorizontalLine();
        int numberOfVLines = numberOfVerticalLine();

        int initialHLine = Math2.ceilDiv(minElevation, spaceBetween2HLines);

        Transform worldToScreen = worldToScreenTransform.get();
        List<PathElement> pathElementList = new ArrayList<>();

        if (group != null) group.getChildren().clear();
        if (pane != null) pane.getChildren().clear();

        //Ajout de la ligne en bas du rectangle bleu si elle ne nécessite pas d'étiquette.
        if (minElevation % spaceBetween2HLines != 0) {
            Point2D point2DMoveTo = worldToScreen.deltaTransform(0, minElevation);
            Point2D point2DLineTo = worldToScreen.deltaTransform(elevationLength, minElevation);
            pathElementList.add(new MoveTo(point2DMoveTo.getX(), point2DMoveTo.getY()));
            pathElementList.add(new LineTo(point2DLineTo.getX(), point2DLineTo.getY()));
        }

        //Ajout de la ligne à droite du rectangle bleu si elle ne nécessite pas d'étiquette.
        if (elevationLength % spaceBetween2VLines != 0) {
            Point2D point2DMoveTo = worldToScreen.deltaTransform(elevationLength, minElevation);
            Point2D point2DLineTo = worldToScreen.deltaTransform(elevationLength, maxElevation);
            pathElementList.add(new MoveTo(point2DMoveTo.getX(), point2DMoveTo.getY()));
            pathElementList.add(new LineTo(point2DLineTo.getX(), point2DLineTo.getY()));
        }

        for (int i = 0; i < numberOfHLines; i++) {
            int variable = (initialHLine + i) * spaceBetween2HLines;
            Point2D point2DMoveTo = worldToScreen.deltaTransform(0, variable);
            Point2D point2DLineTo = worldToScreen.deltaTransform(elevationLength, variable);
            PathElement moveTo = new MoveTo(point2DMoveTo.getX(), point2DMoveTo.getY());
            PathElement lineTo = new LineTo(point2DLineTo.getX(), point2DLineTo.getY());
            pathElementList.add(moveTo);
            pathElementList.add(lineTo);
            Text text = new Text(Integer.toString(variable));
            text.setLayoutX(point2DMoveTo.getX());
            text.setLayoutY(point2DMoveTo.getY());
            text.setTextOrigin(VPos.CENTER);
            text.setFont(Font.font("Avenir", 10));
            text.prefWidth(text.getWrappingWidth() + 2);
            text.getStyleClass().add("grid_label");
            text.getStyleClass().add("vertical");
            group.getChildren().add(text);
        }

        for (int i = 0; i < numberOfVLines; i++) {
            int variable = i * spaceBetween2VLines;
            Point2D point2DMoveTo = worldToScreen.deltaTransform(variable, minElevation);
            Point2D point2DLineTo = worldToScreen.deltaTransform(variable, maxElevation);
            PathElement moveTo = new MoveTo(point2DMoveTo.getX(), point2DMoveTo.getY());
            PathElement lineTo = new LineTo(point2DLineTo.getX(), point2DLineTo.getY());
            pathElementList.add(moveTo);
            pathElementList.add(lineTo);
            Text text = new Text(Integer.toString(variable / 1000));
            text.setLayoutX(point2DMoveTo.getX());
            text.setLayoutY(point2DMoveTo.getY());
            text.setTextOrigin(VPos.TOP);
            text.setFont(Font.font("Avenir", 10));
            text.prefWidth(0);
            text.getStyleClass().add("grid_label");
            text.getStyleClass().add("horizontal");
            group.getChildren().add(text);
        }
        path = new Path(pathElementList);
        path.setId("grid");
        System.out.println(pane.getWidth());
        System.out.println(pane.getHeight());
    }

    private void computePolygon() {
        // Le point du polygone à la coordonnée (0,0) est le coin haut gauche.
        // Taille de deux cases par point, un point par pixel javaFx + les deux coins inférieurs.
        double[] coordinate = new double[2 * ((int) rectangle2D.get().getWidth() + 2)];
        // Coordonnées des points des points de l'itinéraire
        for (int i = 0; i < (int) rectangle2D.get().getWidth(); i++) {
            double xOnScreen = insets.getLeft() + i;
            double xOnWorld = screenToWorldTransform.get().transform(xOnScreen, 0).getX();
            Point2D wayPointOnScreen = worldToScreenTransform.get().transform(xOnWorld,
                    elevationProfile.get().elevationAt(xOnWorld));
            coordinate[2 * i] = wayPointOnScreen.getX();
            coordinate[2 * i + 1] = wayPointOnScreen.getY();
        }
        // Coordonnées des deux coins du bas.
        coordinate[ 2 * (int) rectangle2D.get().getWidth()] = rectangle2D.get().getMaxX();
        coordinate[ 2 * (int) rectangle2D.get().getWidth() + 1] = rectangle2D.get().getMaxY();
        coordinate[ 2 * (int) rectangle2D.get().getWidth() + 2] = rectangle2D.get().getMinX();
        coordinate[ 2 * (int) rectangle2D.get().getWidth() + 3] = rectangle2D.get().getMaxY();
    }

    /**
     * Méthode privée nous permettant de calculer l'espacement des lignes verticales,
     * c'est-à-dire l'espacement entre deux indications de distance.
     * @return l'espacement des lignes verticales
     */
    private int computeVerticalLinesSpacing() {
        for (int posStep : POS_STEPS) {
            double minPixel =
                    rectangle2D.get().getWidth() / (elevationProfile.get().length() / posStep);
            // Test si on respecte la condition pour le l'espacement actuelle.
            if (minPixel >= MIN_PIXEL_POS) {
                return posStep;
            }
        }
        // retourne l'espacement maximal si aucun ne satisfait la condition
        return POS_STEPS[POS_STEPS.length - 1];
    }

    /**
     * Méthode privée nous permettant de calculer l'espacement des lignes horizontales,
     * c'est-à-dire l'espacement entre deux indications d'altitude.
     * @return l'espacement des lignes horizontales.
     */
    private int computeHorizontalLinesSpacing() {
        for (int eleStep : ELE_STEPS) {
            double minPixel = rectangle2D.get().getHeight()
                    / ((elevationProfile.get().maxElevation() - elevationProfile.get().minElevation()) / eleStep);
            // Test si on respecte la condition pour le l'espacement actuelle.
            if (minPixel >= MIN_PIXEL_ELE) {
                return eleStep;
            }
        }
        // retourne l'espacement maximal si aucun ne satisfait la condition
        return ELE_STEPS[ELE_STEPS.length - 1];
    }

    /**
     * Méthode privée retournant le nombre de lignes horizontales ayant un texte d'altitude associé.
     * C'est-à-dire incluant la première si l'altitude minimale est un multiple de l'espacement
     * et incluant également la dernière si l'altitude maximale est un multiple de l'espacement.
     * @return le nombre de lignes horizontales ayant un texte d'altitude associé.
     */
    private int numberOfHorizontalLine() {
        double minEle = elevationProfile.get().minElevation();
        double maxEle = elevationProfile.get().maxElevation();
        int step = computeHorizontalLinesSpacing();
        // dénivelée au-dessus de la dernière ligne
        double spaceUp = maxEle % step;
        // dénivelée au-dessus de la ligne en dessous de la première ligne à dessiner
        double spaceDown = minEle % step;
        double newDeltaEle = maxEle - spaceUp  - (minEle - spaceDown);
        // cas limite où il faut dessiner la première ligne (donc une de +).
        if (spaceDown == 0 ) newDeltaEle += step;
        return (int) newDeltaEle / step ;
    }

    /**
     * Méthode privée retournant le nombre de lignes verticales ayant un texte de distance associé.
     * C'est-à-dire incluant la ligne ayant l'indication "0" dans tous les cas
     * et incluant la dernière ligne si la longueur de l'itinéraire est un multiple de l'espacement.
     * @return le nombre de lignes verticales ayant un texte de distance associé.
     */
    private int numberOfVerticalLine() {
        int maxPos = (int) elevationProfile.get().length();
        int step = computeVerticalLinesSpacing() ;
        // + 1, car pour une distance de n il faut n + 1 séparateur.
        return maxPos / step + 1;
    }

}
