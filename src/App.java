import frontend.pages.LoginPage;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class App extends Application {
    
    public void start(Stage primaryStage) {
        primaryStage.setTitle("PharmaSync");
        
        // Load the initial Scene (LoginPage)
        Scene initialScene = LoginPage.getScene(primaryStage);
        
        primaryStage.setScene(initialScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}