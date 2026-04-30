package frontend.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;

public class PrescriptionCheck {
    private static final String APP_BG = "#F4F7FB";
    private static final String PRIMARY_BLUE = "#0056B3";
    
    private static Stage currentStage;
    private static String currentPrescriptionId = "";
    
    public static Scene createPrescriptionCheckScene(Stage stage) {
        currentStage = stage;
        
        // Main container
        VBox mainLayout = new VBox(0);
        mainLayout.setStyle("-fx-background-color: " + APP_BG + ";");
        
        // Header bar
        HBox header = createHeader(stage);
        
        // Main content area with scroll
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-control-inner-background: " + APP_BG + ";");
        VBox content = createContent(stage);
        scrollPane.setContent(content);
        
        mainLayout.getChildren().addAll(header, scrollPane);
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);
        
        return new Scene(mainLayout, 1200, 800);
    }
    
    private static HBox createHeader(Stage stage) {
        HBox header = new HBox();
        header.setPadding(new Insets(12, 16, 12, 16));
        header.setStyle("-fx-background-color: white; -fx-background-radius: 14; -fx-border-color: #E5EAF2; -fx-border-radius: 14; -fx-effect: dropshadow(three-pass-box, rgba(13, 38, 76, 0.10), 16, 0, 0, 4);");
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Back button
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: #EEF4FF; " +
                           "-fx-text-fill: " + PRIMARY_BLUE + "; " +
                           "-fx-padding: 8 15; " +
                           "-fx-font-size: 11; " +
                           "-fx-font-weight: 700; " +
                           "-fx-cursor: hand; " +
                           "-fx-border-radius: 10; " +
                           "-fx-background-radius: 10;");
        backButton.setOnAction(e -> {
            Scene scene = Dashboard.createDashboardScene(stage);
            stage.setScene(scene);
        });
        
        Label title = new Label("Prescription Check");
        title.setStyle("-fx-font-size: 26; -fx-font-weight: 800; -fx-text-fill: #111827;");
        
        Label subtitle = new Label("Verify prescription and perform clinical checks");
        subtitle.setStyle("-fx-font-size: 12; -fx-text-fill: #6B7280;");
        
        VBox titleBox = new VBox(3);
        titleBox.getChildren().addAll(title, subtitle);
        
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        header.getChildren().addAll(backButton, titleBox, spacer);
        
        return header;
    }
    
    private static VBox createContent(Stage stage) {
        VBox content = new VBox(30);
        content.setPadding(new Insets(40));
        content.setStyle("-fx-background-color: " + APP_BG + ";");
        
        // Search section
        VBox searchSection = createSearchSection();
        
        // Doctor profile section (initially hidden)
        VBox doctorSection = createDoctorSection();
        doctorSection.setVisible(false);
        doctorSection.setManaged(false);
        
        // Medicines list section (initially hidden)
        VBox medicinesSection = createMedicinesSection();
        medicinesSection.setVisible(false);
        medicinesSection.setManaged(false);
        
        // Action buttons section
        HBox actionButtons = createActionButtons(stage, doctorSection, medicinesSection);
        actionButtons.setVisible(false);
        actionButtons.setManaged(false);
        
        // Update search field to show/hide sections
        TextField searchField = (TextField) searchSection.getChildren().get(1);
        searchField.setOnKeyReleased(e -> {
            String prescriptionId = searchField.getText().trim();
            if (!prescriptionId.isEmpty() && prescriptionId.length() >= 3) {
                currentPrescriptionId = prescriptionId;
                doctorSection.setVisible(true);
                doctorSection.setManaged(true);
                medicinesSection.setVisible(true);
                medicinesSection.setManaged(true);
                actionButtons.setVisible(true);
                actionButtons.setManaged(true);
            } else {
                doctorSection.setVisible(false);
                doctorSection.setManaged(false);
                medicinesSection.setVisible(false);
                medicinesSection.setManaged(false);
                actionButtons.setVisible(false);
                actionButtons.setManaged(false);
            }
        });
        
        content.getChildren().addAll(searchSection, doctorSection, medicinesSection, actionButtons);
        
        return content;
    }
    
    private static VBox createSearchSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: white; " +
                        "-fx-border-color: #667eea; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        Label titleLabel = new Label("🔍 Scan/Enter Prescription ID");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        TextField searchField = new TextField();
        searchField.setPromptText("Enter prescription ID (e.g., RX-2024-001)...");
        searchField.setPrefHeight(40);
        searchField.setStyle("-fx-font-size: 13; " +
                            "-fx-padding: 10; " +
                            "-fx-border-color: #ddd; " +
                            "-fx-border-radius: 5; " +
                            "-fx-background-radius: 5; " +
                            "-fx-border-width: 1;");
        
        section.getChildren().addAll(titleLabel, searchField);
        
        return section;
    }
    
    private static VBox createDoctorSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: white; " +
                        "-fx-border-color: #2196F3; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        // Doctor Profile Card
        HBox doctorCard = new HBox(20);
        doctorCard.setPadding(new Insets(20));
        doctorCard.setStyle("-fx-background-color: #f0f8ff; " +
                           "-fx-border-color: #2196F3; " +
                           "-fx-border-radius: 8; " +
                           "-fx-background-radius: 8; " +
                           "-fx-border-width: 1;");
        doctorCard.setAlignment(Pos.CENTER_LEFT);
        
        // Doctor icon
        Label doctorIcon = new Label("👨‍⚕️");
        doctorIcon.setStyle("-fx-font-size: 48;");
        
        // Doctor info
        VBox doctorInfo = new VBox(8);
        
        HBox nameBox = new HBox(10);
        Label doctorName = new Label("Dr. Rajesh Kumar");
        doctorName.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label verifiedBadge = new Label("✓ License Verified");
        verifiedBadge.setStyle("-fx-background-color: #4CAF50; " +
                              "-fx-text-fill: white; " +
                              "-fx-padding: 4 8; " +
                              "-fx-font-size: 11; " +
                              "-fx-font-weight: bold; " +
                              "-fx-border-radius: 3; " +
                              "-fx-background-radius: 3;");
        
        nameBox.getChildren().addAll(doctorName, verifiedBadge);
        
        Label license = new Label("License: MED-2024-789456");
        license.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");
        
        Label specialty = new Label("Specialty: General Medicine");
        specialty.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");
        
        Label contact = new Label("Contact: +91-XXXXXXXXXX");
        contact.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");
        
        doctorInfo.getChildren().addAll(nameBox, license, specialty, contact);
        
        doctorCard.getChildren().addAll(doctorIcon, doctorInfo);
        HBox.setHgrow(doctorInfo, javafx.scene.layout.Priority.ALWAYS);
        
        section.getChildren().add(doctorCard);
        
        return section;
    }
    
    private static VBox createMedicinesSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: white; " +
                        "-fx-border-color: #FF9800; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        Label titleLabel = new Label("📋 Prescribed Medicines");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        // Medicines list
        VBox medicinesList = new VBox(12);
        medicinesList.setStyle("-fx-background-color: #f9f9f9; " +
                              "-fx-border-color: #eee; " +
                              "-fx-border-radius: 5; " +
                              "-fx-background-radius: 5; " +
                              "-fx-border-width: 1;");
        medicinesList.setPadding(new Insets(15));
        
        // Sample medicines
        medicinesList.getChildren().addAll(
            createMedicineItem("Amoxicillin 500mg", "1 tablet", "Twice daily", "#4CAF50"),
            createMedicineItem("Paracetamol 650mg", "1 tablet", "Every 6 hours (as needed)", "#2196F3"),
            createMedicineItem("Omeprazole 20mg", "1 capsule", "Once daily (morning)", "#FF9800")
        );
        
        // Check interactions button
        Button checkInteractionsBtn = new Button("🔬 Check for Interactions");
        checkInteractionsBtn.setPrefWidth(200);
        checkInteractionsBtn.setStyle("-fx-background-color: #FF9800; " +
                                      "-fx-text-fill: white; " +
                                      "-fx-padding: 12 25; " +
                                      "-fx-font-size: 12; " +
                                      "-fx-font-weight: bold; " +
                                      "-fx-cursor: hand; " +
                                      "-fx-border-radius: 5; " +
                                      "-fx-background-radius: 5;");
        checkInteractionsBtn.setOnAction(e -> {
            showToast("✓ Clinical Validation Successful", "#4CAF50");
        });
        
        VBox buttonBox = new VBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(checkInteractionsBtn);
        
        section.getChildren().addAll(titleLabel, medicinesList, buttonBox);
        
        return section;
    }
    
    private static HBox createMedicineItem(String name, String dosage, String frequency, String color) {
        HBox item = new HBox(15);
        item.setPadding(new Insets(12));
        item.setStyle("-fx-background-color: white; " +
                     "-fx-border-color: " + color + "; " +
                     "-fx-border-left-width: 4; " +
                     "-fx-border-right-width: 0; " +
                     "-fx-border-top-width: 0; " +
                     "-fx-border-bottom-width: 0; " +
                     "-fx-border-radius: 3;");
        item.setAlignment(Pos.CENTER_LEFT);
        
        Label medicineIcon = new Label("💊");
        medicineIcon.setStyle("-fx-font-size: 24;");
        
        VBox medicineInfo = new VBox(4);
        
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label dosageLabel = new Label("Dosage: " + dosage);
        dosageLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666;");
        
        Label frequencyLabel = new Label("Frequency: " + frequency);
        frequencyLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666;");
        
        medicineInfo.getChildren().addAll(nameLabel, dosageLabel, frequencyLabel);
        
        item.getChildren().addAll(medicineIcon, medicineInfo);
        HBox.setHgrow(medicineInfo, javafx.scene.layout.Priority.ALWAYS);
        
        return item;
    }
    
    private static HBox createActionButtons(Stage stage, VBox doctorSection, VBox medicinesSection) {
        HBox actionBox = new HBox(20);
        actionBox.setPadding(new Insets(30, 20, 30, 20));
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setStyle("-fx-background-color: white; " +
                          "-fx-border-color: #ddd; " +
                          "-fx-border-radius: 8; " +
                          "-fx-background-radius: 8; " +
                          "-fx-border-width: 1; " +
                          "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        // Proceed to Dispensing button
        Button proceedBtn = new Button("💳 Proceed to Dispensing");
        proceedBtn.setPrefWidth(250);
        proceedBtn.setStyle("-fx-background: linear-gradient(to right, #667eea, #764ba2); " +
                           "-fx-text-fill: white; " +
                           "-fx-padding: 15 30; " +
                           "-fx-font-size: 13; " +
                           "-fx-font-weight: bold; " +
                           "-fx-cursor: hand; " +
                           "-fx-border-radius: 5; " +
                           "-fx-background-radius: 5;");
        proceedBtn.setOnAction(e -> {
            // Navigate to Sales Terminal with prescription context
            Scene scene = SalesTerminal.createSalesTerminalScene(stage);
            stage.setScene(scene);
        });
        
        // Cancel button
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setPrefWidth(150);
        cancelBtn.setStyle("-fx-background-color: #f44336; " +
                          "-fx-text-fill: white; " +
                          "-fx-padding: 15 30; " +
                          "-fx-font-size: 13; " +
                          "-fx-font-weight: bold; " +
                          "-fx-cursor: hand; " +
                          "-fx-border-radius: 5; " +
                          "-fx-background-radius: 5;");
        cancelBtn.setOnAction(e -> {
            Scene scene = Dashboard.createDashboardScene(stage);
            stage.setScene(scene);
        });
        
        actionBox.getChildren().addAll(proceedBtn, cancelBtn);
        
        return actionBox;
    }
    
    private static void showToast(String message, String bgColor) {
        Stage toastStage = new Stage();
        toastStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        
        Label toastLabel = new Label(message);
        toastLabel.setStyle("-fx-background-color: " + bgColor + "; " +
                           "-fx-text-fill: white; " +
                           "-fx-padding: 15 30; " +
                           "-fx-font-size: 13; " +
                           "-fx-font-weight: bold; " +
                           "-fx-border-radius: 5; " +
                           "-fx-background-radius: 5;");
        
        VBox root = new VBox(toastLabel);
        root.setStyle("-fx-background-color: transparent;");
        
        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        
        toastStage.setScene(scene);
        toastStage.setWidth(350);
        toastStage.setHeight(50);
        toastStage.setX(500);
        toastStage.setY(300);
        toastStage.show();
        
        // Auto-hide after 3 seconds
        new javafx.animation.Timeline(new javafx.animation.KeyFrame(
            javafx.util.Duration.seconds(3),
            event -> toastStage.close()
        )).play();
    }
}
