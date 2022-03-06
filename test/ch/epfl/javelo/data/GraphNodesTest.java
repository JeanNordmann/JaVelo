package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphNodesTest {

    @Test
    void Graph1() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_764_547 << 4,
                1_258_478 << 4,
                0x3_000_1823,
                2_764_547 << 4,
                1_258_478 << 4,
                0x3_000_1831
        });

        GraphNodes ns = new GraphNodes(b);
        assertEquals(2, ns.count());
        assertEquals(2_764_547, ns.nodeE(0));
        assertEquals(1_258_478, ns.nodeN(0));
        assertEquals(3, ns.outDegree(0));
        assertEquals(0x1823, ns.edgeId(0, 0));
        assertEquals(0x1824, ns.edgeId(0, 1));
        assertEquals(0x1825, ns.edgeId(0, 2));

        assertEquals(0x1832, ns.edgeId(1, 1));
        assertEquals(0x1833, ns.edgeId(1, 2));
    }

    @Test
    void Graph2() {

        byte a= 0;
        System.out.println((byte)(a+128));
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

    }
}
