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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Returns {
    
    // Combined return item data structure
    public static class ReturnLineItem {
        public TransactionItem transaction;
        public ReturnItemData returnData;
        
        public ReturnLineItem(TransactionItem transaction) {
            this.transaction = transaction;
            this.returnData = new ReturnItemData();
        }
    }
    
    // Mock transaction data structure
    private static class TransactionItem {
        public String itemId;
        public String medicineName;
        public double price;
        public int quantity;
        public String batchId;
        
        public TransactionItem(String itemId, String medicineName, double price, int quantity, String batchId) {
            this.itemId = itemId;
            this.medicineName = medicineName;
            this.price = price;
            this.quantity = quantity;
            this.batchId = batchId;
        }
    }
    
    // Mock transaction database
    private static Map<String, List<TransactionItem>> mockTransactions = new HashMap<>();
    
    static {
        List<TransactionItem> transaction1 = new ArrayList<>();
        transaction1.add(new TransactionItem("1", "Paracetamol 500mg", 5.99, 10, "BATCH-001"));
        transaction1.add(new TransactionItem("2", "Ibuprofen 200mg", 7.50, 5, "BATCH-002"));
        transaction1.add(new TransactionItem("3", "Aspirin 100mg", 4.50, 15, "BATCH-003"));
        mockTransactions.put("TS-2024-001", transaction1);
        
        List<TransactionItem> transaction2 = new ArrayList<>();
        transaction2.add(new TransactionItem("4", "Amoxicillin 250mg", 12.99, 3, "BATCH-004"));
        transaction2.add(new TransactionItem("5", "Cough Syrup 100ml", 8.75, 2, "BATCH-005"));
        mockTransactions.put("TS-2024-002", transaction2);
        
        List<TransactionItem> transaction3 = new ArrayList<>();
        transaction3.add(new TransactionItem("6", "Vitamin C 1000mg", 6.50, 20, "BATCH-006"));
        transaction3.add(new TransactionItem("7", "Multivitamin Tablet", 15.99, 1, "BATCH-007"));
        mockTransactions.put("TS-2024-003", transaction3);
    }
    
    public static Scene createReturnsScene(Stage stage) {
        try {
            return createReturnsSceneInternal(stage);
        } catch (Exception e) {
            System.err.println("ERROR in Returns.createReturnsScene: " + e.getMessage());
            e.printStackTrace();
            VBox errorBox = new VBox();
            errorBox.setPadding(new Insets(20));
            Label errorLabel = new Label("Error loading Returns page:\n" + e.getMessage());
            errorLabel.setWrapText(true);
            errorBox.getChildren().add(errorLabel);
            return new Scene(errorBox, 800, 600);
        }
    }
    
    private static Scene createReturnsSceneInternal(Stage stage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #eef2f7;");
        
        // Header with back button
        HBox header = createHeader(stage);
        root.getChildren().add(header);
        
        Label titleLabel = new Label("Sales Returns");
        titleLabel.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #111827;");
        
        Label subtitleLabel = new Label("Process Sales Return — Non-CRUD Logic Module (UC17)");
        subtitleLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #475569;");
        
        VBox titleBox = new VBox(4, titleLabel, subtitleLabel);
        root.getChildren().add(titleBox);
        
        // Main content area
        HBox mainContent = new HBox(20);
        mainContent.setStyle("-fx-background-color: white; -fx-border-color: #d1d5db; -fx-border-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(one-pass-box, rgba(15,23,42,0.08), 14, 0, 0, 4);");
        
        // Left side: Customer lookup and purchase history
        VBox leftPanel = new VBox(15);
        leftPanel.setPrefWidth(550);
        
        // ==================== CUSTOMER LOOKUP SECTION ====================
        VBox customerLookupBox = new VBox(10);
        customerLookupBox.setPadding(new Insets(14));
        customerLookupBox.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #93c5fd; -fx-border-radius: 12; -fx-border-width: 1;");
        
        Label customerLookupLabel = new Label("Scan Receipt or Enter Transaction ID");
        customerLookupLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        HBox customerSearchBox = new HBox(10);
        customerSearchBox.setAlignment(Pos.CENTER_LEFT);
        
        TextField customerNameField = new TextField();
        customerNameField.setPromptText("Enter customer name or Transaction ID (TS-XXXX-XXX)...");
        customerNameField.setStyle("-fx-padding: 10; -fx-font-size: 13;");
        customerNameField.setPrefWidth(400);
        
        Button lookupButton = new Button("Fetch Order");
        lookupButton.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-padding: 10 18; -fx-font-size: 13; -fx-font-weight: bold; -fx-background-radius: 8;");
        
        customerSearchBox.getChildren().addAll(customerNameField, lookupButton);
        customerLookupBox.getChildren().addAll(customerLookupLabel, customerSearchBox);
        leftPanel.getChildren().add(customerLookupBox);
        
        VBox transactionDetailsBox = new VBox(8);
        transactionDetailsBox.setPadding(new Insets(14));
        transactionDetailsBox.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 12; -fx-border-width: 1;");
        
        Label transactionDetailsTitle = new Label("Original Transaction");
        transactionDetailsTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #111827;");
        
        Label customerInfoLabel = new Label("No transaction selected.");
        customerInfoLabel.setWrapText(true);
        customerInfoLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #475569;");
        
        transactionDetailsBox.getChildren().addAll(transactionDetailsTitle, customerInfoLabel);
        leftPanel.getChildren().add(transactionDetailsBox);
        
        // Purchase History Table
        Label historyLabel = new Label("📦 Purchase History");
        historyLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #111827;");
        leftPanel.getChildren().add(historyLabel);
        
        TableView<CustomerDatabase.Transaction> historyTable = new TableView<>();
        historyTable.setPrefHeight(250);
        historyTable.setStyle("-fx-font-size: 11;");
        
        TableColumn<CustomerDatabase.Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setPrefWidth(90);
        dateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().date.toString()));
        
        TableColumn<CustomerDatabase.Transaction, String> medicineCol = new TableColumn<>("Medicine");
        medicineCol.setPrefWidth(120);
        medicineCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().medicineName));
        
        TableColumn<CustomerDatabase.Transaction, String> batchCol = new TableColumn<>("Batch");
        batchCol.setPrefWidth(80);
        batchCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().batchId));
        
        TableColumn<CustomerDatabase.Transaction, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setPrefWidth(50);
        qtyCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().quantity));
        
        TableColumn<CustomerDatabase.Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setPrefWidth(70);
        typeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().type));
        
        TableColumn<CustomerDatabase.Transaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setPrefWidth(80);
        amountCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().totalAmount));
        amountCol.setCellFactory(column -> new TableCell<CustomerDatabase.Transaction, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.format("Rs. %.2f", item));
            }
        });
        
        historyTable.getColumns().addAll(dateCol, medicineCol, batchCol, qtyCol, typeCol, amountCol);
        leftPanel.getChildren().add(historyTable);
        VBox.setVgrow(historyTable, Priority.ALWAYS);
        
        // ==================== ITEMS TABLE FOR RETURN ====================
        Label returnItemsLabel = new Label("Select Items for Return");
        returnItemsLabel.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: #111827;");
        leftPanel.getChildren().add(returnItemsLabel);
        
        TableView<ReturnLineItem> itemsTable = new TableView<>();
        itemsTable.setPrefHeight(200);
        itemsTable.setStyle("-fx-font-size: 11;");
        
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
        TableColumn<ReturnLineItem, String> medicineColumn = new TableColumn<>("Medicine");
        medicineColumn.setPrefWidth(120);
        medicineColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().transaction.medicineName));
        
        // Quantity column
        TableColumn<ReturnLineItem, Integer> quantityColumn = new TableColumn<>("Original Qty");
        quantityColumn.setPrefWidth(80);
        quantityColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().transaction.quantity));
        
        // Price column
        TableColumn<ReturnLineItem, Double> priceColumn = new TableColumn<>("Unit Price");
        priceColumn.setPrefWidth(80);
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().transaction.price));
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
                    Spinner<Integer> spinner = new Spinner<>(0, 999, item != null ? item : 0, 1);
                    spinner.setPrefWidth(80);
                    spinner.setEditable(true);
                    ReturnLineItem lineItem = getTableView().getItems().get(getIndex());
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
        
        itemsTable.getColumns().addAll(selectColumn, medicineColumn, quantityColumn, priceColumn, returnQtyColumn, conditionColumn);
        leftPanel.getChildren().add(itemsTable);
        VBox.setVgrow(itemsTable, Priority.ALWAYS);
        
        // Right side: Refund Summary
        VBox rightPanel = new VBox(15);
        rightPanel.setPrefWidth(320);
        rightPanel.setPadding(new Insets(22));
        rightPanel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 14; -fx-border-width: 1; -fx-effect: dropshadow(one-pass-box, rgba(15,23,42,0.08), 16, 0, 0, 6);");
        
        Label refundTitleLabel = new Label("Refund Summary");
        refundTitleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #111827;");
        rightPanel.getChildren().add(refundTitleLabel);
        
        Label customerInfoLabel = new Label("");
        customerInfoLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #475569;");
        customerInfoLabel.setWrapText(true);
        rightPanel.getChildren().add(customerInfoLabel);
        
        Separator sep1 = new Separator();
        rightPanel.getChildren().add(sep1);
        
        // Refund details
        Label subtotalLabel = new Label("Refund Subtotal: Rs. 0.00");
        subtotalLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #334155;");
        
        Label itemCountLabel = new Label("Selected Return Qty: 0");
        itemCountLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #334155;");
        
        rightPanel.getChildren().addAll(subtotalLabel, itemCountLabel);
        
        Separator separator = new Separator();
        rightPanel.getChildren().add(separator);
        
        Label totalRefundLabel = new Label("Total Refund: Rs. 0.00");
        totalRefundLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        rightPanel.getChildren().add(totalRefundLabel);
        
        // Add listener for table item changes (after labels are defined)
        itemsTable.getItems().addListener((javafx.collections.ListChangeListener<? super ReturnLineItem>) change -> {
            updateRefundSummary(itemsTable, subtotalLabel, itemCountLabel, totalRefundLabel);
        });
        
        // Process button
        Button processButton = new Button("✓ Process Refund & Update Stock");
        processButton.setPrefWidth(280);
        processButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 12; -fx-font-size: 13; -fx-font-weight: bold; -fx-cursor: hand;");
        processButton.setOnAction(e -> {
            if (itemsList.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "No Items", "No items to return.");
                return;
            }
            
            // Get selected customer
            CustomerDatabase.Customer selectedCustomer = null;
            String searchText = customerNameField.getText().trim();
            if (!searchText.isEmpty()) {
                selectedCustomer = CustomerDatabase.getCustomerByName(searchText);
            }
            
            if (selectedCustomer == null) {
                showAlert(Alert.AlertType.WARNING, "No Customer Selected", "Please search for a customer first.");
                return;
            }
            
            processReturn(itemsTable, selectedCustomer, stage);
            updateRefundSummary(itemsTable, subtotalLabel, itemCountLabel, totalRefundLabel);
        });
        rightPanel.getChildren().add(processButton);
        VBox.setVgrow(rightPanel, Priority.ALWAYS);
        
        mainContent.getChildren().addAll(leftPanel, rightPanel);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        root.getChildren().add(mainContent);
        VBox.setVgrow(mainContent, Priority.ALWAYS);
        
        // Lookup button action
        final CustomerDatabase.Customer[] selectedCustomer = new CustomerDatabase.Customer[1];
        
        lookupButton.setOnAction(e -> {
            String searchText = customerNameField.getText().trim();
            if (searchText.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Empty Input", "Please enter customer name to search.");
                itemsList.clear();
                historyTable.setItems(FXCollections.observableArrayList());
                customerInfoLabel.setText("");
                return;
            }
            
            // Search by name
            CustomerDatabase.Customer customer = CustomerDatabase.getCustomerByName(searchText);
            
            if (customer == null) {
                showAlert(Alert.AlertType.WARNING, "Not Found", "Customer '" + searchText + "' not found.");
                itemsList.clear();
                historyTable.setItems(FXCollections.observableArrayList());
                customerInfoLabel.setText("");
                return;
            }
            
            selectedCustomer[0] = customer;
            customerInfoLabel.setText("Customer: " + customer.customerName + "\nID: " + customer.customerId + 
                                     "\nRegistered: " + customer.registrationDate);
            
            // Show transaction history
            ObservableList<CustomerDatabase.Transaction> historyItems = FXCollections.observableArrayList(customer.transactionHistory);
            historyTable.setItems(historyItems);
            
            // Populate return items with only sales (not returns)
            itemsList.clear();
            for (CustomerDatabase.Transaction txn : customer.transactionHistory) {
                if ("SALE".equals(txn.type)) {
                    ReturnLineItem lineItem = new ReturnLineItem(
                        new Returns.TransactionItem(txn.transactionId, txn.medicineName, txn.pricePerUnit, txn.quantity, txn.batchId)
                    );
                    lineItem.returnData.selectedProperty().addListener((obs, oldVal, newVal) -> {
                        updateRefundSummary(itemsTable, subtotalLabel, itemCountLabel, totalRefundLabel);
                    });
                    lineItem.returnData.returnQtyProperty().addListener((obs, oldVal, newVal) -> {
                        updateRefundSummary(itemsTable, subtotalLabel, itemCountLabel, totalRefundLabel);
                    });
                    itemsList.add(lineItem);
                }
            }
            
            showNotification("✓ Customer found: " + customer.customerName + " with " + 
                           customer.transactionHistory.size() + " transactions");
        });
        
        return new Scene(root, 1300, 850);
    }
    
    private static HBox createHeader(Stage stage) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background: linear-gradient(to right, #667eea, #764ba2); -fx-padding: 15; -fx-border-radius: 5;");
        
        Button backButton = new Button("← Back to Dashboard");
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-padding: 8 16; -fx-font-size: 12; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-radius: 5;");
        backButton.setOnAction(e -> {
            Scene dashboardScene = Dashboard.createDashboardScene(stage);
            stage.setScene(dashboardScene);
        });
        
        Label title = new Label("Returns Management");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white;");
        
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
                    totalRefund += returnQty * lineItem.transaction.price;
                    itemCount += returnQty;
                }
            }
        }
        
        subtotalLabel.setText(String.format("Subtotal: $%.2f", totalRefund));
        itemCountLabel.setText("Items: " + itemCount);
        totalRefundLabel.setText(String.format("Total Refund: $%.2f", totalRefund));
    }
    
    private static void processReturn(TableView<ReturnLineItem> table, CustomerDatabase.Customer customer, Stage stage) {
        StringBuilder returnDetails = new StringBuilder();
        double totalRefund = 0;
        int processedItems = 0;
        
        for (ReturnLineItem lineItem : table.getItems()) {
            if (lineItem.returnData.selected.get() && lineItem.returnData.returnQty.get() > 0) {
                TransactionItem item = lineItem.transaction;
                String condition = lineItem.returnData.condition.get();
                int returnQty = lineItem.returnData.returnQty.get();
                double refundAmount = returnQty * item.price;
                
                // Record return in customer database
                CustomerDatabase.addReturnTransaction(
                    customer.customerId,
                    item.medicineName,
                    item.batchId,
                    returnQty,
                    item.price,
                    condition
                );
                
                // Determine stock action
                String stockAction = "Wrong Item (Restock)".equals(condition) ? "✓ RESTOCKED" : "⚠ QUARANTINED";
                
                returnDetails.append(String.format("• %s (%dx %s) - %s\n  Refund: Rs. %.2f\n  Action: %s\n\n",
                    item.medicineName,
                    returnQty,
                    item.batchId,
                    item.medicineName,
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
        
        // Show success dialog
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Return Processed Successfully");
        successAlert.setHeaderText("✓ Refund & History Updated");
        successAlert.setContentText(String.format("Customer: %s\nTotal Items Returned: %d\nTotal Refund: Rs. %.2f\n\n%s", 
            customer.customerName, processedItems, totalRefund, returnDetails.toString()));
        successAlert.showAndWait();
        
        // Show toast notification
        showSuccessToast(String.format("Return processed: Rs. %.2f refund for %s (%d items)", totalRefund, customer.customerName, processedItems));
    }
    
    private static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private static void showNotification(String message) {
        System.out.println("✓ " + message);
        // In a real application, this could be a toast notification
    }
    
    private static void showSuccessToast(String message) {
        System.out.println("✓ SUCCESS: " + message);
        // Create a temporary label to show success
        Label toastLabel = new Label(message);
        toastLabel.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 15; -fx-font-size: 12; -fx-border-radius: 5;");
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
