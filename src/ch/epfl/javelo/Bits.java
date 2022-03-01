package ch.epfl.javelo;

/**
 * 2.3.3
 * Bits
 *
 * Classe contenant deux méthodes permettant d'extraire une séquence de bits d'un vecteur de 32 bits.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public final class Bits {

    /**
     * Constructeur privé, car cette classe n'est pas instantiable.
     */

    private Bits() {}

    /**
     * ATTENTION CHECK LE PUBLIC STATIC
     * qui extrait du vecteur de 32 bits value la plage de length bits commençant au bit d'index start,
     * qu'elle interprète comme une valeur signée en complément à deux, ou lève une exception si la plage est invalide.
     * @param value Vecteur de 32 bits initial
     * @param start Position du bit de début
     * @param length Longueur à extraire
     * @return Le bit extrait de façon signée
     */

    public static int extractSigned(int value, int start, int length) {
        Preconditions.checkArgument((0 <= start && start <= 31 &&
                0 <= length && length <= 31 && (start + length) <= 32));
        int shiftValue = 32 - length - start;
        return value << shiftValue >> 32 - length;

    }


    /**
     * ATTENTION CHECK LE PUBLIC STATIC
     * @param value Vecteur de 32 bits initial
     * @param start Position du bit de début
     * @param length Longueur à extraire
     * @return Le bit extrait de façon non signée
     */

    public static int extractUnsigned(int value, int start, int length) {
        Preconditions.checkArgument((0 <= start && start <= 31 &&
                0 <= length && length <= 31 && (start + length) <= 32));
        int shiftValue = 32 - length - start;       //
        return value << shiftValue >>> 32 - length;
    }
    //qui fait la même chose que la méthode précédente, à deux différences près : d'une part, la valeur extraite est interpretée de manière non signée, et d'autre part l'exception IllegalArgumentException est également levée si length vaut 32.



}
