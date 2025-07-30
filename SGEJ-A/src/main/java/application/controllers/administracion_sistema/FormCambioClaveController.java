package application.controllers.administracion_sistema;

import application.controllers.DialogUtil;
import application.dao.UsuarioDAO;
import application.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Optional;

public class FormCambioClaveController {

    @FXML
    private PasswordField txt_Actual, txt_Nueva, txt_Confirmar;
    @FXML
    private Label lbl_Error;
    @FXML
    private Button btn_Guardar, btn_Cancelar;
    @FXML
    private Label lbl_Usuario;

    private Runnable onCancelar, onGuardar;
    private Usuario usuario;
    private String modo = "CAMBIO"; // "CAMBIO" o "RESET"

    // DAO para operaciones con la base de datos
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void setOnGuardar(Runnable handler) {
        this.onGuardar = handler;
    }

    public void setOnCancelar(Runnable onCancelar) {
        this.onCancelar = onCancelar;
    }

    /**
     * Establece el usuario cuya clave se va a cambiar
     * 
     * @param usuario Usuario
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;

        // Actualizar la interfaz
        if (lbl_Usuario != null && usuario != null) {
            lbl_Usuario.setText(usuario.getNombreUsuario());
        }
    }

    /**
     * Establece el modo del formulario (CAMBIO o RESET)
     * 
     * @param modo Modo
     */
    public void setModo(String modo) {
        this.modo = modo;

        // En modo RESET no se solicita la clave actual
        if (txt_Actual != null && txt_Actual.getParent() != null) {
            txt_Actual.setVisible(modo.equals("CAMBIO"));
            txt_Actual.setManaged(modo.equals("CAMBIO"));

            // También la etiqueta asociada
            if (txt_Actual.getParent().getChildrenUnmodifiable().size() > 0) {
                txt_Actual.getParent().getChildrenUnmodifiable().get(0).setVisible(modo.equals("CAMBIO"));
                txt_Actual.getParent().getChildrenUnmodifiable().get(0).setManaged(modo.equals("CAMBIO"));
            }
        }
    }

    @FXML
    private void initialize() {
        lbl_Error.setText("");

        btn_Guardar.setOnAction(e -> guardarCambios());
        btn_Cancelar.setOnAction(e -> {
            if (onCancelar != null) {
                onCancelar.run();
            }
        });
    }

    /**
     * Valida los campos del formulario
     * 
     * @return true si los campos son válidos, false en caso contrario
     */
    private boolean validarFormulario() {
        lbl_Error.setText(""); // limpiar errores previos

        // Verificar que el usuario esté establecido
        if (usuario == null) {
            lbl_Error.setText("No se ha especificado un usuario para cambiar la clave");
            return false;
        }

        // En modo CAMBIO, verificar la clave actual
        if (modo.equals("CAMBIO")) {
            // Verificar que se haya ingresado la clave actual
            if (txt_Actual.getText().trim().isEmpty()) {
                lbl_Error.setText("Debe ingresar la contraseña actual");
                return false;
            }

            // Verificar que la clave actual sea correcta
            try {
                if (!usuarioDAO.verificarClave(usuario.getNombreUsuario(), txt_Actual.getText())) {
                    lbl_Error.setText("La contraseña actual es incorrecta");
                    return false;
                }
            } catch (Exception e) {
                System.err.println("Error al verificar clave: " + e.getMessage());
                lbl_Error.setText("Error al verificar la contraseña");
                return false;
            }
        }

        // Verificar que se haya ingresado la nueva clave
        if (txt_Nueva.getText().trim().isEmpty() || txt_Confirmar.getText().trim().isEmpty()) {
            lbl_Error.setText("Debe ingresar y confirmar la nueva contraseña");
            return false;
        }

        // Verificar que las claves coincidan
        if (!txt_Nueva.getText().equals(txt_Confirmar.getText())) {
            lbl_Error.setText("Las contraseñas no coinciden");
            return false;
        }

        // Verificar longitud mínima
        if (txt_Nueva.getText().length() < 6) {
            lbl_Error.setText("La contraseña debe tener al menos 6 caracteres");
            return false;
        }

        return true;
    }

    /**
     * Guarda los cambios de la clave
     */
    private void guardarCambios() {
        if (!validarFormulario()) {
            return;
        }

        // Confirmar con el usuario
        Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                "Confirmación",
                "¿Está seguro de cambiar la contraseña?",
                "confirm",
                List.of(ButtonType.YES, ButtonType.NO));

        if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                // Actualizar la clave en el objeto usuario
                usuario.setClave(txt_Nueva.getText());

                // Guardar en la base de datos
                boolean exito = usuarioDAO.actualizarUsuario(usuario);

                if (exito) {
                    DialogUtil.mostrarDialogo(
                            "Éxito",
                            "La contraseña ha sido actualizada correctamente",
                            "info",
                            List.of(ButtonType.OK));

                    if (onGuardar != null) {
                        onGuardar.run();
                    }
                } else {
                    lbl_Error.setText("No se pudo actualizar la contraseña. Intente nuevamente.");
                }
            } catch (Exception e) {
                System.err.println("Error al cambiar clave: " + e.getMessage());
                lbl_Error.setText("Error al cambiar la contraseña: " + e.getMessage());
            }
        }
    }
}
