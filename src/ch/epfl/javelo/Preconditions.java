package ch.epfl.javelo;


/**
 * 1.3.2
 * Precondition
 *
 * Class utile pour la gestion des IllegalArgumentException
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
     * @param shouldBeTrue
     * si faux => throw exception
     * @throws IllegalArgumentException
     * si shouldBeTrue == false
     */

    public static void checkArgument(boolean shouldBeTrue) throws IllegalArgumentException {
        if (!shouldBeTrue) throw new IllegalArgumentException();
    }

}
