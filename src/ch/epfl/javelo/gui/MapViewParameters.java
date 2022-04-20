package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

//TODO
/**
 * 7.3.5
 * GpxGenerator
 * <p>
 * Class permettant d'écrire dans un fichier un itinéraire au format GPX à partir d'une Route, et de son profil, qu'on
 * obtient facilement via : ElevationProfileComputer.elevationProfile(route, ...)
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public record MapViewParameters (int zoomLevel, double x, double y){
    //Coordonnées x et y du coin en haut à gauche de la tuile de carte.

    /**
     * Constructeur compact lançant une exception si les arguments donnés à la
     * construction ne sont pas valides.
     * @param zoomLevel Niveau de zoom plus grand ou égale à 0.
     * @param x Coordonnée X donnée.
     * @param y Coordonnée Y donnée.
     */

    public MapViewParameters {
        // les coordonnées sont casts en int car isValid est initialement conçue pour check les tuiles.
        Preconditions.checkArgument((x >= 0) && (y >= 0) && (zoomLevel >= 0));
    }

    /**
     * Méthode retournant les coordonnées du coin haut-gauche sous la forme d'un objet de type Point2D.
     * @return Un Point2D contenant les coordonnées du point en haut à gauche.
     */
    public Point2D topLeft() {
        return new Point2D(x, y);
    }

    /**
     * Méthode retournant une instance de MapViewParameters identique au récepteur, si ce n'est que
     * les coordonnées du coin haut-gauche sont celles passées en arguments à la méthode.
     *
     * @param newX nouvelle coordonnée X.
     * @param newY nouvelle coordonnée Y.
     * @return Un MapViewParameters contenant les nouvelles coordonnées au même zoomLevel.
     */
    public MapViewParameters withMinXY(double newX, double newY) {
        return new MapViewParameters(zoomLevel, newX, newY);
    }

    /**
     * Méthode Prenant en arguments les coordonnées x et y d'un point, exprimées par rapport
     * au coin haut-gauche de la portion de carte affichée à l'écran, puis retourne ce point
     * sous la forme d'une instance de PointWebMercator dans les coordonnées globales.
     * → conversion : Coordonnées relatives → PointWebMercator.
     *
     * @param relativeX Coordonnée X relative.
     * @param relativeY Coordonnée Y relative.
     * @return Un PointWebMercator aux coordonnées voulues dans les coordonnées globales au format PointWebMercator.
     */
    public PointWebMercator pointAt(double relativeX, double relativeY) {
        return PointWebMercator.of(zoomLevel, x +  relativeX, y +  relativeY );
    }

    /**
     * Méthodes prenant en argument un point Web Mercator et retournent la position x correspondante,
     * exprimée par rapport au coin haut-gauche de la portion de carte affichée à l'écran.
     * → conversion : PointWebMercator → Coordonnées relatives X.
     * @param pointWebMercator PointWebMercator dont on aimerait connaitre la coordonnée x relative.
     * @return La position x correspondante exprimée par rapport au coin haut-gauche de la carte affichée.
     */
    public double viewX(PointWebMercator pointWebMercator) {
        return  pointWebMercator.xAtZoomLevel(zoomLevel) - this.x;
    }

    /**
     * Méthodes prenant en argument un point Web Mercator et retournent la position y correspondante,
     * exprimée par rapport au coin haut-gauche de la portion de carte affichée à l'écran.
     * → conversion : PointWebMercator → Coordonnées relatives Y.
     *
     * @param pointWebMercator PointWebMercator dont on aimerait connaitre la coordonnée y relative.
     * @return La position y correspondante exprimée par rapport au coin haut-gauche de la carte affichée.
     */
    public double viewY(PointWebMercator pointWebMercator) {
        return  pointWebMercator.yAtZoomLevel(zoomLevel) - this.y;
    }

}