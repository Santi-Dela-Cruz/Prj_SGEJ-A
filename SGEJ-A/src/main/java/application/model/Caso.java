package application.model;

import java.util.Date;
import java.util.List;

public class Caso {
    private int id;
    private int clienteId;
    private Cliente cliente; // Relación con cliente
    private String numeroExpediente;
    private String titulo;
    private String tipo;
    private Date fechaInicio;
    private String descripcion;
    private String estado;
    private int abogadoId; // ID del abogado principal asignado
    private Personal abogado; // Abogado principal asignado
    private List<AbogadoCaso> abogados; // Lista de abogados asignados

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getNumeroExpediente() {
        return numeroExpediente;
    }

    public void setNumeroExpediente(String numeroExpediente) {
        this.numeroExpediente = numeroExpediente;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getAbogadoId() {
        return abogadoId;
    }

    public void setAbogadoId(int abogadoId) {
        this.abogadoId = abogadoId;
    }

    public Personal getAbogado() {
        return abogado;
    }

    public void setAbogado(Personal abogado) {
        this.abogado = abogado;
    }

    public List<AbogadoCaso> getAbogados() {
        return abogados;
    }

    public void setAbogados(List<AbogadoCaso> abogados) {
        this.abogados = abogados;
    }
}
