package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    private static final double SECTOR_WIDTH = SwissBounds.WIDTH / 128.0;
    private static final double SECTOR_HEIGHT = SwissBounds.HEIGHT / 128.0;

    @Test
    void loadFromTestBasis() {
        Path basePath = Path.of("lausanne");
        try {
            Graph.loadFrom(basePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //test node closest too
    /*@Test
    void loadFromNodesWorkProperly() throws IOException {
        IntBuffer b;
        byte[] bb;
        try (InputStream stream = new FileInputStream("lausanne/nodes.bin")) {
            bb = stream.readAllBytes();
            b = (ByteBuffer.wrap(bb)).asIntBuffer();
        }
        GraphNodes graphNodes = new GraphNodes(b);
        assertEquals(graphNodes, Graph.loadFrom(Path.of("lausanne")).getNodes());
    }
*/
    void loadFromEdgesWorkProperly() throws IOException {
        ByteBuffer byteBuffer;
        IntBuffer intBuffer;
        ShortBuffer shortBuffer;


    }

   /* @Test
    void loadFromWorksForNodes() throws IOException{
        try (InputStream stream = new FileInputStream("lausanne/nodes.bin")) {
            byte[] nodesBytes = stream.readAllBytes();
            ByteBuffer nodesByteBuffer = ByteBuffer.wrap(nodesBytes);
            int[] nodes = new int[Math2.ceilDiv(nodesByteBuffer.capacity(), 4)];
            int arrayIndex = 0;
            for (int i = 0; i < nodesByteBuffer.capacity(); i += 4) {
                nodes[arrayIndex++] = nodesByteBuffer.getInt(i);
            }
            Path basePath = Path.of("lausanne");
            assertArrayEquals(nodes, Graph.loadFrom2(basePath));
        }
    }
*/
   /* @Test
    void loadFromWorksForAttributes() throws IOException{
        try (InputStream stream = new FileInputStream("lausanne/attributes.bin")) {
            byte[] attributesBytes = stream.readAllBytes();
            ByteBuffer attributesByteBuffer = ByteBuffer.wrap(attributesBytes);
            ArrayList<AttributeSet> attributeSetsList = new ArrayList<>();
            for (int i = 0; i < attributesByteBuffer.capacity(); i += 8) {
                attributeSetsList.add(new AttributeSet(attributesByteBuffer.getLong(i)));
            }
            AttributeSet[] attributeSetsTab = new AttributeSet[attributeSetsList.size()];
            for (int i = 0; i < attributeSetsList.size(); i++) {
                attributeSetsTab[i] = attributeSetsList.get(i);
            }
            Path basePath = Path.of("lausanne");
            assertArrayEquals(attributeSetsTab, Graph.loadFrom3(basePath));
        }

    }*/





    //pour que ces tests passe, il faut simplement enlever le copyOf de la liste d'attribut et mettre = attributList
    //(cérer une faille d'encapsulation qui facilite le test en permettant d'utiliser "null"
    @Test
    void nodeClosetToWorkProperly() {
        ByteBuffer sectorBuffer = ByteBuffer.allocate(6 * 16384);
        //mettre 3 noeud  dans chaque seteur
        int lastNode = 0;
        for (int i = 0; i < 16384; i++) {
            int r = 3;
            sectorBuffer.putInt(lastNode);
            sectorBuffer.putShort((short) r);
            lastNode += r;
        }
        GraphSectors graph = new GraphSectors(sectorBuffer);
        // ajouter les 3 points dans chaque secteurs

        // comparé
        IntBuffer nodeINt3 = IntBuffer.allocate(3 * 3 * 16384);
        for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 128; x++) {
                nodeINt3.put(Q28_4.ofInt((int) ((x * SECTOR_WIDTH) + 1 + SwissBounds.MIN_E)));
                nodeINt3.put(Q28_4.ofInt((int) ((y * SECTOR_HEIGHT) + 1 + SwissBounds.MIN_N)));
                nodeINt3.put(17);
                nodeINt3.put(Q28_4.ofInt((int) ((x * SECTOR_WIDTH) + 30 + SwissBounds.MIN_E)));
                nodeINt3.put(Q28_4.ofInt((int) ((y * SECTOR_HEIGHT) + 30 + SwissBounds.MIN_N)));
                nodeINt3.put(17);
                nodeINt3.put(Q28_4.ofInt((int) ((x * SECTOR_WIDTH) + 1700 + SwissBounds.MIN_E)));
                nodeINt3.put(Q28_4.ofInt((int) ((y * SECTOR_HEIGHT) + 1700 + SwissBounds.MIN_N)));
                nodeINt3.put(17);
            }
        }

        GraphNodes graphNodes = new GraphNodes(nodeINt3);
        PointCh pointChA = new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N + 1000);
        PointCh pointChB = new PointCh(SwissBounds.MIN_E + SECTOR_WIDTH * 1 + 2700, SwissBounds.MIN_N + SECTOR_HEIGHT * 2 + 1700);
        Graph graph1 = new Graph(graphNodes, graph, null, null);

        assertEquals(-1,graph1.nodeClosestTo(pointChA, 60));
        assertEquals(2, graph1.nodeClosestTo(pointChA, 10000));
        assertEquals(-1, graph1.nodeClosestTo(pointChB, 38));
        assertEquals(386 * 3, graph1.nodeClosestTo(pointChB, 39));
        assertEquals(386 * 3, graph1.nodeClosestTo(pointChB,1000 ));
        assertEquals(386 * 3, graph1.nodeClosestTo(pointChB,100000 ));
        assertEquals(386 * 3, graph1.nodeClosestTo(pointChB,1000000000 ));
    }

    @Test
    void edgeProfileIsWorkingOnType0Profile() {

            ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : inversé. Nœud destination : 15.
            edgesBuffer.putInt(0, ~15);
// Longueur : 0x2e.e m (= 47.9375 m)
            edgesBuffer.putShort(4, (short) 0x2e_e);
// Dénivelé : 0x0f.0 m (= 15.0 m)
            edgesBuffer.putShort(6, (short) 0x0f_0);
// Identité de l'ensemble d'attributs OSM : 8239
            edgesBuffer.putShort(8, (short) 2095);

            IntBuffer profileIds = IntBuffer.wrap(new int[]{
                    // Type : 2. Index du premier échantillon : 1.
                    0b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000
            });

            ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                    (short) 0b0,
                    (short) 6156, (short) 0xFEFF,
                    (short) 0xFFFE, (short) 0xF000
            });

            GraphEdges edges =
                    new GraphEdges(edgesBuffer, profileIds, elevations);
            Graph graph = new Graph(null, null, edges, null);
            assertEquals(Functions.constant(Double.NaN), graph.edgeProfile(0));
    }

    @Test
    void edgeProfileIsWorkingOnOtherProfileTypesThan0() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : inversé. Nœud destination : 15.
        edgesBuffer.putInt(0, ~15);
// Longueur : 0x58 m (= 5.5 m)
        edgesBuffer.putShort(4, (short) 0x58);
// Dénivelé : 0x0f.0 m (= 15.0 m)
        edgesBuffer.putShort(6, (short) 0x0f_0);
// Identité de l'ensemble d'attributs OSM : 8239
        edgesBuffer.putShort(8, (short) 8239);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 2. Index du premier échantillon : 1.
                (2 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 6720, (short) 0b0010_0100_0100_1111,
                (short) 0b1010_0100_0100_1111,
                (byte) 0b1010_0100

        });

        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);
        Graph graph = new Graph (null, null, edges, null);
        float[] expectedSamples = new float[]{
                421.4375f, 427.1875f, 422.25f, 420f
        };

        assertEquals(Functions.sampled(expectedSamples, 5.5), graph.edgeProfile(0));
    }
}
