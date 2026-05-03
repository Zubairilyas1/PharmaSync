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
import frontend.ui.UiTheme;
import frontend.ui.Animations;
import frontend.ui.TopBar;

import backend.database.DatabaseManager;
import backend.models.*;
import backend.repositories.*;
import backend.services.*;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Dashboard {

    public static Scene createDashboardScene(Stage stage) {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("app-background");

        // ── Premium TopBar (sticky, outside scroll) ──────────────────────
        HBox topBar = TopBar.create("Dashboard", "Dashboard", stage, false);
        mainLayout.setTop(topBar);

        VBox shell = new VBox(16);
        shell.setPadding(new Insets(22));
        shell.getChildren().add(createDashboardBody(stage));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-width: 0;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setContent(shell);
        mainLayout.setCenter(scrollPane);

        // Shadow deepens as user scrolls
        TopBar.bindShadowToScroll(topBar, scrollPane);

        Scene scene = new Scene(mainLayout, 1280, 800);
        UiTheme.applyStyleSheet(scene);

        stage.setResizable(true);
        stage.setMinWidth(1000);
        stage.setMinHeight(700);

        Animations.applyPageTransition(mainLayout);
        return scene;
    }

    // createTopHeader() removed — replaced by frontend.ui.TopBar

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
        side.getStyleClass().add("sidebar");

        Label brand = new Label("PharmaSync");
        brand.setStyle("-fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: 800;");
        Label moduleLabel = new Label("All Branch Modules");
        moduleLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 11;");

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
        heroTitle.getStyleClass().add("heading-l");
        Label heroSub = new Label("Unified Branch Dashboard");
        heroSub.getStyleClass().add("body-text");
        hero.getChildren().addAll(heroTitle, heroSub);

        // Fetch Live Backend Data
        String salesStr = "$0.00";
        String topMedStr = "No sales today";
        String inventoryRiskStr = "0 Items";
        String inventoryRiskDetail = "All stock optimal";
        String totalInvValue = "0 Items";
        String totalInvDetail = "Valuation: $0.00";
        String customerCountStr = "0";
        String auditStr = "0 Events";
        String auditDetail = "No recent activity";

        try {
            Connection conn = DatabaseManager.getConnection();
            
            // 1. Sales Today
            SaleRepository saleRepo = new MySQLSaleRepository(conn);
            SalesService salesSvc = new SalesService(saleRepo);
            List<Sale> allSales = salesSvc.getAllSales();
            LocalDate today = LocalDate.now();
            
            double totalSalesToday = 0;
            java.util.Map<Integer, Integer> medSalesCount = new java.util.HashMap<>();
            
            for (Sale s : allSales) {
                if (s.getTimestamp() != null && s.getTimestamp().toLocalDate().isEqual(today)) {
                    totalSalesToday += s.getTotalPrice();
                    medSalesCount.put(s.getMedicineId(), medSalesCount.getOrDefault(s.getMedicineId(), 0) + s.getQuantity());
                }
            }
            salesStr = String.format("$%,.2f", totalSalesToday);

            MedicineRepository medRepo = new MySQLMedicineRepository(conn);
            InventoryService invSvc = new InventoryService(medRepo);
            List<Medicine> allMeds = invSvc.getAllMedicines();
            
            if (!medSalesCount.isEmpty()) {
                int topMedId = medSalesCount.entrySet().stream().max(java.util.Map.Entry.comparingByValue()).get().getKey();
                Medicine topMed = invSvc.getMedicineById(topMedId);
                topMedStr = "Top Medicine: " + topMed.getName();
            }

            // 2. Inventory Stats
            int riskCount = 0;
            double totalValuation = 0;
            StringBuilder riskNames = new StringBuilder();
            
            for (Medicine m : allMeds) {
                totalValuation += (m.getPrice() * m.getStockLevel());
                
                boolean atRisk = false;
                if (m.getStockLevel() <= 20) atRisk = true;
                if (m.getExpiryDate() != null && ChronoUnit.DAYS.between(today, m.getExpiryDate()) <= 30) atRisk = true;
                
                if (atRisk) {
                    riskCount++;
                    if (riskCount <= 2) {
                        if (riskNames.length() > 0) riskNames.append(" • ");
                        riskNames.append(m.getName());
                    }
                }
            }
            
            if (riskCount > 0) {
                inventoryRiskStr = "Red-Alert: " + riskCount + " Items";
                inventoryRiskDetail = riskNames.toString() + (riskCount > 2 ? " (+" + (riskCount - 2) + " more)" : "");
            } else {
                inventoryRiskStr = "All Clear";
            }
            totalInvValue = allMeds.size() + " Unique Items";
            totalInvDetail = String.format("Total Valuation: $%,.2f", totalValuation);

            // 3. Customers
            CustomerRepository custRepo = new MySQLCustomerRepository(conn);
            CustomerService custSvc = new CustomerService(custRepo);
            customerCountStr = String.valueOf(custSvc.getAllCustomers().size());

            // 4. Audit Trail
            AuditLogRepository auditRepo = new MySQLAuditLogRepository(conn);
            AuditService auditSvc = new AuditService(auditRepo);
            List<AuditLog> logs = auditSvc.getAllLogs();
            long recentLogs = logs.stream().filter(l -> l.getTimestamp() != null && l.getTimestamp().toLocalDate().isEqual(today)).count();
            auditStr = recentLogs + " Events";
            if (!logs.isEmpty()) {
                auditDetail = "Latest: " + logs.get(logs.size() - 1).getAction();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(14);
        statsGrid.setVgap(14);
        statsGrid.add(createMetricCard("Sales Performance Today", salesStr, topMedStr, UiTheme.COLOR_PRIMARY), 0, 0);
        statsGrid.add(createAlertCard(stage, inventoryRiskStr, inventoryRiskDetail), 1, 0);
        statsGrid.add(createMetricCard("Total Active Inventory", totalInvValue, totalInvDetail, "#0D9488"), 0, 1);
        statsGrid.add(createMetricCard("Customer Database", customerCountStr, "Registered Profiles", UiTheme.COLOR_PRIMARY), 1, 1);
        statsGrid.add(createMetricCard("Security Audit Trail (Today)", auditStr, auditDetail, "#4F46E5"), 0, 2);
        statsGrid.add(createActionCard(stage), 1, 2);

        main.getChildren().addAll(hero, statsGrid);
        return main;
    }

    private static VBox createMetricCard(String title, String value, String subtitle, String accentColor) {
        VBox card = createPanel(14);
        card.setPrefWidth(420);
        card.setMinHeight(140);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("heading-m");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 38; -fx-font-weight: 800; -fx-text-fill: " + accentColor + ";");

        Label sub = new Label(subtitle);
        sub.getStyleClass().add("body-text");

        card.getChildren().addAll(titleLabel, valueLabel, sub);
        return card;
    }

    private static VBox createAlertCard(Stage stage, String stripText, String detailText) {
        VBox card = createPanel(12);
        card.setPrefWidth(420);
        card.setMinHeight(140);

        Label title = new Label("Inventory at Risk (FEFO/Quarantine)");
        title.getStyleClass().add("heading-m");

        Label alertStrip = new Label(stripText);
        alertStrip.getStyleClass().add("alert-strip");

        Label detail = new Label(detailText);
        detail.setWrapText(true);
        detail.getStyleClass().add("body-text");

        Button cta = createCtaButton("View Inventory", () ->
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
        title.getStyleClass().add("heading-m");

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
        panel.getStyleClass().add("card");
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
        button.getStyleClass().add("nav-button");
        if (active) {
            button.getStyleClass().add("nav-button-active");
        }
        button.setOnAction(e -> action.run());
        Animations.bindPulseOnClick(button);
        return button;
    }

    private static Button createCtaButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().addAll("button-base", "primary-button");
        button.setOnAction(e -> action.run());
        Animations.bindPulseOnClick(button);
        return button;
    }

    private static Button createActionButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().addAll("button-base", "secondary-button");
        button.setOnAction(e -> action.run());
        button.setPrefWidth(190);
        Animations.bindPulseOnClick(button);
        return button;
    }
}
