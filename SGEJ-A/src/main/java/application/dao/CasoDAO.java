package application.dao;

import application.model.Caso;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CasoDAO {
    private Connection conn;

    public CasoDAO(Connection conn) {
        this.conn = conn;
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

    // Otros métodos según necesidades
}
