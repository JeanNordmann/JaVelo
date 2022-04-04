package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class PointWebMercatorTest {

    @Test
    void constructorFailsOnOutOfBoundValue(){
       assertThrows(IllegalArgumentException.class , () -> {
           PointWebMercator point1 = new PointWebMercator( -1 , 5 );
           PointWebMercator point2 = new PointWebMercator( 0 , 1.001 );

           PointWebMercator point3 = new PointWebMercator( -0.00001, 0.5 );

           PointWebMercator point4 = new PointWebMercator( 0.5 , -1.0001 );
           PointWebMercator point5 = new PointWebMercator( -0.0000000000000000000001 , 1 );
           PointWebMercator point6 = new PointWebMercator( -0 , 1.00000000000000000000000000001);
       });
    }

    @Test
    void ofWorksOnEdgeCases(){
        PointWebMercator expected1 = new PointWebMercator(1. , 1. );
        PointWebMercator actual1 = PointWebMercator.of(0 , 256 , 256 );

        assertEquals(expected1 , actual1 );

        PointWebMercator expected2 = new PointWebMercator(0 , 1. );
        PointWebMercator actual2 = PointWebMercator.of(0 , 0 , 256 );

        assertEquals(expected2 , actual2 );
        PointWebMercator expected3 = new PointWebMercator(1. , 0 );
        PointWebMercator actual3 = PointWebMercator.of(0 , 256 , 0 );

        assertEquals(expected3 , actual3 );

        PointWebMercator expected4 = new PointWebMercator(1. , 1. );
        PointWebMercator actual4 = PointWebMercator.of(0 , 256 , 256 );

        assertEquals(expected4 , actual4 );

    }

    @Test
    void ofWorksOnKnownCases(){
        PointWebMercator expected1 = new PointWebMercator( 0.3 ,  0.7);
        PointWebMercator actual1 = PointWebMercator.of(2 , 256*0.3*2*2, 256*0.7*2*2 );

        assertEquals(expected1 , actual1 );

        PointWebMercator expected2 = new PointWebMercator( 0.1 , 0.1 );
        PointWebMercator actual2 = PointWebMercator.of(2 , 256*0.1*2*2,256*0.1*2*2  );

        assertEquals(expected2 , actual2 );

    }


    @Test
    void xAtZoomLevelWorks(){

        PointWebMercator point = new PointWebMercator( 0.3 ,  0.7);
        double expected1 = 256*4*0.3;
        double actual1 = point.xAtZoomLevel(2);

        assertEquals(expected1 , actual1 , 10e-10);
        PointWebMercator point2 = new PointWebMercator( 0. ,  0.7);
        double expected2 = 0;
        double actual2 = point2.xAtZoomLevel(2);

        assertEquals(expected2 , actual2 , 10e-10);
        PointWebMercator point3 = new PointWebMercator( 1. ,  0.7);
        double expected3 = 256*2*1;
        double actual3 = point3.xAtZoomLevel(1);
        assertEquals(expected3 , actual3 , 10e-10);

        PointWebMercator point4 = new PointWebMercator( .7 ,  0.7);
        double expected4 = 256*0.7;
        double actual4 = point4.xAtZoomLevel(0);
        assertEquals(expected4 , actual4 , 10e-10);
    }

    @Test
    void yAtZoomLevelWorks(){

        PointWebMercator point = new PointWebMercator( 0.7, 0.3  );
        double expected1 = 256*4*0.3;
        double actual1 = point.yAtZoomLevel(2);

        assertEquals(expected1 , actual1 , 10e-10);
        PointWebMercator point2 = new PointWebMercator(  0.7,  0.);
        double expected2 = 0;
        double actual2 = point2.yAtZoomLevel(2);

        assertEquals(expected2 , actual2 , 10e-10);
        PointWebMercator point3 = new PointWebMercator( 0.7,  1. );
        double expected3 = 256*2*1;
        double actual3 = point3.yAtZoomLevel(1);


        assertEquals(expected3 , actual3 , 10e-10);
        PointWebMercator point4 = new PointWebMercator( 0.7,  .7 );
        double expected4 = 256*0.7;
        double actual4 = point4.yAtZoomLevel(0);
        assertEquals(expected4 , actual4 , 10e-10);
    }


    @Test
    void toPointReturnsNull(){
        PointWebMercator point = new PointWebMercator(0.3, 0.7);
        assertEquals( point.toPointCh() , null );

        PointWebMercator point2 = new PointWebMercator(0.7, 0.3);
        assertEquals( point2.toPointCh() , null );
    }
}