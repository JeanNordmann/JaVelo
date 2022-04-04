package ch.epfl.javelo;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.Route;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Locale;

public final class KmlPrinter {
    private static final String KML_HEADER =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<kml xmlns=\"http://www.opengis.net/kml/2.2\"\n" +
                    "     xmlns:gx=\"http://www.google.com/kml/ext/2.2\">\n" +
                    "  <Document>\n" +
                    "    <name>JaVelo</name>\n" +
                    "    <Style id=\"byBikeStyle\">\n" +
                    "      <LineStyle>\n" +
                    "        <color>a00000ff</color>\n" +
                    "        <width>4</width>\n" +
                    "      </LineStyle>\n" +
                    "    </Style>\n" +
                    "    <Placemark>\n" +
                    "      <name>Path</name>\n" +
                    "      <styleUrl>#byBikeStyle</styleUrl>\n" +
                    "      <MultiGeometry>\n" +
                    "        <LineString>\n" +
                    "          <tessellate>1</tessellate>\n" +
                    "          <coordinates>";

    private static final String KML_FOOTER =
            "          </coordinates>\n" +
                    "        </LineString>\n" +
                    "      </MultiGeometry>\n" +
                    "    </Placemark>\n" +
                    "  </Document>\n" +
                    "</kml>";

    public static File write(String fileName, Route route) throws IOException {
        Path pa = Path.of("out", "routeExport", fileName);

        pa.toFile().getParentFile().mkdirs();
        pa.toFile().createNewFile();

        try (PrintWriter w = new PrintWriter(pa.toString())) {
            w.println(KML_HEADER);
            for (PointCh p : route.points())
                w.printf(Locale.ROOT,
                        "            %.5f,%.5f\n",
                        Math.toDegrees(p.lon()),
                        Math.toDegrees(p.lat()));
            w.println(KML_FOOTER);
        }

        return pa.toFile();
    }
}

