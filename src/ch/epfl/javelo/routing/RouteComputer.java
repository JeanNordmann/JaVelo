package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
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
     *
     * @param graph Le graph donné.
     * @param costFunction La fonction de coût donnée.
     */

    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }

    /**
     * @param startNodeId Nœud de départ
     * @param endNodeId   Nœud de fin
     * @return L'itinéraire de coût total minimal allant du nœud d'identité startNodeId au nœud d'identité endNodeId
     * dans le graphe passé au constructeur, ou null si aucun itinéraire n'existe.
     * Si le nœud de départ et d'arrivée sont identiques, lève IllegalArgumentException.
     * Si plusieurs itinéraires de coût total minimal existent, bestRouteBetween retourne n'importe lequel d'entre eux.
     */

    public Route bestRouteBetween(int startNodeId, int endNodeId) {

        //Record utile pour stocker les nœuds en cours d'utilisation avec leur distance associée.
        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        //Vérifie si l'itinéraire est valide.
        Preconditions.checkArgument(startNodeId != endNodeId);

        //Remplissage de la liste des WeightedNodes, avec leurs valeurs par défaut.
        List<WeightedNode> weightedNodeList = new ArrayList<>();
        for (int i = 0; i < graph.nodeCount(); i++) {
            weightedNodeList.add(new WeightedNode(i, Float.POSITIVE_INFINITY));
        }

        //Remplissage d'un tableau avec pour chaque point (position dans le tableau), sa distance
        //Selon A* (distance par défaut : Infini positif)
        double[] lengthAStar = new double[graph.nodeCount()];
        Arrays.fill(lengthAStar, Float.POSITIVE_INFINITY);

        //Tableau de distance à vol d'oiseau.
        double[] crowFlies = new double[graph.nodeCount()];

        //Point utile plus tard pour calculer les crowFlies.
        PointCh endPoint = graph.nodePoint(endNodeId);

        //Remplissage d'un tableau de previousNode, ce tableau contient pour chaque nœud l'identité du nœud
        //précédent. (Information utile à la reconstruction de l'itinéraire)
        int[] previousNodeIds = new int[graph.nodeCount()];

        //Initialisation de la distance du nœud de départ du tableau à 0.
        lengthAStar[startNodeId] = 0;

        //Création de la weightedNodePriorityQueue (équivalent de "en Exploration")
        //Ajout du premier nœud au tableau
        Queue<WeightedNode> weightedNodeQueue = new PriorityQueue<>();
        weightedNodeQueue.add(weightedNodeList.get(startNodeId));

        //Condition d'arrêt : la liste de nœuds en exploration est vide (i.e aucun itinéraire n'a été trouvé)
        //Définition en dehors du while pour éviter de les redéfinir à chaque appel.
        WeightedNode actualWeightNode;
        int actNodeId, actEdgeId, targetNodeId;
        while (!weightedNodeQueue.isEmpty()) {
            //Remove le nœud dont la distance selon A* est la plus petite et récupérer son nodeId.
            actualWeightNode = weightedNodeQueue.remove();
            actNodeId = actualWeightNode.nodeId;

            //Condition permettant de passer tous les nœuds déjà explorés.
            if(actualWeightNode.distance == Float.NEGATIVE_INFINITY) continue;

            //Vérification si le nœud en exploration actuellement est endNode.
            //Si oui, on commence à construire l'itinéraire.
            if (actNodeId == endNodeId) {
                //Initialisation de la liste d'arête utile à la création de la route à retourner
                List<Edge> edgeList = new LinkedList<>();
                int previousNodeId = previousNodeIds[actNodeId];

                //Construction de l'itinéraire dans l'ordre inverse.
                //Condition d'arrêt : le noeud actuel == startNode
                while (actNodeId != startNodeId) {
                    //Récupération de l'index de l'arête sortante du nœud précédant allant jusqu'au nœud actuel.
                    for (int j = 0; j < graph.nodeOutDegree(previousNodeId); j++) {
                        //Ajout de l'arête à la liste si il s'agit de l'arête trouvée par le reste de
                        //l'algorithme.
                        if (graph.edgeTargetNodeId(graph.nodeOutEdgeId(previousNodeId, j)) == actNodeId) {
                            edgeList.add(Edge.of(graph, graph.nodeOutEdgeId(
                                    previousNodeId, j), previousNodeId, actNodeId));
                            break;
                        }
                    }
                    //Actualisation des variables actNodeId et previousNodeId.
                    actNodeId = previousNodeId;
                    previousNodeId = previousNodeIds[previousNodeId];
                }
                //Inversion de l'ordre des éléments de la liste puis retour de la route construite.
                Collections.reverse(edgeList);
                return new SingleRoute(edgeList);
            }
            //Début de la partie qui recherche l'itinéraire le plus court.

            //Ajout des tous les nœuds connectés au nœud en exploration
            for (int i = 0; i < graph.nodeOutDegree(actNodeId); i++) {
                actEdgeId = graph.nodeOutEdgeId(actNodeId, i);
                targetNodeId = graph.edgeTargetNodeId(actEdgeId);

                //Mise à jour de l'attribut previous node du tableau. (Qui est l'ID du nœud actuel à la position de
                //l'identité du nœud connecté)
                crowFlies[targetNodeId] = endPoint.distanceTo(graph.nodePoint(targetNodeId));

                //Calcul de la distance Selon A* (incluant CostFunction)
                float distance = (float) (lengthAStar[actNodeId] - crowFlies[actNodeId] + crowFlies[targetNodeId])
                        + (float) graph.edgeLength(actEdgeId) * (float) costFunction.costFactor(actNodeId, actEdgeId);

                //Si le nœud connecté à la i-ème arête sortante n'a pas encore a été exploré via un itinéraire optimal,
                //alors on l'ajoute à la liste des nœuds en exploration.
                if (distance < lengthAStar[targetNodeId]) {
                    //Modification de la distance du WeightNode selon A* dans le tableau de distance selon A*
                    lengthAStar[targetNodeId] = distance;
                    weightedNodeList.set(targetNodeId, new WeightedNode(targetNodeId, distance));
                    //Ajout de ce WeightedNode à la liste en Exploration
                    weightedNodeQueue.add(weightedNodeList.get(targetNodeId));
                    //Mise à jour du nœud précédent.
                    previousNodeIds[targetNodeId] = actNodeId;
                }
            }
            //Marquage des nœuds explorés, pour ne pas les explorer à nouveau.
            lengthAStar[actNodeId] = Float.NEGATIVE_INFINITY;
        }
        //Return une route nulle si aucun itinéraire n'a été trouvé.
        return null;
    }
}