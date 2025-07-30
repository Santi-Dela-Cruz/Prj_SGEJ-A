package application.controllers;

import application.service.AutenticacionService;
import application.model.Usuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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

    @FXML
    private Label errorLabel;

    private double xOffset = 0;
    private double yOffset = 0;

    private final AutenticacionService autenticacionService = AutenticacionService.getInstancia();

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

        userTypeBox.getItems().addAll("Administrador", "Asistente Legal", "Contador", "Abogado");
        userTypeBox.getSelectionModel().selectFirst();

        // Ocultar el error al inicio
        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validar que se hayan ingresado credenciales
        if (username.isEmpty() || password.isEmpty()) {
            if (errorLabel != null) {
                errorLabel.setText("Por favor ingrese nombre de usuario y contraseña.");
                errorLabel.setVisible(true);
            }
            return;
        }

        // Intentar autenticar con las credenciales
        if (autenticacionService.autenticar(username, password)) {
            String rol = autenticacionService.getRolUsuarioActual();
            abrirPanelPrincipal(rol);
        } else {
            // Mostrar error si la autenticación falla
            if (errorLabel != null) {
                errorLabel.setText("Credenciales incorrectas. Por favor intente nuevamente.");
                errorLabel.setVisible(true);
            }
        }
    }

    private void abrirPanelPrincipal(String userType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main_panel.fxml"));
            Parent root = loader.load();

            // Convertir enum a string esperado por MainController
            String tipoUsuarioStr = userType;
            // Si el parámetro es un enum, conviértelo
            if (autenticacionService.getUsuarioActual() != null) {
                tipoUsuarioStr = MainController
                        .tipoUsuarioToString(autenticacionService.getUsuarioActual().getTipoUsuario());
            }

            MainController mainController = loader.getController();
            mainController.setUserType(tipoUsuarioStr);

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
            if (errorLabel != null) {
                errorLabel.setText("Error al iniciar la aplicación: " + e.getMessage());
                errorLabel.setVisible(true);
            }
        }
    }
}
