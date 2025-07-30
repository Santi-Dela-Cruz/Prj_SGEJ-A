package application;

import application.database.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Clase principal de la aplicación JavaFX
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Inicializar la base de datos antes de mostrar la interfaz
        DatabaseInitializer.initialize();
        
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/views/login.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        
        stage.setTitle("Sistema de Gestión de Estudios Jurídicos");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
