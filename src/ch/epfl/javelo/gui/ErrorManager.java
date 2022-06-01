package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * 11.3.3
 * ErrorManager
 * <p>
 * Classe gérant l'affichage des messages d'erreur.
 *
 * @author Jean Nordmann (344692)
 * @author Maxime Ducourau (329544)
 */

public final class ErrorManager {


    private static final double UP_FADE_TRANSITION_DURATION_MS = 200;
    private static final double DOWN_FADE_TRANSITION_DURATION_MS = 500;
    private static final double LOW_OPACITY_COEFFICIENT = 0;
    private static final double HIGH_OPACITY_COEFFICIENT = 0.8;

    /**
     * Attribut représentant le panneau sur lequel apparaissent les messages d'erreur.
     */
    private final StackPane pane;

    /**
     * Attribut représentant un panneau de type VBox pour afficher les erreurs. 
     */
    private final VBox vBox;

    /**
     * Attribut représentant la transition sur laquelle afficher l'erreur demandée.
     */
    private static SequentialTransition transition;

    /**
     * Constructeur public de la classe ErrorManager, qui initialise les attributs de la classe à
     * leurs valeurs par défaut, initialise la feuille de style du panneau et gère si les
     * interactions avec le panneau masque les panneaux en arrière de celui-ci.
     */
    public ErrorManager() {
        vBox = new VBox();
        vBox.getStylesheets().add("error.css");
        vBox.getChildren().add(new Text());
        vBox.setMouseTransparent(true);

        pane = new StackPane(vBox);

        //Empêche que ce panneau masque les interactions avec les panneaux en arrière-plan.
        pane.setPickOnBounds(false);
    }

    /**
     * Méthode retournant le panneau du gestionnaire d'erreurs.
     * @return Le panneau du gestionnaire d'erreurs.
     */
    public StackPane pane() {
        return pane;
    }

    /**
     * Méthode permettant d'afficher un message sous forme de chaîne de caractères passés en
     * paramètres, sur le panneau du gestionnaire d'erreurs.
     * @param errorMessage Chaîne de caractères contenant le message à afficher sur le panneau du
     *                    gestionnaire d'erreurs.
     */

    public void displayError(String errorMessage) {
        //Initialise les paramètres de l'affichage d'erreur demandée.
        //TODO vire tout les magic number, on fais des constante, juste je n'était pas sure des noms
        FadeTransition fstFadeTransition = new FadeTransition(Duration.millis(UP_FADE_TRANSITION_DURATION_MS), vBox);
        fstFadeTransition.setFromValue(LOW_OPACITY_COEFFICIENT);
        fstFadeTransition.setToValue(HIGH_OPACITY_COEFFICIENT);
        FadeTransition sndFadeTransition = new FadeTransition(Duration.millis(DOWN_FADE_TRANSITION_DURATION_MS),
                vBox);
        sndFadeTransition.setFromValue(HIGH_OPACITY_COEFFICIENT);
        sndFadeTransition.setToValue(LOW_OPACITY_COEFFICIENT);

        //Arrête la précédente animation s'il y en a une en cours.
        if (transition != null) transition.stop();

        //Crée la nouvelle transition à afficher.
        transition = new SequentialTransition(fstFadeTransition,
                new PauseTransition(Duration.seconds(2)), sndFadeTransition);

        //Ajoute à la VBox le texte à afficher.
        vBox.getChildren().set(0, new Text(errorMessage));

        //Partie sonore.
        java.awt.Toolkit.getDefaultToolkit().beep();

        //Joue l'animation demandée.
        transition.play();
    }
}
