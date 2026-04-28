package frontend.pages;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class SalesTerminal {
    
    // Medicine model for cart
    public static class CartItem {
        private String medicineName;
        private String batchId;
        private int quantity;
        private double price;
        private int availableStock;
        private int dosageMg;
        
        public CartItem(String medicineName, String batchId, int quantity, double price, int availableStock, int dosageMg) {
            this.medicineName = medicineName;
            this.batchId = batchId;
            this.quantity = quantity;
            this.price = price;
            this.availableStock = availableStock;
            this.dosageMg = dosageMg;
        }
        
        public String getMedicineName() { return medicineName; }
        public String getBatchId() { return batchId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getPrice() { return price; }
        public int getAvailableStock() { return availableStock; }
        public int getDosageMg() { return dosageMg; }
        public double getSubtotal() { return quantity * price; }
        public boolean isInsufficientStock() { return quantity > availableStock; }
    }
    
    private static ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private static ObservableList<CartItem> searchResults = FXCollections.observableArrayList();
    
    // Sample medicines database
    private static final Map<String, CartItem> medicinesDatabase = new HashMap<>();
    
    static {
        medicinesDatabase.put("Aspirin", new CartItem("Aspirin", "BATCH001", 0, 5.50, 100, 500));
        medicinesDatabase.put("Paracetamol", new CartItem("Paracetamol", "BATCH002", 0, 4.00, 150, 500));
        medicinesDatabase.put("Amoxicillin", new CartItem("Amoxicillin", "BATCH003", 0, 8.50, 50, 250));
        medicinesDatabase.put("Ibuprofen", new CartItem("Ibuprofen", "BATCH004", 0, 6.00, 80, 400));
        medicinesDatabase.put("Metformin", new CartItem("Metformin", "BATCH005", 0, 7.50, 120, 500));
        medicinesDatabase.put("Lisinopril", new CartItem("Lisinopril", "BATCH006", 0, 12.00, 60, 10));
    }
    
    public static Scene createSalesTerminalScene(Stage stage) {
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: #f5f5f5;");
        
        // Header with back button
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background: linear-gradient(to right, #1e3c72, #2a5298); -fx-padding: 12; -fx-border-radius: 5;");
        
        Button backButton = new Button("← Back to Dashboard");
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; -fx-padding: 8 16; -fx-font-size: 12; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-radius: 5;");
        backButton.setOnAction(e -> {
            Scene dashboardScene = Dashboard.createDashboardScene(stage);
            stage.setScene(dashboardScene);
        });
        
        Label headerTitle = new Label("Sales Terminal");
        headerTitle.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white;");
        
        header.getChildren().addAll(backButton, headerTitle);
        HBox.setHgrow(headerTitle, Priority.ALWAYS);
        mainContainer.getChildren().add(header);
        
        // Content area
        HBox contentArea = new HBox(15);
        contentArea.setStyle("-fx-background-color: #f5f5f5;");
        mainContainer.getChildren().add(contentArea);
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        
        // ==================== LEFT SIDE ====================
        VBox leftPanel = new VBox(15);
        leftPanel.setPadding(new Insets(15));
        leftPanel.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");
        leftPanel.setPrefWidth(400);
        
        // Title
        Label leftTitle = new Label("Sales Terminal");
        leftTitle.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        // Search bar
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        TextField searchField = new TextField();
        searchField.setPromptText("Search for medicine...");
        searchField.setStyle("-fx-padding: 8; -fx-font-size: 12;");
        
        Button searchButton = new Button("🔍 Search");
        searchButton.setStyle("-fx-padding: 8; -fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand;");
        
        searchBox.getChildren().addAll(searchField, searchButton);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        
        // Search Results List
        Label searchResultsLabel = new Label("Search Results:");
        searchResultsLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold;");
        
        ListView<CartItem> searchResultsList = new ListView<>();
        searchResultsList.setPrefHeight(300);
        searchResultsList.setCellFactory(param -> new SearchResultCell(cartItems));
        
        // Search functionality
        searchButton.setOnAction(e -> {
            String query = searchField.getText().toLowerCase();
            searchResults.clear();
            
            if (query.isEmpty()) {
                searchResults.addAll(medicinesDatabase.values());
            } else {
                medicinesDatabase.values().stream()
                    .filter(item -> item.getMedicineName().toLowerCase().contains(query))
                    .forEach(searchResults::add);
            }
            
            searchResultsList.setItems(searchResults);
        });
        
        // Initialize with all medicines
        searchResults.addAll(medicinesDatabase.values());
        searchResultsList.setItems(searchResults);
        
        // Dosage Converter Section
        VBox dosageBox = new VBox(10);
        dosageBox.setPadding(new Insets(10));
        dosageBox.setStyle("-fx-background-color: #e8f5e9; -fx-border-color: #4CAF50; -fx-border-radius: 5;");
        
        Label dosageTitle = new Label("💊 Dosage Converter");
        dosageTitle.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");
        
        HBox dosageInputBox = new HBox(10);
        dosageInputBox.setAlignment(Pos.CENTER_LEFT);
        Label tabletsLabel = new Label("Tablets:");
        tabletsLabel.setPrefWidth(60);
        Spinner<Integer> tabletsSpinner = new Spinner<>(1, 100, 1);
        tabletsSpinner.setPrefWidth(80);
        
        Label resultLabel = new Label("= 500 mg");
        resultLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #1976d2;");
        
        tabletsSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            int mg = newVal * 500;
            resultLabel.setText("= " + mg + " mg");
        });
        
        dosageInputBox.getChildren().addAll(tabletsLabel, tabletsSpinner, resultLabel);
        dosageBox.getChildren().addAll(dosageTitle, dosageInputBox);
        
        leftPanel.getChildren().addAll(
            leftTitle,
            searchBox,
            searchResultsLabel,
            searchResultsList,
            dosageBox
        );
        
        VBox.setVgrow(searchResultsList, Priority.ALWAYS);
        
        // ==================== RIGHT SIDE ====================
        VBox rightPanel = new VBox(15);
        rightPanel.setPadding(new Insets(15));
        rightPanel.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");
        rightPanel.setPrefWidth(500);
        
        // Title
        Label rightTitle = new Label("Current Order");
        rightTitle.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        // Cart Table
        TableView<CartItem> cartTable = new TableView<>();
        cartTable.setStyle("-fx-font-size: 11;");
        cartTable.setPrefHeight(250);
        
        TableColumn<CartItem, String> nameColumn = new TableColumn<>("Medicine");
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMedicineName()));
        nameColumn.setPrefWidth(120);
        
        TableColumn<CartItem, Integer> quantityColumn = new TableColumn<>("Qty");
        quantityColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getQuantity()));
        quantityColumn.setPrefWidth(50);
        
        TableColumn<CartItem, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPrice()));
        priceColumn.setPrefWidth(60);
        
        TableColumn<CartItem, Double> subtotalColumn = new TableColumn<>("Subtotal");
        subtotalColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getSubtotal()));
        subtotalColumn.setPrefWidth(80);
        
        TableColumn<CartItem, Void> removeColumn = new TableColumn<>("Action");
        removeColumn.setPrefWidth(80);
        removeColumn.setCellFactory(param -> new RemoveButtonCell(cartTable));
        
        cartTable.getColumns().addAll(nameColumn, quantityColumn, priceColumn, subtotalColumn, removeColumn);
        cartTable.setItems(cartItems);
        
        // Apply row styling for insufficient stock
        cartTable.setRowFactory(tv -> new TableRow<CartItem>() {
            @Override
            protected void updateItem(CartItem item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setStyle("");
                } else {
                    if (item.isInsufficientStock()) {
                        setStyle("-fx-background-color: #ffcdd2;");  // Light red
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        
        // Insufficient Stock Warning
        Label stockWarning = new Label();
        stockWarning.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold; -fx-font-size: 11;");
        stockWarning.setVisible(false);
        
        // ==================== CUSTOMER INFO SECTION ====================
        VBox customerBox = new VBox(10);
        customerBox.setPadding(new Insets(10));
        customerBox.setStyle("-fx-background-color: #e3f2fd; -fx-border-color: #2196F3; -fx-border-radius: 5;");
        
        Label customerLabel = new Label("👤 Customer Information");
        customerLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #1565c0;");
        
        HBox customerInputBox = new HBox(10);
        customerInputBox.setAlignment(Pos.CENTER_LEFT);
        
        Label nameLabel = new Label("Customer Name:");
        nameLabel.setPrefWidth(120);
        
        TextField customerNameField = new TextField();
        customerNameField.setPromptText("Enter customer name...");
        customerNameField.setStyle("-fx-padding: 8; -fx-font-size: 12;");
        customerNameField.setPrefWidth(250);
        
        Button recordCustomerButton = new Button("✓ Record");
        recordCustomerButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 8 16; -fx-cursor: hand;");
        
        Label customerIdLabel = new Label("");
        customerIdLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #1565c0; -fx-font-weight: bold;");
        
        customerInputBox.getChildren().addAll(nameLabel, customerNameField, recordCustomerButton, customerIdLabel);
        customerBox.getChildren().addAll(customerLabel, customerInputBox);
        
        // Reference to currently selected customer
        final CustomerDatabase.Customer[] currentCustomer = new CustomerDatabase.Customer[1];
        
        recordCustomerButton.setOnAction(e -> {
            String customerName = customerNameField.getText().trim();
            if (customerName.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter customer name!");
                return;
            }
            
            currentCustomer[0] = CustomerDatabase.getOrCreateCustomer(customerName);
            customerIdLabel.setText("ID: " + currentCustomer[0].customerId);
            customerNameField.setStyle("-fx-padding: 8; -fx-font-size: 12; -fx-control-inner-background: #c8e6c9;");
            showNotification("✓ Customer: " + currentCustomer[0].customerName);
        });
        
        // Summary section
        VBox summaryBox = new VBox(8);
        summaryBox.setPadding(new Insets(10));
        summaryBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #999; -fx-border-radius: 3;");
        
        Label subtotalLabel = new Label("Subtotal: Rs. 0.00");
        subtotalLabel.setStyle("-fx-font-size: 12;");
        
        Label taxLabel = new Label("Tax (10%): Rs. 0.00");
        taxLabel.setStyle("-fx-font-size: 12;");
        
        Label totalLabel = new Label("Total: Rs. 0.00");
        totalLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #1976d2;");
        
        summaryBox.getChildren().addAll(subtotalLabel, taxLabel, totalLabel);
        
        // Update summary and warning when cart changes
        cartItems.addListener((javafx.collections.ListChangeListener<? super CartItem>) c -> {
            double subtotal = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
            double tax = subtotal * 0.10;
            double total = subtotal + tax;
            
            DecimalFormat df = new DecimalFormat("0.00");
            subtotalLabel.setText("Subtotal: Rs. " + df.format(subtotal));
            taxLabel.setText("Tax (10%): Rs. " + df.format(tax));
            totalLabel.setText("Total: Rs. " + df.format(total));
            
            boolean hasInsufficientStock = cartItems.stream().anyMatch(CartItem::isInsufficientStock);
            if (hasInsufficientStock) {
                stockWarning.setText("⚠️ Insufficient Stock: Some items exceed available inventory!");
                stockWarning.setVisible(true);
            } else {
                stockWarning.setVisible(false);
            }
            
            cartTable.refresh();
        });
        
        // Clinical Check Section
        VBox clinicalCheckBox = new VBox(10);
        clinicalCheckBox.setPadding(new Insets(10));
        clinicalCheckBox.setStyle("-fx-background-color: #f3e5f5; -fx-border-color: #9c27b0; -fx-border-radius: 5;");
        
        Label clinicalTitle = new Label("🔐 Safety Validation");
        clinicalTitle.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #6a1b9a;");
        
        HBox clinicalBox = new HBox(10);
        clinicalBox.setAlignment(Pos.CENTER_LEFT);
        
        Button validateButton = new Button("✓ Safety Validate");
        validateButton.setStyle("-fx-background-color: #9c27b0; -fx-text-fill: white; -fx-padding: 8; -fx-cursor: hand;");
        
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(30, 30);
        spinner.setVisible(false);
        
        Label validationResult = new Label();
        validationResult.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        validationResult.setVisible(false);
        
        validateButton.setOnAction(e -> {
            if (cartItems.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Empty Cart", "Please add items to cart before validation!");
                return;
            }
            
            validateButton.setDisable(true);
            spinner.setVisible(true);
            validationResult.setVisible(false);
            
            // Simulate validation with a 2-second delay
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
                Platform.runLater(() -> {
                    spinner.setVisible(false);
                    validationResult.setText("✅ Clinical Check Passed");
                    validationResult.setStyle("-fx-text-fill: #2e7d32; -fx-font-size: 12; -fx-font-weight: bold;");
                    validationResult.setVisible(true);
                    validateButton.setDisable(false);
                });
            }));
            timeline.play();
        });
        
        clinicalBox.getChildren().addAll(validateButton, spinner, validationResult);
        clinicalCheckBox.getChildren().addAll(clinicalTitle, clinicalBox);
        
        // Buttons section
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button checkoutButton = new Button("💳 Checkout");
        checkoutButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10; -fx-font-weight: bold; -fx-cursor: hand;");
        checkoutButton.setOnAction(e -> {
            if (cartItems.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Empty Cart", "Please add items before checkout!");
                return;
            }
            
            if (currentCustomer[0] == null) {
                showAlert(Alert.AlertType.WARNING, "No Customer", "Please record customer name first!");
                return;
            }
            
            if (cartItems.stream().anyMatch(CartItem::isInsufficientStock)) {
                showAlert(Alert.AlertType.ERROR, "Stock Issue", "Cannot checkout: Some items have insufficient stock!");
                return;
            }
            
            // Record all transactions
            for (CartItem item : cartItems) {
                CustomerDatabase.addSaleTransaction(
                    currentCustomer[0].customerId,
                    item.getMedicineName(),
                    item.getBatchId(),
                    item.getQuantity(),
                    item.getPrice()
                );
            }
            
            double subtotal = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
            double tax = subtotal * 0.10;
            double total = subtotal + tax;
            
            showAlert(Alert.AlertType.INFORMATION, "✓ Success", 
                "Order placed successfully!\n\nCustomer: " + currentCustomer[0].customerName +
                "\nTotal: Rs. " + String.format("%.2f", total) +
                "\n\nTransaction recorded in customer history.");
            
            cartItems.clear();
            validationResult.setVisible(false);
            customerNameField.clear();
            customerNameField.setStyle("-fx-padding: 8; -fx-font-size: 12;");
            customerIdLabel.setText("");
            currentCustomer[0] = null;
        });
        
        Button clearButton = new Button("🗑️ Clear Cart");
        clearButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 10; -fx-cursor: hand;");
        clearButton.setOnAction(e -> cartItems.clear());
        
        buttonsBox.getChildren().addAll(checkoutButton, clearButton);
        
        rightPanel.getChildren().addAll(
            rightTitle,
            cartTable,
            stockWarning,
            customerBox,
            summaryBox,
            clinicalCheckBox,
            buttonsBox
        );
        
        VBox.setVgrow(cartTable, Priority.ALWAYS);
        
        // Add both panels to content area
        contentArea.getChildren().addAll(leftPanel, rightPanel);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);
        
        return new Scene(mainContainer, 1200, 800);
    }
    
    // Custom cell for search results with Add to Cart button
    private static class SearchResultCell extends ListCell<CartItem> {
        private ObservableList<CartItem> cart;
        
        public SearchResultCell(ObservableList<CartItem> cart) {
            this.cart = cart;
        }
        
        @Override
        protected void updateItem(CartItem item, boolean empty) {
            super.updateItem(item, empty);
            
            if (empty || item == null) {
                setGraphic(null);
            } else {
                VBox cellContent = new VBox(5);
                cellContent.setPadding(new Insets(8));
                cellContent.setStyle("-fx-border-color: #eee; -fx-border-radius: 3;");
                
                HBox nameBox = new HBox(10);
                Label nameLabel = new Label(item.getMedicineName());
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
                Label priceLabel = new Label("Rs. " + item.getPrice());
                priceLabel.setStyle("-fx-text-fill: #1976d2; -fx-font-weight: bold;");
                nameBox.getChildren().addAll(nameLabel, priceLabel);
                HBox.setHgrow(nameLabel, Priority.ALWAYS);
                
                HBox detailsBox = new HBox(15);
                Label stockLabel = new Label("Stock: " + item.getAvailableStock());
                stockLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #666;");
                Label dosageLabel = new Label("Dosage: " + item.getDosageMg() + " mg");
                dosageLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #666;");
                
                Spinner<Integer> quantitySpinner = new Spinner<>(1, item.getAvailableStock(), 1);
                quantitySpinner.setPrefWidth(70);
                
                Button addButton = new Button("Add to Cart");
                addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5; -fx-font-size: 10; -fx-cursor: hand;");
                addButton.setOnAction(e -> {
                    CartItem existingItem = cart.stream()
                        .filter(ci -> ci.getMedicineName().equals(item.getMedicineName()))
                        .findFirst()
                        .orElse(null);
                    
                    int qty = quantitySpinner.getValue();
                    
                    if (existingItem != null) {
                        existingItem.setQuantity(existingItem.getQuantity() + qty);
                    } else {
                        CartItem cartItem = new CartItem(
                            item.getMedicineName(),
                            item.getBatchId(),
                            qty,
                            item.getPrice(),
                            item.getAvailableStock(),
                            item.getDosageMg()
                        );
                        cart.add(cartItem);
                    }
                    
                    showAlert(Alert.AlertType.INFORMATION, "Added", qty + " units of " + item.getMedicineName() + " added to cart!");
                });
                
                HBox actionBox = new HBox(10);
                actionBox.setAlignment(Pos.CENTER_LEFT);
                actionBox.getChildren().addAll(quantitySpinner, addButton);
                
                cellContent.getChildren().addAll(nameBox, detailsBox, actionBox);
                setGraphic(cellContent);
            }
        }
    }
    
    // Custom cell for remove button in cart table
    private static class RemoveButtonCell extends TableCell<CartItem, Void> {
        private TableView<CartItem> table;
        
        public RemoveButtonCell(TableView<CartItem> table) {
            this.table = table;
        }
        
        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            
            if (empty || getIndex() < 0 || getIndex() >= cartItems.size()) {
                setGraphic(null);
            } else {
                Button removeBtn = new Button("Remove");
                removeBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 5; -fx-font-size: 10;");
                removeBtn.setOnAction(e -> {
                    CartItem item1 = getTableView().getItems().get(getIndex());
                    cartItems.remove(item1);
                });
                setGraphic(removeBtn);
            }
        }
    }
    
    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private static void showNotification(String message) {
        System.out.println(message);
    }
}
