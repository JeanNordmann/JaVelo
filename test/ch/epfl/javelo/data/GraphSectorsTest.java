package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.TestManager;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphSectorsTest {

    // E middle : (2834000 - 2485000)/2 = 174_500
    // N middle : (1296000 - 1075000)/2 = 110_500


    // Functions to test : sectorsInArea()

    static byte[] sectorByteArrayConstructor(int firstNodeId, int numberOfNodes) {
        byte[] returnArr = new byte[6];

        for (int i = 0; i < 4; i++) {
            returnArr[3 - i] = (byte) ((firstNodeId >> 8*i) & 0xFF);
        }

        for (int i = 0; i < 2; i++) {
            returnArr[5 - i] = (byte) ((numberOfNodes >> 8*i) & 0xFF);
        }

        return returnArr;
    }

    class GraphSectorGenerator {
        public static final int SECTOR_GRID_SIDE = 128;
        public static final int TOTAL_SECTORS = SECTOR_GRID_SIDE * SECTOR_GRID_SIDE;

        private final ByteBuffer sectorsBuffer;
        private final GraphSectors.Sector[] allSectors;
        private final GraphSectors gs;

        public GraphSectorGenerator() {
            sectorsBuffer = ByteBuffer.allocate(TOTAL_SECTORS * 6);
            allSectors = new GraphSectors.Sector[TOTAL_SECTORS];

            int currentFirstNodeId = 0;

            for (int i = 0; i < TOTAL_SECTORS; i++) {
                // int currentNumberOfNodes = TestManager.generateRandomIntInBounds(0, 0xFFFF);
                int currentNumberOfNodes = 1;
                int currentLastNodeId = currentFirstNodeId + currentNumberOfNodes;

                byte[] generatedSector = sectorByteArrayConstructor(currentFirstNodeId, currentNumberOfNodes);

                sectorsBuffer.put(generatedSector);
                allSectors[i] = new GraphSectors.Sector(currentFirstNodeId, currentLastNodeId);
                currentFirstNodeId = currentLastNodeId + 1;
            }

            gs = new GraphSectors(sectorsBuffer);
        }

        public GraphSectors getGs() {
            return gs;
        }

        public GraphSectors.Sector[] getAllSectors() {
            return allSectors;
        }

        public GraphSectors.Sector getSector(int index) {
            return allSectors[index];
        }

        public List<GraphSectors.Sector> getRequestedSectors(int start, int end) {
            List<GraphSectors.Sector> retList = new ArrayList<GraphSectors.Sector>();

            for (int i = start; i < end; i++) {
                retList.add(allSectors[i]);
            }

            return retList;
        }
    }

    static int getAbsoluteSectorPosition(PointCh p) {
        final double SECTOR_COL_MIN = SwissBounds.MIN_E;
        final double SECTOR_ROW_MIN = SwissBounds.MIN_N;

        // formula : zero-indexed : (row, col) of sector : 128*row + col
        // each sector is 1.73 width and 2.73 height

        // Procedure
        // First find row and col location of the sectors
        // then determine the location of where it should be in terms of the sector

        double pointE = p.e();
        double pointN = p.n();

        // System.out.println(pointE + " - " + pointN);

        int row = (int) Math.floor((pointN - SECTOR_ROW_MIN)/ 1730);
        int col = (int) Math.floor((pointE - SECTOR_COL_MIN) / 2730);

        // System.out.println("row " + row + " col " + col);

        return 128 * row + col;
    }

    // @Test
    void tryAbsoluteSectorPosition() {
        // seems to be working.
        // E middle : (2834000 - 2485000)/2 = 174_500
        // N middle : (1296000 - 1075000)/2 = 110_500

        // 8192

        PointCh p = new PointCh(2485000 + 174500, 1075000 + 110500);
    }

    // @Test
    void graphSectorsGeneratorDoesCreateGrid() {
        GraphSectorGenerator g = new GraphSectorGenerator();
        GraphSectors b = g.getGs();
    }

    // @Test
    void verifyByteArrayConstructor() {
        int firstNodeId = 0xFFFFFFFF;
        short numberOfNodes = 0xFFF;

        byte[] b = sectorByteArrayConstructor(firstNodeId, numberOfNodes);
        ByteBuffer buff = ByteBuffer.allocate(4 + 2);
        buff.putInt(firstNodeId);
        buff.putShort(numberOfNodes);
        byte[] expected = buff.array();

        assertArrayEquals(expected, b);
    }

    @Test
    void graphSectorsWorksOnExtremeValues() {
        GraphSectorGenerator g = new GraphSectorGenerator();
        GraphSectors gs = g.getGs();
        GraphSectors.Sector[] list = g.getAllSectors();
        // Let's get for example a rectangle

        List<GraphSectors.Sector> expected = List.of(list[0]);

        // Now we have graphsectors :
        assertEquals(expected, gs.sectorsInArea(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 4));
        // System.out.println(" buggy : ");
        List<GraphSectors.Sector> expected2 = List.of(list[GraphSectorGenerator.TOTAL_SECTORS - 1]);
        assertEquals(expected2, gs.sectorsInArea(new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 2));

        PointCh topLeft = new PointCh(SwissBounds.MIN_E, SwissBounds.MAX_N);

        List<GraphSectors.Sector> expected3 = List.of(list[getAbsoluteSectorPosition(topLeft)]);
        assertEquals(expected3, gs.sectorsInArea(topLeft, 1));

        PointCh bottomRight = new PointCh(SwissBounds.MAX_E, SwissBounds.MIN_N);

        List<GraphSectors.Sector> expected4 = List.of(list[getAbsoluteSectorPosition(bottomRight)]);
        assertEquals(expected4, gs.sectorsInArea(bottomRight, 1));

    }

    @Test
    void graphSectorsWorksWithRandomValues() {
        final double SECTOR_HEIGHT =  1.73 * 1000 ;
        final double SECTOR_WIDTH = 2.73 * 1000  ;

        GraphSectorGenerator g = new GraphSectorGenerator();
        GraphSectors gs = g.getGs();
        GraphSectors.Sector[] list = g.getAllSectors();

        PointCh center = new PointCh(SwissBounds.MIN_E + 3 * SECTOR_WIDTH + 500, SwissBounds.MIN_N);
        List<GraphSectors.Sector> expected = List.of(list[2], list[3], list[130], list[131]);
        assertEquals(expected, gs.sectorsInArea(center, SECTOR_HEIGHT + 20));
    }


}