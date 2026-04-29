package frontend.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Dashboard {
    
    public static Scene createDashboardScene(Stage stage) {
        // Main container using BorderPane for better layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #f8f9fa, #e9ecef);");
        
        // Header bar
        HBox header = createHeader();
        mainLayout.setTop(header);
        
        // Main content area with scroll
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        VBox content = createContent(stage);
        scrollPane.setContent(content);
        
        mainLayout.setCenter(scrollPane);
        
        Scene scene = new Scene(mainLayout, 1200, 800);
        stage.setResizable(true);
        stage.setMinWidth(1000);
        stage.setMinHeight(700);
        
        return scene;
    }
    
    private static Stage currentStage;
    
    private static HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setStyle("-fx-background: linear-gradient(to right, #1e3c72, #2a5298); -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 3);");
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("🏥 PharmaSync");
        title.setStyle("-fx-font-size: 36; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(three-pass-box, rgba(255,255,255,0.5), 1, 0, 0, 0);");
        
        Label subtitle = new Label("Advanced Pharmacy Management System");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #e8f4f8; -fx-font-weight: 300;");
        
        VBox titleBox = new VBox(5);
        titleBox.getChildren().addAll(title, subtitle);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        
        // Add a logout button or user info
        Button userButton = new Button("👤 Admin");
        userButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-border-color: rgba(255,255,255,0.3); -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 8 15; -fx-font-size: 12;");
        userButton.setOnAction(e -> {
            // Handle user menu
        });
        
        header.getChildren().addAll(titleBox);
        HBox.setHgrow(titleBox, javafx.scene.layout.Priority.ALWAYS);
        header.getChildren().add(userButton);
        
        return header;
    }
    
    private static VBox createContent(Stage stage) {
        currentStage = stage;
        VBox content = new VBox(25); 
        content.setPadding(new Insets(30)); 
        content.setAlignment(Pos.TOP_CENTER);
        content.setStyle("-fx-background-color: transparent;");
        
        // Welcome section
        Label welcomeLabel = new Label("Welcome to PharmaSync");
        welcomeLabel.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label descriptionLabel = new Label("Streamline your pharmacy operations with our comprehensive management system");
        descriptionLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #7f8c8d; -fx-text-alignment: center;");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(600);
        
        VBox welcomeBox = new VBox(10); 
        welcomeBox.setAlignment(Pos.CENTER);
        welcomeBox.getChildren().addAll(welcomeLabel, descriptionLabel);
        
        // Quick Stats Section
        HBox statsBox = createQuickStats();
        
        // Menu cards grid
        GridPane gridPane = new GridPane();
        gridPane.setHgap(20); 
        gridPane.setVgap(20); 
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(20, 0, 20, 0));
        
        // Inventory Card
        VBox inventoryCard = createMenuCard(
            "📦",
            "Inventory Management",
            "Manage medicines, track stock,\nmonitor expiry dates",
            "#4CAF50",
            () -> {
                Scene scene = InventoryList.createInventoryListScene(stage);
                stage.setScene(scene);
            }
        );
        
        // Sales Terminal Card
        VBox salesCard = createMenuCard(
            "💳",
            "Sales Terminal",
            "Process orders, calculate dosage,\nvalidate clinical checks",
            "#2196F3",
            () -> {
                Scene scene = SalesTerminal.createSalesTerminalScene(stage);
                stage.setScene(scene);
            }
        );
        
        // Prescription Check Card
        VBox prescriptionCard = createMenuCard(
            "📋",
            "Prescription Check",
            "Verify prescriptions, check for\nclinical interactions",
            "#00BCD4",
            () -> {
                Scene scene = PrescriptionCheck.createPrescriptionCheckScene(stage);
                stage.setScene(scene);
            }
        );
        
        // Returns Management Card
        VBox returnsCard = createMenuCard(
            "↩️",
            "Returns Management",
            "Process returns, manage refunds,\nupdate inventory",
            "#FF5722",
            () -> {
                Scene scene = Returns.createReturnsScene(stage);
                stage.setScene(scene);
            }
        );
        
        // Reports Card
        VBox reportsCard = createMenuCard(
            "📊",
            "Reports & Analytics",
            "View sales reports, stock analysis,\nexpiry notifications",
            "#FF9800",
            () -> {
                Scene scene = Reports.createReportsScene(stage);
                stage.setScene(scene);
            }
        );
        
        // Audit Logs Card
        VBox auditLogsCard = createMenuCard(
            "🔐",
            "Audit Logs & Security",
            "Monitor user activities, manage\nsecurity and account control",
            "#ff6b6b",
            () -> {
                Scene scene = AuditLogs.createAuditLogsScene(stage);
                stage.setScene(scene);
            }
        );
        
        // Procurement Card
        VBox procurementCard = createMenuCard(
            "📦",
            "Procurement Management",
            "Manage stock levels, create\npurchase orders, supplier selection",
            "#FF6F00",
            () -> {
                Scene scene = Procurement.createProcurementScene(stage);
                stage.setScene(scene);
            }
        );
        
        // Settings Card
        VBox settingsCard = createMenuCard(
            "⚙️",
            "Settings",
            "Configure system settings,\nuser preferences, database",
            "#9C27B0",
            () -> {
                Scene scene = Settings.createSettingsScene(stage);
                stage.setScene(scene);
            }
        );
        
        gridPane.add(inventoryCard, 0, 0);
        gridPane.add(salesCard, 1, 0);
        gridPane.add(prescriptionCard, 2, 0);
        gridPane.add(returnsCard, 3, 0);
        gridPane.add(reportsCard, 0, 1);
        gridPane.add(auditLogsCard, 1, 1);
        gridPane.add(procurementCard, 2, 1);
        gridPane.add(settingsCard, 3, 1);
        
        content.getChildren().addAll(welcomeBox, statsBox, gridPane);
        
        return content;
    }
    
    private static HBox createQuickStats() {
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(20));
        statsBox.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        VBox stat1 = createStatCard("📊", "Total Medicines", "1,247", "#4CAF50");
        VBox stat2 = createStatCard("💰", "Today's Sales", "$2,450", "#2196F3");
        VBox stat3 = createStatCard("⚠️", "Low Stock Items", "12", "#FF5722");
        VBox stat4 = createStatCard("📅", "Expiring Soon", "8", "#FF9800");
        
        statsBox.getChildren().addAll(stat1, stat2, stat3, stat4);
        return statsBox;
    }
    
    private static VBox createStatCard(String icon, String title, String value, String color) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(180);
        card.setStyle("-fx-background-color: white; -fx-border-color: " + color + "; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");
        
        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);
        return card;
    }
    
    private static VBox createMenuCard(String icon, String title, String description, String color, Runnable action) {
        VBox card = new VBox(8); 
        card.setPadding(new Insets(20)); 
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(250); 
        card.setPrefHeight(180);
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setStyle("-fx-background-color: white; " +
                     "-fx-border-color: " + color + "; " +
                     "-fx-border-width: 2; " +
                     "-fx-border-radius: 15; " +
                     "-fx-background-radius: 15; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 8, 0, 0, 3);");
        
        // Icon
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 40;"); 
        
        // Title
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        titleLabel.setWrapText(true);
        titleLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        // Description
        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #7f8c8d; -fx-text-alignment: center;");
        descLabel.setWrapText(true);
        descLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        // Button
        Button button = new Button("Access Module →");
        button.setPrefWidth(200);
        button.setPrefHeight(35);
        button.setStyle("-fx-background-color: " + color + "; " +
                       "-fx-text-fill: white; " +
                       "-fx-padding: 10 20; " + 
                       "-fx-font-size: 14; " +
                       "-fx-font-weight: bold; " +
                       "-fx-cursor: hand; " +
                       "-fx-border-radius: 8; " +
                       "-fx-background-radius: 8; " +
                       "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 3, 0, 0, 1);");
        button.setOnAction(e -> {
            System.out.println("Button clicked for: " + title);
            action.run();
        });
        
        card.getChildren().addAll(iconLabel, titleLabel, descLabel, button);
        
        // Make card clickable as well
        card.setOnMouseClicked(e -> {
            System.out.println("Card clicked for: " + title);
            action.run();
        });
        
        // Enhanced hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #f8f9fa; " +
                         "-fx-border-color: " + color + "; " +
                         "-fx-border-width: 3; " +
                         "-fx-border-radius: 15; " +
                         "-fx-background-radius: 15; " +
                         "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 8);");
            iconLabel.setScaleX(1.1);
            iconLabel.setScaleY(1.1);
        });
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; " +
                         "-fx-border-color: " + color + "; " +
                         "-fx-border-width: 2; " +
                         "-fx-border-radius: 15; " +
                         "-fx-background-radius: 15; " +
                         "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 8, 0, 0, 3);");
            iconLabel.setScaleX(1.0);
            iconLabel.setScaleY(1.0);
        });
        
        return card;
    }
    
    private static void showAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Coming Soon");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
