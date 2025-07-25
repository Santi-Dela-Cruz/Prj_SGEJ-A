package application.dao;

import application.model.DocumentoCaso;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentoCasoDAO {
    private Connection conn;

    public DocumentoCasoDAO(Connection conn) {
        this.conn = conn;
    }

    public void insertarDocumento(DocumentoCaso doc) throws SQLException {
        String sql = "INSERT INTO documento_caso (caso_id, nombre, ruta, fecha_subida) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, doc.getCasoId());
            stmt.setString(2, doc.getNombre());
            stmt.setString(3, doc.getRuta());
            stmt.setDate(4, new java.sql.Date(doc.getFechaSubida().getTime()));
            stmt.executeUpdate();
        }
    }

    public List<DocumentoCaso> consultarDocumentosPorCaso(int casoId) throws SQLException {
        List<DocumentoCaso> docs = new ArrayList<>();
        String sql = "SELECT * FROM documento_caso WHERE caso_id = ? ORDER BY fecha_subida DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, casoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                DocumentoCaso doc = new DocumentoCaso();
                doc.setId(rs.getInt("id"));
                doc.setCasoId(rs.getInt("caso_id"));
                doc.setNombre(rs.getString("nombre"));
                doc.setRuta(rs.getString("ruta"));
                doc.setFechaSubida(rs.getDate("fecha_subida"));
                docs.add(doc);
            }
        }
        return docs;
    }
}
