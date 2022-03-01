package ch.epfl.javelo.data;

import java.util.List;

/**
 * 2.3.6 Enum fourni dans une archives
 */
public enum Attribute {
    // See https://wiki.openstreetmap.org/wiki/Map_features
    // and https://taginfo.openstreetmap.ch/

    // Highways (https://wiki.openstreetmap.org/wiki/Highways)
    HIGHWAY_SERVICE("highway", "service"),
    HIGHWAY_TRACK("highway", "track"),
    HIGHWAY_RESIDENTIAL("highway", "residential"),
    HIGHWAY_FOOTWAY("highway", "footway"),
    HIGHWAY_PATH("highway", "path"),
    HIGHWAY_UNCLASSIFIED("highway", "unclassified"),
    HIGHWAY_TERTIARY("highway", "tertiary"),
    HIGHWAY_SECONDARY("highway", "secondary"),
    HIGHWAY_STEPS("highway", "steps"),
    HIGHWAY_PRIMARY("highway", "primary"),
    HIGHWAY_CYCLEWAY("highway", "cycleway"),
    HIGHWAY_MOTORWAY("highway","motorway"),
    HIGHWAY_PEDESTRIAN("highway", "pedestrian"),
    HIGHWAY_TRUNK("highway", "trunk"),
    HIGHWAY_LIVING_STREET("highway", "living_street"),
    HIGHWAY_ROAD("highway", "road"),

    // Roads with motorway-like restrictions (https://wiki.openstreetmap.org/wiki/Key%3Amotorroad)
    MOTORROAD_YES("motorroad", "yes"),

    // Track type (https://wiki.openstreetmap.org/wiki/Key%3Atracktype)
    TRACKTYPE_GRADE1("tracktype", "grade1"),
    TRACKTYPE_GRADE2("tracktype", "grade2"),
    TRACKTYPE_GRADE3("tracktype", "grade3"),
    TRACKTYPE_GRADE4("tracktype", "grade4"),
    TRACKTYPE_GRADE5("tracktype", "grade5"),

    // Surface (https://wiki.openstreetmap.org/wiki/Key%3Asurface)
    SURFACE_ASPHALT("surface", "asphalt"),
    SURFACE_UNPAVED("surface", "unpaved"),
    SURFACE_GRAVEL("surface", "gravel"),
    SURFACE_PAVED("surface", "paved"),
    SURFACE_GROUND("surface", "ground"),
    SURFACE_CONCRETE("surface", "concrete"),
    SURFACE_COMPACTED("surface", "compacted"),
    SURFACE_PAVING_STONES("surface", "paving_stones"),
    SURFACE_GRASS("surface", "grass"),
    SURFACE_DIRT("surface", "dirt"),
    SURFACE_FINE_GRAVEL("surface", "fine_gravel"),
    SURFACE_PEBBLESTONE("surface", "pebblestone"),
    SURFACE_SETT("surface", "sett"),
    SURFACE_WOOD("surface", "wood"),
    SURFACE_SAND("surface", "sand"),
    SURFACE_COBBLESTONE("surface", "cobblestone"),

    // One-way roads (https://wiki.openstreetmap.org/wiki/Key%3Aoneway)
    ONEWAY_YES("oneway", "yes"),
    ONEWAY_M1("oneway", "-1"),
    ONEWAY_BICYCLE_YES("oneway:bicycle", "yes"),
    ONEWAY_BICYCLE_NO("oneway:bicycle", "no"),

    // Vehicle access (https://wiki.openstreetmap.org/wiki/Key%3Avehicle)
    VEHICLE_NO("vehicle", "no"),
    VEHICLE_PRIVATE("vehicle", "private"),

    // General access (https://wiki.openstreetmap.org/wiki/Key%3Aaccess)
    ACCESS_YES("access", "yes"),
    ACCESS_NO("access", "no"),
    ACCESS_PRIVATE("access", "private"),
    ACCESS_PERMISSIVE("access", "permissive"),

    // Bicycle lanes (https://wiki.openstreetmap.org/wiki/Key%3Acycleway)
    CYCLEWAY_OPPOSITE("cycleway", "opposite"),
    CYCLEWAY_OPPOSITE_LANE("cycleway", "opposite_lane"),
    CYCLEWAY_OPPOSITE_TRACK("cycleway", "opposite_track"),

    // Bicycle access (https://wiki.openstreetmap.org/wiki/Key%3Abicycle)
    BICYCLE_YES("bicycle", "yes"),
    BICYCLE_NO("bicycle", "no"),
    BICYCLE_DESIGNATED("bicycle", "designated"),
    BICYCLE_DISMOUNT("bicycle", "dismount"),
    BICYCLE_USE_SIDEPATH("bicycle", "use_sidepath"),
    BICYCLE_PERMISSIVE("bicycle", "permissive"),
    BICYCLE_PRIVATE("bicycle", "private"),

    // Bicycle route (see https://wiki.openstreetmap.org/wiki/Cycle_routes)
    ICN_YES("icn", "yes"),
    NCN_YES("ncn", "yes"),
    RCN_YES("rcn", "yes"),
    LCN_YES("lcn", "yes");

    public static final List<Attribute> ALL = List.of(values());
    public static final int COUNT = ALL.size();

    private final String key;
    private final String value;
    private final String keyValue;

    Attribute(String key, String value) {
        this.key = key;
        this.value = value;
        this.keyValue = key + "=" + value;
    }

    public String key() {
        return key;
    }

    public String value() {
        return value;
    }

    public String keyValue() {
        return keyValue;
    }

    @Override
    public String toString() {
        return keyValue;
    }
}