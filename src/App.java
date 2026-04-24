import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;

public class App extends Application {
    
    public void start(Stage primartStage) {
        primartStage.setTitle("Anonymous");
        Label label = new Label();
        label.setText("Hello, World!");
        Scene scene = new Scene(label, 300, 250);
        primartStage.setScene(scene);
        primartStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}