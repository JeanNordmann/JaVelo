package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est
// pas destiné à être exécuté. Son seul but est de vérifier, autant que
// possible, que les noms et les types des différentes entités à définir
// pour cette étape du projet sont corrects.

final class SignatureChecks_5 {
    private SignatureChecks_5() {}

    void checkElevationProfileComputer() throws Exception {
        v03 = ch.epfl.javelo.routing.ElevationProfileComputer.elevationProfile(v01, v02);
    }

    void checkSingleRoute() throws Exception {
        v04 = new ch.epfl.javelo.routing.SingleRoute(v05);
        v05 = v04.edges();
        v02 = v04.elevationAt(v02);
        v06 = v04.indexOfSegmentAt(v02);
        v02 = v04.length();
        v06 = v04.nodeClosestTo(v02);
        v07 = v04.pointAt(v02);
        v08 = v04.pointClosestTo(v07);
        v09 = v04.points();
    }

    ch.epfl.javelo.routing.Route v01;
    double v02;
    ch.epfl.javelo.routing.ElevationProfile v03;
    ch.epfl.javelo.routing.SingleRoute v04;
    java.util.List<ch.epfl.javelo.routing.Edge> v05;
    int v06;
    ch.epfl.javelo.projection.PointCh v07;
    ch.epfl.javelo.routing.RoutePoint v08;
    java.util.List<ch.epfl.javelo.projection.PointCh> v09;
}
