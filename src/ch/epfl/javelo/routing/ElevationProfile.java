package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.Objects;


/**
 * 4.3.3
 * ElevationProfile
 * <p>
 * Classe représentant le profil en long d'un itinéraire simple ou multiple.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public final class ElevationProfile {

    /**
     * Attributs représentant la longueur du profil et les échantillons d'élévation.
     */

    private final double length;
    private final float[] elevationSamples;

    /**
     * Constructeur public.
     *
     * @param length Longueur du profil en mètres.
     * @param elevationSamples Tableau de float avec les différentes altitudes à équidistance horizontale.
     */

    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;
        this.elevationSamples = Arrays.copyOf(elevationSamples, elevationSamples.length);
    }

    /**
     * Retourne la longueur du profil, en mètres.
     * @return La longueur du profil, en mètres.
     */

    public double length() {
        return length;
    }

    /**
     * Retourne l'altitude minimum du profil, en mètres.
     * @return L'altitude minimum du profil, en mètres.
     */

    public double minElevation() {
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (float i : elevationSamples) s.accept(i);
        return s.getMin();
    }

    /**
     * Retourne l'altitude maximum du profil, en mètres.
     * @return L'altitude maximum du profil, en mètres.
     */

    public double maxElevation() {
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (float i : elevationSamples) s.accept(i);
        return s.getMax();
    }

    /**
     * Retourne le dénivelé positif total du profil, en mètres.
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
     * Retourne le dénivelé négatif total du profil, en mètres. La valeur est toujours positive.
     * @return Le dénivelé négatif total du profil, en mètres. (valeur toujours positive)
     */

    public double totalDescent() {
        double total = 0;
        double memory = elevationSamples[0];
        double delta;
        for (float i : elevationSamples) {
            delta = (double) i - memory;
            total = delta < 0 ? total - delta : total;
            memory = i;
        }
        return total;
    }

    /**
     * Permet de savoir l'altitude d'un point dont on connait l'abscisse X.
     *
     * @param position Position x dont on aimerait connaître l'altitude.
     * @return L'altitude du profil à la position donnée, qui n'est pas forcément comprise entre 0 et la
     * longueur du profil. Le premier échantillon est retourné lorsque la position est négative,
     * le dernier lorsqu'elle est supérieure à la longueur.
     */

    public double elevationAt(double position) {
        return Functions.sampled(elevationSamples, length).applyAsDouble(position);
    }
}
