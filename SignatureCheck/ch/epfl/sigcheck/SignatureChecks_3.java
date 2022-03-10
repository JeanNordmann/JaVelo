package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est
// pas destiné à être exécuté. Son seul but est de vérifier, autant que
// possible, que les noms et les types des différentes entités à définir
// pour cette étape du projet sont corrects.

final class SignatureChecks_3 {
    private SignatureChecks_3() {}

    void checkGraphNodes() throws Exception {
        v01 = new ch.epfl.javelo.data.GraphNodes(v02);
        v02 = v01.buffer();
        v03 = v01.count();
        v03 = v01.edgeId(v03, v03);
        v05 = v01.equals(v04);
        v03 = v01.hashCode();
        v06 = v01.nodeE(v03);
        v06 = v01.nodeN(v03);
        v03 = v01.outDegree(v03);
        v07 = v01.toString();
    }

    void checkGraphSectors() throws Exception {
        v08 = new ch.epfl.javelo.data.GraphSectors(v09);
        v09 = v08.buffer();
        v05 = v08.equals(v04);
        v03 = v08.hashCode();
        v11 = v08.sectorsInArea(v10, v06);
        v07 = v08.toString();
    }

    void checkSector() throws Exception {
        v12 = new ch.epfl.javelo.data.GraphSectors.Sector(v03, v03);
        v03 = v12.endNodeId();
        v05 = v12.equals(v04);
        v03 = v12.hashCode();
        v03 = v12.startNodeId();
        v07 = v12.toString();
    }

    void checkGraphEdges() throws Exception {
        v13 = new ch.epfl.javelo.data.GraphEdges(v09, v02, v14);
        v03 = v13.attributesIndex(v03);
        v09 = v13.edgesBuffer();
        v06 = v13.elevationGain(v03);
        v14 = v13.elevations();
        v05 = v13.equals(v04);
        v05 = v13.hasProfile(v03);
        v03 = v13.hashCode();
        v05 = v13.isInverted(v03);
        v06 = v13.length(v03);
        v02 = v13.profileIds();
        v15 = v13.profileSamples(v03);
        v03 = v13.targetNodeId(v03);
        v07 = v13.toString();
    }

    ch.epfl.javelo.data.GraphNodes v01;
    java.nio.IntBuffer v02;
    int v03;
    java.lang.Object v04;
    boolean v05;
    double v06;
    java.lang.String v07;
    ch.epfl.javelo.data.GraphSectors v08;
    java.nio.ByteBuffer v09;
    ch.epfl.javelo.projection.PointCh v10;
    java.util.List<ch.epfl.javelo.data.GraphSectors.Sector> v11;
    ch.epfl.javelo.data.GraphSectors.Sector v12;
    ch.epfl.javelo.data.GraphEdges v13;
    java.nio.ShortBuffer v14;
    float[] v15;
}
