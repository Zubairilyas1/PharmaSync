import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import frontend.pages.LoginPage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("PharmaSync - Login");
        primaryStage.setScene(LoginPage.getScene(primaryStage));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}