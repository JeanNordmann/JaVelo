package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;
import ch.epfl.javelo.projection.PointWebMercator.*;

import static ch.epfl.javelo.projection.PointWebMercator.of;
import static org.junit.jupiter.api.Assertions.*;

public class PointWebMercatorTest {

    public static final double DELTA = 1e-5;
    PointCh pCh = new PointCh(Ch1903.e(Math.toRadians(6.5790772), Math.toRadians(46.5218976)), Ch1903.n(Math.toRadians(6.579077), Math.toRadians(46.5218976)));
    PointWebMercator pW = PointWebMercator.ofPointCh(pCh);

    @Test
    void ofPointChWorksOnNonTrivial1() {
        assertEquals(Math.toRadians(6.5790772), (PointWebMercator.ofPointCh(pCh)).lon(), DELTA);
        assertEquals(Math.toRadians(46.5218976), (PointWebMercator.ofPointCh(pCh)).lat(), DELTA);
    }


    @Test
    void suce() {
        PointCh PCHexcepted = new PointCh(Ch1903.e(Math.toRadians(6.5790772), Math.toRadians(46.5218976)), Ch1903.n(Math.toRadians(6.579077), Math.toRadians(46.5218976)));
        assertEquals(PCHexcepted.lat(), pW.toPointCh().lat(), DELTA);
        assertEquals(PCHexcepted.lon(), pW.toPointCh().lon(), DELTA);
    }

    @Test
    void putedeZoomLevel() {
        PointWebMercator p = of(19,69561772 , 47468099);
        PointWebMercator pp = new PointWebMercator(0.518275214444,0.3536648947499);
        double expectedX = 0.518275214444;
        double expectedY = 0.3536648947499;
        double at19x = 69561772;
        double at19y = 47468099;
        assertEquals(pp.lat(), p.lat(),DELTA);
        assertEquals(pp.lon(), p.lon(),DELTA);
        assertEquals(at19x, pp.xAtZoomLevel(19),100);
        assertEquals(at19y, pp.yAtZoomLevel(19),100);
    }

}
