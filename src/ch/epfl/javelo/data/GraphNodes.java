package ch.epfl.javelo.data;

import ch.epfl.javelo.Q28_4;

import java.nio.*;
import java.util.Objects;

import static ch.epfl.javelo.Bits.extractUnsigned;

/**
 * 3.3.2
 * GraphNodes
 *
 * Classe permettant de représenter le tableau de tous les nœuds du
 * graphe JaVelo. Il possède un seul attribut : le Buffer permettant de
 * compresser les données, ainsi que les méthodes nous permettant d'y
 * accéder de façon lisible.
 *
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 *
 */

public record GraphNodes(IntBuffer buffer) {

    /**
     * Diverses constantes de décalage pour accéder aux données du Buffer.
     */

    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    /**
     * @return Le nombre total de nœuds présents dans le Buffer.
     */

    public int count() {
        return buffer.capacity() / NODE_INTS;
    }

    /**
     *
     * @param nodeId Identité du nœud dont on souhaite connaître la coordonnée E.
     * @return La coordonnée E du nœud d'identité donné.
     */

    public double nodeE(int nodeId) {
        return Q28_4.asDouble(buffer.get((nodeId) * NODE_INTS + OFFSET_E));
    }

    /**
     *
     * @param nodeId Identité du nœud dont on souhaite connaître la coordonnée N.
     * @return La coordonnée N du nœud d'identité donnée.
     */

    public double nodeN(int nodeId){
        return Q28_4.asDouble(buffer.get((nodeId) * NODE_INTS + OFFSET_N));
    }

    /**
     *
     * @param nodeId Nœud dont on souhaite le nombre d'arêtes sortant de celui-ci.
     * @return Le nombre d'arêtes sortant du nœud d'identité donné.
     */

    public int outDegree(int nodeId) {
        int contraction = buffer.get((nodeId) * NODE_INTS + OFFSET_OUT_EDGES);
        return extractUnsigned(contraction, 28, 4);
    }

    /**
     * @param nodeId Noeud dont on souhaite l'identité de la edgeIndex-ième arête.
     * @param edgeIndex Index de l'arête vis-à-vis de la première arête du nœud.
     * @return L'identité de la edgeIndex-ième arête sortant du nœud d'identité nodeId.
     */

    public int edgeId(int nodeId, int edgeIndex) {
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        int contraction = buffer.get((nodeId) * NODE_INTS + OFFSET_OUT_EDGES);
        return extractUnsigned(contraction, 0, 28) + edgeIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphNodes that = (GraphNodes) o;
        return Objects.equals(buffer, that.buffer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(buffer);
    }
}
