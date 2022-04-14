package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Iterator;
import java.util.List;

public class GpxGenerator {

    private GpxGenerator() {}

    public static Document createGpx(Route route, ElevationProfile elevationProfile) {
        // Création du document qu'on va rendre
        Document doc = newDocument();

        // Ajout des MetaDatas.
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

        // Ajout des points.
        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        List<PointCh> pointChList = route.points();
        // Position actuelle, utile pour le Elevation At.
        double actualPos = 0;
        // Iterator sur la liste des edges permettant d'incrémenter la position actuelle.
        Iterator<Edge> edgeIterator = route.edges().iterator();

        for (PointCh pointCh : pointChList) {
            // Ajout des coordonnées.
            Element rtept = doc.createElement("rtept");
            rtept.setAttribute("lat", Double.toString(Math.toDegrees(pointCh.lat())));
            rtept.setAttribute("lon", Double.toString(Math.toDegrees(pointCh.lon())));
            rte.appendChild(rtept);
            // Ajout de l'altitude.
            Element ele = doc.createElement("ele");
            rtept.appendChild(ele);
            ele.setTextContent(Double.toString(elevationProfile.elevationAt(actualPos)));
            // Condition nous permettant de ne pas ajouter la dernière longueur d'arête.
            if(edgeIterator.hasNext()) {
               actualPos += edgeIterator.next().length();
           }
        }
        return doc;
    }

    public static void writeGpx(String name, Route route, ElevationProfile elevationProfilef) throws IOException {
        // Création du document et du fichier dans lequel on veut écrire.
        Document doc = createGpx(route, elevationProfilef);
        Writer w = new FileWriter(name);

        try {
            // Creation du Transformer qui adapte le document GPX en Writer qui est ensuite écrit dans le fichier
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));
        } catch (TransformerException e) {
            throw new Error(e); // Ne dois jamais arriver.
        }
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
