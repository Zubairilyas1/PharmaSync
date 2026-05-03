package frontend.pages;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;
import javafx.scene.paint.Color;
import frontend.ui.UiTheme;
import frontend.ui.TopBar;

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
        // Outer wrapper: TopBar on top, content below
        VBox wrapper = new VBox();
        wrapper.getStyleClass().add("app-background");

        HBox topBar = TopBar.create("Sales Returns", "Dashboard > Sales Returns");
        wrapper.getChildren().add(topBar);

        // Inner BorderPane for the actual page layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));
        root.getStyleClass().add("app-background");
        VBox.setVgrow(root, javafx.scene.layout.Priority.ALWAYS);
        wrapper.getChildren().add(root);
        
        VBox leftCard = new VBox(18);
        leftCard.setPadding(new Insets(22));
        leftCard.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 18; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.08), 24, 0, 0, 10);");
        leftCard.setMaxWidth(Double.MAX_VALUE);
        
        Label sectionTitle = new Label("Transaction Search");
        sectionTitle.setStyle("-fx-font-size: 20; -fx-font-weight: 800; -fx-text-fill: #0B1120;");
        
        Label sectionSubtitle = new Label("Find a transaction and choose the items to process for refund.");
        sectionSubtitle.setStyle("-fx-font-size: 13; -fx-text-fill: #64748B;");
        sectionSubtitle.setWrapText(true);
        
        HBox searchFieldRow = new HBox(16);
        searchFieldRow.setAlignment(Pos.CENTER_LEFT);
        
        TextField transactionIdField = new TextField();
        transactionIdField.setPromptText("Transaction ID or receipt code");
        transactionIdField.getStyleClass().add("form-input");
        transactionIdField.setPrefWidth(460);
        transactionIdField.setPrefHeight(46);
        
        Button searchButton = new Button("Search");
        searchButton.getStyleClass().addAll("button-base", "search-button");
        searchButton.setPrefWidth(140);
        searchButton.setPrefHeight(46);
        
        searchFieldRow.getChildren().addAll(transactionIdField, searchButton);
        
        VBox transactionDetailsBox = new VBox(10);
        transactionDetailsBox.setPadding(new Insets(18));
        transactionDetailsBox.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 14; -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 14;");
        
        Label transactionDetailsTitle = new Label("Original Transaction");
        transactionDetailsTitle.setStyle("-fx-font-size: 14; -fx-font-weight: 700; -fx-text-fill: #0F172A;");
        
        Label customerInfoLabel = new Label("Search a transaction to review return details.");
        customerInfoLabel.setWrapText(true);
        customerInfoLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #475569;");
        
        transactionDetailsBox.getChildren().addAll(transactionDetailsTitle, customerInfoLabel);
        
        Label returnItemsLabel = new Label("Select Items for Return");
        returnItemsLabel.setStyle("-fx-font-size: 16; -fx-font-weight: 800; -fx-text-fill: #0B1120;");
        
        StackPane tableStack = new StackPane();
        tableStack.setPrefHeight(500);
        tableStack.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.08), 24, 0, 0, 10); -fx-padding: 20;");
        
        VBox placeholderBox = new VBox(14);
        placeholderBox.setAlignment(Pos.CENTER);
        placeholderBox.setStyle("-fx-background-color: transparent;");
        placeholderBox.setPrefHeight(460);
        
        Label illustrationIcon = new Label("📄");
        illustrationIcon.setStyle("-fx-font-size: 42;");
        
        Label emptyTitle = new Label("Enter a Transaction ID to get started");
        emptyTitle.setStyle("-fx-font-size: 18; -fx-font-weight: 700; -fx-text-fill: #0F172A;");
        
        Label emptySubtitle = new Label("Once you search, order items will appear here for refund selection.");
        emptySubtitle.setStyle("-fx-font-size: 13; -fx-text-fill: #64748B;");
        emptySubtitle.setWrapText(true);
        emptySubtitle.setMaxWidth(420);
        
        placeholderBox.getChildren().addAll(illustrationIcon, emptyTitle, emptySubtitle);
        
        TableView<ReturnLineItem> itemsTable = new TableView<>();
        itemsTable.setPrefHeight(460);
        itemsTable.getStyleClass().add("return-items-table");
        itemsTable.setVisible(false);
        itemsTable.setOpacity(0);
        
        ObservableList<ReturnLineItem> itemsList = FXCollections.observableArrayList();
        itemsTable.setItems(itemsList);
        
        TableColumn<ReturnLineItem, Boolean> selectColumn = new TableColumn<>("Select");
        selectColumn.setPrefWidth(70);
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().returnData.selectedProperty());
        selectColumn.setCellFactory(column -> new TableCell<ReturnLineItem, Boolean>() {
            private final CheckBox checkBox = new CheckBox();
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
        
        TableColumn<ReturnLineItem, Integer> medicineColumn = new TableColumn<>("Med ID");
        medicineColumn.setPrefWidth(80);
        medicineColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().transaction.getMedicineId()));
        
        TableColumn<ReturnLineItem, String> batchColumn = new TableColumn<>("Timestamp");
        batchColumn.setPrefWidth(140);
        batchColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().transaction.getTimestamp().toString()));
        
        TableColumn<ReturnLineItem, Integer> quantityColumn = new TableColumn<>("Original Qty");
        quantityColumn.setPrefWidth(90);
        quantityColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().transaction.getQuantity()));
        
        TableColumn<ReturnLineItem, Double> priceColumn = new TableColumn<>("Total Price");
        priceColumn.setPrefWidth(100);
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().transaction.getTotalPrice()));
        priceColumn.setCellFactory(column -> new TableCell<ReturnLineItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.format("Rs. %.2f", item));
            }
        });
        
        TableColumn<ReturnLineItem, Integer> returnQtyColumn = new TableColumn<>("Return Qty");
        returnQtyColumn.setPrefWidth(110);
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
                    spinner.valueProperty().addListener((obs, oldVal, newVal) -> lineItem.returnData.returnQty.set(newVal));
                    setGraphic(spinner);
                    setText(null);
                }
            }
        });
        
        TableColumn<ReturnLineItem, String> conditionColumn = new TableColumn<>("Condition");
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
                    comboBox.getItems().addAll("Wrong Item (Restock)", "Damaged/Faulty (Quarantine)");
                    comboBox.getStyleClass().add("condition-dropdown");
                    comboBox.setValue(item != null ? item : "Wrong Item (Restock)");
                    comboBox.setPrefWidth(170);
                    comboBox.setPrefHeight(36);
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
        
        tableStack.getChildren().addAll(placeholderBox, itemsTable);
        
        leftCard.getChildren().addAll(sectionTitle, sectionSubtitle, searchFieldRow, transactionDetailsBox, returnItemsLabel, tableStack);
        
        VBox rightPanel = new VBox(20);
        rightPanel.setPrefWidth(360);
        rightPanel.setPadding(new Insets(24));
        rightPanel.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 18; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.10), 24, 0, 0, 12);");
        
        Label refundTitleLabel = new Label("Refund Summary");
        refundTitleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: 800; -fx-text-fill: #0B1120;");
        
        Label refundInfoLabel = new Label("Refund totals update automatically as return items are selected.");
        refundInfoLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #64748B;");
        refundInfoLabel.setWrapText(true);
        
        Separator sep1 = new Separator();
        
        Label subtotalLabel = new Label("Refund Subtotal: Rs. 0.00");
        subtotalLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #334155;");
        
        Label itemCountLabel = new Label("Selected Return Qty: 0");
        itemCountLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #334155;");
        
        Separator separator = new Separator();
        
        Label totalRefundLabel = new Label("Calculated Refund (Incl. Tax): Rs. 0.00");
        totalRefundLabel.setStyle("-fx-font-size: 18; -fx-font-weight: 800; -fx-text-fill: #0D9488;");
        
        Separator sep2 = new Separator();
        
        Label logicTitleLabel = new Label("Logic Execution Status");
        logicTitleLabel.setStyle("-fx-font-size: 13; -fx-font-weight: 700; -fx-text-fill: #111827;");
        
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
        
        Button processButton = new Button("Confirm Refund");
        processButton.getStyleClass().addAll("button-base", "confirm-button");
        processButton.setPrefWidth(Double.MAX_VALUE);
        processButton.setPrefHeight(48);
        
        rightPanel.getChildren().addAll(refundTitleLabel, refundInfoLabel, sep1, subtotalLabel, itemCountLabel, separator, totalRefundLabel, sep2, logicTitleLabel, inventoryCheck, ledgerCheck, restockCheck, processButton);
        VBox.setVgrow(rightPanel, Priority.ALWAYS);
        
        HBox mainContent = new HBox(24, leftCard, rightPanel);
        mainContent.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(leftCard, Priority.ALWAYS);
        
        root.setCenter(mainContent);
        
        final Customer[] selectedCustomer = new Customer[1];
        
        searchButton.setOnAction(e -> {
            String searchText = transactionIdField.getText().trim();
            if (searchText.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Empty Input", "Please enter a transaction ID to search.");
                itemsList.clear();
                customerInfoLabel.setText("Search a transaction to review return details.");
                placeholderBox.setVisible(true);
                itemsTable.setVisible(false);
                return;
            }
            
            try {
                CustomerService customerService = new CustomerService(new MySQLCustomerRepository(DatabaseManager.getConnection()));
                SalesService salesService = new SalesService(new MySQLSaleRepository(DatabaseManager.getConnection()));
                Customer customer = null;
                try {
                    int id = Integer.parseInt(searchText.replaceAll("[^0-9]", ""));
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
                    showAlert(Alert.AlertType.WARNING, "Not Found", "Transaction or customer not found.");
                    itemsList.clear();
                    customerInfoLabel.setText("Search a transaction to review return details.");
                    placeholderBox.setVisible(true);
                    itemsTable.setVisible(false);
                    return;
                }
                
                selectedCustomer[0] = customer;
                customerInfoLabel.setText("Customer: " + customer.getName() + "\nID: " + customer.getId() + "\nEmail: " + customer.getEmail());
                
                java.util.List<Sale> allSales = salesService.getAllSales();
                ObservableList<Sale> historyItems = FXCollections.observableArrayList(allSales);
                
                itemsList.clear();
                for (Sale txn : historyItems) {
                    ReturnLineItem lineItem = new ReturnLineItem(txn);
                    lineItem.returnData.selectedProperty().addListener((obs2, oldVal2, newVal2) -> updateRefundSummary(itemsTable, subtotalLabel, itemCountLabel, totalRefundLabel));
                    lineItem.returnData.returnQtyProperty().addListener((obs2, oldVal2, newVal2) -> updateRefundSummary(itemsTable, subtotalLabel, itemCountLabel, totalRefundLabel));
                    itemsList.add(lineItem);
                }
                
                placeholderBox.setVisible(false);
                itemsTable.setVisible(true);
                playTableReveal(itemsTable);
                showNotification("✓ Transaction found and ready for return selection.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Backend Error", "Failed to fetch transaction data: " + ex.getMessage());
            }
        });
        
        processButton.setOnAction(e -> {
            if (itemsList.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "No Items", "No return items available.");
                return;
            }
            if (selectedCustomer[0] == null) {
                showAlert(Alert.AlertType.WARNING, "No Transaction", "Please search for a transaction before confirming refund.");
                return;
            }
            processReturn(itemsTable, selectedCustomer[0], stage);
            updateRefundSummary(itemsTable, subtotalLabel, itemCountLabel, totalRefundLabel);
        });
        
        Scene scene = new Scene(wrapper, 1280, 800);
        UiTheme.applyStyleSheet(scene);
        return scene;
    }
    
    private static void playTableReveal(TableView<ReturnLineItem> itemsTable) {
        itemsTable.setOpacity(0);
        itemsTable.setTranslateY(20);
        javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(javafx.util.Duration.millis(320), itemsTable);
        fade.setFromValue(0);
        fade.setToValue(1);
        javafx.animation.TranslateTransition slide = new javafx.animation.TranslateTransition(javafx.util.Duration.millis(320), itemsTable);
        slide.setFromY(20);
        slide.setToY(0);
        javafx.animation.ParallelTransition transition = new javafx.animation.ParallelTransition(fade, slide);
        transition.play();
    }
    
    // createHeader() removed — replaced by frontend.ui.TopBar

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
        if (itemCount > 0) {
            pulseRefundTotal(totalRefundLabel);
        }
    }
    
    private static void pulseRefundTotal(Label label) {
        javafx.animation.ScaleTransition pulse = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(220), label);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.05);
        pulse.setToY(1.05);
        pulse.setCycleCount(2);
        pulse.setAutoReverse(true);
        pulse.play();
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
