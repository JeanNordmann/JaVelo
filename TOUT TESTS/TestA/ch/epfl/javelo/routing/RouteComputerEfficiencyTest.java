/*
package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.G<<raph;
import org.junit.jupiter.api.Test;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;

import static ch.epfl.javelo.routing.RouteComputerTest.openKmlWebsite;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RouteComputerEfficiencyTest {

    @Test
    void routeCumputerEfficiency1() throws IOException {
        int nbrTest = 1;
        int totalTime = 0;
        long t0 = System.nanoTime();
        for (int i = 0; i < nbrTest; i++) {
            int start = 159049, end = 117669;

            Graph g = Graph.loadFrom(Path.of("ch_west"));
            CostFunction cf = new CityBikeCF(g);
            RouteComputer rc = new RouteComputer(g, cf);
            Route r = rc.bestRouteBetween(start, end);
            // System.out.println(r.length());
            File f = KmlPrinter.write("javelo-known.kml", r);

            Graph gBETA = Graph.loadFrom(Path.of("ch_west"));
            CostFunction cfBETA = new CityBikeCF(gBETA);
            RouteComputerBETA rcBETA = new RouteComputerBETA(gBETA, cfBETA);
            Route rBETA = rcBETA.bestRouteBetween(start, end);
            // System.out.println(r.length());
            File fBETA = KmlPrinter.write("javelo-knownBETA.kml", rBETA);

            //openKmlWebsite(f);

            BufferedReader expected = new BufferedReader(new FileReader(f));
            BufferedReader actual = new BufferedReader(new FileReader(fBETA));

            String expectedLine, actualLine;
            while ((expectedLine = expected.readLine()) != null && (actualLine = actual.readLine()) != null) {
                if (!expectedLine.contains("<color>")) assertEquals(expectedLine, actualLine);
            }

            expected.close();
            actual.close();

        }
        */
/*System.out.printf("Résultat Final : " + nbrTest + "calculé en %d ms\n",
                (System.nanoTime() - t0) / 1_000_000);
        KmlPrinter.write("javelo.kml", r);*//*

    }
}
*/
