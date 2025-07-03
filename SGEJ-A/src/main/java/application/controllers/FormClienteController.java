package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

public class FormClienteController {

    @FXML private Button btn_Guardar, btn_Cancelar;
    @FXML private TextField txtf_Nombres, txtf_Apellidos, txtf_NumeroIdentificacion, txtF_Direccion, txtf_Correo, txtf_AdicionalCampo, txtf_NumeroTelefono;
    @FXML private ComboBox<String> cbx_TipoCliente, cbx_TipoIdentificacion, cbx_Estado;
    @FXML private DatePicker dt_FechaIngreso;
    @FXML private Text txt_TituloForm;

    private Runnable onGuardar, onCancelar;

    public void setOnGuardar(Runnable handler) {
        this.onGuardar = handler;
    }

    public void setOnCancelar(Runnable handler) {
        this.onCancelar = handler;
    }

    @FXML
    private void initialize() {
        cbx_Estado.getItems().addAll("Activo", "Inactivo");
        cbx_TipoIdentificacion.getItems().addAll("Cédula", "RUC", "Pasaporte");
        cbx_TipoCliente.getItems().addAll("Natural", "Juridica");
        btn_Guardar.setOnAction(e -> {
            if (onGuardar != null) onGuardar.run();
        });

        btn_Cancelar.setOnAction(e -> {
            if (onCancelar != null) onCancelar.run();
        });
    }

    public void cargarCliente(ModuloClienteController.ClienteDemo cliente) {
        txtf_Nombres.setText(cliente.nombres());
        txtf_Apellidos.setText(cliente.apellidos());
        txtf_NumeroIdentificacion.setText(cliente.numeroIdentificacion());
        // txtF_Direccion, txtf_AdicionalCampo, dt_FechaIngreso: set if your ClienteDemo has these fields
        txtf_NumeroTelefono.setText(cliente.telefono());
        txtf_Correo.setText(cliente.correo());
        // cbx_TipoCliente, cbx_TipoIdentificacion, cbx_Estado: set if your ClienteDemo has these fields
        // Example: cbx_TipoIdentificacion.setValue(cliente.tipoIdentificacion());
        // Example: cbx_Estado.setValue(cliente.estado());
    }

    public void setModo(String modo) {
        boolean editable = "EDITAR".equals(modo);
        boolean ver = "VER".equals(modo);

        txt_TituloForm.setText(editable ? "Editar Cliente" : "Registar nuevo Cliente");
        txtf_Nombres.setEditable(editable);
        txtf_Apellidos.setEditable(editable);
        txtf_NumeroIdentificacion.setEditable(false);
        cbx_TipoIdentificacion.setDisable(true);
        txtf_NumeroTelefono.setEditable(editable);
        txtf_Correo.setEditable(editable);
        cbx_Estado.setDisable(true);
        txtF_Direccion.setEditable(editable);
        txtf_AdicionalCampo.setEditable(editable);
        cbx_TipoCliente.setDisable(!editable);
        dt_FechaIngreso.setDisable(!editable);

        if (ver) {
            txt_TituloForm.setText("Ver Cliente");
            txtf_Nombres.setEditable(false);
            txtf_Apellidos.setEditable(false);
            txtf_NumeroIdentificacion.setEditable(false);
            cbx_TipoIdentificacion.setDisable(true);
            txtf_NumeroTelefono.setEditable(false);
            txtf_Correo.setEditable(false);
            cbx_Estado.setDisable(true);
            txtF_Direccion.setEditable(false);
            txtf_AdicionalCampo.setEditable(false);
            cbx_TipoCliente.setDisable(true);
            dt_FechaIngreso.setDisable(true);
            btn_Guardar.setDisable(true);
        } else {
            btn_Guardar.setDisable(false);
        }
    }
}