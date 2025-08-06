package application.controllers.casos_documentacion;

import application.controllers.DialogUtil;
import application.database.DatabaseConnection;
import application.model.Caso;
import application.model.HistorialComunicacion;
import application.model.Personal;
import application.service.AbogadoCasoService;
import application.service.AbogadoService;
import application.service.CasoService;
import application.service.HistorialComunicacionService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class FormHistorialComunicacionController {

    @FXML
    private DatePicker dtp_Fecha;
    @FXML
    private ComboBox<Personal> cbx_Abogado;
    @FXML
    private ComboBox<String> cbx_TipoAccion;
    @FXML
    private TextField txtf_Expediente;
    @FXML
    private TextArea txta_Descripcion;
    @FXML
    private Button btn_Guardar;
    @FXML
    private Button btn_Cancelar;
    @FXML
    private Text txt_TituloForm;

    private Runnable onGuardar, onCancelar;

    @FXML
    private void initialize() {
        // Configurar los tipos de acción
        cbx_TipoAccion.getItems().addAll("Llamada", "Correo", "Mensaje", "Visita", "Otro");

        // Establecer la fecha actual y bloquearla para edición
        dtp_Fecha.setValue(LocalDate.now());
        dtp_Fecha.setEditable(false);
        // También deshabilitamos el DatePicker para que no se pueda abrir el calendario
        dtp_Fecha.setDisable(true);

        // No cargamos los estilos CSS aquí porque la escena aún no está disponible en
        // initialize
        // Lo haremos en el método setModo que se llama después de que el nodo está en
        // la escena

        // Aplicar estilo para que no parezca deshabilitado
        dtp_Fecha.getStyleClass().add("readonly-datepicker");

        // Agregar listeners para validación en tiempo real
        cbx_Abogado.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Limpiar el estilo de error cuando cambia la selección
            if (newValue != null) {
                cbx_Abogado.setStyle("");
            }
        });

        txtf_Expediente.textProperty().addListener((observable, oldValue, newValue) -> {
            // Limpiar el estilo de error cuando cambia el texto
            if (newValue != null && !newValue.isEmpty()) {
                txtf_Expediente.setStyle("");
            }
        });

        // Cargar la lista de abogados
        cargarAbogados();

        // Configurar cómo se muestran los abogados en el combobox
        cbx_Abogado.setConverter(new StringConverter<Personal>() {
            @Override
            public String toString(Personal abogado) {
                if (abogado == null) {
                    return "";
                }
                return abogado.getNombres() + " " + abogado.getApellidos();
            }

            @Override
            public Personal fromString(String string) {
                return null; // No se utiliza para la conversión en este caso
            }
        });

        btn_Guardar.setOnAction(actionEvent -> {
            // Construir un mensaje específico con solo los campos que faltan
            StringBuilder camposFaltantes = new StringBuilder();

            if (cbx_Abogado.getValue() == null) {
                camposFaltantes.append(" - Abogado Responsable\n");
            }

            if (cbx_TipoAccion.getValue() == null) {
                camposFaltantes.append(" - Tipo de Acción\n");
            }

            if (txtf_Expediente.getText().isEmpty()) {
                camposFaltantes.append(" - Número de Expediente\n");
            }

            if (txta_Descripcion.getText().isEmpty()) {
                camposFaltantes.append(" - Descripción\n");
            }

            // Si hay campos faltantes, mostrar mensaje personalizado
            if (camposFaltantes.length() > 0) {
                DialogUtil.mostrarDialogo(
                        "Campos requeridos",
                        "Por favor, complete los siguientes campos obligatorios:\n" +
                                camposFaltantes.toString(),
                        "warning",
                        List.of(ButtonType.OK));
                return;
            }

            // Si llegamos aquí, tenemos todos los campos requeridos
            // Validar ahora que el número de expediente sea válido y exista
            String numeroExpediente = txtf_Expediente.getText().trim();

            // El campo ya fue validado como no vacío en la verificación anterior,
            // pero verificamos el formato
            if (!numeroExpediente.matches("^EXP-\\d{4}-\\d{4}$")) {
                DialogUtil.mostrarDialogo(
                        "Formato inválido",
                        "El formato del número de expediente es incorrecto.\n" +
                                "El formato correcto es: EXP-YYYY-NNNN (por ejemplo, EXP-2025-0001)",
                        "warning",
                        List.of(ButtonType.OK));
                return;
            }

            // Crear instancia del servicio para uso posterior
            CasoService casoService = new CasoService();
            Caso caso = null;

            // Verificar existencia en la base de datos
            try {
                // Intentar obtener el caso directamente, lo necesitaremos más adelante
                caso = casoService.obtenerCasoPorNumero(numeroExpediente);

                if (caso == null) {
                    DialogUtil.mostrarDialogo(
                            "Caso no encontrado",
                            "El número de expediente '" + numeroExpediente + "' no existe en el sistema.\n" +
                                    "Por favor, verifique el número de expediente.",
                            "warning",
                            List.of(ButtonType.OK));
                    // Marcar el campo con error
                    txtf_Expediente.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    txtf_Expediente.requestFocus();
                    return;
                }

                // Verificamos inmediatamente si el abogado seleccionado está asignado al caso
                Personal abogado = cbx_Abogado.getValue();
                if (abogado != null) {
                    try {
                        // Imprimir información de depuración para verificar los IDs
                        System.out.println("INFO: Verificando asignación de abogado a caso");
                        System.out.println("- Caso ID: " + caso.getId() + ", Número: " + caso.getNumeroExpediente());
                        System.out.println("- Abogado ID: " + abogado.getId() + ", Nombre: " + abogado.getNombres()
                                + " " + abogado.getApellidos());

                        // También imprimir el ID del abogado asignado al caso directamente (si existe)
                        if (caso.getAbogadoId() > 0) {
                            System.out.println("- Abogado principal asignado al caso (ID): " + caso.getAbogadoId());
                        } else {
                            System.out.println("- No hay abogado principal asignado directamente al caso");
                        }

                        AbogadoCasoService abogadoVerificador = new AbogadoCasoService();
                        boolean abogadoAsignado = abogadoVerificador.verificarAbogadoAsignadoACaso(caso.getId(),
                                abogado.getId());
                        System.out.println(
                                "- Resultado de verificación: " + (abogadoAsignado ? "ASIGNADO" : "NO ASIGNADO"));

                        // Verificación adicional directa para depuración
                        boolean esAbogadoPrincipal = caso.getAbogadoId() == abogado.getId();
                        System.out.println("- ¿Es abogado principal directo?: " + (esAbogadoPrincipal ? "SÍ" : "NO"));

                        // Si no está asignado según el servicio, pero es el abogado principal directo,
                        // considerar como asignado
                        if (!abogadoAsignado && esAbogadoPrincipal) {
                            abogadoAsignado = true;
                            System.out.println("- Corrigiendo validación: El abogado es el principal directo del caso");
                        }

                        // Si después de las verificaciones sigue sin estar asignado
                        if (!abogadoAsignado) {
                            DialogUtil.mostrarDialogo(
                                    "Error de validación",
                                    "El abogado seleccionado no está asignado al caso " + numeroExpediente + ".\n\n" +
                                            "Para continuar, debe seleccionar un abogado que esté formalmente asignado al expediente.\n\n"
                                            +
                                            "Si está seguro que este abogado debería tener acceso al caso, asígnelo primero desde el módulo de casos.",
                                    "warning",
                                    List.of(ButtonType.OK));
                            // Marcar el campo con error
                            cbx_Abogado.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                            cbx_Abogado.requestFocus();
                            return;
                        }
                    } catch (Exception e) {
                        System.err.println("ERROR al verificar asignación de abogado: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                DialogUtil.mostrarDialogo(
                        "Error de validación",
                        "No se pudo verificar el número de expediente: " + e.getMessage(),
                        "error",
                        List.of(ButtonType.OK));
                return;
            }

            // Intentar guardar la comunicación
            try {
                // Confirmar antes de guardar
                Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                        "Confirmación",
                        "¿Está seguro que desea guardar este historial de comunicación?",
                        "confirm",
                        List.of(ButtonType.YES, ButtonType.NO));

                if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
                    // Preparar los datos
                    HistorialComunicacion comunicacion = new HistorialComunicacion();

                    // Asignamos directamente la fecha actual sin validaciones
                    comunicacion.setFecha(new java.sql.Date(System.currentTimeMillis()));

                    comunicacion.setTipo(cbx_TipoAccion.getValue());
                    comunicacion.setDescripcion(txta_Descripcion.getText());

                    // Usamos el caso que ya fue validado anteriormente
                    comunicacion.setCasoId(caso.getId());

                    // Obtener el abogado seleccionado y validar que esté asignado al caso
                    Personal abogado = cbx_Abogado.getValue();
                    if (abogado == null) {
                        throw new Exception("Debe seleccionar un abogado responsable");
                    }

                    // Verificar que el abogado esté asignado al caso
                    AbogadoCasoService abogadoCasoService = new AbogadoCasoService();
                    if (!abogadoCasoService.verificarAbogadoAsignadoACaso(caso.getId(), abogado.getId())) {
                        // Mostrar mensaje de error y no permitir continuar
                        DialogUtil.mostrarDialogo(
                                "Error de validación",
                                "El abogado seleccionado no está formalmente asignado al caso " + numeroExpediente
                                        + ".\n\n" +
                                        "Para registrar una comunicación, debe seleccionar un abogado que esté asignado al caso.\n\n"
                                        +
                                        "Por favor, vaya al módulo de casos y asigne primero el abogado al expediente, o seleccione un abogado diferente.",
                                "error",
                                List.of(ButtonType.OK));

                        // Resaltar el campo de abogado para indicar que hay un problema
                        cbx_Abogado.setStyle("-fx-border-color: red; -fx-border-width: 2px;");

                        // Enfocar el campo para que el usuario pueda seleccionar otro abogado
                        cbx_Abogado.requestFocus();

                        // No permitimos continuar
                        return;
                    }

                    comunicacion.setAbogadoId(abogado.getId());
                    comunicacion.setAbogadoNombre(abogado.getNombres() + " " + abogado.getApellidos());

                    // Guardar en la base de datos
                    Connection conn = null;
                    try {
                        conn = DatabaseConnection.getConnection();
                        HistorialComunicacionService service = new HistorialComunicacionService(conn);
                        String resultado = service.registrarComunicacion(comunicacion);

                        if (resultado.isEmpty()) {
                            DialogUtil.mostrarDialogo(
                                    "Guardado exitoso",
                                    "La comunicación ha sido registrada correctamente.",
                                    "info",
                                    List.of(ButtonType.OK));

                            if (onGuardar != null)
                                onGuardar.run();
                        } else {
                            DialogUtil.mostrarDialogo(
                                    "Error al guardar",
                                    "No se pudo guardar la comunicación: " + resultado,
                                    "error",
                                    List.of(ButtonType.OK));
                        }
                    } finally {
                        // Cerrar la conexión después de usarla
                        if (conn != null) {
                            try {
                                conn.close();
                                System.out.println("Conexión cerrada después de registrar comunicación");
                            } catch (Exception e) {
                                System.err.println("ERROR: No se pudo cerrar la conexión: " + e.getMessage());
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                DialogUtil.mostrarDialogo(
                        "Error en la base de datos",
                        "Ocurrió un error al guardar la comunicación: " + e.getMessage(),
                        "error",
                        List.of(ButtonType.OK));
            } catch (Exception e) {
                e.printStackTrace();
                DialogUtil.mostrarDialogo(
                        "Error inesperado",
                        "Ocurrió un error inesperado: " + e.getMessage(),
                        "error",
                        List.of(ButtonType.OK));
            }
        });

        btn_Cancelar.setOnAction(cancelEvent -> {
            Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                    "Confirmación",
                    "¿Está seguro que desea cancelar el formulario?\nSe perderán los cambios no guardados.",
                    "confirm",
                    List.of(ButtonType.YES, ButtonType.NO));

            if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
                if (onCancelar != null)
                    onCancelar.run();
            }
        });

    }

    /**
     * Carga la lista de abogados en el combobox
     */
    private void cargarAbogados() {
        try {
            List<Personal> abogados = AbogadoService.obtenerAbogados();
            cbx_Abogado.setItems(FXCollections.observableArrayList(abogados));
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.mostrarDialogo(
                    "Error al cargar abogados",
                    "No se pudo cargar la lista de abogados: " + e.getMessage(),
                    "error",
                    List.of(ButtonType.OK));
        }
    }

    public void setOnGuardar(Runnable handler) {
        this.onGuardar = handler;
    }

    public void setOnCancelar(Runnable handler) {
        this.onCancelar = handler;
    }

    public void setModo(String modo) {
        boolean esNuevo = "NUEVO".equals(modo);
        boolean esEditar = "EDITAR".equals(modo);
        boolean esVer = "VER".equals(modo);

        // Limpiar cualquier estilo de error previo
        cbx_Abogado.setStyle("");
        txtf_Expediente.setStyle("");
        txta_Descripcion.setStyle("");

        if (esNuevo) {
            txt_TituloForm.setText("Registrar comunicación");
            // Restablecer la fecha actual para nuevos registros
            dtp_Fecha.setValue(LocalDate.now());
        } else if (esEditar) {
            txt_TituloForm.setText("Editar comunicación");
        } else if (esVer) {
            txt_TituloForm.setText("Visualizar comunicación");
        }

        // La fecha siempre estará deshabilitada ya que es automática
        dtp_Fecha.setDisable(true);
        dtp_Fecha.setEditable(false);

        // Aplicar estilos al DatePicker para que se vea mejor aun estando deshabilitado
        if (dtp_Fecha.getScene() != null
                && !dtp_Fecha.getScene().getStylesheets().contains("datepicker_readonly.css")) {
            try {
                String cssPath = getClass().getResource("/styles/datepicker_readonly.css").toExternalForm();
                dtp_Fecha.getScene().getStylesheets().add(cssPath);
            } catch (Exception ex) {
                System.err.println("No se pudo cargar el archivo CSS: " + ex.getMessage());
            }
        }

        // Agregar un listener para el campo de expediente que verifique automáticamente
        // si el abogado actual está asignado a ese expediente
        txtf_Expediente.focusedProperty().addListener((obs, oldVal, newVal) -> {
            // Solo verificamos cuando el campo pierde el foco y no está vacío
            if (!newVal && !txtf_Expediente.getText().trim().isEmpty()) {
                verificarAbogadoExpediente();
            }
        });

        // También agregamos un listener para el combo de abogados
        cbx_Abogado.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !txtf_Expediente.getText().trim().isEmpty()) {
                verificarAbogadoExpediente();
            }
        });

        // Aplicar la clase de estilo
        if (!dtp_Fecha.getStyleClass().contains("readonly-datepicker")) {
            dtp_Fecha.getStyleClass().add("readonly-datepicker");
        }

        cbx_Abogado.setDisable(esVer);
        cbx_TipoAccion.setDisable(esVer);
        txtf_Expediente.setEditable(esNuevo || esEditar);
        txta_Descripcion.setEditable(esNuevo || esEditar);

        btn_Guardar.setVisible(!esVer);
        btn_Guardar.setDisable(esVer);
    }

    /**
     * Verifica si el abogado seleccionado está asignado al expediente ingresado
     * y muestra una advertencia visual si no lo está.
     */
    private void verificarAbogadoExpediente() {
        String numeroExpediente = txtf_Expediente.getText().trim();
        Personal abogado = cbx_Abogado.getValue();

        // Si no hay abogado o expediente seleccionados, no hacemos nada
        if (abogado == null || numeroExpediente.isEmpty()) {
            return;
        }

        // Primero verificar que el expediente tenga el formato correcto
        if (!numeroExpediente.matches("^EXP-\\d{4}-\\d{4}$")) {
            txtf_Expediente.setStyle("-fx-border-color: orange; -fx-border-width: 2px;");
            return;
        }

        // Intentar obtener el caso por número de expediente
        try {
            CasoService casoService = new CasoService();
            Caso caso = casoService.obtenerCasoPorNumero(numeroExpediente);

            if (caso == null) {
                txtf_Expediente.setStyle("-fx-border-color: orange; -fx-border-width: 2px;");
                return;
            }

            // Verificar si el abogado está asignado (ya sea como principal o en la tabla
            // abogado_caso)
            AbogadoCasoService abogadoCasoService = new AbogadoCasoService();
            boolean esAbogadoPrincipal = caso.getAbogadoId() == abogado.getId();
            boolean asignadoEnTabla = abogadoCasoService.verificarAbogadoAsignadoACaso(caso.getId(), abogado.getId());

            System.out.println("Verificando asignación abogado-expediente en tiempo real:");
            System.out.println(" - Expediente: " + numeroExpediente + " (ID: " + caso.getId() + ")");
            System.out.println(" - Abogado: " + abogado.getNombres() + " " + abogado.getApellidos() + " (ID: "
                    + abogado.getId() + ")");
            System.out.println(" - Abogado principal del caso (ID): " + caso.getAbogadoId());
            System.out.println(" - ¿Es abogado principal?: " + (esAbogadoPrincipal ? "SÍ" : "NO"));
            System.out.println(" - ¿Asignado en tabla abogado_caso?: " + (asignadoEnTabla ? "SÍ" : "NO"));

            if (!esAbogadoPrincipal && !asignadoEnTabla) {
                // Advertencia visual
                cbx_Abogado.setStyle("-fx-border-color: orange; -fx-border-width: 2px;");
            } else {
                // Todo correcto
                cbx_Abogado.setStyle("");
                txtf_Expediente.setStyle("");
            }
        } catch (Exception e) {
            System.err.println("Error al verificar abogado-expediente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void cargarDatos(String fecha, String usuario, String tipo, String descripcion, String expediente) {
        if (fecha != null && !fecha.isEmpty()) {
            try {
                dtp_Fecha.setValue(LocalDate.parse(fecha));
            } catch (Exception e) {
                // Si no se puede parsear la fecha, usar la actual
                dtp_Fecha.setValue(LocalDate.now());
            }
        }

        // Buscar el abogado por nombre y seleccionarlo
        if (usuario != null && !usuario.isEmpty()) {
            for (Personal abogado : cbx_Abogado.getItems()) {
                String nombreCompleto = abogado.getNombres() + " " + abogado.getApellidos();
                if (nombreCompleto.equalsIgnoreCase(usuario)) {
                    cbx_Abogado.setValue(abogado);
                    break;
                }
            }
        }

        cbx_TipoAccion.setValue(tipo);
        txta_Descripcion.setText(descripcion);
        txtf_Expediente.setText(expediente);
    }

    public static Node cargarFormulario(String modo, ComunicacionDemo comunicacion, Consumer<Void> onGuardar,
            Consumer<Void> onCancelar) {
        try {
            FXMLLoader loader = new FXMLLoader(FormHistorialComunicacionController.class
                    .getResource("/views/casos_documentos/form_historial_comunicacion.fxml"));
            Node form = loader.load();

            FormHistorialComunicacionController controller = loader.getController();
            controller.setModo(modo);
            controller.setOnGuardar(() -> onGuardar.accept(null));
            controller.setOnCancelar(() -> onCancelar.accept(null));

            if (comunicacion != null && !modo.equals("NUEVO")) {
                controller.cargarDatos(
                        comunicacion.fecha(),
                        comunicacion.usuario(),
                        comunicacion.accion(),
                        comunicacion.descripcion(),
                        comunicacion.expediente());
            }

            return form;
        } catch (IOException e) {
            e.printStackTrace();
            return new Label("Error al cargar el formulario");
        }
    }

    public record ComunicacionDemo(String fecha, String usuario, String accion, String descripcion, String expediente) {
    }
}
