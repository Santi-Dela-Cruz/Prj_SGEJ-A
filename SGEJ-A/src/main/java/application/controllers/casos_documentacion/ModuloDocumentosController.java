package application.controllers.casos_documentacion;

import application.controllers.DialogUtil;
import application.database.DatabaseConnection;
import application.model.DocumentoCaso;
import application.service.DocumentoCasoService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

public class ModuloDocumentosController {

    @FXML
    private TextField txtf_Buscar;
    @FXML
    private ComboBox<String> cmb_CriterioBusqueda;
    @FXML
    private Button btn_Buscar, btn_Subir, btn_Regresar;
    @FXML
    private TableView<DocumentoDemo> tb_Documentos;
    @FXML
    private TableColumn<DocumentoDemo, String> tbc_Nombre, tbc_Tipo, tbc_Fecha, tbc_Tamano;
    @FXML
    private TableColumn<DocumentoDemo, Void> tbc_BotonVer, tbc_BotonEliminar, tbc_BotonDescargar;
    @FXML
    private Label lblTitulo, lbl_TotalDocumentos;

    private Pane pnl_Forms;
    private String numeroExpediente;

    public void setFormularioContainer(Pane pnl_Forms) {
        this.pnl_Forms = pnl_Forms;
    }

    public void setNumeroExpediente(String numeroExpediente) {
        this.numeroExpediente = numeroExpediente;
        // Actualizar título si existe el label
        if (lblTitulo != null) {
            lblTitulo.setText("Documentos - Expediente " + numeroExpediente);
            lblTitulo.setStyle("-fx-font-size: 16.0; -fx-font-weight: bold; -fx-text-fill: white;");
        }
        // Cargar documentos específicos del caso
        cargarDocumentosPorExpediente(numeroExpediente);
    }

    /**
     * Este método ahora ignora el callback proporcionado y siempre usa
     * MainController
     * para navegar, lo cual es más fiable.
     */
    public void setOnRegresar(Runnable callback) {
        System.out.println("DEBUG: setOnRegresar llamado - ignorando callback y usando MainController");

        // Ya no guardamos el callback ni lo usamos

        // Asegurarnos que el botón de regresar use MainController directamente
        if (btn_Regresar != null) {
            btn_Regresar.setOnAction(e -> {
                System.out.println("DEBUG: BOTÓN REGRESAR PRESIONADO (ModuloDocumentosController)");
                try {
                    System.out.println("DEBUG: Navegando a modulo_casos usando MainController");
                    application.controllers.MainController mainController = application.controllers.MainController
                            .getInstance();
                    if (mainController != null) {
                        mainController.cargarModulo("/views/casos_documentos/modulo_casos_documentacion_casos.fxml");
                        System.out.println("DEBUG: Navegación a modulo_casos exitosa");
                    } else {
                        System.err.println("ERROR: No se pudo obtener instancia de MainController");
                    }
                } catch (Exception ex) {
                    System.err.println("ERROR: Error al navegar usando MainController: " + ex.getMessage());
                    ex.printStackTrace();

                    // Intentar con una ruta alternativa
                    try {
                        System.out.println("DEBUG: Intentando ruta alternativa desde setOnRegresar");
                        application.controllers.MainController mainController = application.controllers.MainController
                                .getInstance();
                        if (mainController != null) {
                            mainController.cargarModulo("views/casos_documentos/modulo_casos_documentacion_casos.fxml");
                            System.out.println("DEBUG: Navegación con ruta alternativa exitosa");
                        }
                    } catch (Exception e2) {
                        System.err.println("ERROR: También falló la ruta alternativa: " + e2.getMessage());
                    }
                }
            });
            System.out.println("DEBUG: btn_Regresar configurado exitosamente");
        } else {
            System.err.println("ERROR: btn_Regresar es NULL en setOnRegresar");
        }
    }

    @FXML
    private void initialize() {
        System.out.println("DEBUG: initialize en ModuloDocumentosController");

        // Configurar el botón regresar para usar siempre MainController
        // (mucho más confiable que el sistema de callbacks)
        if (btn_Regresar != null) {
            btn_Regresar.setOnAction(e -> {
                System.out.println("DEBUG: BOTÓN REGRESAR PRESIONADO (initialize en ModuloDocumentosController)");
                try {
                    System.out.println("DEBUG: Navegando a modulo_casos usando MainController");
                    application.controllers.MainController mainController = application.controllers.MainController
                            .getInstance();
                    if (mainController != null) {
                        mainController.cargarModulo("/views/casos_documentos/modulo_casos_documentacion_casos.fxml");
                        System.out.println("DEBUG: Navegación a modulo_casos exitosa");
                    } else {
                        System.err.println("ERROR: No se pudo obtener instancia de MainController");
                    }
                } catch (Exception ex) {
                    System.err.println("ERROR: Error al navegar usando MainController: " + ex.getMessage());
                    ex.printStackTrace();

                    // Intentar con una ruta alternativa si la primera falló
                    try {
                        System.out.println("DEBUG: Intentando ruta alternativa");
                        application.controllers.MainController mainController = application.controllers.MainController
                                .getInstance();
                        if (mainController != null) {
                            // Intentar sin el slash inicial
                            mainController.cargarModulo("views/casos_documentos/modulo_casos_documentacion_casos.fxml");
                            System.out.println("DEBUG: Navegación con ruta alternativa exitosa");
                        }
                    } catch (Exception e2) {
                        System.err.println("ERROR: También falló la ruta alternativa: " + e2.getMessage());
                        e2.printStackTrace();
                    }
                }
            });
        } else {
            System.err.println("ERROR: btn_Regresar es NULL en initialize");
        }

        // Inicializar ComboBox con única opción de búsqueda
        if (cmb_CriterioBusqueda != null) {
            cmb_CriterioBusqueda.getItems().add("Nombre del documento");
            cmb_CriterioBusqueda.setValue("Nombre del documento");

            // Establecer el prompt del TextField para búsqueda por nombre
            txtf_Buscar.setPromptText("🔍 Buscar por nombre del documento...");

            // Desactivar el ComboBox ya que solo hay una opción
            cmb_CriterioBusqueda.setDisable(true);
        } else {
            System.err.println("ERROR: cmb_CriterioBusqueda es NULL en initialize");
        }

        // Configurar botón de búsqueda
        if (btn_Buscar != null) {
            btn_Buscar.setOnAction(e -> realizarBusqueda());
        }

        // Configurar botón subir documento
        if (btn_Subir != null) {
            btn_Subir.setOnAction(event -> mostrarFormularioDocumento());
        }

        configurarColumnas();
        inicializarColumnasDeBotones();
        cargarDatosEjemplo();
    }

    /**
     * Realiza la búsqueda por nombre del documento
     */
    private void realizarBusqueda() {
        if (txtf_Buscar.getText().trim().isEmpty()) {
            DialogUtil.mostrarDialogo(
                    "Búsqueda Vacía",
                    "Por favor, ingrese un nombre de documento para buscar.",
                    "info",
                    List.of(ButtonType.OK));
            return;
        }

        String termino = txtf_Buscar.getText().trim();
        System.out.println("Realizando búsqueda por nombre de documento: " + termino);

        try {
            // Si estamos en modo demo, usar datos de ejemplo filtrados
            if (numeroExpediente == null || numeroExpediente.isEmpty()) {
                buscarDocumentosDemo("Nombre del documento", termino);
                return;
            }

            // Buscar documentos por nombre
            buscarDocumentosPorCriterio("Nombre del documento", termino);

        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.mostrarDialogo(
                    "Error en la Búsqueda",
                    "Ocurrió un error al procesar su búsqueda: " + e.getMessage(),
                    "error",
                    List.of(ButtonType.OK));
        }
    }

    private void buscarDocumentosDemo(String criterio, String termino) {
        // Filtrar los datos demo según el criterio y término
        List<DocumentoDemo> resultados = tb_Documentos.getItems().stream()
                .filter(doc -> coincideConCriterio(doc, criterio, termino))
                .toList();

        tb_Documentos.getItems().clear();
        tb_Documentos.getItems().addAll(resultados);

        // Actualizar contador de documentos
        if (lbl_TotalDocumentos != null) {
            lbl_TotalDocumentos.setText("Total: " + resultados.size() + " documentos");
        }

        if (resultados.isEmpty()) {
            DialogUtil.mostrarDialogo(
                    "Documento no encontrado",
                    "No se encontró ningún documento con el nombre \"" + termino + "\".",
                    "error",
                    List.of(ButtonType.OK));
        }
    }

    private boolean coincideConCriterio(DocumentoDemo doc, String criterio, String termino) {
        termino = termino.toLowerCase();
        // Solo buscaremos por nombre del documento
        return doc.nombre().toLowerCase().contains(termino);
    }

    private void buscarDocumentosPorIdentificacion(String identificacion) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Primero obtenemos los casos asociados a la identificación
            String sqlCasos = "SELECT c.numero_expediente FROM caso c " +
                    "JOIN cliente cl ON c.cliente_id = cl.id " +
                    "WHERE cl.identificacion = ?";

            List<String> expedientesEncontrados = new java.util.ArrayList<>();

            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sqlCasos)) {
                stmt.setString(1, identificacion);
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        expedientesEncontrados.add(rs.getString("numero_expediente"));
                    }
                }
            }

            if (expedientesEncontrados.isEmpty()) {
                DialogUtil.mostrarDialogo(
                        "Sin Resultados",
                        "No se encontraron casos asociados al número de identificación: " + identificacion,
                        "info",
                        List.of(ButtonType.OK));
                return;
            }

            // Para cada expediente, cargamos sus documentos
            List<DocumentoDemo> todosDocumentos = new java.util.ArrayList<>();
            for (String expediente : expedientesEncontrados) {
                // Obtenemos los documentos para este expediente
                try {
                    List<DocumentoDemo> docsExpediente = obtenerDocumentosPorExpediente(expediente);
                    if (docsExpediente != null) {
                        todosDocumentos.addAll(docsExpediente);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(
                            "Error al obtener documentos del expediente " + expediente + ": " + e.getMessage());
                }
            }

            // Actualizar la tabla con todos los documentos encontrados
            tb_Documentos.getItems().clear();
            tb_Documentos.getItems().addAll(todosDocumentos);

            // Actualizar contador
            if (lbl_TotalDocumentos != null) {
                lbl_TotalDocumentos.setText("Total: " + todosDocumentos.size() + " documentos");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            DialogUtil.mostrarDialogo(
                    "Error de Base de Datos",
                    "Ocurrió un error al buscar documentos: " + e.getMessage(),
                    "error",
                    List.of(ButtonType.OK));
        }
    }

    private void buscarDocumentosPorCriterio(String criterio, String termino) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();

            String columnaBusqueda = switch (criterio) {
                case "Nombre del documento" -> "nombre";
                case "Tipo de documento" -> "tipo";
                case "Fecha" -> "fecha_creacion";
                default -> "nombre";
            };

            // Obtenemos el caso_id correspondiente al número de expediente actual usando la
            // misma conexión
            int casoId = -1;
            String sqlCaso = "SELECT id FROM caso WHERE numero_expediente = ?";
            try (java.sql.PreparedStatement stmtCaso = conn.prepareStatement(sqlCaso)) {
                stmtCaso.setString(1, numeroExpediente);
                try (java.sql.ResultSet rsCaso = stmtCaso.executeQuery()) {
                    if (rsCaso.next()) {
                        casoId = rsCaso.getInt("id");
                    }
                }
            }

            if (casoId == -1) {
                DialogUtil.mostrarDialogo(
                        "Error",
                        "No se pudo encontrar el caso con expediente: " + numeroExpediente,
                        "error",
                        List.of(ButtonType.OK));
                return;
            }

            // Consulta SQL con filtro por columna dinámica
            String sql = "SELECT * FROM documento_caso WHERE caso_id = ? AND " + columnaBusqueda + " LIKE ?";
            List<DocumentoDemo> resultados = new java.util.ArrayList<>();

            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, casoId);
                stmt.setString(2, "%" + termino + "%");

                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        resultados.add(new DocumentoDemo(
                                rs.getString("nombre"),
                                rs.getString("tipo"),
                                formatearFecha(rs.getDate("fecha_creacion")),
                                formatearTamano(rs.getLong("tamano")),
                                numeroExpediente));
                    }
                }
            }

            // Actualizar la tabla con los resultados
            tb_Documentos.getItems().clear();
            tb_Documentos.getItems().addAll(resultados);

            // Actualizar contador
            if (lbl_TotalDocumentos != null) {
                lbl_TotalDocumentos.setText("Total: " + resultados.size() + " documentos");
            }

            if (resultados.isEmpty()) {
                DialogUtil.mostrarDialogo(
                        "Documento no encontrado",
                        "No se encontró ningún documento con el nombre \"" + termino + "\".",
                        "error",
                        List.of(ButtonType.OK));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            DialogUtil.mostrarDialogo(
                    "Error de Base de Datos",
                    "Ocurrió un error al buscar documentos: " + e.getMessage(),
                    "error",
                    List.of(ButtonType.OK));
        } finally {
            // Asegurarse de cerrar la conexión en cualquier caso
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }

    // El método obtenerCasoIdPorExpediente fue eliminado ya que su funcionalidad
    // se integró directamente en buscarDocumentosPorCriterio para evitar problemas
    // de conexión

    private String formatearFecha(java.sql.Date fecha) {
        if (fecha == null)
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(fecha);
    }

    private String formatearTamano(long tamano) {
        if (tamano < 1024)
            return tamano + " bytes";
        else if (tamano < 1024 * 1024)
            return String.format("%.2f KB", tamano / 1024.0);
        else
            return String.format("%.2f MB", tamano / (1024.0 * 1024.0));
    }

    /**
     * Obtiene la lista de documentos asociados a un número de expediente
     * 
     * @param expediente el número de expediente
     * @return la lista de documentos asociados al expediente
     */
    private List<DocumentoDemo> obtenerDocumentosPorExpediente(String expediente) {
        List<DocumentoDemo> documentos = new java.util.ArrayList<>();

        if (expediente == null || expediente.isEmpty()) {
            return documentos;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Obtener el ID del caso desde el número de expediente
            String sqlCaso = "SELECT id FROM caso WHERE numero_expediente = ?";
            int casoId = -1;

            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sqlCaso)) {
                stmt.setString(1, expediente);
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        casoId = rs.getInt("id");
                    }
                }
            }

            if (casoId == -1) {
                System.out.println("ADVERTENCIA: No se encontró el caso con número de expediente: " + expediente);
                return documentos;
            }

            // Obtener documentos de ese caso
            DocumentoCasoService service = new DocumentoCasoService(conn);
            List<DocumentoCaso> docsDB = service.obtenerDocumentosPorCaso(casoId);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            for (DocumentoCaso doc : docsDB) {
                // Obtener el tamaño del archivo
                String tamano = "N/A";
                File archivo = new File(doc.getRuta());
                if (archivo.exists()) {
                    long sizeInBytes = archivo.length();
                    tamano = formatearTamano(sizeInBytes);
                }

                // Determinar el tipo de documento
                String tipo = determinarTipoDocumento(doc.getNombre());

                documentos.add(new DocumentoDemo(
                        expediente,
                        doc.getNombre(),
                        tipo,
                        dateFormat.format(doc.getFechaSubida()),
                        tamano));
            }

            return documentos;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener documentos para expediente " + expediente + ": " + e.getMessage());
            return documentos;
        }
    }

    /**
     * Determina el tipo de documento a partir del nombre
     * 
     * @param nombre el nombre del documento
     * @return el tipo del documento
     */
    private String determinarTipoDocumento(String nombre) {
        // Por extensión
        if (nombre.toLowerCase().endsWith(".pdf"))
            return "PDF";
        if (nombre.toLowerCase().endsWith(".doc") || nombre.toLowerCase().endsWith(".docx"))
            return "Word";
        if (nombre.toLowerCase().endsWith(".xls") || nombre.toLowerCase().endsWith(".xlsx"))
            return "Excel";
        if (nombre.toLowerCase().endsWith(".jpg") || nombre.toLowerCase().endsWith(".jpeg") ||
                nombre.toLowerCase().endsWith(".png") || nombre.toLowerCase().endsWith(".gif"))
            return "Imagen";

        // Por tipo en paréntesis, si existe
        if (nombre.contains("(") && nombre.contains(")")) {
            int inicioTipo = nombre.lastIndexOf("(") + 1;
            int finTipo = nombre.lastIndexOf(")");
            if (inicioTipo < finTipo) {
                return nombre.substring(inicioTipo, finTipo);
            }
        }

        return "Otro";
    }

    private void mostrarFormularioDocumento() {
        try {
            // Crear panel para formulario si no existe
            if (pnl_Forms == null) {
                pnl_Forms = new AnchorPane();

                // Añadimos el panel sobrepuesto al panel principal
                AnchorPane panelPrincipal = (AnchorPane) tb_Documentos.getParent();
                panelPrincipal.getChildren().add(pnl_Forms);

                // Inicialmente oculto
                pnl_Forms.setVisible(false);
                pnl_Forms.setManaged(false);
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/casos_documentos/form_documento.fxml"));
            AnchorPane form = loader.load();

            FormDocumentoController controller = loader.getController();

            // Configurar callbacks
            controller.setOnCancelar(() -> cerrarFormulario());
            controller.setOnGuardar(() -> {
                cerrarFormulario();
                // Recargar documentos después de guardar
                if (numeroExpediente != null) {
                    cargarDocumentosPorExpediente(numeroExpediente);
                }
            });

            // Asignar automáticamente el número de expediente si existe
            if (numeroExpediente != null && !numeroExpediente.isEmpty()) {
                controller.asignarExpediente(numeroExpediente);
            }

            // Configurar el formulario para nuevo documento
            controller.setModo("NUEVO");

            // Posicionar el formulario (desde fuera de la pantalla)
            pnl_Forms.getChildren().setAll(form);

            // Posicionamos el formulario en el borde derecho
            AnchorPane.setTopAnchor(pnl_Forms, 60.0); // Espacio para el título
            AnchorPane.setRightAnchor(pnl_Forms, 30.0); // Margen desde la derecha
            AnchorPane.setLeftAnchor(pnl_Forms, null); // Importante: quitar el anclaje izquierdo
            AnchorPane.setBottomAnchor(pnl_Forms, 30.0); // Margen desde abajo

            // Hacer el panel visible
            pnl_Forms.setVisible(true);
            pnl_Forms.setManaged(true);

            // Efecto de animación (desplazamiento desde la derecha)
            javafx.animation.TranslateTransition tt = new javafx.animation.TranslateTransition(
                    javafx.util.Duration.millis(250), form);
            form.setTranslateX(400); // Comienza fuera de la pantalla
            tt.setToX(0);
            tt.play();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar el formulario de documento: " + e.getMessage());
        }
    }

    private void cerrarFormulario() {
        if (pnl_Forms != null && !pnl_Forms.getChildren().isEmpty()) {
            Node form = pnl_Forms.getChildren().get(0);

            // Animación de salida
            javafx.animation.TranslateTransition tt = new javafx.animation.TranslateTransition(
                    javafx.util.Duration.millis(200), form);
            tt.setToX(400); // Sale por la derecha
            tt.setOnFinished(e -> {
                pnl_Forms.getChildren().clear();
                pnl_Forms.setVisible(false);
                pnl_Forms.setManaged(false);

                // Recargar documentos del expediente actual
                if (numeroExpediente != null && !numeroExpediente.isEmpty()) {
                    cargarDocumentosPorExpediente(numeroExpediente);
                }
            });
            tt.play();
        } else {
            // Por si acaso no hay animación
            if (pnl_Forms != null) {
                pnl_Forms.getChildren().clear();
                pnl_Forms.setVisible(false);
                pnl_Forms.setManaged(false);

                // Recargar documentos del expediente actual
                if (numeroExpediente != null && !numeroExpediente.isEmpty()) {
                    cargarDocumentosPorExpediente(numeroExpediente);
                }
            }
        }
    }

    private void configurarColumnas() {
        tbc_Nombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nombre()));
        tbc_Tipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tipo()));
        tbc_Fecha.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fecha()));
        tbc_Tamano.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tamano()));
    }

    private void inicializarColumnasDeBotones() {
        agregarBoton(tbc_BotonVer, "👁", "Ver");
        agregarBoton(tbc_BotonEliminar, "🗑", "Eliminar");
        agregarBoton(tbc_BotonDescargar, "⬇", "Descargar");
    }

    private void agregarBoton(TableColumn<DocumentoDemo, Void> columna, String texto, String tooltip) {
        columna.setCellFactory(tc -> new TableCell<>() {
            final Button btn = new Button(texto);

            {
                btn.getStyleClass().add("table-button");
                btn.setTooltip(new Tooltip(tooltip));
                btn.setOnAction(e -> {
                    DocumentoDemo doc = getTableView().getItems().get(getIndex());
                    if ("Ver".equals(tooltip)) {
                        mostrarDocumentoParaVisualizar(doc);
                    } else if ("Eliminar".equals(tooltip)) {
                        eliminarDocumento(doc);
                    } else if ("Descargar".equals(tooltip)) {
                        descargarDocumento(doc);
                    } else {
                        System.out.println(tooltip + " documento: " + doc.nombre());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
                setStyle("-fx-alignment: CENTER;");
            }
        });
    }

    /**
     * Elimina un documento de la base de datos y del sistema de archivos
     * 
     * @param doc el documento a eliminar
     */
    private void eliminarDocumento(DocumentoDemo doc) {
        // Mensaje de confirmación más personalizado y detallado
        Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                "Confirmación de Eliminación",
                "¿Estás seguro que deseas eliminar este archivo?\n\n" +
                        "• Nombre: " + doc.nombre() + "\n" +
                        "• Tipo: " + doc.tipo() + "\n" +
                        "• Fecha: " + doc.fecha() + "\n\n" +
                        "⚠️ Esta acción no se puede deshacer y el archivo\n" +
                        "será eliminado permanentemente del sistema.",
                "warning",
                List.of(ButtonType.YES, ButtonType.NO));

        if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Primero obtener el ID del caso desde el número de expediente
                String sqlCaso = "SELECT id FROM caso WHERE numero_expediente = ?";
                int casoId = -1;

                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sqlCaso)) {
                    stmt.setString(1, doc.numeroExpediente());
                    try (java.sql.ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            casoId = rs.getInt("id");
                        }
                    }
                }

                if (casoId != -1) {
                    // Obtener la ruta del archivo antes de eliminarlo
                    String sqlRuta = "SELECT ruta FROM documento_caso WHERE caso_id = ? AND nombre = ?";
                    String rutaArchivo = null;

                    try (java.sql.PreparedStatement stmt = conn.prepareStatement(sqlRuta)) {
                        stmt.setInt(1, casoId);
                        stmt.setString(2, doc.nombre());
                        try (java.sql.ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                rutaArchivo = rs.getString("ruta");
                            }
                        }
                    }

                    // Eliminar el registro de la base de datos
                    String sqlEliminar = "DELETE FROM documento_caso WHERE caso_id = ? AND nombre = ?";
                    try (java.sql.PreparedStatement stmt = conn.prepareStatement(sqlEliminar)) {
                        stmt.setInt(1, casoId);
                        stmt.setString(2, doc.nombre());
                        int filasAfectadas = stmt.executeUpdate();

                        if (filasAfectadas > 0) {
                            System.out.println("Documento eliminado de la BD: " + doc.nombre());

                            // Eliminar el archivo físico si existe
                            if (rutaArchivo != null && !rutaArchivo.isEmpty()) {
                                File archivo = new File(rutaArchivo);
                                if (archivo.exists() && archivo.isFile()) {
                                    if (archivo.delete()) {
                                        System.out.println("Archivo físico eliminado: " + rutaArchivo);
                                    } else {
                                        System.err.println("No se pudo eliminar el archivo físico: " + rutaArchivo);
                                    }
                                }
                            }

                            // Mostrar mensaje de éxito
                            DialogUtil.mostrarDialogo(
                                    "Éxito",
                                    "Documento eliminado correctamente.",
                                    "info",
                                    List.of(ButtonType.OK));

                            // Actualizar la tabla
                            cargarDocumentosPorExpediente(doc.numeroExpediente());
                        } else {
                            System.err.println("No se encontró el documento en la BD: " + doc.nombre());
                            DialogUtil.mostrarDialogo(
                                    "Error",
                                    "No se pudo eliminar el documento.",
                                    "error",
                                    List.of(ButtonType.OK));
                        }
                    }
                } else {
                    System.err.println("No se encontró el caso: " + doc.numeroExpediente());
                    DialogUtil.mostrarDialogo(
                            "Error",
                            "No se encontró el caso asociado al documento.",
                            "error",
                            List.of(ButtonType.OK));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error al eliminar documento: " + e.getMessage());
                DialogUtil.mostrarDialogo(
                        "Error",
                        "Error al eliminar el documento: " + e.getMessage(),
                        "error",
                        List.of(ButtonType.OK));
            }
        }
    }

    /**
     * Descarga un documento al sistema de archivos del usuario
     * 
     * @param doc el documento a descargar
     */
    private void descargarDocumento(DocumentoDemo doc) {
        try {
            // Obtener la ruta del archivo
            String rutaArchivo = obtenerRutaArchivoDesdeNombre(doc.numeroExpediente(), doc.nombre());

            if (rutaArchivo.isEmpty()) {
                DialogUtil.mostrarDialogo(
                        "Error",
                        "No se encontró el archivo físico del documento.",
                        "error",
                        List.of(ButtonType.OK));
                return;
            }

            File archivoOrigen = new File(rutaArchivo);
            if (!archivoOrigen.exists() || !archivoOrigen.isFile()) {
                DialogUtil.mostrarDialogo(
                        "Error",
                        "El archivo no existe en el sistema.",
                        "error",
                        List.of(ButtonType.OK));
                return;
            }

            // Mostrar diálogo para seleccionar destino
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Guardar documento");

            // Extraer el nombre original del archivo
            String nombreArchivo = archivoOrigen.getName();
            if (nombreArchivo.contains("_")) {
                // Si tiene formato timestamp_nombre.ext, obtener solo la parte del nombre
                nombreArchivo = nombreArchivo.substring(nombreArchivo.indexOf("_") + 1);
            }

            fileChooser.setInitialFileName(nombreArchivo);

            // Configurar filtros según el tipo de documento
            String extension = "";
            int puntoIndex = nombreArchivo.lastIndexOf('.');
            if (puntoIndex > 0) {
                extension = nombreArchivo.substring(puntoIndex + 1).toLowerCase();

                // Añadir filtro para este tipo de archivo
                String descripcion = "Archivos " + extension.toUpperCase();
                fileChooser.getExtensionFilters().add(
                        new javafx.stage.FileChooser.ExtensionFilter(descripcion, "*." + extension));
            }

            // Añadir filtro para todos los archivos
            fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("Todos los archivos", "*.*"));

            // Mostrar diálogo de guardar
            javafx.stage.Window window = btn_Buscar.getScene().getWindow();
            File archivoDestino = fileChooser.showSaveDialog(window);

            if (archivoDestino != null) {
                // Copiar el archivo
                try {
                    Files.copy(archivoOrigen.toPath(), archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    DialogUtil.mostrarDialogo(
                            "Éxito",
                            "Documento descargado correctamente en:\n" + archivoDestino.getAbsolutePath(),
                            "info",
                            List.of(ButtonType.OK));
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Error al copiar archivo: " + e.getMessage());

                    DialogUtil.mostrarDialogo(
                            "Error",
                            "Error al descargar el documento: " + e.getMessage(),
                            "error",
                            List.of(ButtonType.OK));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al descargar documento: " + e.getMessage());

            DialogUtil.mostrarDialogo(
                    "Error",
                    "Error al procesar la descarga: " + e.getMessage(),
                    "error",
                    List.of(ButtonType.OK));
        }
    }

    /**
     * Abre el documento para visualizar usando el programa predeterminado del
     * sistema
     * 
     * @param doc el documento a visualizar
     */
    private void mostrarDocumentoParaVisualizar(DocumentoDemo doc) {
        try {
            // Obtener la ruta del archivo
            String rutaArchivo = obtenerRutaArchivoDesdeNombre(doc.numeroExpediente(), doc.nombre());

            if (rutaArchivo.isEmpty()) {
                DialogUtil.mostrarDialogo(
                        "Error",
                        "No se encontró el archivo físico del documento.",
                        "error",
                        List.of(ButtonType.OK));
                return;
            }

            File archivo = new File(rutaArchivo);
            if (!archivo.exists() || !archivo.isFile()) {
                DialogUtil.mostrarDialogo(
                        "Error",
                        "El archivo no existe en el sistema.",
                        "error",
                        List.of(ButtonType.OK));
                return;
            }

            // Abrir el archivo con el programa predeterminado del sistema operativo
            try {
                // Verificamos si el sistema operativo es compatible
                if (java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

                    if (desktop.isSupported(java.awt.Desktop.Action.OPEN)) {
                        desktop.open(archivo);
                        System.out.println("Archivo abierto con aplicación predeterminada: " + rutaArchivo);
                    } else {
                        // Si no es compatible con la acción OPEN, usamos alternativa
                        abrirArchivoConProceso(archivo);
                    }
                } else {
                    // Si Desktop no es compatible, usamos alternativa
                    abrirArchivoConProceso(archivo);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error al abrir el archivo: " + e.getMessage());

                DialogUtil.mostrarDialogo(
                        "Error",
                        "No se pudo abrir el archivo: " + e.getMessage() + "\n\nRuta: " + rutaArchivo,
                        "error",
                        List.of(ButtonType.OK));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al procesar el documento para visualizar: " + e.getMessage());

            DialogUtil.mostrarDialogo(
                    "Error",
                    "Error al procesar el documento: " + e.getMessage(),
                    "error",
                    List.of(ButtonType.OK));
        }
    }

    /**
     * Método alternativo para abrir archivos usando ProcessBuilder
     * 
     * @param archivo el archivo a abrir
     */
    private void abrirArchivoConProceso(File archivo) throws IOException {
        String osName = System.getProperty("os.name").toLowerCase();
        ProcessBuilder pb;

        if (osName.contains("windows")) {
            // Windows usa el comando 'start'
            pb = new ProcessBuilder("cmd", "/c", "start", "", archivo.getAbsolutePath());
        } else if (osName.contains("mac")) {
            // macOS usa el comando 'open'
            pb = new ProcessBuilder("open", archivo.getAbsolutePath());
        } else {
            // Linux/Unix usa 'xdg-open'
            pb = new ProcessBuilder("xdg-open", archivo.getAbsolutePath());
        }

        pb.start();
        System.out.println("Archivo abierto con ProcessBuilder: " + archivo.getAbsolutePath());
    }

    /**
     * Obtiene la ruta del archivo desde el nombre del documento y el número de
     * expediente
     * 
     * @param numeroExpediente el número de expediente
     * @param nombreDocumento  el nombre del documento
     * @return la ruta del archivo
     */
    private String obtenerRutaArchivoDesdeNombre(String numeroExpediente, String nombreDocumento) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Primero obtener el ID del caso desde el número de expediente
            String sqlCaso = "SELECT id FROM caso WHERE numero_expediente = ?";
            int casoId = -1;

            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sqlCaso)) {
                stmt.setString(1, numeroExpediente);
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        casoId = rs.getInt("id");
                    }
                }
            }

            if (casoId != -1) {
                // Ahora obtener la ruta del documento
                String sqlDoc = "SELECT ruta FROM documento_caso WHERE caso_id = ? AND nombre = ?";
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sqlDoc)) {
                    stmt.setInt(1, casoId);
                    stmt.setString(2, nombreDocumento);
                    try (java.sql.ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            return rs.getString("ruta");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener ruta de archivo: " + e.getMessage());
        }
        return "";
    }

    private void cargarDatosEjemplo() {
        // Los datos de ejemplo se han eliminado para hacer pruebas con datos reales de
        // la base de datos
        System.out.println("INFO: No se cargan datos de ejemplo para hacer pruebas con datos reales de la BD");

        // Si deseas agregar documentos de prueba, descomenta y modifica las siguientes
        // líneas:
        /*
         * tb_Documentos.getItems().addAll(
         * new DocumentoDemo("", "Contrato_ABC.pdf", "PDF", "01/06/2024", "230 KB"),
         * new DocumentoDemo("","Evidencia_Julio.docx", "Word", "03/06/2024", "114 KB")
         * );
         */
    }

    private void cargarDocumentosPorExpediente(String expediente) {
        // Limpiar la tabla primero
        tb_Documentos.getItems().clear();

        if (expediente != null && !expediente.isEmpty()) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Primero obtener el ID del caso desde el número de expediente
                String sqlCaso = "SELECT id FROM caso WHERE numero_expediente = ?";
                int casoId = -1;

                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sqlCaso)) {
                    stmt.setString(1, expediente);
                    try (java.sql.ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            casoId = rs.getInt("id");
                        }
                    }
                }

                if (casoId != -1) {
                    // Ahora obtener los documentos de ese caso
                    DocumentoCasoService service = new DocumentoCasoService(conn);
                    List<DocumentoCaso> documentos = service.obtenerDocumentosPorCaso(casoId);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                    for (DocumentoCaso doc : documentos) {
                        // Obtener el tamaño del archivo
                        String tamano = "N/A";
                        File archivo = new File(doc.getRuta());
                        if (archivo.exists()) {
                            long sizeInBytes = archivo.length();
                            if (sizeInBytes < 1024) {
                                tamano = sizeInBytes + " B";
                            } else if (sizeInBytes < 1024 * 1024) {
                                tamano = (sizeInBytes / 1024) + " KB";
                            } else {
                                tamano = String.format("%.1f MB", sizeInBytes / (1024.0 * 1024.0));
                            }
                        }

                        // Determinar el tipo de documento desde el nombre
                        String tipo = "Otro";
                        String nombre = doc.getNombre();
                        if (nombre.contains("(") && nombre.contains(")")) {
                            int inicioTipo = nombre.lastIndexOf("(") + 1;
                            int finTipo = nombre.lastIndexOf(")");
                            if (inicioTipo < finTipo) {
                                tipo = nombre.substring(inicioTipo, finTipo);
                            }
                        }

                        tb_Documentos.getItems().add(
                                new DocumentoDemo(
                                        expediente,
                                        doc.getNombre(),
                                        tipo,
                                        dateFormat.format(doc.getFechaSubida()),
                                        tamano));
                    }

                    System.out.println(
                            "DEBUG: Cargados " + documentos.size() + " documentos para expediente: " + expediente);
                } else {
                    System.out.println("ADVERTENCIA: No se encontró el caso con número de expediente: " + expediente);
                    tb_Documentos.getItems().add(
                            new DocumentoDemo(
                                    expediente,
                                    "No se encontraron documentos",
                                    "-",
                                    "-",
                                    "-"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error al cargar documentos: " + e.getMessage());

                // Mostrar mensaje de error en la tabla
                tb_Documentos.getItems().add(
                        new DocumentoDemo(
                                expediente,
                                "Error al cargar documentos",
                                "-",
                                "-",
                                "-"));
            }
        } else {
            System.out.println("ADVERTENCIA: No se pudo cargar documentos, expediente inválido.");
            cargarDatosEjemplo();
        }
    }

    public record DocumentoDemo(
            String numeroExpediente,
            String nombre,
            String tipo,
            String fecha,
            String tamano) {
    }
}
