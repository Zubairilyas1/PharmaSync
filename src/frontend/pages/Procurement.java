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
import frontend.ui.TopBar;

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
        mainContainer.getStyleClass().add("app-background");

        // ── Premium TopBar (sticky) ──
        HBox topBar = TopBar.create("Procurement", "Dashboard > Procurement");
        mainContainer.getChildren().add(topBar);

        // Main content area with SplitPane
        SplitPane contentArea = new SplitPane();
        contentArea.setOrientation(javafx.geometry.Orientation.VERTICAL);
        contentArea.setDividerPositions(0.35);
        contentArea.setPadding(new Insets(15));
        contentArea.getStyleClass().add("app-background");

        // Top side - Red Alert Dashboard
        VBox topPanel = createRedAlertDashboard();

        // Bottom side - HBox with Suggested Orders and PO Generator
        HBox bottomPanel = new HBox(15);
        bottomPanel.getStyleClass().add("app-background");

        VBox ordersPanel = new VBox(15);
        Label ordersTitle = new Label("Suggested Orders - Low Stock Items");
        ordersTitle.getStyleClass().add("text-title");
        TableView<ProcurementItem> ordersTable = createSuggestedOrdersTable();
        VBox.setVgrow(ordersTable, Priority.ALWAYS);
        ordersPanel.getChildren().addAll(ordersTitle, ordersTable);
        HBox.setHgrow(ordersPanel, Priority.ALWAYS);

        VBox rightPanel = createPOGeneratorPanel();
        rightPanel.setPrefWidth(350);

        bottomPanel.getChildren().addAll(ordersPanel, rightPanel);

        contentArea.getItems().addAll(topPanel, bottomPanel);
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        mainContainer.getChildren().add(contentArea);

        Scene scene = new Scene(mainContainer, 1280, 800);
        UiTheme.applyStyleSheet(scene);
        return scene;
    }

    // createHeader() removed — replaced by frontend.ui.TopBar

    private static VBox createRedAlertDashboard() {
        VBox alertSection = new VBox(10);
        alertSection.setStyle(UiTheme.card() + " -fx-padding: 15;");

        Label alertTitle = new Label("Critically Low Stock - Immediate Action Required");
        alertTitle.setStyle("-fx-font-size: 13pt; -fx-font-weight: bold; -fx-text-fill: " + UiTheme.COLOR_DANGER_TEXT + ";");

        ListView<ProcurementItem> alertList = new ListView<>();
        alertList.getStyleClass().add("alert-list-view");
        alertList.setOrientation(javafx.geometry.Orientation.HORIZONTAL);
        alertList.setPrefHeight(150);

        alertList.getItems().addAll(
                new ProcurementItem("Aspirin", 8, 10, 100, 0.50, "Medco Supplies", false),
                new ProcurementItem("Ibuprofen", 5, 20, 150, 0.75, "PharmaBulk", false),
                new ProcurementItem("Amoxicillin", 3, 15, 120, 1.25, "HealthCare Distributors", false),
                new ProcurementItem("Metformin", 7, 25, 200, 0.60, "MediPro Ventures", false)
        );

        alertList.setCellFactory(lv -> new ListCell<ProcurementItem>() {
            @Override
            protected void updateItem(ProcurementItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    VBox card = new VBox(10);
                    card.getStyleClass().add("alert-card");
                    card.setPrefWidth(220);

                    HBox header = new HBox();
                    header.setAlignment(Pos.CENTER_LEFT);
                    Label nameLabel = new Label(item.medicineName);
                    nameLabel.setStyle("-fx-font-size: 12pt; -fx-font-weight: bold; -fx-text-fill: #0B1120;");
                    
                    Label badge = new Label("Low Stock");
                    badge.getStyleClass().add("low-stock-badge");
                    
                    javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    header.getChildren().addAll(nameLabel, spacer, badge);

                    Label stockLabel = new Label("Current: " + item.currentStock + " | Min: " + item.minThreshold);
                    stockLabel.getStyleClass().add("text-description");

                    Button reorderBtn = new Button("Quick Reorder");
                    reorderBtn.getStyleClass().add("button-base");
                    reorderBtn.setStyle(UiTheme.primaryButton() + " -fx-padding: 6 12; -fx-font-size: 10pt;");
                    reorderBtn.setMaxWidth(Double.MAX_VALUE);
                    reorderBtn.setOnAction(e -> {
                        item.selected = true;
                        if (ordersTableRef != null) {
                            ordersTableRef.refresh();
                            updatePOSummary();
                        }
                    });

                    card.getChildren().addAll(header, stockLabel, reorderBtn);
                    setGraphic(card);
                }
            }
        });

        alertSection.getChildren().addAll(alertTitle, alertList);

        return alertSection;
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
