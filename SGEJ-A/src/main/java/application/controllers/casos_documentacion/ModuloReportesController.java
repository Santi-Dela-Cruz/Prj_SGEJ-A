package application.controllers.casos_documentacion;

import application.controllers.DialogUtil;
import application.database.DatabaseConnection;
import application.model.Caso;
import application.model.Personal;
import application.model.Cliente;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class ModuloReportesController {

    @FXML private ComboBox<String> cbx_EstadoCaso, cbx_TipoCaso;
    @FXML private DatePicker dt_FechaDesde, dt_FechaHasta;
    @FXML private MenuButton btnc_Generar;
    @FXML private Label lbl_Error, lbl_Exito;
    @FXML private TableView<ReporteDemo> tb_Reportes;

    @FXML private TableColumn<ReporteDemo, String> tbc_NombreReporte, tbc_TituloCaso, tbc_AbogadoAsignado,
            tbc_FechaDesde, tbc_FechaHasta, tbc_TipoDocumento, tbc_Tamano;
    @FXML private TableColumn<ReporteDemo, Void> tbc_ButonVisualizar;

    @FXML
    private void initialize() {
        configurarColumnas();
        inicializarBotonVisualizar();
        configurarComboBoxes();
        configurarMenuGenerar();
        
        // Inicializar fechas con valor por defecto (mes actual)
        LocalDate hoy = LocalDate.now();
        LocalDate primerDiaMes = hoy.withDayOfMonth(1);
        dt_FechaDesde.setValue(primerDiaMes);
        dt_FechaHasta.setValue(hoy);
        
        cargarDatosEjemplo(); // En producción, esto se reemplazaría por datos reales
    }
    
    private void configurarComboBoxes() {
        // Configurar estados del caso
        cbx_EstadoCaso.getItems().addAll("Todos", "Abierto", "En progreso", "Archivado");
        cbx_EstadoCaso.setValue("Todos");
        
        // Configurar tipos de caso
        cbx_TipoCaso.getItems().addAll("Todos", "Civil", "Penal", "Laboral", "Familiar");
        cbx_TipoCaso.setValue("Todos");
    }
    
    private void configurarMenuGenerar() {
        MenuItem menuItemExcel = new MenuItem("Generar Excel");
        MenuItem menuItemPdf = new MenuItem("Generar PDF");
        
        menuItemExcel.setOnAction(event -> generarReporte("Excel"));
        menuItemPdf.setOnAction(event -> generarReporte("PDF"));
        
        btnc_Generar.getItems().clear();
        btnc_Generar.getItems().addAll(menuItemExcel, menuItemPdf);
    }

    private void configurarColumnas() {
        tbc_NombreReporte.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nombre()));
        tbc_TituloCaso.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tipoCaso()));
        tbc_AbogadoAsignado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().abogado()));
        tbc_FechaDesde.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fechaDesde()));
        tbc_FechaHasta.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fechaHasta()));
        tbc_TipoDocumento.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tipoDocumento()));
        tbc_Tamano.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tamano()));
    }

    private void inicializarBotonVisualizar() {
        tbc_ButonVisualizar.setCellFactory(param -> new TableCell<>() {
            final Button btn = new Button("👁");

            {
                btn.getStyleClass().add("table-button");
                btn.setTooltip(new Tooltip("Visualizar reporte"));
                btn.setOnAction(e -> {
                    ReporteDemo reporte = getTableView().getItems().get(getIndex());
                    System.out.println("Visualizar reporte: " + reporte.nombre());
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

    private void cargarDatosEjemplo() {
        // Intentamos cargar datos reales de reportes anteriores
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            // Aquí podrías implementar la carga de reportes anteriores desde la base de datos
            // Por ahora, dejamos la tabla vacía para mostrar solo los nuevos reportes generados
        } catch (SQLException e) {
            System.err.println("Error al cargar reportes anteriores: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }

    public record ReporteDemo(
            String nombre,
            String tipoCaso,
            String abogado,
            String fechaDesde,
            String fechaHasta,
            String tipoDocumento,
            String tamano
    ) {}
    
    /**
     * Método para generar un reporte en formato Excel o PDF
     * @param formato El formato del reporte (Excel o PDF)
     */
    private void generarReporte(String formato) {
        // Obtener los valores de los filtros
        String estado = cbx_EstadoCaso.getValue();
        String tipo = cbx_TipoCaso.getValue();
        LocalDate inicio = dt_FechaDesde.getValue();
        LocalDate fin = dt_FechaHasta.getValue();
        
        // Ocultar mensajes anteriores
        lbl_Error.setVisible(false);
        lbl_Exito.setVisible(false);
        
        // Validaciones
        if (inicio == null || fin == null) {
            mostrarError("Seleccione ambas fechas para generar el reporte.");
            return;
        }
        if (fin.isBefore(inicio)) {
            mostrarError("La fecha final no puede ser anterior a la fecha inicial.");
            return;
        }
        if (fin.isAfter(LocalDate.now())) {
            mostrarError("La fecha final no puede ser posterior a la fecha actual.");
            return;
        }
        
        try {
            // Obtener los casos filtrados
            List<Caso> casosFiltrados = obtenerCasosFiltrados(estado, tipo, inicio, fin);
            
            if (casosFiltrados.isEmpty()) {
                mostrarError("No se encontraron casos con los criterios seleccionados.");
                return;
            }
            
            // Mostrar diálogo de confirmación
            boolean confirmacion = DialogUtil.mostrarConfirmacion(
                "Generar Reporte", 
                "¿Desea generar el reporte de casos en formato " + formato + "?\n\n" +
                "Se generará un reporte con " + casosFiltrados.size() + " casos que coinciden con los criterios seleccionados."
            );
            
            if (!confirmacion) {
                return; // El usuario canceló la operación
            }
            
            // Crear el diálogo para seleccionar la ubicación del archivo
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Reporte");
            
            // Configurar según el formato
            if (formato.equalsIgnoreCase("Excel")) {
                fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Archivos Excel (*.xlsx)", "*.xlsx")
                );
                File archivo = fileChooser.showSaveDialog(btnc_Generar.getScene().getWindow());
                if (archivo != null) {
                    exportarExcel(casosFiltrados, archivo);
                    
                    // Registrar el reporte en la tabla con información real
                    String nombreReporte = "Reporte de Casos " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
                    String tipoDescripcion = tipo.equals("Todos") ? "Todos los tipos" : tipo;
                    String abogadoAsignado = "Todos"; // Ya que eliminamos este filtro
                    
                    // Calcular el tamaño aproximado del archivo
                    long tamanoBytes = archivo.length();
                    String tamanoFormateado;
                    if (tamanoBytes < 1024) {
                        tamanoFormateado = tamanoBytes + " B";
                    } else if (tamanoBytes < 1024 * 1024) {
                        tamanoFormateado = String.format("%.1f KB", tamanoBytes / 1024.0);
                    } else {
                        tamanoFormateado = String.format("%.1f MB", tamanoBytes / (1024.0 * 1024.0));
                    }
                    
                    ReporteDemo nuevoReporte = new ReporteDemo(
                        nombreReporte,
                        tipoDescripcion,
                        abogadoAsignado,
                        inicio.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        fin.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        formato,
                        tamanoFormateado
                    );
                    
                    // Agregar el nuevo reporte al principio de la lista
                    tb_Reportes.getItems().add(0, nuevoReporte);
                    
                    // Guardar registro del reporte (podríamos implementarlo para persistencia)
                    guardarRegistroReporte(nombreReporte, estado, tipo, inicio, fin, formato, archivo.getAbsolutePath(), tamanoFormateado);
                }
            } else if (formato.equalsIgnoreCase("PDF")) {
                fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf")
                );
                File archivo = fileChooser.showSaveDialog(btnc_Generar.getScene().getWindow());
                if (archivo != null) {
                    exportarPDF(casosFiltrados, archivo);
                    
                    // Registrar el reporte en la tabla con información real
                    String nombreReporte = "Reporte de Casos " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
                    String tipoDescripcion = tipo.equals("Todos") ? "Todos los tipos" : tipo;
                    String abogadoAsignado = "Todos"; // Ya que eliminamos este filtro
                    
                    // Calcular el tamaño aproximado del archivo
                    long tamanoBytes = archivo.length();
                    String tamanoFormateado;
                    if (tamanoBytes < 1024) {
                        tamanoFormateado = tamanoBytes + " B";
                    } else if (tamanoBytes < 1024 * 1024) {
                        tamanoFormateado = String.format("%.1f KB", tamanoBytes / 1024.0);
                    } else {
                        tamanoFormateado = String.format("%.1f MB", tamanoBytes / (1024.0 * 1024.0));
                    }
                    
                    ReporteDemo nuevoReporte = new ReporteDemo(
                        nombreReporte,
                        tipoDescripcion,
                        abogadoAsignado,
                        inicio.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        fin.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        formato,
                        tamanoFormateado
                    );
                    
                    // Agregar el nuevo reporte al principio de la lista
                    tb_Reportes.getItems().add(0, nuevoReporte);
                    
                    // Guardar registro del reporte (podríamos implementarlo para persistencia)
                    guardarRegistroReporte(nombreReporte, estado, tipo, inicio, fin, formato, archivo.getAbsolutePath(), tamanoFormateado);
                }
            }
            
        } catch (Exception e) {
            mostrarError("Error al generar el reporte: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Obtiene los casos filtrados según los criterios seleccionados
     */
    private List<Caso> obtenerCasosFiltrados(String estado, String tipo, LocalDate inicio, LocalDate fin) throws SQLException {
        // Convertir LocalDate a Date para compatibilidad con el modelo
        Date fechaInicio = Date.from(inicio.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fechaFin = Date.from(fin.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());
        
        List<Caso> casosFiltrados = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            // Consulta SQL simplificada y directa que une caso, abogado_caso y personal
            String sql = 
                "SELECT c.*, cl.nombre_completo as cliente_nombre, " +
                "p.nombres as abogado_nombres, p.apellidos as abogado_apellidos " +
                "FROM caso c " +
                "LEFT JOIN cliente cl ON c.cliente_id = cl.id " +
                "LEFT JOIN abogado_caso ac ON c.id = ac.caso_id " +
                "LEFT JOIN personal p ON ac.abogado_id = p.id " +
                "WHERE c.fecha_inicio BETWEEN ? AND ? ";
            
            if (!estado.equals("Todos")) {
                sql += "AND c.estado = ? ";
            }
            
            if (!tipo.equals("Todos")) {
                sql += "AND c.tipo = ? ";
            }
            
            stmt = conn.prepareStatement(sql);
            
            // Configurar parámetros
            int paramIndex = 1;
            stmt.setDate(paramIndex++, new java.sql.Date(fechaInicio.getTime()));
            stmt.setDate(paramIndex++, new java.sql.Date(fechaFin.getTime()));
            
            if (!estado.equals("Todos")) {
                stmt.setString(paramIndex++, estado);
            }
            
            if (!tipo.equals("Todos")) {
                stmt.setString(paramIndex++, tipo);
            }
            
            System.out.println("Ejecutando consulta SQL: " + sql); // Para depuración
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Caso caso = new Caso();
                caso.setId(rs.getInt("id"));
                caso.setClienteId(rs.getInt("cliente_id"));
                caso.setNumeroExpediente(rs.getString("numero_expediente"));
                caso.setTitulo(rs.getString("titulo"));
                caso.setDescripcion(rs.getString("descripcion"));
                caso.setEstado(rs.getString("estado"));
                caso.setTipo(rs.getString("tipo"));
                caso.setFechaInicio(rs.getDate("fecha_inicio"));
                
                // Crear y asignar el cliente
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("cliente_id"));
                String nombreCliente = rs.getString("cliente_nombre");
                if (nombreCliente != null) {
                    cliente.setNombreCompleto(nombreCliente);
                } else {
                    cliente.setNombreCompleto("Cliente sin nombre");
                }
                caso.setCliente(cliente);
                
                // Crear y asignar el abogado
                String nombresAbogado = rs.getString("abogado_nombres");
                String apellidosAbogado = rs.getString("abogado_apellidos");
                
                if (nombresAbogado != null && apellidosAbogado != null) {
                    Personal abogado = new Personal();
                    abogado.setNombres(nombresAbogado);
                    abogado.setApellidos(apellidosAbogado);
                    caso.setAbogado(abogado);
                }
                
                casosFiltrados.add(caso);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener casos filtrados: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            // Cerrar recursos en orden inverso
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        
        System.out.println("Casos filtrados encontrados: " + casosFiltrados.size());
        return casosFiltrados;
    }
    
    /**
     * Guarda un registro del reporte generado para futuras referencias
     */
    private void guardarRegistroReporte(String nombreReporte, String estado, String tipo, 
                                       LocalDate inicio, LocalDate fin, String formato, 
                                       String rutaArchivo, String tamano) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            // Verificar si existe la tabla de reportes
            try {
                stmt = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS reportes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT, " +
                    "estado TEXT, " +
                    "tipo TEXT, " +
                    "fecha_inicio TEXT, " +
                    "fecha_fin TEXT, " +
                    "formato TEXT, " +
                    "ruta_archivo TEXT, " +
                    "tamano TEXT, " +
                    "fecha_generacion TEXT" +
                    ")"
                );
                stmt.executeUpdate();
                stmt.close();
                
                // Insertar registro de reporte
                stmt = conn.prepareStatement(
                    "INSERT INTO reportes (nombre, estado, tipo, fecha_inicio, fecha_fin, formato, " +
                    "ruta_archivo, tamano, fecha_generacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                );
                
                stmt.setString(1, nombreReporte);
                stmt.setString(2, estado);
                stmt.setString(3, tipo);
                stmt.setString(4, inicio.toString());
                stmt.setString(5, fin.toString());
                stmt.setString(6, formato);
                stmt.setString(7, rutaArchivo);
                stmt.setString(8, tamano);
                stmt.setString(9, LocalDateTime.now().toString());
                
                stmt.executeUpdate();
                
                System.out.println("Registro de reporte guardado en la base de datos");
            } catch (SQLException e) {
                System.err.println("Error al crear tabla o guardar reporte: " + e.getMessage());
                // No propagamos la excepción para no afectar la experiencia del usuario
            }
            
        } catch (SQLException e) {
            System.err.println("Error al guardar registro del reporte: " + e.getMessage());
        } finally {
            // Cerrar recursos
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { }
            if (conn != null) try { conn.close(); } catch (SQLException e) { }
        }
    }
    
    /**
     * Exporta los casos a un archivo Excel
     */
    private void exportarExcel(List<Caso> casos, File archivo) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet hoja = workbook.createSheet("Casos Jurídicos");
            
            // Crear la fila de encabezado
            Row encabezado = hoja.createRow(0);
            String[] columnas = {
                "Expediente", "Título", "Cliente", "Abogado", "Fecha Inicio", "Estado", "Tipo"
            };
            
            for (int i = 0; i < columnas.length; i++) {
                encabezado.createCell(i).setCellValue(columnas[i]);
            }
            
            // Llenar los datos
            int filaIndice = 1;
            for (Caso caso : casos) {
                Row fila = hoja.createRow(filaIndice++);
                fila.createCell(0).setCellValue(caso.getNumeroExpediente());
                fila.createCell(1).setCellValue(caso.getTitulo());
                fila.createCell(2).setCellValue(caso.getCliente() != null ? 
                                             caso.getCliente().getNombreCompleto() : "Sin cliente");
                fila.createCell(3).setCellValue(caso.getAbogado() != null ? 
                                             caso.getAbogado().getNombres() + " " + caso.getAbogado().getApellidos() : 
                                             "Sin asignar");
                fila.createCell(4).setCellValue(caso.getFechaInicio().toString());
                fila.createCell(5).setCellValue(caso.getEstado());
                fila.createCell(6).setCellValue(caso.getTipo());
            }
            
            // Auto-ajustar columnas
            for (int i = 0; i < columnas.length; i++) {
                hoja.autoSizeColumn(i);
            }
            
            // Escribir el archivo
            try (FileOutputStream salida = new FileOutputStream(archivo)) {
                workbook.write(salida);
            }
            
            // Mostrar mensaje de éxito con DialogUtil
            DialogUtil.mostrarMensajeInfo(
                "Reporte Generado", 
                "Reporte Excel generado correctamente en:\n" + archivo.getAbsolutePath()
            );
            
            mostrarExito("Reporte Excel generado correctamente en: " + archivo.getAbsolutePath());
        }
    }
    
    /**
     * Exporta los casos a un archivo PDF
     */
    private void exportarPDF(List<Caso> casos, File archivo) throws DocumentException, IOException {
        Document documento = new Document();
        
        try {
            PdfWriter.getInstance(documento, new FileOutputStream(archivo));
            documento.open();
            
            // Título del documento
            documento.add(new Paragraph("REPORTE DE CASOS JURÍDICOS", 
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            documento.add(new Paragraph("Fecha de generación: " + LocalDate.now().toString()));
            documento.add(Chunk.NEWLINE);
            
            // Crear tabla
            PdfPTable tabla = new PdfPTable(7); // 7 columnas
            tabla.setWidthPercentage(100);
            
            // Encabezados
            Stream.of("Expediente", "Título", "Cliente", "Abogado", "Fecha Inicio", "Estado", "Tipo")
                .forEach(columna -> {
                    PdfPCell celda = new PdfPCell(new Phrase(columna, 
                            FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                    celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tabla.addCell(celda);
                });
            
            // Datos
            for (Caso caso : casos) {
                tabla.addCell(caso.getNumeroExpediente());
                tabla.addCell(caso.getTitulo());
                tabla.addCell(caso.getCliente() != null ? 
                             caso.getCliente().getNombreCompleto() : "Sin cliente");
                tabla.addCell(caso.getAbogado() != null ? 
                             caso.getAbogado().getNombres() + " " + caso.getAbogado().getApellidos() : 
                             "Sin asignar");
                tabla.addCell(caso.getFechaInicio().toString());
                tabla.addCell(caso.getEstado());
                tabla.addCell(caso.getTipo());
            }
            
            documento.add(tabla);
            documento.close();
            
            // Mostrar mensaje de éxito con DialogUtil
            DialogUtil.mostrarMensajeInfo(
                "Reporte Generado", 
                "Reporte PDF generado correctamente en:\n" + archivo.getAbsolutePath()
            );
            
            mostrarExito("Reporte PDF generado correctamente en: " + archivo.getAbsolutePath());
        } catch (Exception e) {
            if (documento.isOpen()) {
                documento.close();
            }
            throw e;
        }
    }
    
    /**
     * Muestra un mensaje de error
     */
    private void mostrarError(String mensaje) {
        lbl_Error.setText(mensaje);
        lbl_Error.setVisible(true);
        lbl_Exito.setVisible(false);
    }
    
    /**
     * Muestra un mensaje de éxito
     */
    private void mostrarExito(String mensaje) {
        lbl_Exito.setText(mensaje);
        lbl_Exito.setVisible(true);
        lbl_Error.setVisible(false);
    }
}
