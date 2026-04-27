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
        
        return new Scene(mainLayout, 720, 720);
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
        VBox content = new VBox(15); 
        content.setPadding(new Insets(20)); 
        content.setAlignment(Pos.TOP_CENTER);
        
        // Welcome section
        Label welcomeLabel = new Label("Welcome to PharmaSync");
        welcomeLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill:rgb(0, 39, 215);"); // Reduced font size

        Label descriptionLabel = new Label("Choose an action below to get started");
        descriptionLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");
        
        VBox welcomeBox = new VBox(5); 
        welcomeBox.setAlignment(Pos.CENTER);
        welcomeBox.getChildren().addAll(welcomeLabel, descriptionLabel);
        
        // Menu cards grid
        GridPane gridPane = new GridPane();
        gridPane.setHgap(15); 
        gridPane.setVgap(15); 
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
        
        // Quick stats section is removed to save space
        
        content.getChildren().addAll(welcomeBox, gridPane);
        VBox.setVgrow(gridPane, javafx.scene.layout.Priority.ALWAYS);
        
        return content;
    }
    
    private static VBox createMenuCard(String icon, String title, String description, String color, Runnable action) {
        VBox card = new VBox(5); 
        card.setPadding(new Insets(10)); 
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(220); 
        card.setPrefHeight(150); 
        card.setStyle("-fx-background-color: white; " +
                     "-fx-border-color: " + color + "; " +
                     "-fx-border-width: 2; " +
                     "-fx-border-radius: 10; " +
                     "-fx-background-radius: 10; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        // Icon
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 32;"); 
        
        // Title
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #333;"); // Reduced font size
        titleLabel.setWrapText(true);
        
        // Description
        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #666; -fx-text-alignment: center;"); // Reduced font size
        descLabel.setWrapText(true);
        
        // Button
        Button button = new Button("Open →");
        button.setStyle("-fx-background-color: " + color + "; " +
                       "-fx-text-fill: white; " +
                       "-fx-padding: 5 15; " + 
                       "-fx-font-size: 11; " +
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
    
    private static void showAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Coming Soon");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
