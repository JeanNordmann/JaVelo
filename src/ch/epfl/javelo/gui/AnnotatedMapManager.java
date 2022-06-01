package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

/**
 * 11.3.2
 * AnnotatedMapManager
 * <p>
 * Classe gérant l'affichage de la carte "annotée", soit le fond de carte au-dessus duquel sont
 * superposés l'itinéraire et les points de passage.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public final class AnnotatedMapManager {

    private static final int HIGHLIGHTED_POSITION_MAX_PIXEL_DISTANCE = 15;
    private static final int INITIAL_ZOOM_LEVEL = 12;
    private static final int INITIAL_TOP_LEFT_X = 543200;
    private static final int INITIAL_TOP_LEFT_Y = 370650;

    /**
     * Attribut représentant le panneau de la carte annotée.
     */
    private final StackPane pane;

    /**
     * Attribut représentant une propriété sur les paramètres de vue de la carte.
     */
    private final ObjectProperty<MapViewParameters> mapViewParametersP;

    /**
     * Attribut représentant une propriété sur la position de la souris sur la route.
     */
    private final DoubleProperty mousePositionOnRouteProperty;

    /**
     * Attribut représentant une propriété sur la position de la souris.
     */
    private final ObjectProperty<Point2D> mousePosition;

    /**
     * Constructeur public prenant en arguments un graphe, un gestionnaire de tuiles, le bean de
     * l'itinéraire et un consommateur d'erreurs, et initialisant ses attributs à leurs valeurs
     * par défaut.
     * @param graph Graphe donné.
     * @param tileManager Gestionnaire de tuiles OpenStreetMap donné.
     * @param bean Bean donné de l'itinéraire.
     * @param consumer Consommateur d'erreurs donné.
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean bean,
                               Consumer<String> consumer) {

        mapViewParametersP =
                new SimpleObjectProperty<>(new MapViewParameters(INITIAL_ZOOM_LEVEL,
                        INITIAL_TOP_LEFT_X, INITIAL_TOP_LEFT_Y));

        WaypointsManager waypointsManager =
                new WaypointsManager(graph, mapViewParametersP, bean.waypointsProperty(), consumer);

        BaseMapManager baseMapManager =
                new BaseMapManager(tileManager, waypointsManager, mapViewParametersP);

        RouteManager routeManager = new RouteManager(bean, mapViewParametersP);

        //Construction du panneau avec les trois sous-panneaux.
        pane = new StackPane(baseMapManager.pane(), waypointsManager.pane(), routeManager.pane());
        //Ajout de la feuille de style.
        pane.getStylesheets().add("map.css");

        //Valeurs par défaut de la propriété de la position de la souris, ainsi que de la position
        //elle-même.
        mousePositionOnRouteProperty = new SimpleDoubleProperty(Double.NaN);
        mousePosition = new SimpleObjectProperty<>(new Point2D(Double.NaN, Double.NaN));

        //Configure les gestionnaires d'évènements liés aux mouvements de la souris.
        setUpHandlers();

        mousePositionOnRouteProperty.bind(Bindings.createDoubleBinding(() -> {
            //Assigne la valeur à NaN si la route ou la position de la souris est nulle.
            if(bean.getRoute() == null || mousePosition.get() == null) return Double.NaN;

            PointCh mousePointCh = mapViewParametersP.get().
                    pointAt(mousePosition.get().getX(), mousePosition.get().getY())
                    .toPointCh();
            //Vérifie que le point actuel du curseur de la souris est bien en Suisse.
            if (mousePointCh == null) return Double.NaN;

            //Calcule le point le plus proche du curseur sur la route.
            PointCh closestMousePointCh = bean.getRoute().pointClosestTo(mousePointCh).point();
            //Convertit ce point en coordonnées PointWebMercator.
            PointWebMercator closestMousePWM = PointWebMercator.ofPointCh(closestMousePointCh);
            Point2D closestMousePoint2D = new Point2D(mapViewParametersP.get().viewX(closestMousePWM),
                    mapViewParametersP.get().viewY(closestMousePWM));
            //Retourne la position le long de l'itinéraire ou NaN si elle est à plus de quinze
            //pixels JavaFX.
            return mousePosition.get().distance(closestMousePoint2D) <= HIGHLIGHTED_POSITION_MAX_PIXEL_DISTANCE
                ? bean.getRoute().pointClosestTo(closestMousePointCh).position()
                    : Double.NaN;
        }, mousePosition, bean.routeProperty()));

    }

    /**
     * Méthode publique retournant le panneau contenant la carte annotée.
     * @return le panneau contenant la carte annotée.
     */
    public StackPane pane() {
        return pane;
    }

    /**
     * Méthode publique retournant la propriété contenant la position du pointeur de la souris le
     * long de l'itinéraire.
     * @return Retourne la propriété contenant la position du pointeur de la souris le long de
     * l'itinéraire.
     */
    public DoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteProperty;
    }

    /**
     * Méthode privée configurant les gestionnaires d'évènements liés aux mouvements de la
     * souris, et de sa sortie du panneau.
     */
    private void setUpHandlers() {
        //Gestionnaire d'évènement correspondant à la sortie de la souris du panneau.
        pane.setOnMouseExited(e -> mousePosition.set(null));

        //Gestionnaire d'évènement correspondant au mouvement de la souris sur le panneau.
        pane.setOnMouseMoved(e -> mousePosition.set(new Point2D(e.getX(), e.getY())));

    }
}
