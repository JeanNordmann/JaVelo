package ch.epfl.javelo.routing;

import ch.epfl.javelo.KmlPrinter;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.TestManager;
import ch.epfl.javelo.data.Graph;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class RouteComputerTest {

    @Test
    void routeComputerWorksWithKnownTest() throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));

        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        Route r = rc.bestRouteBetween(159049, 117669);
        // System.out.println(r.length());
        File f = KmlPrinter.write("javelo-known.kml", r);

        openKmlWebsite(f);

        BufferedReader expected = new BufferedReader(new FileReader(f));
        BufferedReader actual = new BufferedReader(new FileReader(Path.of("test", "route_expected.kml").toFile()));

        String expectedLine, actualLine;
        while ((expectedLine = expected.readLine()) != null && (actualLine = actual.readLine()) != null) {
            if (!expectedLine.contains("<")) assertEquals(expectedLine, actualLine);
        }

        expected.close();
        actual.close();
    }

    Graph loadCHWest() throws IOException {
       return Graph.loadFrom(Path.of("ch_west"));
    }

    private final class NodePair<T> {
        final T startNode;
        final T endNode;

        private NodePair (T startNode, T endNode) {
            this.endNode = endNode;
            this.startNode = startNode;
        }

        public T getStartNode() {
            return startNode;
        }

        public T getEndNode() {
            return endNode;
        }

        @Override
        public String toString() {
            return "NodePair{" +
                    "startNode=" + startNode +
                    ", endNode=" + endNode +
                    '}';
        }
    }

    // @Test
    NodePair<Integer> findRandomNodes(int startNodeBegin, int startNodeEnd, int endNodeBegin, int endNodeEnd) throws IOException {
        Path filePath = Path.of("ch_west", "nodes_osmid.bin");

        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }

        startNodeBegin = Math2.clamp(0, startNodeBegin, osmIdBuffer.capacity());
        startNodeEnd = Math2.clamp(startNodeBegin, startNodeEnd, osmIdBuffer.capacity());
        endNodeBegin = Math2.clamp(startNodeEnd, endNodeBegin, osmIdBuffer.capacity());
        endNodeEnd = Math2.clamp(endNodeBegin, endNodeEnd, osmIdBuffer.capacity());

        // Random number at start of map
        int javeloStart = TestManager.generateRandomIntInBounds(startNodeBegin, startNodeEnd);
        long osmStart = osmIdBuffer.get(javeloStart);

        // Random number at end of map
        int javeloEnd = TestManager.generateRandomIntInBounds(endNodeBegin, endNodeEnd);
        long osmEnd = osmIdBuffer.get(javeloEnd);

        return new NodePair<Integer>(javeloStart, javeloEnd);

        // System.out.printf("%10s %20s %20s\n%10s %20d %20d\n%10s %20d %20d", "", "Javelo", "OSM", "Start", javeloStart, osmStart, "End", javeloEnd, osmEnd);

    }

    void printOSMNodes(NodePair<Integer> n) throws IOException {
        Path filePath = Path.of("ch_west", "nodes_osmid.bin");

        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }

        long osmStart = osmIdBuffer.get(n.startNode);
        long osmEnd = osmIdBuffer.get(n.endNode);

        System.out.printf("%10s %20s %20s\n%10s %20d %20d\n%10s %20d %20d", "", "Javelo", "OSM", "Start", n.startNode, osmStart, "End", n.endNode, osmEnd);
    }

    @Test
    void createRandomLongRouteInSwitzerland() throws IOException {
        NodePair a = findRandomNodes(0, 20000, 2000000, 3000000);
        Route r = calculateCHWestRoute(a);
        File f = KmlPrinter.write("random/long/random-route.kml", r);
        openKmlWebsite(f);
    }

    @Test
    void bestRouteBetweenDoesThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            NodePair a = new NodePair(0, 0);
            calculateLausanneRoute(a);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            NodePair a = new NodePair(234235, 234235);
            calculateCHWestRoute(a);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            NodePair a = new NodePair(500, 500);
            calculateCHWestRoute(a);
        });
    }

    @Test
    void testNullValues() throws IOException {
        NodePair<Integer> a = new NodePair(14325,193954);
        printOSMNodes(a);
        assertNull(calculateCHWestRoute(a));
    }

    static void openKmlWebsite(File kmlFile) throws IOException {
        Desktop.getDesktop().browse(URI.create("https://map.geo.admin.ch/?bgLayer=ch.swisstopo.pixelkarte-grau"));
        try {
            Desktop.getDesktop().browseFileDirectory(kmlFile);
        } catch (UnsupportedOperationException e) {
            System.out.println("Click here to go to the generated KML file : " + Path.of(kmlFile.getAbsolutePath()).getParent());
        }

        System.out.println("Click here to go to the generated KML file : " + Path.of(kmlFile.getAbsolutePath()).getParent());

    }

    private Route calculateLausanneRoute(NodePair<Integer> n) {
        return calculateLausanneRoute(n.startNode, n.endNode);
    }

    private Route calculateLausanneRoute(int startId, int endId) {
        Graph g = TestManager.loadLausanne();
        return calculateRouteFromGraph(g, startId, endId);
    }

    private Route calculateCHWestRoute(NodePair<Integer> n) throws IOException {
       return calculateCHWestRoute(n.startNode, n.endNode);
    }

    private Route calculateCHWestRoute(int startId, int endId) throws IOException {
       Graph g = loadCHWest();
       return calculateRouteFromGraph(g, startId, endId);
    }

    private Route calculateRouteFromGraph(Graph g, int startId, int endId) {
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);

        return rc.bestRouteBetween(startId, endId);
    }

    @Test
    void routeComputerWorksWithLongSwissRoute() throws IOException {
        // Output of loadCHWest():
        //                          Javelo                  OSM
        //     Start                  214            635110785
        //       End              3714104            407783650

        Route r = calculateCHWestRoute(214, 374104);
        File f = KmlPrinter.write("swissRoute-random.kml", r);


        openKmlWebsite(f);
    }

    @Test
    void testAlgorithmLength() throws IOException {
        Graph g = loadCHWest();
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);

        long t0 = System.nanoTime();
        Route r = rc.bestRouteBetween(2046055, 2694240);
        File f = KmlPrinter.write("lausanneToBoncourt.kml", r);

        System.out.printf("Itinéraire calculé en %d ms\n",
                (System.nanoTime() - t0) / 1_000_000);

        openKmlWebsite(f);
    }
}