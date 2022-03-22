package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.AttributeSet;
import ch.epfl.javelo.data.Graph;

import java.util.Optional;

import static ch.epfl.javelo.data.Attribute.*;

// Strongly inspired by brouter's "trekking" profile:
// https://github.com/abrensch/brouter/blob/15e84c81ea23408abde8605bd57a87a777003ce2/misc/profiles2/trekking.brf

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public record CityBikeCF(Graph graph) implements CostFunction {
    // Any kind of marked cycle route (international, national, regional or local)
    private static final AttributeSet CYCLE_ROUTE =
            AttributeSet.of(ICN_YES, NCN_YES, RCN_YES, LCN_YES);

    private static final AttributeSet BIKE =
            AttributeSet.of(BICYCLE_YES, BICYCLE_DESIGNATED, BICYCLE_PERMISSIVE);

    // Tags indicating that access by bike is allowed/forbidden
    private static final AttributeSet BIKE_ACCESS_ALLOWED =
            AttributeSet.of(BICYCLE_YES, BICYCLE_DESIGNATED, BICYCLE_PERMISSIVE, BICYCLE_DISMOUNT);
    private static final AttributeSet BIKE_ACCESS_FORBIDDEN =
            AttributeSet.of(BICYCLE_NO, BICYCLE_USE_SIDEPATH, BICYCLE_PRIVATE);

    // Tags indicating that access by any kind of vehicle is forbidden
    private static final AttributeSet VEHICLE_ACCESS_FORBIDDEN =
            AttributeSet.of(VEHICLE_NO, VEHICLE_PRIVATE);

    // Tags indicating that access by public is allowed/forbidden
    private static final AttributeSet ACCESS_ALLOWED =
            AttributeSet.of(ACCESS_YES, ACCESS_PERMISSIVE);
    private static final AttributeSet ACCESS_FORBIDDEN =
            AttributeSet.of(ACCESS_NO, ACCESS_PRIVATE);

    // Tags explicitly or implicitly indicating that a street is one-way.
    private static final AttributeSet ONEWAY_FORWARD =
            AttributeSet.of(ONEWAY_YES, ONEWAY_BICYCLE_YES);

    // Tags indicating that cyclists can travel against traffic along a one-way street.
    private static final AttributeSet ONEWAY_DOES_NOT_APPLY_TO_CYCLISTS =
            AttributeSet.of(ONEWAY_BICYCLE_NO, CYCLEWAY_OPPOSITE, CYCLEWAY_OPPOSITE_LANE, CYCLEWAY_OPPOSITE_TRACK);

    // Tags indicating some kind of residential street.
    private static final AttributeSet RESIDENTIAL_STREET =
            AttributeSet.of(HIGHWAY_RESIDENTIAL, HIGHWAY_LIVING_STREET);

    private static final AttributeSet PAVED_SURFACE = AttributeSet.of(
            SURFACE_PAVED, SURFACE_ASPHALT, SURFACE_CONCRETE, SURFACE_PAVING_STONES, SURFACE_SETT);
    private static final AttributeSet UNPAVED_SURFACE = AttributeSet.of(
            SURFACE_UNPAVED, SURFACE_GRAVEL, SURFACE_GROUND, SURFACE_COMPACTED, SURFACE_GRASS,
            SURFACE_DIRT, SURFACE_FINE_GRAVEL, SURFACE_PEBBLESTONE, SURFACE_WOOD, SURFACE_SAND,
            SURFACE_COBBLESTONE);

    private static final AttributeSet TRACKLIKE =
            AttributeSet.of(HIGHWAY_TRACK, HIGHWAY_ROAD, HIGHWAY_PATH, HIGHWAY_FOOTWAY);

    // Ternary logic
    private static final Optional<Boolean> TRUE = Optional.of(Boolean.TRUE);
    private static final Optional<Boolean> FALSE = Optional.of(Boolean.FALSE);
    private static final Optional<Boolean> UNKNOWN = Optional.empty();

    @Override
    public double costFactor(int nodeId, int edgeId) {
        var edgeAttributes = graph.edgeAttributes(edgeId);

        // Exclude motorways.
        if (edgeAttributes.contains(HIGHWAY_MOTORWAY)) return Double.POSITIVE_INFINITY;

        // Exclude forbidden one-way streets.
        var isInverted = graph.edgeIsInverted(edgeId);
        var wrongOneWay = isInverted
                ? edgeAttributes.intersects(ONEWAY_FORWARD)
                : edgeAttributes.contains(ONEWAY_M1);
        if (wrongOneWay && !edgeAttributes.intersects(ONEWAY_DOES_NOT_APPLY_TO_CYCLISTS))
            return Double.POSITIVE_INFINITY;

        // Penalize steps, as the bike has to be carried.
        if (edgeAttributes.contains(HIGHWAY_STEPS)) return 40d;

        var isCycleRoute = edgeAttributes.intersects(CYCLE_ROUTE);

        // Check that the edge is accessible
        var isAccessible = isCycleRoute ||
                isAccessibleByBike(edgeAttributes)
                        .or(() -> isAccessibleByVehicle(edgeAttributes))
                        .or(() -> isAccessible(edgeAttributes))
                        .orElse(true);
        if (!isAccessible) return Double.POSITIVE_INFINITY;

        // Marked cycle routes are always considered as perfect, anything else is worse.
        var flatCost = isCycleRoute
                ? 1d
                : 0.05 + nonCycleRouteCostFactor(edgeAttributes);

        var averageUpSlope = graph.edgeElevationGain(edgeId) / graph.edgeLength(edgeId);
        if (averageUpSlope < 0.01) return flatCost;
        else if (averageUpSlope < 0.03) return flatCost * 1.2;
        else if (averageUpSlope < 0.05) return flatCost * 1.4;
        else if (averageUpSlope < 0.10) return flatCost * 1.8;
        else return flatCost * 2.6;
    }

    private Optional<Boolean> isAccessibleByBike(AttributeSet edgeAttributes) {
        if (edgeAttributes.intersects(BIKE_ACCESS_ALLOWED)) return TRUE;
        if (edgeAttributes.intersects(BIKE_ACCESS_FORBIDDEN)) return FALSE;
        return UNKNOWN;
    }

    private Optional<Boolean> isAccessibleByVehicle(AttributeSet edgeAttributes) {
        return edgeAttributes.intersects(VEHICLE_ACCESS_FORBIDDEN) ? FALSE : UNKNOWN;
    }

    private Optional<Boolean> isAccessible(AttributeSet edgeAttributes) {
        if (edgeAttributes.intersects(ACCESS_ALLOWED)) return TRUE;
        if (edgeAttributes.intersects(ACCESS_FORBIDDEN)) return FALSE;
        if (edgeAttributes.contains(MOTORROAD_YES)) return FALSE;
        return UNKNOWN;
    }

    private double nonCycleRouteCostFactor(AttributeSet edgeAttributes) {
        if (edgeAttributes.contains(HIGHWAY_PEDESTRIAN)) return 3;
        if (edgeAttributes.contains(HIGHWAY_CYCLEWAY)) return 1;

        // True iff we're sure the edge is unpaved.
        var isUnpaved = edgeAttributes.intersects(UNPAVED_SURFACE);

        if (edgeAttributes.intersects(RESIDENTIAL_STREET)) return isUnpaved ? 1.5 : 1.1;
        if (edgeAttributes.contains(HIGHWAY_SERVICE)) return isUnpaved ? 1.6 : 1.3;

        // True iff the edge is designated for bicycle use.
        var isBike = edgeAttributes.intersects(BIKE);

        // Main roads
        if (edgeAttributes.contains(HIGHWAY_TRUNK)) return isBike ? 1.5 : 10;
        if (edgeAttributes.contains(HIGHWAY_PRIMARY)) return isBike ? 1.2 : 3;
        if (edgeAttributes.contains(HIGHWAY_SECONDARY)) return isBike ? 1.1 : 1.6;
        if (edgeAttributes.contains(HIGHWAY_TERTIARY)) return isBike ? 1.0 : 1.4;
        if (edgeAttributes.contains(HIGHWAY_UNCLASSIFIED)) return isBike ? 1.0 : 1.3;

        // Tracks, paths, etc.
        if (edgeAttributes.intersects(TRACKLIKE)) {
            // True iff we're sure the edge is paved.
            var isPaved = edgeAttributes.intersects(PAVED_SURFACE);
            // True iff the edge is probably good to ride on.
            var probablyGood = isPaved
                    || (!isUnpaved && (isBike || edgeAttributes.contains(HIGHWAY_FOOTWAY)));

            if (edgeAttributes.contains(TRACKTYPE_GRADE1)) return probablyGood ? 1.0 : 1.3;
            if (edgeAttributes.contains(TRACKTYPE_GRADE2)) return probablyGood ? 1.1 : 2.0;
            if (edgeAttributes.contains(TRACKTYPE_GRADE3)) return probablyGood ? 1.5 : 3.0;
            if (edgeAttributes.contains(TRACKTYPE_GRADE4)) return probablyGood ? 2.0 : 5.0;
            if (edgeAttributes.contains(TRACKTYPE_GRADE5)) return probablyGood ? 3.0 : 5.0;
            return probablyGood ? 1.0 : 5.0;
        }

        return 2;
    }
}
