package frontend.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AuditLogs {

    public static Scene createAuditLogsScene(Stage stage) {
        return createAuditLogsSceneInternal(stage);
    }

    private static Scene createAuditLogsSceneInternal(Stage stage) {
        VBox mainContainer = new VBox();
        mainContainer.setStyle("-fx-background-color: #0d1b2a;");

        // Header with back button
        HBox header = createHeader(stage);
        mainContainer.getChildren().add(header);

        // Main content area with side panel
        HBox contentArea = new HBox();
        contentArea.setStyle("-fx-background-color: #0d1b2a;");

        // Left side - Audit logs
        VBox leftPanel = createAuditPanel();
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        // Right side - Account management
        VBox rightPanel = createAccountManagementPanel();
        rightPanel.setPrefWidth(280);

        contentArea.getChildren().addAll(leftPanel, rightPanel);
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        mainContainer.getChildren().add(contentArea);

        return new Scene(mainContainer, 1400, 800);
    }

    private static HBox createHeader(Stage stage) {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background: linear-gradient(to right, #1a2332, #2d3e50); -fx-padding: 12; -fx-border-radius: 5; -fx-border-color: #ff6b6b; -fx-border-width: 0 0 2 0;");

        Button backButton = new Button("← Back to Dashboard");
        backButton.setStyle("-fx-background-color: rgba(255,107,107,0.15); -fx-text-fill: #ff6b6b; -fx-padding: 8 16; -fx-font-size: 12; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-radius: 5;");
        backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-background-color: rgba(255,107,107,0.25); -fx-text-fill: #ff6b6b; -fx-padding: 8 16; -fx-font-size: 12; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-radius: 5;"));
        backButton.setOnMouseExited(e -> backButton.setStyle("-fx-background-color: rgba(255,107,107,0.15); -fx-text-fill: #ff6b6b; -fx-padding: 8 16; -fx-font-size: 12; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-radius: 5;"));
        backButton.setOnAction(e -> {
            Scene dashboardScene = Dashboard.createDashboardScene(stage);
            stage.setScene(dashboardScene);
        });

        Label headerTitle = new Label("🔐 Audit Logs & Security");
        headerTitle.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #ff6b6b;");

        header.getChildren().addAll(backButton, headerTitle);
        HBox.setHgrow(headerTitle, Priority.ALWAYS);

        return header;
    }

    private static VBox createAuditPanel() {
        VBox auditPanel = new VBox(15);
        auditPanel.setPadding(new Insets(15));
        auditPanel.setStyle("-fx-background-color: #0d1b2a;");

        // Search and Filter Section
        HBox searchFilterBox = createSearchFilterBox();
        auditPanel.getChildren().add(searchFilterBox);

        // Audit Table
        TableView<AuditLogEntry> auditTable = createAuditTable();
        VBox.setVgrow(auditTable, Priority.ALWAYS);
        auditPanel.getChildren().add(auditTable);

        return auditPanel;
    }

    private static HBox createSearchFilterBox() {
        HBox searchBox = new HBox(12);
        searchBox.setPadding(new Insets(12));
        searchBox.setStyle("-fx-background-color: #1a2332; -fx-border-color: #ff6b6b; -fx-border-width: 1; -fx-border-radius: 5;");
        searchBox.setAlignment(Pos.CENTER_LEFT);

        // Search by Employee ID
        Label searchLabel = new Label("🔍 Employee ID:");
        searchLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #b0bec5; -fx-font-weight: bold;");

        TextField employeeSearchField = new TextField();
        employeeSearchField.setPromptText("Enter Employee ID...");
        employeeSearchField.setStyle("-fx-control-inner-background: #0d1b2a; -fx-text-fill: #e0e0e0; -fx-prompt-text-fill: #666; -fx-border-color: #ff6b6b; -fx-border-width: 1; -fx-border-radius: 3; -fx-padding: 6;");
        employeeSearchField.setPrefWidth(150);

        // Filter by Criticality
        Label filterLabel = new Label("🚨 Criticality:");
        filterLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #b0bec5; -fx-font-weight: bold;");

        ComboBox<String> criticalityFilter = new ComboBox<>();
        criticalityFilter.getItems().addAll("All", "Low", "Medium", "High");
        criticalityFilter.setValue("All");
        criticalityFilter.setStyle("-fx-control-inner-background: #0d1b2a; -fx-text-fill: #e0e0e0; -fx-border-color: #ff6b6b; -fx-border-width: 1; -fx-border-radius: 3;");
        criticalityFilter.setPrefWidth(120);

        // Search button
        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-padding: 6 16; -fx-font-size: 11; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-radius: 3;");
        searchButton.setOnMouseEntered(e -> searchButton.setStyle("-fx-background-color: #ff5252; -fx-text-fill: white; -fx-padding: 6 16; -fx-font-size: 11; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-radius: 3;"));
        searchButton.setOnMouseExited(e -> searchButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-padding: 6 16; -fx-font-size: 11; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-radius: 3;"));

        // Clear button
        Button clearButton = new Button("Clear");
        clearButton.setStyle("-fx-background-color: #455a64; -fx-text-fill: white; -fx-padding: 6 16; -fx-font-size: 11; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-radius: 3;");
        clearButton.setOnMouseEntered(e -> clearButton.setStyle("-fx-background-color: #546e7a; -fx-text-fill: white; -fx-padding: 6 16; -fx-font-size: 11; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-radius: 3;"));
        clearButton.setOnMouseExited(e -> clearButton.setStyle("-fx-background-color: #455a64; -fx-text-fill: white; -fx-padding: 6 16; -fx-font-size: 11; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-radius: 3;"));

        searchBox.getChildren().addAll(searchLabel, employeeSearchField, filterLabel, criticalityFilter, 
                                       new Separator(javafx.geometry.Orientation.VERTICAL), searchButton, clearButton);

        return searchBox;
    }

    private static TableView<AuditLogEntry> createAuditTable() {
        TableView<AuditLogEntry> table = new TableView<>();
        table.setStyle("-fx-control-inner-background: #1a2332; -fx-table-cell-border-color: #2d3e50; -fx-text-fill: #e0e0e0; -fx-selection-bar: #ff6b6b;");

        // Timestamp Column
        TableColumn<AuditLogEntry, String> timestampCol = new TableColumn<>("⏰ Timestamp");
        timestampCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().timestamp));
        timestampCol.setPrefWidth(140);
        timestampCol.setCellFactory(col -> new TableCell<AuditLogEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #81c784;");
                }
            }
        });

        // User Column
        TableColumn<AuditLogEntry, String> userCol = new TableColumn<>("👤 User");
        userCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().user));
        userCol.setPrefWidth(100);
        userCol.setCellFactory(col -> new TableCell<AuditLogEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #64b5f6;");
                }
            }
        });

        // Action Column
        TableColumn<AuditLogEntry, String> actionCol = new TableColumn<>("⚙️ Action");
        actionCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().action));
        actionCol.setPrefWidth(160);
        actionCol.setCellFactory(col -> new TableCell<AuditLogEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #fff59d;");
                }
            }
        });

        // IP Address Column
        TableColumn<AuditLogEntry, String> ipCol = new TableColumn<>("🌐 IP Address");
        ipCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().ipAddress));
        ipCol.setPrefWidth(130);
        ipCol.setCellFactory(col -> new TableCell<AuditLogEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #ce93d8;");
                }
            }
        });

        // Status Column
        TableColumn<AuditLogEntry, String> statusCol = new TableColumn<>("📊 Status");
        statusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().status));
        statusCol.setPrefWidth(100);
        statusCol.setCellFactory(col -> new TableCell<AuditLogEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Failed") || item.equals("Denied")) {
                        setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #81c784;");
                    }
                }
            }
        });

        table.getColumns().addAll(timestampCol, userCol, actionCol, ipCol, statusCol);
        table.setPrefHeight(400);

        // Add sample audit log data
        table.getItems().addAll(
                new AuditLogEntry("2026-04-28 14:32:15", "EMP001", "Deleted Inventory", "192.168.1.105", "Denied", "High"),
                new AuditLogEntry("2026-04-28 14:15:42", "EMP002", "Failed Login Attempt", "203.45.67.89", "Failed", "High"),
                new AuditLogEntry("2026-04-28 13:48:20", "EMP003", "Inventory Update", "192.168.1.110", "Success", "Low"),
                new AuditLogEntry("2026-04-28 13:22:55", "EMP004", "Sales Transaction", "192.168.1.115", "Success", "Low"),
                new AuditLogEntry("2026-04-28 12:55:30", "EMP005", "Password Reset", "210.12.34.56", "Success", "Medium"),
                new AuditLogEntry("2026-04-28 12:18:45", "EMP001", "Deleted Inventory", "192.168.1.105", "Denied", "High"),
                new AuditLogEntry("2026-04-28 11:30:20", "EMP002", "Failed Login Attempt", "203.45.67.90", "Failed", "High"),
                new AuditLogEntry("2026-04-28 10:45:15", "EMP006", "Created Report", "192.168.1.120", "Success", "Low"),
                new AuditLogEntry("2026-04-28 09:20:05", "EMP007", "User Created", "192.168.1.125", "Success", "Medium"),
                new AuditLogEntry("2026-04-28 08:15:30", "EMP008", "Database Backup", "192.168.1.130", "Success", "Medium")
        );

        // Row highlighting for critical actions
        table.setRowFactory(tv -> new TableRow<AuditLogEntry>() {
            @Override
            protected void updateItem(AuditLogEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    if ((item.action.contains("Deleted") || item.action.contains("Failed Login")) && 
                        (item.status.equals("Denied") || item.status.equals("Failed"))) {
                        setStyle("-fx-background-color: rgba(255, 107, 107, 0.2); -fx-border-color: #ff6b6b; -fx-border-width: 0 0 1 0;");
                    } else if (item.status.equals("Failed") || item.status.equals("Denied")) {
                        setStyle("-fx-background-color: rgba(255, 107, 107, 0.1);");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        return table;
    }

    private static VBox createAccountManagementPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: #1a2332; -fx-border-color: #ff6b6b; -fx-border-width: 2 0 0 2; -fx-border-radius: 0;");

        Label titleLabel = new Label("🔒 Account Control");
        titleLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #ff6b6b;");

        Label lockedAccountsLabel = new Label("Locked Accounts:");
        lockedAccountsLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #b0bec5; -fx-font-weight: bold;");

        // Locked accounts list
        ListView<String> lockedAccountsList = new ListView<>();
        lockedAccountsList.setStyle("-fx-control-inner-background: #0d1b2a; -fx-text-fill: #e0e0e0; -fx-border-color: #ff6b6b; -fx-border-width: 1; -fx-selection-bar: #ff6b6b;");
        lockedAccountsList.setPrefHeight(150);
        lockedAccountsList.getItems().addAll(
                "EMP002 (3 attempts)",
                "EMP005 (5 attempts)",
                "EMP009 (2 attempts)"
        );

        // Unlock button
        Button unlockButton = new Button("🔓 Unlock User");
        unlockButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; -fx-padding: 8 12; -fx-font-size: 11; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-radius: 3; -fx-pref-width: 240;");
        unlockButton.setOnMouseEntered(e -> unlockButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-padding: 8 12; -fx-font-size: 11; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-radius: 3; -fx-pref-width: 240;"));
        unlockButton.setOnMouseExited(e -> unlockButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; -fx-padding: 8 12; -fx-font-size: 11; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-radius: 3; -fx-pref-width: 240;"));
        unlockButton.setOnAction(e -> {
            String selected = lockedAccountsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showAccountActionDialog("Unlock User", "User " + selected + " has been unlocked successfully!");
                lockedAccountsList.getItems().remove(selected);
            } else {
                showAccountActionDialog("Warning", "Please select a user to unlock.");
            }
        });

        // Reset PIN button
        Button resetPinButton = new Button("🔑 Reset PIN");
        resetPinButton.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white; -fx-padding: 8 12; -fx-font-size: 11; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-radius: 3; -fx-pref-width: 240;");
        resetPinButton.setOnMouseEntered(e -> resetPinButton.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-padding: 8 12; -fx-font-size: 11; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-radius: 3; -fx-pref-width: 240;"));
        resetPinButton.setOnMouseExited(e -> resetPinButton.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white; -fx-padding: 8 12; -fx-font-size: 11; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-radius: 3; -fx-pref-width: 240;"));
        resetPinButton.setOnAction(e -> {
            String selected = lockedAccountsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showAccountActionDialog("Reset PIN", "PIN for user " + selected + " has been reset.\nTemporary PIN: 123456");
            } else {
                showAccountActionDialog("Warning", "Please select a user to reset PIN.");
            }
        });

        // Active Sessions Section
        Separator separator1 = new Separator();
        separator1.setStyle("-fx-padding: 0; -fx-text-fill: #455a64;");

        Label activeSessionsLabel = new Label("Active Sessions:");
        activeSessionsLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #b0bec5; -fx-font-weight: bold;");

        ListView<String> activeSessions = new ListView<>();
        activeSessions.setStyle("-fx-control-inner-background: #0d1b2a; -fx-text-fill: #e0e0e0; -fx-border-color: #4caf50; -fx-border-width: 1;");
        activeSessions.setPrefHeight(120);
        activeSessions.getItems().addAll(
                "EMP001 - 192.168.1.105",
                "EMP003 - 192.168.1.110",
                "EMP004 - 192.168.1.115"
        );

        // Terminate Session button
        Button terminateButton = new Button("⛔ Terminate Session");
        terminateButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-padding: 8 12; -fx-font-size: 11; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-radius: 3; -fx-pref-width: 240;");
        terminateButton.setOnMouseEntered(e -> terminateButton.setStyle("-fx-background-color: #f57c00; -fx-text-fill: white; -fx-padding: 8 12; -fx-font-size: 11; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-radius: 3; -fx-pref-width: 240;"));
        terminateButton.setOnMouseExited(e -> terminateButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-padding: 8 12; -fx-font-size: 11; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-radius: 3; -fx-pref-width: 240;"));
        terminateButton.setOnAction(e -> {
            String selected = activeSessions.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showAccountActionDialog("Terminate Session", "Session for " + selected + " has been terminated.");
                activeSessions.getItems().remove(selected);
            } else {
                showAccountActionDialog("Warning", "Please select a session to terminate.");
            }
        });

        panel.getChildren().addAll(
                titleLabel,
                lockedAccountsLabel,
                lockedAccountsList,
                unlockButton,
                resetPinButton,
                separator1,
                activeSessionsLabel,
                activeSessions,
                terminateButton
        );

        VBox.setVgrow(panel, Priority.ALWAYS);

        return panel;
    }

    private static void showAccountActionDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class for audit log entries
    public static class AuditLogEntry {
        public String timestamp;
        public String user;
        public String action;
        public String ipAddress;
        public String status;
        public String criticality;

        public AuditLogEntry(String timestamp, String user, String action, String ipAddress, String status, String criticality) {
            this.timestamp = timestamp;
            this.user = user;
            this.action = action;
            this.ipAddress = ipAddress;
            this.status = status;
            this.criticality = criticality;
        }
    }
}
