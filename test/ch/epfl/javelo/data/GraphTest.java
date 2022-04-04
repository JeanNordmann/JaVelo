package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.TestManager;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    @Test
    void loadFromWorksOnLausanne(){
        assertDoesNotThrow(() -> {
            Graph g = Graph.loadFrom (Path.of("lausanne" ) );
        });
    }

    // Note : make sure your directory respects the form "test/lausanne-no-sectors"
    @Test
    void loadFromDoesNotWorkWhenFilesAreMissing() {
        String[] cases = {"Nodes", "Edges", "ProfileIds", "Elevations", "Sectors", "AttributeSets"};
        for (String c : cases) {
            assertThrows(IOException.class, () -> {
                Graph g = Graph.loadFrom(Path.of("test", "directoryTests", "lausanneNo" + c));
            });
        }
    }

    @Test
    void loadFromDoesNotWorkOnInexistantFolders() {
        assertThrows(IOException.class, () -> {
            Graph g = Graph.loadFrom(Path.of("berlin"));
        });
    }

    // not a test
    // returns null if the file is unable to be loaded
    public static Graph loadLausanne() {
        try {
            Graph g = Graph.loadFrom(Path.of("lausanne"));
            return g;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    ByteBuffer loadBuffer(String name) {
        Path fp = Path.of("lausanne", name + ".bin");
        try (FileChannel channel = FileChannel.open(fp)) {
            ByteBuffer b =  channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            return b;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Test
    void compareLoadedGraphToActual() {
        // Idea : load buffers directly and use Graph* classes to verify functionality.
        IntBuffer nodeBuffer = loadBuffer("nodes").asIntBuffer();
        ByteBuffer edgesBuffer = loadBuffer("edges");
        IntBuffer profileIdsBuffer = loadBuffer("profile_ids").asIntBuffer();
        ShortBuffer elevationsBuffer = loadBuffer("elevations").asShortBuffer();
        ByteBuffer sectorsBuffer = loadBuffer("sectors");
        LongBuffer attributeSetsBuffer = loadBuffer("attributes").asLongBuffer();

        ArrayList<AttributeSet> atts = new ArrayList<AttributeSet>();

        for (int i = 0; i < attributeSetsBuffer.capacity(); i++) {
            atts.add(new AttributeSet(attributeSetsBuffer.get(i)));
        }

        Graph expected = new Graph(new GraphNodes(nodeBuffer), new GraphSectors(sectorsBuffer), new GraphEdges(edgesBuffer, profileIdsBuffer, elevationsBuffer), atts);
        Graph g = loadLausanne();

        GraphEdges edges = new GraphEdges(edgesBuffer, profileIdsBuffer, elevationsBuffer);

        int randomNumber1 = 9043; // TestManager.generateRandomIntInBounds(0, g.nodeCount() - 1);
        assertEquals(expected.edgeLength(randomNumber1), g.edgeLength(randomNumber1));
        assertEquals(expected.edgeAttributes(randomNumber1), g.edgeAttributes(randomNumber1));
        assertEquals(expected.nodeOutEdgeId(randomNumber1, 0), expected.nodeOutEdgeId(randomNumber1, 0));

        int randomNumber2 = TestManager.generateRandomIntInBounds(0, g.nodeCount() - 1);
        assertEquals(expected.edgeElevationGain(randomNumber2), expected.edgeElevationGain(randomNumber2));
        assertEquals(expected.edgeTargetNodeId(randomNumber2), expected.edgeTargetNodeId(randomNumber2));

        int randomNumber3 = TestManager.generateRandomIntInBounds(0, g.nodeCount());

        while (edges.hasProfile(randomNumber3)) {
            randomNumber3 = TestManager.generateRandomIntInBounds(0, g.nodeCount());
        }

        assertEquals(Functions.constant(Double.NaN).applyAsDouble(1902), g.edgeProfile(randomNumber3).applyAsDouble(23));
    }

    @Test
    void testOnKnownValuesUsingGivenInformation() {
        Graph g = loadLausanne();
        LongBuffer osmIdBuffer = loadBuffer("nodes_osmid").asLongBuffer();
        // System.out.println(osmIdBuffer.get(2022)); // OSM ID is 310876657 - lat : 46.6326106, lon : 6.6013034
        // System.out.println(osmIdBuffer.get(302)); // OSM ID is 1588271776 - 46.6418443, 6.7601820
        // System.out.println(osmIdBuffer.get(100034)); // OSM ID is 2074319107 - lat : 46.5567160, lon : 6.6361730

        for (double[] coords : new double[][]{{Math.toRadians(6.6013034), Math.toRadians(46.6326106), 2022}, {Math.toRadians(6.7601820), Math.toRadians(46.6418443), 302}, {Math.toRadians(6.6361730), Math.toRadians(46.5567160), 100034}}) {
            PointCh expected = new PointCh(Ch1903.e(coords[0], coords[1]), Ch1903.n(coords[0], coords[1]));
            int id = (int) coords[2];
            assertEquals(expected.e(), g.nodePoint(id).e(), 10E-1);
            assertEquals(expected.n(), g.nodePoint(id).n(), 10E-1);
        }

    }

    PointCh constructPointFromLonLat(double lonDegrees, double latDegrees) {
        return new PointCh(Ch1903.e(Math.toRadians(lonDegrees), Math.toRadians(latDegrees)), Ch1903.n(Math.toRadians(lonDegrees), Math.toRadians(latDegrees)));
    }

    void printSomeNodes(Graph g, int offset) {
        for (int i = 0; i < 10; i++) {
            PointCh point = g.nodePoint(i + offset);
            System.out.println((i + offset) + " : " + point.e() + "," + point.n());
        }
    }

    @Test
    void testNodeClosestTo() {
        // tests to try : on self, on another node
        Graph g = loadLausanne();
        int random = TestManager.generateRandomIntInBounds(0, g.nodeCount());
        assertEquals(random,  g.nodeClosestTo(g.nodePoint(random), 0));

        // get 10 nodes and check their distances, then use assertions to check functioning
        // printSomeNodes(g, 804);
        // see https://www.desmos.com/calculator/utjwmkofti
        PointCh toSearchFor = new PointCh(2_541_535, 1_164_978);
        assertEquals(809, g.nodeClosestTo(toSearchFor, 20));

        // check if it also returns -1 for no node found
        assertEquals(-1, g.nodeClosestTo(toSearchFor, 0));
    }
}