package ch.epfl.javelo.data;

import ch.epfl.javelo.Q28_4;
import org.junit.jupiter.api.Test;

import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.*;

class GraphNodesTest {
    @Test
    void graphNodesMethodsWorkProperly (){
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(1, ns.count());
        assertEquals(2_600_000, ns.nodeE(0));
        assertEquals(1_200_000, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));
        IntBuffer c = IntBuffer.wrap(new int[]{
                Q28_4.ofInt(2_700_000),
                Q28_4.ofInt(1_300_000),
                0x4_233_1225,
                Q28_4.ofInt(2_800_000),
                Q28_4.ofInt(1_400_000),
                0x1_000_1456
        });
        GraphNodes ns2 = new GraphNodes(c);
        assertEquals(2,ns2.count());
        assertEquals(2_800_000, ns2.nodeE(1));
        assertEquals(1_300_000, ns2.nodeN(0));
        assertEquals(4, ns2.outDegree(0));
        assertEquals(0x233_1227, ns2.edgeId(0, 2));
        assertEquals(0x1456, ns2.edgeId(1, 0));
    }

    @Test
    void edgeIdThrowsOnInvalidEdgeIndex(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertThrows(AssertionError.class, () -> {
            ns.edgeId(0, 2);
        });
    }

}