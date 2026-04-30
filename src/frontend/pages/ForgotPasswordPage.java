package frontend.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ForgotPasswordPage {

    public static Scene getScene(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setStyle("-fx-background-color: #F4F7FB;");

        // Title
        Text sceneTitle = new Text("PharmaSync - Reset Password");
        sceneTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        grid.add(sceneTitle, 0, 0, 2, 1);

        // Email
        Label emailLabel = new Label("Email:");
        grid.add(emailLabel, 0, 1);
        TextField emailTextField = new TextField();
        emailTextField.setPromptText("Enter your email address");
        grid.add(emailTextField, 1, 1);

        // Reset button
        Button btnReset = new Button("Send Reset Link");
        btnReset.setMaxWidth(Double.MAX_VALUE);
        btnReset.setStyle("-fx-background-color: #0056B3; -fx-text-fill: white; -fx-font-weight: 700; -fx-background-radius: 10; -fx-padding: 10 14;");
        grid.add(btnReset, 1, 2);

        btnReset.setOnAction(e -> {
            System.out.println("Password reset requested for:");
            System.out.println("Email: " + emailTextField.getText());
        });

        // Back to Login Page
        Hyperlink loginLink = new Hyperlink("Back to Login");
        loginLink.setStyle("-fx-text-fill: #0056B3; -fx-font-weight: 700;");
        loginLink.setOnAction(e -> {
            primaryStage.setScene(LoginPage.getScene(primaryStage));
        });

        HBox linkBox = new HBox(10);
        linkBox.setAlignment(Pos.CENTER_RIGHT);
        linkBox.getChildren().addAll(loginLink);
        grid.add(linkBox, 1, 3);

        return new Scene(grid, 800, 600);
    }
}