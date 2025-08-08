package application.controllers;

import application.service.AutenticacionService;
import application.service.ParametroService;
import application.model.Usuario;
import application.util.LoginFailureManager;
import application.util.SessionManager;
import application.dao.UsuarioDAO;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
    private Label errorLabel;

    private double xOffset = 0;
    private double yOffset = 0;

    /**
     * Método utilitario para mostrar mensajes de error
     * 
     * @param mensaje El mensaje de error a mostrar
     */
    private void mostrarError(String mensaje) {
        if (errorLabel != null) {
            errorLabel.setText(mensaje);
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            errorLabel.setVisible(true);

            // Asegurarse de que el label sea visible
            errorLabel.requestFocus();

            // Debug
            System.out.println("Mostrando mensaje de error: " + mensaje);
        } else {
            System.err.println("ERROR: No se puede mostrar el mensaje de error porque el label es null");
        }
    }

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

        // Configurar y ocultar el error al inicio
        if (errorLabel != null) {
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            errorLabel.setVisible(false);
            System.out.println("Label de error inicializado correctamente");
        } else {
            System.err.println("ERROR: El label de error no fue encontrado en el FXML");
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validar que se hayan ingresado credenciales
        if (username.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor ingrese nombre de usuario y contraseña.");
            return;
        }

        // Verificar si el usuario existe y no está bloqueado antes de intentar
        // autenticar
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = null;
        try {
            usuario = usuarioDAO.obtenerUsuarioPorNombreUsuario(username);

            if (usuario == null) {
                mostrarError("Usuario no encontrado. Por favor verifique sus credenciales.");
                return;
            }
        } catch (Exception e) {
            mostrarError("Error al verificar el usuario. Intente nuevamente.");
            e.printStackTrace();
            return;
        }

        if (usuario.getEstadoUsuario() == Usuario.EstadoUsuario.INACTIVO) {
            mostrarError("Usuario bloqueado. Contacte al administrador del sistema.");
            return;
        }

        // Intentar autenticar con las credenciales
        if (autenticacionService.autenticar(username, password)) {
            // Reiniciar el contador de intentos fallidos
            LoginFailureManager.getInstance().reiniciarIntentosFallidos(username);

            String rol = autenticacionService.getRolUsuarioActual();
            abrirPanelPrincipal(rol);
        } else {
            // Registrar intento fallido
            int intentosFallidos = LoginFailureManager.getInstance().registrarIntentoFallido(username);

            // Obtener el máximo de intentos permitidos del parámetro
            int maxIntentosFallidos = ParametroService.getInstance().getValorEntero("max_intento_fallidos", 3);

            // Si supera el límite, bloquear la cuenta
            if (intentosFallidos >= maxIntentosFallidos) {
                // Bloquear usuario
                usuario.setEstadoUsuario(Usuario.EstadoUsuario.INACTIVO);
                usuarioDAO.actualizarUsuario(usuario);

                // Mostrar mensaje de bloqueo en el label
                mostrarError("Usuario bloqueado por múltiples intentos fallidos. Contacte al administrador.");

                // Mostrar diálogo de alerta informando sobre el bloqueo
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Cuenta Bloqueada");
                alert.setHeaderText("Usuario bloqueado por seguridad");
                alert.setContentText("Su cuenta ha sido bloqueada después de " + maxIntentosFallidos +
                        " intentos fallidos de inicio de sesión.\n\n" +
                        "Por favor contacte al administrador del sistema para desbloquear su cuenta.");

                // Mostrar el diálogo y esperar a que el usuario lo cierre
                alert.showAndWait();

            } else {
                // Mostrar error si la autenticación falla pero aún no se bloquea
                int intentosRestantes = maxIntentosFallidos - intentosFallidos;
                mostrarError("El usuario o la contraseña son incorrectas. Intentos restantes: " + intentosRestantes);
            }
        }
    }

    private void abrirPanelPrincipal(String userType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main_panel.fxml"));
            Parent root = loader.load();

            // Obtener el tipo de usuario del usuario actual autenticado
            String tipoUsuarioStr = userType;
            // Si el usuario está autenticado, obtenemos su tipo
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

            // Configurar el control de tiempo de sesión
            int tiempoSesion = ParametroService.getInstance().getValorEntero("tiempo_sesion", 30); // 30 minutos por
                                                                                                   // defecto

            // Configuramos la acción de cierre de sesión
            Runnable logoutAction = () -> {
                autenticacionService.cerrarSesion();

                // Cerrar ventana principal y mostrar login
                stage.close();

                try {
                    FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
                    Parent loginRoot = loginLoader.load();

                    // Crear escena y aplicar estilos
                    Scene loginScene = new Scene(loginRoot);
                    loginScene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());

                    Stage loginStage = new Stage();
                    loginStage.setTitle("Sistema de Gestión de Estudios Jurídicos");
                    loginStage.initStyle(StageStyle.UNDECORATED);
                    loginStage.setScene(loginScene);
                    loginStage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }; // Iniciar el control de tiempo de sesión
            SessionManager.getInstance().startSessionTimer(tiempoSesion, logoutAction, stage);

            // Cerrar la ventana de login
            Stage current = (Stage) usernameField.getScene().getWindow();
            current.close();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al iniciar la aplicación: " + e.getMessage());
        }
    }
}
