package frontend.ui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * ONYX LUXURY TopBar for PharmaSync.
 *
 * Design language: Bloomberg Terminal × Apple Pro Display
 * - Near-black  #0C0C12  flat bar
 * - 1px indigo  #6366F1  bottom accent line
 * - Pure white title, dim gray breadcrumb
 * - Transparent-outline back button (no fill)
 * - Indigo avatar dot — only pop of color
 *
 * Usage:
 *   TopBar.create("Dashboard", "Dashboard")                       // no back btn
 *   TopBar.create("Inventory", "Dashboard > Inventory", stage)   // with back btn
 */
public final class TopBar {

    // Indigo accent — only color touch on the dark bar
    private static final String ACCENT   = "#6366F1";
    private static final String BADGE_RED = "#EF4444";

    private TopBar() {}

    // ── Public API ──────────────────────────────────────────────────────────

    public static HBox create(String title, String breadcrumb) {
        return build(title, breadcrumb, null, "Admin");
    }

    public static HBox create(String title, String breadcrumb, Stage stage) {
        return build(title, breadcrumb, stage, "Admin");
    }

    public static HBox create(String title, String breadcrumb, Stage stage, String username) {
        return build(title, breadcrumb, stage, username);
    }

    // ── Shadow Scroll ───────────────────────────────────────────────────────

    /**
     * Deepens the bar's drop-shadow as the user scrolls down —
     * makes the bar appear to "lift" above the content.
     */
    public static void bindShadowToScroll(HBox bar, javafx.scene.control.ScrollPane sp) {
        bar.setEffect(mkShadow(Color.rgb(0, 0, 0, 0.45), 10, 0, 3));
        sp.vvalueProperty().addListener((obs, o, n) -> {
            double v = n.doubleValue();
            bar.setEffect(mkShadow(
                Color.rgb(0, 0, 0, lerp(0.45, 0.80, v)),
                lerp(10, 28, v), 0, lerp(3, 10, v)));
        });
    }

    // ── Core Builder ────────────────────────────────────────────────────────

    private static HBox build(String title, String breadcrumb, Stage stage, String username) {
        HBox bar = new HBox(0);
        bar.setAlignment(Pos.CENTER);
        bar.setPrefHeight(70);
        bar.setMinHeight(70);
        bar.setMaxHeight(70);
        bar.getStyleClass().add("top-bar-premium");

        // Apply resting shadow
        bar.setEffect(mkShadow(Color.rgb(0, 0, 0, 0.50), 12, 0, 4));

        HBox left    = buildLeft(title, breadcrumb);
        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);
        HBox right   = buildRight(stage, username);

        bar.getChildren().addAll(left, space, right);
        return bar;
    }

    // ── Left: brand mark + title + breadcrumb ──────────────────────────────

    private static HBox buildLeft(String title, String breadcrumb) {
        HBox sec = new HBox(14);
        sec.setAlignment(Pos.CENTER_LEFT);
        sec.setPadding(new Insets(0, 0, 0, 26));

        // Thin indigo brand bar (vertical)
        Rectangle brandBar = new Rectangle(3, 34);
        brandBar.setArcWidth(3);
        brandBar.setArcHeight(3);
        brandBar.setFill(Color.web(ACCENT));

        VBox stack = new VBox(3);
        stack.setAlignment(Pos.CENTER_LEFT);

        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("top-bar-title");

        Label breadLbl = new Label(breadcrumb);
        breadLbl.getStyleClass().add("top-bar-breadcrumb");

        stack.getChildren().addAll(titleLbl, breadLbl);
        sec.getChildren().addAll(brandBar, stack);
        return sec;
    }

    // ── Right: [back] | [bell] | [profile] ─────────────────────────────────

    private static HBox buildRight(Stage stage, String username) {
        HBox sec = new HBox(8);
        sec.setAlignment(Pos.CENTER_RIGHT);
        sec.setPadding(new Insets(0, 24, 0, 0));

        if (stage != null) {
            sec.getChildren().add(buildBackBtn(stage));
            // Thin separator
            Rectangle sep = new Rectangle(1, 28);
            sep.setFill(Color.rgb(255, 255, 255, 0.08));
            sec.getChildren().add(sep);
        }

        sec.getChildren().addAll(buildBell(), buildProfile(username));
        return sec;
    }

    // ── Back button — transparent outline pill ──────────────────────────────

    private static Button buildBackBtn(Stage stage) {
        Button btn = new Button("⌂   Dashboard");
        btn.getStyleClass().add("top-bar-back-btn");

        ScaleTransition in  = scaleAnim(btn, 1.0, 1.05, 140);
        ScaleTransition out = scaleAnim(btn, 1.05, 1.0, 140);

        btn.setOnMouseEntered(e -> {
            out.stop();
            btn.getStyleClass().add("top-bar-back-btn--hover");
            in.playFromStart();
        });
        btn.setOnMouseExited(e -> {
            in.stop();
            btn.getStyleClass().remove("top-bar-back-btn--hover");
            out.playFromStart();
        });
        btn.setOnAction(e -> stage.setScene(
                frontend.pages.Dashboard.createDashboardScene(stage)));
        return btn;
    }

    // ── Notification bell ───────────────────────────────────────────────────

    private static StackPane buildBell() {
        StackPane sp = new StackPane();
        sp.setAlignment(Pos.CENTER);
        sp.setPrefSize(40, 40);
        sp.getStyleClass().add("top-bar-icon-btn");

        Label bell = new Label("🔔");
        bell.setStyle("-fx-font-size: 16;");

        // Red badge
        Circle badge = new Circle(5.5);
        badge.setFill(Color.web(BADGE_RED));
        badge.setStroke(Color.web("#0C0C12"));
        badge.setStrokeWidth(1.5);
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        badge.setTranslateX(3);
        badge.setTranslateY(-1);

        // Pulse
        FadeTransition pulse = new FadeTransition(Duration.millis(1000), badge);
        pulse.setFromValue(1.0);
        pulse.setToValue(0.25);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(javafx.animation.Animation.INDEFINITE);
        pulse.play();

        sp.getChildren().addAll(bell, badge);
        Tooltip.install(sp, new Tooltip("3 unread alerts"));
        iconHover(sp);
        return sp;
    }

    // ── Profile chip ─────────────────────────────────────────────────────────

    private static HBox buildProfile(String username) {
        HBox chip = new HBox(10);
        chip.setAlignment(Pos.CENTER);
        chip.setPadding(new Insets(6, 14, 6, 10));
        chip.getStyleClass().add("top-bar-profile-chip");

        // Avatar — indigo circle with white initials (the ONE color accent)
        StackPane av = new StackPane();
        av.setPrefSize(32, 32);
        av.setMinSize(32, 32);
        av.setMaxSize(32, 32);

        Circle bg = new Circle(16);
        bg.setFill(Color.web(ACCENT));
        bg.setStroke(Color.rgb(255, 255, 255, 0.15));
        bg.setStrokeWidth(1.0);

        Label initLbl = new Label(initials(username));
        initLbl.setStyle("-fx-text-fill: white; -fx-font-weight: 800; -fx-font-size: 12;");

        av.getChildren().addAll(bg, initLbl);

        // Name + role
        VBox names = new VBox(0);
        names.setAlignment(Pos.CENTER_LEFT);
        Label nameL = new Label(username);
        nameL.getStyleClass().add("top-bar-profile-name");
        Label roleL = new Label("Administrator");
        roleL.getStyleClass().add("top-bar-profile-role");
        names.getChildren().addAll(nameL, roleL);

        Label caret = new Label("⌄");
        caret.setStyle("-fx-text-fill: rgba(255,255,255,0.30); -fx-font-size: 13;");

        chip.getChildren().addAll(av, names, caret);
        chipHover(chip);
        return chip;
    }

    // ── Hover helpers ─────────────────────────────────────────────────────────

    private static void iconHover(StackPane node) {
        ScaleTransition in  = scaleAnim(node, 1.0, 1.08, 150);
        ScaleTransition out = scaleAnim(node, 1.08, 1.0, 150);
        node.setOnMouseEntered(e -> {
            out.stop();
            node.getStyleClass().add("top-bar-icon-btn--hover");
            in.playFromStart();
        });
        node.setOnMouseExited(e -> {
            in.stop();
            node.getStyleClass().remove("top-bar-icon-btn--hover");
            out.playFromStart();
        });
        node.setStyle("-fx-cursor: hand;");
    }

    private static void chipHover(HBox chip) {
        ScaleTransition in  = scaleAnim(chip, 1.0, 1.03, 150);
        ScaleTransition out = scaleAnim(chip, 1.03, 1.0, 150);
        chip.setOnMouseEntered(e -> {
            out.stop();
            chip.getStyleClass().remove("top-bar-profile-chip--hover");
            chip.getStyleClass().add("top-bar-profile-chip--hover");
            in.playFromStart();
        });
        chip.setOnMouseExited(e -> {
            in.stop();
            chip.getStyleClass().remove("top-bar-profile-chip--hover");
            out.playFromStart();
        });
        chip.setStyle("-fx-cursor: hand;");
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private static ScaleTransition scaleAnim(javafx.scene.Node n, double f, double t, int ms) {
        ScaleTransition st = new ScaleTransition(Duration.millis(ms), n);
        st.setFromX(f); st.setFromY(f);
        st.setToX(t);   st.setToY(t);
        st.setInterpolator(Interpolator.EASE_BOTH);
        return st;
    }

    private static DropShadow mkShadow(Color c, double r, double x, double y) {
        DropShadow ds = new DropShadow();
        ds.setColor(c); ds.setRadius(r); ds.setOffsetX(x); ds.setOffsetY(y);
        return ds;
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * Math.min(1.0, Math.max(0.0, t));
    }

    private static String initials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] p = name.trim().split("\\s+");
        if (p.length == 1) return p[0].substring(0, Math.min(2, p[0].length())).toUpperCase();
        return ("" + p[0].charAt(0) + p[p.length - 1].charAt(0)).toUpperCase();
    }
}
