package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public final class ErrorManager {

    private Pane pane;

    private VBox vBox;

    private static SequentialTransition transition;

    private boolean displayIfNeeded;

    private SequentialTransition previousTransition;

    public ErrorManager() {
        vBox = new VBox();
        vBox.getStylesheets().add("error.css");
        vBox.getChildren().add(new Text());
        vBox.setMouseTransparent(true);
        FadeTransition fstFadeTransition = new FadeTransition(Duration.millis(200), vBox);
        fstFadeTransition.setFromValue(0);
        fstFadeTransition.setToValue(0.8);
        FadeTransition sndFadeTransition = new FadeTransition(Duration.millis(500),
                vBox.getChildren().get(0));
        sndFadeTransition.setFromValue(0.8);
        sndFadeTransition.setToValue(0);

        transition = new SequentialTransition(fstFadeTransition,
                new PauseTransition(Duration.seconds(2)), sndFadeTransition);

        previousTransition = null;
        pane = new Pane(vBox);
        pane.setPickOnBounds(false);
    }

    public Pane pane() {
        return pane;
    }

    public void displayError(String errorMessage) {
        vBox.getChildren().set(0, new Text(errorMessage));
        errorAnimation();
        previousTransition = null;
        //Partie sonore
        java.awt.Toolkit.getDefaultToolkit().beep();
    }

    public void errorAnimation() {
        //Arrêter le précédent message s'il y en a déjà un.
        transition.stop();

        //Afficher l'animation du nouveau message d'erreur.
        transition.play();
    }
}
