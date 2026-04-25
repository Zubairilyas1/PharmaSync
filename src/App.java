import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import frontend.pages.Dashboard;

public class App extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("PharmaSync - Pharmacy Management System");
        
        // Get the scene from our Dashboard class
        Scene scene = Dashboard.createDashboardScene(primaryStage);
        
        primaryStage.setScene(scene);
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}