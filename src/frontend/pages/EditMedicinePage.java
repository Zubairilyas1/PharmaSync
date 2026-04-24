package frontend.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

public class EditMedicinePage {
    
    public static void showEditMedicineDialog(Stage ownerStage, InventoryList.Medicine medicine, Callback<InventoryList.Medicine, Void> onEdit) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(ownerStage);
        dialogStage.setTitle("Edit Medicine");
        dialogStage.setWidth(500);
        dialogStage.setHeight(500);
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        // Title
        Label titleLabel = new Label("Edit Medicine Details");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        // Medicine Name
        HBox nameBox = new HBox(10);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        Label nameLabel = new Label("Medicine Name:");
        nameLabel.setPrefWidth(120);
        TextField nameField = new TextField();
        nameField.setText(medicine.getName());
        nameField.setPrefWidth(300);
        nameBox.getChildren().addAll(nameLabel, nameField);
        
        // Batch ID
        HBox batchBox = new HBox(10);
        batchBox.setAlignment(Pos.CENTER_LEFT);
        Label batchLabel = new Label("Batch ID:");
        batchLabel.setPrefWidth(120);
        TextField batchField = new TextField();
        batchField.setText(medicine.getBatchId());
        batchField.setPrefWidth(300);
        batchBox.getChildren().addAll(batchLabel, batchField);
        
        // Expiry Date
        HBox expiryBox = new HBox(10);
        expiryBox.setAlignment(Pos.CENTER_LEFT);
        Label expiryLabel = new Label("Expiry Date:");
        expiryLabel.setPrefWidth(120);
        DatePicker expiryDatePicker = new DatePicker();
        expiryDatePicker.setValue(medicine.getExpiryDate());
        expiryDatePicker.setPrefWidth(300);
        expiryBox.getChildren().addAll(expiryLabel, expiryDatePicker);
        
        // Quantity
        HBox quantityBox = new HBox(10);
        quantityBox.setAlignment(Pos.CENTER_LEFT);
        Label quantityLabel = new Label("Quantity:");
        quantityLabel.setPrefWidth(120);
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 1000, medicine.getQuantity());
        quantitySpinner.setPrefWidth(300);
        quantityBox.getChildren().addAll(quantityLabel, quantitySpinner);
        
        // Status
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        Label statusLabel = new Label("Status:");
        statusLabel.setPrefWidth(120);
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Active", "Inactive", "Discontinued");
        statusCombo.setValue(medicine.getStatus());
        statusCombo.setPrefWidth(300);
        statusBox.getChildren().addAll(statusLabel, statusCombo);
        
        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button updateButton = new Button("Update");
        updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 10; -fx-font-size: 12;");
        updateButton.setOnAction(e -> {
            if (nameField.getText().isEmpty() || batchField.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields!");
                return;
            }
            
            // Update the medicine object
            medicine.setQuantity(quantitySpinner.getValue());
            medicine.setStatus(statusCombo.getValue());
            
            onEdit.call(medicine);
            dialogStage.close();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Medicine updated successfully!");
        });
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 10; -fx-font-size: 12;");
        cancelButton.setOnAction(e -> dialogStage.close());
        
        buttonBox.getChildren().addAll(updateButton, cancelButton);
        
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
