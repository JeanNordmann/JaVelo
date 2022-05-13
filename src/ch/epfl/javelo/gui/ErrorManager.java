package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.time.temporal.Temporal;

public final class ErrorManager {

    private Pane pane;

    private VBox vBox;

    private boolean displayIfNeeded;

    public ErrorManager() {
        vBox = new VBox();
        vBox.getStylesheets().add("error.css");
        Text text = new Text();
        vBox.setMouseTransparent(true);
    }

    public Pane pane() {
        return pane;
    }

    public void displayError(String errorMessage) {
        //TODO AFFICHER LE MESSAGE

        //Partie sonore
        java.awt.Toolkit.getDefaultToolkit().beep();
    }

    public void errorAnimation() {
        FadeTransition fstFadeTransition = new FadeTransition(Duration.millis(200), vBox);
        fstFadeTransition.setFromValue(0);
        fstFadeTransition.setToValue(0.8);
        FadeTransition sndFadeTransition = new FadeTransition(Duration.millis(500), vBox)
        System    }*/
}
