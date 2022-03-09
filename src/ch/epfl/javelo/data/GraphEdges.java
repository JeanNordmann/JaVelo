package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

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


    private static final int OFFSET_WAY_AND_ID = 0;
    private static final int OFFSET_EDGE_LENGTH = OFFSET_WAY_AND_ID + Integer.BYTES;
    private static final int OFFSET_ASCENDING_ELEVATION = OFFSET_EDGE_LENGTH + Short.BYTES;
    private static final int OFFSET_ID_OSM_ATTRIBUTE = OFFSET_ASCENDING_ELEVATION + Short.BYTES;
    private static final int EDGE_INTS = OFFSET_ID_OSM_ATTRIBUTE + Short.BYTES;

    /**
     *
     * @param edgeId Identité de l'arête donnée.
     * @return Retourne si l'arête d'identité donnée va dans le sens inverse
     * de la voie OSM dont elle provient.
     */

    public boolean isInverted(int edgeId) {
        byte isInvertedBit = (byte) Bits.extractUnsigned(
                edgesBuffer.getInt(edgeId * EDGE_INTS + OFFSET_WAY_AND_ID),
                31,1);
        return 1 == isInvertedBit ;
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
