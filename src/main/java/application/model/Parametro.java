package application.model;

import java.time.LocalDateTime;

/**
 * Modelo para representar un parámetro del sistema
 */
public class Parametro {

    // Enumeraciones para tipo y estado
    public enum Tipo {
        NUMERICO, TEXTO, TIEMPO
    }

    public enum Estado {
        ACTIVO, INACTIVO
    }

    // Atributos
    private String codigo;
    private String nombre;
    private String descripcion;
    private String valor;
    private Tipo tipo;
    private Estado estado;

    // Metadatos
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor vacío
    public Parametro() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor completo
    public Parametro(String codigo, String nombre, String descripcion, String valor, String tipo, String estado) {
        this();
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.valor = valor;
        this.tipo = Tipo.valueOf(tipo.toUpperCase());
        this.estado = Estado.valueOf(estado.toUpperCase());
    }

    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }

    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", codigo, nombre, tipo);
    }
}