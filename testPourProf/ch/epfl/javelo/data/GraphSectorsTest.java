package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphSectorsTest {
    private static final double SWISS_MIN_E = 2_485_000;
    private static final double SWISS_MIN_N = 1_075_000;
    private static final double SWISS_WIDTH = 349_000;
    private static final double SWISS_HEIGHT = 221_000;

    private static final int SUBDIVISIONS_PER_SIDE = 128;
    private static final int SECTORS_COUNT = SUBDIVISIONS_PER_SIDE * SUBDIVISIONS_PER_SIDE;
    private static final double SECTOR_WIDTH = SWISS_WIDTH / SUBDIVISIONS_PER_SIDE;
    private static final double SECTOR_HEIGHT = SWISS_HEIGHT / SUBDIVISIONS_PER_SIDE;

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
    void graphSectorsSectorsInAreaWorksForSingleSector() {
        var graphSectors = new GraphSectors(SECTORS_BUFFER);
        for (int i = 0; i < SECTORS_COUNT; i += 1) {
            var x = i % SUBDIVISIONS_PER_SIDE;
            var y = i / SUBDIVISIONS_PER_SIDE;
            var e = SWISS_MIN_E + (x + 0.5) * SECTOR_WIDTH;
            var n = SWISS_MIN_N + (y + 0.5) * SECTOR_HEIGHT;
            var sectors = graphSectors.sectorsInArea(new PointCh(e, n), 0.49 * SECTOR_HEIGHT);
            assertEquals(List.of(new GraphSectors.Sector(i, i + 1)), sectors);
        }
    }

    @Test
    void graphSectorsSectorsInAreaWorksFor4NeighbouringSectors() {
        var graphSectors = new GraphSectors(SECTORS_BUFFER);
        for (int x = 1; x <= SUBDIVISIONS_PER_SIDE - 1; x += 1) {
            for (int y = 1; y <= SUBDIVISIONS_PER_SIDE - 1; y += 1) {
                var e = SWISS_MIN_E + x * SECTOR_WIDTH;
                var n = SWISS_MIN_N + y * SECTOR_HEIGHT;
                var p = new PointCh(e, n);
                var sectors = graphSectors.sectorsInArea(p, SECTOR_HEIGHT / 2.0);
                sectors.sort(Comparator.comparingInt(GraphSectors.Sector::startNodeId));

                var i1 = sectorIndex(x - 1, y - 1);
                var i2 = sectorIndex(x, y - 1);
                var i3 = sectorIndex(x - 1, y);
                var i4 = sectorIndex(x, y);
                var expectedSectors = List.of(
                        new GraphSectors.Sector(i1, i1 + 1),
                        new GraphSectors.Sector(i2, i2 + 1),
                        new GraphSectors.Sector(i3, i3 + 1),
                        new GraphSectors.Sector(i4, i4 + 1));

                assertEquals(expectedSectors, sectors);
            }
        }
    }

    @Test
    void graphSectorsSectorsInAreaWorksFor8NeighbouringSectors() {
        var graphSectors = new GraphSectors(SECTORS_BUFFER);
        for (int x = 1; x <= SUBDIVISIONS_PER_SIDE - 1; x += 1) {
            for (int y = 2; y <= SUBDIVISIONS_PER_SIDE - 2; y += 1) {
                var e = SWISS_MIN_E + x * SECTOR_WIDTH;
                var n = SWISS_MIN_N + y * SECTOR_HEIGHT;
                var p = new PointCh(e, n);
                var sectors = graphSectors.sectorsInArea(p, SECTOR_HEIGHT * 1.1);
                sectors.sort(Comparator.comparingInt(GraphSectors.Sector::startNodeId));

                var i1 = sectorIndex(x - 1, y - 2);
                var i2 = sectorIndex(x, y - 2);
                var i3 = sectorIndex(x - 1, y - 1);
                var i4 = sectorIndex(x, y - 1);
                var i5 = sectorIndex(x - 1, y);
                var i6 = sectorIndex(x, y);
                var i7 = sectorIndex(x - 1, y + 1);
                var i8 = sectorIndex(x, y + 1);
                var expectedSectors = List.of(
                        new GraphSectors.Sector(i1, i1 + 1),
                        new GraphSectors.Sector(i2, i2 + 1),
                        new GraphSectors.Sector(i3, i3 + 1),
                        new GraphSectors.Sector(i4, i4 + 1),
                        new GraphSectors.Sector(i5, i5 + 1),
                        new GraphSectors.Sector(i6, i6 + 1),
                        new GraphSectors.Sector(i7, i7 + 1),
                        new GraphSectors.Sector(i8, i8 + 1));

                assertEquals(expectedSectors, sectors);
            }
        }
    }

    private int sectorIndex(int x, int y) {
        return y * SUBDIVISIONS_PER_SIDE + x;
    }

    @Test
    void graphSectorsSectorsInAreaWorksForSectorsWithLargeNumberOfNodes() {
        ByteBuffer sectorsBuffer = ByteBuffer.allocate(SECTORS_COUNT * (Integer.BYTES + Short.BYTES));
        var maxSectorSize = 0xFFFF;
        for (int i = 0; i < SECTORS_COUNT; i += 1) {
            sectorsBuffer.putInt(i * maxSectorSize);
            sectorsBuffer.putShort((short) maxSectorSize);
        }
        var readOnlySectorsBuffer = sectorsBuffer.rewind().asReadOnlyBuffer();
        var graphSectors = new GraphSectors(readOnlySectorsBuffer);
        var d = 100;
        var e = SWISS_MIN_E + 2 * d;
        var n = SWISS_MIN_N + 2 * d;
        var sectors = graphSectors.sectorsInArea(new PointCh(e, n), d);
        assertEquals(List.of(new GraphSectors.Sector(0, maxSectorSize)), sectors);
    }

    @Disabled
    @Test
    void graphSectorsSectorsInAreaWorksForAllOfThem() {
        var graphSectors = new GraphSectors(SECTORS_BUFFER);
        var e = SWISS_MIN_E + 0.5 * SWISS_WIDTH;
        var n = SWISS_MIN_N + 0.5 * SWISS_HEIGHT;
        var sectors = graphSectors.sectorsInArea(new PointCh(e, n), SWISS_WIDTH);
        assertEquals(SECTORS_COUNT, sectors.size());
        BitSet expectedSectors = new BitSet();
        expectedSectors.set(0, SECTORS_COUNT);
        for (GraphSectors.Sector sector : sectors) {
            assertTrue(expectedSectors.get(sector.startNodeId()));
            expectedSectors.clear(sector.startNodeId());
        }
        assertEquals(0, expectedSectors.cardinality());
    }
}