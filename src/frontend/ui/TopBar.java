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
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Premium TopBar — Indigo gradient, no search bar.
 *
 * Usage:
 *   TopBar.create("Dashboard", "Dashboard")             // Dashboard (no back btn)
 *   TopBar.create("Inventory", "Dashboard > Inventory", stage)  // All other pages
 */
public final class TopBar {

    private static final String BADGE_RED = "#EF4444";

    private TopBar() {}

    // ── Public API ──────────────────────────────────────────────────────────

    /** Dashboard — no back button. */
    public static HBox create(String title, String breadcrumb) {
        return build(title, breadcrumb, null, "Admin");
    }

    /** Sub-pages — shows glassmorphism Back to Dashboard button. */
    public static HBox create(String title, String breadcrumb, Stage stage) {
        return build(title, breadcrumb, stage, "Admin");
    }

    // ── Shadow Scroll ───────────────────────────────────────────────────────

    public static void bindShadowToScroll(HBox bar, javafx.scene.control.ScrollPane sp) {
        bar.setEffect(shadow(Color.rgb(67, 56, 202, 0.28), 14, 0, 6));
        sp.vvalueProperty().addListener((obs, o, n) -> {
            double v = n.doubleValue();
            bar.setEffect(shadow(
                Color.rgb(67, 56, 202, lerp(0.28, 0.60, v)),
                lerp(14, 30, v), 0, lerp(6, 12, v)));
        });
    }

    // ── Builder ─────────────────────────────────────────────────────────────

    private static HBox build(String title, String breadcrumb, Stage stage, String username) {
        HBox bar = new HBox(0);
        bar.setAlignment(Pos.CENTER);
        bar.setPrefHeight(70);
        bar.setMinHeight(70);
        bar.setMaxHeight(70);
        bar.getStyleClass().add("top-bar-premium");

        HBox left = buildLeft(title, breadcrumb);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox right = buildRight(stage, username);

        bar.getChildren().addAll(left, spacer, right);
        return bar;
    }

    // ── Left: title + breadcrumb ────────────────────────────────────────────

    private static HBox buildLeft(String title, String breadcrumb) {
        HBox sec = new HBox();
        sec.setAlignment(Pos.CENTER_LEFT);
        sec.setPadding(new Insets(0, 0, 0, 26));

        VBox stack = new VBox(2);
        stack.setAlignment(Pos.CENTER_LEFT);

        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("top-bar-title");

        Label breadLbl = new Label(breadcrumb);
        breadLbl.getStyleClass().add("top-bar-breadcrumb");

        stack.getChildren().addAll(titleLbl, breadLbl);
        sec.getChildren().add(stack);
        return sec;
    }

    // ── Right: [back btn] [bell] [profile] ──────────────────────────────────

    private static HBox buildRight(Stage stage, String username) {
        HBox sec = new HBox(10);
        sec.setAlignment(Pos.CENTER_RIGHT);
        sec.setPadding(new Insets(0, 24, 0, 0));

        if (stage != null) {
            sec.getChildren().add(buildBackButton(stage));
        }

        sec.getChildren().addAll(buildBell(), buildProfile(username));
        return sec;
    }

    // ── Back to Dashboard — glassmorphism pill button ───────────────────────

    private static Button buildBackButton(Stage stage) {
        Button btn = new Button("⌂   Dashboard");
        btn.getStyleClass().add("top-bar-back-btn");

        ScaleTransition in  = scale(btn, 1.0, 1.06, 150);
        ScaleTransition out = scale(btn, 1.06, 1.0, 150);

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

    // ── Notification bell ────────────────────────────────────────────────────

    private static StackPane buildBell() {
        StackPane sp = new StackPane();
        sp.setAlignment(Pos.CENTER);
        sp.setPrefSize(40, 40);
        sp.getStyleClass().add("top-bar-icon-btn");

        Label bell = new Label("🔔");
        bell.setStyle("-fx-font-size: 17;");

        Circle badge = new Circle(6);
        badge.setFill(Color.web(BADGE_RED));
        badge.setStroke(Color.WHITE);
        badge.setStrokeWidth(2);
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        badge.setTranslateX(4);
        badge.setTranslateY(-2);

        FadeTransition pulse = new FadeTransition(Duration.millis(900), badge);
        pulse.setFromValue(1.0);
        pulse.setToValue(0.3);
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

        StackPane av = new StackPane();
        av.setPrefSize(34, 34);
        av.setMinSize(34, 34);
        av.setMaxSize(34, 34);
        Circle bg = new Circle(17);
        bg.setStyle("-fx-fill: rgba(255,255,255,0.22); -fx-stroke: rgba(255,255,255,0.45); -fx-stroke-width: 1.5;");
        Label initials = new Label(initials(username));
        initials.setStyle("-fx-text-fill: white; -fx-font-weight: 800; -fx-font-size: 13;");
        av.getChildren().addAll(bg, initials);

        VBox names = new VBox(0);
        names.setAlignment(Pos.CENTER_LEFT);
        Label name = new Label(username);
        name.getStyleClass().add("top-bar-profile-name");
        Label role = new Label("Administrator");
        role.getStyleClass().add("top-bar-profile-role");
        names.getChildren().addAll(name, role);

        Label caret = new Label("⌄");
        caret.setStyle("-fx-text-fill: rgba(255,255,255,0.65); -fx-font-size: 14;");

        chip.getChildren().addAll(av, names, caret);
        chipHover(chip);
        return chip;
    }

    // ── Hover helpers ─────────────────────────────────────────────────────────

    private static void iconHover(StackPane node) {
        ScaleTransition in  = scale(node, 1.0, 1.08, 160);
        ScaleTransition out = scale(node, 1.08, 1.0, 160);
        node.setOnMouseEntered(e -> { out.stop(); node.getStyleClass().add("top-bar-icon-btn--hover"); in.playFromStart(); });
        node.setOnMouseExited(e  -> { in.stop();  node.getStyleClass().remove("top-bar-icon-btn--hover"); out.playFromStart(); });
        node.setStyle("-fx-cursor: hand;");
    }

    private static void chipHover(HBox chip) {
        ScaleTransition in  = scale(chip, 1.0, 1.03, 160);
        ScaleTransition out = scale(chip, 1.03, 1.0, 160);
        chip.setOnMouseEntered(e -> { out.stop(); chip.getStyleClass().remove("top-bar-profile-chip--hover"); chip.getStyleClass().add("top-bar-profile-chip--hover"); in.playFromStart(); });
        chip.setOnMouseExited(e  -> { in.stop();  chip.getStyleClass().remove("top-bar-profile-chip--hover"); out.playFromStart(); });
        chip.setStyle("-fx-cursor: hand;");
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private static ScaleTransition scale(javafx.scene.Node n, double from, double to, int ms) {
        ScaleTransition st = new ScaleTransition(Duration.millis(ms), n);
        st.setFromX(from); st.setFromY(from);
        st.setToX(to);     st.setToY(to);
        st.setInterpolator(Interpolator.EASE_BOTH);
        return st;
    }

    private static DropShadow shadow(Color c, double r, double x, double y) {
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
