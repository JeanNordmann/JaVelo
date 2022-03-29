package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphTest {

    private static final int SUBDIVISIONS_PER_SIDE = 128;
    private static final int SECTORS_COUNT = SUBDIVISIONS_PER_SIDE * SUBDIVISIONS_PER_SIDE;

    private static final ByteBuffer SECTORS_BUFFER = createSectorsBuffer();

    private static ByteBuffer createSectorsBuffer() {
        ByteBuffer sectorsBuffer = ByteBuffer.allocate(SECTORS_COUNT * (Integer.BYTES + Short.BYTES));
        for (int i = 0; i < SECTORS_COUNT; i += 1) {
            sectorsBuffer.putInt(i);
            sectorsBuffer.putShort((short) 1);
        }
        assert !sectorsBuffer.hasRemaining();
        return sectorsBuffer.rewind().asReadOnlyBuffer();
    }

    @Test
    void graphLoadFromWorksOnLausanneData() throws IOException {
        var graph = Graph.loadFrom(Path.of("lausanne"));

        // Check that nodes.bin was properly loaded
        var actual1 = graph.nodeCount();
        var expected1 = 212679;
        assertEquals(expected1, actual1);

        var actual2 = graph.nodeOutEdgeId(2022, 0);
        var expected2 = 4095;
        assertEquals(expected2, actual2);

        // Check that edges.bin was properly loaded
        var actual3 = graph.edgeLength(2022);
        var expected3 = 17.875;
        assertEquals(expected3, actual3);

        // Check that profile_ids.bin and elevations.bin was properly loaded
        var actual4 = graph.edgeProfile(2022).applyAsDouble(0);
        var expected4 = 625.5625;
        assertEquals(expected4, actual4);

        // Check that attributes.bin and elevations.bin was properly loaded
        var actual5 = graph.edgeAttributes(2022).bits();
        var expected5 = 16;
        assertEquals(expected5, actual5);
    }

    @Test
    void graphNodeCountWorksFrom0To99() {
        var edgesCount = 10;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);
        var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
        var attributeSets = List.<AttributeSet>of();

        for (int count = 0; count < 100; count += 1) {
            var buffer = IntBuffer.allocate(3 * count);
            var graphNodes = new GraphNodes(buffer);

            var graph = new Graph(graphNodes, graphSectors, graphEdges, attributeSets);
            assertEquals(count, graph.nodeCount());
        }
    }

    @Test
    void graphNodePointWorksOnRandomValues() {
        var edgesCount = 10;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);
        var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
        var attributeSets = List.<AttributeSet>of();

        var nodesCount = 10_000;
        var buffer = IntBuffer.allocate(3 * nodesCount);
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var e = 2_600_000 + 50_000 * rng.nextDouble();
            var n = 1_200_000 + 50_000 * rng.nextDouble();
            var e_q28_4 = (int) Math.scalb(e, 4);
            var n_q28_4 = (int) Math.scalb(n, 4);
            e = Math.scalb((double) e_q28_4, -4);
            n = Math.scalb((double) n_q28_4, -4);
            var nodeId = rng.nextInt(nodesCount);
            buffer.put(3 * nodeId, e_q28_4);
            buffer.put(3 * nodeId + 1, n_q28_4);
            var graphNodes = new GraphNodes(buffer);

            var graph = new Graph(graphNodes, graphSectors, graphEdges, attributeSets);
            assertEquals(new PointCh(e, n), graph.nodePoint(nodeId));
        }
    }

    @Test
    void graphNodeOutDegreeWorksOnRandomValues() {
        var edgesCount = 10;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);
        var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
        var attributeSets = List.<AttributeSet>of();

        var nodesCount = 10_000;
        var buffer = IntBuffer.allocate(3 * nodesCount);
        var rng = newRandom();
        for (int outDegree = 0; outDegree < 16; outDegree += 1) {
            var firstEdgeId = rng.nextInt(1 << 28);
            var nodeId = rng.nextInt(nodesCount);
            buffer.put(3 * nodeId + 2, (outDegree << 28) | firstEdgeId);
            var graphNodes = new GraphNodes(buffer);
            var graph = new Graph(graphNodes, graphSectors, graphEdges, attributeSets);
            assertEquals(outDegree, graph.nodeOutDegree(nodeId));
        }
    }

    @Test
    void graphNodeOutEdgeIdWorksOnRandomValues() {
        var edgesCount = 10;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);
        var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
        var attributeSets = List.<AttributeSet>of();

        var nodesCount = 10_000;
        var buffer = IntBuffer.allocate(3 * nodesCount);
        var rng = newRandom();
        for (int outDegree = 0; outDegree < 16; outDegree += 1) {
            var firstEdgeId = rng.nextInt(1 << 28);
            var nodeId = rng.nextInt(nodesCount);
            buffer.put(3 * nodeId + 2, (outDegree << 28) | firstEdgeId);
            var graphNodes = new GraphNodes(buffer);
            var graph = new Graph(graphNodes, graphSectors, graphEdges, attributeSets);

            for (int i = 0; i < outDegree; i += 1)
                assertEquals(firstEdgeId + i, graph.nodeOutEdgeId(nodeId, i));
        }
    }

    @Test
    void graphNodeClosestToWorksOnLausanneData() throws IOException {
        var graph = Graph.loadFrom(Path.of("lausanne"));

        var actual1 = graph.nodeClosestTo(new PointCh(2_532_734.8, 1_152_348.0), 100);
        var expected1 = 159049;
        assertEquals(expected1, actual1);

        var actual2 = graph.nodeClosestTo(new PointCh(2_538_619.9, 1_154_088.0), 100);
        var expected2 = 117402;
        assertEquals(expected2, actual2);

        var actual3 = graph.nodeClosestTo(new PointCh(2_600_000, 1_200_000), 100);
        var expected3 = -1;
        assertEquals(expected3, actual3);
    }

    @Test
    void graphEdgeTargetNodeIdWorksOnRandomValues() {
        var nodesCount = 10;
        var nodesBuffer = IntBuffer.allocate(3 * nodesCount);
        var graphNodes = new GraphNodes(nodesBuffer);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);
        var attributeSets = List.<AttributeSet>of();

        var edgesCount = 10_000;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var targetNodeId = rng.nextInt();
            var edgeId = rng.nextInt(edgesCount);
            edgesBuffer.putInt(10 * edgeId, targetNodeId);
            var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
            var graph = new Graph(graphNodes, graphSectors, graphEdges, attributeSets);
            var expectedTargetNodeId = targetNodeId < 0 ? ~targetNodeId : targetNodeId;
            assertEquals(expectedTargetNodeId, graph.edgeTargetNodeId(edgeId));
        }
    }

    @Test
    void graphEdgeIsInvertedWorksForPlusMinus100() {
        var nodesCount = 10;
        var nodesBuffer = IntBuffer.allocate(3 * nodesCount);
        var graphNodes = new GraphNodes(nodesBuffer);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);
        var attributeSets = List.<AttributeSet>of();

        var edgesCount = 10_000;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var rng = newRandom();
        for (int targetNodeId = -100; targetNodeId < 100; targetNodeId += 1) {
            var edgeId = rng.nextInt(edgesCount);
            edgesBuffer.putInt(10 * edgeId, targetNodeId);
            var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
            var graph = new Graph(graphNodes, graphSectors, graphEdges, attributeSets);
            assertEquals(targetNodeId < 0, graph.edgeIsInverted(edgeId));
        }
    }

    @Test
    void graphEdgeAttributesWorksOnRandomValues() {
        var nodesCount = 10;
        var nodesBuffer = IntBuffer.allocate(3 * nodesCount);
        var graphNodes = new GraphNodes(nodesBuffer);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);

        var attributeSetsCount = 3 * RANDOM_ITERATIONS;
        var rng = newRandom();
        var attributeSets = new ArrayList<AttributeSet>(attributeSetsCount);
        for (int i = 0; i < attributeSetsCount; i += 1) {
            var attributeSetBits = rng.nextLong(1L << 62);
            attributeSets.add(new AttributeSet(attributeSetBits));
        }
        var unmodifiableAttributeSets = Collections.unmodifiableList(attributeSets);

        var edgesCount = 10_000;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var edgeId = rng.nextInt(edgesCount);
            var attributeSetIndex = (short) rng.nextInt(attributeSetsCount);
            edgesBuffer.putShort(10 * edgeId + 8, attributeSetIndex);
            var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
            var graph = new Graph(graphNodes, graphSectors, graphEdges, unmodifiableAttributeSets);
            assertEquals(
                    unmodifiableAttributeSets.get(attributeSetIndex),
                    graph.edgeAttributes(edgeId));
        }
    }

    @Test
    void graphConstructorCopiesAttributesListToEnsureImmutability() {
        var nodesCount = 10;
        var nodesBuffer = IntBuffer.allocate(3 * nodesCount);
        var graphNodes = new GraphNodes(nodesBuffer);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);

        var attributeSet = new AttributeSet(0b1111L);
        var attributeSets = new ArrayList<>(List.of(attributeSet));
        var unmodifiableAttributeSets = Collections.unmodifiableList(attributeSets);

        var edgesCount = 1;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        edgesBuffer.putShort(8, (short) 0);
        var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
        var graph = new Graph(graphNodes, graphSectors, graphEdges, unmodifiableAttributeSets);
        attributeSets.set(0, new AttributeSet(0L));
        assertEquals(attributeSet, graph.edgeAttributes(0));
    }

    @Test
    void graphEdgeLengthWorksOnRandomValues() {
        var nodesCount = 10;
        var nodesBuffer = IntBuffer.allocate(3 * nodesCount);
        var graphNodes = new GraphNodes(nodesBuffer);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);

        var edgesCount = 10_000;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var edgeId = rng.nextInt(edgesCount);
            var length = rng.nextDouble(1 << 12);
            var length_q12_4 = (int) Math.scalb(length, 4);
            length = Math.scalb((double) length_q12_4, -4);
            edgesBuffer.putShort(10 * edgeId + 4, (short) length_q12_4);
            var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
            var graph = new Graph(graphNodes, graphSectors, graphEdges, List.of());

            assertEquals(length, graph.edgeLength(edgeId));
        }
    }

    @Test
    void graphEdgeElevationGainWorksOnRandomValues() {
        var nodesCount = 10;
        var nodesBuffer = IntBuffer.allocate(3 * nodesCount);
        var graphNodes = new GraphNodes(nodesBuffer);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);

        var edgesCount = 10_000;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var edgeId = rng.nextInt(edgesCount);
            var elevationGain = rng.nextDouble(1 << 12);
            var elevationGain_q12_4 = (int) Math.scalb(elevationGain, 4);
            elevationGain = Math.scalb((double) elevationGain_q12_4, -4);
            edgesBuffer.putShort(10 * edgeId + 6, (short) elevationGain_q12_4);
            var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
            var graph = new Graph(graphNodes, graphSectors, graphEdges, List.of());

            assertEquals(elevationGain, graph.edgeElevationGain(edgeId));
        }
    }

    @Test
    void graphEdgeProfileWorksForType0() {
        var nodesCount = 10;
        var nodesBuffer = IntBuffer.allocate(3 * nodesCount);
        var graphNodes = new GraphNodes(nodesBuffer);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);

        var edgesCount = 10_000;
        var elevationsCount = 25_000;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(elevationsCount);
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var edgeId = rng.nextInt(edgesCount);
            var firstSampleIndex = rng.nextInt(elevationsCount);
            profileIds.put(edgeId, firstSampleIndex);
            var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
            var graph = new Graph(graphNodes, graphSectors, graphEdges, List.of());
            var edgeProfile = graph.edgeProfile(edgeId);
            assertTrue(Double.isNaN(edgeProfile.applyAsDouble(-1)));
            assertTrue(Double.isNaN(edgeProfile.applyAsDouble(0)));
            assertTrue(Double.isNaN(edgeProfile.applyAsDouble(1000)));
        }
    }

    @Test
    void graphEdgeProfileWorksForType1() {
        var nodesCount = 10;
        var nodesBuffer = IntBuffer.allocate(3 * nodesCount);
        var graphNodes = new GraphNodes(nodesBuffer);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);

        var elevationsCount = 500;
        var edgesBuffer = ByteBuffer.allocate(10);
        var profileIds = IntBuffer.allocate(1);
        var elevations = ShortBuffer.allocate(elevationsCount);
        var rng = newRandom();
        for (int i = 0; i < elevationsCount; i += 1)
            elevations.put(i, (short) rng.nextInt(1 << 16));
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var inverted = rng.nextBoolean();
            var sampleCount = rng.nextInt(2, 100);
            var firstSampleIndex = rng.nextInt(elevationsCount - sampleCount);
            var edgeLength_q28_4 = (2 * (sampleCount - 1)) << 4;
            edgesBuffer.putInt(0, inverted ? ~0 : 0);
            edgesBuffer.putShort(4, (short) edgeLength_q28_4);
            profileIds.put(0, (1 << 30) | firstSampleIndex);
            var graphEdges = new GraphEdges(edgesBuffer.asReadOnlyBuffer(), profileIds.asReadOnlyBuffer(), elevations.asReadOnlyBuffer());
            var graph = new Graph(graphNodes, graphSectors, graphEdges, List.of());
            var edgeProfile = graph.edgeProfile(0);

            for (int j = 0; j < sampleCount; j += 1) {
                var elevation = Math.scalb(Short.toUnsignedInt(elevations.get(firstSampleIndex + j)), -4);
                if (inverted) {
                    var x = (sampleCount - 1 - j) * Math.scalb((double)edgeLength_q28_4, -4) / (sampleCount - 1);
                    assertEquals(elevation, edgeProfile.applyAsDouble(x), 1e-7);
                }
                else {
                    var x = j * Math.scalb((double)edgeLength_q28_4, -4) / (sampleCount - 1);
                    assertEquals(elevation, edgeProfile.applyAsDouble(x), 1e-7);
                }
            }
        }
    }
}
