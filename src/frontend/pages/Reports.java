package frontend.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import frontend.ui.UiTheme;

import java.time.LocalDate;
import java.util.*;

public class Reports {
    public static Scene createReportsScene(Stage stage) {
        return createReportsSceneInternal(stage);
    }

    private static Scene createReportsSceneInternal(Stage stage) {
        VBox mainContainer = new VBox();
        mainContainer.setStyle(UiTheme.appBackground());

        // Header with back button
        HBox header = createHeader(stage);
        mainContainer.getChildren().add(header);

        // Scrollable content area
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);

        VBox contentArea = new VBox(20);
        contentArea.setPadding(UiTheme.pagePadding());
        contentArea.setStyle(UiTheme.appBackground());

        // Summary Cards Section
        HBox summaryCards = createSummaryCards();
        contentArea.getChildren().add(summaryCards);

        // Charts Section
        VBox chartsSection = createChartsSection();
        contentArea.getChildren().add(chartsSection);

        // Expiry Table Section
        VBox expirySection = createExpirySection();
        contentArea.getChildren().add(expirySection);

        // Export Options Section
        HBox exportSection = createExportSection(stage);
        contentArea.getChildren().add(exportSection);

        scrollPane.setContent(contentArea);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        mainContainer.getChildren().add(scrollPane);

        return new Scene(mainContainer, 1200, 800);
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

        Label headerTitle = new Label("Reports & Analytics");
        headerTitle.setStyle("-fx-font-size: 18; -fx-font-weight: 800; -fx-text-fill: #111827;");

        header.getChildren().addAll(backButton, headerTitle);
        HBox.setHgrow(headerTitle, Priority.ALWAYS);

        return header;
    }

    private static HBox createSummaryCards() {
        HBox cardsContainer = new HBox(15);
        cardsContainer.setStyle("-fx-padding: 10;");

        // Total Sales (Monthly)
        VBox totalSalesCard = createSummaryCard("Total Sales (Monthly)", "$45,230.50", "#4CAF50", "Sales");
        cardsContainer.getChildren().add(totalSalesCard);

        // Profit Margin
        VBox profitMarginCard = createSummaryCard("Profit Margin", "32.5%", "#2196F3", "Margin");
        cardsContainer.getChildren().add(profitMarginCard);

        // Items Expired
        VBox itemsExpiredCard = createSummaryCard("Items Expired", "12", "#FF9800", "Expiry");
        cardsContainer.getChildren().add(itemsExpiredCard);

        // Active Prescriptions
        VBox activePrescriptionsCard = createSummaryCard("Active Prescriptions", "487", "#9C27B0", "Scripts");
        cardsContainer.getChildren().add(activePrescriptionsCard);

        // Make all cards equal size
        cardsContainer.getChildren().forEach(card -> HBox.setHgrow(card, Priority.ALWAYS));

        return cardsContainer;
    }

    private static VBox createSummaryCard(String title, String value, String color, String icon) {
        VBox card = new VBox(10);
        card.setStyle(UiTheme.card() + " -fx-padding: 20;");
        card.setAlignment(Pos.TOP_LEFT);

        HBox iconBox = new HBox();
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24;");
        iconBox.getChildren().add(iconLabel);

        Label titleLabel = new Label(title);
        titleLabel.setStyle(UiTheme.bodyText());

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        card.getChildren().addAll(iconBox, titleLabel, valueLabel);

        return card;
    }

    private static VBox createChartsSection() {
        VBox chartsSection = new VBox(15);
        chartsSection.setStyle(UiTheme.card() + " -fx-padding: 20;");

        Label chartsTitle = new Label("Monthly Sales Trend");
        chartsTitle.setStyle(UiTheme.headingM());

        // Create Bar Chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Month");
        yAxis.setLabel("Sales ($)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Monthly Sales Trend");
        barChart.setAnimated(false);
        barChart.setPrefHeight(350);

        // Sample data
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Sales");
        series.getData().addAll(
                new XYChart.Data<>("Jan", 28000),
                new XYChart.Data<>("Feb", 32000),
                new XYChart.Data<>("Mar", 35000),
                new XYChart.Data<>("Apr", 38000),
                new XYChart.Data<>("May", 42000),
                new XYChart.Data<>("Jun", 45230)
        );

        barChart.getData().add(series);
        barChart.setStyle("-fx-font-size: 11;");

        chartsSection.getChildren().addAll(chartsTitle, barChart);
        VBox.setVgrow(barChart, Priority.ALWAYS);

        return chartsSection;
    }

    private static VBox createExpirySection() {
        VBox expirySection = new VBox(15);
        expirySection.setStyle(UiTheme.card() + " -fx-padding: 20;");

        Label expiryTitle = new Label("Upcoming Expirations");
        expiryTitle.setStyle(UiTheme.headingM());

        // Create TableView for expiry items
        TableView<ExpiryItem> expiryTable = new TableView<>();

        TableColumn<ExpiryItem, String> medicineCol = new TableColumn<>("Medicine Name");
        medicineCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().medicineName));
        medicineCol.setPrefWidth(200);

        TableColumn<ExpiryItem, String> batchCol = new TableColumn<>("Batch ID");
        batchCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().batchId));
        batchCol.setPrefWidth(120);

        TableColumn<ExpiryItem, String> expiryDateCol = new TableColumn<>("Expiry Date");
        expiryDateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().expiryDate.toString()));
        expiryDateCol.setPrefWidth(120);

        TableColumn<ExpiryItem, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().quantity));
        quantityCol.setPrefWidth(100);

        TableColumn<ExpiryItem, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().status));
        statusCol.setPrefWidth(120);
        statusCol.setCellFactory(col -> new TableCell<ExpiryItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Expired")) {
                        setStyle("-fx-text-fill: " + UiTheme.COLOR_DANGER_TEXT + "; -fx-font-weight: bold;");
                    } else if (item.equals("Expiring Soon")) {
                        setStyle("-fx-text-fill: " + UiTheme.COLOR_WARNING_TEXT + "; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #4CAF50;");
                    }
                }
            }
        });

        expiryTable.getColumns().addAll(medicineCol, batchCol, expiryDateCol, quantityCol, statusCol);
        expiryTable.setPrefHeight(250);

        // Add sample data
        expiryTable.getItems().addAll(
                new ExpiryItem("Aspirin", "BATCH001", LocalDate.of(2026, 5, 15), 50, "Expiring Soon"),
                new ExpiryItem("Ibuprofen", "BATCH002", LocalDate.of(2026, 6, 20), 30, "Expiring Soon"),
                new ExpiryItem("Paracetamol", "BATCH003", LocalDate.of(2026, 4, 10), 100, "Expired"),
                new ExpiryItem("Amoxicillin", "BATCH004", LocalDate.of(2026, 7, 5), 25, "Good"),
                new ExpiryItem("Metformin", "BATCH005", LocalDate.of(2026, 5, 25), 60, "Expiring Soon")
        );

        expirySection.getChildren().addAll(expiryTitle, expiryTable);
        VBox.setVgrow(expiryTable, Priority.ALWAYS);

        return expirySection;
    }

    private static HBox createExportSection(Stage stage) {
        HBox exportSection = new HBox(15);
        exportSection.setAlignment(Pos.CENTER_LEFT);
        exportSection.setStyle("-fx-padding: 10;");

        Button pdfButton = new Button("Download PDF Report");
        pdfButton.setStyle(UiTheme.secondaryButton() + " -fx-padding: 10 20;");
        pdfButton.setPrefWidth(180);
        pdfButton.setOnAction(e -> showExportDialog(stage, "PDF"));

        Button excelButton = new Button("Export to Excel");
        excelButton.setStyle(UiTheme.primaryButton() + " -fx-padding: 10 20;");
        UiTheme.installPrimaryHover(excelButton);
        excelButton.setPrefWidth(180);
        excelButton.setOnAction(e -> showExportDialog(stage, "Excel"));

        exportSection.getChildren().addAll(pdfButton, excelButton);

        return exportSection;
    }

    private static void showExportDialog(Stage stage, String format) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export Report");
        alert.setHeaderText("Export to " + format);
        alert.setContentText("Report exported successfully as " + format + " format!\n\nFile saved to your downloads folder.");
        alert.showAndWait();
    }

    // Inner class for expiry items
    public static class ExpiryItem {
        public String medicineName;
        public String batchId;
        public LocalDate expiryDate;
        public int quantity;
        public String status;

        public ExpiryItem(String medicineName, String batchId, LocalDate expiryDate, int quantity, String status) {
            this.medicineName = medicineName;
            this.batchId = batchId;
            this.expiryDate = expiryDate;
            this.quantity = quantity;
            this.status = status;
        }
    }
}
