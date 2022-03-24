package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;


public class MultiRouteTest {

    @Test
    void lengthMethodIsWorkingProperly() {
        Edge edge1 = new Edge(0, 1, new PointCh(2500000, 1200000), new PointCh(2501000, 1200000), 1000, Functions.constant(100));
        Edge edge2 = new Edge(1, 2, new PointCh(2501000, 1200000), new PointCh(2502000, 1200000), 1000, Functions.constant(100));
        Edge edge3 = new Edge(2, 3, new PointCh(2502000, 1200000), new PointCh(2503000, 1200000), 1000, null);
        Edge edge4 = new Edge(3, 4, new PointCh(2503000, 1200000), new PointCh(2504000, 1200000), 1000, null);
        Edge edge5 = new Edge(4, 5, new PointCh(2504000, 1200000), new PointCh(2505000, 1200000), 1000, null);
        Edge edge6 = new Edge(5, 6, new PointCh(2505000, 1200000), new PointCh(2506000, 1200000), 1000, null);
        List<Edge> edgeList1 = new ArrayList<>();
        edgeList1.add(edge1);
        List<Edge> edgeList2 = new ArrayList<>();
        edgeList2.add(edge2);
        List<Edge> edgeList3 = new ArrayList<>();
        edgeList3.add(edge3);
        List<Edge> edgeList4 = new ArrayList<>();
        edgeList4.add(edge4);
        List<Edge> edgeList5 = new ArrayList<>();
        edgeList5.add(edge5);
        List<Edge> edgeList6 = new ArrayList<>();
        edgeList6.add(edge6);

        SingleRoute singleRoute1 = new SingleRoute(edgeList1);
        SingleRoute singleRoute2 = new SingleRoute(edgeList2);
        SingleRoute singleRoute3 = new SingleRoute(edgeList3);
        SingleRoute singleRoute4 = new SingleRoute(edgeList4);
        SingleRoute singleRoute5 = new SingleRoute(edgeList5);
        SingleRoute singleRoute6 = new SingleRoute(edgeList6);

        List<Route> singleRouteList1 = new ArrayList<>();
        List<Route> singleRouteList2 = new ArrayList<>();
        singleRouteList1.add(singleRoute1);
        singleRouteList1.add(singleRoute2);
        singleRouteList1.add(singleRoute3);
        singleRouteList2.add(singleRoute4);
        singleRouteList2.add(singleRoute5);
        singleRouteList2.add(singleRoute6);

        MultiRoute multiRoute1 = new MultiRoute(singleRouteList1);
        MultiRoute multiRoute2 = new MultiRoute(singleRouteList2);
        List<Route> routeList = new ArrayList<>();
        routeList.add(multiRoute1);
        routeList.add(multiRoute2);
        MultiRoute multiRoute = new MultiRoute(routeList);

        assertEquals(6000, multiRoute.length());
        assertEquals(5, multiRoute.indexOfSegmentAt(5500));
        assertEquals(new PointCh(2504000, 1200000), multiRoute.pointAt(4000));
        assertEquals(5, multiRoute.nodeClosestTo(4600));
        assertEquals(3, multiRoute.nodeClosestTo(3200));
    }

    @Test
    void pointWorkProperly(){
        /*
        //link pour visualiser ;    https://s.geo.admin.ch/96e1370b74
        //link pour éditer ;        https://s.geo.admin.ch/96e1370b88
        * liste de coordonnées
        * 2_528_521, 1_162_985
        * 2_551_143, 1_168_862
        * 2_559_478, 1_162_174
        * 2_569_586, 1_148_545
        * 2_634_400, 1_127_150
        * 2_695_800, 1_170_150
        * 2_752_800, 1_186_350
        * */
        List<PointCh> exceptedPointChList = new ArrayList<>();
        exceptedPointChList.add(new PointCh(2_528_521, 1_162_985));
        exceptedPointChList.add(new PointCh(2_551_143, 1_168_862));
        /*exceptedPointChList.add(new PointCh(2_559_478, 1_162_174));*/
        exceptedPointChList.add(new PointCh(2_569_586, 1_148_545));
        /*exceptedPointChList.add(new PointCh(2_634_400, 1_127_150));*/
        exceptedPointChList.add(new PointCh(2_695_800, 1_170_150));
        exceptedPointChList.add(new PointCh(2_752_800, 1_186_350));

        //premier nbr = indice second nbr = nbr de points
        Edge edge1_1 = new Edge(0,1, new PointCh( 2_528_521, 1_162_985), new PointCh( 2_551_143, 1_168_862), new PointCh( 2_528_521, 1_162_985).distanceTo(new PointCh( 2_551_143, 1_168_862)),null);
        Edge edge2_2 = new Edge(1,3, new PointCh( 2_551_143, 1_168_862), new PointCh(2_569_586, 1_148_545), new PointCh(2_551_143, 1_168_862).distanceTo(new PointCh( 2_559_478, 1_162_174)) +  new PointCh( 2_569_586, 1_148_545).distanceTo( new PointCh(2_559_478, 1_162_174)), null);
        Edge edge3_2 = new Edge(3,5, new PointCh( 2_569_586, 1_148_545), new PointCh(2_695_800, 1_170_150), new PointCh(2_634_400, 1_127_150).distanceTo(new PointCh( 2_695_800, 1_170_150)) +  new PointCh( 2_634_400, 1_127_150).distanceTo( new PointCh(2_569_586, 1_148_545)), null);
        Edge edge4_1 = new Edge(5,6, new PointCh(2_695_800, 1_170_150), new PointCh(2_752_800, 1_186_350),new PointCh( 2_752_800, 1_186_350).distanceTo(new PointCh( 2_695_800, 1_170_150)) , null);

        List<Edge> edgeList1 = new ArrayList<>();
        edgeList1.add(edge1_1);
        edgeList1.add(edge2_2);
        List<Edge> edgeList2 = new ArrayList<>();
        edgeList2.add(edge3_2);
        edgeList2.add(edge4_1);

        Route singleRoute1 = new SingleRoute(edgeList1);
        Route singleRoute2 = new SingleRoute(edgeList2);
        List<Route> sglR1_SglR2 = new ArrayList();
        sglR1_SglR2.add( singleRoute1);
        sglR1_SglR2.add( singleRoute2);
        Route multiRoute = new MultiRoute(sglR1_SglR2);

        assertEquals(exceptedPointChList, multiRoute.points());
    }

    @Test
    void nodeClosestToTest() {
        /*
        //link pour visualiser ;    https://s.geo.admin.ch/96e1370b74
        //link pour éditer ;        https://s.geo.admin.ch/96e1370b88
        * liste de coordonnées
        * 2_528_521, 1_162_985
        * 2_551_143, 1_168_862
        * 2_559_478, 1_162_174
        * 2_569_586, 1_148_545
        * 2_634_400, 1_127_150
        * 2_695_800, 1_170_150
        * 2_752_800, 1_186_350
        * */
        /*double arete0 = new PointCh( 2_528_521, 1_162_985).distanceTo( new PointCh( 2_551_143, 1_168_862));
        double arete1 = new PointCh( 2_551_143, 1_168_862).distanceTo( new PointCh(2_559_478, 1_162_174));
        double arete2 = new PointCh(2_559_478, 1_162_174).distanceTo( new PointCh(2_569_586, 1_148_545));
        System.out.println(arete0 + " arete0");
        System.out.println(arete1 + " arete1");
        System.out.println(arete2 + " arete2");*/

        Edge edge1 = new Edge(0,1, new PointCh( 2_528_521, 1_162_985), new PointCh( 2_551_143, 1_168_862), new PointCh( 2_528_521, 1_162_985).distanceTo(new PointCh( 2_551_143, 1_168_862)),null);
        Edge edge2 = new Edge(1,2, new PointCh( 2_551_143, 1_168_862), new PointCh(2_559_478, 1_162_174), new PointCh(2_551_143, 1_168_862).distanceTo(new PointCh( 2_559_478, 1_162_174)), null);
        Edge edge3 = new Edge(2,3, new PointCh(2_559_478, 1_162_174), new PointCh(2_569_586, 1_148_545), new PointCh(2_569_586, 1_148_545).distanceTo(new PointCh( 2_559_478, 1_162_174)), null);
        Edge edge4 = new Edge(3,4, new PointCh( 2_569_586, 1_148_545), new PointCh(2_634_400, 1_127_150), new PointCh(2_634_400, 1_127_150).distanceTo(new PointCh( 2_569_586, 1_148_545)) , null);
        Edge edge5 = new Edge(4,5, new PointCh(2_634_400, 1_127_150), new PointCh(2_695_800, 1_170_150), new PointCh(2_634_400, 1_127_150).distanceTo(new PointCh( 2_695_800, 1_170_150)), null);
        Edge edge6 = new Edge(5,6, new PointCh(2_695_800, 1_170_150), new PointCh(2_752_800, 1_186_350),new PointCh( 2_752_800, 1_186_350).distanceTo(new PointCh( 2_695_800, 1_170_150)), null);

        //créatioin 3 Single route
        List<Edge> edgeList1 = new ArrayList<>();
        edgeList1.add(edge1);
        edgeList1.add(edge2);
        List<Edge> edgeList2 = new ArrayList<>();
        edgeList2.add(edge3);
        edgeList2.add(edge4);
        List<Edge> edgeList3 = new ArrayList<>();
        edgeList3.add(edge5);
        edgeList3.add(edge6);
        Route singleRoute1 = new SingleRoute(edgeList1);
        Route singleRoute2 = new SingleRoute(edgeList2);
        Route singleRoute3 = new SingleRoute(edgeList3);

        //création de 2 Multiroute sglR1_SglR2 et sglR3
        List<Route> sglR1_SglR2 = new ArrayList();
        List<Route> sglR3 = new ArrayList();
        sglR1_SglR2.add(singleRoute1);
        sglR1_SglR2.add(singleRoute2);
        sglR3.add(singleRoute3);
        Route multiRoute12 = new MultiRoute(sglR1_SglR2);
        Route multiRoute3 = new MultiRoute(sglR3);

        //création d'une multiroute avec les 2 multiroute

        List<Route> multiroute123 = new ArrayList();
        multiroute123.add(multiRoute12);
        multiroute123.add(multiRoute3);
        Route multiRoute = new MultiRoute(multiroute123);

        assertEquals(2,multiRoute.nodeClosestTo(40000));
        assertEquals(3,multiRoute.nodeClosestTo(48000));
        assertEquals(3,multiRoute.nodeClosestTo(77000));
        assertEquals(5,multiRoute.nodeClosestTo(170000));
        assertEquals(6,multiRoute.nodeClosestTo(243000));
        //TODO cas limites
        assertEquals(6,multiRoute.nodeClosestTo(4_500_000));
        assertEquals(0,multiRoute.nodeClosestTo(-170000));
    }

    @Test
    void pointClosestToWorkProperly() {

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


    @Test
    void elevationAt(){
        Edge edge1 = new Edge(0,1, new PointCh( 2_528_521, 1_162_985), new PointCh( 2_551_143, 1_168_862), new PointCh( 2_528_521, 1_162_985).distanceTo(new PointCh( 2_551_143, 1_168_862)),null);
        Edge edge2 = new Edge(1,2, new PointCh( 2_551_143, 1_168_862), new PointCh(2_559_478, 1_162_174), new PointCh(2_551_143, 1_168_862).distanceTo(new PointCh( 2_559_478, 1_162_174)), null);
        Edge edge3 = new Edge(2,3, new PointCh(2_559_478, 1_162_174), new PointCh(2_569_586, 1_148_545), new PointCh(2_569_586, 1_148_545).distanceTo(new PointCh( 2_559_478, 1_162_174)), null);
        Edge edge4 = new Edge(3,4, new PointCh( 2_569_586, 1_148_545), new PointCh(2_634_400, 1_127_150), new PointCh(2_634_400, 1_127_150).distanceTo(new PointCh( 2_569_586, 1_148_545)) , null);
        Edge edge5 = new Edge(4,5, new PointCh(2_634_400, 1_127_150), new PointCh(2_695_800, 1_170_150), new PointCh(2_634_400, 1_127_150).distanceTo(new PointCh( 2_695_800, 1_170_150)), null);
        Edge edge6 = new Edge(5,6, new PointCh(2_695_800, 1_170_150), new PointCh(2_752_800, 1_186_350),new PointCh( 2_752_800, 1_186_350).distanceTo(new PointCh( 2_695_800, 1_170_150)), null);

        //créatioin 3 Single route
        List<Edge> edgeList1 = new ArrayList<>();
        edgeList1.add(edge1);
        edgeList1.add(edge2);
        List<Edge> edgeList2 = new ArrayList<>();
        edgeList2.add(edge3);
        edgeList2.add(edge4);
        List<Edge> edgeList3 = new ArrayList<>();
        edgeList3.add(edge5);
        edgeList3.add(edge6);
        Route singleRoute1 = new SingleRoute(edgeList1);
        Route singleRoute2 = new SingleRoute(edgeList2);
        Route singleRoute3 = new SingleRoute(edgeList3);

        //création de 2 Multiroute sglR1_SglR2 et sglR3
        List<Route> sglR1_SglR2 = new ArrayList();
        List<Route> sglR3 = new ArrayList();
        sglR1_SglR2.add(singleRoute1);
        sglR1_SglR2.add(singleRoute2);
        sglR3.add(singleRoute3);
        Route multiRoute12 = new MultiRoute(sglR1_SglR2);
        Route multiRoute3 = new MultiRoute(sglR3);

        //création d'une multiroute avec les 2 multiroute

        List<Route> multiroute123 = new ArrayList();
        multiroute123.add(multiRoute12);
        multiroute123.add(multiRoute3);
        Route multiRoute = new MultiRoute(multiroute123);

        List<Edge> edges1 = new ArrayList<>();
        List<Edge> edges2 = new ArrayList<>();
        List<Edge> edges3 = new ArrayList<>();
        float[] tab1 = {1000, 1020, 1040, 1060, 1080};
        edges1.add(new Edge(0, 1, new PointCh(2800000, 1205000), new PointCh(2800040, 1205000), 40, Functions.sampled(tab1, 40)));
        float[] tab2 = {1080, 1075, 1070};
        edges1.add(new Edge(1, 2, new PointCh(2800040, 1205000), new PointCh(2800120, 1205000), 80, Functions.sampled(tab2, 80)));
        float[] tab3 = {1070, 1075, 1080, 1085, 1090, 1095, 1100};
        edges3.add(new Edge(2, 3, new PointCh(2800120, 1205000), new PointCh(2800150, 1205000), 30, Functions.sampled(tab3, 30)));

        SingleRoute singleRoute1 = new SingleRoute(edges1);
        SingleRoute singleRoute2 = new SingleRoute(edges2);
        SingleRoute singleRoute3 = new SingleRoute(edges3);

        List<Route> multiRouteList1 = new ArrayList<>();
        List<Route> multiRouteList2 = new ArrayList<>();
        List<Route> multiRouteList3 = new ArrayList<>();
        multiRouteList1.add(singleRoute1);
        multiRouteList2.add(singleRoute2);
        multiRouteList3.add(singleRoute3);
        MultiRoute multiRoute1 = new MultiRoute(multiRouteList1);
        MultiRoute multiRoute2 = new MultiRoute(multiRouteList2);
        MultiRoute multiRoute33 = new MultiRoute(multiRouteList3);

        List<Route> Mulirrr = new ArrayList<>();
        Mulirrr.add(multiRoute1);
        Mulirrr.add(multiRoute2);
        List<Route> mmm = new MultiRoute(Mulirrr);

        List<Route> multiR12 =new MultiRoute()




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
}
