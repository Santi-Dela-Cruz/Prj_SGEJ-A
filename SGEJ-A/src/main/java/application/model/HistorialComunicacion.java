package application.model;

import java.util.Date;

public class HistorialComunicacion {
    private int id;
    private int casoId;
    private String tipo;
    private Date fecha;
    private String descripcion;
    private int abogadoId;
    private String abogadoNombre; // Campo para almacenar el nombre del abogado (no persiste en BD)

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

    public int getAbogadoId() {
        return abogadoId;
    }

    public void setAbogadoId(int abogadoId) {
        this.abogadoId = abogadoId;
    }

    public String getAbogadoNombre() {
        return abogadoNombre;
    }

    public void setAbogadoNombre(String abogadoNombre) {
        this.abogadoNombre = abogadoNombre;
    }
}
