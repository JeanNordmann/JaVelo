package ch.epfl.javelo.gui;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.SwissBounds;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    /**
     * Attribut représentant le panneau de la carte annotée.
     */
    private final StackPane pane;

    /**
     * Attribut représentant un gestionnaire de fond de carte.
     */
    private BaseMapManager baseMapManager;

    /**
     * Attribut représentant un gestionnaire de points de passage.
     */
    private WaypointsManager waypointsManager;

    /**
     * Attribut représentant un gestionnaire d'itinéraire.
     */
    private RouteManager routeManager;

    /**
     * Attribut représentant une propriété sur les paramètres de vue de la carte.
     */
    private final ObjectProperty<MapViewParameters> mapViewParametersP;

    /**
     * Attribut représentant une propriété sur la position de la souris sur la route.
     */
    private DoubleProperty mousePositionOnRouteProperty;

    /**
     * Attribut représentant une propriété sur la position de la souris.
     */
    private ObjectProperty<Point2D> mousePosition;

    //private static final ObjectProperty<Point2D> NAN_POINT_2D = new Point2D(D);

    /**
     * Constructeur public prenant en arguments un graphe, un gestionnaire de tuiles, le bean de
     * l'itinéraire, et un consommateur d'erreurs.
     * @param graph Graphe donné.
     * @param tileManager Gestionnaire de tuiles OpenStreetMap donné.
     * @param bean Bean donné de l'itinéraire.
     * @param consumer Consommateur d'erreurs donné.
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean bean,
                               Consumer<String> consumer) {
        mapViewParametersP =
                new SimpleObjectProperty<>(new MapViewParameters(12, 543200, 370650));
        waypointsManager = new WaypointsManager(graph, mapViewParametersP, bean.waypointsProperty(), consumer);
        baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersP);
        routeManager = new RouteManager(bean, mapViewParametersP);
        pane = new StackPane(baseMapManager.pane(), waypointsManager.pane(), routeManager.pane());
        pane.getStylesheets().add("map.css");
        mousePositionOnRouteProperty = new SimpleDoubleProperty(Double.NaN);
        mousePosition = new SimpleObjectProperty<>(new Point2D(Double.NaN, Double.NaN));

        //TODO
        pane.setOnMouseExited(e -> mousePosition.set(null));
        pane.setOnMouseMoved(e -> mousePosition.set(new Point2D(e.getX(), e.getY())));

        mousePositionOnRouteProperty.bind(Bindings.createDoubleBinding(() -> {
            if(bean.getRoute() == null) return Double.NaN ;
            if (mousePosition.get() == null) return Double.NaN;
            //todo A CLEAN
            PointCh mousePointCh = mapViewParametersP.get().
                    pointAt(mousePosition.get().getX(), mousePosition.get().getY())
                    .toPointCh();
            if (mousePointCh == null) return Double.NaN;
            PointCh closestMousePointCh = bean.getRoute().pointClosestTo(mousePointCh).point();
            if (closestMousePointCh == null) return Double.NaN;
            PointWebMercator closestMousePWM = PointWebMercator.ofPointCh(closestMousePointCh);
            Point2D closestMousePoint2D = new Point2D(mapViewParametersP.get().viewX(closestMousePWM),
                    mapViewParametersP.get().viewY(closestMousePWM));
            return mousePosition.get().distance(closestMousePoint2D) <= 15
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

}
