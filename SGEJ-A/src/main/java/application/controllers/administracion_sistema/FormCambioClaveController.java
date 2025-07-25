package application.controllers.administracion_sistema;

import application.controllers.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Optional;

public class FormCambioClaveController {

    @FXML private PasswordField txt_Actual, txt_Nueva, txt_Confirmar;
    @FXML private Label lbl_Error;
    @FXML private Button btn_Guardar, btn_Cancelar;

    private Runnable onCancelar, onGuardar;

    public void setOnGuardar(Runnable handler) {
        this.onGuardar = handler;
    }
    public void setOnCancelar(Runnable onCancelar) {
        this.onCancelar = onCancelar;
    }

    private String claveActual = "claveEjemplo123"; // Simulación, reemplazar por lógica real

    private String modo = "CAMBIO";

    public void setModo(String modo) {
        this.modo = modo;
    }

    @FXML
    private void initialize() {
        lbl_Error.setText("");

        btn_Guardar.setOnAction(e -> {
            lbl_Error.setText(""); // limpiar errores previos

            if (txt_Actual.getText().isEmpty() ||
                    txt_Nueva.getText().isEmpty() ||
                    txt_Confirmar.getText().isEmpty()) {

                lbl_Error.setText("Por favor, complete todos los campos.");
                return;
            }

            if (!txt_Nueva.getText().equals(txt_Confirmar.getText())) {
                lbl_Error.setText("La nueva contraseña y la confirmación no coinciden.");
                return;
            }

            Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                    "Confirmación",
                    "¿Está seguro que desea cambiar su contraseña?",
                    "confirm",
                    List.of(ButtonType.YES, ButtonType.NO)
            );

            if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
                if (onGuardar != null) onGuardar.run();
            }
        });

        btn_Cancelar.setOnAction(e -> {
            Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                    "Confirmación",
                    "¿Está seguro que desea cancelar el cambio de contraseña?",
                    "confirm",
                    List.of(ButtonType.YES, ButtonType.NO)
            );

            if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
                if (onCancelar != null) onCancelar.run();
            }
        });

    }

    private void cambiarClave() {
        String actual = txt_Actual.getText();
        String nueva = txt_Nueva.getText();
        String confirmar = txt_Confirmar.getText();

        if (modo.equals("CAMBIO") && !actual.equals(claveActual)) {
            lbl_Error.setText("La contraseña actual no es válida.");
            return;
        }

        if (!nueva.matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
            lbl_Error.setText("La nueva contraseña debe tener al menos 8 caracteres, incluyendo letras y números.");
            return;
        }

        if (!nueva.equals(confirmar)) {
            lbl_Error.setText("Las contraseñas no coinciden.");
            return;
        }

        lbl_Error.setText("Contraseña actualizada correctamente.");
        // Aquí guardarías la nueva contraseña
    }
}
