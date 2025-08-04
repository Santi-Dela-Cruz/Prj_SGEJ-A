package application.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa un detalle de factura en el sistema
 */
public class FacturaDetalle {

    // Identificador único
    private int id;

    // Relación con factura
    private int facturaId;

    // Datos del servicio
    private int codigoServicio;
    private String codigoAuxiliar;
    private String descripcion;
    private BigDecimal cantidad = BigDecimal.ONE;
    private BigDecimal precioUnitario = BigDecimal.ZERO;
    private BigDecimal descuento = BigDecimal.ZERO;
    private BigDecimal valorSubtotal = BigDecimal.ZERO;

    // Impuestos
    private String codigoImpuesto;
    private String codigoTarifaIva;
    private BigDecimal porcentajeIva = BigDecimal.ZERO;
    private BigDecimal valorIva = BigDecimal.ZERO;
    private String tipoImpuesto; // IVA_12, IVA_0, NO_OBJETO_IVA, EXENTO_IVA

    // Auditoría
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String usuarioCreacion;
    private String usuarioModificacion;

    // Constructor vacío
    public FacturaDetalle() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor con parámetros
    public FacturaDetalle(int codigoServicio, String descripcion, BigDecimal cantidad,
            BigDecimal precioUnitario, BigDecimal descuento) {
        this();
        this.codigoServicio = codigoServicio;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.descuento = descuento;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFacturaId() {
        return facturaId;
    }

    public void setFacturaId(int facturaId) {
        this.facturaId = facturaId;
    }

    public int getCodigoServicio() {
        return codigoServicio;
    }

    public void setCodigoServicio(int codigoServicio) {
        this.codigoServicio = codigoServicio;
    }

    public String getCodigoAuxiliar() {
        return codigoAuxiliar;
    }

    public void setCodigoAuxiliar(String codigoAuxiliar) {
        this.codigoAuxiliar = codigoAuxiliar;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getValorSubtotal() {
        return valorSubtotal;
    }

    public void setValorSubtotal(BigDecimal valorSubtotal) {
        this.valorSubtotal = valorSubtotal;
    }

    public String getCodigoImpuesto() {
        return codigoImpuesto;
    }

    public void setCodigoImpuesto(String codigoImpuesto) {
        this.codigoImpuesto = codigoImpuesto;
    }

    public String getCodigoTarifaIva() {
        return codigoTarifaIva;
    }

    public void setCodigoTarifaIva(String codigoTarifaIva) {
        this.codigoTarifaIva = codigoTarifaIva;
    }

    public BigDecimal getPorcentajeIva() {
        return porcentajeIva;
    }

    public void setPorcentajeIva(BigDecimal porcentajeIva) {
        this.porcentajeIva = porcentajeIva;
    }

    public BigDecimal getValorIva() {
        return valorIva;
    }

    public void setValorIva(BigDecimal valorIva) {
        this.valorIva = valorIva;
    }

    public String getTipoImpuesto() {
        return tipoImpuesto;
    }

    public void setTipoImpuesto(String tipoImpuesto) {
        this.tipoImpuesto = tipoImpuesto;
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

    /**
     * Calcula el subtotal (precioUnitario * cantidad - descuento)
     */
    public BigDecimal calcularSubtotal() {
        BigDecimal subtotal = precioUnitario.multiply(cantidad);
        BigDecimal resultado = subtotal.subtract(descuento);
        // Actualizar el campo valorSubtotal
        this.valorSubtotal = resultado;
        return resultado;
    }

    /**
     * Calcula el IVA según el porcentaje especificado
     */
    public BigDecimal calcularIva() {
        BigDecimal subtotal = calcularSubtotal();
        return subtotal.multiply(porcentajeIva.divide(new BigDecimal(100)));
    }

    /**
     * Calcula el valor total (subtotal + IVA)
     */
    public BigDecimal calcularTotal() {
        return calcularSubtotal().add(calcularIva());
    }
}
