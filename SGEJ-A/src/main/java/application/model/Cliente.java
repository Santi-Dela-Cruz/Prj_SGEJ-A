package application.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modelo para representar un cliente en el sistema
 */
public class Cliente {
    
    // Enumeraciones para tipos
    public enum TipoIdentificacion {
        CEDULA, RUC, PASAPORTE
    }
    
    public enum TipoPersona {
        NATURAL, JURIDICA
    }
    
    public enum Estado {
        ACTIVO, INACTIVO
    }
    
    // Atributos
    private int id;
    private String nombreCompleto;
    private TipoIdentificacion tipoIdentificacion;
    private TipoPersona tipoPersona;
    private String numeroIdentificacion;
    private String direccion;
    private String telefono;
    private String correoElectronico;
    private Estado estado;
    private LocalDate fechaRegistro;
    
    // Atributos para personas naturales
    private String estadoCivil;
    
    // Atributos para personas jurídicas
    private String representanteLegal;
    private String direccionFiscal;
    
    // Metadatos
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructor vacío
    public Cliente() {
        this.fechaRegistro = LocalDate.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor completo
    public Cliente(String nombreCompleto, TipoIdentificacion tipoIdentificacion, 
                  TipoPersona tipoPersona, String numeroIdentificacion, 
                  String direccion, String telefono, String correoElectronico, 
                  Estado estado) {
        this();
        this.nombreCompleto = nombreCompleto;
        this.tipoIdentificacion = tipoIdentificacion;
        this.tipoPersona = tipoPersona;
        this.numeroIdentificacion = numeroIdentificacion;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
        this.estado = estado;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    
    public TipoIdentificacion getTipoIdentificacion() { return tipoIdentificacion; }
    public void setTipoIdentificacion(TipoIdentificacion tipoIdentificacion) { this.tipoIdentificacion = tipoIdentificacion; }
    
    public TipoPersona getTipoPersona() { return tipoPersona; }
    public void setTipoPersona(TipoPersona tipoPersona) { this.tipoPersona = tipoPersona; }
    
    public String getNumeroIdentificacion() { return numeroIdentificacion; }
    public void setNumeroIdentificacion(String numeroIdentificacion) { this.numeroIdentificacion = numeroIdentificacion; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }
    
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    public String getEstadoCivil() { return estadoCivil; }
    public void setEstadoCivil(String estadoCivil) { this.estadoCivil = estadoCivil; }
    
    public String getRepresentanteLegal() { return representanteLegal; }
    public void setRepresentanteLegal(String representanteLegal) { this.representanteLegal = representanteLegal; }
    
    public String getDireccionFiscal() { return direccionFiscal; }
    public void setDireccionFiscal(String direccionFiscal) { this.direccionFiscal = direccionFiscal; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Métodos de utilidad
    public boolean esPersonaNatural() {
        return tipoPersona == TipoPersona.NATURAL;
    }
    
    public boolean esPersonaJuridica() {
        return tipoPersona == TipoPersona.JURIDICA;
    }
    
    public boolean estaActivo() {
        return estado == Estado.ACTIVO;
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s (%s)", 
                nombreCompleto, 
                numeroIdentificacion, 
                tipoIdentificacion.name());
    }
}
