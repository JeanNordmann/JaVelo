package ch.epfl.javelo.routing;

/**
 * 6.3.2
 * CostFunction
 * <p>
 * Interface représentant une fonction de coût.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public interface CostFunction {

    /**
     * @param nodeId Nœud d'identité donnée.
     * @param edgeId Arête d'identité donnée.
     * @return Retourne le facteur par lequel la longueur d'arête
     * d'identité donnée, partant du nœud d'identité donnée, doit
     * être multipliée ; ce facteur doit impérativement être
     * supérieur ou égal à 1.
     */
    double costFactor(int nodeId, int edgeId);

}
