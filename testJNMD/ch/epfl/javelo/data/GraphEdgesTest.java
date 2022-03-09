package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.data.GraphSectors.*;
import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GraphEdgesTest {

    @Test
    void isInvertedWorks() {
        GraphEdges graphEdges = new GraphEdges(ByteBuffer.wrap(new byte[]{
                0,0,0,0,0,0,0,0,0,0,
                (byte) 0b11111111, (byte) 255,(byte) 255, (byte) 255,0,0,0,0,0,0,
                (byte) 127,0,0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,0,0,
                (byte) 128,0,0,0,0,0,0,0,0,0,
                (byte) 129,0,0,0,0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,0,0,0,0,0
        }), IntBuffer.allocate(100), ShortBuffer.allocate(100));
        assertEquals(false, graphEdges.isInverted(0));
        assertEquals(true, graphEdges.isInverted(1));
        assertEquals(false, graphEdges.isInverted(2));
        assertEquals(true, graphEdges.isInverted(4));
        assertEquals(true, graphEdges.isInverted(5));
    }

    @Test
    void targetNodeIdWorks() {
        GraphEdges graphEdges = new GraphEdges(ByteBuffer.wrap(new byte[]{
                0,0,0,0,0,0,0,0,0,0,
                (byte) 0b11111111, (byte) 0b11111111,(byte) 0b11111111, (byte) 0b11111111,0,0,0,0,0,0,
                (byte) 0b01111111,0,0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,0,0,
                (byte) 128,0,0,0,0,0,0,0,0,0,
                (byte) 129,0,0,0,0,0,0,0,0,0,
                (byte) 0b10000000, (byte) 0b0110_1001,(byte) 0b1101_1011,(byte) 0b1000_0111,0,0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,0,0,0,0,0
        }), IntBuffer.allocate(100), ShortBuffer.allocate(100));
        assertEquals(0b0111_1111_1111_1111_1111_1111_1111_1111,graphEdges.targetNodeId(1));
        assertEquals(0b111_1111_1111_1111_1111_1111_1111_1111,graphEdges.targetNodeId(1));
        assertEquals(0b0111_1111_0000_0000_0000_0000_0000_0000, graphEdges.targetNodeId(2));
        assertEquals(0b111_1111_0000_0000_0000_0000_0000_0000, graphEdges.targetNodeId(2));
        assertEquals(0b000_0000_0110_1001_1101_1011_1000_0111, graphEdges.targetNodeId(6));
        assertEquals(0b0000_0110_1001_1101_1011_1000_0111, graphEdges.targetNodeId(6));
        assertEquals(0b0110_1001_1101_1011_1000_0111, graphEdges.targetNodeId(6));
        assertEquals(0b110_1001_1101_1011_1000_0111, graphEdges.targetNodeId(6));
    }

    @Test
    void targetNodeIdWork(){}

    @Test
    void lengthWork() {}

    @Test
    void elevationGainWork() {}

    @Test
    void hasProfileWorks() {
        GraphEdges graphEdges = new GraphEdges(ByteBuffer.allocate(0),
                IntBuffer.wrap(new int[]{
                        0b0011_1111_1111_1111_1111_1111_1111_1111,
                        0b0111_1111_1111_1111_1111_1111_1111_1111,
                        0b1011_1111_1111_1111_1111_1111_1111_1111,
                        0b1111_1111_1111_1111_1111_1111_1111_1111,

                }), ShortBuffer.allocate(100));
        assertEquals(false,graphEdges.hasProfile(0));
        assertEquals(true, graphEdges.hasProfile(1));
        assertEquals(true, graphEdges.hasProfile(2));
        assertEquals(true, graphEdges.hasProfile(3));
    }



    @Test
    void testDuProf() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
// Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
// Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertTrue(edges.isInverted(0));
        assertEquals(12, edges.targetNodeId(0));
        assertEquals(16.6875, edges.length(0));
        assertEquals(16.0, edges.elevationGain(0));
        assertEquals(2022, edges.attributesIndex(0));
        float[] expectedSamples = new float[]{
                384.0625f, 384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };
        assertArrayEquals(expectedSamples, edges.profileSamples(0));
    }
}
