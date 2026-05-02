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
import java.util.*;

public class Procurement {
    private static List<ProcurementItem> selectedItems = new ArrayList<>();
    private static int poCounter = 2024001;

    public static Scene createProcurementScene(Stage stage) {
        return createProcurementSceneInternal(stage);
    }

    private static Scene createProcurementSceneInternal(Stage stage) {
        VBox mainContainer = new VBox();
        mainContainer.setStyle(UiTheme.appBackground());

        // Header with back button
        HBox header = createHeader(stage);
        mainContainer.getChildren().add(header);

        // Main content area with side panel
        HBox contentArea = new HBox(15);
        contentArea.setPadding(new Insets(15));
        contentArea.setStyle(UiTheme.appBackground());

        // Left side - Procurement items
        VBox leftPanel = createProcurementPanel();
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        // Right side - PO Generator
        VBox rightPanel = createPOGeneratorPanel();
        rightPanel.setPrefWidth(320);

        contentArea.getChildren().addAll(leftPanel, rightPanel);
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        mainContainer.getChildren().add(contentArea);

        return new Scene(mainContainer, 1920, 1080);
    }

    private static HBox createHeader(Stage stage) {
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

        Label headerTitle = new Label("Procurement Management");
        headerTitle.setStyle(UiTheme.headingM());

        header.getChildren().addAll(backButton, headerTitle);
        HBox.setHgrow(headerTitle, Priority.ALWAYS);

        return header;
    }

    private static VBox createProcurementPanel() {
        VBox panel = new VBox(15);
        panel.setStyle(UiTheme.appBackground());

        // Red Alert Dashboard Section
        VBox alertDashboard = createRedAlertDashboard();
        panel.getChildren().add(alertDashboard);

        // Suggested Orders Section
        Label ordersTitle = new Label("Suggested Orders - Low Stock Items");
        ordersTitle.setStyle(UiTheme.headingM());

        TableView<ProcurementItem> ordersTable = createSuggestedOrdersTable();
        VBox.setVgrow(ordersTable, Priority.ALWAYS);

        panel.getChildren().addAll(ordersTitle, ordersTable);

        return panel;
    }

    private static VBox createRedAlertDashboard() {
        VBox alertSection = new VBox(10);
        alertSection.setStyle(UiTheme.card() + " -fx-padding: 15;");

        Label alertTitle = new Label("Critically Low Stock - Immediate Action Required");
        alertTitle.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: " + UiTheme.COLOR_DANGER_TEXT + ";");

        HBox alertCards = new HBox(12);

        // Sample critically low items
        alertCards.getChildren().addAll(
                createAlertCard("Aspirin", 8, 10),
                createAlertCard("Ibuprofen", 5, 20),
                createAlertCard("Amoxicillin", 3, 15),
                createAlertCard("Metformin", 7, 25)
        );
        alertCards.getChildren().forEach(card -> HBox.setHgrow(card, Priority.ALWAYS));

        alertSection.getChildren().addAll(alertTitle, alertCards);

        return alertSection;
    }

    private static VBox createAlertCard(String medicineName, int currentStock, int minThreshold) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: #ffebee; -fx-border-color: #c62828; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 10;");
        card.setAlignment(Pos.TOP_LEFT);

        Label nameLabel = new Label(medicineName);
        nameLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #c62828;");

        Label stockLabel = new Label("Current: " + currentStock + " | Min: " + minThreshold);
        stockLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #d32f2f;");

        Label urgencyLabel = new Label("ORDER ASAP");
        urgencyLabel.setStyle("-fx-font-size: 10; -fx-font-weight: bold; -fx-text-fill: #c62828;");

        card.getChildren().addAll(nameLabel, stockLabel, urgencyLabel);

        return card;
    }

    private static TableView<ProcurementItem> ordersTableRef;

    private static TableView<ProcurementItem> createSuggestedOrdersTable() {
        TableView<ProcurementItem> table = new TableView<>();
        table.setStyle("-fx-control-inner-background: white; -fx-table-cell-border-color: #e0e0e0;");

        // Checkbox column for selection
        TableColumn<ProcurementItem, Boolean> selectCol = new TableColumn<>("Select");
        selectCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().selected));
        selectCol.setCellFactory(col -> new TableCell<ProcurementItem, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(item);
                    checkBox.setOnAction(e -> {
                        ProcurementItem procItem = getTableView().getItems().get(getIndex());
                        procItem.selected = checkBox.isSelected();
                        updatePOSummary();
                    });
                    setGraphic(checkBox);
                }
            }
        });
        selectCol.setPrefWidth(60);

        // Medicine Name Column
        TableColumn<ProcurementItem, String> medicineCol = new TableColumn<>("Medicine Name");
        medicineCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().medicineName));
        medicineCol.setPrefWidth(150);

        // Current Stock Column
        TableColumn<ProcurementItem, Integer> stockCol = new TableColumn<>("Current Stock");
        stockCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().currentStock));
        stockCol.setPrefWidth(130);
        stockCol.setCellFactory(col -> new TableCell<ProcurementItem, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(item));
                    setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                }
            }
        });

        // Min Threshold Column
        TableColumn<ProcurementItem, Integer> minCol = new TableColumn<>("Min Threshold");
        minCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().minThreshold));
        minCol.setPrefWidth(130);

        // Suggested Order Qty Column (Editable)
        TableColumn<ProcurementItem, Integer> suggestedCol = new TableColumn<>("Suggested Qty (Editable)");
        suggestedCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().suggestedQty));
        suggestedCol.setPrefWidth(150);
        suggestedCol.setCellFactory(col -> new TableCell<ProcurementItem, Integer>() {
            private Spinner<Integer> spinner;

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    spinner = new Spinner<>(1, 500, item);
                    spinner.setPrefWidth(120);
                    spinner.setOnMouseClicked(e -> {
                        ProcurementItem procItem = getTableView().getItems().get(getIndex());
                        procItem.suggestedQty = spinner.getValue();
                        updatePOSummary();
                    });
                    spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                        ProcurementItem procItem = getTableView().getItems().get(getIndex());
                        procItem.suggestedQty = newVal;
                        updatePOSummary();
                    });
                    setGraphic(spinner);
                }
            }
        });

        // Unit Price Column
        TableColumn<ProcurementItem, Double> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().unitPrice));
        priceCol.setPrefWidth(120);
        priceCol.setCellFactory(col -> new TableCell<ProcurementItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                    setStyle("-fx-text-fill: #1976d2;");
                }
            }
        });

        // Vendor/Supplier Column (Dropdown)
        TableColumn<ProcurementItem, String> vendorCol = new TableColumn<>("Vendor");
        vendorCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().vendor));
        vendorCol.setPrefWidth(140);
        vendorCol.setCellFactory(col -> new TableCell<ProcurementItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    ComboBox<String> vendorCombo = new ComboBox<>();
                    vendorCombo.getItems().addAll("Medco Supplies", "PharmaBulk", "HealthCare Distributors", "MediPro Ventures");
                    vendorCombo.setValue(item);
                    vendorCombo.setPrefWidth(130);
                    vendorCombo.setOnAction(e -> {
                        ProcurementItem procItem = getTableView().getItems().get(getIndex());
                        procItem.vendor = vendorCombo.getValue();
                        updatePOSummary();
                    });
                    setGraphic(vendorCombo);
                }
            }
        });

        table.getColumns().addAll(selectCol, medicineCol, stockCol, minCol, suggestedCol, priceCol, vendorCol);
        table.setPrefHeight(400);

        try {
            backend.repositories.MedicineRepository medRepo = new backend.repositories.MySQLMedicineRepository(backend.database.DatabaseManager.getConnection());
            backend.services.InventoryService invService = new backend.services.InventoryService(medRepo);
            List<backend.models.Medicine> medicines = invService.getAllMedicines();
            
            for (backend.models.Medicine m : medicines) {
                if (m.getStockLevel() <= 20) { // Assuming 20 is min threshold for demo
                    int suggested = Math.max(100, 50 - m.getStockLevel());
                    table.getItems().add(new ProcurementItem(m.getName(), m.getStockLevel(), 20, suggested, m.getPrice() * 0.7, "Medco Supplies", false));
                }
            }
        } catch (Exception ex) {
            System.err.println("Error loading inventory for procurement: " + ex.getMessage());
            // Fallback
            table.getItems().addAll(
                    new ProcurementItem("Aspirin", 8, 10, 100, 0.50, "Medco Supplies", false),
                    new ProcurementItem("Ibuprofen", 5, 20, 150, 0.75, "PharmaBulk", false),
                    new ProcurementItem("Amoxicillin", 3, 15, 120, 1.25, "HealthCare Distributors", false),
                    new ProcurementItem("Metformin", 7, 25, 200, 0.60, "MediPro Ventures", false),
                    new ProcurementItem("Paracetamol", 12, 30, 100, 0.40, "Medco Supplies", false),
                    new ProcurementItem("Cough Syrup", 4, 12, 80, 2.50, "PharmaBulk", false)
            );
        }

        ordersTableRef = table;
        return table;
    }

    private static VBox createPOGeneratorPanel() {
        VBox panel = new VBox(12);
        panel.setPadding(new Insets(15));
        panel.setStyle(UiTheme.card() + " -fx-padding: 15;");

        Label panelTitle = new Label("Draft Purchase Order");
        panelTitle.setStyle(UiTheme.headingM());

        // PO Number (auto-generated)
        Label poNumberLabel = new Label("PO #: " + String.format("PO-%d", poCounter));
        poNumberLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666; -fx-font-weight: bold;");

        // DateTime
        Label dateTimeLabel = new Label("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
        dateTimeLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #999;");

        Separator sep1 = new Separator();

        // Summary Section
        Label summaryTitle = new Label("Order Summary");
        summaryTitle.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Item count
        Label itemCountLabel = new Label("Selected Items: 0");
        itemCountLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #666;");

        // Total Quantity
        Label totalQtyLabel = new Label("Total Qty: 0 units");
        totalQtyLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #666;");

        // Estimated Cost
        Label estimatedCostLabel = new Label("Est. Cost: $0.00");
        estimatedCostLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #1976d2;");

        Separator sep2 = new Separator();

        // Selected Items List
        Label selectedItemsTitle = new Label("Selected Items:");
        selectedItemsTitle.setStyle("-fx-font-size: 10; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        ListView<String> selectedItemsList = new ListView<>();
        selectedItemsList.setStyle("-fx-control-inner-background: #f5f5f5; -fx-text-fill: #333; -fx-border-color: #ddd; -fx-border-width: 1;");
        selectedItemsList.setPrefHeight(180);
        VBox.setVgrow(selectedItemsList, Priority.ALWAYS);

        Separator sep3 = new Separator();

        // Generate PO Button
        Button generatePOButton = new Button("Generate and Send PO");
        generatePOButton.setStyle(UiTheme.primaryButton() + " -fx-padding: 10 12; -fx-font-size: 11;");
        generatePOButton.setPrefWidth(290);
        UiTheme.installPrimaryHover(generatePOButton);
        generatePOButton.setOnAction(e -> {
            if (selectedItems.isEmpty()) {
                showPOAlert("Warning", "Please select at least one item to generate PO.");
            } else {
                try {
                    backend.repositories.AuditLogRepository logRepo = new backend.repositories.MySQLAuditLogRepository(backend.database.DatabaseManager.getConnection());
                    backend.services.AuditService auditService = new backend.services.AuditService(logRepo);
                    
                    backend.repositories.MedicineRepository medRepo = new backend.repositories.MySQLMedicineRepository(backend.database.DatabaseManager.getConnection());
                    backend.services.InventoryService invService = new backend.services.InventoryService(medRepo);
                    
                    double totalCost = 0;
                    for(ProcurementItem pi : selectedItems) {
                        totalCost += pi.suggestedQty * pi.unitPrice;
                        
                        // Update stock in backend directly for simplicity of demo
                        backend.models.Medicine m = invService.getMedicineByName(pi.medicineName);
                        if (m != null) {
                            m.setStockLevel(m.getStockLevel() + pi.suggestedQty);
                            invService.updateMedicine(m);
                        }
                    }
                    
                    auditService.logAction("Anonymous", "Procurement", "Generated PO for " + selectedItems.size() + " items, Est. Cost: $" + totalCost);
                } catch (Exception ex) {
                    System.err.println("Error generating PO: " + ex.getMessage());
                }

                int poNumber = poCounter++;
                showPOAlert("Success", "Purchase Order PO-" + poNumber + " sent to Supplier API.\n\nEstimated delivery: 3-5 business days.");
                poSummaryLabels[0].setText("Selected Items: 0");
                poSummaryLabels[1].setText("Total Qty: 0 units");
                poSummaryLabels[2].setText("Est. Cost: $0.00");
                poItemsList.getItems().clear();
                
                // Refresh table
                if (ordersTableRef != null) {
                    ordersTableRef.getItems().clear();
                    createSuggestedOrdersTable(); // repopulate
                }
            }
        });

        // Store references for updating
        updatePOSummaryMethod = () -> {
            selectedItems.clear();
            selectedItemsList.getItems().clear();
            int totalQty = 0;
            double totalCost = 0.0;

            // This would be called from the table update
            updatePOSummary();
        };

        panel.getChildren().addAll(
                panelTitle, poNumberLabel, dateTimeLabel, sep1,
                summaryTitle, itemCountLabel, totalQtyLabel, estimatedCostLabel, sep2,
                selectedItemsTitle, selectedItemsList, sep3,
                generatePOButton
        );

        // Store references for dynamic updates
        poSummaryLabels = new Label[]{itemCountLabel, totalQtyLabel, estimatedCostLabel};
        poItemsList = selectedItemsList;

        return panel;
    }

    private static Label[] poSummaryLabels;
    private static ListView<String> poItemsList;
    private static Runnable updatePOSummaryMethod;

    private static void updatePOSummary() {
        if (ordersTableRef == null || poSummaryLabels == null || poItemsList == null) return;
        
        selectedItems.clear();
        int totalQty = 0;
        double totalCost = 0.0;
        poItemsList.getItems().clear();

        for (ProcurementItem item : ordersTableRef.getItems()) {
            if (item.selected) {
                selectedItems.add(item);
                totalQty += item.suggestedQty;
                double cost = item.suggestedQty * item.unitPrice;
                totalCost += cost;
                poItemsList.getItems().add(item.suggestedQty + "x " + item.medicineName + " ($" + String.format("%.2f", cost) + ")");
            }
        }
        
        poSummaryLabels[0].setText("Selected Items: " + selectedItems.size());
        poSummaryLabels[1].setText("Total Qty: " + totalQty + " units");
        poSummaryLabels[2].setText("Est. Cost: $" + String.format("%.2f", totalCost));
    }

    private static void showPOAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class for procurement items
    public static class ProcurementItem {
        public String medicineName;
        public int currentStock;
        public int minThreshold;
        public int suggestedQty;
        public double unitPrice;
        public String vendor;
        public boolean selected;

        public ProcurementItem(String medicineName, int currentStock, int minThreshold, 
                               int suggestedQty, double unitPrice, String vendor, boolean selected) {
            this.medicineName = medicineName;
            this.currentStock = currentStock;
            this.minThreshold = minThreshold;
            this.suggestedQty = suggestedQty;
            this.unitPrice = unitPrice;
            this.vendor = vendor;
            this.selected = selected;
        }
    }
}
