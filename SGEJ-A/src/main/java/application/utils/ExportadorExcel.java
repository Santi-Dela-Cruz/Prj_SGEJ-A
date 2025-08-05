package application.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

import application.model.Usuario;
import application.model.Cliente;

/**
 * Utilidad para exportar datos a formato Excel
 */
public class ExportadorExcel {

    /**
     * Exporta una lista de usuarios a un archivo Excel (.xlsx)
     * 
     * @param listaUsuarios Lista de usuarios a exportar
     * @return true si la exportación fue exitosa, false en caso contrario
     */
    public static boolean exportarUsuariosAExcel(List<Usuario> listaUsuarios) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Usuarios");

        // Crear estilo para encabezados
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        // Estilo para datos
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setAlignment(HorizontalAlignment.LEFT);

        // Cabecera
        Row headerRow = sheet.createRow(0);
        String[] columnas = { "Código", "Nombres Completos", "Identificación", "Usuario", "Email", "Rol", "Estado" };

        for (int i = 0; i < columnas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos
        int fila = 1;
        for (Usuario u : listaUsuarios) {
            Row row = sheet.createRow(fila++);

            // Rellenar celdas con datos
            Cell cellId = row.createCell(0);
            cellId.setCellValue(u.getId() != null ? u.getId().toString() : "");
            cellId.setCellStyle(dataStyle);

            Cell cellNombre = row.createCell(1);
            cellNombre.setCellValue(u.getNombreCompleto() != null ? u.getNombreCompleto() : "");
            cellNombre.setCellStyle(dataStyle);

            Cell cellIdentificacion = row.createCell(2);
            cellIdentificacion.setCellValue(u.getIdentificacion() != null ? u.getIdentificacion() : "");
            cellIdentificacion.setCellStyle(dataStyle);

            Cell cellUsuario = row.createCell(3);
            cellUsuario.setCellValue(u.getNombreUsuario() != null ? u.getNombreUsuario() : "");
            cellUsuario.setCellStyle(dataStyle);

            Cell cellEmail = row.createCell(4);
            cellEmail.setCellValue(u.getEmail() != null ? u.getEmail() : "");
            cellEmail.setCellStyle(dataStyle);

            Cell cellRol = row.createCell(5);
            cellRol.setCellValue(u.getTipoUsuarioString() != null ? u.getTipoUsuarioString() : "");
            cellRol.setCellStyle(dataStyle);

            Cell cellEstado = row.createCell(6);
            cellEstado.setCellValue(u.getEstadoUsuarioString() != null ? u.getEstadoUsuarioString() : "");
            cellEstado.setCellStyle(dataStyle);
        }

        // Autoajustar columnas
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Configurar FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte de usuarios");

        // Proponer un nombre de archivo con fecha actual
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fechaActual = dateFormat.format(new Date());
        fileChooser.setInitialFileName("Reporte_Usuarios_" + fechaActual + ".xlsx");

        // Filtro para archivos Excel
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos Excel (*.xlsx)", "*.xlsx"));

        // Mostrar diálogo para seleccionar ubicación
        File archivo = fileChooser.showSaveDialog(null);

        boolean resultado = false;
        try {
            if (archivo != null) {
                try (FileOutputStream fileOut = new FileOutputStream(archivo)) {
                    workbook.write(fileOut);
                    resultado = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultado;
    }

    /**
     * Exporta una lista de clientes a un archivo Excel (.xlsx)
     * 
     * @param listaClientes Lista de clientes a exportar
     * @return true si la exportación fue exitosa, false en caso contrario
     */
    public static boolean exportarClientesAExcel(List<Cliente> listaClientes) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Clientes");

        // Crear estilo para encabezados
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        // Estilo para datos
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setAlignment(HorizontalAlignment.LEFT);

        // Cabecera con todas las columnas disponibles
        Row headerRow = sheet.createRow(0);
        String[] columnas = { 
            "ID", 
            "Nombre Completo", 
            "Tipo Persona", 
            "Tipo Identificación", 
            "Número Identificación", 
            "Teléfono", 
            "Correo Electrónico", 
            "Dirección", 
            "Estado", 
            "Fecha Registro", 
            "Estado Civil", 
            "Representante Legal", 
            "Dirección Fiscal",
            "Fecha Creación",
            "Última Actualización"
        };

        for (int i = 0; i < columnas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos
        int fila = 1;
        for (Cliente c : listaClientes) {
            Row row = sheet.createRow(fila++);
            int col = 0;

            // ID
            Cell cellId = row.createCell(col++);
            cellId.setCellValue(String.valueOf(c.getId()));
            cellId.setCellStyle(dataStyle);

            // Nombre Completo
            Cell cellNombre = row.createCell(col++);
            cellNombre.setCellValue(c.getNombreCompleto() != null ? c.getNombreCompleto() : "");
            cellNombre.setCellStyle(dataStyle);
            
            // Tipo Persona
            Cell cellTipoPersona = row.createCell(col++);
            cellTipoPersona.setCellValue(c.getTipoPersona() != null ? c.getTipoPersona().toString() : "");
            cellTipoPersona.setCellStyle(dataStyle);

            // Tipo Identificación
            Cell cellTipoIdentificacion = row.createCell(col++);
            cellTipoIdentificacion.setCellValue(c.getTipoIdentificacion() != null ? c.getTipoIdentificacion().toString() : "");
            cellTipoIdentificacion.setCellStyle(dataStyle);

            // Número Identificación
            Cell cellNumeroIdentificacion = row.createCell(col++);
            cellNumeroIdentificacion.setCellValue(c.getNumeroIdentificacion() != null ? c.getNumeroIdentificacion() : "");
            cellNumeroIdentificacion.setCellStyle(dataStyle);

            // Teléfono
            Cell cellTelefono = row.createCell(col++);
            cellTelefono.setCellValue(c.getTelefono() != null ? c.getTelefono() : "");
            cellTelefono.setCellStyle(dataStyle);

            // Correo Electrónico
            Cell cellCorreo = row.createCell(col++);
            cellCorreo.setCellValue(c.getCorreoElectronico() != null ? c.getCorreoElectronico() : "");
            cellCorreo.setCellStyle(dataStyle);

            // Dirección
            Cell cellDireccion = row.createCell(col++);
            cellDireccion.setCellValue(c.getDireccion() != null ? c.getDireccion() : "");
            cellDireccion.setCellStyle(dataStyle);

            // Estado
            Cell cellEstado = row.createCell(col++);
            cellEstado.setCellValue(c.getEstado() != null ? c.getEstado().toString() : "");
            cellEstado.setCellStyle(dataStyle);
            
            // Fecha Registro
            Cell cellFechaRegistro = row.createCell(col++);
            cellFechaRegistro.setCellValue(c.getFechaRegistro() != null ? c.getFechaRegistro().toString() : "");
            cellFechaRegistro.setCellStyle(dataStyle);
            
            // Estado Civil (si aplica)
            Cell cellEstadoCivil = row.createCell(col++);
            cellEstadoCivil.setCellValue(c.getEstadoCivil() != null ? c.getEstadoCivil() : "");
            cellEstadoCivil.setCellStyle(dataStyle);
            
            // Representante Legal (si aplica)
            Cell cellRepresentante = row.createCell(col++);
            cellRepresentante.setCellValue(c.getRepresentanteLegal() != null ? c.getRepresentanteLegal() : "");
            cellRepresentante.setCellStyle(dataStyle);
            
            // Dirección Fiscal (si aplica)
            Cell cellDireccionFiscal = row.createCell(col++);
            cellDireccionFiscal.setCellValue(c.getDireccionFiscal() != null ? c.getDireccionFiscal() : "");
            cellDireccionFiscal.setCellStyle(dataStyle);
            
            // Fecha Creación
            Cell cellCreatedAt = row.createCell(col++);
            cellCreatedAt.setCellValue(c.getCreatedAt() != null ? c.getCreatedAt().toString() : "");
            cellCreatedAt.setCellStyle(dataStyle);
            
            // Última Actualización
            Cell cellUpdatedAt = row.createCell(col++);
            cellUpdatedAt.setCellValue(c.getUpdatedAt() != null ? c.getUpdatedAt().toString() : "");
            cellUpdatedAt.setCellStyle(dataStyle);
        }

        // Autoajustar columnas
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
            // Asegurar un ancho mínimo para mejor legibilidad
            int anchoActual = sheet.getColumnWidth(i);
            int anchoMinimo = 256 * 15; // 15 caracteres aproximadamente
            if (anchoActual < anchoMinimo) {
                sheet.setColumnWidth(i, anchoMinimo);
            }
        }
        
        // Crear filtros para facilitar la navegación
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, columnas.length - 1));

        // Congelar la fila de encabezado
        sheet.createFreezePane(0, 1);

        // Configurar FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte de clientes");

        // Proponer un nombre de archivo con fecha actual
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fechaActual = dateFormat.format(new Date());
        fileChooser.setInitialFileName("Reporte_Clientes_" + fechaActual + ".xlsx");

        // Filtro para archivos Excel
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos Excel (*.xlsx)", "*.xlsx"));

        // Mostrar diálogo para seleccionar ubicación
        File archivo = fileChooser.showSaveDialog(null);

        boolean resultado = false;
        try {
            if (archivo != null) {
                try (FileOutputStream fileOut = new FileOutputStream(archivo)) {
                    workbook.write(fileOut);
                    resultado = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultado;
    }
}
