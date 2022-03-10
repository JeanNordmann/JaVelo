package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static java.lang.Math.scalb;

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
     * Diverses constantes représentant le décalage lié aux accès des données
     * du Buffer.
     */

    private static final int OFFSET_WAY_AND_ID = 0;
    private static final int OFFSET_EDGE_LENGTH = OFFSET_WAY_AND_ID + Integer.BYTES;
    private static final int OFFSET_ASCENDING_ELEVATION = OFFSET_EDGE_LENGTH + Short.BYTES;
    private static final int OFFSET_ID_OSM_ATTRIBUTE = OFFSET_ASCENDING_ELEVATION + Short.BYTES;
    private static final int EDGE_INTS = OFFSET_ID_OSM_ATTRIBUTE + Short.BYTES;

    /**
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
     * @param edgeId Identité de l'arête donnée.
     * @return Retourne l'identité du noeud destination de l'arête d'identité donnée.
     */

    public int targetNodeId(int edgeId) {
        return isInverted(edgeId) ? ~(
                edgesBuffer.getInt(edgeId * EDGE_INTS + OFFSET_WAY_AND_ID)) :
                edgesBuffer.getInt(edgeId * EDGE_INTS + OFFSET_WAY_AND_ID);
    }

    /**
     *
     * @param edgeId Identité de l'arête donnée.
     * @return Retourne la longueur en mètres de l'arête d'identité donnée.
     */

    public double length(int edgeId) {
        return Q28_4.asDouble(Bits.extractUnsigned(edgesBuffer.getShort
                (edgeId * EDGE_INTS + OFFSET_EDGE_LENGTH), 0, 16));
    }

    /**
     *
     * @param edgeId Identité de l'arête donnée.
     * @return Retourne le dénivelé positif, en mètres, de l'arête d'identité donnée.
     */

    public double elevationGain(int edgeId)  {
        return Q28_4.asDouble(Bits.extractUnsigned(edgesBuffer.getShort
                (edgeId * EDGE_INTS + OFFSET_ASCENDING_ELEVATION), 0, 16));
    }

    /**
     *
     * @param edgeId Identité de l'arête donnée.
     * @return Retourne si l'arête donnée possède un profil.
     */

    public boolean hasProfile(int edgeId) {
        int profilByte = Bits.extractUnsigned(profileIds.get(edgeId), 30,2);
        return profilByte !=0;
    }

    /**
     * @param edgeId Identité de l'arête donnée.
     * @return Retourne le tableau des échantillons du profil de l'arête d'identité
     * donnée, qui est vide si l'arête ne possède pas de profil.
     */

    public float[] profileSamples(int edgeId) {
        if (!hasProfile(edgeId)) return new float[]{};
        int numberSamples = 1 + Math2.ceilDiv((int)(scalb(length(edgeId),4)) , Q28_4.ofInt(2));
        float[] toReturn = new float[numberSamples];
        int firstAltiId = Bits.extractUnsigned(profileIds.get(edgeId), 0, 30);
        byte profilType = (byte) Bits.extractUnsigned(profileIds.get(edgeId), 30,2);
        switch (profilType) {
            case (byte) 1 :
                for (int i = 0; i < numberSamples; i++) {
                    toReturn[i] = asFloat16(elevations.get(firstAltiId + i));
                } break;

            case (byte) 2 :
                int shortsToRead2 = numberSamples / 2;
                toReturn[0] = asFloat16(elevations.get(firstAltiId));
                for (int i = 1; i <= shortsToRead2 ; i++) {

                    int extractShort =  Short.toUnsignedInt(elevations.get(firstAltiId + i));
                    float firstShift = asFloat16(Bits.extractSigned(extractShort, 8, 8));
                    toReturn[2 * i - 1] = toReturn[2 * (i-1)] + firstShift;
                    // pour éviter OutOfBoundException si les 8 derniers bits du shorts sont
                    if (numberSamples - 1 < 2 * i) break;
                    float secondShift = asFloat16(Bits.extractSigned(extractShort, 0, 8));
                    toReturn[2 * i] = toReturn[2 * i - 1] + secondShift;
                } break;

            case (byte) 3 :
                //shortsToRead4 = nbr de shorts à lire après le premier short, sachant qu'un short contient 4 nibble.
                int shortsToRead4 = (numberSamples + 2) / 4;
                toReturn[0] = asFloat16(elevations.get(firstAltiId));
                for (int i = 1; i <= shortsToRead4 ; i++) {
                    short extractShort = elevations.get(firstAltiId + i);

                    float firstShift = asFloat8(Bits.extractSigned(Short.toUnsignedInt(extractShort), 12, 4));
                    toReturn[4 * i - 3] = toReturn[4 * (i-1) ] + firstShift;

                    // pour éviter OutOfBoundException si les 8 derniers bits du short sont inutiles
                    if (numberSamples + 1 < 4 * i) break;

                    float secondShift = asFloat8(Bits.extractSigned(Short.toUnsignedInt(extractShort), 8, 4));
                    toReturn[4 * i - 2] = toReturn[4 * i - 3] + secondShift;

                    // pour éviter OutOfBoundException si les 8 derniers bits du short sont inutiles
                    if (numberSamples < 4 * i) break;

                    float thirdShift = asFloat8(Bits.extractSigned(Short.toUnsignedInt(extractShort), 4, 4));
                    toReturn[4 * i - 1] = toReturn[4 * i - 2] + thirdShift;

                    // pour éviter OutOfBoundException si les 8 derniers bits du short sont inutiles
                    if (numberSamples - 1 < 4 * i) break;

                    float fourthShift = asFloat8(Bits.extractSigned(Short.toUnsignedInt(extractShort), 0, 4));
                    toReturn[4 * i] = toReturn[4 * i - 1] + fourthShift;
                }
        }

        if (isInverted(edgeId)) {
            float[] inverted = new float[numberSamples];
            for (int i = 0; i < numberSamples; i++) {
                inverted[i] = toReturn[numberSamples - 1 - i];
            }
            return inverted;
        }
        return toReturn;
    }

    /**
     * 
     * @param q12_4 Nombre de type short donné
     * @return La valeur de type float correspondant à la valeur Q12.4 donnée
     */
    
    private static float asFloat16(int q12_4) {
        return scalb((float)q12_4,-4);
    }
    /**
     *
     * @param q4_4 Nombre de type float donné
     * @return La valeur de type float correspondant à la valeur Q12.4 donnée
     */

    private static float asFloat8(float q4_4) {
        return scalb((float)q4_4,-4);
    }

    /**
     *
     * @param edgeId Identité de l'arête donnée.
     * @return Retourne l'identité de l'ensemble d'attributs attaché à l'arête
     * d'identité donnée.
     */

    public int attributesIndex(int edgeId) {
        return edgesBuffer.getShort(edgeId * EDGE_INTS + OFFSET_ID_OSM_ATTRIBUTE);
    }
}
