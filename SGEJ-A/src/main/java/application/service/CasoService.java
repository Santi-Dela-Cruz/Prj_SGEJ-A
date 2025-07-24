package application.service;

import application.dao.CasoDAO;
import application.model.Caso;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CasoService {
    private CasoDAO casoDAO;

    public CasoService(Connection conn) {
        this.casoDAO = new CasoDAO(conn);
    }

    public void registrarCaso(Caso caso) throws SQLException {
        casoDAO.insertarCaso(caso);
    }

    public List<Caso> consultarCasos(String filtro) throws SQLException {
        return casoDAO.consultarCasos(filtro);
    }

    public void actualizarCaso(Caso caso) throws SQLException {
        casoDAO.actualizarCaso(caso);
    }

    // Otros métodos según necesidades
}
