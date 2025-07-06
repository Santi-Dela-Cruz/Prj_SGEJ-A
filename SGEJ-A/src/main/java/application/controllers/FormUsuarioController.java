package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class FormUsuarioController {

    @FXML private Button btn_Guardar, btn_Cancelar;
    @FXML private TextField txtf_Nombres, txtf_Apellidos, txtf_NumeroIdentificacion, txtf_Direccion, txtf_Correo, txtf_Adicional, txtf_Telefono;
    @FXML private ComboBox<String> cbx_TipoUsuario, cbx_TipoIdentificacion, cbx_Estado;
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
        cbx_TipoUsuario.getItems().addAll("Natural", "Jurídica");

        btn_Guardar.setOnAction(e -> {
            if (onGuardar != null) onGuardar.run();
        });
        btn_Cancelar.setOnAction(e -> {
            if (onCancelar != null) onCancelar.run();
        });
    }

    public void cargarUsuario(ModuloUsuarioController.UsuarioDemo usuario) {
        txtf_Nombres.setText(usuario.nombres());
        txtf_Apellidos.setText(usuario.apellidos());
        txtf_NumeroIdentificacion.setText(usuario.numeroIdentificacion());
        txtf_Direccion.setText(usuario.direccion());
        txtf_Adicional.setText(usuario.adicional());
        dt_FechaIngreso.setValue(usuario.fechaIngreso());
        txtf_Telefono.setText(usuario.telefono());
        txtf_Correo.setText(usuario.correo());
        cbx_TipoUsuario.setValue(usuario.tipoUsuario());
        cbx_TipoIdentificacion.setValue(usuario.tipoIdentificacion());
        cbx_Estado.setValue(usuario.estado());
    }

    public void setModo(String modo) {
        boolean esEditar = "EDITAR".equalsIgnoreCase(modo);
        boolean esVer = "VER".equalsIgnoreCase(modo);
        boolean esRegistrar = !esEditar && !esVer;

        if (esEditar) {
            txt_TituloForm.setText("Editar Usuario");
        } else if (esVer) {
            txt_TituloForm.setText("Ver Usuario");
        } else {
            txt_TituloForm.setText("Registrar nuevo Usuario");
        }

        if (esVer) {
            txtf_Nombres.setEditable(false);
            txtf_Apellidos.setEditable(false);
            txtf_NumeroIdentificacion.setEditable(false);
            cbx_TipoIdentificacion.setDisable(true);
            txtf_Telefono.setEditable(false);
            txtf_Correo.setEditable(false);
            cbx_Estado.setDisable(true);
            txtf_Direccion.setEditable(false);
            txtf_Adicional.setEditable(false);
            cbx_TipoUsuario.setDisable(true);
            dt_FechaIngreso.setDisable(true);
            btn_Guardar.setDisable(true);
        } else if (esEditar) {
            txtf_Nombres.setEditable(true);
            txtf_Apellidos.setEditable(true);
            txtf_NumeroIdentificacion.setEditable(false);
            cbx_TipoIdentificacion.setDisable(true);
            txtf_Telefono.setEditable(true);
            txtf_Correo.setEditable(true);
            cbx_Estado.setDisable(true);
            txtf_Direccion.setEditable(true);
            txtf_Adicional.setEditable(true);
            cbx_TipoUsuario.setDisable(false);
            dt_FechaIngreso.setDisable(false);
            btn_Guardar.setDisable(false);
        } else { // REGISTRAR
            txtf_Nombres.setEditable(true);
            txtf_Apellidos.setEditable(true);
            txtf_NumeroIdentificacion.setEditable(true);
            cbx_TipoIdentificacion.setDisable(false);
            txtf_Telefono.setEditable(true);
            txtf_Correo.setEditable(true);
            cbx_Estado.setDisable(false);
            txtf_Direccion.setEditable(true);
            txtf_Adicional.setEditable(true);
            cbx_TipoUsuario.setDisable(false);
            dt_FechaIngreso.setDisable(false);
            btn_Guardar.setDisable(false);
        }
    }
}