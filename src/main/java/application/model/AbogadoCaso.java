package application.model;

import java.util.Date;

public class AbogadoCaso {
    private int id;
    private int casoId;
    private int abogadoId;
    private String rol;
    private Date fechaAsignacion;

    // Getters y setters
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

    public int getAbogadoId() {
        return abogadoId;
    }

    public void setAbogadoId(int abogadoId) {
        this.abogadoId = abogadoId;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }
}
