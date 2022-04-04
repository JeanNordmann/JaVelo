package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.Q28_4;
import ch.epfl.javelo.TestManager;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GraphEdgesTest {

    // Helpful note : press the "text" icon next to the javadoc to read it properly

    /**
     * <table style="padding: 5px;">
     *                       <tr>
     *                           <th>Attribute</th>
     *                           <th>Bits</th>
     *                           <th>Format</th>
     *                       </tr>
     *                       <tr>
     *                          <td>Edge direction</td>
     *                          <td>1</td>
     *                          <td></td>
     *                       </tr>
     * <tr>
     * <td>Target node ID</td>
     * <td>31</td>
     * <td>U31</td>
     * </tr>
     *
     * <tr>
     * <td>Length (in meters)</td>
     * <td>16</td>
     * <td>UQ12.4</td>
     * </tr>
     *
     * <tr>
     * <td>Positive height difference, in meters</td>
     * <td>4</td>
     * <td>U4</td>
     * </tr>
     *
     * <tr>
     * <td>OSM attribute index number</td>
     * <td>16</td>
     * <td>U16</td>
     * </tr>
     * </table>
     */
    record EdgeConstructor(int targetNodeAndDirection, int length, byte positiveElevation, short attributeNumber) {
        EdgeConstructor {
            // length must be 16 bits max
            Preconditions.checkArgument(length < 0x10000);

            // elevation must be a nibble
            Preconditions.checkArgument(positiveElevation < 0b10000);
        }

        public byte[] getBytes() {
            return new byte[]{};
        }

        public static final int BYTE_SIZE = 9;
    }

    static ByteBuffer edgesBufferConstructor(EdgeConstructor... e) {
        byte[] b = new byte[e.length * EdgeConstructor.BYTE_SIZE];

        for (int i = 0; i < e.length; i++) {
            for (int j = 0; j < EdgeConstructor.BYTE_SIZE; j++) {
                b[j + i * e.length] = e[i].getBytes()[j];
            }
        }

        return ByteBuffer.wrap(b);
    }

    @Test
    void constructorDoesWork() {
        byte[] a = {1, 20, 127, 90, 10};
        ByteBuffer edgeBuffer = ByteBuffer.wrap(a);


    }

    @Test
    void graphEdgesWorksOnSamples() {
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

        assertArrayEquals(expectedSamples, edges.profileSamples(0), TestManager.FLOAT_DELTA);

    }

    private void printArray(float[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.printf("%f ,", arr[i]);
        }
        System.out.println();
    }

    @Test
    void graphEdgesWorksOnKnownValues() {
        // ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations

        ByteBuffer edgesBuffer = ByteBuffer.wrap(new byte[]{
                // EDGE 0
                0, 0, 0, (byte) 1,
                0, (byte) (5 << 4),
                0, (byte) 0b1111_0100,
                0, 0,
                // EDGE 1
                1, 0, 0, 0, // ~
                0, (7 << 4) | 8,
                (byte) 0xF, (byte)0xF0,
                0 , 7 ,
                // EDGE 2
                0 , 0 , 2 , 1 , // Destination 10 0000 0001 = 513
                1 , 8 , // 1 0000 1000 = 16.5 longueur
                (byte) 0B1000_0000 , (byte)0b1000_0100 , // ( 1 << 11 + 8 ).25 denivelé
                (byte) 0B1000_0000 , 1 , // ( 1 << 15 ) + 1 : attributOSm
        });
        edgesBuffer.putInt(10, ~5);
        assert (edgesBuffer.get(10) < 0);
        // type << 30 , first echantillon
        IntBuffer profileIds = IntBuffer.wrap(new int[]{1 << 30, (2 << 30) | 4 , 0 }  );

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{0, (7 << 4) + 8, (7 << 4) + 8, (7 << 4) + 8,
                (( (5 << 4) + 2 )   ) , ( ((3 << 4) + 1) << 8) + (1 << 4) + 8, 0, 0
        });

        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        // TEST 1
        assertEquals(false, edges.isInverted(0));
        assertEquals(1, edges.targetNodeId(0));
        assertEquals(5., edges.length(0), 10e-10);
        assertEquals(15.25, edges.elevationGain(0));
        assertEquals(true, edges.hasProfile(0));
        assertArrayEquals(new float[]{0, 7.5f, 7.5f, 7.5f}, edges.profileSamples(0));
        assertEquals(0, edges.attributesIndex(0));

        // TEST 2
        System.out.println("TEST2");
        assertTrue(edges.isInverted(1));
        assertEquals(5, edges.targetNodeId(1));
        assertEquals(7.5, edges.length(1) , TestManager.DOUBLE_DELTA);
        assertEquals((double)(0xFF), edges.elevationGain(1));
        assertTrue(edges.hasProfile(1));
//        printArray(edges.profileSamples(1));
        assertArrayEquals(new float[]{  9.6875f, 9.6875f, 9.6875f,8.1875f,5.125f}, edges.profileSamples(1));
        assertEquals(7, edges.attributesIndex(1));

        // TEST3
        System.out.println("TEST3 ");
        assertFalse(edges.isInverted(2));
        assertEquals( 513 , edges.targetNodeId(2));
        assertEquals( 16.5 , edges.length(2));
        assertEquals( ( (1 << 11) + 8 ) + .25 , edges.elevationGain(2));
        assertFalse(edges.hasProfile(2));
        assertArrayEquals(new float[]{} ,  edges.profileSamples(2)  , TestManager.FLOAT_DELTA );
        assertEquals(( 1 << 15 ) + 1 , edges.attributesIndex(2));

    }


}