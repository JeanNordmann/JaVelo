
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
 *
 * Le constructeur compact de AttributeSet lève une IllegalArgumentException si la valeur passée
 * au constructeur contient un bit à 1 qui ne correspond à aucun attribut valide.
 */

public record AttributeSet(long bits) {

    public AttributeSet {
        Preconditions.checkArgument(bits <  Math.scalb(1, Attribute.COUNT) && bits >= 0);
    }

    /**
     *
     * @param attributes
     * @return un ensemble contenant uniquement les attributs donnés en argument
     */

    public static AttributeSet of(Attribute... attributes) {
        long mask = 0 ;
        for (int i = 0; i < attributes.length; i++) {
            long tempMask = 1L << attributes[i].ordinal() ;
            mask = mask | tempMask ;
        }
        return new AttributeSet(mask);
    }


    /**
     *
     * @param attribute
     * @return vrai ssi l'ensemble récepteur (this) contient l'attribut donné
    */
    public boolean contains(Attribute attribute) {
        long masque = 1L << attribute.ordinal() ;
        if(masque== (masque & bits) ) return true ;
        else return false ;
    }


    /**
     *
     * @param that
     * @return vrai ssi l'intersection de l'ensemble récepteur (this) avec celui
     * passé en argument (that) n'est pas vide.
     */
    public boolean intersects(AttributeSet that){
        return (bits & that.bits) != 0;
    }

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


