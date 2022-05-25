package ch.epfl.javelo;
/**
 * 1.3.3
 * Math2
 *
 * Fonctions mathématiques utiles pour la suite (essentiellement relatives à la géométrie 2D).
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public final class Math2 {

    /**
     * Constructeur privé, car cette classe n'est pas censée être instantiable.
     */

    private Math2() {}

    /**
     * Retourne la partie entière supérieure d'une division
     * @param x dividende non nul.
     * @param y diviseur positif et non nul.
     * @return la partie entière supérieure d'une division.
     */

    public static int ceilDiv(int x, int y) {
        Preconditions.checkArgument(!(x<0 || y<=0));
        return (x+y-1) / y;
    }

    /**
     * Méthode permettant à l'aide des coordonnées des points (0,y0) et (1,y1) et de la coordonnée x
     * d'un point de connaitre la coordonnée y de celui-ci.
     * @param y0 premier point de la droite.
     * @param y1 second point de la droite.
     * @param x coordonné x du point dont on aimerait connaitre la coordonnée y.
     * @return l'interprétation linéaire entre deux points y0 et y1.
     */

    public static double interpolate(double y0, double y1, double x) {
        return Math.fma(y1 - y0, x, y0);
    }

    /**
     * Méthode permettant de retourner l'entier le plus proche d'un intervalle
     * @param min borne inférieure.
     * @param v variable entière.
     * @param max borne supérieure.
     * @return la valeur la plus proche de v dans l'intervalle => v si c'est dans
     * l'intervalle, sinon le max ou le min.
     */

    public static int clamp(int min, int v, int max){
        Preconditions.checkArgument(max >= min);
        if (min < v && v < max) return v;
        if (min >= v) {
            return min;
        } else return max;
    }

    /**
     * Méthode permettant de retourner le rationnel le plus proche d'un intervalle.
     * @param min borne inférieure.
     * @param v variable rationnel.
     * @param max borne supérieure.
     * @return la valeur la plus proche de v dans l'intervalle => v si c'est dans
     * l'intervalle, sinon le max ou le min.
     */

    public static double clamp(double min, double v, double max) {
        Preconditions.checkArgument(max >= min);
        if (min < v && v < max) return v;
        return v >= max ? max : min;
    }

    /**
     * Fonction arcsin hyperbolique inverse.
     * @param x Valeur donnée.
     * @return arcsin hyperbolique inverse de X
     */

    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(1+x*x));
    }

    /**
     * Méthode retournant le produit vectoriel de V1 ^ V2 vecteurs 2d
     * @param uX coordonnée X de V1
     * @param uY coordonnée Y de V1
     * @param vX coordonnée X de V2
     * @param vY coordonnée Y de V2
     * @return produit scalaire de V1 ^ V2
     */

    public static double dotProduct(double uX, double uY, double vX, double vY) {
        return uX*vX+vY*uY;
    }

    /**
     * Méthode retournant la norme au carré d'un vecteur 2d
     * @param uX coordonnée X
     * @param uY coordonnée Y
     * @return Norme au carré.
     */

    public static double squaredNorm(double uX, double uY) {
        return dotProduct(uX, uY, uX, uY);
    }

    /**
     * Méthode retournant la norme d'un vecteur 2d.
     * @param uX coordonnée X.
     * @param uY coordonnée Y.
     * @return La norme.
     */

    public static double norm(double uX, double uY) {
        return Math.hypot(uX, uY);
    }

    /**
     * Méthode qui retourne la longueur de la projection du vecteur allant du point A (de
     * coordonnées aX et aY) au point P (de coordonnées pX et pY) sur le vecteur allant du point
     * A au point B (de composantes bX et bY).
     * @param aX Coordonnée X de A.
     * @param aY Coordonnée Y de A.
     * @param bX Coordonnée X de B.
     * @param bY Coordonnée Y de B.
     * @param pX Coordonnée X de P.
     * @param pY Coordonnée Y de P.
     * @return Longueur/norme de la projection.
     */

    public static double projectionLength(double aX, double aY,
                  double bX, double bY, double pX, double pY) {
        return dotProduct(pX - aX, pY - aY, bX - aX, bY - aY) / norm(aX - bX, aY - bY);
    }
}


