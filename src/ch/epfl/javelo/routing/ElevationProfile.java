package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;


/**
 * 4.3.3
 * ElevationProfile
 *
 * Classe représentant le profil en long d'un itinéraire simple ou multiple.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public final class ElevationProfile {
    private final double length;
    private final float[] elevationSamples;

    /**
     * Constructeur public.
     * @param length Longueur du profil en mètres.
     * @param elevationSamples Tableau de float avec les différentes altitudes à équidistance horizontale.
     */

    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;
        this.elevationSamples = elevationSamples;
    }

    /**
     *
     * @return La longueur du profil, en mètres.
     */

    public double length() {
        return length;
    }

    /**
     *
     * @return L'altitude minimum du profil, en mètres.
     */

    public double minElevation() {
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (float i : elevationSamples) s.accept(i);
        return s.getMin();
    }

    /**
     *
     * @return L'altitude maximum du profil, en mètres.
     */

    public double maxElevation() {
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (float i : elevationSamples) s.accept(i);
        return s.getMax();
    }

    /**
     *
     * @return Le dénivelé positif total du profil, en mètres.
     */

    public double totalAscent() {
        double total = 0;
        double memory = elevationSamples[0];
        double delta;
        for (float i : elevationSamples) {
            delta = (double) i - memory;
            total = delta > 0 ? total + delta : total;
            memory = i;
        }
        return total;
    }

    /**
     *
     * @return Le dénivelé négatif total du profil, en mètres. (valeur toujours positive)
     */

    public double totalDescent() {
        double total = 0;
        double memorie = elevationSamples[0];
        double delta;
        for (float i : elevationSamples) {
            delta = (double) i - memorie;
            total = delta < 0 ? total - delta : total;
            memorie = i;
        }
        return total;
    }

    /**
     * Permet de savoir l'altitude d'un point dont on connait l'abscisse X.
     * @param position Position x dont on aimerait connaître l'altitude
     * @return L'altitude du profil à la position donnée, qui n'est pas forcément comprise entre 0 et la longueur du profil;
     * le premier échantillon est retourné lorsque la position est négative, le dernier lorsqu'elle est supérieure à la longueur.
     */

    public double elevationAt(double position) {
        position = Math2.clamp(0, position, length);
        return Functions.sampled(elevationSamples, length).applyAsDouble(position);
    }
}
