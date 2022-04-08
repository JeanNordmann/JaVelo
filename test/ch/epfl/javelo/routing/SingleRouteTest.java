package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.TestManager;

import ch.epfl.javelo.TestManager.*;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.TestManager.DOUBLE_DELTA;
import static org.junit.jupiter.api.Assertions.*;

class SingleRouteTest {


    // CONSTRUCTOR
    @Test
    void singleRouteConstructorFailsOnEmptyList(){

        assertThrows(IllegalArgumentException.class , () -> {
            SingleRoute route = new SingleRoute(new ArrayList<Edge>() );
        });
    }

    // LENGTH

    @Test
    void lengthWorks(){
        int numberOfTests = 100000 ;
        for(int i = 0 ; i < numberOfTests ; i++ ){
            List<Edge> edges = new ArrayList();

            int m = TestManager.generateRandomIntInBounds(10 , 100);
            double length = 0 ;
            for(int j = 0 ; j < m ; j++  ){
                double l = TestManager.generateRandomDoubleInBounds(0 , 1e4);
                length += l;
                edges.add(new Edge(0,0,null , null , l , null ) );
            }
            SingleRoute route = new SingleRoute( edges );
            assertEquals(length , route.length() , DOUBLE_DELTA );

        }
    }


    // EDGE
    @Test
    void edgesWorks(){
        PointCh[] points = {TestManager.offSetBy(50 , 50 ) ,TestManager.offSetBy(20 , 20 ) ,
                TestManager.offSetBy(20 , 10 ) , TestManager.offSetBy(4 , 98 ) ,
                TestManager.offSetBy(100 , 80 ) , TestManager.offSetBy(3 , 1 ) } ;
        List<Edge> edges = new ArrayList<>();

        double[] lengths = {  5800, 2300, 1100, 2200 , 1700  };
        for(int i = 0 ; i < points.length-1 ; i++ ){
            edges.add(
                    new Edge(i ,i+1 , points[i] , points[i+1] ,lengths[i] , null  )
            );
        }
        SingleRoute route = new SingleRoute(edges);
        assertEquals( edges , route.edges() ) ;
    }



    private Edge createEdge(int fromNode , int toNode , double length ){
        return new Edge( fromNode , toNode , TestManager.offSetBy(0,0) ,  TestManager.offSetBy(0,0) , length , null  );
    }

    // PointAt
    @Test
    void pointAtWorksforEdgeCase(){

        PointCh point2 =  TestManager.offSetBy(50 , 50 ) ;
        PointCh point1 =  TestManager.offSetBy(20 , 20 );
        List<Edge> edges = List.of(new Edge( 0 , 1 ,point1 , TestManager.offSetBy(0,0)  , 30 , null ),
                createEdge(1 , 2 , 100 ) ,
                new Edge( 2 , 3 , TestManager.offSetBy(0,0) ,  point2 , 800 , null ));
        SingleRoute route = new SingleRoute(edges);
        assertEquals( point2  , route.pointAt( 1000 ) );
        assertEquals( point1 ,  route.pointAt(-1 ) );
    }

    @Test
    void pointAtToWorks(){
        List<PointCh> points = new ArrayList<PointCh>(Collections.singleton(TestManager.offSetBy(50000, 50000)));
        List<Edge> edges = new ArrayList<>();

        double[] lengths = {  580, 230, 100, 220 , 170  };
        double[] angles = { 30 , 50 , -20 ,10  , 90  };
        for(int i = 0 ; i < lengths.length ; i++ ){
            points.add( pointAtAngleFromPoint(points.get(i) , angles[i] , lengths[i]) );
        }
        for(int i = 0 ; i < lengths.length ; i++ ){

            edges.add(
                    new Edge(i ,i+1 , points.get(i) , points.get(i+1) ,lengths[i] , null  )
            );
        }

        double L = 0 ;
        SingleRoute route = new SingleRoute( edges );
        assertEquals( points.get(0) , route.pointAt(-1 ));
        assertEquals( points.get(0) , route.pointAt(0 ));
        // fix this so that edges have same length as claimed !
        for(int i = 0 ; i < points.size()-1 ; i++ ){
            double next = L + lengths[i];
            System.out.println( i + " " + L );

            PointCh p = points.get(i);
            PointCh np = points.get(i+1);
            double[] positions = { L , L + lengths[i]*0.33333 , L + lengths[i]*0.499999999999999 , L + lengths[i]*0.7 ,
                    next };
            for(int j = 0 ; j < positions.length; j++ ){
                double d = positions[j] - L ;

                assertEquals(pointAtAngleFromPoint( p , angles[i] , d ), route.pointAt(positions[j]));
            }


            L = next ;
        }

        assertEquals( points.get(points.size()-1) , route.pointAt(60000000000.0));
    }






    @Test
    void nodeClosestToWorksforEdgeCase(){

        PointCh point2 =  TestManager.offSetBy(50 , 50 ) ;
        PointCh point1 =  TestManager.offSetBy(20 , 20 );
        List<Edge> edges = List.of(new Edge( 0 , 1 ,point1 , point2  , 30 , null ),
                createEdge(1 , 2 , 100 ) ,
                new Edge( 2 , 3 , TestManager.offSetBy(0,0) ,  point2 , 800 , null ));
        SingleRoute route = new SingleRoute(edges);
        assertEquals( 3  , route.nodeClosestTo( 1000 ) );
        assertEquals( 0  ,  route.nodeClosestTo(-1 ) );

    }

    @Test
    void nodeClosestToWorks(){
        PointCh[] points = {TestManager.offSetBy(50 , 50 ) ,TestManager.offSetBy(20 , 20 ) ,
                TestManager.offSetBy(20 , 10 ) , TestManager.offSetBy(4 , 98 ) ,
                TestManager.offSetBy(100 , 80 ) , TestManager.offSetBy(3 , 1 ) } ;
        List<Edge> edges = new ArrayList<>();

        double[] lengths = {  5800, 2300, 1100, 2200 , 1700  };

        for(int i = 0 ; i < points.length-1 ; i++ ){
            edges.add(
               new Edge(i ,i+1 , points[i] , points[i+1] ,lengths[i] , null  )
            );
        }

        double L = 0 ;
        SingleRoute route = new SingleRoute( edges );
        assertEquals( 0 , route.nodeClosestTo(-1 ));
        assertEquals( 0 , route.nodeClosestTo(0 ));

        for(int i = 0 ; i < points.length-1 ; i++ ){
            double next = L + lengths[i];
            System.out.println( i + " " + L );
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


    public static  PointCh pointAtAngleFromPoint(PointCh point , double angle , double length ){
        return new PointCh( point.e() + length * Math.cos(Math.toRadians(angle))
                , point.n() + length*Math.sin(Math.toRadians(angle)) );
    }

    public static  Edge dummyEdge(DoubleUnaryOperator op , double l ){
        return new Edge( 0 , 0 , TestManager.offSetBy(0,0), TestManager.offSetBy(0,0),l ,
        op ) ;
    }


    @Test
    void elevationAtWorks(){

        DoubleUnaryOperator sqr = (x)->(x*x);
        DoubleUnaryOperator sqrt = (x)->(Math.sqrt(x));
        DoubleUnaryOperator cbrt = (x)->(Math.cbrt(x));
        DoubleUnaryOperator linear = (x)->(0.7*x+ 9);
        double[] lengths = {  5800, 2300, 1100, 2200 , 1700  };
        DoubleUnaryOperator[] operators = { sqr , Functions.constant(6) ,  cbrt ,  sqrt ,
         linear };

        List<Edge> edges = new ArrayList<>();
        for(int i = 0 ; i < lengths.length ; i++ ){
            edges.add ( dummyEdge(operators[i] , lengths[i]) );
        }

        SingleRoute route = new SingleRoute(edges);
        double L = 0 ;
        for(int t = 0 ; t < (int)10e5 ; t++ ) {
            L = 0 ;
            for (int i = 0; i < lengths.length; i++) {
                double next = L + lengths[i];
                double a = L + 0.0001, b = next - 0.0001;
//                if (i == 0) a -= 5000;
                if (i == lengths.length - 1) b += 5000;
                double x = TestManager.generateRandomDoubleInBounds(a, b);
//                System.out.println("i = " + i + " x= " + x + " L = " + L + " Lnext = " + next );
                assertEquals(operators[i].applyAsDouble(Math2.clamp(0, x - L, lengths[i]))
                        , route.elevationAt(x) , DOUBLE_DELTA);
                L = next;
            }
        }

    }



    @Test
    void pointClosestTo() {

        PointCh[] points = { TestManager.offSetBy(1 , 1 ) ,TestManager.offSetBy(4 , 3 ) ,
        TestManager.offSetBy(2 , 5 ) , TestManager.offSetBy(8,8) , TestManager.offSetBy(8.5, 5.8 ) ,
        TestManager.offSetBy( 7 , 3 ) , TestManager.offSetBy(3,10) , TestManager.offSetBy(10,10 ) };

        // [ 0 , len 1 , len 1 + len 2 , .... ]
        List<Double> lengths = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        List<Double> distances = new ArrayList<>();
        distances.add(.0);
        for(int i = 0 ; i < points.length-1  ; i++ ) {
            lengths.add(points[i].distanceTo(points[i + 1]));
            distances.add(lengths.get(i) + distances.get(distances.size()-1) );
            System.out.println(distances.get(i) + " " + points[i] );
            edges.add(
                    new Edge(i, i + 1, points[i], points[i+1], lengths.get(i), null)
            );
        }
        SingleRoute route = new SingleRoute(edges);

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


        PointCh[] A =  { TestManager.offSetBy(1.1 ,  1.1 ) ,TestManager.offSetBy(4 , 3.2 ) ,
                TestManager.offSetBy(2.3 , 5.1 ) , TestManager.offSetBy(9 , 7  ) , TestManager.offSetBy(8.6, 5.6 ) ,
                TestManager.offSetBy( 4 , 9 ) , TestManager.offSetBy(3.2,10.2)  };
        double L = 0 ;
        for(int i = 0 ; i < A.length  ; i ++ ){

            System.out.println((A[i].e() - SwissBounds.MIN_E) + " " + (A[i].n() - SwissBounds.MIN_N));
            double pro = Math2.projectionLength(points[i].e() , points[i].n() , points[i+1].e() , points[i+1].n() ,
                    A[i].e() , A[i].n() );
            pro = Math2.clamp( 0 ,  pro , lengths.get(i)) ;
            PointCh pt = edges.get(i).pointAt(pro) ;
            double disToRef = A[i].distanceTo(pt)  ;
            System.out.println("ref ");
            printRoutePoint(A[i]);
            System.out.println("expected");
            printRoutePoint( new RoutePoint( pt , pro + L  , disToRef ).point() );
            System.out.println("calculated");

            assertEquals( new RoutePoint( pt , pro + L  , disToRef ) , route.pointClosestTo(A[i]) );
            L += lengths.get(i);
        }
    }

    void printRoutePoint(PointCh x){
        System.out.println("("+ (x.e()-SwissBounds.MIN_E) + ","+(x.n()-SwissBounds.MIN_N)+")");

    }
}