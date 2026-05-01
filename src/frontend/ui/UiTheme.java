package frontend.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;

public final class UiTheme {
    private UiTheme() {
    }

    public static final String COLOR_BG_APP = "#F8FAFC";
    public static final String COLOR_BG_SURFACE = "#FFFFFF";
    public static final String COLOR_PRIMARY = "#6366F1";
    public static final String COLOR_TEXT_PRIMARY = "#0B1120";
    public static final String COLOR_TEXT_SECONDARY = "#64748B";
    public static final String COLOR_SUCCESS_TEXT = "#0D9488";
    public static final String COLOR_DANGER_TEXT = "#B91C1C";
    public static final String COLOR_WARNING_BG = "#FFFBEB";
    public static final String COLOR_WARNING_TEXT = "#B45309";

    public static final int SPACE_4 = 4;
    public static final int SPACE_8 = 8;
    public static final int SPACE_12 = 12;
    public static final int SPACE_16 = 16;
    public static final int SPACE_20 = 20;
    public static final int SPACE_24 = 24;

    /**
     * Applies the global stylesheet to the given scene.
     */
    public static void applyStyleSheet(Scene scene) {
        java.net.URL cssUrl = UiTheme.class.getResource("/frontend/ui/style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            // Fallback: try loading from the source directory directly
            java.io.File cssFile = new java.io.File("src/frontend/ui/style.css");
            if (cssFile.exists()) {
                scene.getStylesheets().add(cssFile.toURI().toString());
            } else {
                System.err.println("WARNING: style.css not found on classpath or in src/frontend/ui/");
            }
        }
    }

    public static Insets pagePadding() {
        return new Insets(SPACE_20);
    }

    // -- Helper methods for applying CSS classes --

    /** @deprecated Use getStyleClass().add("app-background") instead */
    @Deprecated
    public static String appBackground() {
        return "";
    }

    /** @deprecated Use getStyleClass().add("card") instead */
    @Deprecated
    public static String card() {
        return "";
    }

    /** @deprecated Use getStyleClass().add("top-bar") instead */
    @Deprecated
    public static String topBar() {
        return "";
    }

    /** @deprecated Use getStyleClass().add("heading-l") instead */
    @Deprecated
    public static String headingL() {
        return "";
    }

    /** @deprecated Use getStyleClass().add("heading-m") instead */
    @Deprecated
    public static String headingM() {
        return "";
    }

    /** @deprecated Use getStyleClass().add("body-text") instead */
    @Deprecated
    public static String bodyText() {
        return "";
    }

    /** @deprecated Use getStyleClass().add("form-input") instead */
    @Deprecated
    public static String input() {
        return "";
    }

    /** @deprecated Use getStyleClass().addAll("button-base", "primary-button") instead */
    @Deprecated
    public static String primaryButton() {
        return "";
    }

    /** @deprecated Use getStyleClass().addAll("button-base", "secondary-button") instead */
    @Deprecated
    public static String secondaryButton() {
        return "";
    }

    /** @deprecated Use getStyleClass().addAll("button-base", "danger-button") instead */
    @Deprecated
    public static String dangerButton() {
        return "";
    }

    /** @deprecated Use getStyleClass().add("success-message") instead */
    @Deprecated
    public static String successMessage() {
        return "";
    }

    /** @deprecated Use getStyleClass().add("error-message") instead */
    @Deprecated
    public static String errorMessage() {
        return "";
    }

    public static void installPrimaryHover(Button button) {
        // Handled by CSS :hover state now!
    }

    public static void styleFormInput(TextInputControl input) {
        input.getStyleClass().add("form-input");
        input.setPrefHeight(44);
    }

    public static Label sectionLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("heading-m");
        return label;
    }
}

