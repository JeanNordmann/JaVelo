package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;


public class MultiRouteTest {

    @Test
    void lengthMethodIsWorkingProperly() {
        Edge edge1 = new Edge(0, 1, new PointCh(2500000, 1200000), new PointCh(2501000, 1200000), 1000, null);
        Edge edge2 = new Edge(1, 2, new PointCh(2501000, 1200000), new PointCh(2502000, 1200000), 1000, null);
        Edge edge3 = new Edge(2, 3, new PointCh(2502000, 1200000), new PointCh(2503000, 1200000), 1000, null);
        Edge edge4 = new Edge(3, 4, new PointCh(2503000, 1200000), new PointCh(2504000, 1200000), 1000, null);
        Edge edge5 = new Edge(4, 5, new PointCh(2504000, 1200000), new PointCh(2505000, 1200000), 1000, null);
        Edge edge6 = new Edge(5, 6, new PointCh(2505000, 1200000), new PointCh(2506000, 1200000), 1000, null);
        List<Edge> edgeList1 = new ArrayList<>();
        edgeList1.add(edge1);
        edgeList1.add(edge2);
        edgeList1.add(edge3);
        List<Edge> edgeList2 = new ArrayList<>();
        edgeList2.add(edge4);
        edgeList2.add(edge5);
        edgeList2.add(edge6);

        SingleRoute singleRoute1 = new SingleRoute(edgeList1);
        SingleRoute singleRoute2 = new SingleRoute(edgeList2);
        List<Route> singleRouteList1 = new ArrayList<>();
        List<Route> singleRouteList2 = new ArrayList<>();
        singleRouteList1.add(singleRoute1);
        singleRouteList1.add(singleRoute2);
        singleRouteList2.add(singleRoute1);
        singleRouteList2.add(singleRoute2);

        MultiRoute multiRoute1 = new MultiRoute(singleRouteList1);
        MultiRoute multiRoute2 = new MultiRoute(singleRouteList2);
        List<Route> routeList = new ArrayList<>();
        routeList.add(multiRoute1);
        routeList.add(multiRoute2);
        MultiRoute multiRoute = new MultiRoute(routeList);

        assertEquals(12000, multiRoute.length());
    }
}
