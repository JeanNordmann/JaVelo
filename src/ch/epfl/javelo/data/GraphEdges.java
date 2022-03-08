package ch.epfl.javelo.data;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * 3.3.4
 * GraphEdges
 *
 * Enregistrement représentant le tableau de toutes les arêtes du graphe JaVelo.
 *
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 *
 */

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    /**
     *
     * @param edgeId Identité de l'arête donnée.
     * @return Retourne si l'arête d'identité donnée va dans le sens inverse
     * de la voie OSM dont elle provient.
     */

    public static boolean isInverted(int edgeId) {
        return true;
    }

    /**
     *
     * @param edgeId Identité de l'arête donnée.
     * @return Retourne l'identité du noeud destination de l'arête d'identité donnée.
     */

    public static int targetNodeId(int edgeId) {
        return 0;
    }

    /**
     *
     * @param edgeId Identité de l'arête donnée.
     * @return Retourne la longueur en mètres de l'arête d'identité donnée.
     */

    public static double length(int edgeId) {
        return 0;
    }

    /**
     *
     * @param edgeId Identité de l'arête donnée.
     * @return Retourne le dénivelé positif, en mètres, de l'arête d'identité donnée.
     */

    public static double elevationGain(int edgeId) {
        return 0;
    }

    /**
     *
     * @param edgeId Identité de l'arête donnée.
     * @return Retourne si l'arête donnée possède un profil.
     */

    public static boolean hasProfile(int edgeId) {
        return true;
    }

    /**
     *
     * @param edgeId Identité de l'arête donnée.
     * @return Retourne le tableau des échantillons du profil de l'arête d'identité
     * donnée, qui est vide si l'arête ne possède pas de profil.
     */

    public static float[] profileSamples(int edgeId) {
        return null;
    }

    /**
     *
     * @param edgeId Identité de l'arête donnée.
     * @return Retourne l'identité de l'ensemble d'attributs attaché à l'arête
     * d'identité donnée.
     */

    public static int attributesIndex(int edgeId) {
        return 0;
    }
}
