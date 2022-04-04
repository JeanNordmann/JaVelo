package ch.epfl.javelo.routing;

import ch.epfl.javelo.TestManager;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoutePointTest {

    @Test
    void noneIsWellDefined(){
        RoutePoint routePoint = RoutePoint.NONE;
        assertEquals( routePoint.point() , null  );
        assertEquals( routePoint.position() , Double.NaN , TestManager.DOUBLE_DELTA );
        assertEquals(routePoint.distanceToReference() , Double.POSITIVE_INFINITY , TestManager.DOUBLE_DELTA);
    }

    @Test
    void withPositionShifted(){
        PointCh pointCh = new PointCh(SwissBounds.MIN_E + 200 , SwissBounds.MIN_N + 200);
        RoutePoint routePoint = new RoutePoint( pointCh , 13500. , 4 );
        assertEquals(routePoint.withPositionShiftedBy(500) , new RoutePoint(pointCh , 14000 , 4 ));
        assertEquals(routePoint.withPositionShiftedBy(-500) , new RoutePoint(pointCh , 13000 , 4 ));
        assertEquals(routePoint.withPositionShiftedBy(0) , new RoutePoint(pointCh , 13500 , 4 ));

    }

    @Test
    void minWorks(){
        int N = 10000 ;
        for(int i = 0 ; i < N ; i++ ){
            double dist1 = TestManager.generateRandomDoubleInBounds( 0 , 100 ) ;

            double dist2 = TestManager.generateRandomDoubleInBounds( dist1 , 100 ) ;

            double e =  TestManager.generateRandomDoubleInBounds( 0 , 100 );
            double n =  TestManager.generateRandomDoubleInBounds( 0 , 100 );
            RoutePoint routePoint =new RoutePoint(EdgeTest.offSetBy( e,n) ,5.5 , dist1);
            RoutePoint routePoin2t =new RoutePoint(EdgeTest.offSetBy( e,n) ,5.5 , dist2);

            assertEquals(routePoint.min( routePoin2t) , routePoint );


            assertEquals(routePoint.min( EdgeTest.offSetBy( e,n) , TestManager.generateRandomDoubleInBounds( 0 , 100 ) ,
                    dist2
                    ) , routePoint );


        }
    }

}