package ch.epfl.javelo.projection;

/**
 * 1.3.5
 * Ch1903
 *
 * Classe possédant des méthodes de conversion de coordonnées géographiques entre le système
 * WGS84 et le système suisse.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public final class Ch1903 {

    /**
     * Constructeur privé, car cette classe n'est pas censée être instantiable.
     */

    private Ch1903() {}

    /**
     * Retourne la coordonnée Est du point donné dans le système suisse à partir des coordonnées
     * dans le système WGS84.
     * @param lon longitude du point.
     * @param lat latitude du point.
     * @return La coordonnée Est du point de longitude lon et latitude lat dans le système WGS84.
     */

    public static double e(double lon, double lat) {
        double lambda = Math.pow(10, -4) * (3600*Math.toDegrees(lon) - 26782.5);
        double phi = Math.pow(10, -4) * (3600*Math.toDegrees(lat) - 169028.66);
        return 2600072.37 + 211455.93*lambda - 10938.51*lambda*phi
                - 0.36*lambda*Math.pow(phi, 2) - 44.54*Math.pow(lambda, 3);
    }

    /**
     * Retourne la coordonnée Nord du point donné dans le système suisse à partir des coordonnées
     * dans le système WGS84.
     * @param lon longitude du point.
     * @param lat latitude du point.
     * @return La coordonnée Nord du point de longitude lon et latitude lat dans le système WGS84.
     */

    public static double n(double lon, double lat) {
        double lambda = Math.pow(10, -4)*(3600*Math.toDegrees(lon) - 26782.5);
        double phi = Math.pow(10, -4)*(3600*Math.toDegrees(lat) - 169028.66);
        return 1200147.07 + 308807.95*phi + 3745.25*Math.pow(lambda, 2) + 76.63*Math.pow(phi, 2)
                - 194.56*Math.pow(lambda, 2)*phi + 119.79*Math.pow(phi, 3);
    }

    /**
     * Retourne la longitude dans le système WGS84 du point dont les coordonnées sont E et N dans le système suisse.
     * @param e Coordonnée E dans le système suisse
     * @param n Coordonnée N dans le système suisse
     * @return La longitude dans le système WGS84 du point dont les coordonnées sont e et n dans le système suisse.
     */

    public static double lon(double e, double n) {
        double x = Math.pow(10, -6) * (e - 2600000);
        double y = Math.pow(10, -6) * (n - 1200000);
        double lambda = 2.6779094 + 4.728982*x + 0.791484*x*y +0.1306*x*Math.pow(y, 2) - 0.0436*Math.pow(x, 3);
        return Math.toRadians(lambda*100.0/36.0);
    }

    /**
     * Retourne la latitude dans le système WGS84 du point dont les coordonnées sont E et N dans le système suisse.
     * @param e Coordonnée E dans le système suisse
     * @param n Coordonnée N dans le système suisse
     * @return La latitude dans le système WGS84 du point dont les coordonnées sont E et N dans le système suisse.
     */

    public static double lat(double e, double n) {
        double x = Math.pow(10, -6)*(e - 2600000);
        double y = Math.pow(10, -6)*(n - 1200000);
        double phi = 16.9023892 + 3.238272*y - 0.270978*Math.pow(x, 2) - 0.002528*Math.pow(y, 2)
                - 0.0447*Math.pow(x, 2)*y - 0.0140*Math.pow(y, 3);
        return Math.toRadians(phi*100.0/36.0);
    }

}
