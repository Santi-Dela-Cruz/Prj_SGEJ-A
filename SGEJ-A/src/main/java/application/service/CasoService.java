package application.service;

import application.dao.CasoDAO;
import application.model.Caso;
import application.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CasoService {
    private CasoDAO casoDAO;

    public CasoService() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            this.casoDAO = new CasoDAO(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    /**
     * Actualiza solo el estado de un caso en la base de datos.
     * 
     * @param numeroExpediente El número de expediente del caso a actualizar
     * @param nuevoEstado      El nuevo estado del caso
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public boolean actualizarEstadoCaso(String numeroExpediente, String nuevoEstado) {
        try {
            return casoDAO.actualizarEstadoCaso(numeroExpediente, nuevoEstado);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Otros métodos según necesidades
}
