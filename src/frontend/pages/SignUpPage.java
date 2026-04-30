package frontend.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import backend.database.DatabaseManager;
import backend.repositories.MySQLUserRepository;
import backend.services.AuthenticationService;
import backend.exceptions.AuthenticationException;
import java.sql.Connection;

// SignUpPage class for user registration, allowing new users to create an account. It includes form fields for username, email, and password, along with validation and error handling. The page also provides a link to the LoginPage for existing users.
public class SignUpPage {
    public static Scene getScene(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f8fbff, #edf4fc);");

        Label phaseBadge = new Label("●  Phase 1.0: Authentication & Dashboard Prototype");
        phaseBadge.setStyle(
                "-fx-background-color: #d9f7e4;" +
                "-fx-text-fill: #177245;" +
                "-fx-padding: 8 16;" +
                "-fx-background-radius: 999;" +
                "-fx-font-weight: 600;" +
                "-fx-font-size: 12;"
        );
        HBox topBox = new HBox(phaseBadge);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(18, 0, 8, 0));
        root.setTop(topBox);

        HBox card = new HBox();
        card.setMaxWidth(980);
        card.setPrefHeight(560);
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #1a1a1a;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 18;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.16), 20, 0.2, 0, 5);"
        );

        VBox leftPanel = new VBox(14);
        leftPanel.setPadding(new Insets(28));
        leftPanel.setAlignment(Pos.CENTER_LEFT);
        leftPanel.setPrefWidth(520);
        leftPanel.setStyle("-fx-background-color: linear-gradient(to bottom right, #e7f6ff, #cfefff); -fx-background-radius: 16 0 0 16;");

        Label brandTitle = new Label("PharmaSync");
        brandTitle.setFont(Font.font("Arial", FontWeight.BOLD, 62));
        brandTitle.setTextFill(Color.web("#094c98"));

        Label brandSub = new Label("Create your staff account.");
        brandSub.setFont(Font.font("Arial", 30));
        brandSub.setTextFill(Color.web("#2f4155"));

        Label imageHint = new Label("Dummy image area\nReplace with your custom image");
        imageHint.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.68);" +
                "-fx-border-color: rgba(9, 76, 152, 0.35);" +
                "-fx-border-style: dashed;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-text-fill: #23507d;" +
                "-fx-padding: 20 24;" +
                "-fx-font-size: 14;"
        );
        leftPanel.getChildren().addAll(brandTitle, brandSub, imageHint);

        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(34, 42, 34, 42));
        rightPanel.setAlignment(Pos.TOP_LEFT);
        rightPanel.setPrefWidth(460);

        Label pageTitle = new Label("Pharmacist Sign Up");
        pageTitle.setFont(Font.font("Arial", FontWeight.BOLD, 38));
        pageTitle.setTextFill(Color.web("#0f3159"));

        Label userName = new Label("Username");
        userName.setTextFill(Color.web("#4e6177"));
        TextField userTextField = new TextField();
        userTextField.setPromptText("Enter username");
        userTextField.setPrefHeight(44);
        userTextField.setStyle("-fx-font-size: 14; -fx-background-radius: 8; -fx-border-radius: 8;");

        Label emailLabel = new Label("Email");
        emailLabel.setTextFill(Color.web("#4e6177"));
        TextField emailTextField = new TextField();
        emailTextField.setPromptText("Enter email");
        emailTextField.setPrefHeight(44);
        emailTextField.setStyle("-fx-font-size: 14; -fx-background-radius: 8; -fx-border-radius: 8;");

        Label pw = new Label("Password");
        pw.setTextFill(Color.web("#4e6177"));
        PasswordField pwBox = new PasswordField();
        pwBox.setPromptText("Enter password");
        pwBox.setPrefHeight(44);
        pwBox.setStyle("-fx-font-size: 14; -fx-background-radius: 8; -fx-border-radius: 8;");

        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setVisible(false);
        messageLabel.managedProperty().bind(messageLabel.visibleProperty());
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setStyle("-fx-background-radius: 8; -fx-padding: 10 12; -fx-font-weight: 600;");

        // Sign Up button
        Button btnSignUp = new Button("Create Account");
        btnSignUp.setMaxWidth(Double.MAX_VALUE); // To make it stretch
        btnSignUp.setPrefHeight(46);
        btnSignUp.setStyle(
                "-fx-background-color: #0b63c7;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15;" +
                "-fx-font-weight: 700;" +
                "-fx-background-radius: 8;"
        );
        
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
                
                messageLabel.setTextFill(Color.web("#166534"));
                messageLabel.setStyle(
                        "-fx-background-color: #dcfce7;" +
                        "-fx-border-color: #86efac;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 12;" +
                        "-fx-font-weight: 600;"
                );
                messageLabel.setText("Sign Up Successful! Redirecting...");
                messageLabel.setVisible(true);
                
                // Redirect to Login Page automatically after short delay or immediately
                primaryStage.setScene(LoginPage.getScene(primaryStage));
                
            } catch (AuthenticationException authException) {
                messageLabel.setTextFill(Color.web("#991b1b"));
                messageLabel.setStyle(
                        "-fx-background-color: #fee2e2;" +
                        "-fx-border-color: #fca5a5;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 12;" +
                        "-fx-font-weight: 600;"
                );
                messageLabel.setText(authException.getMessage());
                messageLabel.setVisible(true);
            } catch (Exception ex) {
                messageLabel.setTextFill(Color.web("#991b1b"));
                messageLabel.setStyle(
                        "-fx-background-color: #fee2e2;" +
                        "-fx-border-color: #fca5a5;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 12;" +
                        "-fx-font-weight: 600;"
                );
                messageLabel.setText("Database Connection Error.");
                messageLabel.setVisible(true);
                ex.printStackTrace();
            }
        });

        // Already have an account link
        Hyperlink loginLink = new Hyperlink("Already have an account? Sign In");
        loginLink.setOnAction(e -> {
            primaryStage.setScene(LoginPage.getScene(primaryStage));
        });
        loginLink.setStyle("-fx-text-fill: #0a5eb7; -fx-font-weight: 600;");
        HBox linkBox = new HBox(10);
        linkBox.setAlignment(Pos.CENTER_RIGHT);
        linkBox.getChildren().addAll(loginLink);
        HBox.setHgrow(linkBox, Priority.ALWAYS);

        VBox formArea = new VBox(10);
        formArea.getChildren().addAll(
                pageTitle,
                userName, userTextField,
                emailLabel, emailTextField,
                pw, pwBox,
                messageLabel,
                btnSignUp,
                linkBox
        );
        VBox.setMargin(pageTitle, new Insets(0, 0, 10, 0));
        VBox.setMargin(btnSignUp, new Insets(8, 0, 6, 0));

        rightPanel.getChildren().add(formArea);
        card.getChildren().addAll(leftPanel, rightPanel);

        StackPane center = new StackPane(card);
        center.setPadding(new Insets(10, 28, 26, 28));
        root.setCenter(center);

        return new Scene(root, 1200, 760);
    }
}