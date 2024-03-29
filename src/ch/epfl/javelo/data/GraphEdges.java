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
 * <p>
 * Enregistrement représentant le tableau de toutes les arêtes du graphe JaVelo.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
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
    private static final int OFFSET_CASE_2 = 2;
    private static final int OFFSET_CASE_3 = 4;

    /**
     * Retourne si l'arête d'identité donnée va dans le sens inverse de la
     * voie OSM dont elle provient.
     * @param edgeId Identité de l'arête donnée.
     * @return Retourne si l'arête d'identité donnée va dans le sens inverse
     * de la voie OSM dont elle provient.
     */

    public boolean isInverted(int edgeId) {
        byte isInvertedBit = (byte) Bits.extractUnsigned(
                edgesBuffer.getInt(edgeId * EDGE_INTS + OFFSET_WAY_AND_ID),
                31, 1);
        return 1 == isInvertedBit;
    }

    /**
     * Retourne l'identité du nœud destination de l'arête d'identité donnée.
     * @param edgeId Identité de l'arête donnée.
     * @return Retourne l'identité du nœud destination de l'arête d'identité donnée.
     */

    public int targetNodeId(int edgeId) {
        int targetNodeId = edgesBuffer.getInt(edgeId * EDGE_INTS + OFFSET_WAY_AND_ID);
        return isInverted(edgeId) ? ~(targetNodeId) : targetNodeId;
    }

    /**
     * Retourne la longueur en mètres de l'arête d'identité donnée.
     * @param edgeId Identité de l'arête donnée.
     * @return Retourne la longueur en mètres de l'arête d'identité donnée.
     */

    public double length(int edgeId) {
        return Q28_4.asDouble(Bits.extractUnsigned(edgesBuffer.getShort
                (edgeId * EDGE_INTS + OFFSET_EDGE_LENGTH), 0, 16));
    }

    /**
     * Retourne le dénivelé positif, en mètres, de l'arête d'identité donnée.
     * @param edgeId Identité de l'arête donnée.
     * @return Retourne le dénivelé positif, en mètres, de l'arête d'identité donnée.
     */

    public double elevationGain(int edgeId) {
        return Q28_4.asDouble(Bits.extractUnsigned(edgesBuffer.getShort
                (edgeId * EDGE_INTS + OFFSET_ASCENDING_ELEVATION), 0, 16));
    }

    /**
     * Retourne si l'arête donnée possède un profil.
     * @param edgeId Identité de l'arête donnée.
     * @return Retourne si l'arête donnée possède un profil.
     */

    public boolean hasProfile(int edgeId) {
        int profilByte = Bits.extractUnsigned(profileIds.get(edgeId), 30, 2);
        return profilByte != 0;
    }

    /**
     * Retourne le tableau des échantillons du profil de l'arête d'identité donnée,
     * qui est vide si l'arête ne possède pas de profil.
     * @param edgeId Identité de l'arête donnée.
     * @return Retourne le tableau des échantillons du profil de l'arête d'identité
     * donnée, qui est vide si l'arête ne possède pas de profil.
     */

    public float[] profileSamples(int edgeId) {
        if (!hasProfile(edgeId)) return new float[]{};
        //Calcule le nombre d'échantillons en fonction de la formule donnée.
        int numberSamples = 1 + Math2.ceilDiv((int) (scalb(length(edgeId), 4)), Q28_4.ofInt(2));
        float[] toReturn = new float[numberSamples];
        int firstAltiId = Bits.extractUnsigned(profileIds.get(edgeId), 0, 30);
        //Récupère le type de profil (les deux bits de poids fort).
        byte profilType = (byte) Bits.extractUnsigned(profileIds.get(edgeId), 30, 2);

        //Traite les différents types de profil.
        switch (profilType) {
            case (byte) 1:
                for (int i = 0; i < numberSamples; i++) {
                    toReturn[i] = asFloat16(Short.toUnsignedInt(elevations.get(firstAltiId + i)));
                }
                break;

            case (byte) 2:
                samplesForType2Profile(numberSamples, toReturn, firstAltiId);
                break;

            case (byte) 3:
                samplesForType3Profile(numberSamples, toReturn, firstAltiId);
        }
        //Inverse le tableau si l'arête est inversée.
        if (isInverted(edgeId)) {
            for (int i = 0; i < Math2.ceilDiv(numberSamples,2); i++) {
                float tempFloatToSwap = toReturn[i];
                toReturn[i] = toReturn[numberSamples - 1 - i];
                toReturn[numberSamples - 1 - i] = tempFloatToSwap;
            }
        }
        return toReturn;
    }

    /**
     * Méthode privée extrayant le profil d'une arête avec un profil de type 2, et le mettant
     * dans un tableau passé en paramètre.
     * @param numberSamples Nombre d'échantillons du profil.
     * @param toReturn Tableau dans lequel les valeurs du profil de l'arête sont mises.
     * @param firstAltiId Identité de la première altitude donnée.
     */
    private void samplesForType2Profile(int numberSamples, float[] toReturn, int firstAltiId) {
        int shortsToRead2 = numberSamples / OFFSET_CASE_2;
        toReturn[0] = asFloat16(Short.toUnsignedInt(elevations.get(firstAltiId)));
        for (int i = 1; i <= shortsToRead2; i++) {
            //Première altitude.
            int extractShort = Short.toUnsignedInt(elevations.get(firstAltiId + i));

            float firstShift = asFloat16(Bits.extractSigned(extractShort, 8, 8));
            toReturn[OFFSET_CASE_2 * i - 1] = toReturn[OFFSET_CASE_2 * (i - 1)]
                    + firstShift;

            //Pour éviter OutOfBoundException si les 8 derniers bits du short sont inutiles.
            if (numberSamples - 1 < OFFSET_CASE_2 * i) break;

            //Remplit les valeurs en fonction de la précédente, et du shift.
            float secondShift = asFloat16(Bits.extractSigned(extractShort, 0, 8));
            toReturn[OFFSET_CASE_2 * i] = toReturn[OFFSET_CASE_2 * i - 1] + secondShift;
        }
    }

    /**
     * Méthode privée extrayant le profil d'une arête avec un profil de type 3, et le mettant
     * dans un tableau passé en paramètre.
     * @param numberSamples Nombre d'échantillons du profil.
     * @param toReturn Tableau dans lequel les valeurs du profil de l'arête sont mises.
     * @param firstAltiId Identité de la première altitude donnée.
     */
    private void samplesForType3Profile(int numberSamples, float[] toReturn, int firstAltiId) {
        //Nombre de shorts à lire après le premier short, sachant qu'un short contient
        //4 nibble.
        int shortsToRead4 = (numberSamples + 2) / OFFSET_CASE_3;
        toReturn[0] = asFloat16(Short.toUnsignedInt(elevations.get(firstAltiId)));
        for (int i = 1; i <= shortsToRead4; i++) {
            int extractShort = Short.toUnsignedInt(elevations.get(firstAltiId + i));

            float firstShift = asFloat8(Bits.extractSigned(extractShort, 12, 4));
            toReturn[OFFSET_CASE_3 * i - 3] = toReturn[OFFSET_CASE_3 * i - 4] + firstShift;

            //Pour éviter OutOfBoundException si les 8 derniers bits du short sont inutiles
            if (numberSamples + 1 < OFFSET_CASE_3 * i) break;

            float secondShift = asFloat8(Bits.extractSigned(extractShort, 8, 4));
            toReturn[OFFSET_CASE_3 * i - 2] = toReturn[OFFSET_CASE_3 * i - 3] + secondShift;

            //Pour éviter OutOfBoundException si les 8 derniers bits du short sont inutiles
            if (numberSamples < OFFSET_CASE_3 * i) break;

            float thirdShift = asFloat8(Bits.extractSigned(extractShort, 4, 4));
            toReturn[OFFSET_CASE_3 * i - 1] = toReturn[OFFSET_CASE_3 * i - 2] + thirdShift;

            //Pour éviter OutOfBoundException si les 8 derniers bits du short sont inutiles
            if (numberSamples - 1 < OFFSET_CASE_3 * i) break;

            //Ajoute les valeurs en fonction du shift et de la valeur précédente.
            float fourthShift = asFloat8(Bits.extractSigned(extractShort, 0, 4));
            toReturn[OFFSET_CASE_3 * i] = toReturn[OFFSET_CASE_3 * i - 1] + fourthShift;
        }
    }

    /**
     * Méthode privée retournant la valeur de type float correspondant à la valeur
     * Q12.4 donnée.
     * @param q12_4 Valeur de type short donnée.
     * @return La valeur de type float correspondant à la valeur Q12.4 donnée.
     */

    private static float asFloat16(int q12_4) {
        return scalb((float) q12_4, -4);
    }

    /**
     * Méthode privée retournant la valeur de type float correspondant à la valeur
     * Q4.4 donnée.
     * @param q4_4 Valeur de type float donnée.
     * @return La valeur de type float correspondant à la valeur Q4.4 donnée.
     */

    private static float asFloat8(int q4_4) {
        return scalb((float) q4_4, -4);
    }

    /**
     * Retourne l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée.
     * @param edgeId Identité de l'arête donnée.
     * @return Retourne l'identité de l'ensemble d'attributs attaché à l'arête
     * d'identité donnée.
     */

    public int attributesIndex(int edgeId) {
        return Short.toUnsignedInt(edgesBuffer.getShort(
                edgeId * EDGE_INTS + OFFSET_ID_OSM_ATTRIBUTE));
    }
}
