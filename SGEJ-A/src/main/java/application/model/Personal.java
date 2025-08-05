package application.model;

import java.time.LocalDate;

/**
 * Clase que representa a un empleado del sistema.
 */
public class Personal {
    private int id;
    private String nombres;
    private String apellidos;
    private String numeroIdentificacion;
    private String tipoIdentificacion;
    private String telefono;
    private String correo;
    private String direccion;
    private LocalDate fechaIngreso;
    private String rol;
    private String estado;

    /**
     * Constructor por defecto
     */
    public Personal() {
    }

    /**
     * Constructor con parámetros
     */
    public Personal(String nombres, String apellidos, String numeroIdentificacion, 
            String tipoIdentificacion, String telefono, String correo, 
            String direccion, LocalDate fechaIngreso, String rol, String estado) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.numeroIdentificacion = numeroIdentificacion;
        this.tipoIdentificacion = tipoIdentificacion;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.fechaIngreso = fechaIngreso;
        this.rol = rol;
        this.estado = estado;
    }

    /**
     * Constructor con ID
     */
    public Personal(int id, String nombres, String apellidos, String numeroIdentificacion, 
            String tipoIdentificacion, String telefono, String correo, 
            String direccion, LocalDate fechaIngreso, String rol, String estado) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.numeroIdentificacion = numeroIdentificacion;
        this.tipoIdentificacion = tipoIdentificacion;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.fechaIngreso = fechaIngreso;
        this.rol = rol;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public void setNumeroIdentificacion(String numeroIdentificacion) {
        this.numeroIdentificacion = numeroIdentificacion;
    }

    public String getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(String tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Retorna el nombre completo del empleado
     */
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    @Override
    public String toString() {
        return getNombreCompleto();
    }
}
