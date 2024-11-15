import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Appinitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("view/LoginPageForm.fxml"));

        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login Page");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("./image/only hands.png")));
        primaryStage.centerOnScreen();
        primaryStage.show();
        primaryStage.setResizable(false);

    }
}
