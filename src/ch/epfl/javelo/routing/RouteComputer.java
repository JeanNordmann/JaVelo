
package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.io.File;
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
     * @param graph Le graph donné
     * @param costFunction la fonction de coût donnée
     */

    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }

    /**
     * @param startNodeId Nœud de départ
     * @param endNodeId Nœud de fin
     * @return l'itinéraire de coût total minimal allant du nœud d'identité startNodeId au nœud d'identité endNodeId
     * dans le graphe passé au constructeur, ou null si aucun itinéraire n'existe.
     * Si le nœud de départ et d'arrivée sont identiques, lève IllegalArgumentException.
     *
     * Si plusieurs itinéraires de coût total minimal existent, bestRouteBetween retourne n'importe lequel d'entre eux.
     */

    public Route bestRouteBetween(int startNodeId, int endNodeId) {

        //Record utile pour stoker les nœuds en cours d'utilisation avec leur distance associée.
        record WeightedNode(int nodeId, float distance, int previousNode,
                            float distanceAndFliesDistance) implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                //sans optimisation
                /*return Float.compare(this.distance, that.distance);*/
                return Float.compare(this.distanceAndFliesDistance, that.distanceAndFliesDistance);
            }
        }

        //Début de la méthode
        Preconditions.checkArgument(startNodeId != endNodeId);

        //Remplissage de la liste des WeightedNodes, avec leurs valeurs par défaut.
        List<WeightedNode> weightedNodeList = new ArrayList<>();
        for (int i = 0; i < graph.nodeCount(); i++) {
            weightedNodeList.add(new WeightedNode(i, Float.POSITIVE_INFINITY, -1,
                    Float.POSITIVE_INFINITY));
        }
        //Initialisation de la distance du premier nœud.
        weightedNodeList.set(startNodeId, new WeightedNode(startNodeId, 0, startNodeId,
                Float.POSITIVE_INFINITY));

        //Création de la WeightedNodePriorityQueue (correspond à en_exploration), et ajout du premier
        //nœud.
        Queue<WeightedNode> weightedNodePriorityQueue = new PriorityQueue<>();
        weightedNodePriorityQueue.add(weightedNodeList.get(startNodeId));
        List<Integer> listeIndiceNoeud = new ArrayList<>();


        // Définini ici et pas dans le while pour pouvoir y accéder dans la condition d'arrêt.
        int targetNodeId =-1;
        do {
            //Récupération du WeightedNode qui a la plus petite distance, dans la WeightedNodePriorityQueue.
            WeightedNode actualWeightedNodeFrom = weightedNodePriorityQueue.remove();

            int actualNodeIndex = actualWeightedNodeFrom.nodeId;
            int nbrEdgesSortantesDuNode = graph.nodeOutDegree(actualNodeIndex);
            int edgeId;

            //Ajout de tous les nodes connectés aux arêtes sortantes du node récupéré.
            for (int i = 0; i < nbrEdgesSortantesDuNode; i++) {
                edgeId = graph.nodeOutEdgeId(actualNodeIndex, i);
                targetNodeId = graph.edgeTargetNodeId(edgeId);
                //Sauter les nodes dont la distance à déjà été calculée.
                if (weightedNodeList.get(targetNodeId).distance != Float.POSITIVE_INFINITY) continue;

                //Ajout du WeightedNode avec la distance calculée selon la CostFunction.
                float distance = (float) graph.edgeLength(edgeId);
                distance *= (float) costFunction.costFactor(actualNodeIndex, edgeId);
                distance += actualWeightedNodeFrom.distance;
                float distanceAndFlyDistance = distance +
                        (float) graph.nodePoint(endNodeId).distanceTo(graph.nodePoint(targetNodeId));
                weightedNodeList.set(targetNodeId, new WeightedNode(targetNodeId, distance, actualNodeIndex, distanceAndFlyDistance));
                weightedNodePriorityQueue.add(weightedNodeList.get(targetNodeId));
                listeIndiceNoeud.add(targetNodeId);
                if (listeIndiceNoeud.size() == weightedNodeList.size()) return null;
                //Si on a atteint le dernier node.
                if (targetNodeId == endNodeId) break;
            }
            } while (targetNodeId != endNodeId);

        //Construction de la liste d'index du chemin trouvé.
        List<Edge> wayEdgeList = new LinkedList<>();

        int edgeIndex = 0, edgeId, previousNodeIndex = endNodeId;
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
            // Récupération du EdgeIndex (info anciennement stockée pour tout les WeightedNodes dans le record), mais
            // ce n'était pas optimisé car on stockait cette info alors qu'elle est "retrouvable" avec "previousNode"
            // et de plus, elle n'est utile que pour les noeuds de l'itinéraire le plus court.

            fromNodeId = actualWeightedNodeFrom.previousNode;
            toNodeId = previousNodeIndex;
            int maxEdgeOutFromNodeID = graph.nodeOutDegree(fromNodeId);
            for (int i = 0; i < maxEdgeOutFromNodeID; i++) {
                if (graph.nodeOutEdgeId(fromNodeId, i) == toNodeId) {
                    edgeIndex = i;
                    break;
                }
            }
            edgeId = graph.nodeOutEdgeId(previousNodeIndex, edgeIndex);

            //Initialisation des attributs de Edge.
            fromPoint = graph.nodePoint(fromNodeId);
            toPoint = graph.nodePoint(toNodeId);
            length = graph.edgeLength(edgeId);
            profile = graph.edgeProfile(edgeId);

            edge = new Edge(fromNodeId, toNodeId, fromPoint, toPoint, length, profile);

            //Ajout des Edge à l'index 0, car on le construit dans le chemin inverse.
            wayEdgeList.add(0, edge);

            previousNodeIndex = actualWeightedNodeFrom.previousNode;
        } while (toNodeId != startNodeId);
        //TODO trop getto a enlever


        return new SingleRoute(wayEdgeList);
    }
}

