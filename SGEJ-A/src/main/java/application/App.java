package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {

    private double xOffset = 0;
    private double yOffset = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Usamos StageStyle.UNDECORATED para quitar la barra de título predeterminada de Windows
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(true); // La ventana es redimensionable

        // Cargar la vista FXML
        Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
        Scene scene = new Scene(root);

        // Añadir los estilos CSS
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());

        // Establecer la escena
        primaryStage.setScene(scene);

        // Obtener las dimensiones de la pantalla para ajustarlo sin cubrir la barra de tareas
        Screen screen = Screen.getPrimary();
        double width = screen.getVisualBounds().getWidth();
        double height = screen.getVisualBounds().getHeight();

        // Establecer el tamaño de la ventana para que ocupe todo el espacio disponible
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);

        primaryStage.show();

        // Hacer que la barra personalizada sea arrastrable
        root.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged((MouseEvent event) -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });
    }
}
