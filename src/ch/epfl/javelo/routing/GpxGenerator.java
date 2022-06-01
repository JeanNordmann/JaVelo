package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 7.3.5
 * GpxGenerator
 * <p>
 * Classe permettant d'écrire dans un fichier un itinéraire au format GPX à partir d'une Route,
 * et de son profil.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public class GpxGenerator {

    /**
     * Constructeur privé, car cette classe n'est pas censée être instantiable.
     */
    private GpxGenerator() {}

    /**
     * Méthode permettant de créer un Document au format GPX à partir d'un itinéraire.
     * @param route Route de l'itinéraire.
     * @param elevationProfile Profil altimétrique de l'itinéraire.
     * @return Un document de type Document contenant l'itinéraire au format GPX.
     */
    public static Document createGpx(Route route, ElevationProfile elevationProfile) {
        //Création du document que l'on va retourner.
        Document doc = newDocument();

        //Ajout des MetaDatas.
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

        //Ajout des points.
        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        List<PointCh> pointChList = route.points();

        //Position actuelle, utile pour la méthode retournant l'élévation à une certaine position.
        double actualPos = 0;

        //Itérateur sur la liste des arêtes permettant d'incrémenter la position actuelle.
        Iterator<Edge> edgeIterator = route.edges().iterator();

        for (PointCh pointCh : pointChList) {
            //Ajout des coordonnées.
            Element rtept = doc.createElement("rtept");
            rtept.setAttribute("lat", Double.toString(Math.toDegrees(pointCh.lat())));
            rtept.setAttribute("lon", Double.toString(Math.toDegrees(pointCh.lon())));
            rte.appendChild(rtept);

            //Ajout de l'altitude.
            Element ele = doc.createElement("ele");
            rtept.appendChild(ele);
            ele.setTextContent(Double.toString(elevationProfile.elevationAt(actualPos)));

            //Condition nous permettant de ne pas ajouter la dernière longueur d'arête.
            if (edgeIterator.hasNext()) {
               actualPos += edgeIterator.next().length();
           }
        }
        return doc;
    }

    /**
     * Méthode permettant d'écrire un itinéraire à partir d'une route et de son profile dans un
     * fichier GPX.
     * @param name Nom du fichier dans lequel on écrit notre document.
     * @param route Route de notre itinéraire.
     * @param elevationProfile Profil altimétrique de notre itinéraire.
     * @throws IOException Exception liée à une erreur dûe au FileWriter.
     */

    public static void writeGpx(String name, Route route, ElevationProfile elevationProfile) throws
            IOException {
        //Création du document et du fichier dans lequel on souhaite écrire.
        Document doc = createGpx(route, elevationProfile);
        Writer w = new FileWriter(name);

        try {
            //Création du transformateur qui adapte le document GPX en écriture qui est ensuite
            //écrit dans le fichier souhaité.
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));
        } catch (TransformerException e) {
            throw new Error(e); //Cela ne doit jamais arriver.
        }
    }

    /**
     * Méthode privée permettant la création d'un Document.
     * @return Le document en question.
     */
    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); //Cela ne doit jamais arriver.
        }
    }
}
