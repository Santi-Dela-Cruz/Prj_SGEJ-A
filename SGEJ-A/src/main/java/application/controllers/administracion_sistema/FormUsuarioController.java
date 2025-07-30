package application.controllers.administracion_sistema;

import application.controllers.DialogUtil;
import application.dao.UsuarioDAO;
import application.model.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class FormUsuarioController {

    @FXML
    private TextField txt_Nombres, txt_Apellidos, txt_Identificacion, txt_Email, txt_Usuario;
    @FXML
    private PasswordField txt_Clave, txt_ConfirmarClave;
    @FXML
    private ComboBox<String> cmb_TipoUsuario, cmb_EstadoUsuario;
    @FXML
    private Label lbl_Error;
    @FXML
    private Button btn_Guardar, btn_Cancelar;
    @FXML
    private Label lbl_Titulo;

    private Runnable onCancelar, onGuardar;
    private Usuario usuario;
    private String modo = "CREAR"; // "CREAR" o "EDITAR"

    // DAO para operaciones con la base de datos
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Patrones para validación
    private final Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private final Pattern cedulaPattern = Pattern.compile("^[0-9]{10}$");
    private final Pattern rucPattern = Pattern.compile("^[0-9]{13}$");

    public void setOnGuardar(Runnable handler) {
        this.onGuardar = handler;
    }

    public void setOnCancelar(Runnable onCancelar) {
        this.onCancelar = onCancelar;
    }

    /**
     * Establece el usuario a editar o crea uno nuevo si es null
     * 
     * @param usuario Usuario a editar, null para crear uno nuevo
     */
    public void setUsuario(Usuario usuario) {
        if (usuario != null) {
            this.usuario = usuario;
            modo = "EDITAR";

            // Mostrar datos del usuario en el formulario
            txt_Nombres.setText(usuario.getNombres());
            txt_Apellidos.setText(usuario.getApellidos());
            txt_Identificacion.setText(usuario.getIdentificacion());
            txt_Email.setText(usuario.getEmail());
            txt_Usuario.setText(usuario.getNombreUsuario());

            // No mostrar la clave en modo edición
            ocultarCamposClaves(true);

            // Seleccionar rol basado en el tipo de usuario
            if (usuario.getTipoUsuario() != null) {
                String rol = "";
                switch (usuario.getTipoUsuario()) {
                    case INTERNO -> rol = "Administrador";
                    case NATURAL -> rol = "Abogado";
                    case JURIDICA -> rol = "Contador";
                    case EXTERNO -> rol = "Asistente Legal";
                    default -> rol = "Administrador";
                }
                cmb_TipoUsuario.getSelectionModel().select(rol);
            }

            if (usuario.getEstadoUsuario() != null) {
                String estadoStr = usuario.getEstadoUsuario().toString();
                cmb_EstadoUsuario.getSelectionModel().select(estadoStr);
            }

            lbl_Titulo.setText("Editar Usuario");
        } else {
            this.usuario = new Usuario();
            modo = "CREAR";

            // Mostrar campos de clave en modo crear
            ocultarCamposClaves(false);

            lbl_Titulo.setText("Crear Usuario");
        }
    }

    /**
     * Oculta o muestra los campos de clave
     * 
     * @param ocultar true para ocultar, false para mostrar
     */
    private void ocultarCamposClaves(boolean ocultar) {
        if (txt_Clave != null && txt_Clave.getParent() != null) {
            txt_Clave.setVisible(!ocultar);
            txt_Clave.setManaged(!ocultar);
            txt_ConfirmarClave.setVisible(!ocultar);
            txt_ConfirmarClave.setManaged(!ocultar);

            // También las etiquetas asociadas
            if (txt_Clave.getParent().getChildrenUnmodifiable().size() > 0) {
                txt_Clave.getParent().getChildrenUnmodifiable().get(0).setVisible(!ocultar);
                txt_Clave.getParent().getChildrenUnmodifiable().get(0).setManaged(!ocultar);
            }

            if (txt_ConfirmarClave.getParent().getChildrenUnmodifiable().size() > 0) {
                txt_ConfirmarClave.getParent().getChildrenUnmodifiable().get(0).setVisible(!ocultar);
                txt_ConfirmarClave.getParent().getChildrenUnmodifiable().get(0).setManaged(!ocultar);
            }
        }
    }

    @FXML
    private void initialize() {
        lbl_Error.setText("");

        // Inicializar combos con los roles directamente
        cmb_TipoUsuario.setItems(FXCollections.observableArrayList(
                "Administrador", "Abogado", "Contador", "Asistente Legal"));

        cmb_EstadoUsuario.setItems(FXCollections.observableArrayList(
                "ACTIVO", "INACTIVO", "SUSPENDIDO"));

        // Seleccionar valores predeterminados
        cmb_TipoUsuario.getSelectionModel().select("Administrador");
        cmb_EstadoUsuario.getSelectionModel().select("ACTIVO");

        // Configurar botones
        btn_Guardar.setOnAction(e -> guardarUsuario());
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

        // Validar campos obligatorios
        if (txt_Nombres.getText().trim().isEmpty() ||
                txt_Apellidos.getText().trim().isEmpty() ||
                txt_Identificacion.getText().trim().isEmpty() ||
                txt_Email.getText().trim().isEmpty() ||
                txt_Usuario.getText().trim().isEmpty()) {

            lbl_Error.setText("Todos los campos son obligatorios");
            return false;
        }

        // Validar email
        if (!emailPattern.matcher(txt_Email.getText().trim()).matches()) {
            lbl_Error.setText("El correo electrónico no es válido");
            return false;
        }

        // Validar identificación (cédula o RUC)
        String identificacion = txt_Identificacion.getText().trim();
        if (!cedulaPattern.matcher(identificacion).matches() && !rucPattern.matcher(identificacion).matches()) {
            lbl_Error.setText("La identificación debe ser una cédula (10 dígitos) o RUC (13 dígitos)");
            return false;
        }

        // Validar claves en modo CREAR
        if (modo.equals("CREAR")) {
            if (txt_Clave.getText().trim().isEmpty() || txt_ConfirmarClave.getText().trim().isEmpty()) {
                lbl_Error.setText("Debe ingresar y confirmar la contraseña");
                return false;
            }

            if (!txt_Clave.getText().equals(txt_ConfirmarClave.getText())) {
                lbl_Error.setText("Las contraseñas no coinciden");
                return false;
            }

            if (txt_Clave.getText().length() < 6) {
                lbl_Error.setText("La contraseña debe tener al menos 6 caracteres");
                return false;
            }
        }

        // Validar que el nombre de usuario sea único (excepto para el mismo usuario en
        // edición)
        try {
            Usuario usuarioExistente = usuarioDAO.obtenerUsuarioPorNombreUsuario(txt_Usuario.getText().trim());
            if (usuarioExistente != null
                    && (modo.equals("CREAR") || !usuarioExistente.getId().equals(usuario.getId()))) {
                lbl_Error.setText("El nombre de usuario ya existe");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error al verificar usuario: " + e.getMessage());
            lbl_Error.setText("Error al verificar disponibilidad del usuario");
            return false;
        }

        return true;
    }

    /**
     * Guarda el usuario en la base de datos
     */
    private void guardarUsuario() {
        if (!validarFormulario()) {
            return;
        }

        // Actualizar datos del usuario
        usuario.setNombres(txt_Nombres.getText().trim());
        usuario.setApellidos(txt_Apellidos.getText().trim());
        usuario.setIdentificacion(txt_Identificacion.getText().trim());
        usuario.setEmail(txt_Email.getText().trim());
        usuario.setNombreUsuario(txt_Usuario.getText().trim());

        // Establecer clave solo en modo CREAR
        if (modo.equals("CREAR")) {
            usuario.setClave(txt_Clave.getText());
        }

        // Convertir el rol seleccionado al tipo de usuario correspondiente
        String rolSeleccionado = cmb_TipoUsuario.getValue();
        Usuario.TipoUsuario tipoUsuario = switch (rolSeleccionado) {
            case "Administrador" -> Usuario.TipoUsuario.INTERNO;
            case "Abogado" -> Usuario.TipoUsuario.NATURAL;
            case "Contador" -> Usuario.TipoUsuario.JURIDICA;
            case "Asistente Legal" -> Usuario.TipoUsuario.EXTERNO;
            default -> Usuario.TipoUsuario.OTRO;
        };

        // Establecer tipo y estado
        usuario.setTipoUsuario(tipoUsuario);
        usuario.setEstadoUsuario(Usuario.EstadoUsuario.valueOf(cmb_EstadoUsuario.getValue()));

        // Confirmar con el usuario
        String mensaje = modo.equals("CREAR")
                ? "¿Está seguro de crear el usuario?"
                : "¿Está seguro de guardar los cambios?";

        Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                "Confirmación",
                mensaje,
                "confirm",
                List.of(ButtonType.YES, ButtonType.NO));

        if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                boolean exito;

                if (modo.equals("CREAR")) {
                    exito = usuarioDAO.agregarUsuario(usuario);
                } else {
                    exito = usuarioDAO.actualizarUsuario(usuario);
                }

                if (exito) {
                    String mensajeExito = modo.equals("CREAR")
                            ? "Usuario creado correctamente"
                            : "Usuario actualizado correctamente";

                    DialogUtil.mostrarDialogo(
                            "Éxito",
                            mensajeExito,
                            "info",
                            List.of(ButtonType.OK));

                    if (onGuardar != null) {
                        onGuardar.run();
                    }
                } else {
                    lbl_Error.setText("No se pudo guardar el usuario. Intente nuevamente.");
                }
            } catch (Exception e) {
                System.err.println("Error al guardar usuario: " + e.getMessage());
                lbl_Error.setText("Error al guardar el usuario: " + e.getMessage());
            }
        }
    }
}
