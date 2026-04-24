import frontend.pages.*;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class App extends Application {
    
    private Stage primaryStage;
    
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("PharmaSync - Sales & Inventory Management");
        
        // Load the Dashboard/Navigation scene
        Scene initialScene = Dashboard.createDashboardScene(primaryStage, this);
        
        primaryStage.setScene(initialScene);
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.show();
    }
    
    public void showScene(String pageName) {
        Scene scene = null;
        
        switch(pageName) {
            case "inventory":
                scene = InventoryList.createInventoryListScene(primaryStage);
                break;
            case "sales":
                scene = SalesTerminal.createSalesTerminalScene(primaryStage);
                break;
            case "dashboard":
                scene = Dashboard.createDashboardScene(primaryStage, this);
                break;
            default:
                scene = Dashboard.createDashboardScene(primaryStage, this);
        }
        
        if (scene != null) {
            primaryStage.setScene(scene);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}