package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 7.3.2
 * TileManager
 *
 * Classe publique et finale, représentant un gestionnaire de tuiles OSM. Son rôle est d'obtenir
 * les tuiles depuis un serveur de tuiles et de les stocker dans un cache mémoire et dans un cache
 * disque.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public final class TileManager {

    //Constante représentant la taille du cache mémoire.
    private static final int MEMORY_CACHE_SIZE = 100;

    //Constante représentant le facteur de chargement du cache mémoire à donner à la
    //construction de ce cache.
    private static final float MEMORY_CACHE_LOAD_FACTOR = 0.75f;

    /**
     * Attribut représentant le chemin d'accès au cache disque.
     */
    private final Path path;

    /**
     * Attribut représentant le nom du serveur de tuiles.
     */
    private final String name;

    /**
     * Attribut représentant le cache mémoire des tuiles.
     */
    private final LinkedHashMap<TileId, Image> memoryCache;

    /**
     * Enregistrement contenant une unique méthode statique nous permettant de vérifier si la tuile
     * OSM est valide.
     */

    //TODO-check si privée/public avec les autres groupes
    public record TileId(int zoomLevel, int xTile, int yTile) {

        //Constante représentant le niveau de zoom minimum.
        private static final int MIN_ZOOM_LEVEL = 0;

        //Constante représentant le niveau de zoom maximum.
        private static final int MAX_ZOOM_LEVEL = 20;

        //Constante représentant la coordonnée minimale.
        private static final int MIN_COORDINATE = 0;

        /**
         * Constructeur compact lançant une exception si les arguments donnés à la
         * construction ne sont pas valides.
         * @param zoomLevel Niveau de zoom compris entre 0 et 19 (inclus).
         * @param xTile Coordonnée X donnée.
         * @param yTile Coordonnée Y donnée.
         */
        public TileId {
            Preconditions.checkArgument(isValid(zoomLevel, xTile, yTile));
        }

        /**
         * Méthode permettant de vérifier si une tuile est valide.
         * @param z Niveau de zoomLevel compris entre 0 et 20 (inclus).
         * @param x Coordonnée X entre zéro et (2 ^ z) - 1 (inclus).
         * @param y Coordonnée Y entre zéro et (2 ^ z) - 1 (inclus).
         * @return Vrai si et seulement si la tuile est valide.
         */
        public static boolean isValid(int z, int x, int y) {
            int maxCoordinate = (int) Math.pow(2, z) - 1;
            return (z >= MIN_ZOOM_LEVEL) && (z <= MAX_ZOOM_LEVEL)
                  && (x >= MIN_COORDINATE) && (x <= maxCoordinate)
                  && (y >= MIN_COORDINATE) && (y <= maxCoordinate);
        }
    }

    /**
     * Constructeur fabriquant un TileManager, et initialisant ses attributs aux valeurs passés
     * en paramètres ou par défaut.
     * @param path Chemin d'accès au répertoire, contenant le cache disque, de type Path.
     * @param name Le nom du serveur de tuile (sous forme de String).
     */

    public TileManager(Path path, String name) {
        this.path = path;
        this.name = name;
        this.memoryCache = new LinkedHashMap<>(MEMORY_CACHE_SIZE, MEMORY_CACHE_LOAD_FACTOR, true);
    }

    /**
     * Méthode publique retournant l'image associée à la tuile donnée en paramètre de la méthode.
     * @param tileId Identité de la tuile.
     * @return Retourne l'image associée à la tuile donnée.
     * @throws IOException En cas de flot corrompu, ou si une erreur liée aux flots se produit.
     */

    public Image imageForTileAt(TileId tileId) throws IOException {
        //Cas où l'image est dans le cache mémoire.
        if (memoryCache.containsKey(tileId)) return memoryCache.get(tileId);
        Path directoryPath = pathOfTileId(tileId);
        Path filePath = directoryPath.resolve(tileId.yTile + ".png");

        //Cas où l'image est dans le cache disque.
        if (Files.exists(filePath)) {
            //Bloc Try-with-resource pour fermer le flot à la sortie.
            try (InputStream inputStream = new FileInputStream(filePath.toFile())) {
                Image image = new Image(inputStream);
                //Méthode privée gérant le cache-mémoire et supprimant celui utilisé le moins
                //récemment, pour le remplacer par celui utilisé le plus récemment (l'image
                //actuelle).
                addMRUAndRemoveLRU(tileId, image);
                return image;
            }
        } else {
            //Cas où l'image doit être récupérée sur le serveur, car non-présente dans le cache
            //disque.
            URL u = new URL(linkOfTileId(tileId));
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "JaVelo");
            Files.createDirectories(directoryPath);
            //De nouveau, un bloc try-with-resource est utilisé pour fermer les flots.
            //Crée un flot de sortie écrivant dans le fichier désiré.
            try (InputStream i = c.getInputStream();
                OutputStream outputStream = new FileOutputStream(filePath.toFile())) {
                //Transfère les données du flot d'entrée, vers le flot de sortie.
                i.transferTo(outputStream);
                try (InputStream inputStream = new FileInputStream(filePath.toFile())) {
                    Image image = new Image(inputStream);
                    //Méthode privée gérant le cache-mémoire et supprimant celui utilisé le moins
                    //récemment, pour le remplacer par celui utilisé le plus récemment (l'image
                    //actuelle).
                    addMRUAndRemoveLRU(tileId, image);
                    return image;
                }
            }
        }
    }

    /**
     * Ajoute la TileId et l'image données au cache-mémoire, tout en supprimant la paire utilisée
     * la moins récemment (LRU).
     * @param tileId Identité de la tuile donnée.
     * @param image Image donnée.
     */

    private void addMRUAndRemoveLRU(TileId tileId, Image image) {
        memoryCache.put(tileId, image);
         if (memoryCache.entrySet().size() > MEMORY_CACHE_SIZE) {
                    Iterator<Map.Entry<TileId, Image>> it = memoryCache.entrySet().iterator();
                    it.next();
                    it.remove();
         }
    }

    /**
     * Méthode utile pour ajouter le suffixe à une tuile donnée, notamment pour avoir plus
     * facilement l'URL où il faut chercher pour une tuile donnée.
     * @param tileId Identité de la tuile donnée.
     * @return Le suffixe propre à chaque tuile, sous forme de chaîne de caractère.
     */

    private String suffixOfTileId(TileId tileId) {
        return "/" + tileId.zoomLevel + '/' + tileId.xTile + '/'
                + tileId.yTile + ".png";
    }

    /**
     * Retourne le chemin sous forme de Path, du fichier correspondant à une tuile. (Utile
     * pour créer ce fichier).
     * @param tileId Identité de la tuile donnée.
     * @return Retourne le chemin sous forme de Path, du fichier correspondant à une tuile.
     */

    private Path pathOfTileId(TileId tileId) {
        return Path.of(path.toString()).resolve("osm-cache").resolve(Integer
                        .toString(tileId.zoomLevel))
                        .resolve(Integer.toString(tileId.xTile));
    }

    /**
     * Retourne le lien sous forme de String correspondant à la tuile, avec le serveur donné à la
     * construction du TileManager.
     * @param tileId Identité de la tuile donnée.
     * @return Retourne le lien sous forme de String correspondant à la tuile, avec le serveur
     * donné à la construction du TileManager.
     */
    private String linkOfTileId(TileId tileId) {
         return "https://" + name + suffixOfTileId(tileId);
    }
}
