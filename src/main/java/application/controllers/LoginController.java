package application.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginController {

    @FXML
    private HBox titleBar;

    @FXML
    private Button closeButton, minimizeButton;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> userTypeBox;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private void initialize() {
        titleBar.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        titleBar.setOnMouseDragged((MouseEvent event) -> {
            Stage stage = (Stage) titleBar.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        closeButton.setOnAction(_ -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });

        minimizeButton.setOnAction(_ -> {
            Stage stage = (Stage) minimizeButton.getScene().getWindow();
            stage.setIconified(true);
        });

        userTypeBox.getItems().addAll("Administrador", "Asistente Legal", "Contador");
        userTypeBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleLogin() {
        String userType = userTypeBox.getValue();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main_panel.fxml"));
            Parent root = loader.load();

            // Pass userType to MainController
            MainController mainController = loader.getController();
            mainController.setUserType(userType);

            // Crear la escena para el panel principal
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());

            // Crear y configurar la ventana principal
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);

            // Maximizar la ventana sin cubrir la barra de tareas
            Screen screen = Screen.getPrimary();
            double width = screen.getVisualBounds().getWidth();
            double height = screen.getVisualBounds().getHeight();
            stage.setWidth(width);
            stage.setHeight(height);

            stage.show();

            // Cerrar la ventana de login
            Stage current = (Stage) usernameField.getScene().getWindow();
            current.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
