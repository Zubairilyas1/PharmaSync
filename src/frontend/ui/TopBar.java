package frontend.ui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Premium TopBar (Header) component for PharmaSync.
 *
 * <p>Layout: [Page Title + Breadcrumb]  |  [Global Search]  |  [Notifications] [User Profile]</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 *   HBox topBar = TopBar.create("Dashboard", "Dashboard");
 *   // Or with a breadcrumb trail:
 *   HBox topBar = TopBar.create("Inventory", "Dashboard > Inventory");
 *   // With a username:
 *   HBox topBar = TopBar.create("Reports", "Dashboard > Reports", "Dr. Sarah");
 * }</pre>
 *
 * <p>The TopBar automatically fills the full available width. Wrap in a VBox
 * or BorderPane top-slot; do NOT place inside a ScrollPane or it will scroll away.</p>
 */
public final class TopBar {

    // Accent colors — kept in sync with UiTheme / style.css
    private static final String INDIGO        = "#6366F1";
    private static final String BORDER_LIGHT  = "#E2E8F0";
    private static final String MUTED_TEXT    = "#64748B";
    private static final String DARK_TEXT     = "#0B1120";
    private static final String SEARCH_BG     = "#F1F5F9";
    private static final String BADGE_RED     = "#EF4444";
    private static final String AVATAR_BG     = "linear-gradient(to bottom right, #6366F1, #818CF8)";
    private static final String HOVER_SURFACE = "#F1F5F9";

    // Drop-shadow intensities for the "Shadow Scroll" feature
    private static final DropShadow SHADOW_RESTING =
        buildShadow(Color.rgb(0, 0, 0, 0.03), 10, 0, 5);
    private static final DropShadow SHADOW_SCROLLED =
        buildShadow(Color.rgb(0, 0, 0, 0.12), 24, 0, 8);

    private TopBar() { /* utility class */ }

    // -----------------------------------------------------------------------
    // Public Factory Methods
    // -----------------------------------------------------------------------

    /**
     * Creates a TopBar with the given page title and breadcrumb.
     * Username defaults to "Admin".
     */
    public static HBox create(String pageTitle, String breadcrumb) {
        return create(pageTitle, breadcrumb, "Admin");
    }

    /**
     * Creates a TopBar with the given page title, breadcrumb and user display name.
     */
    public static HBox create(String pageTitle, String breadcrumb, String username) {
        HBox bar = new HBox(0);
        bar.setAlignment(Pos.CENTER);
        bar.setPrefHeight(70);
        bar.setMinHeight(70);
        bar.setMaxHeight(70);
        bar.getStyleClass().add("top-bar-premium");

        // ── Left ──────────────────────────────────────────────────────────
        HBox leftSection = buildLeftSection(pageTitle, breadcrumb);

        // ── Spacer ────────────────────────────────────────────────────────
        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        // ── Center ────────────────────────────────────────────────────────
        HBox centerSection = buildSearchSection();

        // ── Spacer ────────────────────────────────────────────────────────
        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        // ── Right ─────────────────────────────────────────────────────────
        HBox rightSection = buildRightSection(username);

        bar.getChildren().addAll(leftSection, leftSpacer, centerSection, rightSpacer, rightSection);

        return bar;
    }

    // -----------------------------------------------------------------------
    // Shadow Scroll Integration
    // -----------------------------------------------------------------------

    /**
     * Binds the TopBar's drop-shadow intensity to a ScrollPane's vertical scroll position.
     *
     * <p>As the user scrolls down, the shadow deepens to create the floating-header
     * effect seen in Apple macOS apps and modern web UIs.</p>
     *
     * <pre>{@code
     *   ScrollPane scroll = new ScrollPane(content);
     *   HBox topBar = TopBar.create("Dashboard", "Dashboard");
     *   TopBar.bindShadowToScroll(topBar, scroll);
     * }</pre>
     *
     * @param topBar    The bar returned by {@link #create}
     * @param scrollPane The ScrollPane whose vValue property drives the shadow
     */
    public static void bindShadowToScroll(HBox topBar, javafx.scene.control.ScrollPane scrollPane) {
        // Start at resting shadow
        topBar.setEffect(SHADOW_RESTING);

        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            double v = newVal.doubleValue();

            // Clamp to [0, 1]; interpolate radius and opacity
            double radius   = lerp(10, 24, v);
            double offsetY  = lerp(5,  8,  v);
            double opacity  = lerp(0.03, 0.14, v);

            topBar.setEffect(buildShadow(Color.rgb(0, 0, 0, opacity), radius, 0, offsetY));
        });
    }

    // -----------------------------------------------------------------------
    // Section Builders (private)
    // -----------------------------------------------------------------------

    /** Left section: Page title (bold) + breadcrumb (muted small) */
    private static HBox buildLeftSection(String pageTitle, String breadcrumb) {
        HBox section = new HBox();
        section.setAlignment(Pos.CENTER_LEFT);
        section.setPadding(new Insets(0, 0, 0, 24));

        VBox textStack = new VBox(2);
        textStack.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(pageTitle);
        titleLabel.getStyleClass().add("top-bar-title");

        Label breadLabel = new Label(breadcrumb);
        breadLabel.getStyleClass().add("top-bar-breadcrumb");

        textStack.getChildren().addAll(titleLabel, breadLabel);
        section.getChildren().add(textStack);
        return section;
    }

    /** Center section: rounded pill-style search field */
    private static HBox buildSearchSection() {
        HBox section = new HBox();
        section.setAlignment(Pos.CENTER);

        // Search icon prefix inside an HBox inside the field container
        Label searchIcon = new Label("⌕");
        searchIcon.getStyleClass().add("top-bar-search-icon");

        TextField searchField = new TextField();
        searchField.setPromptText("Search medicines, orders, suppliers…");
        searchField.getStyleClass().add("top-bar-search");
        searchField.setPrefWidth(340);
        searchField.setPrefHeight(40);

        // Container to layer icon + field (icon is visual-only; field has left padding)
        HBox searchContainer = new HBox(0, searchIcon, searchField);
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.getStyleClass().add("top-bar-search-container");

        section.getChildren().add(searchContainer);
        return section;
    }

    /** Right section: notification bell (with badge) + user profile chip */
    private static HBox buildRightSection(String username) {
        HBox section = new HBox(12);
        section.setAlignment(Pos.CENTER_RIGHT);
        section.setPadding(new Insets(0, 24, 0, 0));

        StackPane notifIcon = buildNotificationIcon();
        HBox profileChip   = buildProfileChip(username);

        section.getChildren().addAll(notifIcon, profileChip);
        return section;
    }

    /** Bell icon wrapped in a StackPane with a small red badge dot */
    private static StackPane buildNotificationIcon() {
        StackPane stack = new StackPane();
        stack.setAlignment(Pos.CENTER);
        stack.setPrefSize(40, 40);
        stack.getStyleClass().add("top-bar-icon-btn");

        // Bell glyph (Unicode bell)
        Label bell = new Label("🔔");
        bell.setStyle("-fx-font-size: 17;");

        // Red badge circle
        Circle badge = new Circle(6);
        badge.setFill(Color.web(BADGE_RED));
        badge.setStroke(Color.WHITE);
        badge.setStrokeWidth(2);
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        badge.setTranslateX(4);
        badge.setTranslateY(-2);

        // Pulse animation on badge to draw attention
        FadeTransition pulse = new FadeTransition(Duration.millis(900), badge);
        pulse.setFromValue(1.0);
        pulse.setToValue(0.3);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(javafx.animation.Animation.INDEFINITE);
        pulse.play();

        stack.getChildren().addAll(bell, badge);
        Tooltip.install(stack, new Tooltip("3 unread alerts"));

        // Hover: scale up 1.05× + lighten background
        installIconHover(stack);
        return stack;
    }

    /** User avatar circle + username label — premium pill chip */
    private static HBox buildProfileChip(String username) {
        HBox chip = new HBox(10);
        chip.setAlignment(Pos.CENTER);
        chip.setPadding(new Insets(6, 14, 6, 10));
        chip.getStyleClass().add("top-bar-profile-chip");

        // Circular avatar placeholder
        StackPane avatarWrap = new StackPane();
        avatarWrap.setPrefSize(34, 34);
        avatarWrap.setMinSize(34, 34);
        avatarWrap.setMaxSize(34, 34);

        Circle avatarBg = new Circle(17);
        avatarBg.setStyle("-fx-fill: " + AVATAR_BG + ";");

        // Initials
        String initials = getInitials(username);
        Label initialsLabel = new Label(initials);
        initialsLabel.setStyle("-fx-text-fill: white; -fx-font-weight: 800; -fx-font-size: 12;");

        avatarWrap.getChildren().addAll(avatarBg, initialsLabel);

        // Name + role
        VBox nameStack = new VBox(0);
        nameStack.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(username);
        nameLabel.getStyleClass().add("top-bar-profile-name");

        Label roleLabel = new Label("Administrator");
        roleLabel.getStyleClass().add("top-bar-profile-role");

        nameStack.getChildren().addAll(nameLabel, roleLabel);

        // Caret
        Label caret = new Label("⌄");
        caret.setStyle("-fx-text-fill: " + MUTED_TEXT + "; -fx-font-size: 14;");

        chip.getChildren().addAll(avatarWrap, nameStack, caret);

        // Hover animation
        installChipHover(chip);
        return chip;
    }

    // -----------------------------------------------------------------------
    // Hover Interaction Helpers
    // -----------------------------------------------------------------------

    /** Scale-up + bg-tint hover on the notification icon StackPane */
    private static void installIconHover(StackPane node) {
        ScaleTransition scaleIn  = buildScale(node, 1.0, 1.08, 160);
        ScaleTransition scaleOut = buildScale(node, 1.08, 1.0, 160);

        node.setOnMouseEntered(e -> {
            scaleOut.stop();
            node.setStyle("-fx-background-color: " + HOVER_SURFACE + "; -fx-background-radius: 12;");
            scaleIn.playFromStart();
        });
        node.setOnMouseExited(e -> {
            scaleIn.stop();
            node.setStyle("-fx-background-color: transparent; -fx-background-radius: 12;");
            scaleOut.playFromStart();
        });
        node.setStyle("-fx-cursor: hand; -fx-background-radius: 12;");
    }

    /** Scale-up + border-highlight hover on the profile chip */
    private static void installChipHover(HBox chip) {
        ScaleTransition scaleIn  = buildScale(chip, 1.0, 1.03, 160);
        ScaleTransition scaleOut = buildScale(chip, 1.03, 1.0, 160);

        chip.setOnMouseEntered(e -> {
            scaleOut.stop();
            chip.getStyleClass().remove("top-bar-profile-chip--hover");
            chip.getStyleClass().add("top-bar-profile-chip--hover");
            scaleIn.playFromStart();
        });
        chip.setOnMouseExited(e -> {
            scaleIn.stop();
            chip.getStyleClass().remove("top-bar-profile-chip--hover");
            scaleOut.playFromStart();
        });
        chip.setStyle("-fx-cursor: hand;");
    }

    // -----------------------------------------------------------------------
    // Private Utilities
    // -----------------------------------------------------------------------

    private static ScaleTransition buildScale(javafx.scene.Node node,
                                               double from, double to, int ms) {
        ScaleTransition st = new ScaleTransition(Duration.millis(ms), node);
        st.setFromX(from); st.setFromY(from);
        st.setToX(to);     st.setToY(to);
        st.setInterpolator(Interpolator.EASE_BOTH);
        return st;
    }

    private static DropShadow buildShadow(Color color, double radius, double x, double y) {
        DropShadow ds = new DropShadow();
        ds.setColor(color);
        ds.setRadius(radius);
        ds.setOffsetX(x);
        ds.setOffsetY(y);
        return ds;
    }

    /** Linear interpolation helper */
    private static double lerp(double a, double b, double t) {
        return a + (b - a) * Math.min(1.0, Math.max(0.0, t));
    }

    /** Returns up-to-two-letter initials from a display name */
    private static String getInitials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return String.valueOf(parts[0].charAt(0)).toUpperCase()
             + String.valueOf(parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}
