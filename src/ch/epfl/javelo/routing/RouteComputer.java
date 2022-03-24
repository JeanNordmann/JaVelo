package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

/**
 * 6.3.4
 * RouteComputer
 *
 * La classe RouteComputer représente un planificateur d'itinéraire.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */
public class RouteComputer {

    private final Graph graph;
    private final CostFunction costFunction;

    /**
     * edgeIdETPASSONINDEX
     * @param graph le graph donné
     * @param costFunction la fonction de coût donnée
     */
    RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }


    /**
     *
     * @param startNodeId noeud de départ
     * @param endNodeId noeud de fin
     * @return l'itinéraire de coût total minimal allant du nœud d'identité startNodeId au nœud d'identité endNodeId
     * dans le graphe passé au constructeur, ou null si aucun itinéraire n'existe.
     *  Si le nœud de départ et d'arrivée sont identiques, lève IllegalArgumentException.
     *
     * Si plusieurs itinéraires de coût total minimal existent, bestRouteBetween retourne n'importe lequel d'entre eux.
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId){
        /**
         * reccord util pour stoker les noeuds en cour d'utilisation avec leur distance associé
         */
        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        //début de la méthode
        Preconditions.checkArgument(startNodeId != endNodeId);



        return new MultiRoute(null);
    }


}
