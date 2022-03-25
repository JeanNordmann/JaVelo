package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.List;


class GraphTest {
    @Test
    void nodeClosestToWorksProperly() throws IOException{
        IntBuffer nodesBuffer = IntBuffer.allocate(16384*3);
        for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 128; x++) {
                nodesBuffer.put((int)(SwissBounds.MIN_E + x*(SwissBounds.WIDTH/128.0)) << 4);
                nodesBuffer.put((int)(SwissBounds.MIN_N + y*(SwissBounds.HEIGHT/128.0)) << 4);
                nodesBuffer.put(1);
            }
        }
        GraphNodes nodes = new GraphNodes(nodesBuffer);

        ByteBuffer sectorBuffer  = ByteBuffer.allocate(16384*6);
        int lastNode = 0 ;
        for (int i = 0; i < 16384; i++){
            sectorBuffer.putInt(lastNode++);
            sectorBuffer.putShort((short)1);
        }
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

        PointCh test0 = new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N);
        PointCh test1 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh test2 = new PointCh((SwissBounds.MAX_E + SwissBounds.MIN_E)/2, (SwissBounds.MIN_N + SwissBounds.MAX_N)/2);
        PointCh test4 = new PointCh(SwissBounds.MIN_E+32434, SwissBounds.MIN_N+ 34565);

        assertEquals(16383, graph.nodeClosestTo(test0, 10000));
        assertEquals(0, graph.nodeClosestTo(test1, 10000));
        assertEquals(8256, graph.nodeClosestTo(test2, 10000));
        assertEquals(-1, graph.nodeClosestTo(test4, 0));
        assertEquals(-1, graph.nodeClosestTo(test4, -10000));
    }

    @Test
    void edgeProfileThrowsOnNonProfile() throws IOException {
        //Points :
        // - Départ en bas à gauche de la carte suisse ;
        // - Arrivée 10m plus loin à l'est et au nord ;
        IntBuffer nodesBuffer = IntBuffer.wrap(new int[]{
                (int)SwissBounds.MIN_E << 4, (int)SwissBounds.MIN_N << 4, 3,
                (int)(SwissBounds.MIN_E + 10) << 4, (int)(SwissBounds.MIN_N + 10) << 4, 3
        });
        //Buffer des noeuds correspondants
        GraphNodes nodes = new GraphNodes(nodesBuffer);

        //Buffer inutile car non testé de secteurs
        ByteBuffer sectorBuffer  = ByteBuffer.allocate(16384*6);
        for (int i = 0; i < 16384; i += 1) {
            sectorBuffer.putInt(i);
            sectorBuffer.putShort((short) 1);
        }
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
        profileIdsBuffer.put(0);
        //Profil en long : sur 14m, altitudes de 0, 1, 2, 3, 4, 5, 6 et 7m
        ShortBuffer elevationsBuffer = ShortBuffer.wrap(new short[]{
                0, 1, 2, 3, 4, 5, 6, 7
        });
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIdsBuffer, elevationsBuffer);
        Graph graph = new Graph(nodes, sectors, edges, List.of());
        assertEquals(Functions.constant(Double.NaN), graph.edgeProfile(0));
    }
}

