package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
/**
 * 7.3.4
 * Waypoint
 * <p>
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */
//TODO
/**
 * Record nous permettant de représenter un point de passage. Ses deux attributs sont :
 *
 * La position du point de passage dans le système de coordonnées suisse (PointCh).
 * L'identité du nœud JaVelo le plus proche de ce point de passage.
 */
public record Waypoint (PointCh pointCh, int nodeId) {
}