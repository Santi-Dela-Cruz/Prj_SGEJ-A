package application.service;

import application.dao.HistorialComunicacionDAO;
import application.model.HistorialComunicacion;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class HistorialComunicacionService {
    private HistorialComunicacionDAO dao;

    public HistorialComunicacionService(Connection conn) {
        this.dao = new HistorialComunicacionDAO(conn);
    }

    public void registrarComunicacion(HistorialComunicacion com) throws SQLException {
        dao.insertarComunicacion(com);
    }

    public List<HistorialComunicacion> obtenerPorCaso(int casoId) throws SQLException {
        return dao.consultarPorCaso(casoId);
    }
}
