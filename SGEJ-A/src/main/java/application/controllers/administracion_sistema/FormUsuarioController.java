package application.controllers.administracion_sistema;

import application.controllers.DialogUtil;
import application.dao.UsuarioDAO;
import application.model.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
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
    private Label lbl_Error, lbl_Clave, lbl_ConfirmarClave;
    @FXML
    private Label lbl_ErrorIdentificacion, lbl_ErrorNombres, lbl_ErrorApellidos, lbl_ErrorUsuario, lbl_ErrorEmail;
    @FXML
    private Label lbl_RequisitosPassword, lbl_ReqLongitud, lbl_ReqMayuscula, lbl_ReqNumero, lbl_ReqEspecial;
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
    
    // Patrones individuales para cada requisito de contraseña
    private final Pattern longitudPattern = Pattern.compile(".{8,}");
    private final Pattern mayusculaPattern = Pattern.compile(".*[A-Z].*");
    private final Pattern numeroPattern = Pattern.compile(".*[0-9].*");
    private final Pattern especialPattern = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

    // Patrón para contraseñas seguras: al menos una mayúscula, un número y un
    // carácter especial
    private final Pattern contrasenaSeguraPattern = Pattern
            .compile("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$");

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
     * Oculta o muestra los campos de clave y sus etiquetas
     * 
     * @param ocultar true para ocultar, false para mostrar
     */
    private void ocultarCamposClaves(boolean ocultar) {
        // Asegurar que los componentes estén inicializados
        if (txt_Clave != null && txt_ConfirmarClave != null) {
            // Ocultar/Mostrar los campos de contraseña
            txt_Clave.setVisible(!ocultar);
            txt_Clave.setManaged(!ocultar);
            txt_ConfirmarClave.setVisible(!ocultar);
            txt_ConfirmarClave.setManaged(!ocultar);
            
            // Ocultar/Mostrar las etiquetas
            if (lbl_Clave != null && lbl_ConfirmarClave != null) {
                lbl_Clave.setVisible(!ocultar);
                lbl_Clave.setManaged(!ocultar);
                lbl_ConfirmarClave.setVisible(!ocultar);
                lbl_ConfirmarClave.setManaged(!ocultar);
                
                System.out.println("Campos de contraseña " + (ocultar ? "ocultados" : "mostrados") + " correctamente");
            } else {
                System.err.println("Las etiquetas de los campos de contraseña no están disponibles");
            }
            
            // Ocultar/Mostrar los indicadores de requisitos de contraseña
            if (lbl_RequisitosPassword != null && lbl_ReqLongitud != null && 
                lbl_ReqMayuscula != null && lbl_ReqNumero != null && lbl_ReqEspecial != null) {
                
                lbl_RequisitosPassword.setVisible(!ocultar);
                lbl_RequisitosPassword.setManaged(!ocultar);
                lbl_ReqLongitud.setVisible(!ocultar);
                lbl_ReqLongitud.setManaged(!ocultar);
                lbl_ReqMayuscula.setVisible(!ocultar);
                lbl_ReqMayuscula.setManaged(!ocultar);
                lbl_ReqNumero.setVisible(!ocultar);
                lbl_ReqNumero.setManaged(!ocultar);
                lbl_ReqEspecial.setVisible(!ocultar);
                lbl_ReqEspecial.setManaged(!ocultar);
                
                System.out.println("Requisitos de contraseña " + (ocultar ? "ocultados" : "mostrados") + " correctamente");
            }
        } else {
            System.err.println("Los campos de contraseña no están disponibles");
        }
    }

    @FXML
    private void initialize() {
        lbl_Error.setText("");
        
        // Limpiar las etiquetas de error
        limpiarEtiquetasError();

        // Inicializar combos con los roles directamente
        cmb_TipoUsuario.setItems(FXCollections.observableArrayList(
                "Administrador", "Abogado", "Contador", "Asistente Legal"));

        cmb_EstadoUsuario.setItems(FXCollections.observableArrayList(
                "ACTIVO", "INACTIVO", "SUSPENDIDO"));

        // Seleccionar valores predeterminados
        cmb_TipoUsuario.getSelectionModel().select("Administrador");
        cmb_EstadoUsuario.getSelectionModel().select("ACTIVO");
        
        // Configurar listeners para validación en tiempo real
        txt_Identificacion.textProperty().addListener((__, ___, newValue) -> 
            validarIdentificacion(newValue)
        );
        
        txt_Email.textProperty().addListener((__, ___, newValue) -> 
            validarEmail(newValue)
        );
        
        txt_Clave.textProperty().addListener((__, ___, newValue) -> 
            actualizarIndicadoresRequisitos(newValue)
        );

        // Configurar botones
        btn_Guardar.setOnAction(__ -> guardarUsuario());
        btn_Cancelar.setOnAction(__ -> {
            if (onCancelar != null) {
                onCancelar.run();
            }
        });
    }
    
    /**
     * Limpia todas las etiquetas de error
     */
    private void limpiarEtiquetasError() {
        // Limpiar etiquetas de error específicas
        lbl_ErrorIdentificacion.setText("");
        lbl_ErrorNombres.setText("");
        lbl_ErrorApellidos.setText("");
        lbl_ErrorUsuario.setText("");
        lbl_ErrorEmail.setText("");
        
        // Establecer color gris para requisitos de contraseña
        if (lbl_ReqLongitud != null && lbl_ReqMayuscula != null && 
            lbl_ReqNumero != null && lbl_ReqEspecial != null) {
            String colorGris = "-fx-font-size: 9px; -fx-text-fill: #666666;";
            lbl_ReqLongitud.setStyle(colorGris);
            lbl_ReqMayuscula.setStyle(colorGris);
            lbl_ReqNumero.setStyle(colorGris);
            lbl_ReqEspecial.setStyle(colorGris);
        }
    }
    
    /**
     * Valida la identificación (cédula ecuatoriana)
     */
    private void validarIdentificacion(String identificacion) {
        application.utils.VerificationID verificador = new application.utils.VerificationID();
        
        if (!identificacion.isEmpty()) {
            // Primero validamos que sea una cédula válida
            if (!verificador.validarCedula(identificacion)) {
                lbl_ErrorIdentificacion.setText("La cédula ingresada no es válida");
                return;
            }
            
            // Luego verificamos que no exista otro usuario con la misma identificación
            try {
                Integer idUsuarioActual = (modo.equals("EDITAR") && usuario != null) ? usuario.getId() : null;
                boolean identificacionExistente = usuarioDAO.existeUsuarioConIdentificacion(identificacion, idUsuarioActual);
                
                if (identificacionExistente) {
                    lbl_ErrorIdentificacion.setText("Esta cédula ya está registrada para otro usuario");
                } else {
                    lbl_ErrorIdentificacion.setText("");
                }
            } catch (SQLException e) {
                System.err.println("Error al verificar identificación: " + e.getMessage());
                // No mostramos error al usuario durante la validación en tiempo real
            }
        } else {
            lbl_ErrorIdentificacion.setText("");
        }
    }
    
    /**
     * Valida el formato del correo electrónico
     */
    private void validarEmail(String email) {
        if (!email.isEmpty()) {
            if (!emailPattern.matcher(email).matches()) {
                lbl_ErrorEmail.setText("El formato del correo electrónico no es válido");
            } else {
                lbl_ErrorEmail.setText("");
            }
        } else {
            lbl_ErrorEmail.setText("");
        }
    }
    
    /**
     * Actualiza los indicadores visuales de los requisitos de contraseña
     * 
     * @param password La contraseña a validar
     */
    private void actualizarIndicadoresRequisitos(String password) {
        // Si no están inicializados, salir
        if (lbl_ReqLongitud == null || lbl_ReqMayuscula == null || 
            lbl_ReqNumero == null || lbl_ReqEspecial == null) {
            return;
        }
        
        // Validar cada requisito por separado
        boolean cumpleLongitud = longitudPattern.matcher(password).matches();
        boolean cumpleMayuscula = mayusculaPattern.matcher(password).matches();
        boolean cumpleNumero = numeroPattern.matcher(password).matches();
        boolean cumpleEspecial = especialPattern.matcher(password).matches();
        
        // Actualizar color de cada indicador (verde si cumple, rojo si no)
        lbl_ReqLongitud.setStyle("-fx-font-size: 9px; -fx-text-fill: " + 
                              (cumpleLongitud ? "#006400" : "#FF0000") + ";");
        
        lbl_ReqMayuscula.setStyle("-fx-font-size: 9px; -fx-text-fill: " + 
                               (cumpleMayuscula ? "#006400" : "#FF0000") + ";");
        
        lbl_ReqNumero.setStyle("-fx-font-size: 9px; -fx-text-fill: " + 
                            (cumpleNumero ? "#006400" : "#FF0000") + ";");
        
        lbl_ReqEspecial.setStyle("-fx-font-size: 9px; -fx-text-fill: " + 
                              (cumpleEspecial ? "#006400" : "#FF0000") + ";");
    }

    /**
     * Valida los campos del formulario
     * 
     * @return true si los campos son válidos, false en caso contrario
     */
    private boolean validarFormulario() {
        // Limpiar todas las etiquetas de error
        limpiarEtiquetasError();
        lbl_Error.setText(""); // limpiar error general
        
        boolean formularioValido = true;

        // Validar campos obligatorios
        if (txt_Nombres.getText().trim().isEmpty()) {
            lbl_ErrorNombres.setText("El nombre es obligatorio");
            formularioValido = false;
        }
        
        if (txt_Apellidos.getText().trim().isEmpty()) {
            lbl_ErrorApellidos.setText("El apellido es obligatorio");
            formularioValido = false;
        }
        
        if (txt_Identificacion.getText().trim().isEmpty()) {
            lbl_ErrorIdentificacion.setText("La cédula es obligatoria");
            formularioValido = false;
        }
        
        if (txt_Email.getText().trim().isEmpty()) {
            lbl_ErrorEmail.setText("El correo es obligatorio");
            formularioValido = false;
        }
        
        if (txt_Usuario.getText().trim().isEmpty()) {
            lbl_ErrorUsuario.setText("El nombre de usuario es obligatorio");
            formularioValido = false;
        }

        // Validar email
        if (!txt_Email.getText().trim().isEmpty() && !emailPattern.matcher(txt_Email.getText().trim()).matches()) {
            lbl_ErrorEmail.setText("El formato del correo electrónico no es válido");
            formularioValido = false;
        }

        // Validar cédula ecuatoriana
        if (!txt_Identificacion.getText().trim().isEmpty()) {
            String identificacion = txt_Identificacion.getText().trim();
            application.utils.VerificationID verificador = new application.utils.VerificationID();

            if (!verificador.validarCedula(identificacion)) {
                lbl_ErrorIdentificacion.setText("La cédula ingresada no es válida");
                formularioValido = false;
            } else {
                // Verificar que no exista otro usuario con la misma identificación
                try {
                    Integer idUsuarioActual = (modo.equals("EDITAR") && usuario != null) ? usuario.getId() : null;
                    boolean identificacionExistente = usuarioDAO.existeUsuarioConIdentificacion(identificacion, idUsuarioActual);
                    
                    if (identificacionExistente) {
                        lbl_ErrorIdentificacion.setText("Esta cédula ya está registrada para otro usuario");
                        formularioValido = false;
                    }
                } catch (SQLException e) {
                    System.err.println("Error al verificar identificación: " + e.getMessage());
                    lbl_Error.setText("Error al verificar disponibilidad de la cédula");
                    formularioValido = false;
                }
            }
        }

        // Validar claves en modo CREAR
        if (modo.equals("CREAR")) {
            if (txt_Clave.getText().trim().isEmpty() || txt_ConfirmarClave.getText().trim().isEmpty()) {
                lbl_Error.setText("Debe ingresar y confirmar la contraseña");
                formularioValido = false;
            }

            if (!txt_Clave.getText().equals(txt_ConfirmarClave.getText())) {
                lbl_Error.setText("Las contraseñas no coinciden");
                formularioValido = false;
            }

            String contrasena = txt_Clave.getText();
            
            // Actualizar indicadores visuales de requisitos
            actualizarIndicadoresRequisitos(contrasena);

            // Validar requisitos de seguridad
            if (!contrasenaSeguraPattern.matcher(contrasena).matches()) {
                lbl_Error.setText("La contraseña no cumple con todos los requisitos de seguridad");
                formularioValido = false;
            }
        }
        
        // Validar que el nombre de usuario sea único (excepto para el mismo usuario en edición)
        try {
            Usuario usuarioExistente = usuarioDAO.obtenerUsuarioPorNombreUsuario(txt_Usuario.getText().trim());
            if (usuarioExistente != null && (modo.equals("CREAR") || !usuarioExistente.getId().equals(usuario.getId()))) {
                lbl_ErrorUsuario.setText("Este nombre de usuario ya existe");
                formularioValido = false;
            }
        } catch (Exception e) {
            System.err.println("Error al verificar usuario: " + e.getMessage());
            lbl_Error.setText("Error al verificar disponibilidad del usuario");
            formularioValido = false;
        }
        
        return formularioValido;
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
