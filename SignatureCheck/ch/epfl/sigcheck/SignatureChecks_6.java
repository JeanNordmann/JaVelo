package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est
// pas destiné à être exécuté. Son seul but est de vérifier, autant que
// possible, que les noms et les types des différentes entités à définir
// pour cette étape du projet sont corrects.
//TODO decommenter signcheck 6
final class SignatureChecks_6 {
    private SignatureChecks_6() {}

    void checkMultiRoute() throws Exception {
        v01 = new ch.epfl.javelo.routing.MultiRoute(v02);
        v03 = v01.edges();
        v04 = v01.elevationAt(v04);
        v05 = v01.indexOfSegmentAt(v04);
        v04 = v01.length();
        v05 = v01.nodeClosestTo(v04);
        v06 = v01.pointAt(v04);
        v07 = v01.pointClosestTo(v06);
        v08 = v01.points();
    }

    void checkRouteComputer() throws Exception {
        v09 = new ch.epfl.javelo.routing.RouteComputer(v10, v11);
        v12 = v09.bestRouteBetween(v05, v05);
    }

    ch.epfl.javelo.routing.MultiRoute v01;
    java.util.List<ch.epfl.javelo.routing.Route> v02;
    java.util.List<ch.epfl.javelo.routing.Edge> v03;
    double v04;
    int v05;
    ch.epfl.javelo.projection.PointCh v06;
    ch.epfl.javelo.routing.RoutePoint v07;
    java.util.List<ch.epfl.javelo.projection.PointCh> v08;
    ch.epfl.javelo.routing.RouteComputer v09;
    ch.epfl.javelo.data.Graph v10;
    ch.epfl.javelo.routing.CostFunction v11;
    ch.epfl.javelo.routing.Route v12;
}
