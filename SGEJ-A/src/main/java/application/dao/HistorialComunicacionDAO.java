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
        String sql = "INSERT INTO historial_comunicacion (caso_id, tipo, fecha, descripcion, abogado_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, com.getCasoId());
            stmt.setString(2, com.getTipo());
            stmt.setDate(3, new java.sql.Date(com.getFecha().getTime()));
            stmt.setString(4, com.getDescripcion());
            stmt.setInt(5, com.getAbogadoId());
            stmt.executeUpdate();
        }
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
}
