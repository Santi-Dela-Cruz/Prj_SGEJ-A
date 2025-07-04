package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.function.Consumer;

public class FormBitacoraController {

    @FXML private Label lbl_TituloFormulario;
    // Bitácora fields
    @FXML private Label lbl_Expediente, lbl_Responsable, lbl_FechaCreacion;
    @FXML private TextField txtf_Expediente, txtf_Responsable;
    @FXML private DatePicker dp_FechaCreacion;
    // Entrada fields
    @FXML private Label lbl_Fecha, lbl_Usuario, lbl_Accion, lbl_Descripcion;
    @FXML private DatePicker dp_Fecha;
    @FXML private TextField txtf_Usuario, txtf_Accion;
    @FXML private TextArea txta_Descripcion;
    // Buttons
    @FXML private Button btn_Guardar, btn_Cancelar;

    private Consumer<Void> onGuardar;
    private Consumer<Void> onCancelar;

    public void setModoFormulario(String modo) {
        boolean esBitacora = "BITACORA".equalsIgnoreCase(modo);
        boolean esEntrada = "ENTRADA".equalsIgnoreCase(modo);

        if (lbl_TituloFormulario != null)
            lbl_TituloFormulario.setText(esBitacora ? "Registrar nueva Bitácora" : "Añadir entrada de Bitácora");

        // Only set visibility for fields that exist in the current FXML
        if (lbl_Expediente != null) lbl_Expediente.setVisible(true);
        if (txtf_Expediente != null) txtf_Expediente.setVisible(true);
        if (lbl_Responsable != null) lbl_Responsable.setVisible(true);
        if (txtf_Responsable != null) txtf_Responsable.setVisible(true);
        if (lbl_FechaCreacion != null) lbl_FechaCreacion.setVisible(true);
        if (dp_FechaCreacion != null) dp_FechaCreacion.setVisible(true);

        if (lbl_Fecha != null) lbl_Fecha.setVisible(true);
        if (dp_Fecha != null) dp_Fecha.setVisible(true);
        if (lbl_Usuario != null) lbl_Usuario.setVisible(true);
        if (txtf_Usuario != null) txtf_Usuario.setVisible(true);
        if (lbl_Accion != null) lbl_Accion.setVisible(true);
        if (txtf_Accion != null) txtf_Accion.setVisible(true);
        if (lbl_Descripcion != null) lbl_Descripcion.setVisible(true);
        if (txta_Descripcion != null) txta_Descripcion.setVisible(true);
    }

    public void setOnGuardar(Consumer<Void> callback) { this.onGuardar = callback; }
    public void setOnCancelar(Consumer<Void> callback) { this.onCancelar = callback; }

    @FXML
    private void initialize() {
        if (btn_Guardar != null)
            btn_Guardar.setOnAction(e -> { if (onGuardar != null) onGuardar.accept(null); });
        if (btn_Cancelar != null)
            btn_Cancelar.setOnAction(e -> { if (onCancelar != null) onCancelar.accept(null); });
    }
}