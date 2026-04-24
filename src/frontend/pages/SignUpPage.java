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
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SignUpPage {

    public static Scene getScene(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Account Title
        Text sceneTitle = new Text("PharmaSync - Sign Up");
        sceneTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        grid.add(sceneTitle, 0, 0, 2, 1);

        // Name
        Label userName = new Label("Username:");
        grid.add(userName, 0, 1);
        TextField userTextField = new TextField();
        userTextField.setPromptText("Enter username");
        grid.add(userTextField, 1, 1);

        // Email
        Label emailLabel = new Label("Email:");
        grid.add(emailLabel, 0, 2);
        TextField emailTextField = new TextField();
        emailTextField.setPromptText("Enter email");
        grid.add(emailTextField, 1, 2);

        // Password
        Label pw = new Label("Password:");
        grid.add(pw, 0, 3);
        PasswordField pwBox = new PasswordField();
        pwBox.setPromptText("Enter password");
        grid.add(pwBox, 1, 3);

        // Sign Up button
        Button btnSignUp = new Button("Sign Up");
        btnSignUp.setMaxWidth(Double.MAX_VALUE); // To make it stretch
        
        // Optional: Action event for the button
        btnSignUp.setOnAction(e -> {
            System.out.println("Sign Up Attempted:");
            System.out.println("Username: " + userTextField.getText());
            System.out.println("Email: " + emailTextField.getText());
            // Redirect to Login Page automatically after sign up
            primaryStage.setScene(LoginPage.getScene(primaryStage));
        });

        // Add button to the layout
        grid.add(btnSignUp, 1, 4);

        // Already have an account link
        Hyperlink loginLink = new Hyperlink("Already have an account? Login");
        loginLink.setOnAction(e -> {
            primaryStage.setScene(LoginPage.getScene(primaryStage));
        });

        HBox linkBox = new HBox(10);
        linkBox.setAlignment(Pos.CENTER_RIGHT);
        linkBox.getChildren().addAll(loginLink);
        grid.add(linkBox, 1, 5);

        // Wrap layout in a scene
        return new Scene(grid, 800, 600);
    }
}