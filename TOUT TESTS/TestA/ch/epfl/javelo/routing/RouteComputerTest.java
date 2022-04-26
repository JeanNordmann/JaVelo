package ch.epfl.javelo.routing;


import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RouteComputerTest {

    @Test
    void priorityQueuTest() {

        record WeightedNode(int nodeId, float distance, int previousNode)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        PriorityQueue<Integer> p = new PriorityQueue<>();
        p.addAll(List.of(5, 2, 17, 29, 33, 1, 8));
        assertEquals(1, p.remove());
        assertEquals(2, p.remove());
        assertEquals(5, p.remove());
    }
}
