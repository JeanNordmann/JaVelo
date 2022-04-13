package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GpxGenerator {

    private GpxGenerator() {}

    public static Document createGpx(Route route, ElevationProfile elevationProfile) {
        Document doc = newDocument(); // voir plus bas

        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        List<PointCh> pointChList = route.points();

        for (PointCh pointCh : pointChList) {
            Element rtept = doc.createElement("rtept");
            rtept.setAttribute("lat", Double.toString(pointCh.lat()));
            rtept.setAttribute("lon", Double.toString(pointCh.lon()));
            Element ele = doc.createElement("ele");
            rtept.appendChild(ele);
            ele.setTextContent(Double.toString(elevationProfile.elevationAt(route.pointClosestTo(pointCh).position())));
        }
        return doc;
    }

    public static void writeGpx(String name, Route route, ElevationProfile elevationProfilef) throws IOException {
        //Document doc = createGpx(route, elevationProfilef);

        Path path = Path.of(name);
        //étape 1 : transformer le doc Writer
        //étape 2 : écrir le Writer dans le fichier
        File file = new File(name);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))){

            bufferedWriter.write("JE suis TOTO");
            bufferedWriter.newLine();
            bufferedWriter.write("LE DOCUMENT est écrit lolllllll");
        }
/*
        try (OutputStream outputStream = new FileOutputStream(name)){
            Writer writer2 = new FileWriter(name);
            Writer writer3 = new BufferedWriter(Files.newBufferedWriter(path));
            Writer writer = new OutputStreamWriter(outputStream, name);
            writer3.write(doc.toString());
        }
*/

    }

    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Should never happen
        }
    }
}
