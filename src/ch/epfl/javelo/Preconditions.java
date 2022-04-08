package ch.epfl.javelo;


/**
 * 1.3.2
 * Preconditions
 *
 * Classe utile pour lancer des IllegalArgumentException.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public final class Preconditions {

    /**
     * Constructeur privé, car cette classe n'est pas censée être instantiable.
     */

    private Preconditions() {}

    /**
     * Lève une IllegalArgumentException si shouldBeTrue est faux.
     *
     * @param shouldBeTrue Booléen à vérifier.
     * @throws IllegalArgumentException si le booléen est faux.
     */

    public static void checkArgument(boolean shouldBeTrue) throws IllegalArgumentException {
        if (!shouldBeTrue) throw new IllegalArgumentException();
    }

}
