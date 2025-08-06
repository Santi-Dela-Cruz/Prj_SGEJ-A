package application.controllers.casos_documentacion;

import application.controllers.DialogUtil;
import application.database.DatabaseConnection;
import application.model.DocumentoCaso;
import application.service.DocumentoCasoService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class FormDocumentoController {

    private static final Logger LOGGER = Logger.getLogger(FormDocumentoController.class.getName());

    @FXML
    private TextField txtf_NombreDocumento;
    @FXML
    private ComboBox<String> cbx_TipoDocumento;
    @FXML
    private TextArea txta_Descripcion;
    @FXML
    private DatePicker dtp_FechaCarga;
    @FXML
    private TextField txtf_NumeroExpediente;
    @FXML
    private TextField txtf_RutaArchivo;
    @FXML
    private javafx.scene.text.Text txt_TituloForm;

    @FXML
    private Button btn_SeleccionarArchivo;
    @FXML
    private Button btn_Guardar;
    @FXML
    private Button btn_Cancelar;

    private Runnable onCancelar;
    private Runnable onGuardar;

    @FXML
    private void initialize() {
        cbx_TipoDocumento.getItems().addAll(
                "PDF",
                "Word",
                "Imagen",
                "Texto",
                "Excel",
                "PowerPoint",
                "Comprimido",
                "Otro");

        // Establecer fecha actual por defecto
        dtp_FechaCarga.setValue(LocalDate.now());

        btn_SeleccionarArchivo.setOnAction(e -> seleccionarArchivo());

        // Configuración inicial del botón Cancelar (será modificado en setModo si es
        // necesario)
        btn_Cancelar.setOnAction(e -> {
            Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                    "Confirmación",
                    "¿Está seguro que desea cancelar el formulario?\nSe perderán los cambios no guardados.",
                    "confirm",
                    List.of(ButtonType.YES, ButtonType.NO));

            if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
                if (onCancelar != null) {
                    onCancelar.run();
                }
            }
        });

        btn_Guardar.setOnAction(event -> {
            if (txtf_NumeroExpediente.getText().isEmpty() ||
                    txtf_NombreDocumento.getText().isEmpty() ||
                    cbx_TipoDocumento.getValue() == null ||
                    dtp_FechaCarga.getValue() == null ||
                    txtf_RutaArchivo.getText().isEmpty()) {

                DialogUtil.mostrarDialogo(
                        "Campos requeridos",
                        "Por favor, complete los campos obligatorios:\n" +
                                " - Número de Expediente\n" +
                                " - Nombre del Documento\n" +
                                " - Tipo de Documento\n" +
                                " - Fecha de Carga\n" +
                                " - Seleccione un archivo",
                        "warning",
                        List.of(ButtonType.OK));
                return;
            }

            Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                    "Confirmación",
                    "¿Está seguro que desea guardar este documento?",
                    "confirm",
                    List.of(ButtonType.YES, ButtonType.NO));

            if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
                try {
                    // Guardar el archivo en la carpeta de uploads según configuración
                    File archivoOrigen = new File(txtf_RutaArchivo.getText());
                    String numeroExpediente = txtf_NumeroExpediente.getText();

                    // Obtener ruta base desde parámetros
                    application.service.ParametroService paramService = application.service.ParametroService
                            .getInstance();
                    String rutaBase = paramService.getRutaArchivos();

                    // Crear el directorio para los documentos del caso si no existe
                    Path directorioDestino = Paths.get(rutaBase, numeroExpediente);
                    if (!Files.exists(directorioDestino)) {
                        Files.createDirectories(directorioDestino);
                    }

                    // Generar nombre único para el archivo
                    String nombreArchivo = System.currentTimeMillis() + "_" + archivoOrigen.getName();
                    Path rutaDestino = directorioDestino.resolve(nombreArchivo);

                    // Copiar el archivo a la carpeta de uploads
                    Files.copy(archivoOrigen.toPath(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);

                    // Crear el objeto DocumentoCaso
                    DocumentoCaso documento = new DocumentoCaso();

                    // Obtener el ID del caso desde el número de expediente
                    int casoId = obtenerCasoIdDesdeNumeroExpediente(numeroExpediente);
                    if (casoId == -1) {
                        throw new SQLException(
                                "No se encontró el caso con el número de expediente: " + numeroExpediente);
                    }

                    documento.setCasoId(casoId);
                    documento.setNombre(txtf_NombreDocumento.getText() + " (" + cbx_TipoDocumento.getValue() + ")");
                    documento.setRuta(rutaDestino.toString());
                    documento.setFechaSubida(
                            Date.from(dtp_FechaCarga.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));

                    // Guardar en la base de datos
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        DocumentoCasoService service = new DocumentoCasoService(conn);
                        service.registrarDocumento(documento);

                        DialogUtil.mostrarDialogo(
                                "Éxito",
                                "Documento guardado correctamente.",
                                "info",
                                List.of(ButtonType.OK));

                        if (onGuardar != null)
                            onGuardar.run();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                    DialogUtil.mostrarDialogo(
                            "Error",
                            "Error al guardar el archivo: " + e1.getMessage(),
                            "error",
                            List.of(ButtonType.OK));
                } catch (SQLException e2) {
                    e2.printStackTrace();
                    DialogUtil.mostrarDialogo(
                            "Error",
                            "Error de base de datos: " + e2.getMessage(),
                            "error",
                            List.of(ButtonType.OK));
                }
            }
        });

        btn_Cancelar.setOnAction(event -> {
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

    private void seleccionarArchivo() {
        // Obtener la configuración de formatos permitidos desde el servicio de
        // parámetros
        application.service.ParametroService paramService = application.service.ParametroService.getInstance();
        // Invalidar caché antes de obtener el valor para asegurar que obtenemos la
        // versión más reciente
        paramService.invalidarCache();
        String formatosPermitidos = paramService.getFormatosPermitidos();

        LOGGER.info("Formatos permitidos obtenidos del servicio: [" + formatosPermitidos + "]");

        // Convertir la cadena de formatos a un array, eliminando espacios
        String[] formatos = formatosPermitidos.split(",");

        // Preparar los formatos para el FileChooser (añadir * y .)
        String[] formatosConPunto = new String[formatos.length];
        for (int i = 0; i < formatos.length; i++) {
            formatosConPunto[i] = "*." + formatos[i].trim();
            LOGGER.info("Formato con punto preparado: " + formatosConPunto[i]);
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo");

        // Configurar los filtros según los parámetros
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Formatos permitidos (" + formatosPermitidos + ")", formatosConPunto));

        // Añadir también filtros específicos por tipo para mejor usabilidad
        // Verificar que cada formato esté realmente en la lista (no solo que la cadena
        // lo contenga)
        boolean tienePDF = false;
        boolean tieneWord = false;
        boolean tieneImagen = false;

        for (String formato : formatos) {
            String fmt = formato.trim().toLowerCase();
            if ("pdf".equals(fmt)) {
                tienePDF = true;
            } else if ("doc".equals(fmt) || "docx".equals(fmt)) {
                tieneWord = true;
            } else if ("jpg".equals(fmt) || "jpeg".equals(fmt) || "png".equals(fmt)) {
                tieneImagen = true;
            }
        }

        LOGGER.info("Filtros específicos - PDF: " + tienePDF + ", Word: " + tieneWord + ", Imágenes: " + tieneImagen);

        if (tienePDF) {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Documentos PDF", "*.pdf"));
        }
        if (tieneWord) {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Documentos Word", "*.docx", "*.doc"));
        }
        if (tieneImagen) {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        }

        Window window = btn_SeleccionarArchivo.getScene().getWindow();
        File archivo = fileChooser.showOpenDialog(window);

        if (archivo != null) {
            // Verificar tamaño del archivo
            int tamañoMaximoMB = paramService.getTamañoMaximoArchivo();
            long tamañoArchivoMB = archivo.length() / (1024 * 1024); // Convertir bytes a MB

            // Verificar extensión del archivo
            String fileName = archivo.getName();
            int puntoIndex = fileName.lastIndexOf('.');
            String extension = "";
            if (puntoIndex > 0) {
                extension = fileName.substring(puntoIndex + 1).toLowerCase().trim();
            }

            LOGGER.info("Verificando archivo: [" + fileName + "] con extensión: [" + extension + "]");
            // Usamos la variable formatosPermitidos ya declarada anteriormente
            LOGGER.info("Formatos permitidos: [" + formatosPermitidos + "]");

            // Verificación manual de la extensión para depurar
            boolean extensionEnFormatos = false;
            for (String formato : formatos) {
                String formatoTrimmed = formato.trim();
                LOGGER.info("Comparando extensión [" + extension + "] con formato [" + formatoTrimmed + "]");
                if (formatoTrimmed.equalsIgnoreCase(extension)) {
                    extensionEnFormatos = true;
                    break;
                }
            }
            LOGGER.info("Verificación manual de extensión: " + extensionEnFormatos);

            // Usamos el servicio para la verificación oficial
            boolean extensionPermitida = paramService.esExtensionPermitida(extension);

            // Validar tamaño y extensión
            if (tamañoArchivoMB > tamañoMaximoMB) {
                DialogUtil.mostrarDialogo(
                        "Archivo demasiado grande",
                        "El archivo seleccionado excede el tamaño máximo permitido de " + tamañoMaximoMB + " MB.",
                        "warning",
                        List.of(ButtonType.OK));
                return;
            }

            if (!extensionPermitida) {
                LOGGER.warning("Archivo rechazado: extensión [" + extension + "] no permitida");
                DialogUtil.mostrarDialogo(
                        "Formato no permitido",
                        "El formato de archivo ." + extension + " no está permitido.\n" +
                                "Formatos permitidos: " + paramService.getFormatosPermitidos(),
                        "warning",
                        List.of(ButtonType.OK));
                return;
            } else {
                LOGGER.info("Archivo aceptado: extensión [" + extension + "] permitida");
            }

            txtf_RutaArchivo.setText(archivo.getAbsolutePath());

            // Extraer y autocompletar nombre del documento basado en el archivo
            String nombreArchivo = fileName;
            // Quitar la extensión para un nombre más limpio
            if (puntoIndex > 0) {
                nombreArchivo = fileName.substring(0, puntoIndex);
            }
            txtf_NombreDocumento.setText(nombreArchivo);

            // Intentar determinar el tipo de documento basado en la extensión
            if (puntoIndex > 0) {
                // La variable extension ya fue declarada y asignada anteriormente, no es
                // necesario redeclararla
                switch (extension) {
                    case "pdf":
                        cbx_TipoDocumento.setValue("PDF");
                        break;
                    case "docx":
                    case "doc":
                    case "rtf":
                    case "odt":
                        cbx_TipoDocumento.setValue("Word");
                        break;
                    case "jpg":
                    case "jpeg":
                    case "png":
                    case "gif":
                    case "bmp":
                    case "tif":
                    case "tiff":
                        cbx_TipoDocumento.setValue("Imagen");
                        break;
                    case "txt":
                    case "md":
                    case "csv":
                        cbx_TipoDocumento.setValue("Texto");
                        break;
                    case "xls":
                    case "xlsx":
                        cbx_TipoDocumento.setValue("Excel");
                        break;
                    case "ppt":
                    case "pptx":
                        cbx_TipoDocumento.setValue("PowerPoint");
                        break;
                    case "zip":
                    case "rar":
                    case "7z":
                        cbx_TipoDocumento.setValue("Comprimido");
                        break;
                    default:
                        cbx_TipoDocumento.setValue("Otro");
                        break;
                }
            }
        }
    }

    public void setOnCancelar(Runnable callback) {
        this.onCancelar = callback;
    }

    public void setOnGuardar(Runnable callback) {
        this.onGuardar = callback;
    }

    public void asignarExpediente(String expediente) {
        if (txtf_NumeroExpediente != null) {
            txtf_NumeroExpediente.setText(expediente);
            txtf_NumeroExpediente.setDisable(true); // Deshabilitar para evitar cambios
        }
    }

    public void setModo(String modo) {
        boolean esNuevo = "NUEVO".equals(modo);
        boolean esEditar = "EDITAR".equals(modo);
        boolean esVer = "VER".equals(modo);

        if (esNuevo) {
            txt_TituloForm.setText("Registrar Documento");
        } else if (esEditar) {
            txt_TituloForm.setText("Editar Documento");
        } else if (esVer) {
            txt_TituloForm.setText("Visualizar Documento");
        }

        txtf_NombreDocumento.setEditable(esNuevo || esEditar);
        cbx_TipoDocumento.setDisable(esVer);
        dtp_FechaCarga.setDisable(esVer);
        txta_Descripcion.setEditable(esNuevo || esEditar);
        txtf_NumeroExpediente.setEditable(esNuevo || esEditar);
        txtf_RutaArchivo.setEditable(false);
        btn_SeleccionarArchivo.setDisable(esVer);

        btn_Guardar.setVisible(!esVer);
        btn_Guardar.setDisable(esVer);

        // Configurar el botón Cancelar específicamente para el modo Ver
        if (esVer) {
            btn_Cancelar.setText("Regresar");
            // En modo VER, el botón cancelar no debe mostrar diálogo de confirmación
            btn_Cancelar.setOnAction(e -> {
                if (onCancelar != null) {
                    onCancelar.run();
                }
            });
        } else {
            btn_Cancelar.setText("Cancelar");
            // Reestablecer comportamiento normal para otros modos
            btn_Cancelar.setOnAction(e -> {
                Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                        "Confirmación",
                        "¿Está seguro que desea cancelar el formulario?\nSe perderán los cambios no guardados.",
                        "confirm",
                        List.of(ButtonType.YES, ButtonType.NO));

                if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
                    if (onCancelar != null) {
                        onCancelar.run();
                    }
                }
            });
        }
    }

    public void cargarDatosDocumento(String nombre, String tipo, String fecha, String descripcion, String expediente,
            String archivo) {
        txtf_NombreDocumento.setText(nombre);
        cbx_TipoDocumento.setValue(tipo);
        txtf_RutaArchivo.setText(archivo);
        txtf_NumeroExpediente.setText(expediente);
        txta_Descripcion.setText(descripcion);
        if (fecha != null && !fecha.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                dtp_FechaCarga.setValue(LocalDate.parse(fecha, formatter));
            } catch (DateTimeParseException e) {
                dtp_FechaCarga.setValue(null); // or handle error
            }
        }
    }

    /**
     * Obtiene el ID del caso a partir del número de expediente
     * 
     * @param numeroExpediente el número de expediente
     * @return el ID del caso, o -1 si no se encuentra
     */
    private int obtenerCasoIdDesdeNumeroExpediente(String numeroExpediente) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Primero verificamos si existe un caso con ese número de expediente
            String sqlCount = "SELECT COUNT(*) as total FROM caso WHERE numero_expediente = ?";
            try (java.sql.PreparedStatement stmtCount = conn.prepareStatement(sqlCount)) {
                stmtCount.setString(1, numeroExpediente);
                try (java.sql.ResultSet rsCount = stmtCount.executeQuery()) {
                    if (rsCount.next() && rsCount.getInt("total") == 0) {
                        // Si no existe, creamos uno temporal para pruebas
                        String sqlInsert = "INSERT INTO caso (numero_expediente, titulo, tipo, estado) VALUES (?, ?, ?, ?)";
                        try (java.sql.PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert,
                                java.sql.Statement.RETURN_GENERATED_KEYS)) {
                            stmtInsert.setString(1, numeroExpediente);
                            stmtInsert.setString(2, "Caso " + numeroExpediente);
                            stmtInsert.setString(3, "Civil");
                            stmtInsert.setString(4, "Activo");
                            stmtInsert.executeUpdate();

                            // Obtenemos el ID generado
                            try (java.sql.ResultSet generatedKeys = stmtInsert.getGeneratedKeys()) {
                                if (generatedKeys.next()) {
                                    System.out.println("Caso temporal creado con ID: " + generatedKeys.getInt(1));
                                    return generatedKeys.getInt(1);
                                }
                            }
                        }
                    }
                }
            }

            // Buscamos el caso normalmente
            String sql = "SELECT id FROM caso WHERE numero_expediente = ?";
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, numeroExpediente);
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al buscar el caso: " + e.getMessage());
        }
        return -1;
    }
}
