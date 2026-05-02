package frontend.pages;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;
import javafx.scene.paint.Color;
import frontend.ui.UiTheme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backend.database.DatabaseManager;
import backend.models.Customer;
import backend.models.Sale;
import backend.models.Medicine; // If needed for getting medicine details by ID
import backend.repositories.MySQLCustomerRepository;
import backend.repositories.MySQLSaleRepository;
import backend.repositories.MySQLMedicineRepository;
import backend.repositories.MySQLAuditLogRepository;
import backend.services.CustomerService;
import backend.services.SalesService;
import backend.services.InventoryService;
import backend.services.AuditService;

public class Returns {
    
    private static final String PANEL_BG = "#F8FAFC";
    private static final String PRIMARY_BLUE = "#6366F1";
    
    // Combined return item data structure
    public static class ReturnLineItem {
        public Sale transaction;
        public ReturnItemData returnData;
        
        public ReturnLineItem(Sale transaction) {
            this.transaction = transaction;
            this.returnData = new ReturnItemData();
        }
    }
    
    private InventoryService inventoryService;
    private AuditService auditService;
    private SalesService salesService;     

    public Returns(InventoryService inventoryService, AuditService auditService, SalesService salesService) {
        this.inventoryService = inventoryService;
        this.auditService = auditService;
        this.salesService = salesService;
    }
    
    public static Scene createReturnsScene(Stage stage) {
        try {
            return createReturnsSceneInternal(stage);
        } catch (Exception e) {
            System.err.println("ERROR in Returns.createReturnsScene: " + e.getMessage());
            VBox errorBox = new VBox(15);
            errorBox.setAlignment(Pos.CENTER);
            errorBox.setPadding(new Insets(20));
            Label errorLabel = new Label("Error loading Returns page:\n" + e.getMessage());
            errorLabel.setStyle("-fx-font-size: 20; -fx-text-fill: red;");
            errorLabel.setWrapText(true);
            Button errorBackButton = new Button("← Back to Dashboard");
            errorBackButton.setStyle("-fx-font-size: 14; -fx-padding: 10 20;");
            errorBackButton.setOnAction(evt -> {
                Scene dashboardScene = Dashboard.createDashboardScene(stage);
                stage.setScene(dashboardScene);
            });
            errorBox.getChildren().addAll(errorLabel, errorBackButton);
            return new Scene(errorBox, 1280, 800);
        }
    }
    
    private static Scene createReturnsSceneInternal(Stage stage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(22));
        root.setStyle(UiTheme.appBackground());
        
        // Header with back button
        HBox header = createHeader(stage);
        root.getChildren().add(header);
        
        Label titleLabel = new Label("Sales Returns");
        titleLabel.setStyle(UiTheme.headingL());
        
        Label subtitleLabel = new Label("Process Sales Return — Non-CRUD Logic Module (UC17)");
        subtitleLabel.setStyle(UiTheme.bodyText());
        
        VBox titleBox = new VBox(4, titleLabel, subtitleLabel);
        root.getChildren().add(titleBox);
        
        // Main content area
        HBox mainContent = new HBox(20);
        mainContent.setStyle(UiTheme.card() + " -fx-padding: 18;");
        
        // Left side: Customer lookup and purchase history
        VBox leftPanel = new VBox(15);
        leftPanel.setPrefWidth(780);
        
        // ==================== CUSTOMER LOOKUP SECTION ====================
        VBox customerLookupBox = new VBox(10);
        customerLookupBox.setPadding(new Insets(14));
        customerLookupBox.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 12; -fx-border-color: #DBEAFE; -fx-border-radius: 12; -fx-border-width: 1;");
        
        Label customerLookupLabel = new Label("Scan Receipt or Enter Transaction ID");
        customerLookupLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        HBox customerSearchBox = new HBox(10);
        customerSearchBox.setAlignment(Pos.CENTER_LEFT);
        
        TextField customerNameField = new TextField();
        customerNameField.setPromptText("Enter customer name or Transaction ID (TS-XXXX-XXX)...");
        customerNameField.setStyle(UiTheme.input() + " -fx-padding: 10;");
        customerNameField.setPrefWidth(400);
        
        Button lookupButton = new Button("Fetch Order");
        lookupButton.setStyle(UiTheme.primaryButton() + " -fx-padding: 10 16;");
        UiTheme.installPrimaryHover(lookupButton);
        
        customerSearchBox.getChildren().addAll(customerNameField, lookupButton);
        customerLookupBox.getChildren().addAll(customerLookupLabel, customerSearchBox);
        leftPanel.getChildren().add(customerLookupBox);
        
        VBox transactionDetailsBox = new VBox(8);
        transactionDetailsBox.setPadding(new Insets(14));
        transactionDetailsBox.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-border-width: 1;");
        
        Label transactionDetailsTitle = new Label("Original Transaction");
        transactionDetailsTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #111827;");
        
        Label customerInfoLabel = new Label("No transaction selected.");
        customerInfoLabel.setWrapText(true);
        customerInfoLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #475569;");
        
        transactionDetailsBox.getChildren().addAll(transactionDetailsTitle, customerInfoLabel);
        leftPanel.getChildren().add(transactionDetailsBox);
        
        // Purchase History Table
        Label historyLabel = new Label("Purchase History (Sales)");
        historyLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #111827;");
        leftPanel.getChildren().add(historyLabel);
        
        TableView<Sale> historyTable = new TableView<>();
        historyTable.setPrefHeight(250);
        historyTable.setStyle("-fx-font-size: 11; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #E5EAF2;");
        
        TableColumn<Sale, String> dateCol = new TableColumn<>("Date");
        dateCol.setPrefWidth(120);
        dateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTimestamp().toString()));
        
        TableColumn<Sale, Integer> medicineIdCol = new TableColumn<>("Med ID");
        medicineIdCol.setPrefWidth(60);
        medicineIdCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getMedicineId()));
        
        TableColumn<Sale, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setPrefWidth(50);
        qtyCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getQuantity()));
        
        TableColumn<Sale, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setPrefWidth(80);
        amountCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getTotalPrice()));
        amountCol.setCellFactory(column -> new TableCell<Sale, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.format("Rs. %.2f", item));
            }
        });
        
        historyTable.getColumns().addAll(dateCol, medicineIdCol, qtyCol, amountCol);
        leftPanel.getChildren().add(historyTable);
        VBox.setVgrow(historyTable, Priority.ALWAYS);
        
        // ==================== ITEMS TABLE FOR RETURN ====================
        Label returnItemsLabel = new Label("Select Items for Return");
        returnItemsLabel.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: #111827;");
        leftPanel.getChildren().add(returnItemsLabel);
        
        TableView<ReturnLineItem> itemsTable = new TableView<>();
        itemsTable.setPrefHeight(430);
        itemsTable.setStyle("-fx-font-size: 11; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #E5EAF2;");
        
        ObservableList<ReturnLineItem> itemsList = FXCollections.observableArrayList();
        itemsTable.setItems(itemsList);
        
        // Checkbox column
        TableColumn<ReturnLineItem, Boolean> selectColumn = new TableColumn<>("Select");
        selectColumn.setPrefWidth(60);
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().returnData.selectedProperty());
        selectColumn.setCellFactory(column -> new TableCell<ReturnLineItem, Boolean>() {
            private CheckBox checkBox = new CheckBox();
            {
                checkBox.setOnAction(e -> getTableView().refresh());
            }
            
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item != null && item);
                    ReturnLineItem lineItem = getTableView().getItems().get(getIndex());
                    checkBox.selectedProperty().bindBidirectional(lineItem.returnData.selectedProperty());
                    setGraphic(checkBox);
                }
            }
        });
        
        // Medicine name column
        TableColumn<ReturnLineItem, Integer> medicineColumn = new TableColumn<>("Med ID");
        medicineColumn.setPrefWidth(80);
        medicineColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().transaction.getMedicineId()));
        
        // Batch column
        TableColumn<ReturnLineItem, String> batchColumn = new TableColumn<>("Timestamp");
        batchColumn.setPrefWidth(120);
        batchColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().transaction.getTimestamp().toString()));
        
        // Quantity column
        TableColumn<ReturnLineItem, Integer> quantityColumn = new TableColumn<>("Original Qty");
        quantityColumn.setPrefWidth(80);
        quantityColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().transaction.getQuantity()));
        
        // Price column
        TableColumn<ReturnLineItem, Double> priceColumn = new TableColumn<>("Total Price");
        priceColumn.setPrefWidth(80);
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().transaction.getTotalPrice()));
        priceColumn.setCellFactory(column -> new TableCell<ReturnLineItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.format("Rs. %.2f", item));
            }
        });
        
        // Return Quantity column
        TableColumn<ReturnLineItem, Integer> returnQtyColumn = new TableColumn<>("Return Qty");
        returnQtyColumn.setPrefWidth(100);
        returnQtyColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().returnData.returnQty.get()));
        returnQtyColumn.setCellFactory(column -> new TableCell<ReturnLineItem, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    setText(null);
                } else {
                    ReturnLineItem lineItem = getTableView().getItems().get(getIndex());
                    Spinner<Integer> spinner = new Spinner<>(0, lineItem.transaction.getQuantity(), item != null ? item : 0, 1);
                    spinner.setPrefWidth(80);
                    spinner.setEditable(true);
                    spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                        lineItem.returnData.returnQty.set(newVal);
                    });
                    setGraphic(spinner);
                    setText(null);
                }
            }
        });
        
        // Condition dropdown column
        TableColumn<ReturnLineItem, String> conditionColumn = new TableColumn<>("Return Reason");
        conditionColumn.setPrefWidth(180);
        conditionColumn.setCellValueFactory(cellData -> cellData.getValue().returnData.conditionProperty());
        conditionColumn.setCellFactory(column -> new TableCell<ReturnLineItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    setText(null);
                } else {
                    ComboBox<String> comboBox = new ComboBox<>();
                    comboBox.setItems(FXCollections.observableArrayList(
                        "Wrong Item (Restock)",
                        "Damaged/Faulty (Quarantine)"
                    ));
                    comboBox.setValue(item != null ? item : "Wrong Item (Restock)");
                    comboBox.setPrefWidth(170);
                    ReturnLineItem lineItem = getTableView().getItems().get(getIndex());
                    comboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                        if (newVal != null) {
                            lineItem.returnData.condition.set(newVal);
                        }
                    });
                    setGraphic(comboBox);
                    setText(null);
                }
            }
        });
        
        itemsTable.getColumns().addAll(selectColumn, medicineColumn, batchColumn, quantityColumn, priceColumn, returnQtyColumn, conditionColumn);
        leftPanel.getChildren().add(itemsTable);
        VBox.setVgrow(itemsTable, Priority.ALWAYS);
        
        // Right side: Refund Summary
        VBox rightPanel = new VBox(15);
        rightPanel.setPrefWidth(320);
        rightPanel.setPadding(new Insets(22));
        rightPanel.setStyle("-fx-background-color: " + PANEL_BG + "; -fx-background-radius: 14; -fx-border-color: #E5EAF2; -fx-border-radius: 14; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.08), 14, 0, 0, 3);");
        
        Label refundTitleLabel = new Label("Refund Summary");
        refundTitleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #111827;");
        rightPanel.getChildren().add(refundTitleLabel);
        
        Label refundInfoLabel = new Label("Refund totals will update as you choose return items.");
        refundInfoLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #475569;");
        refundInfoLabel.setWrapText(true);
        rightPanel.getChildren().add(refundInfoLabel);
        
        Separator sep1 = new Separator();
        rightPanel.getChildren().add(sep1);
        
        Label subtotalLabel = new Label("Refund Subtotal: Rs. 0.00");
        subtotalLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #334155;");
        
        Label itemCountLabel = new Label("Selected Return Qty: 0");
        itemCountLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #334155;");
        
        rightPanel.getChildren().addAll(subtotalLabel, itemCountLabel);
        
        Separator separator = new Separator();
        rightPanel.getChildren().add(separator);
        
        Label totalRefundLabel = new Label("Calculated Refund (Incl. Tax): Rs. 0.00");
        totalRefundLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #047857;");
        
        Separator sep2 = new Separator();
        rightPanel.getChildren().add(sep2);
        
        Label logicTitleLabel = new Label("Logic Execution Status");
        logicTitleLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #111827;");
        
        CheckBox inventoryCheck = new CheckBox("Increment Inventory (SQL)");
        inventoryCheck.setSelected(true);
        inventoryCheck.setDisable(true);
        inventoryCheck.setStyle("-fx-font-size: 12; -fx-text-fill: #475569;");
        
        CheckBox ledgerCheck = new CheckBox("Transaction Ledger Update");
        ledgerCheck.setSelected(true);
        ledgerCheck.setDisable(true);
        ledgerCheck.setStyle("-fx-font-size: 12; -fx-text-fill: #475569;");
        
        CheckBox restockCheck = new CheckBox("Restock FEFO Pool");
        restockCheck.setSelected(false);
        restockCheck.setDisable(true);
        restockCheck.setStyle("-fx-font-size: 12; -fx-text-fill: #475569;");
        
        rightPanel.getChildren().addAll(totalRefundLabel, logicTitleLabel, inventoryCheck, ledgerCheck, restockCheck);
        
        // Add listener for table item changes (after labels are defined)
        itemsTable.getItems().addListener((javafx.collections.ListChangeListener<? super ReturnLineItem>) change -> {
            updateRefundSummary(itemsTable, subtotalLabel, itemCountLabel, totalRefundLabel);
        });
        
        // Reference to currently selected customer from lookup
        final Customer[] selectedCustomer = new Customer[1];
        
        // Process button
        Button processButton = new Button("Process Refund");
        processButton.setPrefWidth(Double.MAX_VALUE);
        processButton.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-padding: 12; -fx-font-size: 13; -fx-font-weight: 700; -fx-background-radius: 10;");
        processButton.setOnAction(e -> {
            if (itemsList.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "No Items", "No items to return.");
                return;
            }
            
            if (selectedCustomer[0] == null) {
                showAlert(Alert.AlertType.WARNING, "No Customer Selected", "Please search for a customer first.");
                return;
            }
            
            processReturn(itemsTable, selectedCustomer[0], stage);
            updateRefundSummary(itemsTable, subtotalLabel, itemCountLabel, totalRefundLabel);
        });
        rightPanel.getChildren().add(processButton);
        VBox.setVgrow(rightPanel, Priority.ALWAYS);
        
        mainContent.getChildren().addAll(leftPanel, rightPanel);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        root.getChildren().add(mainContent);
        VBox.setVgrow(mainContent, Priority.ALWAYS);
        
        // Lookup button action
        lookupButton.setOnAction(e -> {
            String searchText = customerNameField.getText().trim();
            if (searchText.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Empty Input", "Please enter customer email or ID to search.");
                itemsList.clear();
                historyTable.setItems(FXCollections.observableArrayList());
                customerInfoLabel.setText("");
                return;
            }
            
            try {
                CustomerService customerService = new CustomerService(new MySQLCustomerRepository(DatabaseManager.getConnection()));
                SalesService salesService = new SalesService(new MySQLSaleRepository(DatabaseManager.getConnection()));
                
                Customer customer = null;
                try {
                    int id = Integer.parseInt(searchText);
                    customer = customerService.getCustomerById(id);
                } catch (NumberFormatException ignored) {
                }
                
                if (customer == null) {
                    for (Customer c : customerService.getAllCustomers()) {
                        if (c.getEmail().equalsIgnoreCase(searchText) || c.getName().equalsIgnoreCase(searchText)) {
                            customer = c;
                            break;
                        }
                    }
                }
                
                if (customer == null) {
                    showAlert(Alert.AlertType.WARNING, "Not Found", "Customer not found.");
                    itemsList.clear();
                    historyTable.setItems(FXCollections.observableArrayList());
                    customerInfoLabel.setText("");
                    return;
                }
                
                selectedCustomer[0] = customer;
                customerInfoLabel.setText("Customer: " + customer.getName() + "\nID: " + customer.getId() + 
                                         "\nEmail: " + customer.getEmail());
                
                // Show transaction history (we assume we can get all sales, then filter by some logic if needed, 
                // but the current SalesService returns all sales. We don't have a getSalesByCustomer, so we'll show all or recent.)
                java.util.List<Sale> allSales = salesService.getAllSales();
                
                ObservableList<Sale> historyItems = FXCollections.observableArrayList(allSales); // in reality, filter by customerId if that existed in Sale model
                historyTable.setItems(historyItems);
                
                // Populate return items
                itemsList.clear();
                for (Sale txn : historyItems) {
                    ReturnLineItem lineItem = new ReturnLineItem(txn);
                    lineItem.returnData.selectedProperty().addListener((obs, oldVal, newVal) -> {
                        updateRefundSummary(itemsTable, subtotalLabel, itemCountLabel, totalRefundLabel);
                    });
                    lineItem.returnData.returnQtyProperty().addListener((obs, oldVal, newVal) -> {
                        updateRefundSummary(itemsTable, subtotalLabel, itemCountLabel, totalRefundLabel);
                    });
                    itemsList.add(lineItem);
                }
                
                showNotification("✓ Customer found: " + customer.getName());

            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Backend Error", "Failed to fetch customer data: " + ex.getMessage());
            }
        });
        
        return new Scene(root, 1280, 800);
    }
    
    private static HBox createHeader(Stage stage) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 16, 12, 16));
        header.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 14; -fx-border-color: #E5EAF2; -fx-border-radius: 14; -fx-effect: dropshadow(three-pass-box, rgba(13, 38, 76, 0.10), 16, 0, 0, 4);");
        
        Button backButton = new Button("← Back to Dashboard");
        backButton.setStyle("-fx-background-color: #EEF4FF; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-padding: 8 14; -fx-font-size: 12; -fx-font-weight: 700; -fx-cursor: hand; -fx-background-radius: 10;");
        backButton.setOnAction(e -> {
            Scene dashboardScene = Dashboard.createDashboardScene(stage);
            stage.setScene(dashboardScene);
        });
        
        Label title = new Label("Sales Returns");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: 800; -fx-text-fill: #111827;");
        
        header.getChildren().addAll(backButton, title);
        HBox.setHgrow(title, Priority.ALWAYS);
        
        return header;
    }
    
    private static void updateRefundSummary(TableView<ReturnLineItem> table, 
                                           Label subtotalLabel, Label itemCountLabel, Label totalRefundLabel) {
        double totalRefund = 0;
        int itemCount = 0;
        
        for (ReturnLineItem lineItem : table.getItems()) {
            if (lineItem.returnData.selected.get()) {
                int returnQty = lineItem.returnData.returnQty.get();
                if (returnQty > 0) {
                    double unitPrice = lineItem.transaction.getTotalPrice() / lineItem.transaction.getQuantity();
                    totalRefund += returnQty * unitPrice;
                    itemCount += returnQty;
                }
            }
        }
        
        subtotalLabel.setText(String.format("Refund Subtotal: Rs. %.2f", totalRefund));
        itemCountLabel.setText("Selected Return Qty: " + itemCount);
        totalRefundLabel.setText(String.format("Calculated Refund (Incl. Tax): Rs. %.2f", totalRefund));
    }
    
    private static void processReturn(TableView<ReturnLineItem> table, Customer customer, Stage stage) {
        StringBuilder returnDetails = new StringBuilder();
        double totalRefund = 0;
        int processedItems = 0;
        
        try {
            InventoryService invService = new InventoryService(new MySQLMedicineRepository(DatabaseManager.getConnection()));
            SalesService salesService = new SalesService(new MySQLSaleRepository(DatabaseManager.getConnection()));
            AuditService auditService = new AuditService(new MySQLAuditLogRepository(DatabaseManager.getConnection()));
        
            for (ReturnLineItem lineItem : table.getItems()) {
                if (lineItem.returnData.selected.get() && lineItem.returnData.returnQty.get() > 0) {
                    Sale item = lineItem.transaction;
                    String condition = lineItem.returnData.condition.get();
                    int returnQty = lineItem.returnData.returnQty.get();
                    double unitPrice = item.getTotalPrice() / item.getQuantity();
                    double refundAmount = returnQty * unitPrice;
                    
                    String stockAction = "";
                    if ("Wrong Item (Restock)".equals(condition)) {
                        Medicine med = invService.getMedicineById(item.getMedicineId());
                        if (med != null) {
                            med.setStockQuantity(med.getStockQuantity() + returnQty);
                            invService.updateMedicine(med);
                            stockAction = "Restocked to Inventory";
                        }
                    } else {
                        stockAction = "Quarantined";
                    }
                    
                    returnDetails.append(String.format("• %dx MedID %d - Rs. %.2f\n  Action: %s\n\n",
                        returnQty,
                        item.getMedicineId(),
                        refundAmount,
                        stockAction));
                    
                    totalRefund += refundAmount;
                    processedItems++;
                }
            }
            
            if (processedItems == 0) {
                showAlert(Alert.AlertType.WARNING, "No Items Selected", "Please select at least one item with a return quantity greater than 0.");
                return;
            }
            
            salesService.recordSale(new Sale(0, 0, 1, -totalRefund, java.time.LocalDateTime.now(), "Return: " + processedItems + " items"));
            auditService.logAction("Anonymous", "Return", "Processed return for Rs. " + totalRefund + " (Customer: " + customer.getName() + ")");
            
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Return Processed Successfully");
            successAlert.setHeaderText("✓ Refund Updated");
            successAlert.setContentText(String.format("Customer: %s\nTotal Items Returned: %d\nTotal Refund: Rs. %.2f\n\n%s", 
                customer.getName(), processedItems, totalRefund, returnDetails.toString()));
            successAlert.showAndWait();
            
            showSuccessToast(String.format("Return processed: Rs. %.2f refund for %s (%d items)", totalRefund, customer.getName(), processedItems));
        } catch (Exception ex) {
            System.err.println("Error processing return to backend: " + ex.getMessage());
            showAlert(Alert.AlertType.ERROR, "Backend Error", "Failed to process return to backend: " + ex.getMessage());
        }
    }
    
    private static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private static void showNotification(String message) {
        // Placeholder for non-blocking toast notifications.
    }
    
    private static void showSuccessToast(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Success", message);
    }
    
    // Helper class for return item data
    private static class ReturnItemData {
        javafx.beans.property.SimpleBooleanProperty selected = new javafx.beans.property.SimpleBooleanProperty(false);
        javafx.beans.property.SimpleIntegerProperty returnQty = new javafx.beans.property.SimpleIntegerProperty(0);
        javafx.beans.property.SimpleStringProperty condition = new javafx.beans.property.SimpleStringProperty("Wrong Item (Restock)");
        
        public javafx.beans.property.BooleanProperty selectedProperty() {
            return selected;
        }
        
        public javafx.beans.property.IntegerProperty returnQtyProperty() {
            return returnQty;
        }
        
        public javafx.beans.property.StringProperty conditionProperty() {
            return condition;
        }
    }
}
