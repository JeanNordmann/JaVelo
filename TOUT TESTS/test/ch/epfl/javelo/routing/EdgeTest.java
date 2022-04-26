package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.TestManager;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class EdgeTest {


    class F implements DoubleUnaryOperator {

        @Override
        public double applyAsDouble(double operand) {
            return operand*operand ;
        }
    }

    public static PointCh offSetBy(double e , double n ){
        return new PointCh(SwissBounds.MIN_E + e , SwissBounds.MIN_N + n  );
    }
    @Test
    void ofWorksOnKnownFunction(){
            double xMax = 5. ;
            Edge e = new Edge(0 , 1 , offSetBy(0.5 , 0.5 ) , offSetBy( 5 , 5 ) , xMax , new F() );

            assertEquals(e.elevationAt(0) , 0  , TestManager.DOUBLE_DELTA );
            for(double x = -10 ; x < 11 ; x += 1./3 ){
                assertEquals(e.elevationAt(x) , x*x , TestManager.DOUBLE_DELTA );
            }
    }


    @Test
    void ofWorksOnKnownSampledFunction(){
        double xMax = 5. ;


        float[] samples = {1, 2, -3, -7/3, (float)Math.PI, 1};

        DoubleUnaryOperator f = Functions.sampled(samples , xMax );

        Edge e = new Edge(0 , 1 , offSetBy(0.5 , 0.5 ) , offSetBy( 5 , 5 ) , xMax , f );
        for(double x = -10 ; x < 11 ; x += 1./3 ){
            assertEquals( f.applyAsDouble(x) ,e.elevationAt(x) , TestManager.DOUBLE_DELTA );
        }


    }

    @Test
    void pointAtWorksOnEndpoints(){
        double xMax = 7.;
        PointCh startPoint =offSetBy(0.5 , 0.5 ) ;
        PointCh endPoint = offSetBy( 5 , 5 ) ;
        Edge e = new Edge(0 , 1 , startPoint , endPoint , xMax , new F() );

        assertEquals( startPoint , e.pointAt( 0 )   );
        assertEquals( endPoint , e.pointAt(xMax)  );

        e =   new Edge(0 , 1 , startPoint , startPoint , xMax , new F() );

        assertEquals( startPoint , e.pointAt( 0 )   );
        assertEquals( startPoint , e.pointAt(xMax)  );

    }

    @Test
    void pointWorksOnNegativePositions(){

        double e = 10 ;
        PointCh startPoint =offSetBy(e , e ) ;
        PointCh endPoint = offSetBy( e+5 , e+5 ) ;
        double xMax =startPoint.distanceTo(endPoint);
        Edge edge = new Edge(0 , 1 , startPoint , endPoint , xMax , new F() );

        assertEquals(  offSetBy( e-5 , e-5 )  , edge.pointAt( -xMax  ) );
        assertEquals( offSetBy(10 + e , e+10 ) , edge.pointAt( 2 * xMax )  );

//        edge =   new Edge(0 , 1 , startPoint , startPoint , 0.0  , new F() );
//
//        assertEquals( startPoint , edge.pointAt( 0 )   );
    }

    @Test
    void pointWorksOnNiceValues(){
        double xMax = 7.;
        PointCh startPoint =offSetBy(0.5 , 0.5 ) ;
        PointCh endPoint = offSetBy( 5 , 5 ) ;
        Edge e = new Edge(0 , 1 , startPoint , endPoint , xMax , new F() );

        assertEquals( offSetBy(5.5/2 , 5.5/2 )  , e.pointAt( 3.5  )  );

        xMax = 9.;
        startPoint =offSetBy(0 ,  0 ) ;
        endPoint = offSetBy( 3 , 4 ) ;
         e = new Edge(0 , 1 , startPoint , endPoint , xMax , new F() );

        assertEquals( offSetBy(1.  , 4./3 ) , e.pointAt( xMax / 3   )  );


    }

    @Test
    void closestPointWorksOnSquare(){
        double xMax = 5.;
        PointCh startPoint =offSetBy(0 , 0 ) ;
        PointCh endPoint = offSetBy( 5 , 5 ) ;
        double len = endPoint.distanceTo(startPoint) ;
        Edge e = new Edge(0 , 1 , startPoint , endPoint , len , new F() );

    }

    @Test
    void edgeWorksOnRandomValues(){

        for(int i = 0 ; i < 100000 ; i++ ){
            double eS = TestManager.generateRandomIntInBounds(0 ,  SwissBounds.WIDTH );
            double eE = TestManager.generateRandomIntInBounds(0 , SwissBounds.WIDTH);
            double nS = TestManager.generateRandomIntInBounds(0 , SwissBounds.HEIGHT);
            double nE = TestManager.generateRandomIntInBounds(0 , SwissBounds.HEIGHT);

            double ep = TestManager.generateRandomIntInBounds(0 , SwissBounds.WIDTH);
            double np = TestManager.generateRandomIntInBounds(0 ,  SwissBounds.HEIGHT);

            PointCh startPoint =offSetBy(eS , nS ) ;
            PointCh endPoint = offSetBy( eE , nE ) ;
            double len = endPoint.distanceTo(startPoint);
            Edge e = new Edge(0 , 1 , startPoint , endPoint , len , new F() );

            double expected = Math2.dotProduct( ep - eS , np - nS , eE - eS , nE - nS ) / Math2.norm(eE - eS , nE - nS);

            assertEquals(  expected , e.positionClosestTo(offSetBy(ep,np)), TestManager.DOUBLE_DELTA);
        }
    }

}