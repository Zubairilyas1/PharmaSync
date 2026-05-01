package frontend.pages;

import backend.database.DatabaseManager;
import backend.exceptions.InventoryException;
import backend.models.User;
import backend.services.InventoryService;
import backend.repositories.MySQLMedicineRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import frontend.ui.UiTheme;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class InventoryList {
    public static Scene createInventoryListScene(Stage stage) {
        InventoryService inventoryService = null;
        try {
            java.sql.Connection conn = DatabaseManager.getConnection();
            MySQLMedicineRepository repo = new MySQLMedicineRepository(conn);
            inventoryService = new InventoryService(repo);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Connection Error", "Unable to connect to the database.");
            return new Scene(new VBox(new Label("Database Connection Error")), 720, 720);
        }
        
        final InventoryService finalInventoryService = inventoryService;

        VBox root = new VBox(15);
        root.setPadding(UiTheme.pagePadding());
        root.setStyle(UiTheme.appBackground());
        
        // Header with back button
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 16, 12, 16));
        header.setStyle(UiTheme.topBar());
        
        Button backButton = new Button("← Back to Dashboard");
        backButton.setStyle(UiTheme.secondaryButton() + " -fx-padding: 8 14;");
        backButton.setOnAction(e -> {
            Scene dashboardScene = Dashboard.createDashboardScene(stage);
            stage.setScene(dashboardScene);
        });
        
        Label headerTitle = new Label("Inventory Management");
        headerTitle.setStyle(UiTheme.headingM());
        
        header.getChildren().addAll(backButton, headerTitle);
        HBox.setHgrow(headerTitle, Priority.ALWAYS);
        root.getChildren().add(header);
        
        // Title
        Label titleLabel = new Label("Inventory Management");
        titleLabel.setStyle(UiTheme.headingL());
        
        // Search bar container
        HBox searchContainer = new HBox(10);
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.setPadding(new Insets(10));
        searchContainer.setStyle(UiTheme.card());
        
        Label searchLabel = new Label("Search by Medicine Name:");
        searchLabel.setStyle(UiTheme.bodyText());
        
        TextField searchField = new TextField();
        searchField.setPromptText("Enter medicine name...");
        searchField.setStyle(UiTheme.input());
        searchField.setPrefWidth(300);
        
        searchContainer.getChildren().addAll(searchLabel, searchField);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        
        // Create table
        TableView<backend.models.Medicine> table = new TableView<>();
        table.setStyle("-fx-font-size: 12;");
        table.setPrefHeight(400);
        
        ObservableList<backend.models.Medicine> medicines = FXCollections.observableArrayList();
        try {
            medicines.addAll(finalInventoryService.getAllMedicines());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load inventory: " + e.getMessage());
        }
        
        FilteredList<backend.models.Medicine> filteredMedicines = new FilteredList<>(medicines, p -> true);
        table.setItems(filteredMedicines);
        
        // Name column
        TableColumn<backend.models.Medicine, String> nameColumn = new TableColumn<>("Medicine Name");
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        nameColumn.setPrefWidth(150);
        
        // Batch ID column
        TableColumn<backend.models.Medicine, String> batchIdColumn = new TableColumn<>("Batch ID");
        batchIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBatchId()));
        batchIdColumn.setPrefWidth(100);
        
        // Expiry Date column
        TableColumn<backend.models.Medicine, String> expiryDateColumn = new TableColumn<>("Expiry Date");
        expiryDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getExpiryDate().toString()));
        expiryDateColumn.setPrefWidth(120);
        
        // Quantity column
        TableColumn<backend.models.Medicine, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getStockLevel()));
        quantityColumn.setPrefWidth(80);
        
        // Custom cell factory for Quantity column to make low stock red
        quantityColumn.setCellFactory(column -> {
            return new TableCell<backend.models.Medicine, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item.toString());
                        // Apply red text if quantity is less than 10 (background handled by row)
                        if (item < 10) {
                            setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold; -fx-alignment: center;");
                        } else {
                            setStyle("-fx-text-fill: black; -fx-alignment: center;");
                        }
                    }
                }
            };
        });

        // Status column with badge
        TableColumn<backend.models.Medicine, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(getStatusBadge(cellData.getValue())));
        statusColumn.setPrefWidth(150);
        
        table.getColumns().addAll(nameColumn, batchIdColumn, expiryDateColumn, quantityColumn, statusColumn);
        
        // Row-level styling for low stock warning
        table.setRowFactory(tv -> new TableRow<backend.models.Medicine>() {
            @Override
            protected void updateItem(backend.models.Medicine medicine, boolean empty) {
                super.updateItem(medicine, empty);
                if (empty || medicine == null) {
                    setStyle("");
                } else if (medicine.getStockLevel() < 10) {
                    setStyle("-fx-background-color: #ffcccc;"); // Light red background for low stock
                } else {
                    setStyle(""); 
                }
            }
        });
        
        // Search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredMedicines.setPredicate(medicine -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                return medicine.getName().toLowerCase().contains(newValue.toLowerCase());
            });
        });
        
        // Summary info - Define early so buttons can reference it
        HBox summaryBox = new HBox(20);
        summaryBox.setAlignment(Pos.CENTER_LEFT);
        summaryBox.setPadding(new Insets(10));
        summaryBox.setStyle(UiTheme.card());
        
        Label totalLabel = new Label();
        totalLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: " + UiTheme.COLOR_TEXT_PRIMARY + ";");
        
        Label lowStockLabel = new Label();
        lowStockLabel.setStyle("-fx-font-size: 12; -fx-text-fill: " + UiTheme.COLOR_DANGER_TEXT + ";");
        
        Label expiringLabel = new Label();
        expiringLabel.setStyle("-fx-font-size: 12; -fx-text-fill: " + UiTheme.COLOR_WARNING_TEXT + ";");
        
        summaryBox.getChildren().addAll(totalLabel, lowStockLabel, expiringLabel);
        
        // Update summary initially
        updateSummaryWithLabels(medicines, totalLabel, lowStockLabel, expiringLabel);
        
        // Action buttons container
        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.setPadding(new Insets(10));
        
        Button addButton = new Button("Add Medicine");
        addButton.setStyle(UiTheme.primaryButton() + " -fx-padding: 10 12;");
        UiTheme.installPrimaryHover(addButton);
        addButton.setOnAction(e -> {
            AddMedicinePage.showAddMedicineDialog(stage, finalInventoryService, medicine -> {
                medicines.add(medicine);
                updateSummary(medicines, summaryBox);
                return null;
            });
        });
        
        Button editButton = new Button("Edit");
        editButton.setStyle(UiTheme.secondaryButton() + " -fx-padding: 8 12;");
        editButton.setOnAction(e -> {
            backend.models.Medicine selectedMedicine = table.getSelectionModel().getSelectedItem();
            if (selectedMedicine == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a medicine to edit!");
                return;
            }
            EditMedicinePage.showEditMedicineDialog(stage, finalInventoryService, selectedMedicine, medicine -> {
                // To reflect the update, we can simply refresh the table and update the list if needed
                int index = medicines.indexOf(selectedMedicine);
                if(index != -1) {
                    medicines.set(index, medicine); // replace with updated medicine
                }
                table.refresh();
                updateSummary(medicines, summaryBox);
                return null;
            });
        });
        
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle(UiTheme.dangerButton() + " -fx-padding: 8 12;");
        deleteButton.setOnAction(e -> {
            backend.models.Medicine selectedMedicine = table.getSelectionModel().getSelectedItem();
            if (selectedMedicine == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a medicine to delete!");
                return;
            }
            
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, 
                "Are you sure you want to delete " + selectedMedicine.getName() + "?", 
                ButtonType.YES, ButtonType.NO);
            confirmAlert.showAndWait();
            
            if (confirmAlert.getResult() == ButtonType.YES) {
                try {
                    finalInventoryService.removeMedicine(selectedMedicine.getId());
                    medicines.remove(selectedMedicine);
                    updateSummary(medicines, summaryBox);
                } catch (InventoryException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete medicine: " + ex.getMessage());
                }
            }
        });
        
        buttonContainer.getChildren().addAll(addButton, editButton, deleteButton);
        
        // Add all to root
        root.getChildren().addAll(
            titleLabel,
            searchContainer,
            new Label("Medicine Inventory"),
            table,
            summaryBox,
            buttonContainer
        );
        
        VBox.setVgrow(table, Priority.ALWAYS);
        
        return new Scene(root, 720, 720);
    }
    
    /**
     * Update summary box
     */
    private static void updateSummary(ObservableList<backend.models.Medicine> medicines, HBox summaryBox) {
        if (summaryBox.getChildren().size() >= 3) {
            Label totalLabel = (Label) summaryBox.getChildren().get(0);
            Label lowStockLabel = (Label) summaryBox.getChildren().get(1);
            Label expiringLabel = (Label) summaryBox.getChildren().get(2);
            updateSummaryWithLabels(medicines, totalLabel, lowStockLabel, expiringLabel);
        }
    }
    
    private static void updateSummaryWithLabels(ObservableList<backend.models.Medicine> medicines, 
                                             Label totalLabel, Label lowStockLabel, Label expiringLabel) {
        int total = medicines.size();
        long lowStock = medicines.stream().filter(m -> m.getStockLevel() < 10).count();
        long expiringSoon = medicines.stream().filter(m -> {
            LocalDate now = LocalDate.now();
            long daysBetween = ChronoUnit.DAYS.between(now, m.getExpiryDate());
            return daysBetween > 0 && daysBetween <= 30;
        }).count();
        
        totalLabel.setText("Total Medicines: " + total);
        lowStockLabel.setText("Low Stock: " + lowStock + " items");
        expiringLabel.setText("Expiring Soon (<30 days): " + expiringSoon + " items");
    }
    
    private static String getStatusBadge(backend.models.Medicine medicine) {
        LocalDate now = LocalDate.now();
        if (medicine.getExpiryDate().isBefore(now)) {
            return "Expired (No Sale)";
        }
        
        long daysUntilExpiry = ChronoUnit.DAYS.between(now, medicine.getExpiryDate());
        if (daysUntilExpiry <= 30) {
            return "Expiring Soon";
        }
        
        if (medicine.getStockLevel() == 0) {
            return "Out of Stock";
        }
        
        // Return the actual status (Active, Inactive, Discontinued)
        return medicine.getStatus() != null && !medicine.getStatus().isEmpty() ? medicine.getStatus() : "Active";
    }
    
    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
