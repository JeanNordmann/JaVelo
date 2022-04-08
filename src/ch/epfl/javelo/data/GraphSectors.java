package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 3.3.3
 * GraphSectors
 *
 * Enregistrement représentant le tableau contenant les 16384 secteurs de JaVelo.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public record GraphSectors(ByteBuffer buffer) {

    /**
     * Diverses constantes représentant les dimensions d'un secteur, ainsi que le
     * décalage voulu pour accéder aux données du Buffer.
     */

    private static final double NUMBER_OF_SECTORS = 128.0;
    private static final double SECTOR_WIDTH = SwissBounds.WIDTH / NUMBER_OF_SECTORS;
    private static final double SECTOR_HEIGHT = SwissBounds.HEIGHT / NUMBER_OF_SECTORS;
    private static final int OFFSET_BYTES = (Integer.BYTES + Short.BYTES);
    private static final int MIN_SECTOR = 0;
    private static final int MAX_SECTOR = 127;

    /**
     * Enregistrement représentant un seul secteur, caractérisé par son nœud
     * de départ et celui de fin.
     */

    public record Sector(int startNodeId, int endNodeId) {}

    /**
     * Retourne la liste de tous les secteurs ayant une intersection avec le carré centré au point
     * donné et de côté égal au double (!) de la distance donnée.
     * @param center   Point donné devenant le centre du carré.
     * @param distance Distance depuis le point prise en compte pour l'intersection.
     * @return La liste de tous les secteurs ayant une intersection avec le carré centré
     * au point donné et de côté égal au double (!) de la distance donnée.
     */


    public List<Sector> sectorsInArea(PointCh center, double distance) {
        //Calcul et clamp de la zone de recherche
        int xMin = Math2.clamp(MIN_SECTOR, (int) ((center.e() - distance - SwissBounds.MIN_E) / SECTOR_WIDTH), MAX_SECTOR);
        int xMax = Math2.clamp(MIN_SECTOR, (int) ((center.e() + distance - SwissBounds.MIN_E) / SECTOR_WIDTH), MAX_SECTOR);
        int yMin = Math2.clamp(MIN_SECTOR, (int) ((center.n() - distance - SwissBounds.MIN_N) / SECTOR_HEIGHT), MAX_SECTOR);
        int yMax = Math2.clamp(MIN_SECTOR, (int) ((center.n() + distance - SwissBounds.MIN_N) / SECTOR_HEIGHT), MAX_SECTOR);

        //ajout des secteurs
        List<Sector> sectorList = new ArrayList<>();
        for (double j = yMin; j <= yMax; j++) {
            for (double i = xMin; i <= xMax; i++) {
                //Calcul du secteur dans le buffer
                int sectorIndexOfFirstByte = OFFSET_BYTES * (int) (i + NUMBER_OF_SECTORS * j);
                int startNode = buffer.getInt(sectorIndexOfFirstByte);
                int endNode = startNode + Short.toUnsignedInt(
                        buffer.getShort(sectorIndexOfFirstByte + Integer.BYTES));
                sectorList.add(new Sector(startNode, endNode));
            }
        }
        return sectorList;
    }
}

