package application.dao;

import application.database.DatabaseConnection;
import application.model.AbogadoCaso;
import application.model.Personal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbogadoCasoDAO {
    private Connection conn;

    public AbogadoCasoDAO(Connection conn) {
        this.conn = conn;
    }
    
    public AbogadoCasoDAO() {
        // Constructor sin parámetros para obtener la conexión al momento de usarla
    }

    public void insertarAbogadoCaso(AbogadoCaso abogadoCaso) throws SQLException {
        String sql = "INSERT INTO abogado_caso (caso_id, abogado_id, rol, fecha_asignacion) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, abogadoCaso.getCasoId());
            stmt.setInt(2, abogadoCaso.getAbogadoId());
            stmt.setString(3, abogadoCaso.getRol());
            stmt.setDate(4, new java.sql.Date(abogadoCaso.getFechaAsignacion().getTime()));
            stmt.executeUpdate();
        }
    }
    
    /**
     * Verifica si un abogado está asignado a un caso específico
     * Verifica tanto en la tabla abogado_caso como en la tabla caso (como abogado principal)
     *
     * @param casoId ID del caso
     * @param abogadoId ID del abogado
     * @return true si el abogado está asignado al caso, false en caso contrario
     */
    public boolean verificarAbogadoAsignadoACaso(int casoId, int abogadoId) {
        // Primero verificamos si está asignado en la tabla abogado_caso
        String sql1 = "SELECT COUNT(*) FROM abogado_caso WHERE caso_id = ? AND abogado_id = ?";
        // También verificamos si está asignado como abogado principal en la tabla caso
        String sql2 = "SELECT COUNT(*) FROM caso WHERE id = ? AND abogado_id = ?";
        
        Connection conn = null;
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        boolean asignado = false;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            // Verificar en la tabla abogado_caso
            stmt1 = conn.prepareStatement(sql1);
            stmt1.setInt(1, casoId);
            stmt1.setInt(2, abogadoId);
            rs1 = stmt1.executeQuery();
            
            if (rs1.next() && rs1.getInt(1) > 0) {
                asignado = true;
            } else {
                // Si no está en abogado_caso, verificar en la tabla caso
                stmt2 = conn.prepareStatement(sql2);
                stmt2.setInt(1, casoId);
                stmt2.setInt(2, abogadoId);
                rs2 = stmt2.executeQuery();
                
                if (rs2.next()) {
                    asignado = rs2.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs1 != null) rs1.close();
                if (rs2 != null) rs2.close();
                if (stmt1 != null) stmt1.close();
                if (stmt2 != null) stmt2.close();
                if (conn != null) conn.close(); // Importante: cerrar la conexión
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return asignado;
    }
    
    /**
     * Obtiene todos los abogados asignados a un caso específico
     *
     * @param casoId ID del caso
     * @return Lista de objetos Personal (abogados)
     */
    public List<Personal> obtenerAbogadosPorCaso(int casoId) {
        List<Personal> abogados = new ArrayList<>();
        String sql = "SELECT p.* FROM personal p " +
                     "JOIN abogado_caso ac ON p.id = ac.abogado_id " +
                     "WHERE ac.caso_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, casoId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Personal abogado = new Personal();
                abogado.setId(rs.getInt("id"));
                abogado.setNombres(rs.getString("nombres"));
                abogado.setApellidos(rs.getString("apellidos"));
                abogado.setCorreo(rs.getString("correo"));
                abogado.setRol(rs.getString("rol"));
                abogados.add(abogado);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return abogados;
    }
    
    /**
     * Asigna un abogado como principal a un caso en la tabla caso
     * 
     * @param casoId ID del caso
     * @param abogadoId ID del abogado
     * @return true si la asignación fue exitosa, false en caso contrario
     */
    public boolean asignarAbogadoPrincipal(int casoId, int abogadoId) {
        // Primero aseguramos que exista la columna abogado_id en la tabla caso
        try {
            application.database.MigracionAbogadoCaso.ejecutarMigracion();
        } catch (Exception e) {
            System.err.println("Error al ejecutar migración: " + e.getMessage());
            // Continuamos aún si hay error, para intentar la asignación
        }
        
        String sql = "UPDATE caso SET abogado_id = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, abogadoId);
            stmt.setInt(2, casoId);
            int filasAfectadas = stmt.executeUpdate();
            
            // También aseguramos que el abogado esté en la tabla de relación
            if (filasAfectadas > 0 && !verificarAbogadoAsignadoACaso(casoId, abogadoId)) {
                asignarAbogadoACaso(casoId, abogadoId, "Principal");
            }
            
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Asigna un abogado a un caso en la tabla de relación abogado_caso
     * 
     * @param casoId ID del caso
     * @param abogadoId ID del abogado
     * @param rol Rol del abogado en el caso
     * @return true si la asignación fue exitosa, false en caso contrario
     */
    public boolean asignarAbogadoACaso(int casoId, int abogadoId, String rol) {
        // Primero aseguramos que exista la tabla abogado_caso
        try {
            application.database.MigracionAbogadoCaso.ejecutarMigracion();
        } catch (Exception e) {
            System.err.println("Error al ejecutar migración: " + e.getMessage());
            // Continuamos aún si hay error, para intentar la asignación
        }
        
        String sql = "INSERT OR IGNORE INTO abogado_caso (caso_id, abogado_id, rol, fecha_asignacion) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, casoId);
            stmt.setInt(2, abogadoId);
            stmt.setString(3, rol);
            stmt.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            int filasAfectadas = stmt.executeUpdate();
            
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
