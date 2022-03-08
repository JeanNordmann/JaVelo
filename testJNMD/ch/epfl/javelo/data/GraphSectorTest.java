package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.data.GraphSectors.*;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphSectorTest {

    //TODO faire les Test

    @Test
    void sectorsInAreaWorkOnAllSectors() {
        //création d'un Buffer rempli aléatoirement
        ByteBuffer graphSector = ByteBuffer.allocate(98304);
        int a = 0;
        List<Sector> excpectedGS = new ArrayList<Sector>();

        for (int i = 0; i < 16384; i++) {
            graphSector.putInt(a);
            int b = 3;
                    //(int) Math.round(Math.random()*10);
            a +=b;
            graphSector.putShort((short)b);
            excpectedGS.add(new Sector(a - b, a));
        }
        GraphSectors gss = new GraphSectors(graphSector);
        List<Sector> actualSector = gss.sectorsInArea(new PointCh(2500000, 1100000),1000000);
        assertEquals(excpectedGS, actualSector); ;
    }




}
