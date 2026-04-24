package frontend.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginPage {

    public static Scene getScene(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Title
        Text sceneTitle = new Text("PharmaSync - Login");
        sceneTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        grid.add(sceneTitle, 0, 0, 2, 1);

        // Username
        Label userName = new Label("Username:");
        grid.add(userName, 0, 1);
        TextField userTextField = new TextField();
        userTextField.setPromptText("Enter username");
        grid.add(userTextField, 1, 1);

        // Password
        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);
        PasswordField pwBox = new PasswordField();
        pwBox.setPromptText("Enter password");
        grid.add(pwBox, 1, 2);

        // Login button
        Button btnLogin = new Button("Login");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        grid.add(btnLogin, 1, 3);
        
        btnLogin.setOnAction(e -> {
            System.out.println("Login Attempted:");
            System.out.println("Username: " + userTextField.getText());
        });

        // Forgot password & Sign up links
        Hyperlink forgotPwLink = new Hyperlink("Forgot Password?");
        forgotPwLink.setOnAction(e -> {
            primaryStage.setScene(ForgotPasswordPage.getScene(primaryStage));
        });

        Hyperlink signUpLink = new Hyperlink("Sign Up");
        signUpLink.setOnAction(e -> {
            primaryStage.setScene(SignUpPage.getScene(primaryStage));
        });

        HBox linkBox = new HBox(10);
        linkBox.setAlignment(Pos.CENTER_RIGHT);
        linkBox.getChildren().addAll(forgotPwLink, signUpLink);
        grid.add(linkBox, 1, 4);

        return new Scene(grid, 800, 600);
    }
}