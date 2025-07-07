package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class FormCambioClaveController {

    @FXML private PasswordField txt_Actual, txt_Nueva, txt_Confirmar;
    @FXML private Label lbl_Error;
    @FXML private Button btn_Guardar, btn_Cancelar;

    private Runnable onCancelar;

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

        btn_Guardar.setOnAction(e -> cambiarClave());
        btn_Cancelar.setOnAction(e -> {
            if (onCancelar != null) onCancelar.run();
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
