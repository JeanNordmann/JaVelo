package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * 7.3.2
 * TileManager
 *
 * Classe publique et finale, représentant un gestionnaire de tuiles OSM. Son rôle est d'obtenir
 * les tuiles depuis un serveur de tuile et de les stocker dans un cache mémoire et dans un cache
 * disque.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public final class TileManager {

    public static final int MEMORY_CACHE_SIZE = 100;

    private Path path;
    private String name;
    private LinkedHashMap<TileId, Image> memoryCache;

    /**
     * Record contenant une unique méthode statique nous permettant de vérifier si la tuile OSM est valide.
     */

    public record TileId(int zoomLevel, int xTile, int yTile) {
        //TODO privé ou public ?
        private static final int MIN_ZOOM_LEVEL = 0;
        private static final int MAX_ZOOM_LEVEL = 20;
        private static final int MIN_COORDINATE = 0;

        /**
         * Constructeur compact lançant une exception si les arguments donnés à la
         * construction ne sont pas valides.
         * @param zoomLevel Niveau de zoomLevel compris entre 0 et 19 (inclus).
         * @param xTile Coordonnée X donnée.
         * @param yTile Coordonnée Y donnée.
         */

        public TileId {
            Preconditions.checkArgument(isValid(zoomLevel, xTile, yTile));
        }

        /**
         * Méthode permettant de vérifier si une tuile est valide.
         * @param z Niveau de zoomLevel compris entre 0 et 20 (inclus).
         * @param x Coordonnée X entre 0 et (2 ^ z) - 1 (inclus).
         * @param y Coordonnée Y entre 0 et (2 ^ z) - 1 (inclus).
         * @return Vrai si la tuile est valide.
         */

        public static boolean isValid(int z, int x, int y) {
            int maxCoordinate = (int) Math.pow(2, z) - 1;
            return (z >= MIN_ZOOM_LEVEL) && (z <= MAX_ZOOM_LEVEL)
                  && (x >= MIN_COORDINATE) && (x <= maxCoordinate)
                  && (y >= MIN_COORDINATE) && (y <= maxCoordinate);
        }
    }

    /**
     * Constructeur fabriquant un TileManager.
     * @param path Chemin d'accès au répertoire, contenant le cache disque, de type Path.
     * @param name Le nom du serveur de tuile (sous forme de String).
     */

    public TileManager(Path path, String name) {
        this.path = path;
        this.name = name;
        this.memoryCache = new LinkedHashMap<>(MEMORY_CACHE_SIZE, 0.75f, true);
    }

    public Image imageForTileAt(TileId tileId) throws IOException {
        if (memoryCache.containsKey(tileId)) return memoryCache.get(tileId);
        String stringPath = pathOfTileId(tileId);
        Path filePath = Path.of(stringPath);
        Path directoryPath = Path.of(directoryOfTileId(tileId));
        if (Files.exists(filePath)) {
            try(InputStream inputStream = new FileInputStream(stringPath)) {
                Image image = new Image(inputStream);
                addMRUAndRemoveLRU(tileId, image);
                return image;
            }
        } else {
            URL u = new URL(linkOfTileId(tileId));
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "JaVelo");
            Files.createDirectories(directoryPath);
            try(InputStream i = c.getInputStream();
                OutputStream outputStream = new FileOutputStream(stringPath)) {
                Image image = new Image(i);
                i.transferTo(outputStream);
                addMRUAndRemoveLRU(tileId, image);
                return image;
            }
        }
    }

    private void addMRUAndRemoveLRU(TileId tileId, Image image) {
        memoryCache.put(tileId, image);
        if (memoryCache.entrySet().size() > MEMORY_CACHE_SIZE) {
            Iterator<Map.Entry<TileId, Image>> it = memoryCache.entrySet().iterator();
            it.next();
            it.remove();
        }
    }

    private String directoryOfTileId(TileId tileId) {
        StringBuilder s = new StringBuilder();
        s.append(path.toString());
        s.append('/').append("diskMemory").append('/').append(tileId.zoomLevel).append('/').append(tileId.xTile).append('/');
        return s.toString();
    }

    private String suffixOfTileId(TileId tileId) {

        StringBuilder s = new StringBuilder();
        s.append('/').append(tileId.zoomLevel).append('/').append(tileId.xTile).append('/')
                .append(tileId.yTile).append(".png");
        return s.toString();
    }

    private String pathOfTileId(TileId tileId) {
        StringBuilder s = new StringBuilder();
        s.append(path.toString());
        s.append('/').append("diskMemory");
        s.append(suffixOfTileId(tileId));
        return s.toString();
    }

    private String linkOfTileId(TileId tileId) {
        StringBuilder s = new StringBuilder();
        s.append("https://tile.openstreetmap.org");
        s.append(suffixOfTileId(tileId));
        return s.toString();
    }

}
