package application.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modelo para representar un usuario en el sistema
 */
public class Usuario {

    // Enumeraciones para tipos
    public enum TipoIdentificacion {
        CEDULA, RUC, PASAPORTE
    }

    public enum TipoUsuario {
        NATURAL, JURIDICA
    }

    public enum Estado {
        ACTIVO, INACTIVO
    }

    // Atributos
    private int id;
    private String nombresCompletos;
    private String nombreUsuario;
    private String numeroIdentificacion;
    private TipoIdentificacion tipoIdentificacion;
    private String telefono;
    private String correo;
    private Estado estado;
    private String direccion;
    private LocalDate fechaIngreso;
    private TipoUsuario tipoUsuario;
    private String rol;
    private String clave;

    // Metadatos
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor vacío
    public Usuario() {
        this.fechaIngreso = LocalDate.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor completo
    public Usuario(String nombresCompletos, String nombreUsuario, String numeroIdentificacion,
                   TipoIdentificacion tipoIdentificacion, String telefono, String correo,
                   Estado estado, String direccion, LocalDate fechaIngreso,
                   TipoUsuario tipoUsuario, String rol, String clave) {
        this();
        this.nombresCompletos = nombresCompletos;
        this.nombreUsuario = nombreUsuario;
        this.numeroIdentificacion = numeroIdentificacion;
        this.tipoIdentificacion = tipoIdentificacion;
        this.telefono = telefono;
        this.correo = correo;
        this.estado = estado;
        this.direccion = direccion;
        this.fechaIngreso = fechaIngreso;
        this.tipoUsuario = tipoUsuario;
        this.rol = rol;
        this.clave = clave;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombresCompletos() { return nombresCompletos; }
    public void setNombresCompletos(String nombresCompletos) { this.nombresCompletos = nombresCompletos; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getNumeroIdentificacion() { return numeroIdentificacion; }
    public void setNumeroIdentificacion(String numeroIdentificacion) { this.numeroIdentificacion = numeroIdentificacion; }

    public TipoIdentificacion getTipoIdentificacion() { return tipoIdentificacion; }
    public void setTipoIdentificacion(TipoIdentificacion tipoIdentificacion) { this.tipoIdentificacion = tipoIdentificacion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Métodos de utilidad
    public boolean esActivo() {
        return estado == Estado.ACTIVO;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", nombresCompletos, nombreUsuario, rol);
    }
}