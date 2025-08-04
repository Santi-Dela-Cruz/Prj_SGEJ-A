package application.service;

import application.model.Factura;
import application.model.FacturaDetalle;
import application.util.FileUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio para generar archivos PDF de facturas
 */
public class FacturaPDFService {

    private static final Logger LOGGER = Logger.getLogger(FacturaPDFService.class.getName());
    private static final Font TITULO_FONT = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
    private static final Font SUBTITULO_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font TEXTO_NORMAL = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    private static final Font TEXTO_NEGRITA = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    private static final Font TEXTO_PEQUEÑO = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);

    /**
     * Genera un archivo PDF a partir de una factura
     * 
     * @param factura La factura a convertir en PDF
     * @return Ruta del archivo PDF generado o null si hubo un error
     */
    public static String generarPDF(Factura factura) {
        if (factura == null) {
            LOGGER.warning("No se puede generar PDF: la factura es nula");
            return null;
        }

        // Crear nombre de archivo basado en número de factura
        String nombreArchivo = "factura_" + factura.getSecuencial() + ".pdf";
        String directorioTemp = FileUtil.getDirectorioTemporal();
        String rutaArchivo = directorioTemp + nombreArchivo;

        Document document = new Document(PageSize.A4);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo));
            document.open();

            // Agregar encabezado
            agregarEncabezado(document, factura);

            // Información de factura
            agregarInformacionFactura(document, factura);

            // Información del cliente
            agregarInformacionCliente(document, factura);

            // Tabla de detalles
            agregarTablaDetalles(document, factura);

            // Totales
            agregarTotales(document, factura);

            // Información adicional
            agregarInformacionAdicional(document, factura);

            // Pie de página
            agregarPiePagina(writer, document, factura);

            document.close();
            LOGGER.info("PDF de factura generado correctamente: " + rutaArchivo);
            return rutaArchivo;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al generar PDF de factura", e);
            return null;
        }
    }

    /**
     * Agrega el encabezado con los datos del emisor
     */
    private static void agregarEncabezado(Document document, Factura factura) throws DocumentException {
        Paragraph titulo = new Paragraph("FACTURA ELECTRÓNICA", TITULO_FONT);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(Chunk.NEWLINE);

        PdfPTable tablaEncabezado = new PdfPTable(2);
        tablaEncabezado.setWidthPercentage(100);

        // Columna izquierda: Datos del emisor
        PdfPCell celdaEmisor = new PdfPCell();
        celdaEmisor.setBorder(Rectangle.BOX);
        celdaEmisor.setPadding(5);

        Paragraph razonSocial = new Paragraph(factura.getRazonSocialEmisor(), SUBTITULO_FONT);
        celdaEmisor.addElement(razonSocial);

        Paragraph ruc = new Paragraph("RUC: " + factura.getRucEmisor(), TEXTO_NORMAL);
        celdaEmisor.addElement(ruc);

        Paragraph direccion = new Paragraph("Dirección Matriz: " + factura.getDireccionMatriz(), TEXTO_NORMAL);
        celdaEmisor.addElement(direccion);

        if (factura.getDireccionSucursal() != null && !factura.getDireccionSucursal().isEmpty()) {
            Paragraph sucursal = new Paragraph("Dirección Sucursal: " + factura.getDireccionSucursal(), TEXTO_NORMAL);
            celdaEmisor.addElement(sucursal);
        }

        // Columna derecha: Número y datos de factura
        PdfPCell celdaNumero = new PdfPCell();
        celdaNumero.setBorder(Rectangle.BOX);
        celdaNumero.setPadding(5);

        Paragraph numeroFactura = new Paragraph("No. " + factura.getCodigoEstablecimiento() + "-" +
                factura.getCodigoPuntoEmision() + "-" + factura.getSecuencial(), SUBTITULO_FONT);
        numeroFactura.setAlignment(Element.ALIGN_CENTER);
        celdaNumero.addElement(numeroFactura);

        if (factura.getNumeroAutorizacion() != null && !factura.getNumeroAutorizacion().isEmpty()) {
            Paragraph autorizacion = new Paragraph("NÚMERO DE AUTORIZACIÓN", TEXTO_PEQUEÑO);
            autorizacion.setAlignment(Element.ALIGN_CENTER);
            celdaNumero.addElement(autorizacion);

            Paragraph numAutorizacion = new Paragraph(factura.getNumeroAutorizacion(), TEXTO_PEQUEÑO);
            numAutorizacion.setAlignment(Element.ALIGN_CENTER);
            celdaNumero.addElement(numAutorizacion);
        }

        if (factura.getFechaAutorizacion() != null) {
            Paragraph fechaAutorizacion = new Paragraph("FECHA Y HORA DE AUTORIZACIÓN", TEXTO_PEQUEÑO);
            fechaAutorizacion.setAlignment(Element.ALIGN_CENTER);
            celdaNumero.addElement(fechaAutorizacion);

            Paragraph fechaAut = new Paragraph(factura.getFechaAutorizacion().toString(), TEXTO_PEQUEÑO);
            fechaAut.setAlignment(Element.ALIGN_CENTER);
            celdaNumero.addElement(fechaAut);
        }

        Paragraph ambiente = new Paragraph("AMBIENTE: " +
                ("PRODUCCION".equals(factura.getAmbiente()) ? "PRODUCCIÓN" : "PRUEBAS"), TEXTO_PEQUEÑO);
        ambiente.setAlignment(Element.ALIGN_CENTER);
        celdaNumero.addElement(ambiente);

        Paragraph emision = new Paragraph("EMISIÓN: " + factura.getEmision(), TEXTO_PEQUEÑO);
        emision.setAlignment(Element.ALIGN_CENTER);
        celdaNumero.addElement(emision);

        Paragraph obligadoContabilidad = new Paragraph("OBLIGADO A LLEVAR CONTABILIDAD: " +
                (factura.isObligadoContabilidad() ? "SÍ" : "NO"), TEXTO_PEQUEÑO);
        obligadoContabilidad.setAlignment(Element.ALIGN_CENTER);
        celdaNumero.addElement(obligadoContabilidad);

        tablaEncabezado.addCell(celdaEmisor);
        tablaEncabezado.addCell(celdaNumero);

        document.add(tablaEncabezado);
        document.add(Chunk.NEWLINE);
    }

    /**
     * Agrega la información básica de la factura
     */
    private static void agregarInformacionFactura(Document document, Factura factura) throws DocumentException {
        PdfPTable tablaInfoFactura = new PdfPTable(1);
        tablaInfoFactura.setWidthPercentage(100);

        PdfPCell celda = new PdfPCell();
        celda.setBorder(Rectangle.BOX);
        celda.setPadding(5);

        Paragraph infoTitulo = new Paragraph("INFORMACIÓN DE LA FACTURA", SUBTITULO_FONT);
        celda.addElement(infoTitulo);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaEmision = factura.getFechaEmision() != null ? factura.getFechaEmision().format(formatter) : "N/A";

        Paragraph fechaFactura = new Paragraph("Fecha de Emisión: " + fechaEmision, TEXTO_NORMAL);
        celda.addElement(fechaFactura);

        tablaInfoFactura.addCell(celda);
        document.add(tablaInfoFactura);
        document.add(Chunk.NEWLINE);
    }

    /**
     * Agrega la información del cliente
     */
    private static void agregarInformacionCliente(Document document, Factura factura) throws DocumentException {
        PdfPTable tablaCliente = new PdfPTable(1);
        tablaCliente.setWidthPercentage(100);

        PdfPCell celda = new PdfPCell();
        celda.setBorder(Rectangle.BOX);
        celda.setPadding(5);

        Paragraph infoTitulo = new Paragraph("DATOS DEL CLIENTE", SUBTITULO_FONT);
        celda.addElement(infoTitulo);

        Paragraph nombreCliente = new Paragraph("Razón Social / Nombres: " + factura.getNombreCliente(), TEXTO_NORMAL);
        celda.addElement(nombreCliente);

        Paragraph identificacion = new Paragraph("Identificación: " + factura.getIdCliente(), TEXTO_NORMAL);
        celda.addElement(identificacion);

        if (factura.getDireccionCliente() != null && !factura.getDireccionCliente().isEmpty()) {
            Paragraph direccion = new Paragraph("Dirección: " + factura.getDireccionCliente(), TEXTO_NORMAL);
            celda.addElement(direccion);
        }

        if (factura.getTelefonoCliente() != null && !factura.getTelefonoCliente().isEmpty()) {
            Paragraph telefono = new Paragraph("Teléfono: " + factura.getTelefonoCliente(), TEXTO_NORMAL);
            celda.addElement(telefono);
        }

        if (factura.getEmailCliente() != null && !factura.getEmailCliente().isEmpty()) {
            Paragraph email = new Paragraph("Email: " + factura.getEmailCliente(), TEXTO_NORMAL);
            celda.addElement(email);
        }

        tablaCliente.addCell(celda);
        document.add(tablaCliente);
        document.add(Chunk.NEWLINE);
    }

    /**
     * Agrega la tabla de detalles de la factura
     */
    private static void agregarTablaDetalles(Document document, Factura factura) throws DocumentException {
        PdfPTable tablaDetalles = new PdfPTable(new float[] { 2, 7, 2, 2, 2, 3 });
        tablaDetalles.setWidthPercentage(100);

        // Encabezados
        agregarCeldaEncabezado(tablaDetalles, "Código");
        agregarCeldaEncabezado(tablaDetalles, "Descripción");
        agregarCeldaEncabezado(tablaDetalles, "Cantidad");
        agregarCeldaEncabezado(tablaDetalles, "P. Unitario");
        agregarCeldaEncabezado(tablaDetalles, "Descuento");
        agregarCeldaEncabezado(tablaDetalles, "Subtotal");

        // Detalles
        if (factura.getDetalles() != null && !factura.getDetalles().isEmpty()) {
            for (FacturaDetalle detalle : factura.getDetalles()) {
                agregarCeldaDetalle(tablaDetalles, String.valueOf(detalle.getCodigoServicio()));
                agregarCeldaDetalle(tablaDetalles, detalle.getDescripcion());
                agregarCeldaDetalle(tablaDetalles, detalle.getCantidad().toString());
                agregarCeldaDetalle(tablaDetalles, formatearValor(detalle.getPrecioUnitario()));
                agregarCeldaDetalle(tablaDetalles, formatearValor(detalle.getDescuento()));
                agregarCeldaDetalle(tablaDetalles, formatearValor(detalle.getValorSubtotal()));
            }
        }

        document.add(tablaDetalles);
        document.add(Chunk.NEWLINE);
    }

    /**
     * Agrega una celda de encabezado a la tabla
     */
    private static void agregarCeldaEncabezado(PdfPTable tabla, String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, TEXTO_NEGRITA));
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
        celda.setPadding(5);
        tabla.addCell(celda);
    }

    /**
     * Agrega una celda de detalle a la tabla
     */
    private static void agregarCeldaDetalle(PdfPTable tabla, String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, TEXTO_NORMAL));
        celda.setPadding(5);
        tabla.addCell(celda);
    }

    /**
     * Agrega los totales de la factura
     */
    private static void agregarTotales(Document document, Factura factura) throws DocumentException {
        PdfPTable tablaTotales = new PdfPTable(2);
        tablaTotales.setWidthPercentage(40);
        tablaTotales.setHorizontalAlignment(Element.ALIGN_RIGHT);

        agregarFilaTotales(tablaTotales, "SUBTOTAL 12%", formatearValor(factura.getSubtotal12()));
        agregarFilaTotales(tablaTotales, "SUBTOTAL 0%", formatearValor(factura.getSubtotal0()));
        agregarFilaTotales(tablaTotales, "SUBTOTAL NO OBJETO DE IVA", formatearValor(factura.getSubtotalNoObjetoIva()));
        agregarFilaTotales(tablaTotales, "SUBTOTAL EXENTO DE IVA", formatearValor(factura.getSubtotalExentoIva()));
        agregarFilaTotales(tablaTotales, "SUBTOTAL SIN IMPUESTOS", formatearValor(factura.getSubtotalSinImpuestos()));
        agregarFilaTotales(tablaTotales, "DESCUENTO", formatearValor(factura.getTotalDescuento()));
        agregarFilaTotales(tablaTotales, "IVA 12%", formatearValor(factura.getValorIva()));
        agregarFilaTotales(tablaTotales, "VALOR TOTAL", formatearValor(factura.getValorTotal()));

        document.add(tablaTotales);
        document.add(Chunk.NEWLINE);
    }

    /**
     * Agrega una fila a la tabla de totales
     */
    private static void agregarFilaTotales(PdfPTable tabla, String concepto, String valor) {
        PdfPCell celdaConcepto = new PdfPCell(new Phrase(concepto, TEXTO_NEGRITA));
        celdaConcepto.setHorizontalAlignment(Element.ALIGN_LEFT);
        celdaConcepto.setPadding(3);

        PdfPCell celdaValor = new PdfPCell(new Phrase(valor, TEXTO_NORMAL));
        celdaValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
        celdaValor.setPadding(3);

        tabla.addCell(celdaConcepto);
        tabla.addCell(celdaValor);
    }

    /**
     * Agrega información adicional de la factura
     */
    private static void agregarInformacionAdicional(Document document, Factura factura) throws DocumentException {
        if ((factura.getNombreCaso() != null && !factura.getNombreCaso().isEmpty()) ||
                (factura.getNumeroExpediente() != null && !factura.getNumeroExpediente().isEmpty()) ||
                (factura.getAbogadoResponsable() != null && !factura.getAbogadoResponsable().isEmpty()) ||
                (factura.getFormaPago() != null && !factura.getFormaPago().isEmpty())) {

            PdfPTable tablaInfo = new PdfPTable(1);
            tablaInfo.setWidthPercentage(100);

            PdfPCell celda = new PdfPCell();
            celda.setBorder(Rectangle.BOX);
            celda.setPadding(5);

            Paragraph infoTitulo = new Paragraph("INFORMACIÓN ADICIONAL", SUBTITULO_FONT);
            celda.addElement(infoTitulo);

            if (factura.getNombreCaso() != null && !factura.getNombreCaso().isEmpty()) {
                Paragraph caso = new Paragraph("Caso: " + factura.getNombreCaso(), TEXTO_NORMAL);
                celda.addElement(caso);
            }

            if (factura.getNumeroExpediente() != null && !factura.getNumeroExpediente().isEmpty()) {
                Paragraph expediente = new Paragraph("Expediente N°: " + factura.getNumeroExpediente(), TEXTO_NORMAL);
                celda.addElement(expediente);
            }

            if (factura.getAbogadoResponsable() != null && !factura.getAbogadoResponsable().isEmpty()) {
                Paragraph abogado = new Paragraph("Abogado Responsable: " + factura.getAbogadoResponsable(),
                        TEXTO_NORMAL);
                celda.addElement(abogado);
            }

            if (factura.getFormaPago() != null && !factura.getFormaPago().isEmpty()) {
                Paragraph formaPago = new Paragraph("Forma de Pago: " + factura.getFormaPago(), TEXTO_NORMAL);
                celda.addElement(formaPago);
            }

            tablaInfo.addCell(celda);
            document.add(tablaInfo);
            document.add(Chunk.NEWLINE);
        }
    }

    /**
     * Agrega el pie de página
     */
    private static void agregarPiePagina(PdfWriter writer, Document document, Factura factura) {
        // Se podría agregar un pie de página personalizado usando un PdfPageEvent
        // Por simplicidad, se deja en blanco en este ejemplo
    }

    /**
     * Formatea un valor decimal para mostrar en el PDF
     */
    private static String formatearValor(BigDecimal valor) {
        if (valor == null) {
            return "0.00";
        }
        return String.format("%.2f", valor.doubleValue());
    }
}
