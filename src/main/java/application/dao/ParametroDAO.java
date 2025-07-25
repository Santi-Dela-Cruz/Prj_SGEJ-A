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
     * @return lista de parámetros
     */
    public List<Parametro> obtenerTodos() {
        List<Parametro> lista = new ArrayList<>();
        String sql = "SELECT * FROM parametros ORDER BY nombre";
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
     * Buscar parámetros por texto en nombre, descripción o valor
     * @param texto texto a buscar
     * @return lista de parámetros filtrados
     */
    public List<Parametro> buscarPorTexto(String texto) {
        List<Parametro> lista = new ArrayList<>();
        String sql = "SELECT * FROM parametros WHERE nombre LIKE ? OR descripcion LIKE ? OR valor LIKE ? ORDER BY nombre";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String like = "%" + texto + "%";
            pstmt.setString(1, like);
            pstmt.setString(2, like);
            pstmt.setString(3, like);
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
     * @param parametro el parámetro a insertar
     * @return true si se insertó correctamente
     */
    public boolean insertarParametro(Parametro parametro) {
        String sql = """
            INSERT INTO parametros (codigo, nombre, descripcion, valor, tipo, estado, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, parametro.getCodigo());
            pstmt.setString(2, parametro.getNombre());
            pstmt.setString(3, parametro.getDescripcion());
            pstmt.setString(4, parametro.getValor());
            pstmt.setString(5, parametro.getTipo().name());
            pstmt.setString(6, parametro.getEstado().name());
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
     * @param parametro el parámetro con datos actualizados
     * @return true si se actualizó correctamente
     */
    public boolean actualizarParametro(Parametro parametro) {
        String sql = """
            UPDATE parametros SET nombre = ?, descripcion = ?, valor = ?, tipo = ?, estado = ?, updated_at = CURRENT_TIMESTAMP
            WHERE codigo = ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, parametro.getNombre());
            pstmt.setString(2, parametro.getDescripcion());
            pstmt.setString(3, parametro.getValor());
            pstmt.setString(4, parametro.getTipo().name());
            pstmt.setString(5, parametro.getEstado().name());
            pstmt.setString(6, parametro.getCodigo());
            int filas = pstmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar parámetro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Eliminar un parámetro por su código
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
        return new Parametro(
            rs.getString("codigo"),
            rs.getString("nombre"),
            rs.getString("descripcion"),
            rs.getString("valor"),
            rs.getString("tipo"),
            rs.getString("estado")
        );
    }
}