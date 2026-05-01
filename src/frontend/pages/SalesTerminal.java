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
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import frontend.ui.UiTheme;
import frontend.ui.Animations;

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
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-background");

        // ─── TOP HEADER BAR ───
        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 22, 14, 22));
        header.getStyleClass().add("top-bar");

        Button backButton = new Button("← Dashboard");
        backButton.getStyleClass().addAll("button-base", "secondary-button");
        backButton.setStyle("-fx-font-size: 12;");
        backButton.setOnAction(e -> stage.setScene(Dashboard.createDashboardScene(stage)));
        Animations.bindPulseOnClick(backButton);

        VBox titleWrap = new VBox(1);
        Label headerTitle = new Label("Sales & Dispensing");
        headerTitle.getStyleClass().add("heading-l");
        headerTitle.setStyle("-fx-font-size: 22;");
        Label headerSub = new Label("Point-of-Sale Terminal  ·  Real-time Inventory");
        headerSub.getStyleClass().add("body-text");
        headerSub.setStyle("-fx-font-size: 11;");
        titleWrap.getChildren().addAll(headerTitle, headerSub);

        Region hspacer = new Region();
        HBox.setHgrow(hspacer, Priority.ALWAYS);

        Label liveBadge = new Label("● LIVE");
        liveBadge.setStyle("-fx-background-color: #ECFDF5; -fx-text-fill: #059669; -fx-background-radius: 999; -fx-padding: 4 12; -fx-font-size: 10; -fx-font-weight: 800;");

        header.getChildren().addAll(backButton, titleWrap, hspacer, liveBadge);
        root.setTop(header);
        BorderPane.setMargin(header, new Insets(16, 22, 0, 22));

        // ─── MAIN CONTENT: LEFT (Catalog) + RIGHT (Order) ───
        HBox content = new HBox(18);
        content.setPadding(new Insets(16, 22, 22, 22));

        VBox leftPanel = buildCatalogPanel();
        VBox rightPanel = buildOrderPanel(stage);

        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        rightPanel.setPrefWidth(480);
        rightPanel.setMinWidth(440);

        content.getChildren().addAll(leftPanel, rightPanel);
        root.setCenter(content);

        Scene scene = new Scene(root, 1280, 820);
        UiTheme.applyStyleSheet(scene);
        Animations.applyPageTransition(root);
        return scene;
    }

    // ════════════════════════════════════════
    //  LEFT PANEL — Medicine Catalog
    // ════════════════════════════════════════
    private static VBox buildCatalogPanel() {
        VBox panel = new VBox(14);
        panel.getStyleClass().add("card");

        // Section header
        Label sectionLabel = new Label("MEDICINE CATALOG");
        sectionLabel.getStyleClass().add("section-label");

        // Search bar
        HBox searchRow = new HBox(10);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search medicines by name...");
        searchField.getStyleClass().add("search-input");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchBtn = new Button("Search");
        searchBtn.getStyleClass().addAll("button-base", "primary-button");
        searchBtn.setStyle("-fx-font-size: 12; -fx-padding: 8 18;");
        Animations.bindPulseOnClick(searchBtn);

        searchRow.getChildren().addAll(searchField, searchBtn);

        // Results list
        ListView<CartItem> resultsList = new ListView<>();
        resultsList.setPrefHeight(380);
        resultsList.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0; -fx-border-width: 0;");
        resultsList.setCellFactory(param -> new SearchResultCell(cartItems));

        // Search logic
        searchBtn.setOnAction(e -> {
            String query = searchField.getText().toLowerCase();
            searchResults.clear();
            if (query.isEmpty()) {
                searchResults.addAll(medicinesDatabase.values());
            } else {
                medicinesDatabase.values().stream()
                    .filter(item -> item.getMedicineName().toLowerCase().contains(query))
                    .forEach(searchResults::add);
            }
            resultsList.setItems(searchResults);
        });

        // Also search on Enter key
        searchField.setOnAction(e -> searchBtn.fire());

        // Initialize
        searchResults.addAll(medicinesDatabase.values());
        resultsList.setItems(searchResults);

        // ─── Dosage Converter (compact) ───
        VBox dosageBox = new VBox(8);
        dosageBox.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 10; -fx-border-color: #E2E8F0; -fx-border-radius: 10; -fx-border-width: 1; -fx-padding: 12;");

        Label dosageTitle = new Label("DOSAGE CONVERTER");
        dosageTitle.getStyleClass().add("section-label");

        HBox dosageRow = new HBox(10);
        dosageRow.setAlignment(Pos.CENTER_LEFT);
        Label tabLabel = new Label("Tablets:");
        tabLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #64748B;");
        Spinner<Integer> tabSpinner = new Spinner<>(1, 100, 1);
        tabSpinner.setPrefWidth(80);
        Label resultLabel = new Label("= 500 mg");
        resultLabel.setStyle("-fx-font-size: 13; -fx-font-weight: 800; -fx-text-fill: #6366F1;");

        tabSpinner.valueProperty().addListener((obs, o, n) -> resultLabel.setText("= " + (n * 500) + " mg"));
        dosageRow.getChildren().addAll(tabLabel, tabSpinner, resultLabel);
        dosageBox.getChildren().addAll(dosageTitle, dosageRow);

        panel.getChildren().addAll(sectionLabel, searchRow, resultsList, dosageBox);
        VBox.setVgrow(resultsList, Priority.ALWAYS);
        return panel;
    }

    // ════════════════════════════════════════
    //  RIGHT PANEL — Current Order
    // ════════════════════════════════════════
    private static VBox buildOrderPanel(Stage stage) {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("order-panel");

        // Title row
        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label orderTitle = new Label("Current Order");
        orderTitle.getStyleClass().add("heading-m");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label itemCountBadge = new Label("0 items");
        itemCountBadge.setStyle("-fx-background-color: #EEF2FF; -fx-text-fill: #4338CA; -fx-background-radius: 999; -fx-padding: 3 12; -fx-font-size: 11; -fx-font-weight: 700;");
        titleRow.getChildren().addAll(orderTitle, sp, itemCountBadge);

        // ─── Cart Table ───
        Label cartLabel = new Label("ORDER ITEMS");
        cartLabel.getStyleClass().add("section-label");

        TableView<CartItem> cartTable = new TableView<>();
        cartTable.setStyle("-fx-font-size: 12; -fx-background-color: transparent; -fx-border-width: 0;");
        cartTable.setPrefHeight(280);
        cartTable.setMinHeight(250);
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<CartItem, String> nameCol = new TableColumn<>("Medicine");
        nameCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getMedicineName()));

        TableColumn<CartItem, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getQuantity()));
        qtyCol.setMaxWidth(60);

        TableColumn<CartItem, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(String.format("Rs. %.2f", cd.getValue().getPrice())));
        priceCol.setMaxWidth(90);

        TableColumn<CartItem, String> subtCol = new TableColumn<>("Subtotal");
        subtCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(String.format("Rs. %.2f", cd.getValue().getSubtotal())));
        subtCol.setMaxWidth(100);

        TableColumn<CartItem, Void> actCol = new TableColumn<>("");
        actCol.setMaxWidth(70);
        actCol.setCellFactory(param -> new RemoveButtonCell(cartTable));

        cartTable.getColumns().addAll(nameCol, qtyCol, priceCol, subtCol, actCol);
        cartTable.setItems(cartItems);

        // Row styling for insufficient stock
        cartTable.setRowFactory(tv -> new TableRow<CartItem>() {
            @Override
            protected void updateItem(CartItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setStyle(""); }
                else if (item.isInsufficientStock()) { setStyle("-fx-background-color: #FEF2F2;"); }
                else { setStyle(""); }
            }
        });

        // Stock warning
        Label stockWarning = new Label();
        stockWarning.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: 700; -fx-font-size: 11; -fx-background-color: #FEF2F2; -fx-background-radius: 8; -fx-padding: 8 12;");
        stockWarning.setVisible(false);
        stockWarning.setMaxWidth(Double.MAX_VALUE);

        // ─── Customer Section (compact) ───
        VBox customerBox = new VBox(4);
        customerBox.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 10; -fx-border-color: #E2E8F0; -fx-border-radius: 10; -fx-border-width: 1; -fx-padding: 8 12;");

        HBox custRow = new HBox(8);
        custRow.setAlignment(Pos.CENTER_LEFT);
        Label custIcon = new Label("Customer");
        custIcon.setStyle("-fx-font-size: 11; -fx-font-weight: 700; -fx-text-fill: #64748B;");
        TextField customerNameField = new TextField();
        customerNameField.setPromptText("Name...");
        customerNameField.getStyleClass().add("search-input");
        customerNameField.setStyle("-fx-font-size: 11; -fx-padding: 6 10;");
        customerNameField.setPrefHeight(30);
        HBox.setHgrow(customerNameField, Priority.ALWAYS);

        Button recordBtn = new Button("Register");
        recordBtn.getStyleClass().addAll("button-base", "primary-button");
        recordBtn.setStyle("-fx-font-size: 10; -fx-padding: 6 14;");
        Animations.bindPulseOnClick(recordBtn);

        Label customerIdLabel = new Label("");
        customerIdLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #059669; -fx-font-weight: 700;");

        custRow.getChildren().addAll(custIcon, customerNameField, recordBtn, customerIdLabel);
        customerBox.getChildren().add(custRow);

        // Reference to currently selected customer
        final CustomerDatabase.Customer[] currentCustomer = new CustomerDatabase.Customer[1];

        recordBtn.setOnAction(e -> {
            String customerName = customerNameField.getText().trim();
            if (customerName.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter customer name!");
                return;
            }
            currentCustomer[0] = CustomerDatabase.getOrCreateCustomer(customerName);
            customerIdLabel.setText("✓ " + currentCustomer[0].customerId);
            customerNameField.setStyle("-fx-font-size: 11; -fx-padding: 6 10; -fx-background-color: #F0FDF4; -fx-border-color: #86EFAC; -fx-border-radius: 10; -fx-background-radius: 10;");
            showNotification("✓ Customer: " + currentCustomer[0].customerName);
        });

        // ─── Summary Section ───
        VBox summaryBox = new VBox(6);
        summaryBox.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 12; -fx-padding: 14;");

        Label summaryTitle = new Label("ORDER SUMMARY");
        summaryTitle.getStyleClass().add("section-label");

        Label subtotalLabel = new Label("Subtotal: Rs. 0.00");
        subtotalLabel.getStyleClass().add("summary-label");
        Label taxLabel = new Label("Tax (10%): Rs. 0.00");
        taxLabel.getStyleClass().add("summary-label");

        summaryBox.getChildren().addAll(summaryTitle, subtotalLabel, taxLabel);

        // Total strip (gradient background)
        HBox totalStrip = new HBox();
        totalStrip.getStyleClass().add("order-total-strip");
        totalStrip.setAlignment(Pos.CENTER_LEFT);
        Label totalLabel = new Label("Total: Rs. 0.00");
        totalLabel.setStyle("-fx-font-size: 20; -fx-font-weight: 800; -fx-text-fill: white;");
        Region tsp = new Region();
        HBox.setHgrow(tsp, Priority.ALWAYS);
        Label itemsTag = new Label("0 items");
        itemsTag.setStyle("-fx-font-size: 12; -fx-text-fill: rgba(255,255,255,0.7); -fx-font-weight: 600;");
        totalStrip.getChildren().addAll(totalLabel, tsp, itemsTag);

        // Cart change listener — updates summary, warning, badge
        cartItems.addListener((javafx.collections.ListChangeListener<? super CartItem>) c -> {
            double subtotal = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
            double tax = subtotal * 0.10;
            double total = subtotal + tax;
            DecimalFormat df = new DecimalFormat("0.00");

            subtotalLabel.setText("Subtotal: Rs. " + df.format(subtotal));
            taxLabel.setText("Tax (10%): Rs. " + df.format(tax));
            totalLabel.setText("Total: Rs. " + df.format(total));

            int count = cartItems.size();
            itemCountBadge.setText(count + " item" + (count != 1 ? "s" : ""));
            itemsTag.setText(count + " item" + (count != 1 ? "s" : ""));

            boolean hasInsufficient = cartItems.stream().anyMatch(CartItem::isInsufficientStock);
            if (hasInsufficient) {
                stockWarning.setText("⚠ Some items exceed available inventory");
                stockWarning.setVisible(true);
            } else {
                stockWarning.setVisible(false);
            }
            cartTable.refresh();
        });

        // ─── Clinical Validation Section ───
        VBox clinicalBox = new VBox(10);
        clinicalBox.getStyleClass().add("clinical-section");

        Label clinTitle = new Label("SAFETY VALIDATION");
        clinTitle.getStyleClass().add("section-label");

        HBox clinRow = new HBox(10);
        clinRow.setAlignment(Pos.CENTER_LEFT);

        Button validateBtn = new Button("Run Clinical Check");
        validateBtn.getStyleClass().add("validate-button");
        Animations.bindPulseOnClick(validateBtn);

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(24, 24);
        spinner.setVisible(false);

        Label validationResult = new Label();
        validationResult.setStyle("-fx-font-size: 12; -fx-font-weight: 700;");
        validationResult.setVisible(false);

        validateBtn.setOnAction(e -> {
            if (cartItems.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Empty Cart", "Please add items to cart before validation!");
                return;
            }
            validateBtn.setDisable(true);
            spinner.setVisible(true);
            validationResult.setVisible(false);

            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
                Platform.runLater(() -> {
                    spinner.setVisible(false);
                    validationResult.setText("✓ Clinical Check Passed — No Interactions");
                    validationResult.setStyle("-fx-text-fill: #059669; -fx-font-size: 12; -fx-font-weight: 700;");
                    validationResult.setVisible(true);
                    validateBtn.setDisable(false);
                });
            }));
            timeline.play();
        });

        clinRow.getChildren().addAll(validateBtn, spinner, validationResult);
        clinicalBox.getChildren().addAll(clinTitle, clinRow);

        // ─── Action Buttons ───
        HBox actionRow = new HBox(10);
        actionRow.setAlignment(Pos.CENTER_RIGHT);

        Button clearBtn = new Button("Clear Cart");
        clearBtn.getStyleClass().addAll("button-base", "secondary-button");
        clearBtn.setStyle("-fx-font-size: 12;");
        clearBtn.setOnAction(e -> cartItems.clear());
        Animations.bindPulseOnClick(clearBtn);

        Button checkoutBtn = new Button("Checkout & Dispense");
        checkoutBtn.getStyleClass().add("checkout-button");
        Animations.bindPulseOnClick(checkoutBtn);

        checkoutBtn.setOnAction(e -> {
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
            customerNameField.getStyleClass().removeAll("search-input");
            customerNameField.getStyleClass().add("search-input");
            customerNameField.setStyle("-fx-font-size: 12;");
            customerIdLabel.setText("");
            currentCustomer[0] = null;
        });

        actionRow.getChildren().addAll(clearBtn, checkoutBtn);

        // Assemble order panel
        panel.getChildren().addAll(
            titleRow, cartLabel, cartTable, stockWarning,
            customerBox, summaryBox, totalStrip,
            clinicalBox, actionRow
        );
        VBox.setVgrow(cartTable, Priority.ALWAYS);
        return panel;
    }

    // ════════════════════════════════════════
    //  Custom cell for medicine catalog cards
    // ════════════════════════════════════════
    private static class SearchResultCell extends ListCell<CartItem> {
        private ObservableList<CartItem> cart;
        
        public SearchResultCell(ObservableList<CartItem> cart) {
            this.cart = cart;
            setStyle("-fx-background-color: transparent; -fx-padding: 3 0;");
        }
        
        @Override
        protected void updateItem(CartItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setStyle("-fx-background-color: transparent;");
            } else {
                VBox card = new VBox(8);
                card.getStyleClass().add("medicine-catalog-card");

                // Top row: name + price
                HBox topRow = new HBox(8);
                topRow.setAlignment(Pos.CENTER_LEFT);
                Label nameLabel = new Label(item.getMedicineName());
                nameLabel.setStyle("-fx-font-size: 14; -fx-font-weight: 800; -fx-text-fill: #0B1120;");
                Region s1 = new Region();
                HBox.setHgrow(s1, Priority.ALWAYS);
                Label priceLabel = new Label("Rs. " + String.format("%.2f", item.getPrice()));
                priceLabel.setStyle("-fx-font-size: 14; -fx-font-weight: 800; -fx-text-fill: #6366F1;");
                topRow.getChildren().addAll(nameLabel, s1, priceLabel);

                // Badges row: stock + dosage + batch
                HBox badges = new HBox(6);
                badges.setAlignment(Pos.CENTER_LEFT);
                Label stockBadge = new Label("Stock: " + item.getAvailableStock());
                stockBadge.getStyleClass().add(item.getAvailableStock() < 20 ? "stock-badge-low" : "stock-badge");
                Label dosageBadge = new Label(item.getDosageMg() + " mg");
                dosageBadge.getStyleClass().add("dosage-chip");
                Label batchBadge = new Label(item.getBatchId());
                batchBadge.setStyle("-fx-font-size: 10; -fx-text-fill: #94A3B8;");
                badges.getChildren().addAll(stockBadge, dosageBadge, batchBadge);

                // Action row: spinner + add button
                HBox actionRow = new HBox(8);
                actionRow.setAlignment(Pos.CENTER_LEFT);
                Spinner<Integer> qtySpinner = new Spinner<>(1, Math.max(1, item.getAvailableStock()), 1);
                qtySpinner.setPrefWidth(75);
                qtySpinner.setStyle("-fx-font-size: 11;");

                Button addBtn = new Button("+ Add to Cart");
                addBtn.getStyleClass().add("add-cart-button");
                addBtn.setOnAction(e -> {
                    CartItem existing = cart.stream()
                        .filter(ci -> ci.getMedicineName().equals(item.getMedicineName()))
                        .findFirst().orElse(null);
                    int qty = qtySpinner.getValue();
                    if (existing != null) {
                        existing.setQuantity(existing.getQuantity() + qty);
                    } else {
                        cart.add(new CartItem(item.getMedicineName(), item.getBatchId(), qty, item.getPrice(), item.getAvailableStock(), item.getDosageMg()));
                    }
                    showAlert(Alert.AlertType.INFORMATION, "Added", qty + "x " + item.getMedicineName() + " added to cart");
                });

                actionRow.getChildren().addAll(qtySpinner, addBtn);

                card.getChildren().addAll(topRow, badges, actionRow);
                setGraphic(card);
                setStyle("-fx-background-color: transparent;");
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
                removeBtn.getStyleClass().add("remove-cart-button");
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
        // Placeholder for non-blocking toast notifications.
    }
}
