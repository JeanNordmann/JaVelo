package ch.epfl.javelo.data;

import ch.epfl.javelo.Q28_4;
import ch.epfl.javelo.TestManager;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.*;

class GraphNodesTest {

    // Ad-hoc function to combine number of exiting edges with the first edge of a node.
    int createExitingEdgeInt(int numberOfExitingEdges, int firstEdgeId) {
        return numberOfExitingEdges << 28 | firstEdgeId;
    }

    @Test
    void graphNodesWorksOnProvidedTests(){

        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(1, ns.count());
        assertEquals(2_600_000, ns.nodeE(0));
        assertEquals(1_200_000, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));
    }


    @Test
    void graphNodesWorksOnSamplesTests(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                (2_700_000 << 4) + 4 ,
                (1_295_000 << 4) + 8 ,
                0x3_000_1334 ,

                2_800_000 << 4  ,
                1_295_001 << 4 ,
                0x2_000_0010 ,

        });

        GraphNodes ns = new GraphNodes(b);
        assertEquals(2, ns.count());
        assertEquals(2_700_000.25, ns.nodeE(0));
        assertEquals(1_295_000.5, ns.nodeN(0));


        assertEquals(2_800_000., ns.nodeE(1));
        assertEquals(1_295_001., ns.nodeN(1));


        assertEquals(3, ns.outDegree(0));
        assertEquals(2, ns.outDegree(1));


        assertEquals(0x1334, ns.edgeId(0, 0));
        assertEquals( 0x1335 , ns.edgeId(0, 1 ) );
        assertEquals(0x1336 , ns.edgeId(0,2) )  ;

        assertEquals(16 , ns.edgeId(1, 0 ));
        assertEquals(17 , ns.edgeId(1, 1 ));


    }

    @Test
    void graphNodesWorksOnArbitraryValues() {
        int e = TestManager.generateRandomEAsQ28_4();
        int n = TestManager.generateRandomNAsQ28_4();
        int firstEdge = 0x11;

        PointCh check = new PointCh(Q28_4.asDouble(e), Q28_4.asDouble(n));

        IntBuffer b = IntBuffer.wrap(new int[] {
                e, n, (10 << 28) | firstEdge
        });

        GraphNodes gn = new GraphNodes(b);

        assertEquals(check.e(), gn.nodeE(0), TestManager.DOUBLE_DELTA);
        assertEquals(check.n(), gn.nodeN(0), TestManager.DOUBLE_DELTA);
        assertEquals(10, gn.outDegree(0));
        assertEquals(firstEdge, gn.edgeId(0, 0));
        assertEquals(firstEdge + 1, gn.edgeId(0, 1));
    }

    @Test
    void graphNodesWorksOnSketchyValues() {
        // See what happens when we use a very large exiting node value
        IntBuffer b = IntBuffer.wrap(new int[]{
                TestManager.generateRandomEAsQ28_4(),
                TestManager.generateRandomNAsQ28_4(),
                createExitingEdgeInt(0xE, 9),
        });

        GraphNodes gn = new GraphNodes(b);

        assertEquals(0xE, gn.outDegree(0));
    }

    //  Random tests

    class RandomNode {
        private final int e; // stored as Q28_4
        private final int n; // stored as Q28_4
        private final int numberOfExitingEdges;
        private final int firstExitingEdge;

        public final static int INT_ARRAY_OCCUPANCY = 3;

        public RandomNode() {
            // generate random values within Swiss bounds
            this.e = TestManager.generateRandomIntInBounds(Q28_4.ofInt((int) SwissBounds.MIN_E), Q28_4.ofInt((int) SwissBounds.MAX_E));
            this.n = TestManager.generateRandomIntInBounds(Q28_4.ofInt((int) SwissBounds.MIN_N), Q28_4.ofInt((int) SwissBounds.MAX_N));
            this.numberOfExitingEdges = TestManager.generateRandomIntInBounds(0, 0xF);
            this.firstExitingEdge = TestManager.generateRandomIntInBounds(0,0xFFF);

        }

        public int[] asIntArray() {
            // combine last two:
            int last = numberOfExitingEdges << 28 | firstExitingEdge;
            // System.out.println(last);
            return new int[] {e, n, last};
        }

        public int expectedOutDegree() {
            return numberOfExitingEdges;
        }

        public double expectedNodeE() {
            return Q28_4.asDouble(e);
        }

        public double expectedNodeN() {
            return Q28_4.asDouble(n);
        }

        public int expectedEdgeId(int edgeIndex) {
            return firstExitingEdge + edgeIndex;
        }
    }

    class RandomGraphNodes {
        // modify for more…
        private static final int NUMBER_OF_NODES = 100;

        private RandomNode[] nodes = new RandomNode[NUMBER_OF_NODES];
        private GraphNodes graphNodes;

        public RandomGraphNodes() {
            // generate random nodes
            for (int i = 0; i < NUMBER_OF_NODES; i++) {
                nodes[i] = new RandomNode();
            }

            graphNodes = new GraphNodes(randomGraphNodesConstructor());
        }

        private IntBuffer randomGraphNodesConstructor() {
            int[] nodeArray = new int[NUMBER_OF_NODES * RandomNode.INT_ARRAY_OCCUPANCY];

            for (int i = 0; i < NUMBER_OF_NODES; i++) {
                // get current node
                int[] currentNodeInts = nodes[i].asIntArray();
                for (int j = 0; j < RandomNode.INT_ARRAY_OCCUPANCY; j++) {
                    nodeArray[i * RandomNode.INT_ARRAY_OCCUPANCY + j] = currentNodeInts[j];
                }
            }

            return IntBuffer.wrap(nodeArray);
        }

        // intrusive getter, use with caution.
        public GraphNodes getGraphNodes() {
            return graphNodes;
        }

        // intrusive getter, use with caution.
        public RandomNode[] getNodes() {
            return nodes;
        }
    }

    @Test
    void graphNodesWorksOnCompletelyRandomValues() {
        RandomGraphNodes a = new RandomGraphNodes();
        GraphNodes gn = a.getGraphNodes();
        RandomNode[] expected = a.getNodes();

        assertEquals(expected.length, gn.count());

        for (int i = 0; i < expected.length; i++) {
            // tests to run : e, n, number of exiting nodes, first exiting edge
            RandomNode currentNode = expected[i];
            assertEquals(currentNode.expectedNodeE(), gn.nodeE(i), TestManager.DOUBLE_DELTA);
            assertEquals(currentNode.expectedNodeN(), gn.nodeN(i), TestManager.DOUBLE_DELTA);

            if (currentNode.expectedOutDegree() > 0) {
                int exitingEdgeIndex = TestManager.generateRandomIntInBounds(0, currentNode.expectedOutDegree() - 1);
                // System.out.println("Edge index " + exitingEdgeIndex + ", expected out degree " + currentNode.expectedOutDegree());
                assertEquals(currentNode.expectedEdgeId(exitingEdgeIndex), gn.edgeId(i, exitingEdgeIndex));
            }

            assertEquals(currentNode.expectedOutDegree(), gn.outDegree(i));
        }
    }
}