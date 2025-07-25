package application.controllers.administracion_sistema;

import application.controllers.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Optional;

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
    cbx_Estado.getItems().addAll("ACTIVO", "INACTIVO");
    cbx_TipoIdentificacion.getItems().addAll("CEDULA", "RUC", "PASAPORTE"); // <-- Enum exacto
    cbx_Rol.getItems().addAll("Administrador", "Usuario", "Invitado");
    
    btn_Guardar.setOnAction(e -> {
    String nombres = txtf_NombresCompletos.getText().trim();
    String usuario = txtf_NombreUsuario.getText().trim();
    String identificacion = txtf_NumeroIdentificacion.getText().trim();

    // LOG para depuración
    System.out.println("Guardando usuario: " +
        nombres + ", " +
        usuario + ", " +
        identificacion + ", " +
        cbx_TipoIdentificacion.getValue() + ", " +
        txtf_Telefono.getText() + ", " +
        txtf_Correo.getText() + ", " +
        cbx_Estado.getValue() + ", " +
        txtf_Direccion.getText() + ", " +
        dt_FechaIngreso.getValue() + ", " +
        cbx_Rol.getValue() + ", " +
        txtf_Contrasena.getText());

    if (nombres.isEmpty() ||
        usuario.isEmpty() ||
        identificacion.isEmpty() ||
        cbx_TipoIdentificacion.getValue() == null ||
        cbx_Rol.getValue() == null ||
        cbx_Estado.getValue() == null ||
        dt_FechaIngreso.getValue() == null ||
        txtf_Contrasena.getText().isEmpty() ||
        txtf_ConfirmarContrasena.getText().isEmpty()) {

        DialogUtil.mostrarDialogo(
            "Campos requeridos",
            "Por favor, complete los campos obligatorios:\n" +
            " - Nombres Completos (actual: '" + nombres + "')\n" +
            " - Nombre de Usuario (actual: '" + usuario + "')\n" +
            " - Número de Identificación (actual: '" + identificacion + "')\n" +
            " - Tipo de Identificación\n" +
            " - Rol\n" +
            " - Estado\n" +
            " - Fecha de Ingreso\n" +
            " - Contraseña y Confirmación",
            "warning",
            List.of(ButtonType.OK)
        );
        return;
    }



    if (!txtf_Contrasena.getText().equals(txtf_ConfirmarContrasena.getText())) {
        DialogUtil.mostrarDialogo(
            "Contraseña incorrecta",
            "La contraseña y la confirmación no coinciden.",
            "error",
            List.of(ButtonType.OK)
        );
        return;
    }

    Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
        "Confirmación",
        "¿Está seguro que desea guardar este usuario?",
        "confirm",
        List.of(ButtonType.YES, ButtonType.NO)
    );

    if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
        try {
            application.model.Usuario usuarioObj = new application.model.Usuario(
                nombres,
                usuario,
                identificacion,
                application.model.Usuario.TipoIdentificacion.valueOf(cbx_TipoIdentificacion.getValue()),
                txtf_Telefono.getText(),
                txtf_Correo.getText(),
                application.model.Usuario.Estado.valueOf(cbx_Estado.getValue()),
                txtf_Direccion.getText(),
                dt_FechaIngreso.getValue(),
                application.model.Usuario.TipoUsuario.NATURAL,
                cbx_Rol.getValue(),
                txtf_Contrasena.getText()
            );

            application.dao.UsuarioDAO usuarioDAO = new application.dao.UsuarioDAO();
            boolean exito = usuarioDAO.insertarUsuario(usuarioObj);

            if (exito) {
                DialogUtil.mostrarDialogo("Éxito", "Usuario guardado correctamente.", "info", List.of(ButtonType.OK));
                if (onGuardar != null) onGuardar.run();
            } else {
                DialogUtil.mostrarDialogo("Error", "No se pudo guardar el usuario. Verifique que el nombre de usuario y número de identificación no estén repetidos.", "error", List.of(ButtonType.OK));
            }
        } catch (Exception ex) {
            DialogUtil.mostrarDialogo("Error", "Error al guardar usuario: " + ex.getMessage(), "error", List.of(ButtonType.OK));
            ex.printStackTrace();
        }
    }
});

    btn_Cancelar.setOnAction(e -> {
        Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
            "Confirmación",
            "¿Está seguro que desea cancelar el formulario?\nSe perderán los cambios no guardados.",
            "confirm",
            List.of(ButtonType.YES, ButtonType.NO)
        );

        if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
            if (onCancelar != null) onCancelar.run();
        }
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