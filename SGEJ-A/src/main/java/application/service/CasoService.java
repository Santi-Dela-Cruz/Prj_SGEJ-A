package application.service;

import application.dao.CasoDAO;
import application.model.Caso;
import application.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CasoService {
    private CasoDAO casoDAO;
    private Connection conn;

    public CasoService() {
        try {
            this.conn = DatabaseConnection.getConnection();
            this.casoDAO = new CasoDAO(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CasoService(Connection conn) {
        this.conn = conn;
        this.casoDAO = new CasoDAO(conn);
    }

    public void registrarCaso(Caso caso) throws SQLException {
        casoDAO.insertarCaso(caso);
    }

    public List<Caso> consultarCasos(String filtro) throws SQLException {
        return casoDAO.consultarCasos(filtro);
    }

    /**
     * Busca casos por término para autocompletado
     * 
     * @param termino término de búsqueda
     * @return lista de casos que coinciden con el término
     */
    public List<Caso> buscarCasosPorTermino(String termino) {
        return casoDAO.buscarCasosPorTermino(termino);
    }

    /**
     * Valida si un número de expediente existe en la base de datos
     * 
     * @param numeroExpediente Número de expediente a validar
     * @return true si el caso existe, false en caso contrario
     */
    public boolean existeCasoPorNumero(String numeroExpediente) {
        if (numeroExpediente == null || numeroExpediente.trim().isEmpty()) {
            return false;
        }

        Caso caso = casoDAO.obtenerCasoPorNumero(numeroExpediente);
        return caso != null;
    }

    /**
     * Obtiene un caso por su número de expediente
     * 
     * @param numeroExpediente Número de expediente a buscar
     * @return Objeto Caso si existe, null en caso contrario
     */
    public Caso obtenerCasoPorNumero(String numeroExpediente) {
        if (numeroExpediente == null || numeroExpediente.trim().isEmpty()) {
            return null;
        }

        return casoDAO.obtenerCasoPorNumero(numeroExpediente);
    }

    /**
     * Verifica si un caso pertenece a un cliente específico
     * 
     * @param numeroExpediente Número de expediente del caso
     * @param clienteId        ID del cliente
     * @return true si el caso pertenece al cliente, false en caso contrario
     */
    public boolean casoPertenecaACliente(String numeroExpediente, int clienteId) {
        try {
            if (this.conn == null || this.conn.isClosed()) {
                this.conn = DatabaseConnection.getConnection();
                this.casoDAO = new CasoDAO(conn);
            }
            return casoDAO.casoPertenecaACliente(numeroExpediente, clienteId);
        } catch (SQLException e) {
            System.err.println("Error al verificar si el caso pertenece al cliente: " + e.getMessage());
            e.printStackTrace();
            return true; // En caso de error, permitimos continuar (asumimos que pertenece)
        }
    }

    /**
     * Obtiene un caso por su número de expediente
     * 
     * @param numeroExpediente el número de expediente del caso
     * @return el caso encontrado o null si no existe
     */
    public Caso obtenerCasoPorNumeroExpediente(String numeroExpediente) {
        return casoDAO.obtenerCasoPorNumeroExpediente(numeroExpediente);
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
