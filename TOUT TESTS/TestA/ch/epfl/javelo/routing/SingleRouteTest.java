package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SingleRouteTest {

    public final static double DELTA = 1E-9;

    @Test
    void lengthIsWorkingProperly() {
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1, null, null, 12.56, null));
        edges.add(new Edge(0, 1, null, null, 1.23, null));
        edges.add(new Edge(0, 1, null, null, 8.394, null));
        edges.add(new Edge(0, 1, null, null, 371, null));
        edges.add(new Edge(0, 1, null, null, 31.23, null));
        edges.add(new Edge(0, 1, null, null, 71, null));
        edges.add(new Edge(0, 1, null, null, 8.123, null));
        edges.add(new Edge(0, 1, null, null, 14.58, null));
        SingleRoute singleRoute = new SingleRoute(edges);
        assertEquals(518.117, singleRoute.length(), DELTA);
    }

    @Test
    void indexOfSegmentAtworkProperlyLOL(){
                List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1, null, null, 12.56, null));
        edges.add(new Edge(0, 1, null, null, 1.23, null));
        edges.add(new Edge(0, 1, null, null, 8.394, null));
        edges.add(new Edge(0, 1, null, null, 371, null));
        edges.add(new Edge(0, 1, null, null, 31.23, null));
        edges.add(new Edge(0, 1, null, null, 71, null));
        edges.add(new Edge(0, 1, null, null, 8.123, null));

        SingleRoute singleRoute = new SingleRoute(edges);

        assertEquals((double) 0, singleRoute.indexOfSegmentAt(0));
        assertEquals((double) 0, singleRoute.indexOfSegmentAt(1231909123));
        assertEquals((double) 0, singleRoute.indexOfSegmentAt(-230123));
    }

    @Test
    void pointsMethodIsWorkingProperly() {

        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1, new PointCh(2500000, 1200000), new PointCh(2500010, 1200010), 12.56, null));
        edges.add(new Edge(0, 1, new PointCh(2500010, 1200010), new PointCh(2499050, 1199900), 1.23, null));
        edges.add(new Edge(0, 1, new PointCh(2499050, 1199900), new PointCh(2490000, 1199000), 8.394, null));


        SingleRoute singleRoute = new SingleRoute(edges);
        List<PointCh> pointChList = new ArrayList<>();
        pointChList.add(new PointCh(2500000, 1200000));
        pointChList.add(new PointCh(2500010, 1200010));
        pointChList.add(new PointCh(2499050, 1199900));
        pointChList.add(new PointCh(2490000, 1199000));

        assertEquals(pointChList, singleRoute.points());

    }

    @Test
    void pointAtWorkProperly(){
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1, new PointCh(2500_000, 1200_000), new PointCh(2485_500, 1075_500), new PointCh(2500_000,1200_000).distanceTo( new PointCh(2485_500, 1075_500)), null));
        edges.add(new Edge(1, 2,  new PointCh(2485_500, 1075_500), new PointCh(2600_000,1075_500), new PointCh(2600_000,1075_500).distanceTo( new PointCh(2485_500, 1075_500)), null));
        edges.add(new Edge(2, 3,  new PointCh(2600_000,1075_500), new PointCh(2830_000, 1290_000), new PointCh(2600_000,1075_500).distanceTo( new PointCh(2830_000, 1290_000)), null));

        SingleRoute singleRoute = new SingleRoute(edges);

        assertEquals(new PointCh(2485_500, 1075_500), singleRoute.pointAt(125341.53341969293));
        assertEquals(new PointCh(2578_707, 1075_500), singleRoute.pointAt(218548.53341969293));
        assertEquals(new PointCh(2491_750, 1075_500), singleRoute.pointAt(131_591.53341969293));


    }

    @Test
    void elevationAtWorksProperly() {
        List<Edge> edges = new ArrayList<>();
        float[] tab1 = {1000, 1020, 1040, 1060, 1080};
        edges.add(new Edge(0, 1, new PointCh(2800000, 1205000), new PointCh(2800040, 1205000), 40, Functions.sampled(tab1, 40)));
        float[] tab2 = {1080, 1075, 1070};
        edges.add(new Edge(1, 2, new PointCh(2800040, 1205000), new PointCh(2800120, 1205000), 80, Functions.sampled(tab2, 80)));
        float[] tab3 = {1070, 1075, 1080, 1085, 1090, 1095, 1100};
        edges.add(new Edge(2, 3, new PointCh(2800120, 1205000), new PointCh(2800150, 1205000), 30, Functions.sampled(tab3, 30)));

        SingleRoute singleRoute = new SingleRoute(edges);


        assertEquals(1040, singleRoute.elevationAt(20));
        assertEquals(1075, singleRoute.elevationAt(80));
        assertEquals(1077.5, singleRoute.elevationAt(60));
        assertEquals(1000, singleRoute.elevationAt(0));
        assertEquals(2, singleRoute.nodeClosestTo(110));
        assertEquals(150, singleRoute.length());
        assertEquals(1090, singleRoute.elevationAt(140));
        assertEquals(1100, singleRoute.elevationAt(160));
        assertEquals(1000, singleRoute.elevationAt(-132));

    }

    @Test
    void nodeClosestToWorkProperly1(){
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1, new PointCh(2500_000, 1200_000), new PointCh(2485_500, 1075_500), new PointCh(2500_000,1200_000).distanceTo( new PointCh(2485_500, 1075_500)), null));
        edges.add(new Edge(1, 2,  new PointCh(2485_500, 1075_500), new PointCh(2600_000,1075_500), new PointCh(2600_000,1075_500).distanceTo( new PointCh(2485_500, 1075_500)), null));
        edges.add(new Edge(2, 3,  new PointCh(2600_000,1075_500), new PointCh(2830_000, 1290_000), new PointCh(2600_000,1075_500).distanceTo( new PointCh(2830_000, 1290_000)), null));

        SingleRoute singleRoute = new SingleRoute(edges);

        assertEquals(1, singleRoute.nodeClosestTo(125341.53341969293));
        assertEquals(1, singleRoute.nodeClosestTo(125297));
        assertEquals(1, singleRoute.nodeClosestTo(156892));
        assertEquals(1, singleRoute.nodeClosestTo(125341.53341969+114500/2));
        assertEquals(2, singleRoute.nodeClosestTo(125341.53341969294+114500/2));
        assertEquals(2, singleRoute.nodeClosestTo(220000));
        assertEquals(2, singleRoute.nodeClosestTo(270000));
        assertEquals(2, singleRoute.nodeClosestTo(310000));
        assertEquals(2, singleRoute.nodeClosestTo(125341.53341969+114500+314500/2));
        assertEquals(3, singleRoute.nodeClosestTo(125341.53341969293+114500+314500/2+0.1));
    }

    @Test
    void nodeClosestToWorkProperly3(){
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1, new PointCh(2485_500, 1075_500), new PointCh(2485_500, 1075_500),  0, null));
        edges.add(new Edge(1, 2,  new PointCh(2485_500, 1075_500), new PointCh(2600_000,1075_500), new PointCh(2600_000,1075_500).distanceTo( new PointCh(2485_500, 1075_500)), null));
        edges.add(new Edge(2, 3,  new PointCh(2600_000,1075_500), new PointCh(2830_000, 1290_000), new PointCh(2600_000,1075_500).distanceTo( new PointCh(2830_000, 1290_000)), null));

        SingleRoute singleRoute = new SingleRoute(edges);


        assertEquals(2, singleRoute.nodeClosestTo(114500+314500/2));
        assertEquals(2, singleRoute.nodeClosestTo(114500+314500/2));
        assertEquals(3, singleRoute.nodeClosestTo(114500+314500/2+0.00000001));
    }


    @Test
    void nodeClosestToWorkProperly2(){
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1,  new PointCh(2485_500, 1075_500), new PointCh(2600_000,1075_500), 0, null));
        edges.add(new Edge(1, 2,  new PointCh(2485_500, 1075_500), new PointCh(2600_000,1075_500), new PointCh(2600_000,1075_500).distanceTo( new PointCh(2485_500, 1075_500)), null));
        edges.add(new Edge(2, 3,  new PointCh(2600_000,1075_500), new PointCh(2830_000, 1290_000), new PointCh(2600_000,1075_500).distanceTo( new PointCh(2830_000, 1290_000)), null));

        SingleRoute singleRoute = new SingleRoute(edges);

        assertEquals(1, singleRoute.nodeClosestTo(114500/2));
        assertEquals(2, singleRoute.nodeClosestTo(0.00000000001+114500/2));
        assertEquals(2, singleRoute.nodeClosestTo(114500+314500/2));
        assertEquals(3, singleRoute.nodeClosestTo(0.0000000001+114500+314500/2));
    }

    @Test
    void pointClosestToWorkProperly(){
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1, new PointCh(2500_000, 1200_000), new PointCh(2485_500, 1075_500), new PointCh(2500_000,1200_000).distanceTo( new PointCh(2485_500, 1075_500)), null));
        edges.add(new Edge(1, 2,  new PointCh(2485_500, 1075_500), new PointCh(2600_000,1075_500), new PointCh(2600_000,1075_500).distanceTo( new PointCh(2485_500, 1075_500)), null));
        edges.add(new Edge(2, 3,  new PointCh(2600_000,1075_500), new PointCh(2830_000, 1290_000), new PointCh(2600_000,1075_500).distanceTo( new PointCh(2830_000, 1290_000)), null));

        SingleRoute singleRoute = new SingleRoute(edges);

        assertEquals(new RoutePoint(new PointCh(2485_500, 1075_500),125341.53341969293,0.0),singleRoute.pointClosestTo(new PointCh(2485_500, 1075_500)));
        assertEquals(new RoutePoint(new PointCh(2485_500, 1075_500),125341.53341969293,Math.sqrt(2*400*400)),singleRoute.pointClosestTo(new PointCh(2485_100, 1075_100)));
        assertEquals(new RoutePoint(new PointCh(2485_500, 1075_500),125341.53341969293,Math.sqrt(2*500*500)),singleRoute.pointClosestTo(new PointCh(2485_000, 1075_000)));
        assertEquals(new RoutePoint(new PointCh(2485_500 +114500/2, 1075_500),125341.53341969293+114500/2,0),singleRoute.pointClosestTo(new PointCh(2485_500 +114500/2, 1075_500)));
        assertEquals(new RoutePoint(new PointCh(2485_500 +114500/2, 1075_500),125341.53341969293+114500/2,500),singleRoute.pointClosestTo(new PointCh(2485_500 +114500/2, 1075_000)));
        assertEquals(new RoutePoint(new PointCh(2485_500 +114500/2+1, 1075_500),125341.53341969293+114500/2 +1,0),singleRoute.pointClosestTo(new PointCh(2485_500 +114500/2+1, 1075_500)));
        assertEquals(new RoutePoint(new PointCh(2485_500 +114500/2+1, 1075_500),125341.53341969293+114500/2 +1,500),singleRoute.pointClosestTo(new PointCh(2485_500 +114500/2+1, 1075_000)));
        assertEquals(new RoutePoint(new PointCh(2485_500 +114500-5000, 1075_500),125341.53341969293+114500-5000,1500),singleRoute.pointClosestTo(new PointCh(2485_500 +114500-5000, 1077_000)));
        assertEquals(new RoutePoint(new PointCh(2485_500 +114500-1000, 1075_500),125341.53341969293+114500-1000,1500),singleRoute.pointClosestTo(new PointCh(2485_500 +114500-1000, 1077_000)));
        assertEquals(new RoutePoint(new PointCh(2485_500 +114500-1000, 1075_500),125341.53341969293+114500-1000,2538),singleRoute.pointClosestTo(new PointCh(2485_500 +114500-1000, 1075_500+2538)));
        assertEquals(new RoutePoint(new PointCh(2600732.08691718, 1076182.7506249351),240842.58270427166,2539.5866454691545),singleRoute.pointClosestTo(new PointCh(2485_500 +114500-1000, 1075_500+2540)));


    }
}
