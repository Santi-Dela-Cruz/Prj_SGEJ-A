package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class FormUsuarioController {

    @FXML private Button btn_Guardar, btn_Cancelar;
    @FXML private TextField txtf_NombresCompletos, txtf_NombreUsuario, txtf_NumeroIdentificacion, txtf_Direccion, txtf_Correo, txtf_Telefono;
    @FXML private PasswordField txtf_Contrasena, txtf_ConfirmarContrasena;
    @FXML private ComboBox<String> cbx_Rol, cbx_TipoIdentificacion, cbx_Estado;
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
        cbx_Rol.getItems().addAll("Administrador", "Usuario", "Invitado");

        btn_Guardar.setOnAction(e -> {
            if (onGuardar != null) onGuardar.run();
        });
        btn_Cancelar.setOnAction(e -> {
            if (onCancelar != null) onCancelar.run();
        });
    }

    public void cargarUsuario(ModuloUsuarioController.UsuarioDemo usuario) {
        txtf_NombresCompletos.setText(usuario.nombresCompletos());
        txtf_NombreUsuario.setText(usuario.nombreUsuario());
        txtf_NumeroIdentificacion.setText(usuario.numeroIdentificacion());
        txtf_Direccion.setText(usuario.direccion());
        dt_FechaIngreso.setValue(usuario.fechaIngreso());
        txtf_Telefono.setText(usuario.telefono());
        txtf_Correo.setText(usuario.correo());
        cbx_Rol.setValue(usuario.rol());
        cbx_TipoIdentificacion.setValue(usuario.tipoIdentificacion());
        cbx_Estado.setValue(usuario.estado());
        // Password fields are not loaded for security reasons
    }

    public void setModo(String modo) {
        boolean esEditar = "EDITAR".equalsIgnoreCase(modo);
        boolean esVer = "VER".equalsIgnoreCase(modo);

        if (esEditar) {
            txt_TituloForm.setText("Editar Usuario");
        } else if (esVer) {
            txt_TituloForm.setText("Ver Usuario");
        } else {
            txt_TituloForm.setText("Registrar nuevo Usuario");
        }

        boolean editable = !esVer;
        txtf_NombresCompletos.setEditable(editable);
        txtf_NombreUsuario.setEditable(editable);
        txtf_NumeroIdentificacion.setEditable(!esEditar && !esVer);
        cbx_TipoIdentificacion.setDisable(esEditar || esVer);
        txtf_Telefono.setEditable(editable);
        txtf_Correo.setEditable(editable);
        cbx_Estado.setDisable(esVer);
        txtf_Direccion.setEditable(editable);
        cbx_Rol.setDisable(esVer);
        dt_FechaIngreso.setDisable(esVer);
        txtf_Contrasena.setEditable(editable);
        txtf_ConfirmarContrasena.setEditable(editable);
        btn_Guardar.setDisable(esVer);
    }
}