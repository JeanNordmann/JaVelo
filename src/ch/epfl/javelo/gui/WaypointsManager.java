package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import java.util.function.Consumer;

/**
 * 8.3.3
 * WaypointManager
 * <p>
 * Classe gérant l'affichage et l'interaction avec les points de passage.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public final class WaypointsManager {

    /**
     * Constante représentant le rayon de recherche d'un nœud lorsque l'on veut placer un nouveau Waypoint.
     */
    private static final int SEARCH_DISTANCE = 500;

    /**
     * Graphe du réseau routier.
     */
    private final Graph graph;

    /**
     * Propriété JavaFX contenant les paramètres de la carte affichée. 
     */
    private final ObjectProperty<MapViewParameters> mapViewParameters;

    /**
     * Liste (observable) de tous les points de passage. 
     */
    private final ObservableList<Waypoint> waypointList;

    /**
     * Objet permettant de signaler les erreurs, qui est un consommateur de String.
     */
    private final Consumer<String> stringConsumer;

    /**
     * Point2D stockant les coordonnées précédentes du curseur pour les différentes opérations de translation.
     */
    private Point2D previousCoordsOnScreen;

    /**
     * Point2D stockant les coordonnées initiales du curseur, utiles pour les opérations liées à la souris.
     */
    private Point2D initialCoordinatesPoint2D;

    /**
     * Point2D stockant les coordonnées actuelles du curseur, utiles pour les opérations liées à la souris.
     */
    private Point2D actualCoordinatesPoint2D;

    /**
     * Panneau auquel se rattachent tous les groupes.
     */
    private final Pane pane;
    
    private final ErrorManager errorManager;
    /**
     * Constructeur initialisant les attributs à leurs valeurs données, et sinon à leurs valeurs par défaut.
     * @param graph graph du réseau routier.
     * @param mapViewParameters paramètres du fond de carte actuel.
     * @param waypointList liste observable de tous les points de passages.
     * @param stringConsumer consumer nous permettant de signaler les erreurs à afficher sur l'interface graphique.
     */

    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParameters,
                            ObservableList<Waypoint> waypointList, Consumer<String> stringConsumer) {
        this.graph = graph;
        this.mapViewParameters = mapViewParameters;
        errorManager = new ErrorManager(); 
        this.waypointList = waypointList;
        this.stringConsumer = stringConsumer;
        previousCoordsOnScreen = new Point2D(0, 0);
        initialCoordinatesPoint2D= new Point2D(0, 0);
        actualCoordinatesPoint2D = new Point2D(0, 0);
        pane = new Pane();

        waypointList.addListener((ListChangeListener<? super Waypoint>) e -> recreateGroups());

        recreateGroups();
        pane.setPickOnBounds(false);
    }

    /**
     * Méthode publique retournant l'attribut représentant le panneau de type Pane.
     * @return le panneau de type Pane.
     */

    public Pane pane() {
        return pane; }

    /**
     * Méthode modifiant les coordonnées des points de passage en cas de déplacement de ceux-ci.
     */

    public void updateWaypointsLocations() {
        //Itère sur tous les points de passage de l'attribut waypointList.
        for (int i = 0; i < waypointList.size(); i++) {
            //Crée un pointWebMercator correspondant aux coordonnées PointCh du point de passage.
            PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(waypointList.get(i).pointCh());
            //Crée les coordonnées sur l'écran en fonction des coordonnées PointWebMercator.
            double xWayPoint = mapViewParameters.get().viewX(pointWebMercator);
            double yWayPoint = mapViewParameters.get().viewY(pointWebMercator);
            //Modifie les emplacements des points de passage sur le panneau.
            pane.getChildren().get(i).setLayoutX(xWayPoint);
            pane.getChildren().get(i).setLayoutY(yWayPoint);
        }
        //Fait en sorte de ne pas empêcher que les gestionnaires d'évènement de la classe n'empêchent pas à ceux du fond
        //de carte d'être activés.
        pane.setPickOnBounds(false);
    }

    /**
     * Méthode privée recréant entièrement les groupes à chaque fois qu'un point a été ajouté.
     */

    private void recreateGroups() {
        //Enlève tous les groupes liés au panneau.
        pane.getChildren().clear();

        //Ajoute le premier groupe avec sa classe de style "first".
        if (waypointList.size() > 0) addPinToPane("first");

        //Ajoute tous les groupes au milieu avec leur classe de style "middle".
        if (waypointList.size() > 2) {
            for (int i = 1; i < waypointList.size() - 1; i++) {
                addPinToPane("middle");
            }
        }

        //Ajoute le dernier groupe avec sa classe de style "last".
        if (waypointList.size() > 1) addPinToPane("last");

        //Met à jour les coordonnées des points de passage, qui vont être redessinées grâce aux listeners liés aux
        //points de passage.
        updateWaypointsLocations();
    }

    /**
     * Méthode privée ajoutant un marqueur au panneau, en fonction de son emplacement dans la liste donnée en paramètre.
     * @param emplacement chaîne de caractères donnée représentant la position dans la liste, (peut être first, middle
     *                    ou last).
     */

    private void addPinToPane(final String emplacement) {
        //Crée le groupe à ajouter.
        Group groupToAdd = new Group(getAndSetOutsideBorder(), getAndSetInsideBorder());
        //Ajoute à sa classe de style le "pin" commun à tous.
        groupToAdd.getStyleClass().add("pin");
        //Ajoute à sa classe de style la chaîne de caractères liée à sa position dans la liste de points de passage.
        groupToAdd.getStyleClass().add(emplacement);
        //Ajoute tous les listeners au groupe concerné.
        setUpListeners(groupToAdd);
        //Ajoute enfin le groupe au panneau.
        pane.getChildren().add(groupToAdd);
    }

    /**
     * Méthode permettant d'ajouter une point de passage sur la carte à l'aide de ses coordonnées relatives
     * sur la carte affichée à l'écran.
     * @param x coordonnée X partante depuis le coin haut gauche
     * @param y coordonnée Y partante depuis le coin haut gauche
     */

    public void addWaypoint(final double x, final double y) {
        PointCh pointCh = mapViewParameters.get().pointAt(x, y).toPointCh();
        if (pointCh == null) {
            stringConsumer.accept("Point hors de la Suisse !");
            errorManager.displayError(stringConsumer.toString());
        } else {
            int idNodeClosestTo = graph.nodeClosestTo(pointCh, SEARCH_DISTANCE);
            if (idNodeClosestTo == -1) {
                //Pas de nœud trouvé dans la distance de recherche.
                stringConsumer.accept("Aucune route à proximité !");
                errorManager.displayError(stringConsumer.toString());
            } else {
                //Ajoute le point de passage trouvé à la liste de points de passage de la classe.
                Waypoint waypointToAdd = new Waypoint(pointCh, idNodeClosestTo);
                waypointList.add(waypointToAdd);
            }
        }
    }

    /**
     * Méthode permettant de déplacer un marqueur lorsqu'un évènement de souris a été détecté.
     * @param event évènement de souris détecté.
     * @param pin marqueur à déplacer.
     */

    public void moveWaypoint(MouseEvent event, Node pin) {
        Point2D translation = previousCoordsOnScreen.subtract(event.getX(), event.getY());
        pin.setLayoutX(pin.getLayoutX() - translation.getX());
        pin.setLayoutY(pin.getLayoutY() - translation.getY());
        actualCoordinatesPoint2D = new Point2D(pin.getLayoutX() - translation.getX(),
                pin.getLayoutY() - translation.getY());
    }

    /**
     * Méthode privée créant le SVGPath correspondant au contour du marqueur, ajoutant sa classe de style et le
     * retournant.
     * @return le SVGPath correspondant au contour du marqueur, ayant sa classe de style ajoutée.
     */

    private SVGPath getAndSetOutsideBorder() {
        SVGPath outsideBorder = new SVGPath();
        //Configure le contenu du SVGPath, correspondant à la bordure extérieure.
        outsideBorder.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
        //Ajoute sa classe de style.
        outsideBorder.getStyleClass().add("pin_outside");
        return outsideBorder;
    }

    /**
     * Méthode privée créant le SVGPath correspondant à l'intérieur du marqueur, ajoutant sa classe de style et le
     * retournant.
     * @return le SVGPath correspondant à l'intérieur du marqueur, ayant sa classe de style ajoutée.
     */

    private SVGPath getAndSetInsideBorder() {
        SVGPath insideBorder = new SVGPath();
        //Configure le contenu du SVGPath, correspondant à la bordure intérieure.
        insideBorder.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
        //Ajoute sa classe de style.
        insideBorder.getStyleClass().add("pin_inside");
        return insideBorder;
    }

    /**
     * Méthode privée configurant les gestionnaires d'évènement pour un groupe donné en paramètre.
     * @param pin groupe donné en paramètre.
     */

    private void setUpListeners(Node pin) {
        pin.setOnMouseClicked(event -> {
            //Vérifie si le curseur ne bouge plus depuis le clic sur un marqueur.
            if (event.isStillSincePress())
                //Si oui, supprimer le point de passage de la liste.
                waypointList.remove(pane.getChildren().indexOf(pin));
        });

        pin.setOnMousePressed(event -> {
            //Vérifie si le curseur bouge depuis le clic sur le marqueur.
            if (!event.isStillSincePress()) {
                //Si oui, actualise les coordonnées précédentes, initiales et actuelles des attributs de la classe,
                //utiles pour les différentes opérations de déplacement.
                previousCoordsOnScreen = new Point2D(event.getX(), event.getY());
                initialCoordinatesPoint2D = pin.localToParent(event.getX(), event.getY());
                actualCoordinatesPoint2D = pin.localToParent(event.getX(), event.getY());
            }
        });

        pin.setOnMouseDragged(event -> {
            //Lorsqu'un évènement de glissement de la souris est détecté sur le marqueur, on le déplace.
            moveWaypoint(event, pin);
            });

        pin.setOnMouseReleased(event -> {
            //Lorsqu'un évènement de relâchement de la souris est détecté sur le marqueur, on le repose.
            if (!initialCoordinatesPoint2D.equals(actualCoordinatesPoint2D)) {
                //La méthode décide de replacer le point de passage soit à l'endroit du relâchement, soit à l'endroit
                //initial, car aucune route n'a été détectée à proximité.
                relocateWaypoint(new Point2D(actualCoordinatesPoint2D.getX(), actualCoordinatesPoint2D.getY()), pin);
                //On remet les attributs Point2D à leurs valeurs d'origine.
                previousCoordsOnScreen = new Point2D(0, 0);
                initialCoordinatesPoint2D =  new Point2D(0, 0);
                actualCoordinatesPoint2D =  new Point2D(0, 0);
            }
        });
    }

    /**
     * Méthode privée relocalisant un point de passage si possible, et sinon affiche qu'aucune route n'est
     * à proximité.
     * @param actualPosition coordonnées actuelles en Point2D.
     * @param pin groupe associé au marqueur donné en paramètre.
     */

    private void relocateWaypoint(final Point2D actualPosition, Node pin) {
        PointCh pointCh = mapViewParameters.get().pointAt(actualPosition.getX(), actualPosition.getY()).toPointCh();
        if (pointCh == null) {
            stringConsumer.accept("impossible de relocaliser un point de passage en dehors de la " +
                    "suisse");
            //La liste de points de passage n'est pas modifiée, mais doit malgré tout être redessinée.
            updateWaypointsLocations();
        } else {

            int idNodeClosestTo = graph.nodeClosestTo(pointCh, SEARCH_DISTANCE);
            if (idNodeClosestTo == -1) {
                stringConsumer.accept("Aucune route à proximité !");
                //La liste de points de passage n'est pas modifiée, mais doit malgré tout être redessinée.
                updateWaypointsLocations();
            } else {
                int position = pane.getChildren().indexOf(pin);
                waypointList.set(position, new Waypoint(pointCh, idNodeClosestTo));
            }
        }
    }
 }
