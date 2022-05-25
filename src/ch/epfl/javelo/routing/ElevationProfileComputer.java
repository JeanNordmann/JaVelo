package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;

/**
 * 5.3.1
 * ElevationProfileComputer
 * <p>
 * Classe représentant un calculateur de profil en long.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public final class ElevationProfileComputer {

    /**
     * Constructeur privé, car cette classe n'est pas censée être instantiable.
     */

    private ElevationProfileComputer() {}

    /**
     * Retourne le profil en long de l'itinéraire route, en garantissant que l'espacement
     * entre les échantillons du profil est d'au maximum maxStepLength mètres; lève
     * IllegalArgumentException si cet espacement n'est pas strictement n'est pas strictement
     * positif.
     * @param route L'itinéraire route de type Route.
     * @param maxStepLength L'espace maximum entre les échantillons du profil.
     * @return le profil en long de l'itinéraire route, en garantissant que l'espacement entre les
     * échantillons du profil est d'au maximum maxStepLength mètres; lève IllegalArgumentException
     * si cet espacement n'est pas strictement positif.
     */

    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        Preconditions.checkArgument(maxStepLength > 0);
        int samplesNumber = (int) Math.ceil(route.length() / maxStepLength) + 1;
        maxStepLength = route.length() / (samplesNumber - 1.0);
        float[] floatsProfile = new float[samplesNumber];
        for (int i = 0; i < samplesNumber; i++) {
            floatsProfile[i] = (float) route.elevationAt(maxStepLength * i);

        }

        //Remplir les trous du début du tableau.
        for (int i = 0; i < samplesNumber; i++) {
            if (!Float.isNaN(floatsProfile[i])) {
                Arrays.fill(floatsProfile, 0, i, floatsProfile[i]);
                break;
            }
            if (i == samplesNumber - 1) Arrays.fill(floatsProfile, 0, i + 1, 0);
        }

        //Remplir les trous de la fin du tableau.
        for (int i = samplesNumber - 1; i >= 0; i--) {
            if (!Float.isNaN(floatsProfile[i])) {
                Arrays.fill(floatsProfile, i + 1, samplesNumber, floatsProfile[i]);
                break;
            }
        }

        //Remplir les trous intermédiaires.
        for (int i = 1; i < samplesNumber; i++) {
            if (Float.isNaN(floatsProfile[i])) {
                float interpolationLimit = 0.f;
                int endIndex = 0;
                for (int j = i + 1; j < samplesNumber; j++) {
                    //Pour la première valeur à partir de la i-ème qui n'est pas un NaN
                    //On interpole toutes les valeurs entre deux valeurs "sûres", et
                    //non entre une valeur sûre et une valeur déjà interpolée, donc
                    //potentiellement approximative.
                    if (!Float.isNaN(floatsProfile[j])) {
                        interpolationLimit = floatsProfile[j];
                        endIndex = j - i + 1;
                        break;
                    }
                }
                for (int j = 1; j < endIndex; j++) {
                    floatsProfile[i + j - 1] = (float) Math2.interpolate(floatsProfile[i - 1],
                            interpolationLimit, (double) j / (endIndex));
                }
                i = endIndex;
            }
        }
        return new ElevationProfile(route.length(), floatsProfile);
    }
}
