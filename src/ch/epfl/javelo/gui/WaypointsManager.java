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

    //Constante représentant le rayon de recherche d'un nœud lorsque l'on veut placer un nouveau
    //point de passage.
    private static final int SEARCH_DISTANCE = 500;

    //TODO relire + avi sur pertinence de cette constante.
    //Constante représentant un vecteur de translation nulle, vecteur origine, pour éviter de
    // recréer des instance de point centrée à l'origine à chaque remise à zero.
    private static final Point2D ORIGINE_POINT2D = new Point2D(0, 0);

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
     * Objet permettant de signaler les erreurs, qui est un consommateur de chaîne de caractères.
     */
    private final Consumer<String> stringConsumer;

    /**
     * //TODO relire
     * Point2D stockant les coordonnées du curseur sur le point de passage, nous permet de garder
     * la même position relative au point de passage quand on déplace un point de passage.
     */
    private Point2D mouseCoordinatesOnTheWaypoint;

    /**
     * //TODO relire
     * Point2D stockant les coordonnées initiales du point de passage relative au fond de carte
     * au début du déplacement.
     */
    private Point2D initialCoordinatesPoint2D;

    /**
     * //TODO relire
     * Point2D stockant les coordonnées actuelles du point de passage relative au fond de carte,
     * utile au moment de relocaliser le point de
     */
    private Point2D actualCoordinatesPoint2D;

    /**
     * Panneau auquel se rattachent tous les groupes.
     */
    private final Pane pane;

    /**
     * Constructeur initialisant les attributs à leurs valeurs données, et sinon à leurs valeurs
     * par défaut.
     * @param graph Graphe du réseau routier.
     * @param mapViewParameters Paramètres du fond de carte actuels.
     * @param waypointList Liste observable de tous les points de passages.
     * @param stringConsumer Consommateur nous permettant de signaler les erreurs à afficher sur
     *                       l'interface graphique.
     */

    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParameters,
                            ObservableList<Waypoint> waypointList, Consumer<String> stringConsumer) {
        this.graph = graph;
        this.mapViewParameters = mapViewParameters;
        this.waypointList = waypointList;
        this.stringConsumer = stringConsumer;
        mouseCoordinatesOnTheWaypoint = ORIGINE_POINT2D;
        initialCoordinatesPoint2D = ORIGINE_POINT2D;
        actualCoordinatesPoint2D = ORIGINE_POINT2D;
        pane = new Pane();

        //Ajoute un auditeur sur la liste de points de passage, pour actualiser les couleurs des
        //marqueurs lorsque la liste de points de passage change.
        waypointList.addListener((ListChangeListener<? super Waypoint>) e -> recreateGroups());

        //Ajoute un auditeur sur les paramètres de vue de la carte, pour permettre aux points de
        //passage de bouger avec la carte, lorsque cette dernière se déplace ou bien redimensionnée.
        mapViewParameters.addListener((p, oldS, newS) -> updateWaypointsLocations());

        //Permet au panneau de ne pas bloquer les interactions avec les panneaux en arrière-plan.
        pane.setPickOnBounds(false);
    }

    /**
     * Méthode publique retournant l'attribut représentant le panneau de type Pane.
     * @return le panneau de type Pane.
     */

    public Pane pane() {
        return pane; }

    /**
     * Méthode permettant d'ajouter un point de passage sur la carte à l'aide de ses coordonnées
     * relatives sur la carte affichée à l'écran.
     * @param x Coordonnée X depuis le coin haut gauche.
     * @param y Coordonnée Y depuis le coin haut gauche.
     */

    public void addWaypoint(double x, double y) {
        PointCh pointCh = mapViewParameters.get().pointAt(x, y).toPointCh();
        //Si le PointCh correspondant à la position où l'on veut ajouter un point de passage est
        //nul, ce qui correspond à un point hors des limites suisses définies par la classe
        //SwissBounds.
        if (pointCh == null) {
            //Dans le cas où le point n'est pas dans les limites de la Suisse.
            stringConsumer.accept("Aucune route à proximité !");
        } else {
            //Sinon, une recherche du nœud le plus proche est entamée.
            int idNodeClosestTo = graph.nodeClosestTo(pointCh, SEARCH_DISTANCE);
            if (idNodeClosestTo == -1) {
                //Dans le cas où aucun nœud n'a été trouvé dans la distance de recherche.
                stringConsumer.accept("Aucune route à proximité !");
            } else {
                //Ajoute le point de passage trouvé à la liste de points de passage de la classe.
                Waypoint waypointToAdd = new Waypoint(pointCh, idNodeClosestTo);
                waypointList.add(waypointToAdd);
            }
        }
    }

    /**
     * Méthode modifiant les coordonnées des points de passage en cas de déplacement de ceux-ci.
     */

    private void updateWaypointsLocations() {
        //Itère sur tous les points de passage de l'attribut waypointList.
        for (int i = 0; i < waypointList.size(); i++) {
            //Crée un pointWebMercator correspondant aux coordonnées PointCh du point de passage.
            PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(waypointList
                    .get(i).pointCh());
            //Crée les coordonnées sur l'écran en fonction des coordonnées PointWebMercator.
            double xWayPoint = mapViewParameters.get().viewX(pointWebMercator);
            double yWayPoint = mapViewParameters.get().viewY(pointWebMercator);
            //Modifie les emplacements des points de passage sur le panneau.
            pane.getChildren().get(i).setLayoutX(xWayPoint);
            pane.getChildren().get(i).setLayoutY(yWayPoint);
        }
    }

    /**
     * Méthode privée recréant entièrement les groupes à chaque fois qu'un point a été ajouté ou
     * supprimé.
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

        //Met à jour les coordonnées des points de passage, qui vont être redessinées grâce aux
        //auditeurs liés aux points de passage.
        updateWaypointsLocations();
    }

    /**
     * Méthode privée ajoutant un marqueur au panneau, en fonction de son emplacement dans la
     * liste donnée en paramètre.
     * @param emplacement Chaîne de caractères donnée représentant la position dans la liste, (peut
     * être first, middle ou last).
     */

    private void addPinToPane(String emplacement) {
        //Crée le groupe à ajouter.
        Group groupToAdd = new Group(getAndSetOutsideBorder(), getAndSetInsideBorder());
        //Ajoute à sa classe de style le "pin" commun à tous.
        groupToAdd.getStyleClass().add("pin");

        //Ajoute à sa classe de style la chaîne de caractères liée à sa position dans la liste de
        //points de passage.
        groupToAdd.getStyleClass().add(emplacement);

        //Ajoute tous les auditeurs au groupe concerné.
        setUpListeners(groupToAdd);

        //Ajoute enfin le groupe au panneau.
        pane.getChildren().add(groupToAdd);
    }


    /**
     * Méthode permettant de déplacer un marqueur lorsqu'un évènement de souris a été détecté.
     * @param event Évènement de souris détecté.
     * @param pin Marqueur à déplacer.
     */

    private void moveWaypoint(MouseEvent event, Node pin) {
        Point2D translation = mouseCoordinatesOnTheWaypoint.subtract(event.getX(), event.getY());
        pin.setLayoutX(pin.getLayoutX() - translation.getX());
        pin.setLayoutY(pin.getLayoutY() - translation.getY());
        actualCoordinatesPoint2D = new Point2D(pin.getLayoutX() - translation.getX(),
                pin.getLayoutY() - translation.getY());
    }

    /**
     * Méthode privée créant le SVGPath correspondant au contour du marqueur, ajoutant sa classe
     * de style et le retournant.
     * @return Le SVGPath correspondant au contour du marqueur, ayant sa classe de style ajoutée.
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
     * Méthode privée créant le SVGPath correspondant à l'intérieur du marqueur, ajoutant sa classe
     * de style et le retournant.
     * @return Le SVGPath correspondant à l'intérieur du marqueur, ayant sa classe de style ajoutée.
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
//TODO dis moi si tu penses que je devrais essayer de clean un peu, je trouve que 3 attributs
// c'est bcp
        pin.setOnMousePressed(event -> {
            //Vérifie si le curseur bouge depuis le clic sur le marqueur.
            if (!event.isStillSincePress()) {
                //Si oui, actualise les coordonnées précédentes, initiales et actuelles des
                //attributs de la classe, utiles pour les différentes opérations de déplacement.
                mouseCoordinatesOnTheWaypoint = new Point2D(event.getX(), event.getY());
                initialCoordinatesPoint2D = pin.localToParent(event.getX(), event.getY());
                actualCoordinatesPoint2D = pin.localToParent(event.getX(), event.getY());
            }
        });

        //Si un glissement de la souris est détecté sur un marqueur, on le déplace en suivant la
        //souris.
        pin.setOnMouseDragged(event ->  moveWaypoint(event, pin));

        pin.setOnMouseReleased(event -> {
            //Lorsqu'un évènement de relâchement de la souris est détecté sur le marqueur, on
            //le repose.
            if (!initialCoordinatesPoint2D.equals(actualCoordinatesPoint2D)) {
                //La méthode décide de replacer le point de passage soit à l'endroit du relâchement,
                //soit à l'endroit initial, car aucune route n'a été détectée à proximité.
                relocateWaypoint(new Point2D(actualCoordinatesPoint2D.getX(),
                        actualCoordinatesPoint2D.getY()), pin);

                //On remet les attributs Point2D à leurs valeurs d'origine.
                mouseCoordinatesOnTheWaypoint = ORIGINE_POINT2D;
                initialCoordinatesPoint2D = ORIGINE_POINT2D;
                actualCoordinatesPoint2D = ORIGINE_POINT2D;
            }
        });
    }

    /**
     * Méthode privée relocalisant un point de passage si possible, et sinon affiche qu'aucune
     * route n'est à proximité.
     * @param actualPosition Coordonnées actuelles en Point2D.
     * @param pin Groupe associé au marqueur donné en paramètre.
     */
    private void relocateWaypoint(final Point2D actualPosition, Node pin) {
        PointCh pointCh = mapViewParameters.get().pointAt(actualPosition.getX(),
                actualPosition.getY()).toPointCh();
        if (pointCh == null) {
            stringConsumer.accept("Aucune route à proximité !");
            //La liste de points de passage n'est pas modifiée, mais doit malgré tout
            //être redessinée afin que le point de passage retourne à son point initial.
            updateWaypointsLocations();
        } else {

            int idNodeClosestTo = graph.nodeClosestTo(pointCh, SEARCH_DISTANCE);
            if (idNodeClosestTo == -1) {
                stringConsumer.accept("Aucune route à proximité !");
                //La liste de points de passage n'est pas modifiée, mais doit malgré tout
                //être redessinée afin que le point de passage retourne à son point initial.
                updateWaypointsLocations();
            } else {
                int position = pane.getChildren().indexOf(pin);
                waypointList.set(position, new Waypoint(pointCh, idNodeClosestTo));
            }
        }
    }
 }
