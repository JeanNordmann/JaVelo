package ch.epfl.javelo.projection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class WebMercatorTest {

    /**
     * The x function was written by Julian
     * These tests were written by Youssef and expected results were computed using python
     */
    @Test
    void xWorksOnRandomValues(){
        double expected0 = -0.2957747154594767;
        double actual0 = WebMercator.x(-5.0) ;
        assertEquals(expected0,actual0,10e-10) ;
        double expected1 = -0.11893588980181521;
        double actual1 = WebMercator.x(-3.888888888888889) ;
        assertEquals(expected1,actual1,10e-10) ;
        double expected2 = 0.05790293585584629;
        double actual2 = WebMercator.x(-2.7777777777777777) ;
        assertEquals(expected2,actual2,10e-10) ;
        double expected3 = 0.23474176151350779;
        double actual3 = WebMercator.x(-1.6666666666666665) ;
        assertEquals(expected3,actual3,10e-10) ;
        double expected4 = 0.4115805871711693;
        double actual4 = WebMercator.x(-0.5555555555555554) ;
        assertEquals(expected4,actual4,10e-10) ;
        double expected5 = 0.5884194128288307;
        double actual5 = WebMercator.x(0.5555555555555554) ;
        assertEquals(expected5,actual5,10e-10) ;
        double expected6 = 0.7652582384864923;
        double actual6 = WebMercator.x(1.666666666666667) ;
        assertEquals(expected6,actual6,10e-10) ;
        double expected7 = 0.9420970641441538;
        double actual7 = WebMercator.x(2.7777777777777786) ;
        assertEquals(expected7,actual7,10e-10) ;
        double expected8 = 1.1189358898018154;
        double actual8 = WebMercator.x(3.8888888888888893) ;
        assertEquals(expected8,actual8,10e-10) ;
        double expected9 = 1.2957747154594768;
        double actual9 = WebMercator.x(5.0) ;
        assertEquals(expected9,actual9,10e-10) ;
    }

    // check edge cases
    @Test
    void xWorksOnInfinities(){
        double expected1 = Double.POSITIVE_INFINITY;
        double actual1 = WebMercator.x(Double.POSITIVE_INFINITY ) ;
        assertEquals(Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,10e-10) ;

        double expected2 = Double.NEGATIVE_INFINITY;
        double actual2 = WebMercator.x(Double.NEGATIVE_INFINITY ) ;
        assertEquals(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,10e-10) ;
    }

    @Test
    void yWorksOnRandomVariables(){
        double expected0 = 0.1924542846861648;
        double actual0 = WebMercator.y(-5.0) ;
        assertEquals(expected0,actual0,10e-10) ;
        double expected1 = 0.6318563803279579;
        double actual1 = WebMercator.y(-3.888888888888889) ;
        assertEquals(expected1,actual1,10e-10) ;
        double expected2 = 0.44077574917551526;
        double actual2 = WebMercator.y(-2.7777777777777777) ;
        assertEquals(expected2,actual2,10e-10) ;
        double expected3 = 0.016624240558015913;
        double actual3 = WebMercator.y(-1.6666666666666665) ;
        assertEquals(expected3,actual3,10e-10) ;
        double expected4 = 0.593353564282764;
        double actual4 = WebMercator.y(-0.5555555555555554) ;
        assertEquals(expected4,actual4,10e-10) ;
        double expected5 = 0.4066464357172361;
        double actual5 = WebMercator.y(0.5555555555555554) ;
        assertEquals(expected5,actual5,10e-10) ;
        double expected6 = 0.9833757594419833;
        double actual6 = WebMercator.y(1.666666666666667) ;
        assertEquals(expected6,actual6,10e-10) ;
        double expected7 = 0.5592242508244846;
        double actual7 = WebMercator.y(2.7777777777777786) ;
        assertEquals(expected7,actual7,10e-10) ;
        double expected8 = 0.368143619672042;
        double actual8 = WebMercator.y(3.8888888888888893) ;
        assertEquals(expected8,actual8,10e-10) ;
        double expected9 = 0.8075457153138352;
        double actual9 = WebMercator.y(5.0) ;
        assertEquals(expected9,actual9,10e-10) ;

    }


    @Test
    void lonWorksOnKnownValues(){
        double expected0 = -3.141592653589793;
        double actual0 = WebMercator.lon(0.0) ;
        assertEquals(expected0,actual0,10e-10) ;
        double expected1 = -0.8267349088394194;
        double actual1 = WebMercator.lon(0.3684210526315789) ;
        assertEquals(expected1,actual1,10e-10) ;
        double expected2 = 1.4881228359109544;
        double actual2 = WebMercator.lon(0.7368421052631579) ;
        assertEquals(expected2,actual2,10e-10) ;
        double expected3 = 3.8029805806613277;
        double actual3 = WebMercator.lon(1.1052631578947367) ;
        assertEquals(expected3,actual3,10e-10) ;
        double expected4 = 6.117838325411702;
        double actual4 = WebMercator.lon(1.4736842105263157) ;
        assertEquals(expected4,actual4,10e-10) ;
        double expected5 = 8.432696070162075;
        double actual5 = WebMercator.lon(1.8421052631578947) ;
        assertEquals(expected5,actual5,10e-10) ;
        double expected6 = 10.747553814912449;
        double actual6 = WebMercator.lon(2.2105263157894735) ;
        assertEquals(expected6,actual6,10e-10) ;
        double expected7 = 13.062411559662824;
        double actual7 = WebMercator.lon(2.5789473684210527) ;
        assertEquals(expected7,actual7,10e-10) ;
        double expected8 = 15.377269304413197;
        double actual8 = WebMercator.lon(2.9473684210526314) ;
        assertEquals(expected8,actual8,10e-10) ;
        double expected9 = 17.69212704916357;
        double actual9 = WebMercator.lon(3.31578947368421) ;
        assertEquals(expected9,actual9,10e-10) ;
        double expected10 = 20.006984793913944;
        double actual10 = WebMercator.lon(3.6842105263157894) ;
        assertEquals(expected10,actual10,10e-10) ;
        double expected11 = 22.321842538664317;
        double actual11 = WebMercator.lon(4.052631578947368) ;
        assertEquals(expected11,actual11,10e-10) ;
        double expected12 = 24.63670028341469;
        double actual12 = WebMercator.lon(4.421052631578947) ;
        assertEquals(expected12,actual12,10e-10) ;
        double expected13 = 26.951558028165064;
        double actual13 = WebMercator.lon(4.789473684210526) ;
        assertEquals(expected13,actual13,10e-10) ;
        double expected14 = 29.26641577291544;
        double actual14 = WebMercator.lon(5.157894736842105) ;
        assertEquals(expected14,actual14,10e-10) ;
        double expected15 = 31.581273517665814;
        double actual15 = WebMercator.lon(5.526315789473684) ;
        assertEquals(expected15,actual15,10e-10) ;
        double expected16 = 33.89613126241618;
        double actual16 = WebMercator.lon(5.894736842105263) ;
        assertEquals(expected16,actual16,10e-10) ;
        double expected17 = 36.21098900716656;
        double actual17 = WebMercator.lon(6.263157894736842) ;
        assertEquals(expected17,actual17,10e-10) ;
        double expected18 = 38.52584675191693;
        double actual18 = WebMercator.lon(6.63157894736842) ;
        assertEquals(expected18,actual18,10e-10) ;
        double expected19 = 40.840704496667314;
        double actual19 = WebMercator.lon(7.0) ;
        assertEquals(expected19,actual19,10e-10) ;
    }
    @Test
    void latWorksOnKnowValue(){
        double expected0 = 1.4844222297453324;
        double actual0 = WebMercator.lat(0.0) ;
        assertEquals(expected0,actual0,10e-10) ;
        double expected1 = 0.7460168171918116;
        double actual1 = WebMercator.lat(0.3684210526315789) ;
        assertEquals(expected1,actual1,10e-10) ;
        double expected2 = -1.1266521945860553;
        double actual2 = WebMercator.lat(0.7368421052631579) ;
        assertEquals(expected2,actual2,10e-10) ;
        double expected3 = -1.5261953353297661;
        double actual3 = WebMercator.lat(1.1052631578947367) ;
        assertEquals(expected3,actual3,10e-10) ;
        double expected4 = -1.5663899070253988;
        double actual4 = WebMercator.lat(1.4736842105263157) ;
        assertEquals(expected4,actual4,10e-10) ;
        double expected5 = -1.5703610589171033;
        double actual5 = WebMercator.lat(1.8421052631578947) ;
        assertEquals(expected5,actual5,10e-10) ;
        double expected6 = -1.5707533309309638;
        double actual6 = WebMercator.lat(2.2105263157894735) ;
        assertEquals(expected6,actual6,10e-10) ;
        double expected7 = -1.5707920796533523;
        double actual7 = WebMercator.lat(2.5789473684210527) ;
        assertEquals(expected7,actual7,10e-10) ;
        double expected8 = -1.5707959072612567;
        double actual8 = WebMercator.lat(2.9473684210526314) ;
        assertEquals(expected8,actual8,10e-10) ;
        double expected9 = -1.570796285353265;
        double actual9 = WebMercator.lat(3.31578947368421) ;
        assertEquals(expected9,actual9,10e-10) ;
        double expected10 = -1.5707963227012824;
        double actual10 = WebMercator.lat(3.6842105263157894) ;
        assertEquals(expected10,actual10,10e-10) ;
        double expected11 = -1.5707963263905285;
        double actual11 = WebMercator.lat(4.052631578947368) ;
        assertEquals(expected11,actual11,10e-10) ;
        double expected12 = -1.570796326754953;
        double actual12 = WebMercator.lat(4.421052631578947) ;
        assertEquals(expected12,actual12,10e-10) ;
        double expected13 = -1.570796326790951;
        double actual13 = WebMercator.lat(4.789473684210526) ;
        assertEquals(expected13,actual13,10e-10) ;
        double expected14 = -1.5707963267945069;
        double actual14 = WebMercator.lat(5.157894736842105) ;
        assertEquals(expected14,actual14,10e-10) ;
        double expected15 = -1.5707963267948581;
        double actual15 = WebMercator.lat(5.526315789473684) ;
        assertEquals(expected15,actual15,10e-10) ;
        double expected16 = -1.5707963267948928;
        double actual16 = WebMercator.lat(5.894736842105263) ;
        assertEquals(expected16,actual16,10e-10) ;
        double expected17 = -1.5707963267948963;
        double actual17 = WebMercator.lat(6.263157894736842) ;
        assertEquals(expected17,actual17,10e-10) ;
        double expected18 = -1.5707963267948966;
        double actual18 = WebMercator.lat(6.63157894736842) ;
        assertEquals(expected18,actual18,10e-10) ;
        double expected19 = -1.5707963267948966;
        double actual19 = WebMercator.lat(7.0) ;
        assertEquals(expected19,actual19,10e-10) ;

    }
}
