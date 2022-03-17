package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.List;

public final class SingleRoute implements Route{

    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(!edges.isEmpty());

    }

    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    @Override
    public double length() {
        return 0;
    }

    @Override
    public List<Edge> edges() {
        return null;
    }

    @Override
    public List<PointCh> points() {
        return null;
    }

    @Override
    public PointCh pointAt(double position) {
        return null;
    }

    @Override
    public double elevationAt(double position) {
        return 0;
    }

    @Override
    public int nodeClosestTo(double position) {
        return 0;
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        return null;
    }
}
