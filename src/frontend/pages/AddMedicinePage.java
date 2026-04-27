package frontend.pages;

import backend.exceptions.InventoryException;
import backend.services.InventoryService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AddMedicinePage {

    public static void showAddMedicineDialog(Stage ownerStage, InventoryService inventoryService, Callback<backend.models.Medicine, Void> onAdd) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(ownerStage);
        dialogStage.setTitle("Add New Medicine");
        dialogStage.setWidth(500);
        dialogStage.setHeight(500);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Title
        Label titleLabel = new Label("Add New Medicine");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Medicine Name
        HBox nameBox = new HBox(10);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        Label nameLabel = new Label("Medicine Name:");
        nameLabel.setPrefWidth(120);
        TextField nameField = new TextField();
        nameField.setPromptText("e.g., Aspirin");
        nameField.setPrefWidth(300);
        nameBox.getChildren().addAll(nameLabel, nameField);

        // Batch ID
        HBox batchBox = new HBox(10);
        batchBox.setAlignment(Pos.CENTER_LEFT);
        Label batchLabel = new Label("Batch ID:");
        batchLabel.setPrefWidth(120);
        TextField batchField = new TextField();
        batchField.setPromptText("e.g., BATCH001");
        batchField.setPrefWidth(300);
        batchBox.getChildren().addAll(batchLabel, batchField);

        // Expiry Date
        HBox expiryBox = new HBox(10);
        expiryBox.setAlignment(Pos.CENTER_LEFT);
        Label expiryLabel = new Label("Expiry Date:");
        expiryLabel.setPrefWidth(120);
        DatePicker expiryDatePicker = new DatePicker();
        expiryDatePicker.setValue(LocalDate.now().plusDays(365));
        expiryDatePicker.setPrefWidth(300);
        expiryBox.getChildren().addAll(expiryLabel, expiryDatePicker);

        // Quantity
        HBox quantityBox = new HBox(10);
        quantityBox.setAlignment(Pos.CENTER_LEFT);
        Label quantityLabel = new Label("Quantity:");
        quantityLabel.setPrefWidth(120);
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 1000, 50);
        quantitySpinner.setPrefWidth(300);
        quantityBox.getChildren().addAll(quantityLabel, quantitySpinner);

        // Status
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        Label statusLabel = new Label("Status:");
        statusLabel.setPrefWidth(120);
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Active", "Inactive", "Discontinued");
        statusCombo.setValue("Active");
        statusCombo.setPrefWidth(300);
        statusBox.getChildren().addAll(statusLabel, statusCombo);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10; -fx-font-size: 12;");
        saveButton.setOnAction(e -> {
            if (nameField.getText().isEmpty() || batchField.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields!");
                return;
            }

            try {
                backend.models.Medicine medicine = new backend.models.Medicine(0,
                    nameField.getText(),
                    nameField.getText(), // placeholder for description
                    0.0, // placeholder for price
                    quantitySpinner.getValue(),
                    batchField.getText(),
                    expiryDatePicker.getValue(),
                    statusCombo.getValue()
                );
                
                inventoryService.addMedicine(medicine);

                onAdd.call(medicine);
                dialogStage.close();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Medicine added successfully!");
            } catch (InventoryException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add medicine: " + ex.getMessage());
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + ex.getMessage());
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 10; -fx-font-size: 12;");
        cancelButton.setOnAction(e -> dialogStage.close());

        buttonBox.getChildren().addAll(saveButton, cancelButton);

        // Add all to root
        root.getChildren().addAll(
            titleLabel,
            new Separator(),
            nameBox,
            batchBox,
            expiryBox,
            quantityBox,
            statusBox,
            new Separator(),
            buttonBox
        );

        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FunctionalInterface
    public interface Callback<T, R> {
        R call(T param);
    }
}