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
        String sql = "INSERT INTO historial_comunicacion (caso_id, tipo, fecha, descripcion) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, com.getCasoId());
            stmt.setString(2, com.getTipo());
            stmt.setDate(3, new java.sql.Date(com.getFecha().getTime()));
            stmt.setString(4, com.getDescripcion());
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
                lista.add(com);
            }
        }
        return lista;
    }
}
