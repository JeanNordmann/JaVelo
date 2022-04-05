package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.TestManager;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.TestManager.DOUBLE_DELTA;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;



public class MultiRouteTest {


    @Test
    void constructorFailsOnEmptyList(){
        assertThrows( IllegalArgumentException.class , () -> {
            MultiRoute mr = new MultiRoute( new ArrayList<Route>());
        });

    }

    private Route samplesMultiRoute(){

        Edge e = SingleRouteTest.dummyEdge(null , 500 );
        Route r = new MultiRoute( List.of ( new SingleRoute ( List.of(e,e) ) ) )  ;
        Route rr = new MultiRoute( List.of ( r , r , r ) );
        Route rrr = new MultiRoute( List.of( rr , rr )) ;
        return rrr ;
    }
    @Test
    void indexOfSegmentAtWorksForKnownValues(){
        Route rrr = samplesMultiRoute() ;
        assertEquals( 5  , rrr.indexOfSegmentAt( 5500 ));
        assertEquals( 5  , rrr.indexOfSegmentAt( 6000 ));

        assertEquals( 5  , rrr.indexOfSegmentAt( 9999000 ));

        assertEquals( 0 , rrr.indexOfSegmentAt(500 ));
        assertEquals( 0 , rrr.indexOfSegmentAt( 0 ));
        assertEquals( 0 , rrr.indexOfSegmentAt( -1.33  ));

        assertEquals(  0 , rrr.indexOfSegmentAt( 1000 )); // convention : return previous edge
        assertEquals( 2 , rrr.indexOfSegmentAt(3000 ));
        assertEquals( 3 , rrr.indexOfSegmentAt( 3200 ));
        assertEquals( 3 , rrr.indexOfSegmentAt(3900 ));
        assertEquals( 4 , rrr.indexOfSegmentAt( 4200 ));
    }

    @Test
    void indexOfSegmentsWorksForTrivialCases(){
        double edgeLength = 700 / 11 ;
        Edge e = SingleRouteTest.dummyEdge(null , edgeLength  );
        Route r = new MultiRoute( List.of ( new SingleRoute ( List.of(e,e) ) ) )  ;
        assertEquals(  0 , r.indexOfSegmentAt(edgeLength / 2)  );

        assertEquals(  0 , r.indexOfSegmentAt(edgeLength * 2)  );

        assertEquals(  0 , r.indexOfSegmentAt(edgeLength * 3)  );
        r = new MultiRoute( List.of ( r , r , r ) ) ;
        assertEquals( 0 , r.indexOfSegmentAt(edgeLength * 1.5 ));
        assertEquals( 1 , r.indexOfSegmentAt(edgeLength * 2.5 ));
        assertEquals( 0 , r.indexOfSegmentAt(edgeLength / 2 ));
    }




    @Test
    void lengthWorksOnKnowExamples(){
        Route r = samplesMultiRoute();
        assertEquals( 6000 , r.length(), TestManager.FLOAT_DELTA );
    }

    private static List<Edge> createEdgesWithRandomLengths(int N , List<Double> l ){

        var rng = newRandom();
        List<Edge> edges = new ArrayList<>();

        for(int i = 0 ; i < N ; i++ ){
            double ll = rng.nextDouble(0,20000 ) ;
            l.add ( ll );
            edges.add( SingleRouteTest.dummyEdge(null, ll )) ;
        }
        return edges ;
    }
    @Test
    void lengthWorksOnRandom(){
        var rng = newRandom() ;
        for(int t = 0 ; t < 100 ; t++ ) {
            int n = rng.nextInt(0, 20);
            double totalLength = 0;
            Route r;
            List<Route> rlist = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                List<Double> l = new ArrayList<>();
                var edges = createEdgesWithRandomLengths(rng.nextInt(5, 10), l);
                double s = 0;
                for (double x : l) s += x;
                totalLength += s;


                Route ar = new MultiRoute(List.of(new SingleRoute(edges)));
                rlist.add(ar);
                assertEquals(s, ar.length());
                r = new MultiRoute(rlist);
                assertEquals(totalLength, r.length());
            }
        }

    }

    @Test
    void pointAtWorks(){

        List<PointCh> points = new ArrayList<PointCh>(Collections.singleton(TestManager.offSetBy(50000, 50000)));
        List<Edge> edges = new ArrayList<>();
        List<Route> routes = new ArrayList<>();

        double lengths [] = {  580, 230, 100, 220 , 170  };
        double angles [] = { 30 , 50 , -20 ,10  , 90  };
        for(int i = 0 ; i < lengths.length ; i++ ){
            points.add( SingleRouteTest.pointAtAngleFromPoint(points.get(i) , angles[i] , lengths[i]) );
        }
        for(int i = 0 , j = 0 ; i < lengths.length ; i++ ){
            edges= new ArrayList<>();
            edges.add(
                    new Edge(j ,j+1 , points.get(i) , points.get(i+1) ,lengths[i] , null  )
            );
            routes.add( new MultiRoute(List.of ( new SingleRoute(edges)) )   );
        }

        double L = 0 ;

        Route  route = groupInRoutes( new MultiRoute( routes ) );
        route = groupInRoutes( (MultiRoute) route )  ;
        route = groupInRoutes((MultiRoute) route);
        route = groupInRoutes((MultiRoute) route);

        assertEquals( points.get(0) , route.pointAt(-1 ));
        assertEquals( points.get(0) , route.pointAt(0 ));
        // fix this so that edges have same length as claimed !
        for(int i = 0 ; i < points.size()-1 ; i++ ){
            double next = L + lengths[i];


            PointCh p = points.get(i);
            PointCh np = points.get(i+1);
            double positions[] = { L , L + lengths[i]*0.33333 , L + lengths[i]*0.499999999999999 , L + lengths[i]*0.7 ,
                    next };
            for(int j = 0 ; j < positions.length; j++ ){
                double d = positions[j] - L ;

                assertEquals(SingleRouteTest.pointAtAngleFromPoint( p , angles[i] , d ), route.pointAt(positions[j]));
            }


            L = next ;
        }

        assertEquals( points.get(points.size()-1) , route.pointAt(60000000000.0));
    }


    @Test
    void elevationAtWorks(){

        DoubleUnaryOperator sqr = (x)->(x*x);
        DoubleUnaryOperator sqrt = (x)->(Math.sqrt(x));
        DoubleUnaryOperator cbrt = (x)->(Math.cbrt(x));
        DoubleUnaryOperator linear = (x)->(0.7*x+ 9);
        double lengths [] = {  5800, 2300, 1100, 2200 , 1700  };
        DoubleUnaryOperator operators[] = { sqr , Functions.constant(6) ,  cbrt ,  sqrt ,
                linear };

        List<Edge> edges = new ArrayList<>();
        List<Route> routes = new ArrayList<>() ;
        for(int i = 0 ; i < lengths.length ; i++ ){
            edges = new ArrayList<>();
            edges.add ( SingleRouteTest.dummyEdge(operators[i] , lengths[i]) );
            routes.add ( new SingleRoute( edges )) ;
        }


        Route  route = groupInRoutes( new MultiRoute( routes ) );
        route = groupInRoutes( (MultiRoute) route )  ;
        route = groupInRoutes((MultiRoute) route);
        route = groupInRoutes((MultiRoute) route);

        double L = 0 ;
        for(int t = 0 ; t < (int)10e5 ; t++ ) {
            L = 0 ;
            for (int i = 0; i < lengths.length; i++) {
                double next = L + lengths[i];
                double a = L + 0.0001, b = next - 0.0001;
//                if (i == 0) a -= 5000;
                if (i == lengths.length - 1) b += 5000;
                double x = TestManager.generateRandomDoubleInBounds(a, b);
                assertEquals(operators[i].applyAsDouble(Math2.clamp(0, x - L, lengths[i]))
                        , route.elevationAt(x) , DOUBLE_DELTA);
                L = next;
            }
        }

    }

    @Test
    void nodeClosestToWorks(){
        PointCh points[] = {TestManager.offSetBy(50 , 50 ) ,TestManager.offSetBy(20 , 20 ) ,
                TestManager.offSetBy(20 , 10 ) , TestManager.offSetBy(4 , 98 ) ,
                TestManager.offSetBy(100 , 80 ) , TestManager.offSetBy(3 , 1 ) } ;
        List<Edge> edges = new ArrayList<>();
        List<Route> routes = new ArrayList<>();
        double lengths [] = {  5800, 2300, 1100, 2200 , 1700  };

        for(int i = 0 ; i < points.length-1 ; i++ ){
            edges = new ArrayList<>();
            edges.add(
                    new Edge(i ,i+1 , points[i] , points[i+1] ,lengths[i] , null  )
            );
            routes.add(new SingleRoute(edges));
        }

        double L = 0 ;
        Route  route = groupInRoutes( new MultiRoute( routes ) );
        route = groupInRoutes( (MultiRoute) route )  ;
        route = groupInRoutes((MultiRoute) route);
        route = groupInRoutes((MultiRoute) route);
        assertEquals( 0 , route.nodeClosestTo(-1 ));
        assertEquals( 0 , route.nodeClosestTo(0 ));

        for(int i = 0 ; i < points.length-1 ; i++ ){
            double next = L + lengths[i];
            assertEquals( i , route.nodeClosestTo(L));
            assertEquals( i , route.nodeClosestTo( L + lengths[i] * 0.3333 ));
            assertEquals( i , route.nodeClosestTo( L + lengths[i] * 0.499999999999999999999999));
            assertEquals( i+1 , route.nodeClosestTo( L + lengths[i] * 0.777777777777777));
            assertEquals( i + 1 , route.nodeClosestTo( next ) );
            L = next ;
        }
        assertEquals( points.length - 1 , route.nodeClosestTo( L ));
        assertEquals( points.length - 1 , route.nodeClosestTo( L + 79  ));

    }


    @Test
    void pointClosestToWorks() {

        PointCh points [] = { TestManager.offSetBy(1 , 1 ) ,TestManager.offSetBy(4 , 3 ) ,
                TestManager.offSetBy(2 , 5 ) , TestManager.offSetBy(8,8) , TestManager.offSetBy(8.5, 5.8 ) ,
                TestManager.offSetBy( 7 , 3 ) , TestManager.offSetBy(3,10) , TestManager.offSetBy(10,10 ) };

        // [ 0 , len 1 , len 1 + len 2 , .... ]
        List<Double> lengths = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        List<Edge> edges1 = new ArrayList<>();
        List<Double> distances = new ArrayList<>();
        List<Route> routes = new ArrayList();
        distances.add(.0);
        for(int i = 0 ; i < points.length-1  ; i++ ) {
            lengths.add(points[i].distanceTo(points[i + 1]));
            distances.add(lengths.get(i) + distances.get(distances.size()-1) );
            edges1 = new ArrayList<>();
            edges1.add(
                    new Edge(i, i + 1, points[i], points[i+1], lengths.get(i), null)
            );
            edges.add(new Edge(i, i + 1, points[i], points[i+1], lengths.get(i), null));
            routes.add( new SingleRoute(edges1));
        }
        Route  route = groupInRoutes( new MultiRoute( routes ) );
        route = groupInRoutes( (MultiRoute) route )  ;
        route = groupInRoutes((MultiRoute) route);
        route = groupInRoutes((MultiRoute) route);

        for (PointCh p : points ){
            // test point equality, distance to reference equality, and position equality
            RoutePoint actual = route.pointClosestTo(p);
            assertEquals(p, actual.point());
            assertEquals(0, actual.distanceToReference(), DOUBLE_DELTA);
        }

        assertEquals( new RoutePoint( points[0] , 0 , 1 )    , route.pointClosestTo( TestManager.offSetBy( 0 , 1 )) ) ;
        assertEquals( new RoutePoint( points[0] , 0 , 1 )  ,route.pointClosestTo( TestManager.offSetBy( 1 , 0 )) ) ;
        assertEquals( new RoutePoint( points[0] , 0 , Math.sqrt(2) )  , route.pointClosestTo(TestManager.offSetBy( 0 , 0 )) ) ;

        assertEquals( new RoutePoint( points[points.length-1] , distances.get(distances.size()-1) , 10 )  , route.pointClosestTo( TestManager.offSetBy(10 , 20)) ) ;
        assertEquals(new RoutePoint(points[points.length-1] , distances.get(distances.size()-1) , 10 ) , route.pointClosestTo( TestManager.offSetBy(20 , 10)) ) ;
        assertEquals(new RoutePoint(points[points.length-1]  , distances.get(distances.size()-1) , 0 ), route.pointClosestTo( TestManager.offSetBy(10 , 10 )) ) ;

        assertEquals(new RoutePoint( TestManager.offSetBy(8.5, 5.8 ) , distances.get(4) , 1.5 )
                , route.pointClosestTo( TestManager.offSetBy(10 , 5.8 )) );


        assertEquals(new RoutePoint(TestManager.offSetBy(3,10  ) , distances.get(6) , 1 )
                , route.pointClosestTo(TestManager.offSetBy(2,10)));


        PointCh A[] =  { TestManager.offSetBy(1.1 ,  1.1 ) ,TestManager.offSetBy(4 , 3.2 ) ,
                TestManager.offSetBy(2.3 , 5.1 ) , TestManager.offSetBy(9 , 7  ) , TestManager.offSetBy(8.6, 5.6 ) ,
                TestManager.offSetBy( 4 , 9 ) , TestManager.offSetBy(3.2,10.2)  };
        double L = 0 ;
        for(int i = 0 ; i < A.length  ; i ++ ){

            double pro = Math2.projectionLength(points[i].e() , points[i].n() , points[i+1].e() , points[i+1].n() ,
                    A[i].e() , A[i].n() );
            pro = Math2.clamp( 0 ,  pro , lengths.get(i)) ;
            PointCh pt = edges.get(i).pointAt(pro) ;
            double disToRef = A[i].distanceTo(pt)  ;
//            System.out.println("ref ");
//            printRoutePoint(A[i]);
//            System.out.println("expected");
//            printRoutePoint( new RoutePoint( pt , pro + L  , disToRef ).point() );
//            System.out.println("calculated");

            assertEquals( new RoutePoint( pt , pro + L  , disToRef ) , route.pointClosestTo(A[i]) );
            L += lengths.get(i);
        }
    }

    void printRoutePoint(PointCh x){
        System.out.println("("+ (x.e()-SwissBounds.MIN_E) + ","+(x.n()-SwissBounds.MIN_N)+")");

    }
    @Test
    void testMultipleTimes(){
        for(int i = 0 ; i < 10 ; i++ ){
            elevationAtWorks();
            nodeClosestToWorks();
            pointAtWorks();
            pointClosestToWorks();
            pointsWorks();
        }
    }




    @Test
    void pointsWorks(){
        PointCh points[] =  { TestManager.offSetBy(1.1 ,  1.1 )  , TestManager.offSetBy(1.1 ,  1.1 )  ,TestManager.offSetBy(4 , 3.2 ) ,
                TestManager.offSetBy(2.3 , 5.1 ) , TestManager.offSetBy(9 , 7  ) , TestManager.offSetBy(8.6, 5.6 ) ,
                TestManager.offSetBy( 4 , 9 ) , TestManager.offSetBy(3.2,10.2)  ,  TestManager.offSetBy(9 , 7  ) ,   TestManager.offSetBy(1.1 ,  1.1 ) };
        List<Edge> edges = new ArrayList();
        List<Route> routes = new ArrayList<>();
        for(int i = 0 ; i < points.length-1  ; i++ ) {
            edges = new ArrayList<>();
            edges.add(new Edge(i, i + 1, points[i], points[i+1], 100 , null));
            routes.add(new SingleRoute(edges));
        }

        Route  route = groupInRoutes( new MultiRoute( routes ) );
        route = groupInRoutes( (MultiRoute) route )  ;
        route = groupInRoutes((MultiRoute) route);
        route = groupInRoutes((MultiRoute) route);

        var A=  List.of( TestManager.offSetBy(1.1 ,  1.1 ) ,TestManager.offSetBy(4 , 3.2 ) ,
                TestManager.offSetBy(2.3 , 5.1 ) , TestManager.offSetBy(9 , 7  ) , TestManager.offSetBy(8.6, 5.6 ) ,
                TestManager.offSetBy( 4 , 9 ) , TestManager.offSetBy(3.2,10.2)  );

        assertEquals(List.of(points),route.points());

    }

}
