package application.service;

import application.dao.FacturaDAO;
import application.model.Factura;
import application.model.FacturaDetalle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio para gestionar las facturas
 */
public class FacturaService {
    private static final Logger LOGGER = Logger.getLogger(FacturaService.class.getName());

    private final FacturaDAO facturaDAO;
    private final ParametroService parametroService;

    // Constructor
    public FacturaService() {
        this.facturaDAO = new FacturaDAO();
        this.parametroService = ParametroService.getInstance();
    }

    /**
     * Guarda o actualiza una factura
     */
    public boolean guardarFactura(Factura factura) {
        // Validación básica
        if (factura.getNombreCliente() == null || factura.getNombreCliente().isEmpty()) {
            LOGGER.log(Level.WARNING, "El nombre del cliente es obligatorio");
            return false;
        }

        // Si es una factura nueva
        if (factura.getId() == 0) {
            // Configurar datos de emisor desde parámetros
            configurarDatosEmisor(factura);

            // Configurar secuencial y documento
            factura.setSecuencial(facturaDAO.generarProximoSecuencial());
            factura.setCodigoDocumento(parametroService.getValor("CODIGO_DOCUMENTO_FACTURA", "01"));

            // Establecer fecha de emisión si no está configurada
            if (factura.getFechaEmision() == null) {
                factura.setFechaEmision(LocalDate.now());
            }

            // Calcular totales si no están configurados
            calcularTotalesFactura(factura);

            // Establecer datos de auditoría
            factura.setCreatedAt(LocalDateTime.now());
            factura.setUpdatedAt(LocalDateTime.now());

            // Por defecto, establecer estado pendiente
            if (factura.getEstadoFactura() == null || factura.getEstadoFactura().isEmpty()) {
                factura.setEstadoFactura("PENDIENTE");
            }

            // Estado SRI inicial
            factura.setEstadoSRI("PENDIENTE");

            // Guardar
            int id = facturaDAO.guardar(factura);
            return id > 0;
        } else {
            // Si es una actualización
            calcularTotalesFactura(factura);
            factura.setUpdatedAt(LocalDateTime.now());
            return facturaDAO.actualizar(factura);
        }
    }

    /**
     * Configura los datos del emisor desde parámetros
     */
    private void configurarDatosEmisor(Factura factura) {
        factura.setTipoDocumento(parametroService.getValor("TIPO_DOCUMENTO_FACTURA", "FACTURA"));
        factura.setRucEmisor(parametroService.getValor("RUC_EMPRESA", ""));
        factura.setRazonSocialEmisor(parametroService.getValor("RAZON_SOCIAL", ""));
        factura.setDireccionMatriz(parametroService.getValor("DIRECCION_MATRIZ", ""));
        factura.setDireccionSucursal(parametroService.getValor("DIRECCION_SUCURSAL", factura.getDireccionMatriz()));
        factura.setObligadoContabilidad(parametroService.getValorBoolean("OBLIGADO_CONTABILIDAD", false));
        factura.setCodigoEstablecimiento(parametroService.getValor("CODIGO_ESTABLECIMIENTO", "001"));
        factura.setCodigoPuntoEmision(parametroService.getValor("CODIGO_PUNTO_EMISION", "001"));
        factura.setAmbiente(parametroService.getValor("AMBIENTE_FACTURACION", "1")); // 1: Pruebas, 2: Producción
        factura.setEmision(parametroService.getValor("TIPO_EMISION", "1")); // 1: Normal
    }

    /**
     * Calcula los totales de la factura
     */
    private void calcularTotalesFactura(Factura factura) {
        BigDecimal subtotal12 = BigDecimal.ZERO;
        BigDecimal subtotal0 = BigDecimal.ZERO;
        BigDecimal subtotalNoObjetoIva = BigDecimal.ZERO;
        BigDecimal subtotalExentoIva = BigDecimal.ZERO;
        BigDecimal totalDescuento = BigDecimal.ZERO;
        BigDecimal valorIva = BigDecimal.ZERO;

        if (factura.getDetalles() != null) {
            // Obtener porcentaje de IVA actual
            BigDecimal porcentajeIva = parametroService.getValorBigDecimal("PORCENTAJE_IVA", new BigDecimal("12"));
            factura.setPorcentajeIva(porcentajeIva);

            for (FacturaDetalle detalle : factura.getDetalles()) {
                // Calcular subtotal del detalle
                BigDecimal subtotalDetalle = detalle.getCantidad().multiply(detalle.getPrecioUnitario());
                BigDecimal descuentoDetalle = detalle.getDescuento() != null ? detalle.getDescuento() : BigDecimal.ZERO;

                // Actualizar subtotal del detalle después del descuento
                BigDecimal subtotalNeto = subtotalDetalle.subtract(descuentoDetalle);
                detalle.setValorSubtotal(subtotalNeto);

                // Acumular descuento
                totalDescuento = totalDescuento.add(descuentoDetalle);

                // Clasificar por tipo de impuesto
                String tipoImpuesto = detalle.getTipoImpuesto();
                if (tipoImpuesto == null) {
                    tipoImpuesto = "IVA_12"; // Valor por defecto
                    detalle.setTipoImpuesto(tipoImpuesto);
                }

                // Establecer códigos de impuesto según tipo
                if ("IVA_12".equals(tipoImpuesto)) {
                    detalle.setCodigoImpuesto("2"); // Código del IVA
                    detalle.setCodigoTarifaIva("2"); // Tarifa 12%
                    detalle.setPorcentajeIva(porcentajeIva);
                    subtotal12 = subtotal12.add(subtotalNeto);

                    // Calcular IVA del detalle
                    BigDecimal ivaDetalle = subtotalNeto.multiply(porcentajeIva.divide(new BigDecimal("100")));
                    detalle.setValorIva(ivaDetalle);
                    valorIva = valorIva.add(ivaDetalle);

                } else if ("IVA_0".equals(tipoImpuesto)) {
                    detalle.setCodigoImpuesto("2"); // Código del IVA
                    detalle.setCodigoTarifaIva("0"); // Tarifa 0%
                    detalle.setPorcentajeIva(BigDecimal.ZERO);
                    detalle.setValorIva(BigDecimal.ZERO);
                    subtotal0 = subtotal0.add(subtotalNeto);

                } else if ("NO_OBJETO_IVA".equals(tipoImpuesto)) {
                    detalle.setCodigoImpuesto("2"); // Código del IVA
                    detalle.setCodigoTarifaIva("6"); // No objeto de IVA
                    detalle.setPorcentajeIva(BigDecimal.ZERO);
                    detalle.setValorIva(BigDecimal.ZERO);
                    subtotalNoObjetoIva = subtotalNoObjetoIva.add(subtotalNeto);

                } else if ("EXENTO_IVA".equals(tipoImpuesto)) {
                    detalle.setCodigoImpuesto("2"); // Código del IVA
                    detalle.setCodigoTarifaIva("7"); // Exento de IVA
                    detalle.setPorcentajeIva(BigDecimal.ZERO);
                    detalle.setValorIva(BigDecimal.ZERO);
                    subtotalExentoIva = subtotalExentoIva.add(subtotalNeto);
                }
            }
        }

        // Establecer subtotales
        factura.setSubtotal12(subtotal12);
        factura.setSubtotal0(subtotal0);
        factura.setSubtotalNoObjetoIva(subtotalNoObjetoIva);
        factura.setSubtotalExentoIva(subtotalExentoIva);
        factura.setTotalDescuento(totalDescuento);
        factura.setValorIva(valorIva);

        // Calcular subtotal sin impuestos
        BigDecimal subtotalSinImpuestos = subtotal12.add(subtotal0).add(subtotalNoObjetoIva).add(subtotalExentoIva);
        factura.setSubtotalSinImpuestos(subtotalSinImpuestos);

        // Calcular propina si existe
        BigDecimal propina = factura.getPropina() != null ? factura.getPropina() : BigDecimal.ZERO;

        // Calcular total
        BigDecimal valorTotal = subtotalSinImpuestos.add(valorIva).add(propina);
        factura.setValorTotal(valorTotal);

        // Calcular valores de subsidio si aplica
        if (factura.getValorSinSubsidio() != null && factura.getValorSinSubsidio().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal ahorroSubsidio = factura.getValorSinSubsidio().subtract(valorTotal);
            factura.setAhorroSubsidio(ahorroSubsidio);
        }
    }

    /**
     * Genera la clave de acceso para la factura
     */
    public String generarClaveAcceso(Factura factura) {
        DateTimeFormatter fechaFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String fecha = factura.getFechaEmision().format(fechaFormatter);
        String tipoComprobante = parametroService.getValor("CODIGO_DOCUMENTO_FACTURA", "01");
        String ruc = factura.getRucEmisor();
        String ambiente = factura.getAmbiente();
        String establecimiento = factura.getCodigoEstablecimiento();
        String puntoEmision = factura.getCodigoPuntoEmision();
        String secuencial = factura.getSecuencial();
        String codigoNumerico = "12345678"; // Este código debe ser generado de forma segura
        String tipoEmision = factura.getEmision();

        String claveAccesoSinDigito = fecha + tipoComprobante + ruc + ambiente + establecimiento + puntoEmision
                + secuencial + codigoNumerico + tipoEmision;
        int digitoVerificador = calcularModulo11(claveAccesoSinDigito);

        return claveAccesoSinDigito + digitoVerificador;
    }

    /**
     * Calcula el dígito verificador módulo 11
     */
    private int calcularModulo11(String cadena) {
        int[] coeficientes = { 2, 3, 4, 5, 6, 7 };
        int indiceCoeficiente = 0;
        int suma = 0;

        for (int i = cadena.length() - 1; i >= 0; i--) {
            int digito = Integer.parseInt(cadena.substring(i, i + 1));
            suma += digito * coeficientes[indiceCoeficiente];
            indiceCoeficiente = (indiceCoeficiente + 1) % coeficientes.length;
        }

        int digitoVerificador = 11 - (suma % 11);
        if (digitoVerificador == 11) {
            digitoVerificador = 0;
        } else if (digitoVerificador == 10) {
            digitoVerificador = 1;
        }

        return digitoVerificador;
    }

    /**
     * Obtiene una factura por su ID
     */
    public Factura obtenerFacturaPorId(int id) {
        return facturaDAO.obtenerPorId(id);
    }

    /**
     * Elimina una factura por su ID
     */
    public boolean eliminarFactura(int id) {
        return facturaDAO.eliminar(id);
    }

    /**
     * Obtiene todas las facturas
     */
    public List<Factura> obtenerTodasLasFacturas() {
        return facturaDAO.obtenerTodas();
    }

    /**
     * Busca facturas por filtros
     */
    public List<Factura> buscarFacturas(LocalDate fechaDesde, LocalDate fechaHasta,
            String cliente, String estado, String expediente) {
        return facturaDAO.buscarFacturas(fechaDesde, fechaHasta, cliente, estado, expediente);
    }

    /**
     * Genera el próximo número secuencial de factura
     * 
     * @return Próximo número secuencial en formato '000000001'
     */
    public String generarProximoSecuencial() {
        return facturaDAO.generarProximoSecuencial();
    }

    /**
     * Actualiza el estado de una factura
     */
    public boolean actualizarEstadoFactura(int id, String estado) {
        Factura factura = facturaDAO.obtenerPorId(id);
        if (factura != null) {
            factura.setEstadoFactura(estado);
            factura.setUpdatedAt(LocalDateTime.now());
            return facturaDAO.actualizar(factura);
        }
        return false;
    }

    /**
     * Registra el pago de una factura
     */
    public boolean registrarPagoFactura(int id) {
        Factura factura = facturaDAO.obtenerPorId(id);
        if (factura != null) {
            factura.setPagoRealizado(true);
            factura.setEstadoFactura("PAGADO");
            factura.setUpdatedAt(LocalDateTime.now());
            return facturaDAO.actualizar(factura);
        }
        return false;
    }
}
