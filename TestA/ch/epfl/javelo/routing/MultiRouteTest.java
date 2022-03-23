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
}
