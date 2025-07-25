package application.model;

import java.util.Date;

public class HistorialComunicacion {
    private int id;
    private int casoId;
    private String tipo;
    private Date fecha;
    private String descripcion;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCasoId() {
        return casoId;
    }

    public void setCasoId(int casoId) {
        this.casoId = casoId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
