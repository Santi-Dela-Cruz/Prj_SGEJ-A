package application.model;

import java.time.LocalDateTime;

/**
 * Modelo para representar un usuario en el sistema
 */
public class Usuario {

    // Enumeraciones para tipos
    public enum TipoUsuario {
        NATURAL, JURIDICA, INTERNO, EXTERNO, OTRO
    }

    public enum EstadoUsuario {
        ACTIVO, INACTIVO, SUSPENDIDO
    }

    // Atributos
    private Integer id;
    private String nombres;
    private String apellidos;
    private String identificacion;
    private String email;
    private String nombreUsuario;
    private String clave;
    private TipoUsuario tipoUsuario;
    private EstadoUsuario estadoUsuario;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor vacío
    public Usuario() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.estadoUsuario = EstadoUsuario.ACTIVO;
        this.tipoUsuario = TipoUsuario.INTERNO;
    }

    // Constructor completo
    public Usuario(Integer id, String nombres, String apellidos, String identificacion, String email,
            String nombreUsuario, String clave, TipoUsuario tipoUsuario, EstadoUsuario estadoUsuario) {
        this();
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.identificacion = identificacion;
        this.email = email;
        this.nombreUsuario = nombreUsuario;
        this.clave = clave;
        this.tipoUsuario = tipoUsuario;
        this.estadoUsuario = estadoUsuario;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public EstadoUsuario getEstadoUsuario() {
        return estadoUsuario;
    }

    public void setEstadoUsuario(EstadoUsuario estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
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

    // Métodos de utilidad
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    public String getTipoUsuarioString() {
        return tipoUsuario != null ? tipoUsuario.toString() : "";
    }

    public String getEstadoUsuarioString() {
        return estadoUsuario != null ? estadoUsuario.toString() : "";
    }

    public boolean esActivo() {
        return estadoUsuario == EstadoUsuario.ACTIVO;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", getNombreCompleto(), nombreUsuario);
    }
}
