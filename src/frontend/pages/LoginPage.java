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
import javafx.stage.Stage;

import backend.database.DatabaseManager;
import backend.repositories.MySQLUserRepository;
import backend.services.AuthenticationService;
import backend.exceptions.AuthenticationException;
import java.sql.Connection;
import frontend.pages.Dashboard;
import frontend.ui.UiTheme;

public class LoginPage {

    public static Scene getScene(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle(UiTheme.appBackground());

        Label phaseBadge = new Label("●  Phase 1.0: Authentication & Dashboard Prototype");
        phaseBadge.setStyle(
                "-fx-background-color: #DCFCE7;" +
                "-fx-text-fill: #166534;" +
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
        card.setPrefHeight(520);
        card.setStyle(UiTheme.card());

        VBox leftPanel = new VBox(14);
        leftPanel.setPadding(new Insets(28));
        leftPanel.setAlignment(Pos.CENTER_LEFT);
        leftPanel.setPrefWidth(520);
        leftPanel.setStyle("-fx-background-color: linear-gradient(to bottom right, #E7F2FF, #D9E9FF); -fx-background-radius: 16 0 0 16;");

        Label brandTitle = new Label("PharmaSync");
        brandTitle.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 54; -fx-font-weight: 800;");
        brandTitle.setTextFill(Color.web("#094c98"));

        Label brandSub = new Label("Intelligence in every dose.");
        brandSub.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 28; -fx-font-weight: 500;");
        brandSub.setTextFill(Color.web("#2f4155"));

        Label imageHint = new Label("Brand visual placeholder\nReplace with approved product illustration");
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
        rightPanel.setPadding(new Insets(38, 42, 34, 42));
        rightPanel.setAlignment(Pos.TOP_LEFT);
        rightPanel.setPrefWidth(460);

        Label loginTitle = new Label("Pharmacist Sign In");
        loginTitle.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 34; -fx-font-weight: 800; -fx-text-fill: #0F3159;");

        Label userName = new Label("Employee ID (e.g. BRA_24-XXXX)");
        userName.setStyle(UiTheme.bodyText());
        TextField userTextField = new TextField();
        userTextField.setPromptText("BRA_24-3100");
        UiTheme.styleFormInput(userTextField);

        Label pw = new Label("Password (Masked)");
        pw.setStyle(UiTheme.bodyText());
        PasswordField pwBox = new PasswordField();
        pwBox.setPromptText("[********]");
        UiTheme.styleFormInput(pwBox);

        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setVisible(false);
        messageLabel.managedProperty().bind(messageLabel.visibleProperty());
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setStyle("-fx-background-radius: 8; -fx-padding: 10 12; -fx-font-weight: 600;");

        Button btnLogin = new Button("Sign In");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setPrefHeight(46);
        btnLogin.setStyle(UiTheme.primaryButton());
        UiTheme.installPrimaryHover(btnLogin);
        
        btnLogin.setOnAction(e -> {
            String identifier = userTextField.getText();
            String password = pwBox.getText();
            
            try {
                Connection conn = DatabaseManager.getConnection();
                MySQLUserRepository repo = new MySQLUserRepository(conn);
                AuthenticationService authService = new AuthenticationService(repo);
                
                authService.login(identifier, password);
                
                messageLabel.setTextFill(Color.web(UiTheme.COLOR_SUCCESS_TEXT));
                messageLabel.setStyle(UiTheme.successMessage());
                messageLabel.setText("Login Successful!");
                messageLabel.setVisible(true);
                
                // Transition to the main application dashboard
                primaryStage.setScene(Dashboard.createDashboardScene(primaryStage));
                
            } catch (AuthenticationException authException) {
                messageLabel.setTextFill(Color.web(UiTheme.COLOR_DANGER_TEXT));
                messageLabel.setStyle(UiTheme.errorMessage());
                messageLabel.setText(authException.getMessage());
                messageLabel.setVisible(true);
            } catch (Exception ex) {
                messageLabel.setTextFill(Color.web(UiTheme.COLOR_DANGER_TEXT));
                messageLabel.setStyle(UiTheme.errorMessage());
                messageLabel.setText("Database Connection Error.");
                messageLabel.setVisible(true);
            }
        });

        // Forgot password & Sign up links
        Hyperlink forgotPwLink = new Hyperlink("Forgot Password?");
        forgotPwLink.setOnAction(e -> {
            primaryStage.setScene(ForgotPasswordPage.getScene(primaryStage));
        });
        forgotPwLink.setStyle("-fx-text-fill: " + UiTheme.COLOR_PRIMARY + "; -fx-font-weight: 600;");

        Hyperlink signUpLink = new Hyperlink("Create Account");
        signUpLink.setOnAction(e -> {
            primaryStage.setScene(SignUpPage.getScene(primaryStage));
        });
        signUpLink.setStyle("-fx-text-fill: " + UiTheme.COLOR_PRIMARY + "; -fx-font-weight: 600;");

        HBox linkBox = new HBox(10);
        linkBox.setAlignment(Pos.CENTER_RIGHT);
        linkBox.getChildren().addAll(forgotPwLink, signUpLink);
        HBox.setHgrow(linkBox, Priority.ALWAYS);

        VBox formArea = new VBox(10);
        formArea.getChildren().addAll(
                loginTitle,
                userName, userTextField,
                pw, pwBox,
                messageLabel,
                btnLogin,
                linkBox
        );
        VBox.setMargin(loginTitle, new Insets(0, 0, 10, 0));
        VBox.setMargin(btnLogin, new Insets(8, 0, 6, 0));

        rightPanel.getChildren().add(formArea);
        card.getChildren().addAll(leftPanel, rightPanel);

        StackPane center = new StackPane(card);
        center.setPadding(new Insets(10, 28, 26, 28));
        root.setCenter(center);

        return new Scene(root, 1200, 760);
    }
}