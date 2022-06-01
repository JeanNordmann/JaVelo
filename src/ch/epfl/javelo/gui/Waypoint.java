package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
/**
 * 7.3.4
 * Waypoint
 * <p>
 * Enregistrement nous permettant de représenter un point de passage. Ses deux attributs sont :
 *
 * La position du point de passage dans le système de coordonnées suisse (PointCh).
 * L'identité du nœud JaVelo le plus proche de ce point de passage.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 *
 *
 * @param pointCh Position du point de passage dans le système de coordonnées suisse (PointCh).
 * @param nodeId Identité du nœud donné.
 */

public record Waypoint (PointCh pointCh, int nodeId) {}
