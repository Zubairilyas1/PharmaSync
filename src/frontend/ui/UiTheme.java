package frontend.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;

public final class UiTheme {
    private UiTheme() {
    }

    public static final String COLOR_BG_APP = "#F4F7FB";
    public static final String COLOR_BG_SURFACE = "#FFFFFF";
    public static final String COLOR_BORDER = "#E5EAF2";
    public static final String COLOR_TEXT_PRIMARY = "#111827";
    public static final String COLOR_TEXT_SECONDARY = "#667085";
    public static final String COLOR_PRIMARY = "#0056B3";
    public static final String COLOR_PRIMARY_HOVER = "#004A99";
    public static final String COLOR_SUCCESS_BG = "#DCFCE7";
    public static final String COLOR_SUCCESS_BORDER = "#86EFAC";
    public static final String COLOR_SUCCESS_TEXT = "#166534";
    public static final String COLOR_DANGER_BG = "#FEE2E2";
    public static final String COLOR_DANGER_BORDER = "#FCA5A5";
    public static final String COLOR_DANGER_TEXT = "#991B1B";
    public static final String COLOR_WARNING_BG = "#FEF3C7";
    public static final String COLOR_WARNING_TEXT = "#92400E";

    public static final String FONT_FAMILY = "Arial";

    public static final int SPACE_4 = 4;
    public static final int SPACE_8 = 8;
    public static final int SPACE_12 = 12;
    public static final int SPACE_16 = 16;
    public static final int SPACE_20 = 20;
    public static final int SPACE_24 = 24;

    public static Insets pagePadding() {
        return new Insets(SPACE_20);
    }

    public static String appBackground() {
        return "-fx-background-color: " + COLOR_BG_APP + ";";
    }

    public static String card() {
        return "-fx-background-color: " + COLOR_BG_SURFACE + "; "
                + "-fx-background-radius: 14; "
                + "-fx-border-color: " + COLOR_BORDER + "; "
                + "-fx-border-radius: 14; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.08), 14, 0, 0, 3);";
    }

    public static String topBar() {
        return "-fx-background-color: " + COLOR_BG_SURFACE + "; "
                + "-fx-background-radius: 14; "
                + "-fx-border-color: " + COLOR_BORDER + "; "
                + "-fx-border-radius: 14; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(13, 38, 76, 0.10), 16, 0, 0, 4);";
    }

    public static String headingL() {
        return "-fx-font-family: '" + FONT_FAMILY + "'; -fx-font-size: 28; -fx-font-weight: 800; -fx-text-fill: " + COLOR_TEXT_PRIMARY + ";";
    }

    public static String headingM() {
        return "-fx-font-family: '" + FONT_FAMILY + "'; -fx-font-size: 18; -fx-font-weight: 800; -fx-text-fill: " + COLOR_TEXT_PRIMARY + ";";
    }

    public static String bodyText() {
        return "-fx-font-family: '" + FONT_FAMILY + "'; -fx-font-size: 13; -fx-text-fill: " + COLOR_TEXT_SECONDARY + ";";
    }

    public static String input() {
        return "-fx-font-family: '" + FONT_FAMILY + "'; -fx-font-size: 14; "
                + "-fx-background-radius: 8; -fx-border-radius: 8; "
                + "-fx-border-color: " + COLOR_BORDER + ";";
    }

    public static String primaryButton() {
        return "-fx-background-color: " + COLOR_PRIMARY + "; "
                + "-fx-text-fill: white; "
                + "-fx-font-family: '" + FONT_FAMILY + "'; "
                + "-fx-font-size: 14; "
                + "-fx-font-weight: 700; "
                + "-fx-background-radius: 8; "
                + "-fx-cursor: hand;";
    }

    public static String secondaryButton() {
        return "-fx-background-color: #EEF4FF; "
                + "-fx-text-fill: " + COLOR_PRIMARY + "; "
                + "-fx-font-family: '" + FONT_FAMILY + "'; "
                + "-fx-font-size: 13; "
                + "-fx-font-weight: 700; "
                + "-fx-background-radius: 8; "
                + "-fx-cursor: hand;";
    }

    public static String dangerButton() {
        return "-fx-background-color: #DC2626; "
                + "-fx-text-fill: white; "
                + "-fx-font-family: '" + FONT_FAMILY + "'; "
                + "-fx-font-size: 13; "
                + "-fx-font-weight: 700; "
                + "-fx-background-radius: 8; "
                + "-fx-cursor: hand;";
    }

    public static String successMessage() {
        return "-fx-background-color: " + COLOR_SUCCESS_BG + "; "
                + "-fx-border-color: " + COLOR_SUCCESS_BORDER + "; "
                + "-fx-border-radius: 8; "
                + "-fx-background-radius: 8; "
                + "-fx-padding: 10 12; "
                + "-fx-font-weight: 600;";
    }

    public static String errorMessage() {
        return "-fx-background-color: " + COLOR_DANGER_BG + "; "
                + "-fx-border-color: " + COLOR_DANGER_BORDER + "; "
                + "-fx-border-radius: 8; "
                + "-fx-background-radius: 8; "
                + "-fx-padding: 10 12; "
                + "-fx-font-weight: 600;";
    }

    public static void installPrimaryHover(Button button) {
        button.setOnMouseEntered(e -> button.setStyle(primaryButton().replace(COLOR_PRIMARY, COLOR_PRIMARY_HOVER)));
        button.setOnMouseExited(e -> button.setStyle(primaryButton()));
    }

    public static void styleFormInput(TextInputControl input) {
        input.setStyle(input());
        input.setPrefHeight(44);
    }

    public static Label sectionLabel(String text) {
        Label label = new Label(text);
        label.setStyle(headingM());
        return label;
    }
}
