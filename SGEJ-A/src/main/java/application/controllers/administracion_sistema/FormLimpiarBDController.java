package application.controllers.administracion_sistema;

import application.controllers.DialogUtil;
import application.util.LimpiarBD;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para el formulario de limpieza de base de datos
 */
public class FormLimpiarBDController {

    @FXML
    private CheckBox chk_Confirmar;
    @FXML
    private Button btn_Limpiar, btn_Cancelar;

    private Runnable onCancelar, onLimpiar;

    @FXML
    private void initialize() {
        // Habilitar botón solo cuando el checkbox está marcado
        chk_Confirmar.selectedProperty().addListener((obs, oldVal, newVal) -> {
            btn_Limpiar.setDisable(!newVal);
        });

        // Configurar botones
        btn_Limpiar.setOnAction(e -> ejecutarLimpieza());
        btn_Cancelar.setOnAction(e -> {
            if (onCancelar != null) {
                onCancelar.run();
            } else {
                cerrarVentana();
            }
        });
    }

    /**
     * Establece el callback a ejecutar cuando se cancela la operación
     */
    public void setOnCancelar(Runnable handler) {
        this.onCancelar = handler;
    }

    /**
     * Establece el callback a ejecutar cuando se completa la limpieza
     */
    public void setOnLimpiar(Runnable handler) {
        this.onLimpiar = handler;
    }

    /**
     * Ejecuta la limpieza de la base de datos
     */
    private void ejecutarLimpieza() {
        // Confirmar nuevamente con el usuario
        Optional<ButtonType> confirmacion = DialogUtil.mostrarDialogo(
                "Confirmación Final",
                "¿Está COMPLETAMENTE SEGURO de querer eliminar todos los datos excepto usuarios?\n" +
                        "Esta acción NO puede deshacerse.",
                "confirm",
                List.of(ButtonType.YES, ButtonType.NO));

        if (confirmacion.orElse(ButtonType.NO) == ButtonType.YES) {
            // Ejecutar limpieza
            boolean exito = LimpiarBD.limpiarBaseDatosExceptoUsuarios();

            if (exito && onLimpiar != null) {
                onLimpiar.run();
            }

            // Cerrar ventana
            cerrarVentana();
        }
    }

    /**
     * Cierra la ventana actual
     */
    private void cerrarVentana() {
        Stage stage = (Stage) btn_Cancelar.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}
