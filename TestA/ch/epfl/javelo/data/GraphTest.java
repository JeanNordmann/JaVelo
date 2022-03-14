package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphTest {

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
    @Test
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

    void loadFromEdgesWorkProperly() throws IOException {
        ByteBuffer byteBuffer;
        IntBuffer intBuffer;
        ShortBuffer shortBuffer;


    }

    @Test
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

    @Test
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

    }
}
