package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Q28_4Test {

    @Test
    void ofIntWorksWithZero(){
        int expected = 0 ;
        int actual = Q28_4.ofInt(0 );
        assertEquals(expected , actual );
    }

    @Test
    void ofIntWorksWithNegative(){
        int expected = ((1<<28)-1)<<4;
        int actual = Q28_4.ofInt(-1);
        assertEquals(expected , actual);
    }
    private static String toBinary(int x){
        String res = "";
        for(int i = 31 ; i >= 0 ; i-- ){
            if ( (x&(1<<i)) != 0  )
                res += '1';
            else res += '0';
        }
        return res ;
    }
    @Test
    void ofInitWorksWithKnownValues(){
        int actual = Q28_4.ofInt( 5);
        int expected = 0b0101_0000 ;
        assertEquals(expected , actual );
    }

    @Test

    void asDoubleWorksWithZero(){
        double expected = .0;
        double actual = Q28_4.asDouble(0);
        assertEquals( expected , actual );
    }

    @Test
    void asDoubleWorksWithKnownValues(){
        double expected1 = 2.5;
        double actual1 = Q28_4.asDouble(0b0010_1000);
        assertEquals( expected1 , actual1 , 10e-10);
        double expected2 = 6.625;
        double actual2 = Q28_4.asDouble(0b0110_1010);
        assertEquals( expected2 , actual2 , 10e-10);
    }
    @Test
    void asDoubleWorksWithNegativeValues(){
        double expected1 = -2.5;
        double actual1 = Q28_4.asDouble(((-3)<<4) + 8);
        assertEquals( expected1 , actual1 , 10e-10);
        double expected2 = -6.625;
        double actual2 = Q28_4.asDouble(((-7)<<4)+4+2 );
        assertEquals( expected2 , actual2 , 10e-10);
    }


    void asFloatWorksWithZero(){
        float expected = .0f;
        float actual = Q28_4.asFloat(0);
        assertEquals( expected , actual );
    }

    @Test
    void asFloatWorksWithKnownValues(){
        float expected1 = 2.5f;
        float actual1 = Q28_4.asFloat(0b0010_1000);
        assertEquals( expected1 , actual1 , 10e-10);
        float expected2 = 6.625f;
        float actual2 = Q28_4.asFloat(0b0110_1010);
        assertEquals( expected2 , actual2 , 10e-10);

        

    }

    @Test
    void asFloatWorksWithNegativeValues(){
        float expected1 = -2.5f;
        float actual1 = Q28_4.asFloat(((-3)<<4) + 8);
        assertEquals( expected1 , actual1 , 10e-10);
        float expected2 = -6.625f;
        float actual2 = Q28_4.asFloat(((-7)<<4)+4+2 );
        assertEquals( expected2 , actual2 , 10e-10);
    }

    @Test
    void trySomeValues() {
        System.out.println(Q28_4.asFloat((byte) 0b1111));
    }
}