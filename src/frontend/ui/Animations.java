package frontend.ui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class Animations {

    /**
     * Applies a premium Ease-In-Out Fade and Translate transition when switching between pages.
     * @param root The root node of the new page/scene.
     */
    public static void applyPageTransition(Node root) {
        // Fade in from 0 to 1
        FadeTransition fade = new FadeTransition(Duration.millis(400), root);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.setInterpolator(Interpolator.EASE_OUT);

        // Slide up slightly
        TranslateTransition translate = new TranslateTransition(Duration.millis(400), root);
        translate.setFromY(20);
        translate.setToY(0);
        translate.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition pt = new ParallelTransition(fade, translate);
        pt.play();
    }

    /**
     * Applies a subtle 'Scale' pulse animation to a button when clicked.
     * Use this by calling it inside the button's setOnAction handler, or by adding a mouse click listener.
     * @param button The button to animate.
     */
    public static void applyButtonPulseAnimation(Button button) {
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), button);
        scaleDown.setFromX(1.0);
        scaleDown.setFromY(1.0);
        scaleDown.setToX(0.95);
        scaleDown.setToY(0.95);
        scaleDown.setInterpolator(Interpolator.EASE_BOTH);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), button);
        scaleUp.setFromX(0.95);
        scaleUp.setFromY(0.95);
        scaleUp.setToX(1.0);
        scaleUp.setToY(1.0);
        scaleUp.setInterpolator(Interpolator.EASE_BOTH);

        scaleDown.setOnFinished(e -> scaleUp.play());
        scaleDown.play();
    }
    
    /**
     * Optional utility to bind pulse animation automatically to a button click
     */
    public static void bindPulseOnClick(Button button) {
        button.setOnMousePressed(e -> applyButtonPulseAnimation(button));
    }
}
