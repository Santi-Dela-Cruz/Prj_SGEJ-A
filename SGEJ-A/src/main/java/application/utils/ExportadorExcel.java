package application.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

import application.model.Usuario;

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
}
