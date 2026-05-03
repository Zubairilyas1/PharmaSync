package frontend.ui;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.*;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * ONYX LUXURY TopBar — with live backend notification panel.
 */
public final class TopBar {

    private static final String ACCENT    = "#6366F1";
    private static final String BADGE_RED = "#EF4444";

    private static Popup          activePopup = null;
    private static Circle          bellBadge   = null;   // red dot
    private static FadeTransition  bellPulse   = null;   // pulse animation

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

    public static void bindShadowToScroll(HBox bar, ScrollPane sp) {
        bar.setEffect(shadow(Color.rgb(0, 0, 0, 0.50), 10, 0, 3));
        sp.vvalueProperty().addListener((obs, o, n) -> {
            double v = n.doubleValue();
            bar.setEffect(shadow(Color.rgb(0, 0, 0, lerp(0.50, 0.85, v)),
                    lerp(10, 28, v), 0, lerp(3, 10, v)));
        });
    }

    // ── Builder ─────────────────────────────────────────────────────────────

    private static HBox build(String title, String breadcrumb, Stage stage, String username) {
        HBox bar = new HBox(0);
        bar.setAlignment(Pos.CENTER);
        bar.setPrefHeight(70); bar.setMinHeight(70); bar.setMaxHeight(70);
        bar.getStyleClass().add("top-bar-premium");
        bar.setEffect(shadow(Color.rgb(0, 0, 0, 0.50), 12, 0, 4));

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        bar.getChildren().addAll(buildLeft(title, breadcrumb), space, buildRight(stage, username));
        return bar;
    }

    // ── Left ─────────────────────────────────────────────────────────────────

    private static HBox buildLeft(String title, String breadcrumb) {
        HBox sec = new HBox(14);
        sec.setAlignment(Pos.CENTER_LEFT);
        sec.setPadding(new Insets(0, 0, 0, 26));

        Rectangle brand = new Rectangle(3, 34);
        brand.setArcWidth(3); brand.setArcHeight(3);
        brand.setFill(Color.web(ACCENT));

        VBox stack = new VBox(3);
        stack.setAlignment(Pos.CENTER_LEFT);
        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("top-bar-title");
        Label breadLbl = new Label(breadcrumb);
        breadLbl.getStyleClass().add("top-bar-breadcrumb");
        stack.getChildren().addAll(titleLbl, breadLbl);

        sec.getChildren().addAll(brand, stack);
        return sec;
    }

    // ── Right ─────────────────────────────────────────────────────────────────

    private static HBox buildRight(Stage stage, String username) {
        HBox sec = new HBox(8);
        sec.setAlignment(Pos.CENTER_RIGHT);
        sec.setPadding(new Insets(0, 24, 0, 0));

        if (stage != null) {
            sec.getChildren().add(buildBackBtn(stage));
            Rectangle sep = new Rectangle(1, 28);
            sep.setFill(Color.rgb(255, 255, 255, 0.08));
            sec.getChildren().add(sep);
        }
        sec.getChildren().addAll(buildBell(stage), buildProfile(username));
        return sec;
    }

    // ── Back button ─────────────────────────────────────────────────────────

    private static Button buildBackBtn(Stage stage) {
        Button btn = new Button("⌂   Dashboard");
        btn.getStyleClass().add("top-bar-back-btn");
        ScaleTransition in  = scaleAnim(btn, 1.0, 1.05, 140);
        ScaleTransition out = scaleAnim(btn, 1.05, 1.0, 140);
        btn.setOnMouseEntered(e -> { out.stop(); btn.getStyleClass().add("top-bar-back-btn--hover"); in.playFromStart(); });
        btn.setOnMouseExited(e  -> { in.stop();  btn.getStyleClass().remove("top-bar-back-btn--hover"); out.playFromStart(); });
        btn.setOnAction(e -> stage.setScene(frontend.pages.Dashboard.createDashboardScene(stage)));
        return btn;
    }

    // ── Notification Bell ─────────────────────────────────────────────────────

    private static StackPane buildBell(Stage stage) {
        StackPane sp = new StackPane();
        sp.setAlignment(Pos.CENTER);
        sp.setPrefSize(40, 40);
        sp.getStyleClass().add("top-bar-icon-btn");

        Label bell = new Label("🔔");
        bell.setStyle("-fx-font-size: 16;");

        Circle badge = new Circle(5.5);
        badge.setFill(Color.web(BADGE_RED));
        badge.setStroke(Color.web("#0B1120"));
        badge.setStrokeWidth(1.5);
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        badge.setTranslateX(3); badge.setTranslateY(-1);

        FadeTransition pulse = new FadeTransition(Duration.millis(1000), badge);
        pulse.setFromValue(1.0); pulse.setToValue(0.25);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        // Persist refs so "Mark all read" can reach them
        bellBadge = badge;
        bellPulse = pulse;

        sp.getChildren().addAll(bell, badge);
        iconHover(sp);

        // ── Click: toggle notification panel ──
        sp.setOnMouseClicked(e -> toggleNotifPanel(sp, stage, pulse));
        return sp;
    }

    // ── Toggle panel ──────────────────────────────────────────────────────────

    private static void toggleNotifPanel(StackPane bell, Stage stage, FadeTransition pulse) {
        if (activePopup != null && activePopup.isShowing()) {
            activePopup.hide();
            activePopup = null;
            return;
        }

        // Fetch live data
        List<Notif> notifs = fetchNotifications();

        // Stop pulse when opened
        pulse.stop();

        VBox panel = buildNotifPanel(notifs, stage, pulse);

        Popup popup = new Popup();
        popup.setAutoHide(true);
        popup.setOnHidden(ev -> { activePopup = null; pulse.play(); });
        popup.getContent().add(panel);

        Bounds b = bell.localToScreen(bell.getBoundsInLocal());
        popup.show(bell.getScene().getWindow(), b.getMaxX() - 390, b.getMaxY() + 10);
        activePopup = popup;
    }

    // ── Notification Panel ────────────────────────────────────────────────────

    private static VBox buildNotifPanel(List<Notif> notifs, Stage stage, FadeTransition pulse) {
        VBox panel = new VBox(0);
        panel.setPrefWidth(390);
        panel.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: #E2E8F0;" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.22), 32, 0, 0, 12);"
        );

        // Header
        HBox header = new HBox(8);
        header.setPadding(new Insets(16, 18, 14, 18));
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLbl = new Label("Notifications");
        titleLbl.setStyle("-fx-font-size: 15; -fx-font-weight: 800; -fx-text-fill: #0B1120;");

        Label countLbl = new Label(String.valueOf(notifs.size()));
        countLbl.setStyle("-fx-background-color: #6366F1; -fx-text-fill: white; -fx-font-size: 11; -fx-font-weight: 700; -fx-background-radius: 10; -fx-padding: 2 7;");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button markRead = new Button("Mark all read");
        markRead.setStyle("-fx-background-color: transparent; -fx-text-fill: #6366F1; -fx-font-size: 12; -fx-font-weight: 600; -fx-cursor: hand; -fx-padding: 0;");
        markRead.setOnAction(e -> {
            // Hide the red dot and stop pulse
            if (bellBadge != null) {
                bellBadge.setVisible(false);
            }
            if (bellPulse != null) {
                bellPulse.stop();
            }
            if (activePopup != null) activePopup.hide();
        });

        header.getChildren().addAll(titleLbl, countLbl, sp, markRead);

        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: #F1F5F9;");

        panel.getChildren().addAll(header, sep1);

        if (notifs.isEmpty()) {
            VBox empty = new VBox(8);
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(40));
            Label ico  = new Label("🔔"); ico.setStyle("-fx-font-size: 36;");
            Label msg  = new Label("All caught up!"); msg.setStyle("-fx-font-size: 14; -fx-font-weight: 700; -fx-text-fill: #64748B;");
            Label sub  = new Label("No new alerts."); sub.setStyle("-fx-font-size: 12; -fx-text-fill: #94A3B8;");
            empty.getChildren().addAll(ico, msg, sub);
            panel.getChildren().add(empty);
        } else {
            VBox list = new VBox(0);
            ScrollPane scroll = new ScrollPane(list);
            scroll.setFitToWidth(true);
            scroll.setPrefHeight(Math.min(notifs.size() * 80.0, 340));
            scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-width: 0;");
            scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            for (int i = 0; i < notifs.size(); i++) {
                list.getChildren().add(buildNotifItem(notifs.get(i)));
                if (i < notifs.size() - 1) {
                    Separator s = new Separator();
                    s.setStyle("-fx-background-color: #F8FAFC;");
                    list.getChildren().add(s);
                }
            }
            panel.getChildren().add(scroll);
        }

        // Footer
        Separator sep2 = new Separator();
        Button viewAll = new Button("View All in Audit Logs →");
        viewAll.setStyle("-fx-background-color: transparent; -fx-text-fill: #6366F1; -fx-font-size: 13; -fx-font-weight: 600; -fx-cursor: hand; -fx-padding: 14 18;");
        viewAll.setMaxWidth(Double.MAX_VALUE);
        viewAll.setAlignment(Pos.CENTER);
        if (stage != null) {
            viewAll.setOnAction(e -> {
                // Hide red dot — user is going to audit logs
                if (bellBadge != null) bellBadge.setVisible(false);
                if (bellPulse != null) bellPulse.stop();
                if (activePopup != null) activePopup.hide();
                stage.setScene(frontend.pages.AuditLogs.createAuditLogsScene(stage));
            });
        }
        panel.getChildren().addAll(sep2, viewAll);
        return panel;
    }

    // ── Single Notification Row ────────────────────────────────────────────────

    private static HBox buildNotifItem(Notif n) {
        HBox item = new HBox(12);
        item.setPadding(new Insets(12, 18, 12, 0));
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-background-color: #FFFFFF; -fx-cursor: hand;");

        // Left accent bar
        Rectangle bar = new Rectangle(4, 46);
        bar.setArcWidth(4); bar.setArcHeight(4);
        bar.setFill(Color.web(n.color));

        // Icon circle
        StackPane iconWrap = new StackPane();
        iconWrap.setPrefSize(36, 36); iconWrap.setMinSize(36, 36);
        Circle iconBg = new Circle(18);
        iconBg.setFill(Color.web(n.color.replace(")", ", 0.12)").replace("rgb(", "rgba(")));
        iconBg.setStyle("-fx-fill: " + n.color + "1E;");
        Label iconLbl = new Label(n.icon); iconLbl.setStyle("-fx-font-size: 14;");
        iconWrap.getChildren().addAll(iconBg, iconLbl);

        // Text block
        VBox text = new VBox(2);
        HBox.setHgrow(text, Priority.ALWAYS);

        Label tag = new Label(n.tag);
        tag.setStyle("-fx-font-size: 9; -fx-font-weight: 800; -fx-text-fill: " + n.color +
                "; -fx-background-color: " + n.color + "1A; -fx-background-radius: 4; -fx-padding: 2 6;");

        Label titleLbl = new Label(n.title);
        titleLbl.setStyle("-fx-font-size: 12; -fx-font-weight: 700; -fx-text-fill: #0F172A;");
        titleLbl.setWrapText(true);

        Label detailLbl = new Label(n.detail);
        detailLbl.setStyle("-fx-font-size: 11; -fx-text-fill: #64748B;");
        detailLbl.setWrapText(true);

        text.getChildren().addAll(tag, titleLbl, detailLbl);
        item.getChildren().addAll(bar, iconWrap, text);

        item.setOnMouseEntered(e -> item.setStyle("-fx-background-color: #F8FAFC; -fx-cursor: hand;"));
        item.setOnMouseExited(e  -> item.setStyle("-fx-background-color: #FFFFFF; -fx-cursor: hand;"));
        return item;
    }

    // ── Backend Fetch ─────────────────────────────────────────────────────────

    private static List<Notif> fetchNotifications() {
        List<Notif> list = new ArrayList<>();

        // 1. Low stock + expiry alerts from Inventory
        try {
            backend.repositories.MedicineRepository medRepo =
                new backend.repositories.MySQLMedicineRepository(backend.database.DatabaseManager.getConnection());
            backend.services.InventoryService inv = new backend.services.InventoryService(medRepo);
            List<backend.models.Medicine> meds = inv.getAllMedicines();

            LocalDate today = LocalDate.now();
            int lowCount = 0, expCount = 0;

            for (backend.models.Medicine m : meds) {
                if (m.getStockLevel() <= 20 && lowCount < 5) {
                    list.add(new Notif("LOW STOCK", "📦",
                        m.getName() + " — " + m.getStockLevel() + " units left",
                        "Below minimum threshold of 20 units",
                        "#EF4444"));
                    lowCount++;
                }
                if (m.getExpiryDate() != null && expCount < 3) {
                    long days = ChronoUnit.DAYS.between(today, m.getExpiryDate());
                    if (days >= 0 && days <= 30) {
                        list.add(new Notif("EXPIRY ALERT", "⚠️",
                            m.getName() + " expires in " + days + " day" + (days == 1 ? "" : "s"),
                            "Expiry: " + m.getExpiryDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                            "#F59E0B"));
                        expCount++;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[TopBar] Inventory notifications: " + e.getMessage());
        }

        // 2. Recent audit logs
        try {
            backend.repositories.AuditLogRepository logRepo =
                new backend.repositories.MySQLAuditLogRepository(backend.database.DatabaseManager.getConnection());
            backend.services.AuditService audit = new backend.services.AuditService(logRepo);
            List<backend.models.AuditLog> logs = audit.getAllLogs();

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, HH:mm");
            int from = Math.max(0, logs.size() - 3);
            for (int i = logs.size() - 1; i >= from; i--) {
                backend.models.AuditLog log = logs.get(i);
                String when = log.getTimestamp() != null ? log.getTimestamp().format(fmt) : "Recent";
                list.add(new Notif("AUDIT LOG", "📋",
                    log.getAction(),
                    "By " + log.getUsername() + "  •  " + when,
                    "#6366F1"));
            }
        } catch (Exception e) {
            System.err.println("[TopBar] Audit notifications: " + e.getMessage());
        }

        // Fallback so panel is never completely empty
        if (list.isEmpty()) {
            list.add(new Notif("SYSTEM", "ℹ️",
                "PharmaSync is running normally",
                "No critical alerts at this time",
                "#14B8A6"));
        }

        return list;
    }

    // ── Notif data class ──────────────────────────────────────────────────────

    private static class Notif {
        final String tag, icon, title, detail, color;
        Notif(String tag, String icon, String title, String detail, String color) {
            this.tag = tag; this.icon = icon; this.title = title;
            this.detail = detail; this.color = color;
        }
    }

    // ── Profile chip ──────────────────────────────────────────────────────────

    private static HBox buildProfile(String username) {
        HBox chip = new HBox(10);
        chip.setAlignment(Pos.CENTER);
        chip.setPadding(new Insets(6, 14, 6, 10));
        chip.getStyleClass().add("top-bar-profile-chip");

        StackPane av = new StackPane();
        av.setPrefSize(32, 32); av.setMinSize(32, 32); av.setMaxSize(32, 32);
        Circle bg = new Circle(16);
        bg.setFill(Color.web(ACCENT));
        bg.setStroke(Color.rgb(255, 255, 255, 0.15)); bg.setStrokeWidth(1.0);
        Label init = new Label(initials(username));
        init.setStyle("-fx-text-fill: white; -fx-font-weight: 800; -fx-font-size: 12;");
        av.getChildren().addAll(bg, init);

        VBox names = new VBox(0);
        names.setAlignment(Pos.CENTER_LEFT);
        Label nameL = new Label(username); nameL.getStyleClass().add("top-bar-profile-name");
        Label roleL = new Label("Administrator"); roleL.getStyleClass().add("top-bar-profile-role");
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
        node.setOnMouseEntered(e -> { out.stop(); node.getStyleClass().add("top-bar-icon-btn--hover"); in.playFromStart(); });
        node.setOnMouseExited(e  -> { in.stop();  node.getStyleClass().remove("top-bar-icon-btn--hover"); out.playFromStart(); });
        node.setStyle("-fx-cursor: hand;");
    }

    private static void chipHover(HBox chip) {
        ScaleTransition in  = scaleAnim(chip, 1.0, 1.03, 150);
        ScaleTransition out = scaleAnim(chip, 1.03, 1.0, 150);
        chip.setOnMouseEntered(e -> { out.stop(); chip.getStyleClass().remove("top-bar-profile-chip--hover"); chip.getStyleClass().add("top-bar-profile-chip--hover"); in.playFromStart(); });
        chip.setOnMouseExited(e  -> { in.stop();  chip.getStyleClass().remove("top-bar-profile-chip--hover"); out.playFromStart(); });
        chip.setStyle("-fx-cursor: hand;");
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private static ScaleTransition scaleAnim(javafx.scene.Node n, double f, double t, int ms) {
        ScaleTransition st = new ScaleTransition(Duration.millis(ms), n);
        st.setFromX(f); st.setFromY(f); st.setToX(t); st.setToY(t);
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
