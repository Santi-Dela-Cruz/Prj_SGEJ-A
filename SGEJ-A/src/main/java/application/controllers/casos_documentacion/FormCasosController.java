package application.controllers.casos_documentacion;

import application.controllers.DialogUtil;
import application.service.CasoService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class FormCasosController {
    @FXML
    private TextField txtf_NumeroExpediente;
    @FXML
    private TextField txtf_TituloCaso;
    @FXML
    private TextField txtf_IdentificacionCliente; // Nuevo campo de identificación del cliente
    @FXML
    private ComboBox<String> cbx_TipoCaso;
    @FXML
    private ComboBox<String> cbx_Estado;
    @FXML
    private DatePicker dt_FechaInicio;
    @FXML
    private TextArea txtb_DescripcionCaso;

    @FXML
    private TableView<AbogadoDemo> tb_Abogados;
    @FXML
    private TableColumn<AbogadoDemo, Boolean> tbc_CheckBoton;
    @FXML
    private TableColumn<AbogadoDemo, String> tbc_Nombres;
    @FXML
    private TableColumn<AbogadoDemo, String> tbc_Apellidos;
    @FXML
    private TableColumn<AbogadoDemo, String> tbc_Cedula;
    @FXML
    private TableColumn<AbogadoDemo, String> tbc_Rol_ChekBox;
    @FXML
    private Text txt_TituloForm;

    @FXML
    private Button btn_Guardar;
    @FXML
    private Button btn_Cancelar;

    private Runnable onGuardar;
    private Runnable onCancelar;
    private String modo; // Variable para almacenar el modo actual

    private final ObservableList<String> roles = FXCollections.observableArrayList(
            "Principal", "Asistente", "Apoderado", "Consultor");

    @FXML
    private void initialize() {
        // Configurar los tipos de casos y estados
        cbx_TipoCaso.getItems().addAll("Civil", "Laboral", "Penal", "Familiar");
        cbx_Estado.getItems().addAll("Abierto", "En proceso", "Archivado");

        // Establecer la fecha actual por defecto
        dt_FechaInicio.setValue(java.time.LocalDate.now());

        // Generar y establecer número de expediente automáticamente
        generarNumeroExpediente();

        configurarTabla();
        cargarAbogadosEjemplo();

        btn_Guardar.setOnAction(e -> {
            if (modo != null && "EDITAR".equals(modo)) {
                // En modo edición, solo verificamos que el estado esté seleccionado
                if (cbx_Estado.getValue() == null) {
                    DialogUtil.mostrarDialogo(
                            "Campo requerido",
                            "Por favor, seleccione el estado del caso.",
                            "warning",
                            List.of(ButtonType.OK));
                    return;
                }

                Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                        "Confirmación",
                        "¿Está seguro que desea actualizar el estado de este caso?",
                        "confirm",
                        List.of(ButtonType.YES, ButtonType.NO));

                if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
                    // Actualizar solo el estado del caso en la base de datos
                    if (actualizarEstadoCasoEnBaseDeDatos()) {
                        // Si la actualización fue exitosa, cerrar el formulario
                        if (onGuardar != null)
                            onGuardar.run();
                    }
                }
            } else {
                // Modo nuevo caso - verificar todos los campos requeridos
                if (txtf_TituloCaso.getText().isEmpty()
                        || cbx_TipoCaso.getValue() == null || cbx_Estado.getValue() == null
                        || txtf_IdentificacionCliente.getText().isEmpty()) {
                    DialogUtil.mostrarDialogo(
                            "Campos requeridos",
                            "Por favor, complete los campos obligatorios: \n - Título del Caso \n - Tipo de Caso \n - Estado del Caso \n - Identificación del Cliente",
                            "warning",
                            List.of(ButtonType.OK));
                    return;
                }

                Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                        "Confirmación",
                        "¿Está seguro que desea guardar este caso?",
                        "confirm",
                        List.of(ButtonType.YES, ButtonType.NO));

                if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
                    // Guardar el caso en la base de datos
                    if (guardarCasoEnBaseDeDatos()) {
                        // Si el guardado fue exitoso, cerrar el formulario
                        if (onGuardar != null)
                            onGuardar.run();
                    }
                }
            }
        });

        btn_Cancelar.setOnAction(e -> {
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

    public void setOnGuardar(Runnable handler) {
        this.onGuardar = handler;
    }

    public void setOnCancelar(Runnable handler) {
        this.onCancelar = handler;
    }

    public void cargarDatosCaso(String numeroExpediente, String titulo, String tipo, String fecha, String estado,
            String descripcion) {
        System.out.println("INFO: Cargando datos del caso: " + numeroExpediente);

        // El número de expediente se establece pero no es editable
        txtf_NumeroExpediente.setText(numeroExpediente);
        txtf_NumeroExpediente.setEditable(false);

        txtf_TituloCaso.setText(titulo);
        cbx_TipoCaso.setValue(tipo);
        cbx_Estado.setValue(estado);

        // Establecer la descripción si está disponible
        if (descripcion != null) {
            txtb_DescripcionCaso.setText(descripcion);
        }

        // Buscar el cliente asociado a este caso para obtener su identificación
        try {
            System.out.println("DEBUG: Buscando identificación del cliente para caso: " + numeroExpediente);
            java.sql.Connection conn = application.database.DatabaseConnection.getConnection();
            if (conn != null) {
                // Primero verificamos si existe la tabla cliente
                String checkTableSql = "SELECT name FROM sqlite_master WHERE type='table' AND name='cliente'";
                java.sql.PreparedStatement checkStmt = conn.prepareStatement(checkTableSql);
                java.sql.ResultSet checkRs = checkStmt.executeQuery();

                boolean tablaClienteExiste = checkRs.next();
                checkRs.close();
                checkStmt.close();

                if (tablaClienteExiste) {
                    // La tabla existe, podemos continuar con la consulta
                    // Obtenemos el cliente_id de la tabla caso primero
                    String sql = "SELECT cliente_id FROM caso WHERE numero_expediente = ?";
                    java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, numeroExpediente);
                    java.sql.ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        int clienteId = rs.getInt("cliente_id");
                        rs.close();
                        stmt.close();

                        // Ahora buscamos la identificación del cliente usando el cliente_id
                        sql = "SELECT numero_identificacion FROM cliente WHERE id = ?";
                        stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, clienteId);
                        rs = stmt.executeQuery();

                        if (rs.next()) {
                            String identificacionCliente = rs.getString("numero_identificacion");
                            txtf_IdentificacionCliente.setText(identificacionCliente);
                            System.out.println("INFO: Cargada identificación del cliente: " + identificacionCliente);
                        } else {
                            System.out
                                    .println("WARN: No se encontró la identificación para el cliente id: " + clienteId);
                            txtf_IdentificacionCliente.setText("[Cliente ID: " + clienteId + "]");
                        }

                        rs.close();
                        stmt.close();
                    } else {
                        System.out.println("WARN: No se encontró el cliente_id para el caso: " + numeroExpediente);
                        txtf_IdentificacionCliente.setText("[Cliente no asignado]");
                    }
                } else {
                    System.out.println("WARN: La tabla 'cliente' no existe en la base de datos");
                    // Diagnóstico adicional - listar tablas existentes
                    try {
                        System.out.println("DEBUG: Listando tablas existentes en la base de datos:");
                        String listTablesSql = "SELECT name FROM sqlite_master WHERE type='table'";
                        java.sql.Statement listStmt = conn.createStatement();
                        java.sql.ResultSet listRs = listStmt.executeQuery(listTablesSql);

                        while (listRs.next()) {
                            System.out.println(" - " + listRs.getString("name"));
                        }

                        listRs.close();
                        listStmt.close();
                    } catch (Exception ex) {
                        System.err.println("ERROR al listar tablas: " + ex.getMessage());
                    }

                    // Como no podemos obtener la identificación, dejamos el campo vacío o con un
                    // mensaje
                    txtf_IdentificacionCliente.setText("[No disponible - Tabla cliente no existe]");
                }

                conn.close();
            }
        } catch (Exception e) {
            System.err.println("ERROR al cargar identificación del cliente: " + e.getMessage());
            e.printStackTrace();

            // Información adicional para diagnóstico
            System.out.println("DEBUG: Número de expediente que causó el error: " + numeroExpediente);

            // En caso de error, dejamos el campo con un mensaje indicando el problema
            txtf_IdentificacionCliente.setText("[Error: " + e.getMessage() + "]");
        }

        if (fecha != null && !fecha.isEmpty()) {
            try {
                // Intentar parsear la fecha que podría venir en formato dd/MM/yyyy
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                        .ofPattern("dd/MM/yyyy");
                dt_FechaInicio.setValue(java.time.LocalDate.parse(fecha, formatter));
            } catch (Exception e) {
                // Si hay error, intentar con formato ISO
                try {
                    dt_FechaInicio.setValue(java.time.LocalDate.parse(fecha));
                } catch (Exception ex) {
                    System.err.println("No se pudo parsear la fecha: " + fecha);
                    dt_FechaInicio.setValue(null);
                }
            }
        } else {
            dt_FechaInicio.setValue(null);
        }
    }

    public void setModo(String modo) {
        this.modo = modo; // Guardar el modo actual
        boolean esNuevo = "NUEVO".equals(modo);
        boolean esEditar = "EDITAR".equals(modo);
        boolean esVer = "VER".equals(modo);

        if (esNuevo) {
            txt_TituloForm.setText("Registrar nuevo Caso");
        } else if (esEditar) {
            txt_TituloForm.setText("Editar Estado del Caso");
        } else if (esVer) {
            txt_TituloForm.setText("Visualizar Caso");
        }

        // El número de expediente nunca es editable, se genera automáticamente
        txtf_NumeroExpediente.setEditable(false);

        // En modo editar, solo el estado es editable
        if (esEditar) {
            txtf_TituloCaso.setEditable(false);
            txtf_IdentificacionCliente.setEditable(false);
            cbx_TipoCaso.setDisable(true);
            cbx_Estado.setDisable(false); // Solo el estado es editable
            dt_FechaInicio.setDisable(true);
            txtb_DescripcionCaso.setEditable(false);
            tb_Abogados.setDisable(true);
        } else {
            txtf_TituloCaso.setEditable(esNuevo);
            txtf_IdentificacionCliente.setEditable(esNuevo);
            cbx_TipoCaso.setDisable(!esNuevo || esVer);
            cbx_Estado.setDisable(esVer);
            dt_FechaInicio.setDisable(!esNuevo || esVer);
            txtb_DescripcionCaso.setEditable(esNuevo);
            tb_Abogados.setDisable(!esNuevo || esVer);
        }

        btn_Guardar.setVisible(!esVer);
        btn_Guardar.setDisable(esVer);
    }

    private void configurarTabla() {
        tbc_CheckBoton.setCellFactory(CheckBoxTableCell.forTableColumn(tbc_CheckBoton));
        tbc_CheckBoton.setCellValueFactory(data -> data.getValue().asignadoProperty());

        tbc_Nombres.setCellValueFactory(data -> data.getValue().nombresProperty());
        tbc_Apellidos.setCellValueFactory(data -> data.getValue().apellidosProperty());
        tbc_Cedula.setCellValueFactory(data -> data.getValue().cedulaProperty());
        tbc_Cedula.setStyle("-fx-alignment: CENTER-LEFT;"); // Align cedula left

        tbc_Rol_ChekBox.setCellFactory(ComboBoxTableCell.forTableColumn(roles));
        tbc_Rol_ChekBox.setCellValueFactory(data -> data.getValue().rolProperty());
        tbc_Rol_ChekBox.setOnEditCommit(event -> {
            event.getRowValue().rolProperty().set(event.getNewValue());
        });

        tb_Abogados.setEditable(true);
    }

    private void cargarAbogadosEjemplo() {
        tb_Abogados.setItems(FXCollections.observableArrayList(
                new AbogadoDemo("Andrea", "Salinas", "12345678", "Elegir rol", false),
                new AbogadoDemo("José", "Ruiz", "87654321", "Elegir rol", false),
                new AbogadoDemo("María", "León", "11223344", "Elegir rol", false)));
    }

    /**
     * Guarda el caso en la base de datos.
     * 
     * @return true si el guardado fue exitoso, false en caso contrario
     */
    private boolean guardarCasoEnBaseDeDatos() {
        try {
            // Obtener los datos del formulario
            String numeroExpediente = txtf_NumeroExpediente.getText();
            String titulo = txtf_TituloCaso.getText();
            String tipo = cbx_TipoCaso.getValue();
            String estado = cbx_Estado.getValue();
            String descripcion = txtb_DescripcionCaso.getText();
            java.util.Date fechaInicio = java.sql.Date.valueOf(dt_FechaInicio.getValue());
            String identificacionCliente = txtf_IdentificacionCliente.getText();

            // Verificar si la tabla cliente existe antes de buscar el cliente
            java.sql.Connection checkConn = application.database.DatabaseConnection.getConnection();
            if (checkConn == null) {
                throw new Exception("Error al conectar con la base de datos");
            }

            // Verificar si la tabla cliente existe
            String checkTableSql = "SELECT name FROM sqlite_master WHERE type='table' AND name='cliente'";
            java.sql.PreparedStatement checkStmt = checkConn.prepareStatement(checkTableSql);
            java.sql.ResultSet checkRs = checkStmt.executeQuery();

            boolean tablaClienteExiste = checkRs.next();
            checkRs.close();
            checkStmt.close();
            checkConn.close();

            if (!tablaClienteExiste) {
                DialogUtil.mostrarDialogo("Error",
                        "La tabla 'cliente' no existe en la base de datos. No se puede continuar.",
                        "error",
                        List.of(ButtonType.OK));
                return false;
            }

            // Buscar el cliente por su identificación
            application.dao.ClienteDAO clienteDAO = new application.dao.ClienteDAO();
            application.model.Cliente cliente = clienteDAO.obtenerClientePorIdentificacion(identificacionCliente);

            if (cliente == null) {
                DialogUtil.mostrarDialogo("Error",
                        "No se encontró un cliente con la identificación: " + identificacionCliente,
                        "error",
                        List.of(ButtonType.OK));
                return false;
            }

            // Crear el objeto Caso
            application.model.Caso caso = new application.model.Caso();
            caso.setNumeroExpediente(numeroExpediente);
            caso.setTitulo(titulo);
            caso.setTipo(tipo);
            caso.setEstado(estado);
            caso.setDescripcion(descripcion);
            caso.setFechaInicio(fechaInicio);
            caso.setClienteId(cliente.getId());

            // Guardar el caso en la base de datos
            try (java.sql.Connection conn = application.database.DatabaseConnection.getConnection()) {
                application.service.CasoService casoService = new application.service.CasoService(conn);
                casoService.registrarCaso(caso);

                // Imprimir información para depuración
                System.out.println("INFO: Caso guardado exitosamente:");
                System.out.println("  Expediente: " + numeroExpediente);
                System.out.println("  Título: " + titulo);
                System.out.println("  Cliente ID: " + cliente.getId());

                // Mostrar mensaje de éxito
                DialogUtil.mostrarDialogo("Éxito",
                        "El caso ha sido guardado correctamente.",
                        "info",
                        List.of(ButtonType.OK));

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.mostrarDialogo("Error",
                    "Ocurrió un error al guardar el caso: " + e.getMessage(),
                    "error",
                    List.of(ButtonType.OK));
            return false;
        }
    }

    /**
     * Actualiza solo el estado del caso en la base de datos.
     * 
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    private boolean actualizarEstadoCasoEnBaseDeDatos() {
        try {
            // Obtener los datos necesarios del formulario
            String numeroExpediente = txtf_NumeroExpediente.getText();
            String estado = cbx_Estado.getValue();

            System.out.println("INFO: Actualizando estado del caso " + numeroExpediente + " a: " + estado);

            // Inicializar el servicio
            CasoService casoService = new CasoService();

            // Actualizar el estado del caso en la base de datos
            boolean actualizado = casoService.actualizarEstadoCaso(numeroExpediente, estado);

            if (actualizado) {
                // Mostrar mensaje de éxito
                System.out.println("INFO: Estado del caso actualizado exitosamente");
                DialogUtil.mostrarDialogo("Éxito",
                        "El estado del caso ha sido actualizado correctamente.",
                        "info",
                        List.of(ButtonType.OK));
                return true;
            } else {
                System.err.println("ERROR: No se pudo actualizar el estado. No se encontró el caso con expediente: "
                        + numeroExpediente);
                DialogUtil.mostrarDialogo("Error",
                        "No se pudo actualizar el estado del caso. Verifique el número de expediente.",
                        "error",
                        List.of(ButtonType.OK));
                return false;
            }
        } catch (Exception e) {
            System.err.println("ERROR GRAVE al actualizar estado del caso: " + e.getMessage());
            e.printStackTrace();

            // Información detallada del error
            System.err.println("Detalles del caso que causó el error:");
            System.err.println(" - Número de expediente: " + txtf_NumeroExpediente.getText());
            System.err.println(" - Estado a actualizar: " + cbx_Estado.getValue());

            DialogUtil.mostrarDialogo("Error",
                    "Ocurrió un error al actualizar el estado del caso: " + e.getMessage(),
                    "error",
                    List.of(ButtonType.OK));
            return false;
        }
    }

    // Inner class for demo purposes
    public static class AbogadoDemo {
        private final StringProperty nombres;
        private final StringProperty apellidos;
        private final StringProperty cedula;
        private final StringProperty rol;
        private final BooleanProperty asignado;

        public AbogadoDemo(String nombres, String apellidos, String cedula, String rol, boolean asignado) {
            this.nombres = new SimpleStringProperty(nombres);
            this.apellidos = new SimpleStringProperty(apellidos);
            this.cedula = new SimpleStringProperty(cedula);
            this.rol = new SimpleStringProperty(rol);
            this.asignado = new SimpleBooleanProperty(asignado);
        }

        public StringProperty nombresProperty() {
            return nombres;
        }

        public StringProperty apellidosProperty() {
            return apellidos;
        }

        public StringProperty cedulaProperty() {
            return cedula;
        }

        public StringProperty rolProperty() {
            return rol;
        }

        public BooleanProperty asignadoProperty() {
            return asignado;
        }
    }

    /**
     * Genera un número de expediente automáticamente basado en el último caso en la
     * base de datos
     */
    private void generarNumeroExpediente() {
        try {
            System.out.println("INFO: Generando número de expediente automáticamente...");

            java.sql.Connection conn = application.database.DatabaseConnection.getConnection();
            if (conn == null) {
                throw new Exception("Error al conectar con la base de datos");
            }

            String sql = "SELECT MAX(id) as ultimo_id FROM caso";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            java.sql.ResultSet rs = stmt.executeQuery();

            int ultimoId = 0;
            if (rs.next()) {
                ultimoId = rs.getInt("ultimo_id");
                System.out.println("INFO: Último ID encontrado: " + ultimoId);
            }

            // Incrementamos en 1 para el nuevo caso
            int nuevoId = ultimoId + 1;

            // Formateamos el número con el año actual y un número secuencial
            java.time.Year anioActual = java.time.Year.now();
            String numeroExpediente = String.format("EXP-%s-%04d", anioActual.getValue(), nuevoId);

            System.out.println("INFO: Número de expediente generado: " + numeroExpediente);
            txtf_NumeroExpediente.setText(numeroExpediente);

            // Hacemos el campo de número de expediente no editable ya que se genera
            // automáticamente
            txtf_NumeroExpediente.setEditable(false);

            // Cerrar recursos
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.err.println("ERROR al generar número de expediente: " + e.getMessage());
            e.printStackTrace();

            // En caso de error, generamos un número basado en la fecha y hora actual
            String numeroExpediente = "EXP-" + java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            txtf_NumeroExpediente.setText(numeroExpediente);
            txtf_NumeroExpediente.setEditable(false);

            System.out.println("INFO: Número de expediente generado por fecha: " + numeroExpediente);
        }
    }
}