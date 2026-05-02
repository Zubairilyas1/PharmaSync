package frontend.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import frontend.ui.UiTheme;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Settings {
    public static Scene createSettingsScene(Stage stage) {
        return createSettingsSceneInternal(stage);
    }

    private static Scene createSettingsSceneInternal(Stage stage) {
        VBox mainContainer = new VBox();
        mainContainer.setStyle(UiTheme.appBackground());

        // Header with back button
        HBox header = createHeader(stage);
        mainContainer.getChildren().add(header);

        // Scrollable content area
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);

        VBox contentArea = new VBox(20);
        contentArea.setPadding(UiTheme.pagePadding());
        contentArea.setStyle(UiTheme.appBackground());

        // Apply staggered entry animation layout
        javafx.scene.layout.FlowPane sectionsPane = new javafx.scene.layout.FlowPane(25, 25);
        sectionsPane.setAlignment(Pos.TOP_LEFT);

        // System Configuration Section
        VBox systemConfig = createSystemConfigurationSection();
        systemConfig.setPrefWidth(580);
        
        // User Preferences Section
        VBox userPrefs = createUserPreferencesSection();
        userPrefs.setPrefWidth(580);
        
        // Account Management Section
        VBox accountMgmt = createAccountManagementSection();
        accountMgmt.setPrefWidth(580);
        
        // Notification Settings Section
        VBox notifications = createNotificationSettingsSection();
        notifications.setPrefWidth(580);
        
        // Audit Settings Section
        VBox auditSettings = createAuditSettingsSection();
        auditSettings.setPrefWidth(580);
        
        // About Section
        VBox aboutSection = createAboutSection();
        aboutSection.setPrefWidth(580);

        sectionsPane.getChildren().addAll(systemConfig, userPrefs, accountMgmt, notifications, auditSettings, aboutSection);
        frontend.ui.Animations.applyStaggeredEntry(sectionsPane.getChildren());

        contentArea.getChildren().add(sectionsPane);

        // Save and Reset Buttons
        HBox buttonBox = createButtonBox(stage);
        contentArea.getChildren().add(buttonBox);

        scrollPane.setContent(contentArea);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        mainContainer.getChildren().add(scrollPane);

        return new Scene(mainContainer, 1280, 800);
    }

    private static HBox createHeader(Stage stage) {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 16, 12, 16));
        header.setStyle(UiTheme.topBar());

        Button backButton = new Button("← Back to Dashboard");
        backButton.getStyleClass().addAll("glass-button", "button-base");
        backButton.setOnAction(e -> {
            Scene dashboardScene = Dashboard.createDashboardScene(stage);
            stage.setScene(dashboardScene);
        });

        Label headerTitle = new Label("System Settings");
        headerTitle.setStyle(UiTheme.headingM());

        header.getChildren().addAll(backButton, headerTitle);
        HBox.setHgrow(headerTitle, Priority.ALWAYS);

        return header;
    }

    private static VBox createSystemConfigurationSection() {
        VBox section = new VBox(15);
        section.setStyle(UiTheme.card() + " -fx-padding: 20;");
        section.getStyleClass().add("metric-card");

        Label sectionTitle = new Label("System Configuration");
        sectionTitle.getStyleClass().add("text-title");

        // Database Connection
        HBox dbBox = new HBox(15);
        dbBox.setAlignment(Pos.CENTER_LEFT);
        Label dbLabel = new Label("Database Host:");
        dbLabel.setPrefWidth(150);
        TextField dbField = new TextField("localhost:3306");
        dbField.getStyleClass().add("form-input");
        dbField.setPrefWidth(220);
        Button dbTestButton = new Button("Test Connection");
        dbTestButton.getStyleClass().addAll("glass-button", "button-base");
        dbTestButton.setOnAction(e -> showAlert("Database", "✓ Connection successful!\nDatabase: PharmaSync (MySQL)\nStatus: Active"));
        dbBox.getChildren().addAll(dbLabel, dbField, dbTestButton);

        // Backup Path
        HBox backupBox = new HBox(15);
        backupBox.setAlignment(Pos.CENTER_LEFT);
        Label backupLabel = new Label("Backup Path:");
        backupLabel.setPrefWidth(150);
        TextField backupField = new TextField("C:\\PharmaSync\\backups");
        backupField.getStyleClass().add("form-input");
        backupField.setPrefWidth(220);
        Button browseButton = new Button("Browse...");
        browseButton.getStyleClass().addAll("glass-button", "button-base");
        browseButton.setOnAction(e -> showAlert("Backup", "Select backup directory (feature not fully implemented)"));
        backupBox.getChildren().addAll(backupLabel, backupField, browseButton);

        // API Endpoint
        HBox apiBox = new HBox(15);
        apiBox.setAlignment(Pos.CENTER_LEFT);
        Label apiLabel = new Label("API Endpoint:");
        apiLabel.setPrefWidth(150);
        TextField apiField = new TextField("https://api.pharmasync.local/v1");
        apiField.getStyleClass().add("form-input");
        apiField.setPrefWidth(350);
        apiBox.getChildren().addAll(apiLabel, apiField);

        // Auto Backup Schedule
        HBox autoBackupBox = new HBox(15);
        autoBackupBox.setAlignment(Pos.CENTER_LEFT);
        Label autoBackupLabel = new Label("Auto Backup Schedule:");
        autoBackupLabel.setPrefWidth(150);
        ComboBox<String> autoBackupCombo = new ComboBox<>();
        autoBackupCombo.getItems().addAll("Disabled", "Daily", "Weekly", "Monthly");
        autoBackupCombo.setValue("Daily");
        autoBackupCombo.setPrefWidth(150);
        Label autoBackupNote = new Label("(Time: 02:00 AM)");
        autoBackupNote.getStyleClass().add("text-description");
        autoBackupBox.getChildren().addAll(autoBackupLabel, autoBackupCombo, autoBackupNote);

        section.getChildren().addAll(sectionTitle, new Separator(), dbBox, backupBox, apiBox, autoBackupBox);

        return section;
    }

    private static VBox createUserPreferencesSection() {
        VBox section = new VBox(15);
        section.setStyle(UiTheme.card() + " -fx-padding: 20;");
        section.getStyleClass().add("metric-card");

        Label sectionTitle = new Label("User Preferences");
        sectionTitle.getStyleClass().add("text-title");

        // Theme Selection
        HBox themeBox = new HBox(15);
        themeBox.setAlignment(Pos.CENTER_LEFT);
        Label themeLabel = new Label("Application Theme:");
        themeLabel.setPrefWidth(150);
        ComboBox<String> themeCombo = new ComboBox<>();
        themeCombo.getItems().addAll("Light (Default)", "Dark", "Professional Blue", "High Contrast");
        themeCombo.setValue("Light (Default)");
        themeCombo.setPrefWidth(200);
        themeBox.getChildren().addAll(themeLabel, themeCombo);

        // Language Selection
        HBox langBox = new HBox(15);
        langBox.setAlignment(Pos.CENTER_LEFT);
        Label langLabel = new Label("Language:");
        langLabel.setPrefWidth(150);
        ComboBox<String> langCombo = new ComboBox<>();
        langCombo.getItems().addAll("English", "Spanish", "French", "German", "Chinese");
        langCombo.setValue("English");
        langCombo.setPrefWidth(200);
        langBox.getChildren().addAll(langLabel, langCombo);

        // Session Timeout
        HBox timeoutBox = new HBox(15);
        timeoutBox.setAlignment(Pos.CENTER_LEFT);
        Label timeoutLabel = new Label("Session Timeout (minutes):");
        timeoutLabel.setPrefWidth(150);
        Spinner<Integer> timeoutSpinner = new Spinner<>(5, 120, 30);
        timeoutSpinner.setPrefWidth(150);
        Label timeoutNote = new Label("(Auto-logout after inactivity)");
        timeoutNote.getStyleClass().add("text-description");
        timeoutBox.getChildren().addAll(timeoutLabel, timeoutSpinner, timeoutNote);

        // Date Format
        HBox dateBox = new HBox(15);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        Label dateLabel = new Label("Date Format:");
        dateLabel.setPrefWidth(150);
        ComboBox<String> dateCombo = new ComboBox<>();
        dateCombo.getItems().addAll("MM/DD/YYYY", "DD/MM/YYYY", "YYYY-MM-DD");
        dateCombo.setValue("MM/DD/YYYY");
        dateCombo.setPrefWidth(200);
        dateBox.getChildren().addAll(dateLabel, dateCombo);

        section.getChildren().addAll(sectionTitle, new Separator(), themeBox, langBox, timeoutBox, dateBox);

        return section;
    }

    private static VBox createAccountManagementSection() {
        VBox section = new VBox(15);
        section.setStyle(UiTheme.card() + " -fx-padding: 20;");
        section.getStyleClass().add("metric-card");

        Label sectionTitle = new Label("Account Management");
        sectionTitle.getStyleClass().add("text-title");

        // Current User
        HBox userBox = new HBox(15);
        userBox.setAlignment(Pos.CENTER_LEFT);
        Label userLabel = new Label("Current User:");
        userLabel.setPrefWidth(150);
        Label userValue = new Label("Admin (EMP001)");
        userValue.setStyle("-fx-text-fill: #6366F1; -fx-font-weight: bold;");
        userBox.getChildren().addAll(userLabel, userValue);

        // Change Password
        HBox pwdBox = new HBox(15);
        pwdBox.setAlignment(Pos.CENTER_LEFT);
        Label pwdLabel = new Label("Change Password:");
        pwdLabel.setPrefWidth(150);
        Button pwdButton = new Button("Change Password");
        pwdButton.getStyleClass().addAll("glass-button", "button-base");
        pwdButton.setOnAction(e -> showPasswordChangeDialog());
        pwdBox.getChildren().addAll(pwdLabel, pwdButton);

        // Reset PIN
        HBox pinBox = new HBox(15);
        pinBox.setAlignment(Pos.CENTER_LEFT);
        Label pinLabel = new Label("Security PIN:");
        pinLabel.setPrefWidth(150);
        Button pinButton = new Button("Reset PIN");
        pinButton.getStyleClass().addAll("glass-button", "button-base");
        pinButton.setOnAction(e -> showAlert("Security PIN", "PIN reset code: 123456\nSend via email: admin@pharmasync.com"));
        pinBox.getChildren().addAll(pinLabel, pinButton);

        // Two-Factor Authentication
        HBox twoFABox = new HBox(15);
        twoFABox.setAlignment(Pos.CENTER_LEFT);
        Label twoFALabel = new Label("Two-Factor Authentication:");
        twoFALabel.setPrefWidth(150);
        CheckBox twoFACheck = new CheckBox("Enabled");
        twoFACheck.setSelected(true);
        Label twoFANote = new Label("(Status: Active)");
        twoFANote.setStyle("-fx-text-fill: #14B8A6; -fx-font-weight: bold;");
        twoFABox.getChildren().addAll(twoFALabel, twoFACheck, twoFANote);

        section.getChildren().addAll(sectionTitle, new Separator(), userBox, pwdBox, pinBox, twoFABox);

        return section;
    }

    private static VBox createNotificationSettingsSection() {
        VBox section = new VBox(15);
        section.setStyle(UiTheme.card() + " -fx-padding: 20;");
        section.getStyleClass().add("metric-card");

        Label sectionTitle = new Label("Notification Settings");
        sectionTitle.getStyleClass().add("text-title");

        // Alert Preferences
        HBox alertBox = new HBox(15);
        alertBox.setAlignment(Pos.CENTER_LEFT);
        Label alertLabel = new Label("Low Stock Alerts:");
        alertLabel.setPrefWidth(150);
        CheckBox alertCheck = new CheckBox("Enabled");
        alertCheck.setSelected(true);
        Label alertNote = new Label("(When stock < minimum threshold)");
        alertNote.getStyleClass().add("text-description");
        alertBox.getChildren().addAll(alertLabel, alertCheck, alertNote);

        // Email Notifications
        HBox emailBox = new HBox(15);
        emailBox.setAlignment(Pos.CENTER_LEFT);
        Label emailLabel = new Label("Email Notifications:");
        emailLabel.setPrefWidth(150);
        CheckBox emailCheck = new CheckBox("Enabled");
        emailCheck.setSelected(true);
        Label emailValue = new Label("admin@pharmasync.com");
        emailValue.getStyleClass().add("text-description");
        emailBox.getChildren().addAll(emailLabel, emailCheck, emailValue);

        // Expiry Warnings
        HBox expiryBox = new HBox(15);
        expiryBox.setAlignment(Pos.CENTER_LEFT);
        Label expiryLabel = new Label("Expiry Warnings (days):");
        expiryLabel.setPrefWidth(150);
        Spinner<Integer> expirySpinner = new Spinner<>(1, 90, 30);
        expirySpinner.setPrefWidth(100);
        Label expiryNote = new Label("(Notify when medicine expires in X days)");
        expiryNote.getStyleClass().add("text-description");
        expiryBox.getChildren().addAll(expiryLabel, expirySpinner, expiryNote);

        // Failed Login Alerts
        HBox loginBox = new HBox(15);
        loginBox.setAlignment(Pos.CENTER_LEFT);
        Label loginLabel = new Label("Failed Login Alerts:");
        loginLabel.setPrefWidth(150);
        CheckBox loginCheck = new CheckBox("Enabled");
        loginCheck.setSelected(true);
        Label loginNote = new Label("(Alert on suspicious login attempts)");
        loginNote.getStyleClass().add("text-description");
        loginBox.getChildren().addAll(loginLabel, loginCheck, loginNote);

        section.getChildren().addAll(sectionTitle, new Separator(), alertBox, emailBox, expiryBox, loginBox);

        return section;
    }

    private static VBox createAuditSettingsSection() {
        VBox section = new VBox(15);
        section.setStyle(UiTheme.card() + " -fx-padding: 20;");
        section.getStyleClass().add("metric-card");

        Label sectionTitle = new Label("Audit Settings");
        sectionTitle.getStyleClass().add("text-title");

        // Log Retention
        HBox logRetentionBox = new HBox(15);
        logRetentionBox.setAlignment(Pos.CENTER_LEFT);
        Label logLabel = new Label("Log Retention Period:");
        logLabel.setPrefWidth(150);
        ComboBox<String> logCombo = new ComboBox<>();
        logCombo.getItems().addAll("30 days", "90 days", "6 months", "1 year", "Indefinite");
        logCombo.setValue("1 year");
        logCombo.setPrefWidth(150);
        logRetentionBox.getChildren().addAll(logLabel, logCombo);

        // Export Logs
        HBox exportBox = new HBox(15);
        exportBox.setAlignment(Pos.CENTER_LEFT);
        Label exportLabel = new Label("Export Audit Logs:");
        exportLabel.setPrefWidth(150);
        Button exportButton = new Button("Export to CSV");
        exportButton.getStyleClass().addAll("glass-button", "button-base");
        exportButton.setOnAction(e -> showAlert("Export", "Audit logs exported successfully!\nFile: audit_logs_2026-04-29.csv"));
        exportBox.getChildren().addAll(exportLabel, exportButton);

        // Detailed Logging
        HBox detailedBox = new HBox(15);
        detailedBox.setAlignment(Pos.CENTER_LEFT);
        Label detailedLabel = new Label("Detailed Logging:");
        detailedLabel.setPrefWidth(150);
        CheckBox detailedCheck = new CheckBox("Enabled");
        detailedCheck.setSelected(false);
        Label detailedNote = new Label("(Logs all user actions)");
        detailedNote.getStyleClass().add("text-description");
        detailedBox.getChildren().addAll(detailedLabel, detailedCheck, detailedNote);

        // Current Logs Size
        HBox sizeBox = new HBox(15);
        sizeBox.setAlignment(Pos.CENTER_LEFT);
        Label sizeLabel = new Label("Current Log Size:");
        sizeLabel.setPrefWidth(150);
        Label sizeValue = new Label("245 MB");
        sizeValue.setStyle("-fx-font-weight: bold; -fx-text-fill: #0B1120;");
        Button clearButton = new Button("Clear Logs");
        clearButton.setStyle(UiTheme.dangerButton() + " -fx-padding: 6 12;");
        clearButton.getStyleClass().add("button-base");
        clearButton.setOnAction(e -> showAlert("Clear Logs", "Are you sure? This action cannot be undone."));
        sizeBox.getChildren().addAll(sizeLabel, sizeValue, clearButton);

        section.getChildren().addAll(sectionTitle, new Separator(), logRetentionBox, exportBox, detailedBox, sizeBox);

        return section;
    }

    private static VBox createAboutSection() {
        VBox section = new VBox(15);
        section.setStyle(UiTheme.card() + " -fx-padding: 20;");
        section.getStyleClass().add("metric-card");

        Label sectionTitle = new Label("About PharmaSync");
        sectionTitle.getStyleClass().add("text-title");

        // Application Name and Version
        HBox appBox = new HBox(15);
        appBox.setAlignment(Pos.CENTER_LEFT);
        Label appLabel = new Label("Application:");
        appLabel.setPrefWidth(150);
        Label appValue = new Label("PharmaSync v2.1.0");
        appValue.setStyle("-fx-font-weight: bold; -fx-font-size: 11pt;");
        appBox.getChildren().addAll(appLabel, appValue);

        // Release Date
        HBox releaseBox = new HBox(15);
        releaseBox.setAlignment(Pos.CENTER_LEFT);
        Label releaseLabel = new Label("Release Date:");
        releaseLabel.setPrefWidth(150);
        Label releaseValue = new Label("April 29, 2026");
        releaseBox.getChildren().addAll(releaseLabel, releaseValue);

        // License
        HBox licenseBox = new HBox(15);
        licenseBox.setAlignment(Pos.CENTER_LEFT);
        Label licenseLabel = new Label("License:");
        licenseLabel.setPrefWidth(150);
        Label licenseValue = new Label("Enterprise Edition (EE)");
        licenseValue.setStyle("-fx-text-fill: #6366F1;");
        licenseBox.getChildren().addAll(licenseLabel, licenseValue);

        // Build ID
        HBox buildBox = new HBox(15);
        buildBox.setAlignment(Pos.CENTER_LEFT);
        Label buildLabel = new Label("Build ID:");
        buildLabel.setPrefWidth(150);
        Label buildValue = new Label("PS-20260429-001");
        buildValue.setStyle("-fx-font-family: monospace; -fx-font-size: 10pt;");
        buildBox.getChildren().addAll(buildLabel, buildValue);

        // Company Info
        HBox companyBox = new HBox(15);
        companyBox.setAlignment(Pos.CENTER_LEFT);
        Label companyLabel = new Label("Company:");
        companyLabel.setPrefWidth(150);
        VBox companyInfoBox = new VBox(3);
        Label companyName = new Label("PharmaSync Solutions Inc.");
        Label companyWeb = new Label("www.pharmasync.com");
        companyWeb.setStyle("-fx-text-fill: #6366F1;");
        Label companyEmail = new Label("support@pharmasync.com");
        companyInfoBox.getChildren().addAll(companyName, companyWeb, companyEmail);
        companyBox.getChildren().addAll(companyLabel, companyInfoBox);

        // Credits Button
        HBox creditsBox = new HBox(15);
        creditsBox.setAlignment(Pos.CENTER_LEFT);
        Label creditsLabel = new Label("Credits:");
        creditsLabel.setPrefWidth(150);
        Button creditsButton = new Button("View Credits");
        creditsButton.getStyleClass().addAll("glass-button", "button-base");
        creditsButton.setOnAction(e -> showAlert("Credits", "PharmaSync Development Team\n\nLead Developer: Alex Chen\nUI/UX Designer: Sarah Johnson\nDatabase Admin: Mike Rodriguez\nQA Manager: Emma Davis\n\nThank you for using PharmaSync!"));
        creditsBox.getChildren().addAll(creditsLabel, creditsButton);

        section.getChildren().addAll(sectionTitle, new Separator(), appBox, releaseBox, licenseBox, buildBox, companyBox, creditsBox);

        return section;
    }

    private static HBox createButtonBox(Stage stage) {
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button saveButton = new Button("Save Settings");
        saveButton.setStyle(UiTheme.primaryButton() + " -fx-padding: 10 20;");
        saveButton.getStyleClass().add("button-base");
        UiTheme.installPrimaryHover(saveButton);
        saveButton.setOnAction(e -> showAlert("Settings Saved", "All settings have been saved successfully!\nChanges will take effect on next restart."));

        Button resetButton = new Button("Reset to Defaults");
        resetButton.getStyleClass().addAll("glass-button", "button-base");
        resetButton.setOnAction(e -> showAlert("Reset Settings", "Settings have been reset to factory defaults."));

        Button closeButton = new Button("Close");
        closeButton.setStyle(UiTheme.dangerButton() + " -fx-padding: 10 20;");
        closeButton.getStyleClass().add("button-base");
        closeButton.setOnAction(e -> {
            Scene dashboardScene = Dashboard.createDashboardScene(stage);
            stage.setScene(dashboardScene);
        });

        buttonBox.getChildren().addAll(saveButton, resetButton, closeButton);

        return buttonBox;
    }

    private static void showPasswordChangeDialog() {
        VBox dialogContent = new VBox(12);
        dialogContent.setPadding(new Insets(15));

        Label currentPwdLabel = new Label("Current Password:");
        PasswordField currentPwdField = new PasswordField();
        currentPwdField.setPromptText("Enter current password");
        currentPwdField.getStyleClass().add("form-input");

        Label newPwdLabel = new Label("New Password:");
        PasswordField newPwdField = new PasswordField();
        newPwdField.setPromptText("Enter new password");
        newPwdField.getStyleClass().add("form-input");

        Label confirmPwdLabel = new Label("Confirm Password:");
        PasswordField confirmPwdField = new PasswordField();
        confirmPwdField.setPromptText("Confirm new password");
        confirmPwdField.getStyleClass().add("form-input");

        dialogContent.getChildren().addAll(
                currentPwdLabel, currentPwdField,
                newPwdLabel, newPwdField,
                confirmPwdLabel, confirmPwdField
        );

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Update Your Password");
        dialog.getDialogPane().setContent(dialogContent);
        if (dialog.showAndWait().isPresent()) {
            if (newPwdField.getText().equals(confirmPwdField.getText()) && !newPwdField.getText().isEmpty()) {
                showAlert("Success", "✓ Password changed successfully!");
            } else {
                showAlert("Error", "Passwords do not match or are empty!");
            }
        }
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
