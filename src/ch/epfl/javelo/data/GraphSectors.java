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
 * Classe permettant le tableau contenant les 16384 secteurs de JaVelo
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


    private record Sector(int startNodeId, int endNodeId) {

    }

    /**
     *
     * @param center Point donné devenant le centre du carré.
     * @param distance Distance depuis le point prise en compte pour l'intersection.
     * @return La liste de tous les secteurs ayant une intersection avec le carré centré
     * au point donné et de côté égal au double (!) de la distance donnée.
     */

    public List<Sector> sectorsInArea(PointCh center, double distance) {
        PointCh coinGaucheBasZoneDesSecteursPCH = new PointCh((int)Math2.clamp(SwissBounds.MIN_E,center.e() - distance,SwissBounds.MAX_E),
                (int)Math2.clamp(SwissBounds.MIN_N,center.n() - distance,SwissBounds.MAX_N));
        PointCh coinDroiteHautZoneDesSecteursPCH = new PointCh((int)Math2.clamp(SwissBounds.MIN_E,center.e() + distance,SwissBounds.MAX_E),
                (int)Math2.clamp(SwissBounds.MIN_N,center.n() + distance,SwissBounds.MAX_N));
        List<Sector> sectorList = new ArrayList<>();
        for (double i = coinGaucheBasZoneDesSecteursPCH.e() ; i < coinDroiteHautZoneDesSecteursPCH.e() ; i += SECTOR_WIDTH) {
            for (double j = coinGaucheBasZoneDesSecteursPCH.n(); j < coinDroiteHautZoneDesSecteursPCH.n() ; j += SECTOR_HEIGHT) {

                byte sectorX = (byte) Math.floor((i - SwissBounds.MIN_E) / SECTOR_WIDTH);
                byte sectorY = (byte) Math.floor((j - SwissBounds.MIN_N) / SECTOR_HEIGHT);
                Preconditions.checkArgument(sectorX >=0 && sectorY >= 0);

                sectorList.add(new Sector(buffer.getInt(OFFSET_BYTES * (sectorX + 128 * sectorY)), Short.toUnsignedInt(buffer.getShort(OFFSET_BYTES * (sectorX + 128 * sectorY)+Integer.BYTES))));
            }
        }
        //TODO faire les Test

        return sectorList;
    }

}
