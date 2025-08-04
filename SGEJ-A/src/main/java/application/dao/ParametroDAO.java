package application.dao;

import application.database.DatabaseConnection;
import application.model.Parametro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la gestión de parámetros del sistema
 */
public class ParametroDAO {

    /**
     * Obtener todos los parámetros
     * 
     * @return lista de parámetros
     */
    public List<Parametro> obtenerTodos() {
        List<Parametro> lista = new ArrayList<>();
        String sql = "SELECT * FROM parametro ORDER BY nombre";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearResultSetAParametro(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener parámetros: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Obtener parámetros por categoría
     * 
     * @param categoria categoría a filtrar
     * @return lista de parámetros filtrados por categoría
     */
    public List<Parametro> obtenerPorCategoria(String categoria) {
        List<Parametro> lista = new ArrayList<>();
        String sql = "SELECT * FROM parametro WHERE categoria = ? AND estado = 'ACTIVO' ORDER BY nombre";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categoria);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(mapearResultSetAParametro(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener parámetros por categoría: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Obtener solo parámetros visibles
     * 
     * @return lista de parámetros activos/visibles
     */
    public List<Parametro> obtenerParametrosVisibles() {
        List<Parametro> lista = new ArrayList<>();
        String sql = "SELECT * FROM parametro WHERE estado = 'ACTIVO' ORDER BY categoria, nombre";
        // Usamos directamente el campo estado
        try {
            Connection conn = DatabaseConnection.getConnection();

            // Ejecutar la consulta apropiada
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    lista.add(mapearResultSetAParametro(rs));
                }
            }

            conn.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener parámetros visibles: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Obtener parámetros no visibles que pueden ser activados
     * 
     * @return lista de parámetros inactivos
     */
    public List<Parametro> obtenerParametrosInactivos() {
        return obtenerParametrosInactivosPorCategoria(null);
    }

    /**
     * Obtener parámetros no visibles que pueden ser activados por categoría
     * 
     * @param categoria categoría para filtrar, si es null devuelve todos
     * @return lista de parámetros inactivos
     */
    public List<Parametro> obtenerParametrosInactivosPorCategoria(String categoria) {
        List<Parametro> lista = new ArrayList<>();
        String sql = "SELECT * FROM parametro WHERE estado = 'INACTIVO'";

        // Añadir filtro de categoría si se especifica
        if (categoria != null) {
            sql += " AND categoria = ?";
        }

        sql += " ORDER BY nombre";

        try {
            Connection conn = DatabaseConnection.getConnection();

            // Ejecutar la consulta apropiada
            if (categoria != null) {
                // Si hay categoría, usar PreparedStatement
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, categoria);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            lista.add(mapearResultSetAParametro(rs));
                        }
                    }
                }
            } else {
                // Si no hay categoría, usar Statement normal
                try (Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        lista.add(mapearResultSetAParametro(rs));
                    }
                }
            }

            conn.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener parámetros inactivos: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Restablecer un parámetro a su valor por defecto
     * 
     * @param codigo código del parámetro a restablecer
     * @return true si se realizó correctamente
     */
    public boolean restablecerValorDefecto(String codigo) {
        String sql = "UPDATE parametro SET valor = valor_defecto, updated_at = CURRENT_TIMESTAMP WHERE codigo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codigo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al restablecer valor por defecto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Activa un parámetro inactivo
     * 
     * @param codigo código del parámetro a activar
     * @return true si se activó correctamente
     */
    public boolean activarParametro(String codigo) {
        // Solo actualizamos el estado, manteniendo la categoría y otras propiedades
        String sql = "UPDATE parametro SET estado = 'ACTIVO', updated_at = CURRENT_TIMESTAMP WHERE codigo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codigo);
            int filasAfectadas = pstmt.executeUpdate();
            System.out.println("DEPURACIÓN: Activando parámetro " + codigo + " - Filas afectadas: " + filasAfectadas);
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al activar parámetro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Desactiva un parámetro activo
     * 
     * @param codigo código del parámetro a desactivar
     * @return true si se desactivó correctamente
     */
    public boolean desactivarParametro(String codigo) {
        String sql = "UPDATE parametro SET estado = 'INACTIVO', updated_at = CURRENT_TIMESTAMP WHERE codigo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codigo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al desactivar parámetro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Buscar parámetros por texto en nombre, descripción o valor
     * 
     * @param texto texto a buscar
     * @return lista de parámetros filtrados
     */
    public List<Parametro> buscarPorTexto(String texto) {
        List<Parametro> lista = new ArrayList<>();
        String sql = "SELECT * FROM parametro WHERE estado = 'ACTIVO' AND (nombre LIKE ? OR descripcion LIKE ? OR valor LIKE ? OR codigo LIKE ?) ORDER BY categoria, nombre";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String like = "%" + texto + "%";
            pstmt.setString(1, like);
            pstmt.setString(2, like);
            pstmt.setString(3, like);
            pstmt.setString(4, like);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(mapearResultSetAParametro(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar parámetros: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Insertar un nuevo parámetro
     * 
     * @param parametro el parámetro a insertar
     * @return true si se insertó correctamente
     */
    public boolean insertarParametro(Parametro parametro) {
        String sql = """
                INSERT INTO parametro (codigo, nombre, descripcion, valor, valor_defecto, tipo, categoria,
                                predefinido, visible, estado, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, parametro.getCodigo());
            pstmt.setString(2, parametro.getNombre());
            pstmt.setString(3, parametro.getDescripcion());
            pstmt.setString(4, parametro.getValor());
            pstmt.setString(5,
                    parametro.getValorDefecto() != null ? parametro.getValorDefecto() : parametro.getValor());
            pstmt.setString(6, parametro.getTipo().name());
            pstmt.setString(7, parametro.getCategoria() != null ? parametro.getCategoria() : "General");
            pstmt.setBoolean(8, parametro.isPredefinido());
            // Asignamos visible basado en estado para mantener consistencia
            pstmt.setBoolean(9, parametro.getEstado() == Parametro.Estado.ACTIVO);
            pstmt.setString(10, parametro.getEstado().name());
            int filas = pstmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar parámetro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualizar un parámetro existente
     * 
     * @param parametro el parámetro con datos actualizados
     * @return true si se actualizó correctamente
     */
    public boolean actualizarParametro(Parametro parametro) {
        String sql = """
                UPDATE parametro SET nombre = ?, descripcion = ?, valor = ?, tipo = ?,
                           categoria = ?, estado = ?, updated_at = CURRENT_TIMESTAMP
                WHERE codigo = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, parametro.getNombre());
            pstmt.setString(2, parametro.getDescripcion());
            pstmt.setString(3, parametro.getValor());
            pstmt.setString(4, parametro.getTipo().name());
            pstmt.setString(5, parametro.getCategoria() != null ? parametro.getCategoria() : "General");
            pstmt.setString(6, parametro.getEstado().name());
            pstmt.setString(7, parametro.getCodigo());
            int filas = pstmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar parámetro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualizar solo el valor de un parámetro
     * 
     * @param codigo     código del parámetro a actualizar
     * @param nuevoValor nuevo valor del parámetro
     * @return true si se actualizó correctamente
     */
    public boolean actualizarValor(String codigo, String nuevoValor) {
        String sql = "UPDATE parametro SET valor = ?, updated_at = CURRENT_TIMESTAMP WHERE codigo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevoValor);
            pstmt.setString(2, codigo);
            int filas = pstmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar valor del parámetro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Eliminar un parámetro por su código
     * 
     * @param codigo código del parámetro
     * @return true si se eliminó correctamente
     */
    public boolean eliminarParametro(String codigo) {
        String sql = "DELETE FROM parametros WHERE codigo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codigo);
            int filas = pstmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar parámetro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mapear un ResultSet a un objeto Parametro
     */
    private Parametro mapearResultSetAParametro(ResultSet rs) throws SQLException {
        // Usar el constructor extendido para asegurar que todos los campos se asignen
        // correctamente
        Parametro parametro = new Parametro();
        parametro.setCodigo(rs.getString("codigo"));
        parametro.setNombre(rs.getString("nombre"));
        parametro.setDescripcion(rs.getString("descripcion"));
        parametro.setValor(rs.getString("valor"));

        // Asignar estado correctamente
        String estadoStr = rs.getString("estado");
        parametro.setEstado(Parametro.Estado.valueOf(estadoStr));

        // Asignar tipo
        String tipoStr = rs.getString("tipo");
        parametro.setTipo(Parametro.Tipo.valueOf(tipoStr));

        try {
            // Intentar obtener los nuevos campos (pueden no existir en versiones antiguas
            // de la BD)
            parametro.setValorDefecto(rs.getString("valor_defecto"));
            parametro.setCategoria(rs.getString("categoria"));
            parametro.setPredefinido(rs.getBoolean("predefinido"));

            // Establecer visible basado en estado para mantener consistencia
            parametro.setVisible(parametro.getEstado() == Parametro.Estado.ACTIVO);

        } catch (SQLException ex) {
            // Ignorar excepciones si los campos no existen
            System.out.println("Algunos campos extendidos no están disponibles: " + ex.getMessage());
        }

        return parametro;
    }
}