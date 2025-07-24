package application.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
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
}
