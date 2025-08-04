package application.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una factura en el sistema
 */
public class Factura {

    // Identificador único
    private int id;

    // Datos generales
    private String tipoDocumento;
    private String claveAcceso;
    private String numeroAutorizacion;
    private LocalDateTime fechaAutorizacion;
    private String ambiente;
    private String emision;

    // Datos del emisor
    private String rucEmisor;
    private String razonSocialEmisor;
    private String direccionMatriz;
    private String direccionSucursal;
    private boolean obligadoContabilidad;

    // Datos del establecimiento y secuencia
    private String codigoEstablecimiento;
    private String codigoPuntoEmision;
    private String secuencial;
    private String codigoDocumento;

    // Fecha de emisión
    private LocalDate fechaEmision;

    // Datos del cliente
    private String nombreCliente;
    private String idCliente;
    private String tipoIdentificacion;
    private String direccionCliente;
    private String telefonoCliente;
    private String emailCliente;

    // Caso relacionado
    private String numeroExpediente;
    private String nombreCaso;
    private String abogadoResponsable;

    // Totales
    private BigDecimal subtotal12 = BigDecimal.ZERO;
    private BigDecimal subtotal0 = BigDecimal.ZERO;
    private BigDecimal subtotalNoObjetoIva = BigDecimal.ZERO;
    private BigDecimal subtotalExentoIva = BigDecimal.ZERO;
    private BigDecimal subtotalSinImpuestos = BigDecimal.ZERO;
    private BigDecimal totalDescuento = BigDecimal.ZERO;
    private BigDecimal valorIva = BigDecimal.ZERO;
    private BigDecimal porcentajeIva = BigDecimal.ZERO;
    private BigDecimal devolucionIva = BigDecimal.ZERO;
    private BigDecimal propina = BigDecimal.ZERO;
    private BigDecimal valorTotal = BigDecimal.ZERO;

    // Subsidio
    private BigDecimal valorSinSubsidio = BigDecimal.ZERO;
    private BigDecimal ahorroSubsidio = BigDecimal.ZERO;

    // Pago
    private String formaPago;
    private BigDecimal montoPago = BigDecimal.ZERO;
    private int plazo = 0;
    private String estadoFactura;
    private boolean pagoRealizado;

    // Auditoría
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String usuarioCreacion;
    private String usuarioModificacion;

    // SRI
    private String estadoSRI = "PENDIENTE";
    private String respuestaSRI;
    private LocalDateTime fechaRespuestaSRI;

    // Detalles de la factura
    private List<FacturaDetalle> detalles = new ArrayList<>();

    // Constructor vacío
    public Factura() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getClaveAcceso() {
        return claveAcceso;
    }

    public void setClaveAcceso(String claveAcceso) {
        this.claveAcceso = claveAcceso;
    }

    public String getNumeroAutorizacion() {
        return numeroAutorizacion;
    }

    public void setNumeroAutorizacion(String numeroAutorizacion) {
        this.numeroAutorizacion = numeroAutorizacion;
    }

    public LocalDateTime getFechaAutorizacion() {
        return fechaAutorizacion;
    }

    public void setFechaAutorizacion(LocalDateTime fechaAutorizacion) {
        this.fechaAutorizacion = fechaAutorizacion;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    public String getEmision() {
        return emision;
    }

    public void setEmision(String emision) {
        this.emision = emision;
    }

    public String getRucEmisor() {
        return rucEmisor;
    }

    public void setRucEmisor(String rucEmisor) {
        this.rucEmisor = rucEmisor;
    }

    public String getRazonSocialEmisor() {
        return razonSocialEmisor;
    }

    public void setRazonSocialEmisor(String razonSocialEmisor) {
        this.razonSocialEmisor = razonSocialEmisor;
    }

    public String getDireccionMatriz() {
        return direccionMatriz;
    }

    public void setDireccionMatriz(String direccionMatriz) {
        this.direccionMatriz = direccionMatriz;
    }

    public String getDireccionSucursal() {
        return direccionSucursal;
    }

    public void setDireccionSucursal(String direccionSucursal) {
        this.direccionSucursal = direccionSucursal;
    }

    public boolean isObligadoContabilidad() {
        return obligadoContabilidad;
    }

    public void setObligadoContabilidad(boolean obligadoContabilidad) {
        this.obligadoContabilidad = obligadoContabilidad;
    }

    public String getCodigoEstablecimiento() {
        return codigoEstablecimiento;
    }

    public void setCodigoEstablecimiento(String codigoEstablecimiento) {
        this.codigoEstablecimiento = codigoEstablecimiento;
    }

    public String getCodigoPuntoEmision() {
        return codigoPuntoEmision;
    }

    public void setCodigoPuntoEmision(String codigoPuntoEmision) {
        this.codigoPuntoEmision = codigoPuntoEmision;
    }

    public String getSecuencial() {
        return secuencial;
    }

    public void setSecuencial(String secuencial) {
        this.secuencial = secuencial;
    }

    public String getCodigoDocumento() {
        return codigoDocumento;
    }

    public void setCodigoDocumento(String codigoDocumento) {
        this.codigoDocumento = codigoDocumento;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(String tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public String getDireccionCliente() {
        return direccionCliente;
    }

    public void setDireccionCliente(String direccionCliente) {
        this.direccionCliente = direccionCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }

    public String getNumeroExpediente() {
        return numeroExpediente;
    }

    public void setNumeroExpediente(String numeroExpediente) {
        this.numeroExpediente = numeroExpediente;
    }

    public String getNombreCaso() {
        return nombreCaso;
    }

    public void setNombreCaso(String nombreCaso) {
        this.nombreCaso = nombreCaso;
    }

    public String getAbogadoResponsable() {
        return abogadoResponsable;
    }

    public void setAbogadoResponsable(String abogadoResponsable) {
        this.abogadoResponsable = abogadoResponsable;
    }

    public BigDecimal getSubtotal12() {
        return subtotal12;
    }

    public void setSubtotal12(BigDecimal subtotal12) {
        this.subtotal12 = subtotal12;
    }

    public BigDecimal getSubtotal0() {
        return subtotal0;
    }

    public void setSubtotal0(BigDecimal subtotal0) {
        this.subtotal0 = subtotal0;
    }

    public BigDecimal getSubtotalNoObjetoIva() {
        return subtotalNoObjetoIva;
    }

    public void setSubtotalNoObjetoIva(BigDecimal subtotalNoObjetoIva) {
        this.subtotalNoObjetoIva = subtotalNoObjetoIva;
    }

    public BigDecimal getSubtotalExentoIva() {
        return subtotalExentoIva;
    }

    public void setSubtotalExentoIva(BigDecimal subtotalExentoIva) {
        this.subtotalExentoIva = subtotalExentoIva;
    }

    public BigDecimal getSubtotalSinImpuestos() {
        return subtotalSinImpuestos;
    }

    public void setSubtotalSinImpuestos(BigDecimal subtotalSinImpuestos) {
        this.subtotalSinImpuestos = subtotalSinImpuestos;
    }

    public BigDecimal getTotalDescuento() {
        return totalDescuento;
    }

    public void setTotalDescuento(BigDecimal totalDescuento) {
        this.totalDescuento = totalDescuento;
    }

    public BigDecimal getValorIva() {
        return valorIva;
    }

    public void setValorIva(BigDecimal valorIva) {
        this.valorIva = valorIva;
    }

    public BigDecimal getPorcentajeIva() {
        return porcentajeIva;
    }

    public void setPorcentajeIva(BigDecimal porcentajeIva) {
        this.porcentajeIva = porcentajeIva;
    }

    public BigDecimal getDevolucionIva() {
        return devolucionIva;
    }

    public void setDevolucionIva(BigDecimal devolucionIva) {
        this.devolucionIva = devolucionIva;
    }

    public BigDecimal getPropina() {
        return propina;
    }

    public void setPropina(BigDecimal propina) {
        this.propina = propina;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorSinSubsidio() {
        return valorSinSubsidio;
    }

    public void setValorSinSubsidio(BigDecimal valorSinSubsidio) {
        this.valorSinSubsidio = valorSinSubsidio;
    }

    public BigDecimal getAhorroSubsidio() {
        return ahorroSubsidio;
    }

    public void setAhorroSubsidio(BigDecimal ahorroSubsidio) {
        this.ahorroSubsidio = ahorroSubsidio;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public BigDecimal getMontoPago() {
        return montoPago;
    }

    public void setMontoPago(BigDecimal montoPago) {
        this.montoPago = montoPago;
    }

    public int getPlazo() {
        return plazo;
    }

    public void setPlazo(int plazo) {
        this.plazo = plazo;
    }

    public String getEstadoFactura() {
        return estadoFactura;
    }

    public void setEstadoFactura(String estadoFactura) {
        this.estadoFactura = estadoFactura;
    }

    public boolean isPagoRealizado() {
        return pagoRealizado;
    }

    public void setPagoRealizado(boolean pagoRealizado) {
        this.pagoRealizado = pagoRealizado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(String usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }

    public String getUsuarioModificacion() {
        return usuarioModificacion;
    }

    public void setUsuarioModificacion(String usuarioModificacion) {
        this.usuarioModificacion = usuarioModificacion;
    }

    public String getEstadoSRI() {
        return estadoSRI;
    }

    public void setEstadoSRI(String estadoSRI) {
        this.estadoSRI = estadoSRI;
    }

    public String getRespuestaSRI() {
        return respuestaSRI;
    }

    public void setRespuestaSRI(String respuestaSRI) {
        this.respuestaSRI = respuestaSRI;
    }

    public LocalDateTime getFechaRespuestaSRI() {
        return fechaRespuestaSRI;
    }

    public void setFechaRespuestaSRI(LocalDateTime fechaRespuestaSRI) {
        this.fechaRespuestaSRI = fechaRespuestaSRI;
    }

    public List<FacturaDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<FacturaDetalle> detalles) {
        this.detalles = detalles;
    }

    /**
     * Agrega un detalle a la factura
     */
    public void agregarDetalle(FacturaDetalle detalle) {
        if (this.detalles == null) {
            this.detalles = new ArrayList<>();
        }
        this.detalles.add(detalle);
    }
}
