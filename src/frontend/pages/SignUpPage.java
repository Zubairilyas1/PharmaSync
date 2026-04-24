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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import backend.database.DatabaseManager;
import backend.repositories.MySQLUserRepository;
import backend.services.AuthenticationService;
import backend.exceptions.AuthenticationException;
import java.sql.Connection;

// SignUpPage class for user registration, allowing new users to create an account. It includes form fields for username, email, and password, along with validation and error handling. The page also provides a link to the LoginPage for existing users.
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
        
        // Error / Success Label
        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.RED);
        grid.add(messageLabel, 1, 6);

        // Sign Up button
        Button btnSignUp = new Button("Sign Up");
        btnSignUp.setMaxWidth(Double.MAX_VALUE); // To make it stretch
        
        // Action event for the button using MySQL backend
        btnSignUp.setOnAction(e -> {
            String username = userTextField.getText();
            String email = emailTextField.getText();
            String password = pwBox.getText();
            
            try {
                Connection conn = DatabaseManager.getConnection();
                MySQLUserRepository repo = new MySQLUserRepository(conn);
                AuthenticationService authService = new AuthenticationService(repo);
                
                authService.signup(username, email, password);
                
                messageLabel.setTextFill(Color.GREEN);
                messageLabel.setText("Sign Up Successful! Redirecting...");
                
                // Redirect to Login Page automatically after short delay or immediately
                primaryStage.setScene(LoginPage.getScene(primaryStage));
                
            } catch (AuthenticationException authException) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText(authException.getMessage());
            } catch (Exception ex) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("Database Connection Error.");
                ex.printStackTrace();
            }
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