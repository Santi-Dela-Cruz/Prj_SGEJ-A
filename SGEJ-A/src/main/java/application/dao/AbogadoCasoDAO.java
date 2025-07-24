package application.dao;

import application.model.AbogadoCaso;
import java.sql.*;

public class AbogadoCasoDAO {
    private Connection conn;

    public AbogadoCasoDAO(Connection conn) {
        this.conn = conn;
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

    // Otros métodos según necesidades
}
