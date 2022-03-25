package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.data.GraphEdges;
import ch.epfl.javelo.data.GraphNodes;
import ch.epfl.javelo.data.GraphSectors;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

public class EdgeTest {

    @Test
    void ofWorksProperly(){
        //Noeuds d'index 0 et 1
        int fromNodeId = 0, toNodeId = 1 ;
        //Points :
        // - Départ en bas à gauche de la carte suisse ;
        // - Arrivée 10m plus loin à l'est et au nord ;
        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N + 10);
        //Longueur : 14m
        double length = 14;
        //Profil en long : sur 14 m, altitudes de 0, 1, 2, 3, 4, 5, 6 et 7m
        DoubleUnaryOperator profile = Functions.sampled(new float[]{0, Math.scalb(1, -4), Math.scalb(2, -4), Math.scalb(3, -4), Math.scalb(4, -4), Math.scalb(5, -4), Math.scalb(6, -4), Math.scalb(7, -4)}, 14);
        Edge expected = new Edge(fromNodeId, toNodeId, fromPoint, toPoint, length, profile);

        //Buffer des noeuds correspondants
        IntBuffer nodesBuffer = IntBuffer.wrap(new int[]{
                (int)SwissBounds.MIN_E << 4, (int)SwissBounds.MIN_N << 4, 3,
                (int)(SwissBounds.MIN_E + 10) << 4, (int)(SwissBounds.MIN_N + 10) << 4, 3
        });
        GraphNodes nodes = new GraphNodes(nodesBuffer);

        //Buffer inutile car non testé de secteurs
        ByteBuffer sectorBuffer  = ByteBuffer.allocate(1000);
        GraphSectors sectors = new GraphSectors(sectorBuffer);

        ByteBuffer edgesBuffer = ByteBuffer.allocate(1000);
        //Index noeud destination
        edgesBuffer.putInt(1);
        //Longueur de l'arête
        edgesBuffer.putShort((short)(14 << 4));
        //Dénivelé positif total de l'arête
        edgesBuffer.putShort((short)(7 << 4));


        IntBuffer profileIdsBuffer = IntBuffer.allocate(1000);
        //Profil 1, identité du premier profil : 0
        profileIdsBuffer.put(1 << 30);
        //Profil en long : sur 14m, altitudes de 0, 1, 2, 3, 4, 5, 6 et 7m
        ShortBuffer elevationsBuffer = ShortBuffer.wrap(new short[]{
                0, 1, 2, 3, 4, 5, 6, 7
        });
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIdsBuffer, elevationsBuffer);


        Graph graph = new Graph(nodes, sectors, edges, List.of());
        assertEquals(expected, Edge.of(graph, 0, fromNodeId, toNodeId));
    }

    @Test
    void positionClosestToWorksProperly(){
        Edge edge = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N + 10), 10*Math.sqrt(2), Functions.sampled(new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0}, 10*Math.sqrt(2)));
        assertEquals(3*Math.sqrt(2), edge.positionClosestTo(new PointCh(SwissBounds.MIN_E + 4, SwissBounds.MIN_N + 2)), 1e-7);
    }

    @Test
    void pointAtWorksProperly(){
        Edge edge = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N + 10), 10*Math.sqrt(2), Functions.sampled(new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0}, 10*Math.sqrt(2)));
        assertEquals(new PointCh(SwissBounds.MIN_E + 3, SwissBounds.MIN_N + 3), edge.pointAt(3*Math.sqrt(2)));
    }

    @Test
    void elevationAtWorksProperly(){
        Edge edge = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 4), new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N + 4), 10, Functions.sampled(new float[]{3, 4, 3, 2, 3, 4, 5, 9, 1, 2, 0}, 10));
        assertEquals(7, edge.elevationAt(6.5));
        assertEquals(3, edge.elevationAt(0));
        assertEquals(0, edge.elevationAt(10));
        assertEquals(2.5, edge.elevationAt(3.5));
    }



}
