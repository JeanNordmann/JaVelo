package ch.epfl.javelo.data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.projection.PointCh;

/**
 * 3.3.3
 * GraphSectors
 *
 * Enregistrement représentant le tableau contenant les 16384 secteurs de JaVelo
 *
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 *
 */

public record GraphSectors(ByteBuffer buffer) {

    private static final double SECTOR_WIDTH = SwissBounds.WIDTH/128.0;
    private static final double SECTOR_HEIGHT = SwissBounds.HEIGHT/128.0;
    private static final int OFFSET_BYTES = (Integer.BYTES + Short.BYTES);

//TODO étais en privé, passé publique pour les tests
    public record Sector(int startNodeId, int endNodeId) {}

    /**
     *
     * @param center Point donné devenant le centre du carré.
     * @param distance Distance depuis le point prise en compte pour l'intersection.
     * @return La liste de tous les secteurs ayant une intersection avec le carré centré
     * au point donné et de côté égal au double (!) de la distance donnée.
     */


public List<Sector> sectorsInArea(PointCh center, double distance) {

        int xmin = Math2.clamp(0, (int)((center.e()-distance-SwissBounds.MIN_E)/SECTOR_WIDTH) ,127);
        int xmax = Math2.clamp(0, (int)((center.e()+distance-SwissBounds.MIN_E)/SECTOR_WIDTH) ,127);
        int ymin = Math2.clamp(0, (int)((center.n()-distance-SwissBounds.MIN_N)/SECTOR_HEIGHT) ,127);
        int ymax = Math2.clamp(0, (int)((center.n()+distance-SwissBounds.MIN_N)/SECTOR_HEIGHT) ,127);

        List<Sector> sectorList = new ArrayList<>();
            for (double j = ymin; j <= ymax ; j++) {
                for (double i = xmin ; i <= xmax ; i++) {

                int sectorIndexOfFirstByte = OFFSET_BYTES * (int) (i + 128 * j);
                int startNode = buffer.getInt(sectorIndexOfFirstByte);
                int endNode = startNode + Short.toUnsignedInt(buffer.getShort(sectorIndexOfFirstByte+Integer.BYTES));

                sectorList.add(new Sector(startNode,endNode));
            }
        }
        return sectorList;
    }
}

