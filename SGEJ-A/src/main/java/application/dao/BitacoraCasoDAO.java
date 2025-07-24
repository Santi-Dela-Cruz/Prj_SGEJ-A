package application.dao;

import application.model.BitacoraCaso;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BitacoraCasoDAO {
    private Connection conn;

    public BitacoraCasoDAO(Connection conn) {
        this.conn = conn;
    }

    public void insertarBitacora(BitacoraCaso bitacora) throws SQLException {
        String sql = "INSERT INTO bitacora_caso (caso_id, fecha_entrada, usuario, tipo_accion, descripcion) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bitacora.getCasoId());
            stmt.setDate(2, new java.sql.Date(bitacora.getFechaEntrada().getTime()));
            stmt.setString(3, bitacora.getUsuario());
            stmt.setString(4, bitacora.getTipoAccion());
            stmt.setString(5, bitacora.getDescripcion());
            stmt.executeUpdate();
        }
    }

    public List<BitacoraCaso> consultarBitacorasPorCaso(int casoId) throws SQLException {
        List<BitacoraCaso> bitacoras = new ArrayList<>();
        String sql = "SELECT * FROM bitacora_caso WHERE caso_id = ? ORDER BY fecha_entrada DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, casoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                BitacoraCaso b = new BitacoraCaso();
                b.setId(rs.getInt("id"));
                b.setCasoId(rs.getInt("caso_id"));
                b.setFechaEntrada(rs.getDate("fecha_entrada"));
                b.setUsuario(rs.getString("usuario"));
                b.setTipoAccion(rs.getString("tipo_accion"));
                b.setDescripcion(rs.getString("descripcion"));
                bitacoras.add(b);
            }
        }
        return bitacoras;
    }

    // Otros métodos según necesidades
}
