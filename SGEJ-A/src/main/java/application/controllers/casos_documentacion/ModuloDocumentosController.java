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
    private Button btn_Buscar, btn_Subir, btn_Regresar;
    @FXML
    private TableView<DocumentoDemo> tb_Documentos;
    @FXML
    private TableColumn<DocumentoDemo, String> tbc_Nombre, tbc_Tipo, tbc_Fecha, tbc_Tamano;
    @FXML
    private TableColumn<DocumentoDemo, Void> tbc_BotonVer, tbc_BotonEliminar, tbc_BotonDescargar;
    @FXML
    private Label lblTitulo;

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
     * Este método ahora ignora el callback proporcionado y siempre usa MainController
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
                    application.controllers.MainController mainController = 
                        application.controllers.MainController.getInstance();
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
                        application.controllers.MainController mainController = 
                            application.controllers.MainController.getInstance();
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
                    application.controllers.MainController mainController = 
                        application.controllers.MainController.getInstance();
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
                        application.controllers.MainController mainController = 
                            application.controllers.MainController.getInstance();
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
        
        // Configurar botón subir documento
        if (btn_Subir != null) {
            btn_Subir.setOnAction(event -> mostrarFormularioDocumento());
        }

        configurarColumnas();
        inicializarColumnasDeBotones();
        cargarDatosEjemplo();
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
            tt.setOnFinished(evt -> {
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
        columna.setCellFactory(param -> new TableCell<>() {
            final Button btn = new Button(texto);

            {
                btn.getStyleClass().add("table-button");
                btn.setTooltip(new Tooltip(tooltip));
                btn.setOnAction(event -> {
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
        Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                "Confirmación de eliminación",
                "¿Está seguro que desea eliminar el documento '" + doc.nombre() + "'?\n" +
                        "Esta acción no se puede deshacer.",
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

    private void mostrarDocumentoParaVisualizar(DocumentoDemo doc) {
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

            // Configurar callbacks para ambos escenarios: cancelar y guardar
            // Esto asegura que el botón de regresar funcione correctamente
            controller.setOnCancelar(() -> cerrarFormulario());
            controller.setOnGuardar(() -> cerrarFormulario()); // Aunque no se use en modo VER, es buena práctica configurarlo

            // Obtener la ruta del archivo
            String rutaArchivo = obtenerRutaArchivoDesdeNombre(doc.numeroExpediente(), doc.nombre());

            // Cargar los datos del documento
            controller.cargarDatosDocumento(
                    doc.nombre(),
                    doc.tipo(),
                    doc.fecha(),
                    "", // Descripción (no disponible en el modelo actual)
                    doc.numeroExpediente(),
                    rutaArchivo);

            // Configurar el formulario para visualización
            controller.setModo("VER");

            // Posicionamos el formulario en el borde derecho
            AnchorPane.setTopAnchor(pnl_Forms, 60.0); // Espacio para el título
            AnchorPane.setRightAnchor(pnl_Forms, 30.0); // Margen desde la derecha
            AnchorPane.setLeftAnchor(pnl_Forms, null); // Importante: quitar el anclaje izquierdo
            AnchorPane.setBottomAnchor(pnl_Forms, 30.0); // Margen desde abajo

            pnl_Forms.getChildren().setAll(form);
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
