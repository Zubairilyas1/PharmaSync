package frontend.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import frontend.ui.UiTheme;

public class ForgotPasswordPage {

    public static Scene getScene(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle(UiTheme.appBackground());

        VBox card = new VBox(12);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(24));
        card.setMaxWidth(560);
        card.setStyle(UiTheme.card());

        Label sceneTitle = new Label("Reset Password");
        sceneTitle.setStyle(UiTheme.headingL());

        Label helpText = new Label("Enter your account email and we will send a reset link.");
        helpText.setStyle(UiTheme.bodyText());

        Label emailLabel = new Label("Email");
        emailLabel.setStyle(UiTheme.bodyText());
        TextField emailTextField = new TextField();
        emailTextField.setPromptText("Enter your email address");
        UiTheme.styleFormInput(emailTextField);

        Button btnReset = new Button("Send Reset Link");
        btnReset.setMaxWidth(Double.MAX_VALUE);
        btnReset.setStyle(UiTheme.primaryButton());
        UiTheme.installPrimaryHover(btnReset);

        btnReset.setOnAction(e -> {
            // Placeholder flow until backend reset endpoint is connected.
            showNotice("Reset link requested", "If this email exists, a reset link will be sent.");
        });

        Hyperlink loginLink = new Hyperlink("Back to Login");
        loginLink.setStyle("-fx-text-fill: " + UiTheme.COLOR_PRIMARY + "; -fx-font-weight: 700;");
        loginLink.setOnAction(e -> {
            primaryStage.setScene(LoginPage.getScene(primaryStage));
        });

        HBox linkBox = new HBox(10);
        linkBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(linkBox, Priority.ALWAYS);
        linkBox.getChildren().add(loginLink);

        card.getChildren().addAll(sceneTitle, helpText, emailLabel, emailTextField, btnReset, linkBox);

        StackPane center = new StackPane(card);
        center.setPadding(new Insets(30));
        root.setCenter(center);

        return new Scene(root, 1920, 1080);
    }

    private static void showNotice(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}