package ch.epfl.javelo.data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.Functions;
import ch.epfl.javelo.data.GraphSectors;
import ch.epfl.javelo.data.GraphNodes;


/**
 * 4.3.2
 * Graph
 *
 * Classe offrant une méthode statique permettant de charger le graphe depuis
 * un répertoire.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public final class Graph {

    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;

    Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = attributeSets;
    }

    /**
     *
     * @param basePath Chemin d'accès donné.
     * @return Le graphe Javelo obtenu à partir des fichiers se trouvant dans le répertoire.
     * @throws IOException En cas d'erreur d'entrée/sortie.
     */

    public static Graph loadFrom(Path basePath) throws IOException {
        //Nodes
        Path nodesPath = basePath.resolve("nodes.bin");
        IntBuffer nodeBuffer = mappedBuffer(nodesPath).asIntBuffer();
        GraphNodes nodes = new GraphNodes(nodeBuffer);

        //Sectors
        Path sectorsPath = basePath.resolve("sectors.bin");
        ByteBuffer sectorBuffer = mappedBuffer(sectorsPath);
        GraphSectors sectors = new GraphSectors(sectorBuffer);

        //Edges
        Path edgesPath = basePath.resolve("edges.bin");
        ByteBuffer edgeBuffer = mappedBuffer(edgesPath);
        Path profileIdsPath = basePath.resolve("profile_ids.bin");
        IntBuffer profilBuffer = mappedBuffer(profileIdsPath).asIntBuffer();
        Path elevationPath = basePath.resolve("elevation.bin");
        ShortBuffer elevationBuffer = mappedBuffer(elevationPath).asShortBuffer();
        GraphEdges edges = new GraphEdges(edgeBuffer, profilBuffer, elevationBuffer);

        //Attributes
        Path attributesPath = basePath.resolve("attributes.bin");
        LongBuffer attributeBuffer = mappedBuffer(basePath).asLongBuffer();
        List<AttributeSet> attributeSets = null;
        for (int i = 0; i < attributeBuffer.capacity(); i++) {
            attributeSets.add(new AttributeSet(attributeBuffer.get(i)));
        }

        return new Graph(nodes, sectors, edges, attributeSets);
    }

    /**
     * Méthode permettant de lire un fichier bin et de le retourner en ByteBuffer.
     * ATTENTION à ensuite "recast" en Buffer du type voulu (int/short...)
     * @param basePath Chemin d'accès donné.
     * @return Retourne le fichier bin lu en ByteBuffer.
     * @throws IOException En cas d'erreur d'entrée/sortie.
     */

    private static ByteBuffer mappedBuffer(Path basePath) throws IOException{
        try (FileChannel channel = FileChannel.open(basePath)) {
            return channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
    }

    /** 
     * @returnn Le nombre total de nœuds dans le graphe.
     */
    
    public int nodeCount() {
        return nodes.count();
    }

    /**
     * @param nodeId Identité du nœud donné.
     * @return La position géographique du nœud donné.
     */
    
    public PointCh nodePoint(int nodeId) {
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    /**
     * @param nodeId Identité du nœud donné.
     * @return Retourne le nombre d
     */

    public int nodeOutDegree(int nodeId) {
       return nodes.outDegree(nodeId);
    }

    /**
     * @param nodeId Identité dunœud donné.
     * @param edgeIndex
     * @return Retourne l'identité de la edgeIndex-ième arête soratnt du nœud
     * d'identité donné.
     */

    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId, edgeIndex);
    }

    public int nodeClosestTo(PointCh point, double searchDistance) {
        //TODO check si aucun noeud ne correspond aux critères;

        List<GraphSectors.Sector> sectorList = sectors.sectorsInArea(point, searchDistance);
        double min = Double.POSITIVE_INFINITY;
        int nodeId = -1;
        for (GraphSectors.Sector sector : sectorList) {
            int startNode = sector.startNodeId();
            int endNode = sector.endNodeId();
            for (int j = startNode; j <= endNode; j++) {
                double distance = point.squaredDistanceTo(new PointCh(nodes.nodeE(j), nodes.nodeN(j)));
                if (distance < min) {
                    min = distance;
                    nodeId = j;
                }
            }
        }
        return nodeId;
    }

    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }

    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }


    public AttributeSet edgeAttributes(int edgeId) {
        return attributeSets.get(edges.attributesIndex(edgeId));
    }

    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }

    public DoubleUnaryOperator edgeProfile(int edgeId) {
        if (edges.hasProfile(edgeId)) { return Functions.sampled(edges.profileSamples(edgeId), edgeLength(edgeId)); }
        else { return Functions.constant(Double.NaN);}
    }


}
