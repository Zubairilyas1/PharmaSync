package frontend.pages;

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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class InventoryList {
    
    // Medicine data model
    public static class Medicine {
        private String name;
        private String batchId;
        private LocalDate expiryDate;
        private int quantity;
        private String status;
        
        public Medicine(String name, String batchId, LocalDate expiryDate, int quantity, String status) {
            this.name = name;
            this.batchId = batchId;
            this.expiryDate = expiryDate;
            this.quantity = quantity;
            this.status = status;
        }
        
        // Getters and Setters
        public String getName() { return name; }
        public String getBatchId() { return batchId; }
        public LocalDate getExpiryDate() { return expiryDate; }
        public int getQuantity() { return quantity; }
        public String getStatus() { return status; }
        
        public void setName(String name) { this.name = name; }
        public void setBatchId(String batchId) { this.batchId = batchId; }
        public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public void setStatus(String status) { this.status = status; }
    }
    
    public static Scene createInventoryListScene(Stage stage) {
        return createInventoryListScene(stage, null);
    }
    
    public static Scene createInventoryListScene(Stage stage, App app) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        // Top navigation bar
        HBox navBar = new HBox(10);
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setPadding(new Insets(10));
        navBar.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");
        
        Button backButton = new Button("← Back to Dashboard");
        backButton.setStyle("-fx-padding: 8; -fx-background-color: #667eea; -fx-text-fill: white; -fx-cursor: hand;");
        if (app != null) {
            backButton.setOnAction(e -> app.showScene("dashboard"));
        } else {
            backButton.setDisable(true);
        }
        
        navBar.getChildren().add(backButton);
        
        // Title
        Label titleLabel = new Label("Inventory Management");
        titleLabel.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        // Search bar container
        HBox searchContainer = new HBox(10);
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.setPadding(new Insets(10));
        searchContainer.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");
        
        Label searchLabel = new Label("Search by Medicine Name:");
        searchLabel.setStyle("-fx-font-size: 12;");
        
        TextField searchField = new TextField();
        searchField.setPromptText("Enter medicine name...");
        searchField.setStyle("-fx-padding: 8; -fx-font-size: 12;");
        searchField.setPrefWidth(300);
        
        searchContainer.getChildren().addAll(searchLabel, searchField);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        
        // Create sample data
        ObservableList<Medicine> medicines = FXCollections.observableArrayList(
            new Medicine("Aspirin", "BATCH001", LocalDate.now().plusDays(15), 5, "Active"),
            new Medicine("Paracetamol", "BATCH002", LocalDate.now().plusDays(60), 25, "Active"),
            new Medicine("Amoxicillin", "BATCH003", LocalDate.now().plusDays(3), 8, "Active"),
            new Medicine("Ibuprofen", "BATCH004", LocalDate.now().plusDays(120), 50, "Active"),
            new Medicine("Metformin", "BATCH005", LocalDate.now().minusDays(5), 3, "Expired"),
            new Medicine("Lisinopril", "BATCH006", LocalDate.now().plusDays(45), 12, "Active"),
            new Medicine("Atorvastatin", "BATCH007", LocalDate.now().plusDays(90), 30, "Active"),
            new Medicine("Cetirizine", "BATCH008", LocalDate.now().plusDays(25), 9, "Active")
        );
        
        // Create filtered list
        FilteredList<Medicine> filteredMedicines = new FilteredList<>(medicines, p -> true);
        
        // Create table
        TableView<Medicine> table = new TableView<>();
        table.setItems(filteredMedicines);
        table.setStyle("-fx-font-size: 12;");
        table.setPrefHeight(400);
        
        // Name column
        TableColumn<Medicine, String> nameColumn = new TableColumn<>("Medicine Name");
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        nameColumn.setPrefWidth(150);
        
        // Batch ID column
        TableColumn<Medicine, String> batchIdColumn = new TableColumn<>("Batch ID");
        batchIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBatchId()));
        batchIdColumn.setPrefWidth(100);
        
        // Expiry Date column
        TableColumn<Medicine, String> expiryDateColumn = new TableColumn<>("Expiry Date");
        expiryDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getExpiryDate().toString()));
        expiryDateColumn.setPrefWidth(120);
        
        // Quantity column
        TableColumn<Medicine, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getQuantity()));
        quantityColumn.setPrefWidth(80);
        
        // Status column with badge
        TableColumn<Medicine, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(getStatusBadge(cellData.getValue())));
        statusColumn.setPrefWidth(150);
        
        table.getColumns().addAll(nameColumn, batchIdColumn, expiryDateColumn, quantityColumn, statusColumn);
        
        // Apply row styling based on conditions
        table.setRowFactory(tv -> new TableRow<Medicine>() {
            @Override
            protected void updateItem(Medicine medicine, boolean empty) {
                super.updateItem(medicine, empty);
                
                if (empty || medicine == null) {
                    setStyle("");
                } else {
                    // Check quantity for low stock
                    if (medicine.getQuantity() < 10) {
                        setStyle("-fx-background-color: #ffcccc;");  // Light red for low stock
                    } else {
                        setStyle("");
                    }
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
        summaryBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");
        
        Label totalLabel = new Label();
        totalLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        
        Label lowStockLabel = new Label();
        lowStockLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #d32f2f;");
        
        Label expiringLabel = new Label();
        expiringLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #f57c00;");
        
        summaryBox.getChildren().addAll(totalLabel, lowStockLabel, expiringLabel);
        
        // Update summary initially
        updateSummaryWithLabels(medicines, totalLabel, lowStockLabel, expiringLabel);
        
        // Action buttons container
        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.setPadding(new Insets(10));
        
        Button addButton = new Button("+ Add Medicine");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10; -fx-font-size: 12; -fx-cursor: hand;");
        addButton.setOnAction(e -> {
            AddMedicinePage.showAddMedicineDialog(stage, null, medicine -> {
                medicines.add(medicine);
                updateSummary(medicines, summaryBox);
                return null;
            });
        });
        
        Button editButton = new Button("✏️ Edit");
        editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 8; -fx-font-size: 12; -fx-cursor: hand;");
        editButton.setOnAction(e -> {
            Medicine selectedMedicine = table.getSelectionModel().getSelectedItem();
            if (selectedMedicine == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a medicine to edit!");
                return;
            }
            EditMedicinePage.showEditMedicineDialog(stage, selectedMedicine, medicine -> {
                table.refresh();
                updateSummary(medicines, summaryBox);
                return null;
            });
        });
        
        Button deleteButton = new Button("🗑️ Delete");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 8; -fx-font-size: 12; -fx-cursor: hand;");
        deleteButton.setOnAction(e -> {
            Medicine selectedMedicine = table.getSelectionModel().getSelectedItem();
            if (selectedMedicine == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a medicine to delete!");
                return;
            }
            
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Delete");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Are you sure you want to delete " + selectedMedicine.getName() + "?");
            
            if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                medicines.remove(selectedMedicine);
                updateSummary(medicines, summaryBox);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Medicine deleted successfully!");
            }
        });
        
        buttonContainer.getChildren().addAll(addButton, editButton, deleteButton);
        
        // Add all to root
        root.getChildren().addAll(
            navBar,
            titleLabel,
            searchContainer,
            new Label("Medicine Inventory"),
            table,
            summaryBox,
            buttonContainer
        );
        
        VBox.setVgrow(table, Priority.ALWAYS);
        
        return new Scene(root, 1000, 700);
    }
    
    /**
     * Update summary labels with current data
     */
    private static void updateSummaryWithLabels(ObservableList<Medicine> medicines, Label totalLabel, Label lowStockLabel, Label expiringLabel) {
        totalLabel.setText("Total Medicines: " + medicines.size());
        
        long lowStockCount = medicines.stream().filter(m -> m.getQuantity() < 10).count();
        lowStockLabel.setText("Low Stock: " + lowStockCount);
        
        long expiringCount = medicines.stream().filter(m -> ChronoUnit.DAYS.between(LocalDate.now(), m.getExpiryDate()) <= 30 && ChronoUnit.DAYS.between(LocalDate.now(), m.getExpiryDate()) >= 0).count();
        expiringLabel.setText("Expiring Soon: " + expiringCount);
    }
    
    /**
     * Update summary box
     */
    private static void updateSummary(ObservableList<Medicine> medicines, HBox summaryBox) {
        if (summaryBox.getChildren().size() >= 3) {
            Label totalLabel = (Label) summaryBox.getChildren().get(0);
            Label lowStockLabel = (Label) summaryBox.getChildren().get(1);
            Label expiringLabel = (Label) summaryBox.getChildren().get(2);
            updateSummaryWithLabels(medicines, totalLabel, lowStockLabel, expiringLabel);
        }
    }
    
    /**
     * Show alert dialog
     */
    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Generate status badge based on medicine conditions
     */
    private static String getStatusBadge(Medicine medicine) {
        StringBuilder badge = new StringBuilder();
        
        LocalDate today = LocalDate.now();
        long daysUntilExpiry = ChronoUnit.DAYS.between(today, medicine.getExpiryDate());
        
        // Check expiry status
        if (daysUntilExpiry < 0) {
            badge.append("🔴 EXPIRED");
        } else if (daysUntilExpiry <= 30) {
            badge.append("⚠️ EXPIRING SOON");
        } else {
            badge.append("✓ Active");
        }
        
        // Add quantity info
        if (medicine.getQuantity() < 10) {
            badge.append(" | 📉 LOW STOCK");
        }
        
        return badge.toString();
    }
}
