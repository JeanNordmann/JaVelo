package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;

import java.io.IOException;
import java.nio.file.Path;

public class GpxGeneratorTest {
    public static void main(String[] args) throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        Route r = rc.bestRouteBetween(159049, 117669);
        ElevationProfile elevationProfile = ElevationProfileComputer.elevationProfile(r, 2);
        GpxGenerator.writeGpx("JEAN_LE_BG.gpx", r, elevationProfile);
    }
}