package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.data.GraphSectors.*;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphSectorTest {

    //TODO faire les Test

    @Test
    void sectorsInAreaWorkOnAllSectors() {
        //création d'un Buffer rempli aléatoirement
        ByteBuffer graphSector = ByteBuffer.allocate(98304);
        int a = 0;
        List<Sector> expectedGS = new ArrayList<Sector>();

        for (int i = 0; i < 16384; i++) {
            graphSector.putInt(a);
            int b = (int) Math.round(Math.random()*10);
            a +=b;
            graphSector.putShort((short)b);
            expectedGS.add(new Sector(a - b, a));
        }
        GraphSectors gss = new GraphSectors(graphSector);
        List<Sector> actualSector = gss.sectorsInArea(new PointCh(2500000, 1100000),1000000);
        assertEquals(expectedGS, actualSector); ;
    }

    @Test
    void sectorsInAreaWorkOnOneSectors() {
        //création d'un Buffer rempli aléatoirement
        ByteBuffer graphSector = ByteBuffer.allocate(98304);
        int a = 0;
        List<Sector> expectedGS = new ArrayList<Sector>();

        for (int i = 0; i < 16384; i++) {
            graphSector.putInt(a);
            int b = (int) Math.round(Math.random()*10);
            a +=b;
            graphSector.putShort((short)b);
            if (i == 1797) expectedGS.add(new Sector(a - b, a));
        }
        GraphSectors gss = new GraphSectors(graphSector);
        List<Sector> actualSector = gss.sectorsInArea(new PointCh(2500000, 1100000),10);
        assertEquals(expectedGS, actualSector); ;
    }

    @Test
    void sectorsInAreaWorkInvalideSectors() {
        //création d'un Buffer rempli aléatoirement
        ByteBuffer graphSector = ByteBuffer.allocate(98304);
        int a = 0;
        List<Sector> expectedGS = new ArrayList<Sector>();

        for (int i = 0; i < 16384; i++) {
            graphSector.putInt(a);
            int b = (int) Math.round(Math.random()*10);
            a +=b;
            graphSector.putShort((short)b);
            if (i == 1798) expectedGS.add(new Sector(a - b, a));
        }
        GraphSectors gss = new GraphSectors(graphSector);
        List<Sector> actualSector = gss.sectorsInArea(new PointCh(2500000, 1100000),10);
        assertEquals(false, (expectedGS.equals(actualSector)));
    }

    @Test
    void sectorsInAreaWorkOnSomeSectors() {
        //création d'un Buffer rempli aléatoirement
        ByteBuffer graphSector = ByteBuffer.allocate(98304);
        int a = 0;
        List<Sector> expectedGS = new ArrayList<Sector>();

        for (int i = 0; i < 16384; i++) {
            graphSector.putInt(a);
            int b = (int) Math.round(Math.random()*10);
            a +=b;
            graphSector.putShort((short)b);
            if (i == 1797) expectedGS.add(new Sector(a - b, a));
        }
        GraphSectors gss = new GraphSectors(graphSector);
        List<Sector> actualSector = gss.sectorsInArea(new PointCh(2500000, 1100000),10);
        assertEquals(expectedGS, actualSector); ;
    }

    @Test
    void sectorsInAreaWorkOnUpperRightHandSectors() {
        //création d'un Buffer rempli aléatoirement
        ByteBuffer graphSector = ByteBuffer.allocate(98304);
        int a = 0;
        List<Sector> expectedGS = new ArrayList<Sector>();

        for (int i = 0; i < 16384; i++) {
            graphSector.putInt(a);
            int b = (int) Math.round(Math.random()*10);
            a +=b;
            graphSector.putShort((short)b);
            switch (i) {
                case 16126, 16127, 16254, 16255, 16382, 16383 :
                    expectedGS.add(new Sector(a - b, a));
                    break;
            }
        }
        GraphSectors gss = new GraphSectors(graphSector);
        List<Sector> actualSector = gss.sectorsInArea(new PointCh(2_832_500, 1_294_200),2730);
        assertEquals(expectedGS, actualSector);
        System.out.println();
    }

    @Test
    void sectorsInAreaWorksWithExtremeSectors(){
        ByteBuffer sectorBuffer = ByteBuffer.allocate(6*16384);
        int lastNode = 0 ;
        for (int i = 0; i < 16384; i++){
            sectorBuffer.putInt(lastNode);
            sectorBuffer.putShort((short)1);
            lastNode++ ;
        }
        GraphSectors graph = new GraphSectors(sectorBuffer);

        List<GraphSectors.Sector> sectorsList1 = new ArrayList<>();
        List<GraphSectors.Sector> sectorsList2 = new ArrayList<>();
        List<GraphSectors.Sector> sectorsList3 = new ArrayList<>();

        sectorsList1.add(new GraphSectors.Sector(0, 1));
        PointCh point1 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);

        sectorsList2.add(new GraphSectors.Sector(16383, 16384));
        PointCh point2 = new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N);

        sectorsList3.add(new GraphSectors.Sector(16254,16255));
        sectorsList3.add(new GraphSectors.Sector(16255,16256));
        sectorsList3.add(new GraphSectors.Sector(16382,16383));
        sectorsList3.add(new GraphSectors.Sector(16383,16384));
        PointCh point3 = new PointCh((SwissBounds.MAX_E), SwissBounds.MAX_N);


        assertEquals(sectorsList1, graph.sectorsInArea(point1, 300));
        assertEquals(sectorsList2, graph.sectorsInArea(point2, 350));
        assertEquals(sectorsList3, graph.sectorsInArea(point3, 2900));
    }
}
