package frontend.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Dashboard {
    
    public static Scene createDashboardScene(Stage stage) {
        // Main container
        VBox mainLayout = new VBox(0);
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");
        
        // Header bar
        HBox header = createHeader();
        
        // Main content area
        VBox content = createContent(stage);
        
        mainLayout.getChildren().addAll(header, content);
        VBox.setVgrow(content, javafx.scene.layout.Priority.ALWAYS);
        
        return new Scene(mainLayout, 1200, 800);
    }
    
    private static Stage currentStage;
    
    private static HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background: linear-gradient(to right, #667eea, #764ba2); -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("PharmaSync");
        title.setStyle("-fx-font-size: 32; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label subtitle = new Label("Pharmacy Management System");
        subtitle.setStyle("-fx-font-size: 12; -fx-text-fill: #e0e0e0;");
        
        VBox titleBox = new VBox(3);
        titleBox.getChildren().addAll(title, subtitle);
        
        header.getChildren().add(titleBox);
        HBox.setHgrow(titleBox, javafx.scene.layout.Priority.ALWAYS);
        
        return header;
    }
    
    private static VBox createContent(Stage stage) {
        currentStage = stage;
        VBox content = new VBox(30);
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.TOP_CENTER);
        
        // Welcome section
        Label welcomeLabel = new Label("Welcome to PharmaSync");
        welcomeLabel.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label descriptionLabel = new Label("Choose an action below to get started");
        descriptionLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");
        
        VBox welcomeBox = new VBox(10);
        welcomeBox.setAlignment(Pos.CENTER);
        welcomeBox.getChildren().addAll(welcomeLabel, descriptionLabel);
        
        // Menu cards grid
        GridPane gridPane = new GridPane();
        gridPane.setHgap(30);
        gridPane.setVgap(30);
        gridPane.setAlignment(Pos.CENTER);
        
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
        
        // Reports Card
        VBox reportsCard = createMenuCard(
            "📊",
            "Reports & Analytics",
            "View sales reports, stock analysis,\nexpiry notifications",
            "#FF9800",
            () -> showAlert("Reports feature coming soon!")
        );
        
        // Settings Card
        VBox settingsCard = createMenuCard(
            "⚙️",
            "Settings",
            "Configure system settings,\nuser preferences, database",
            "#9C27B0",
            () -> showAlert("Settings feature coming soon!")
        );
        
        gridPane.add(inventoryCard, 0, 0);
        gridPane.add(salesCard, 1, 0);
        gridPane.add(prescriptionCard, 0, 1);
        gridPane.add(reportsCard, 1, 1);
        gridPane.add(settingsCard, 0, 2);
        
        // Quick stats section
        HBox statsBox = createStatsBox();
        
        content.getChildren().addAll(welcomeBox, gridPane, statsBox);
        VBox.setVgrow(gridPane, javafx.scene.layout.Priority.ALWAYS);
        
        return content;
    }
    
    private static VBox createMenuCard(String icon, String title, String description, String color, Runnable action) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(280);
        card.setPrefHeight(220);
        card.setStyle("-fx-background-color: white; " +
                     "-fx-border-color: " + color + "; " +
                     "-fx-border-width: 2; " +
                     "-fx-border-radius: 10; " +
                     "-fx-background-radius: 10; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        // Icon
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 48;");
        
        // Title
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #333;");
        titleLabel.setWrapText(true);
        
        // Description
        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666; -fx-text-alignment: center;");
        descLabel.setWrapText(true);
        
        // Button
        Button button = new Button("Open →");
        button.setStyle("-fx-background-color: " + color + "; " +
                       "-fx-text-fill: white; " +
                       "-fx-padding: 10 25; " +
                       "-fx-font-size: 12; " +
                       "-fx-font-weight: bold; " +
                       "-fx-cursor: hand; " +
                       "-fx-border-radius: 5; " +
                       "-fx-background-radius: 5;");
        button.setOnAction(e -> action.run());
        
        card.getChildren().addAll(iconLabel, titleLabel, descLabel, button);
        
        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: white; " +
                                                  "-fx-border-color: " + color + "; " +
                                                  "-fx-border-width: 2; " +
                                                  "-fx-border-radius: 10; " +
                                                  "-fx-background-radius: 10; " +
                                                  "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 8, 0, 0, 3);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; " +
                                                 "-fx-border-color: " + color + "; " +
                                                 "-fx-border-width: 2; " +
                                                 "-fx-border-radius: 10; " +
                                                 "-fx-background-radius: 10; " +
                                                 "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);"));
        
        return card;
    }
    
    private static HBox createStatsBox() {
        HBox statsBox = new HBox(20);
        statsBox.setPadding(new Insets(20));
        statsBox.setStyle("-fx-background-color: white; " +
                         "-fx-border-color: #ddd; " +
                         "-fx-border-radius: 8; " +
                         "-fx-background-radius: 8; " +
                         "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 3, 0, 0, 1);");
        statsBox.setAlignment(Pos.CENTER);
        
        // Stat 1: Total Medicines
        VBox stat1 = createStatCard("📦", "Medicines", "250", "#4CAF50");
        
        // Stat 2: Low Stock
        VBox stat2 = createStatCard("⚠️", "Low Stock", "15", "#FF9800");
        
        // Stat 3: Expiring Soon
        VBox stat3 = createStatCard("⏰", "Expiring Soon", "8", "#F44336");
        
        // Stat 4: Today's Sales
        VBox stat4 = createStatCard("💰", "Today's Sales", "Rs. 5,420", "#2196F3");
        
        statsBox.getChildren().addAll(stat1, stat2, stat3, stat4);
        HBox.setHgrow(stat1, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(stat2, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(stat3, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(stat4, javafx.scene.layout.Priority.ALWAYS);
        
        return statsBox;
    }
    
    private static VBox createStatCard(String icon, String label, String value, String color) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #f9f9f9; " +
                     "-fx-border-color: " + color + "; " +
                     "-fx-border-width: 1; " +
                     "-fx-border-radius: 5; " +
                     "-fx-background-radius: 5;");
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 28;");
        
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 11; -fx-text-fill: #666;");
        
        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        card.getChildren().addAll(iconLabel, valueText, labelText);
        
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
