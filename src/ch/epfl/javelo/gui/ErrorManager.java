package ch.epfl.javelo.gui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
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

    /**
     * Attribut représentant le panneau sur lequel apparaissent les messages d'erreur.
     */
    private StackPane pane;

    /**
     * Attribut représentant un panneau de type VBox pour afficher les erreurs. 
     */
    private VBox vBox;

    private static SequentialTransition transition;

    private boolean displayIfNeeded;

    private SequentialTransition previousTransition;

    public ErrorManager() {
        vBox = new VBox();
        vBox.getStylesheets().add("error.css");
        vBox.getChildren().add(new Text());
        vBox.setMouseTransparent(true);

        previousTransition = null;
        pane = new StackPane(vBox);
        pane.setPickOnBounds(false);
    }

    public StackPane pane() {
        return pane;
    }

    public void displayError(String errorMessage) {
        FadeTransition fstFadeTransition = new FadeTransition(Duration.millis(200), vBox);
        fstFadeTransition.setFromValue(0);
        fstFadeTransition.setToValue(0.8);
        FadeTransition sndFadeTransition = new FadeTransition(Duration.millis(500),
                vBox);
        sndFadeTransition.setFromValue(0.8);
        sndFadeTransition.setToValue(0);

        transition = new SequentialTransition(fstFadeTransition,
                new PauseTransition(Duration.seconds(2)), sndFadeTransition);

        vBox.getChildren().set(0, new Text(errorMessage));
        errorAnimation();
        previousTransition = null;
        //Partie sonore
        java.awt.Toolkit.getDefaultToolkit().beep();
        System.out.println(vBox.getHeight());
        System.out.println(vBox.getWidth());

    }

    public void errorAnimation() {
        //Arrêter le précédent message s'il y en a déjà un.
//TODO REGLER LES ANIMATIONS EN MEME TEMPS
        //Afficher l'animation du nouveau message d'erreur.
        transition.play();
    }
}
