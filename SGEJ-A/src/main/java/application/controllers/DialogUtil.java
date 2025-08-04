package application.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DialogUtil {

    public static Optional<ButtonType> mostrarDialogo(String titulo, String mensaje, String tipo,
            List<ButtonType> botones) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogUtil.class.getResource("/views/dialogo_personalizado.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT); // ✅ necesario para esquinas reales
            scene.getStylesheets().add(DialogUtil.class.getResource("/styles/app.css").toExternalForm());

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.TRANSPARENT); // ✅ permite mostrar esquinas redondeadas reales
            dialogStage.setScene(scene);

            DialogoPersonalizadoController controller = loader.getController();
            controller.inicializar(titulo, mensaje, tipo, dialogStage, botones);

            dialogStage.showAndWait();

            return Optional.ofNullable((ButtonType) dialogStage.getUserData());
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Muestra un mensaje en un cuadro de diálogo
     * 
     * @param titulo  Título del mensaje
     * @param mensaje Contenido del mensaje
     * @param tipo    Tipo de alerta (INFORMATION, WARNING, ERROR, CONFIRMATION)
     */
    public static void mostrarMensaje(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un mensaje de error en un cuadro de diálogo
     * 
     * @param titulo  Título del mensaje de error
     * @param mensaje Contenido del mensaje de error
     */
    public static void mostrarMensajeError(String titulo, String mensaje) {
        mostrarMensaje(titulo, mensaje, Alert.AlertType.ERROR);
    }

    /**
     * Muestra un mensaje de información en un cuadro de diálogo
     * 
     * @param titulo  Título del mensaje de información
     * @param mensaje Contenido del mensaje de información
     */
    public static void mostrarMensajeInfo(String titulo, String mensaje) {
        mostrarMensaje(titulo, mensaje, Alert.AlertType.INFORMATION);
    }

    /**
     * Método alternativo para mostrar un mensaje de información
     * para mantener compatibilidad con código existente
     * 
     * @param titulo  Título del mensaje de información
     * @param mensaje Contenido del mensaje de información
     */
    public static void mostrarMensajeInformacion(String titulo, String mensaje) {
        mostrarMensajeInfo(titulo, mensaje);
    }

    /**
     * Muestra un mensaje de advertencia en un cuadro de diálogo
     * 
     * @param titulo  Título del mensaje de advertencia
     * @param mensaje Contenido del mensaje de advertencia
     */
    public static void mostrarMensajeAdvertencia(String titulo, String mensaje) {
        mostrarMensaje(titulo, mensaje, Alert.AlertType.WARNING);
    }

    /**
     * Muestra un mensaje de confirmación en un cuadro de diálogo
     * 
     * @param titulo  Título del mensaje de confirmación
     * @param mensaje Contenido del mensaje de confirmación
     * @return true si el usuario confirmó, false si canceló
     */
    public static boolean mostrarConfirmacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
