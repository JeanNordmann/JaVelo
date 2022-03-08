package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GraphNodesTest {

    //TODO faire les 4 tests par classe
    @Test
    void graphNodeWorkOnBasicValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_764_547 << 4,
                1_258_478 << 4,
                0x3_000_1823,
                2_764_547 << 4,
                1_258_478 << 4,
                0x3_000_1831
        });

        GraphNodes ns = new GraphNodes(b);
        assertEquals(2, ns.count());
        assertEquals(2_764_547, ns.nodeE(0));
        assertEquals(1_258_478, ns.nodeN(0));
        assertEquals(3, ns.outDegree(0));
        assertEquals(0x1823, ns.edgeId(0, 0));
        assertEquals(0x1824, ns.edgeId(0, 1));
        assertEquals(0x1825, ns.edgeId(0, 2));

        assertEquals(0x1832, ns.edgeId(1, 1));
        assertEquals(0x1833, ns.edgeId(1, 2));

        IntBuffer c = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns2 = new GraphNodes(c);
        assertEquals(1, ns2.count());
        assertEquals(2_600_000, ns2.nodeE(0));
        assertEquals(1_200_000, ns2.nodeN(0));
        assertEquals(2, ns2.outDegree(0));
        assertEquals(0x1234, ns2.edgeId(0, 0));
        assertEquals(0x1235, ns2.edgeId(0, 1));

    }

    @Test
    void graphNodeWorkOnTrivialValue() {
        IntBuffer a = IntBuffer.wrap(new int[]{
        });
        GraphNodes gn = new GraphNodes(a);
        assertEquals(0,gn.count());
        assertThrows(IndexOutOfBoundsException.class, ()-> {
            double t = gn.nodeN(0);
        });
    }


    @Test
    void edgeIdTestOnLimitValues() {
        IntBuffer a = IntBuffer.wrap(new int[]{

        });

    }

    //Test inutil car ce n'est pas à nous de gérer le cas ou le buffer est invalide
    //le buffer doit avoir un nbr avec des multiples de 3

    @Test
    void GraphNodesOnInvalideBuffer() {
        IntBuffer a = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_FFF_1234,
                1_200_000 << 4

        });
        GraphNodes gna = new GraphNodes(a);
        assertEquals(1,gna.count());

        assertEquals(1_200_000,gna.nodeE(1));
        assertThrows(IndexOutOfBoundsException.class, ()-> {
            double t = gna.nodeN(1);
        });

        // assertEquals(2_600_000,gna.nodeN(1));
        // ne marche pas car cette valeur n'existe pas car le tableau a 4 valeurs
        // (le noeud d'indice 1 n'est pas complet car le buffer est invalide)
    }
}
