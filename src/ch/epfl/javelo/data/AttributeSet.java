package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;
import java.util.StringJoiner;

/**
 * 2.3.7
 * AttributeSet
 *
 * Classe utile pour représenter les différents attributs OpenStreetMap attachés
 * aux éléments (nœuds, voies et relation) qui seront utiles au projet.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */


public record AttributeSet(long bits) {

    /**
     * Le constructeur compact de AttributeSet lève une IllegalArgumentException si la valeur passée
     * au constructeur contient un bit à 1 qui ne correspond à aucun attribut valide.
     */

    public AttributeSet {
        Preconditions.checkArgument(bits < (1L << Attribute.COUNT) && bits >= 0);
    }

    /**
     * Retourne l'ensemble contenant uniquement les attributs donnés en argument.
     * @param attributes Attributs dont on veut créer un ensemble
     * @return L'ensemble contenant uniquement les attributs donnés en argument.
     */

    public static AttributeSet of(Attribute... attributes) {
        long mask = 0 ;
        for (Attribute attribute : attributes) {
            long tempMask = 1L << attribute.ordinal() ;
            mask = mask | tempMask ;
        }
        return new AttributeSet(mask);
    }

    /**
     * Retourne vrai si et seulement si l'ensemble récepteur (this) contient
     * l'attribut donné.
     * @param attribute Attribut que l'on vérifie.
     * @return Vrai ssi l'ensemble récepteur (this) contient l'attribut donné.
    */

    public boolean contains(Attribute attribute) {
        long mask = 1L << attribute.ordinal();
        return (mask == (mask & bits));
    }

    /**
     * Retourne vrai si et seulement si l'intersection de l'ensemble récepteur (this) avec
     * celui passé en argument (that) n'est pas vide.
     * @param that Deuxième ensemble passé en paramètre.
     * @return vrai ssi l'intersection de l'ensemble récepteur (this) avec celui
     * passé en argument (that) n'est pas vide.
     */

    public boolean intersects(AttributeSet that){
        return (bits & that.bits) != 0;
    }

    /**
     * Retourne une chaine composée de la représentation textuelle des éléments de l'ensemble
     * entourés d'accolades et séparés par des virgules.
     * @return Une chaîne composée de la représentation textuelle des éléments de l'ensemble
     * entourés d'accolades et séparés par des virgules.
     */

    @Override
    public String toString() {
        StringJoiner chaine = new StringJoiner(",", "{", "}");
        for (Attribute attribut : Attribute.ALL) {
            long masque = 1L << attribut.ordinal()  ;
            if((masque & bits) != 0) chaine.add(attribut.toString());
        }
        return chaine.toString();
    }
}


