package application.dao;

import application.database.DatabaseConnection;
import application.model.Caso;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CasoDAO {
    private Connection conn;

    public CasoDAO(Connection conn) {
        this.conn = conn;
    }

    public CasoDAO() {
        // Constructor sin parámetros para obtener la conexión al momento de usarla
    }

    /**
     * Busca casos por término incremental
     * 
     * @param termino el término de búsqueda (parte del título, número de expediente
     *                o descripción)
     * @return lista de casos que coinciden con el término
     */
    public List<Caso> buscarCasosPorTermino(String termino) {
        List<Caso> casos = new ArrayList<>();
        String sql = "SELECT * FROM caso WHERE titulo LIKE ? OR numero_expediente LIKE ? OR descripcion LIKE ? LIMIT 10";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + termino + "%");
            stmt.setString(2, "%" + termino + "%");
            stmt.setString(3, "%" + termino + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Caso caso = new Caso();
                    caso.setId(rs.getInt("id"));
                    caso.setClienteId(rs.getInt("cliente_id"));
                    caso.setNumeroExpediente(rs.getString("numero_expediente"));
                    caso.setTitulo(rs.getString("titulo"));
                    caso.setTipo(rs.getString("tipo"));
                    caso.setFechaInicio(rs.getDate("fecha_inicio"));
                    caso.setDescripcion(rs.getString("descripcion"));
                    caso.setEstado(rs.getString("estado"));

                    // Intentar obtener el abogado_id si la columna existe
                    try {
                        int abogadoId = rs.getInt("abogado_id");
                        caso.setAbogadoId(abogadoId);

                        // Si hay un abogado asignado, cargar sus datos
                        if (abogadoId > 0) {
                            application.dao.PersonalDAO personalDAO = new application.dao.PersonalDAO();
                            application.model.Personal abogado = personalDAO.obtenerPersonalPorId(abogadoId);
                            caso.setAbogado(abogado);
                        }
                    } catch (SQLException ex) {
                        // Si la columna no existe, simplemente continuamos
                        System.out.println("Nota: La columna abogado_id no existe en la tabla caso.");
                    }

                    casos.add(caso);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return casos;
    }

    public void insertarCaso(Caso caso) throws SQLException {
        String sql = "INSERT INTO caso (cliente_id, numero_expediente, titulo, tipo, fecha_inicio, descripcion, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, caso.getClienteId());
            stmt.setString(2, caso.getNumeroExpediente());
            stmt.setString(3, caso.getTitulo());
            stmt.setString(4, caso.getTipo());
            stmt.setDate(5, new java.sql.Date(caso.getFechaInicio().getTime()));
            stmt.setString(6, caso.getDescripcion());
            stmt.setString(7, caso.getEstado());
            stmt.executeUpdate();
        }
    }

    public List<Caso> consultarCasos(String filtro) throws SQLException {
        List<Caso> casos = new ArrayList<>();
        String sql = "SELECT * FROM caso WHERE titulo LIKE ? OR tipo LIKE ? OR estado LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            stmt.setString(2, "%" + filtro + "%");
            stmt.setString(3, "%" + filtro + "%");
            ResultSet rs = stmt.executeQuery();
            application.dao.ClienteDAO clienteDAO = new application.dao.ClienteDAO();
            while (rs.next()) {
                Caso caso = new Caso();
                caso.setId(rs.getInt("id"));
                caso.setClienteId(rs.getInt("cliente_id"));
                caso.setNumeroExpediente(rs.getString("numero_expediente"));
                caso.setTitulo(rs.getString("titulo"));
                caso.setTipo(rs.getString("tipo"));
                caso.setFechaInicio(rs.getDate("fecha_inicio"));
                caso.setDescripcion(rs.getString("descripcion"));
                caso.setEstado(rs.getString("estado"));
                // Relación completa
                application.model.Cliente cliente = clienteDAO.obtenerClientePorId(caso.getClienteId());
                caso.setCliente(cliente);
                casos.add(caso);
            }
        }
        return casos;
    }

    /**
     * Obtiene un caso por su número de expediente
     * 
     * @param numeroExpediente El número de expediente del caso
     * @return El caso si existe, null si no se encuentra
     */
    public Caso obtenerCasoPorNumero(String numeroExpediente) {
        if (numeroExpediente == null || numeroExpediente.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT * FROM caso WHERE numero_expediente = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Caso caso = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, numeroExpediente.trim());
            rs = stmt.executeQuery();

            if (rs.next()) {
                caso = new Caso();
                caso.setId(rs.getInt("id"));
                caso.setClienteId(rs.getInt("cliente_id"));
                caso.setNumeroExpediente(rs.getString("numero_expediente"));
                caso.setTitulo(rs.getString("titulo"));
                caso.setTipo(rs.getString("tipo"));
                caso.setFechaInicio(rs.getDate("fecha_inicio"));
                caso.setDescripcion(rs.getString("descripcion"));
                caso.setEstado(rs.getString("estado"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // No cerramos la conexión aquí porque podría ser compartida
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return caso;
    }

    public void actualizarCaso(Caso caso) throws SQLException {
        String sql = "UPDATE caso SET titulo=?, tipo=?, descripcion=?, abogado=?, estado=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, caso.getTitulo());
            stmt.setString(2, caso.getTipo());
            stmt.setString(3, caso.getDescripcion());
            // stmt.setString(4, caso.getAbogado()); // Si tienes campo abogado
            stmt.setString(4, caso.getEstado());
            stmt.setInt(5, caso.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Verifica si un caso pertenece a un cliente específico
     * 
     * @param numeroExpediente Número de expediente del caso
     * @param clienteId        ID del cliente
     * @return true si el caso pertenece al cliente, false en caso contrario
     * @throws SQLException si ocurre un error al consultar la base de datos
     */
    public boolean casoPertenecaACliente(String numeroExpediente, int clienteId) throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DatabaseConnection.getConnection();
        }

        String sql = "SELECT COUNT(*) FROM caso WHERE numero_expediente = ? AND cliente_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numeroExpediente);
            stmt.setInt(2, clienteId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar si el caso pertenece al cliente: " + e.getMessage());
            throw e;
        }

        return false;
    }

    /**
     * Obtiene un caso por su número de expediente
     * 
     * @param numeroExpediente el número de expediente del caso
     * @return el caso encontrado o null si no existe
     */
    public Caso obtenerCasoPorNumeroExpediente(String numeroExpediente) {
        String sql = "SELECT * FROM caso WHERE numero_expediente = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numeroExpediente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Caso caso = new Caso();
                    caso.setId(rs.getInt("id"));
                    caso.setClienteId(rs.getInt("cliente_id"));
                    caso.setNumeroExpediente(rs.getString("numero_expediente"));
                    caso.setTitulo(rs.getString("titulo"));
                    caso.setTipo(rs.getString("tipo"));
                    caso.setFechaInicio(rs.getDate("fecha_inicio"));
                    caso.setDescripcion(rs.getString("descripcion"));
                    caso.setEstado(rs.getString("estado"));
                    return caso;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Otros métodos según necesidades

    /**
     * Actualiza solo el estado de un caso en la base de datos.
     * 
     * @param numeroExpediente El número de expediente del caso a actualizar
     * @param nuevoEstado      El nuevo estado del caso
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public boolean actualizarEstadoCaso(String numeroExpediente, String nuevoEstado) throws SQLException {
        System.out.println("DEBUG: Actualizando estado para caso " + numeroExpediente + " a: " + nuevoEstado);

        String sql = "UPDATE caso SET estado=? WHERE numero_expediente=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuevoEstado);
            stmt.setString(2, numeroExpediente);
            int filasAfectadas = stmt.executeUpdate();

            System.out.println("DEBUG: Actualización de estado completada. Filas afectadas: " + filasAfectadas);
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("ERROR al actualizar estado del caso: " + e.getMessage());
            System.err.println("Número de expediente: " + numeroExpediente + ", Nuevo estado: " + nuevoEstado);
            throw e;
        }
    }
}
