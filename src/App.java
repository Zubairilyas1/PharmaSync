import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import frontend.pages.Dashboard;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("PharmaSync - Dashboard");
        primaryStage.setScene(Dashboard.createDashboardScene(primaryStage));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}