package application.dao;

import application.model.HistorialComunicacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistorialComunicacionDAO {
    private Connection conn;

    public HistorialComunicacionDAO(Connection conn) {
        this.conn = conn;
    }

    public void insertarComunicacion(HistorialComunicacion com) throws SQLException {
        // Primero intentamos ejecutar la migración para asegurar que las columnas existan
        try {
            ejecutarMigracion();
        } catch (Exception e) {
            System.out.println("Advertencia al ejecutar migración: " + e.getMessage());
            // Continuamos incluso si hay error, para intentar la inserción de todas formas
        }
        
        String sql = "INSERT INTO historial_comunicacion (caso_id, tipo, fecha, descripcion, abogado_id, abogado_nombre) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, com.getCasoId());
            stmt.setString(2, com.getTipo());
            stmt.setDate(3, new java.sql.Date(com.getFecha().getTime()));
            stmt.setString(4, com.getDescripcion());
            stmt.setInt(5, com.getAbogadoId());
            stmt.setString(6, com.getAbogadoNombre());
            stmt.executeUpdate();
        }
    }
    
    /**
     * Ejecuta el script SQL para añadir las columnas necesarias a la tabla historial_comunicacion
     */
    private void ejecutarMigracion() throws SQLException {
        String[] alterTableStatements = {
            "ALTER TABLE historial_comunicacion ADD COLUMN IF NOT EXISTS abogado_id INTEGER",
            "ALTER TABLE historial_comunicacion ADD COLUMN IF NOT EXISTS abogado_nombre TEXT"
        };
        
        // Intentamos cada sentencia ALTER TABLE por separado
        Statement stmt = conn.createStatement();
        for (String sql : alterTableStatements) {
            try {
                stmt.execute(sql);
            } catch (SQLException e) {
                // Si la base de datos no soporta "IF NOT EXISTS", intentamos verificar si la columna existe primero
                if (e.getMessage().contains("syntax error")) {
                    // SQLite versiones antiguas no soportan ADD COLUMN IF NOT EXISTS
                    // Extraemos el nombre de la columna para verificar si existe
                    String columnName = sql.contains("abogado_id") ? "abogado_id" : "abogado_nombre";
                    if (!columnExists("historial_comunicacion", columnName)) {
                        // Si la columna no existe, ejecutamos el ALTER TABLE sin el IF NOT EXISTS
                        stmt.execute("ALTER TABLE historial_comunicacion ADD COLUMN " + columnName + 
                                    (columnName.equals("abogado_id") ? " INTEGER" : " TEXT"));
                    }
                } else {
                    throw e; // Propagar otros errores SQL
                }
            }
        }
        stmt.close();
    }
    
    /**
     * Verifica si una columna existe en una tabla
     */
    private boolean columnExists(String tableName, String columnName) throws SQLException {
        ResultSet rs = conn.getMetaData().getColumns(null, null, tableName, columnName);
        boolean exists = rs.next();
        rs.close();
        return exists;
    }

    public List<HistorialComunicacion> consultarPorCaso(int casoId) throws SQLException {
        List<HistorialComunicacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM historial_comunicacion WHERE caso_id = ? ORDER BY fecha DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, casoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                HistorialComunicacion com = new HistorialComunicacion();
                com.setId(rs.getInt("id"));
                com.setCasoId(rs.getInt("caso_id"));
                com.setTipo(rs.getString("tipo"));
                com.setFecha(rs.getDate("fecha"));
                com.setDescripcion(rs.getString("descripcion"));

                // Manejar el nuevo campo abogado_id (podría ser nulo en registros antiguos)
                try {
                    com.setAbogadoId(rs.getInt("abogado_id"));

                    // Opcionalmente, podemos cargar el nombre del abogado aquí
                    obtenerNombreAbogado(com);
                } catch (SQLException e) {
                    // Si el campo no existe en registros antiguos, simplemente continúe
                    System.out.println("Campo abogado_id no encontrado en el registro: " + e.getMessage());
                }
                lista.add(com);
            }
        }
        return lista;
    }

    /**
     * Obtiene el nombre del abogado a partir del ID
     * 
     * @param comunicacion El objeto HistorialComunicacion que contiene el ID del
     *                     abogado
     */
    private void obtenerNombreAbogado(HistorialComunicacion comunicacion) {
        if (comunicacion.getAbogadoId() <= 0) {
            comunicacion.setAbogadoNombre("No especificado");
            return;
        }

        String sql = "SELECT nombres, apellidos FROM personal WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, comunicacion.getAbogadoId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nombres = rs.getString("nombres");
                String apellidos = rs.getString("apellidos");
                comunicacion.setAbogadoNombre(nombres + " " + apellidos);
            } else {
                comunicacion.setAbogadoNombre("Desconocido");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el nombre del abogado: " + e.getMessage());
            comunicacion.setAbogadoNombre("Error al cargar");
        }
    }

    /**
     * Verifica si un abogado existe en la base de datos
     * 
     * @param abogadoId ID del abogado a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean verificarExistenciaAbogado(int abogadoId) {
        String sql = "SELECT id FROM personal WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, abogadoId);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Si hay al menos un resultado, el abogado existe
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de abogado: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si un caso existe en la base de datos
     * 
     * @param casoId ID del caso a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean verificarExistenciaCaso(int casoId) {
        String sql = "SELECT id FROM caso WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, casoId);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Si hay al menos un resultado, el caso existe
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de caso: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene ID de un caso a partir del número de expediente
     * 
     * @param numeroExpediente Número de expediente a buscar
     * @return ID del caso o -1 si no se encuentra
     */
    public int obtenerIdCasoPorExpediente(String numeroExpediente) {
        String sql = "SELECT id FROM caso WHERE numero_expediente = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numeroExpediente);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ID del caso por expediente: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * Obtiene todas las comunicaciones registradas en la base de datos
     * 
     * @return Lista con todas las comunicaciones
     * @throws SQLException Si ocurre un error en la base de datos
     */
    public List<HistorialComunicacion> consultarTodasLasComunicaciones() throws SQLException {
        List<HistorialComunicacion> lista = new ArrayList<>();
        String sql = "SELECT hc.*, c.numero_expediente FROM historial_comunicacion hc LEFT JOIN caso c ON hc.caso_id = c.id ORDER BY hc.fecha DESC";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                HistorialComunicacion com = new HistorialComunicacion();
                com.setId(rs.getInt("id"));
                com.setCasoId(rs.getInt("caso_id"));
                com.setTipo(rs.getString("tipo"));
                com.setFecha(rs.getDate("fecha"));
                com.setDescripcion(rs.getString("descripcion"));
                
                // Obtener el número de expediente
                String numeroExpediente = rs.getString("numero_expediente");
                if (numeroExpediente != null && !numeroExpediente.isEmpty()) {
                    com.setNumeroExpediente(numeroExpediente);
                } else {
                    com.setNumeroExpediente("EXP-" + com.getCasoId());
                }
                
                // Manejar el nuevo campo abogado_id (podría ser nulo en registros antiguos)
                try {
                    com.setAbogadoId(rs.getInt("abogado_id"));
                    
                    // Cargar el nombre del abogado
                    obtenerNombreAbogado(com);
                } catch (SQLException e) {
                    // Si el campo no existe, continuamos
                    System.out.println("Campo abogado_id no encontrado en el registro: " + e.getMessage());
                }
                
                lista.add(com);
            }
        }
        
        return lista;
    }
    
    /**
     * Elimina una comunicación de la base de datos
     * 
     * @param id ID de la comunicación a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     * @throws SQLException Si ocurre un error en la base de datos
     */
    public boolean eliminarComunicacion(int id) throws SQLException {
        String sql = "DELETE FROM historial_comunicacion WHERE id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        }
    }
}
