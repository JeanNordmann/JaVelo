package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est
// pas destiné à être exécuté. Son seul but est de vérifier, autant que
// possible, que les noms et les types des différentes entités à définir
// pour cette étape du projet sont corrects.

final class SignatureChecks_4 {
    private SignatureChecks_4() {}

    void checkGraph() throws Exception {
        v01 = new ch.epfl.javelo.data.Graph(v02, v03, v04, v05);
        v01 = ch.epfl.javelo.data.Graph.loadFrom(v06);
        v08 = v01.edgeAttributes(v07);
        v09 = v01.edgeElevationGain(v07);
        v10 = v01.edgeIsInverted(v07);
        v09 = v01.edgeLength(v07);
        v11 = v01.edgeProfile(v07);
        v07 = v01.edgeTargetNodeId(v07);
        v07 = v01.nodeClosestTo(v12, v09);
        v07 = v01.nodeCount();
        v07 = v01.nodeOutDegree(v07);
        v07 = v01.nodeOutEdgeId(v07, v07);
        v12 = v01.nodePoint(v07);
    }

    void checkElevationProfile() throws Exception {
        v13 = new ch.epfl.javelo.routing.ElevationProfile(v09, v14);
        v09 = v13.elevationAt(v09);
        v09 = v13.length();
        v09 = v13.maxElevation();
        v09 = v13.minElevation();
        v09 = v13.totalAscent();
        v09 = v13.totalDescent();
    }

    void checkEdge() throws Exception {
        v15 = new ch.epfl.javelo.routing.Edge(v07, v07, v12, v12, v09, v11);
        v15 = ch.epfl.javelo.routing.Edge.of(v01, v07, v07, v07);
        v09 = v15.elevationAt(v09);
        v10 = v15.equals(v16);
        v07 = v15.fromNodeId();
        v12 = v15.fromPoint();
        v07 = v15.hashCode();
        v09 = v15.length();
        v12 = v15.pointAt(v09);
        v09 = v15.positionClosestTo(v12);
        v11 = v15.profile();
        v07 = v15.toNodeId();
        v12 = v15.toPoint();
        v17 = v15.toString();
    }

    void checkRoute() throws Exception {
        v19 = v18.edges();
        v09 = v18.elevationAt(v09);
        v07 = v18.indexOfSegmentAt(v09);
        v09 = v18.length();
        v07 = v18.nodeClosestTo(v09);
        v12 = v18.pointAt(v09);
        v20 = v18.pointClosestTo(v12);
        v21 = v18.points();
    }

    void checkRoutePoint() throws Exception {
        v20 = new ch.epfl.javelo.routing.RoutePoint(v12, v09, v09);
        v20 = ch.epfl.javelo.routing.RoutePoint.NONE;
        v09 = v20.distanceToReference();
        v10 = v20.equals(v16);
        v07 = v20.hashCode();
        v20 = v20.min(v20);
        v20 = v20.min(v12, v09, v09);
        v12 = v20.point();
        v09 = v20.position();
        v17 = v20.toString();
        v20 = v20.withPositionShiftedBy(v09);
    }

    ch.epfl.javelo.data.Graph v01;
    ch.epfl.javelo.data.GraphNodes v02;
    ch.epfl.javelo.data.GraphSectors v03;
    ch.epfl.javelo.data.GraphEdges v04;
    java.util.List<ch.epfl.javelo.data.AttributeSet> v05;
    java.nio.file.Path v06;
    int v07;
    ch.epfl.javelo.data.AttributeSet v08;
    double v09;
    boolean v10;
    java.util.function.DoubleUnaryOperator v11;
    ch.epfl.javelo.projection.PointCh v12;
    ch.epfl.javelo.routing.ElevationProfile v13;
    float[] v14;
    ch.epfl.javelo.routing.Edge v15;
    java.lang.Object v16;
    java.lang.String v17;
    ch.epfl.javelo.routing.Route v18;
    java.util.List<ch.epfl.javelo.routing.Edge> v19;
    ch.epfl.javelo.routing.RoutePoint v20;
    java.util.List<ch.epfl.javelo.projection.PointCh> v21;
}
