
package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import java.util.*;


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
     *
     * @param graph        Le graph donné
     * @param costFunction la fonction de coût donnée
     */

    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }

    /**
     * @param startNodeId Nœud de départ
     * @param endNodeId   Nœud de fin
     * @return l'itinéraire de coût total minimal allant du nœud d'identité startNodeId au nœud d'identité endNodeId
     * dans le graphe passé au constructeur, ou null si aucun itinéraire n'existe.
     * Si le nœud de départ et d'arrivée sont identiques, lève IllegalArgumentException.
     * <p>
     * Si plusieurs itinéraires de coût total minimal existent, bestRouteBetween retourne n'importe lequel d'entre eux.
     */

    public Route bestRouteBetween(int startNodeId, int endNodeId) {

        //Record utile pour stoker les nœuds en cours d'utilisation avec leur distance associée.
        //l'attribut distance représente : distance parcourue + distance à vol d'oiseau

        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }


        //Début de la méthode
        Preconditions.checkArgument(startNodeId != endNodeId);

        //Remplissage de la liste des WeightedNodes, avec leurs valeurs par défaut.
        List<WeightedNode> weightedNodeList = new ArrayList<>();
        for (int i = 0; i < graph.nodeCount(); i++) {
            weightedNodeList.add(new WeightedNode(i, Float.POSITIVE_INFINITY));
        }

        //Remplissage d'un tableau avec pour chaque point (position dans le tableau), sa distance
        //parcourue pour arriver jusque-là (distance par défaut : infini)

        double[] lengthTraveled = new double[graph.nodeCount()];
        for (int i = 0; i < graph.nodeCount(); i++) {
            lengthTraveled[i] = Double.POSITIVE_INFINITY;
        }

        //Remplissage d'un tableau de previousNode, ce tableau contient pour chaque nœud l'identité du nœud
        //précédant. (Information utile à la reconstruction de l'itinéraire.)
        int[] previousNodeIds = new int[graph.nodeCount()];

        //Initialisation de la distance du nœud de départ du tableau à 0.
        lengthTraveled[startNodeId] = 0;

        //Création de la weightedNodePriorityQueue (équivalent de "en Exploration")
        Queue<WeightedNode> weightedNodeQueue = new PriorityQueue<>();

        //Ajout du premier noeud au tableau
        weightedNodeQueue.add(weightedNodeList.get(startNodeId));

        //Boucle qui s'exécute jusqu'au moment où on a trouvé l'itinéraire le plus court.
        //Condition d'arrêt : la liste de nœuds en exploration est vide (i.e aucun itinéraire a été trouvé)

        //Définition en dehors du while pour éviter de les redéfinir à chaque appelle.
        WeightedNode actualWeightedNode;
        int actualNodeId, actualEdgeId, targetNodeId;
        while (!weightedNodeQueue.isEmpty()) {
            //Remove le noeud dont la distance parcourue + distance à vol d'oiseau est la plus petite,
            actualWeightedNode = weightedNodeQueue.remove();
            actualNodeId = actualWeightedNode.nodeId;
            //Ajout des tous les nœuds connectés au nœud en exploration
            for (int i = 0; i < graph.nodeOutDegree(actualNodeId); i++) {
                actualEdgeId = graph.nodeOutEdgeId(actualNodeId, i);
                targetNodeId = graph.edgeTargetNodeId(actualEdgeId);

                //Vérification que le nœud connecté à la i-ème arête sortante n'ait pas encore été exploré.
                if(weightedNodeList.get(targetNodeId).distance != Float.POSITIVE_INFINITY) continue;
                //Mise à jour de l'attribut previous node du tableau. (Qui est l'ID du nœud actuel à la position de
                //l'identité du nœud connecté)
                previousNodeIds[targetNodeId] = actualNodeId;

                //Vérification si nœud en exploration == endNode
                if (targetNodeId == endNodeId) {
                    System.out.println("lol");
                    //initialisation de la liste d'arête utile à la création de la route à retourner
                    List<Edge> edgeList = new LinkedList<>();

                    //Construction de l'itinéraire dans l'ordre inverse.
                    //Condition d'arrêt : le noeud précédent == startNode
                    while (targetNodeId != startNodeId) {
                        //Récupération de l'index de l'arête sortante du nœud précédant allant jusqu'au nœud actuel.
                        int index = 0;
                        while (graph.edgeTargetNodeId(graph.nodeOutEdgeId(actualNodeId, index)) != targetNodeId) {
                            ++index;
                        }
                        //Ajout de l'arête à la liste
                        edgeList.add(Edge.of(graph, graph.nodeOutEdgeId(
                                actualNodeId, index), actualNodeId, targetNodeId));
                        //Actualisation de targetNodeId et actualNodeId.
                        targetNodeId = actualNodeId;
                        actualNodeId = previousNodeIds[actualNodeId];
                    }
                    //Inversion de l'ordre des éléments de la liste puis retour de la route construite.
                    Collections.reverse(edgeList);
                    return new SingleRoute(edgeList);
                }

                //Ajout dans le tableau de distance, la distance du chemin terrestre (incluant CostFunction)
                float distance = (float) graph.edgeLength(actualEdgeId);
                distance *= (float) costFunction.costFactor(actualNodeId, actualEdgeId);
                //Condition nous permettant de ne pas ajouter les noeuds dont l'itinéraire est "inatteignable"
                if (distance == Float.POSITIVE_INFINITY) continue;
                distance += lengthTraveled[actualNodeId];
                lengthTraveled[targetNodeId] = distance;
                distance += graph.nodePoint(actualNodeId).distanceTo(graph.nodePoint(targetNodeId));

                //Modification de la distance du WeightNode selon A* dans l'attribut distance, qui représente
                //La somme de la distance parcourue + la distance à vol d'oiseau.
                weightedNodeList.set(targetNodeId, new WeightedNode(targetNodeId, distance));
                //Ajout de ce WeightedNode à la liste en Exploration
                weightedNodeQueue.add(weightedNodeList.get(targetNodeId));
            }
        }
        //return null si aucun itinéraire trouvé
        return null;
    }
}