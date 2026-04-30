package frontend.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Dashboard {

    private static final String PRIMARY_BLUE = "#0056B3";
    private static final String PANEL_BG = "#FFFFFF";
    private static final String APP_BG = "#F4F7FB";

    public static Scene createDashboardScene(Stage stage) {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + APP_BG + ";");

        VBox shell = new VBox(16);
        shell.setPadding(new Insets(22));

        shell.getChildren().add(createTopHeader());
        shell.getChildren().add(createDashboardBody(stage));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-width: 0;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setContent(shell);

        mainLayout.setCenter(scrollPane);

        Scene scene = new Scene(mainLayout, 1200, 800);
        stage.setResizable(true);
        stage.setMinWidth(1000);
        stage.setMinHeight(700);

        return scene;
    }

    private static HBox createTopHeader() {
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 18, 12, 18));
        header.setStyle("-fx-background-color: white; -fx-background-radius: 14; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(13, 38, 76, 0.10), 16, 0, 0, 4);");

        VBox titleWrap = new VBox(2);
        Label title = new Label("PharmaSync");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: 800; -fx-text-fill: #111827;");
        Label subtitle = new Label("PharmaSync Executive Dashboard");
        subtitle.setStyle("-fx-font-size: 12; -fx-text-fill: #6B7280;");
        titleWrap.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label welcome = new Label("Welcome back, Admin");
        welcome.setStyle("-fx-font-size: 12; -fx-text-fill: #475467;");

        Button profileButton = new Button("Admin");
        profileButton.setStyle("-fx-background-color: #EEF4FF; -fx-text-fill: " + PRIMARY_BLUE + "; "
                + "-fx-font-weight: 700; -fx-background-radius: 12; -fx-padding: 7 14;");

        header.getChildren().addAll(titleWrap, spacer, welcome, profileButton);
        return header;
    }

    private static HBox createDashboardBody(Stage stage) {
        HBox body = new HBox(20);
        body.getChildren().addAll(createSidebar(stage), createMainPanel(stage));
        HBox.setHgrow(body.getChildren().get(1), Priority.ALWAYS);
        return body;
    }

    private static VBox createSidebar(Stage stage) {
        VBox side = new VBox(10);
        side.setPrefWidth(250);
        side.setPadding(new Insets(18));
        side.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-background-radius: 16;");

        Label brand = new Label("PharmaSync");
        brand.setStyle("-fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: 800;");
        Label moduleLabel = new Label("All Branch Modules");
        moduleLabel.setStyle("-fx-text-fill: #C7DBFF; -fx-font-size: 11;");

        side.getChildren().addAll(brand, moduleLabel, createSideSpacer(14));
        side.getChildren().addAll(
            createNavButton("Dashboard", true, () -> stage.setScene(createDashboardScene(stage))),
            createNavButton("Inventory", false, () -> stage.setScene(InventoryList.createInventoryListScene(stage))),
            createNavButton("Sales & Dispensing", false, () -> stage.setScene(SalesTerminal.createSalesTerminalScene(stage))),
            createNavButton("Sales Returns", false, () -> stage.setScene(Returns.createReturnsScene(stage))),
            createNavButton("Prescriptions", false, () -> stage.setScene(PrescriptionCheck.createPrescriptionCheckScene(stage))),
            createNavButton("Reports", false, () -> stage.setScene(Reports.createReportsScene(stage))),
            createNavButton("Procurement", false, () -> stage.setScene(Procurement.createProcurementScene(stage))),
            createNavButton("Admin", false, () -> stage.setScene(Settings.createSettingsScene(stage)))
        );
        return side;
    }

    private static VBox createMainPanel(Stage stage) {
        VBox main = new VBox(16);
        main.setPadding(new Insets(2));
        HBox.setHgrow(main, Priority.ALWAYS);

        VBox hero = createPanel(16);
        Label heroTitle = new Label("PharmaSync: Executive Overview");
        heroTitle.setStyle("-fx-font-size: 26; -fx-font-weight: 800; -fx-text-fill: #0F172A;");
        Label heroSub = new Label("Unified Branch Dashboard");
        heroSub.setStyle("-fx-font-size: 14; -fx-text-fill: #667085;");
        hero.getChildren().addAll(heroTitle, heroSub);

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(14);
        statsGrid.setVgap(14);
        statsGrid.add(createMetricCard("Sales Performance Today", "$14,250.00", "Top Medicine: Panadol 500mg", "#0056B3"), 0, 0);
        statsGrid.add(createAlertCard(stage), 1, 0);
        statsGrid.add(createMetricCard("Procurement & Vendor Health", "92%", "Supplier API Health", "#0B8A86"), 0, 1);
        statsGrid.add(createMetricCard("Pending Validations", "7", "Prescriptions: 5 | Returns: 2", "#0056B3"), 1, 1);
        statsGrid.add(createMetricCard("Security Audit Trail", "24h", "Critical security events", "#1D4ED8"), 0, 2);
        statsGrid.add(createActionCard(stage), 1, 2);

        main.getChildren().addAll(hero, statsGrid);
        return main;
    }

    private static VBox createMetricCard(String title, String value, String subtitle, String accentColor) {
        VBox card = createPanel(14);
        card.setPrefWidth(420);
        card.setMinHeight(140);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 15; -fx-font-weight: 700; -fx-text-fill: #111827;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 38; -fx-font-weight: 800; -fx-text-fill: " + accentColor + ";");

        Label sub = new Label(subtitle);
        sub.setStyle("-fx-font-size: 12; -fx-text-fill: #6B7280;");

        card.getChildren().addAll(titleLabel, valueLabel, sub);
        return card;
    }

    private static VBox createAlertCard(Stage stage) {
        VBox card = createPanel(12);
        card.setPrefWidth(420);
        card.setMinHeight(140);

        Label title = new Label("Inventory at Risk (FEFO/Quarantine)");
        title.setStyle("-fx-font-size: 15; -fx-font-weight: 700; -fx-text-fill: #111827;");

        Label alertStrip = new Label("Red-Alert: 12 Items");
        alertStrip.setStyle("-fx-font-size: 13; -fx-font-weight: 700; -fx-text-fill: #1F2937; "
                + "-fx-background-color: #FACC15; -fx-background-radius: 8; -fx-padding: 7 10;");

        Label detail = new Label("Warfarin 5mg (Quarantine)   •   Aspirin 81mg (Near-Expiry)");
        detail.setWrapText(true);
        detail.setStyle("-fx-font-size: 12; -fx-text-fill: #475467;");

        Button cta = createCtaButton("View Inventory", PRIMARY_BLUE, () ->
            stage.setScene(InventoryList.createInventoryListScene(stage))
        );
        card.getChildren().addAll(title, alertStrip, detail, cta);
        return card;
    }

    private static VBox createActionCard(Stage stage) {
        VBox card = createPanel(10);
        card.setPrefWidth(420);
        card.setMinHeight(140);

        Label title = new Label("Quick Actions");
        title.setStyle("-fx-font-size: 15; -fx-font-weight: 700; -fx-text-fill: #111827;");

        HBox row1 = new HBox(8,
            createActionButton("Dispense Sales", () -> stage.setScene(SalesTerminal.createSalesTerminalScene(stage))),
            createActionButton("Run Reports", () -> stage.setScene(Reports.createReportsScene(stage)))
        );
        HBox row2 = new HBox(8,
            createActionButton("Audit Trail", () -> stage.setScene(AuditLogs.createAuditLogsScene(stage))),
            createActionButton("Manage Settings", () -> stage.setScene(Settings.createSettingsScene(stage)))
        );

        card.getChildren().addAll(title, row1, row2);
        return card;
    }

    private static VBox createPanel(double spacing) {
        VBox panel = new VBox(spacing);
        panel.setPadding(new Insets(14));
        panel.setStyle("-fx-background-color: " + PANEL_BG + "; -fx-background-radius: 14; "
                + "-fx-border-color: #E5EAF2; -fx-border-radius: 14; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.08), 14, 0, 0, 3);");
        return panel;
    }

    private static Region createSideSpacer(double height) {
        Region spacer = new Region();
        spacer.setMinHeight(height);
        return spacer;
    }

    private static Button createNavButton(String title, boolean active, Runnable action) {
        Button button = new Button(title);
        button.setPrefWidth(210);
        button.setAlignment(Pos.CENTER_LEFT);
        String activeStyle = "-fx-background-color: white; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: 700;";
        String idleStyle = "-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: #E6EEFF; -fx-font-weight: 600;";
        button.setStyle((active ? activeStyle : idleStyle) + " -fx-background-radius: 10; -fx-padding: 10 14;");
        button.setOnAction(e -> action.run());
        return button;
    }

    private static Button createCtaButton(String text, String color, Runnable action) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: 700; "
                + "-fx-background-radius: 9; -fx-padding: 8 12;");
        button.setOnAction(e -> action.run());
        return button;
    }

    private static Button createActionButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #EEF4FF; -fx-text-fill: #003F8A; -fx-font-weight: 700; "
                + "-fx-background-radius: 8; -fx-padding: 8 10;");
        button.setOnAction(e -> action.run());
        button.setPrefWidth(190);
        return button;
    }
}
