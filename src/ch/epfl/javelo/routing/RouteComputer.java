package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import java.util.*;
import java.util.function.DoubleUnaryOperator;

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
     * Si le nœud de départ et d'arrivée sont identiques, lève IllegalArgumentException.
     *
     * Si plusieurs itinéraires de coût total minimal existent, bestRouteBetween retourne n'importe lequel d'entre eux.
     */
//TODO relire si forme de tableau adapté aux opérations effectuées !
    //TODO attention ne pas confondre les edgeID et les edgesIndex
    public Route bestRouteBetween(int startNodeId, int endNodeId) {

        /**
         * Record utile pour stoker les noeuds en cours d'utilisation avec leur distance associée.
         */

        record WeightedNode(int nodeId, float distance, int previousNode, int edgeIndex)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        //début de la méthode
        Preconditions.checkArgument(startNodeId != endNodeId);
        int[] nodes = new int[graph.nodeCount()];
        for (int i = 0; i < graph.nodeCount(); i++) {
            weightedNodeList.add(new WeightedNode(i, Float.POSITIVE_INFINITY, -1,-1));
        }
        //Initialisation de la distance du premier nœud.
        weightedNodeList.set(startNodeId, new WeightedNode(startNodeId, 0, startNodeId,-1));

        //Création de la WeightedNodePriorityQueue (correspond à en_exploration), et ajout du premier
        //nœud.
        Queue<WeightedNode> weightedNodePriorityQueue = new PriorityQueue<>();
        weightedNodePriorityQueue.add(weightedNodeList.get(startNodeId));

        //Condition d'arrêt.
        boolean stop = true;

        do {
            //Récupération du WeightedNode qui a la plus petite distance, dans la WeightedNodePriorityQueue.
            WeightedNode actualWeightedNodeFrom = weightedNodePriorityQueue.remove();

            int actualNodeIndex = actualWeightedNodeFrom.nodeId;
            int nbrEdgesSortantesDuNode = graph.nodeOutDegree(actualNodeIndex);
            int targetNodeId = 0, edgeId = -1;

            //Ajout de tous les nodes connectés aux arêtes sortantes du node récupéré.
            for (int i = 0; i < nbrEdgesSortantesDuNode; i++) {
                edgeId = graph.nodeOutEdgeId(actualNodeIndex, i);
                targetNodeId = graph.edgeTargetNodeId(edgeId);
                //Sauter les nodes dont la distance à déjà été calculée.
                if (weightedNodeList.get(targetNodeId).distance != Float.POSITIVE_INFINITY) continue;
                //Si on a atteint le dernier node.
                if (targetNodeId == endNodeId) {
                    stop = false;
                    break;
                }

                //Ajout du WeightedNode avec la distance calculée selon la CostFunction.
                float distance = (float) graph.edgeLength(edgeId);
                distance *= (float) costFunction.costFactor(actualNodeIndex, edgeId);
                distance += actualWeightedNodeFrom.distance;
                weightedNodeList.set(targetNodeId, new WeightedNode(targetNodeId, distance, actualNodeIndex, i));
                weightedNodePriorityQueue.add(weightedNodeList.get(targetNodeId));
            }
            } while (stop);

        //Construction de la liste d'index du chemin trouvé.
        List<Edge> wayEdgeList = new ArrayList<>();

        int edgeIndex, edgeId, previousNodeIndex = endNodeId;
        WeightedNode actualWeightedNodeFrom;

        //Initialisation des attributs de Edge, ainsi qu'elle-même.
        int fromNodeId, toNodeId;
        PointCh fromPoint, toPoint;
        double length;
        DoubleUnaryOperator profile;
        Edge edge;

        //Construction et ajout des Edge à la wayEdgeList.
        do {
            actualWeightedNodeFrom = weightedNodeList.get(previousNodeIndex);
            edgeIndex = actualWeightedNodeFrom.edgeIndex;
            edgeId = graph.nodeOutEdgeId(previousNodeIndex, edgeIndex);

            //Initialisation des attributs de Edge.
            fromNodeId = actualWeightedNodeFrom.previousNode;
            toNodeId = previousNodeIndex;
            fromPoint = graph.nodePoint(fromNodeId);
            toPoint = graph.nodePoint(toNodeId);
            length = graph.edgeLength(edgeId);
            profile = graph.edgeProfile(edgeId);

            edge = new Edge(fromNodeId, toNodeId, fromPoint, toPoint, length, profile);

            //Ajout des Edge à l'index 0, car on le construit dans le chemin inverse.
            wayEdgeList.add(0, edge);

            previousNodeIndex = actualWeightedNodeFrom.previousNode;
        } while (toNodeId != startNodeId);

        return new SingleRoute(wayEdgeList);
    }
}
