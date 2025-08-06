package application.service;

import application.dao.CasoDAO;
import application.model.Caso;
import application.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    public int registrarCaso(Caso caso) throws SQLException {
        // Insertar el caso y obtener el ID generado
        String sql = "INSERT INTO caso (cliente_id, numero_expediente, titulo, tipo, fecha_inicio, descripcion, estado";

        // Verificar si hay abogado_id para agregar a la consulta
        if (caso.getAbogadoId() > 0) {
            sql += ", abogado_id";
        }

        sql += ") VALUES (?, ?, ?, ?, ?, ?, ?";

        if (caso.getAbogadoId() > 0) {
            sql += ", ?";
        }

        sql += ")";

        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, caso.getClienteId());
            stmt.setString(2, caso.getNumeroExpediente());
            stmt.setString(3, caso.getTitulo());
            stmt.setString(4, caso.getTipo());
            stmt.setDate(5, caso.getFechaInicio() != null ? new java.sql.Date(caso.getFechaInicio().getTime()) : null);
            stmt.setString(6, caso.getDescripcion());
            stmt.setString(7, caso.getEstado());

            // Agregar abogado_id si existe
            if (caso.getAbogadoId() > 0) {
                stmt.setInt(8, caso.getAbogadoId());
            }

            stmt.executeUpdate();

            // Obtener el ID generado
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("No se pudo obtener el ID del caso creado");
            }
        }
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
