package application.dao;

import application.database.DatabaseConnection;
import application.model.Cliente;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la gestión de clientes
 */
public class ClienteDAO {
    
    /**
     * Insertar un nuevo cliente en la base de datos
     * @param cliente el cliente a insertar
     * @return true si se insertó correctamente, false en caso contrario
     */
    public boolean insertarCliente(Cliente cliente) {
        String sql = """
            INSERT INTO clientes (nombre_completo, tipo_identificacion, tipo_persona, 
                                numero_identificacion, direccion, telefono, correo_electronico, 
                                estado, fecha_registro, estado_civil, representante_legal, 
                                direccion_fiscal, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, cliente.getNombreCompleto());
            pstmt.setString(2, cliente.getTipoIdentificacion().name());
            pstmt.setString(3, cliente.getTipoPersona().name());
            pstmt.setString(4, cliente.getNumeroIdentificacion());
            pstmt.setString(5, cliente.getDireccion());
            pstmt.setString(6, cliente.getTelefono());
            pstmt.setString(7, cliente.getCorreoElectronico());
            pstmt.setString(8, cliente.getEstado().name());
            pstmt.setString(9, cliente.getFechaRegistro().toString());
            pstmt.setString(10, cliente.getEstadoCivil());
            pstmt.setString(11, cliente.getRepresentanteLegal());
            pstmt.setString(12, cliente.getDireccionFiscal());
            pstmt.setString(13, LocalDateTime.now().toString());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Obtener el ID generado
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        cliente.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al insertar cliente: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Actualizar un cliente existente
     * @param cliente el cliente con los datos actualizados
     * @return true si se actualizó correctamente, false en caso contrario
     */
    public boolean actualizarCliente(Cliente cliente) {
        String sql = """
            UPDATE clientes SET 
                direccion = ?, telefono = ?, correo_electronico = ?, estado = ?, 
                estado_civil = ?, representante_legal = ?, direccion_fiscal = ?, 
                updated_at = ?
            WHERE id = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cliente.getDireccion());
            pstmt.setString(2, cliente.getTelefono());
            pstmt.setString(3, cliente.getCorreoElectronico());
            pstmt.setString(4, cliente.getEstado().name());
            pstmt.setString(5, cliente.getEstadoCivil());
            pstmt.setString(6, cliente.getRepresentanteLegal());
            pstmt.setString(7, cliente.getDireccionFiscal());
            pstmt.setString(8, LocalDateTime.now().toString());
            pstmt.setInt(9, cliente.getId());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtener un cliente por su ID
     * @param id el ID del cliente
     * @return el cliente encontrado o null si no existe
     */
    public Cliente obtenerClientePorId(int id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapearResultSetACliente(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener cliente por ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Obtener un cliente por su número de identificación
     * @param numeroIdentificacion el número de identificación
     * @return el cliente encontrado o null si no existe
     */
    public Cliente obtenerClientePorIdentificacion(String numeroIdentificacion) {
        String sql = "SELECT * FROM clientes WHERE numero_identificacion = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, numeroIdentificacion);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapearResultSetACliente(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener cliente por identificación: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Obtener todos los clientes
     * @return lista de todos los clientes
     */
    public List<Cliente> obtenerTodosLosClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY nombre_completo";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                clientes.add(mapearResultSetACliente(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los clientes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return clientes;
    }
    
    /**
     * Buscar clientes por nombre
     * @param nombre el nombre a buscar
     * @return lista de clientes que coinciden con el nombre
     */
    public List<Cliente> buscarClientesPorNombre(String nombre) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE nombre_completo LIKE ? ORDER BY nombre_completo";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + nombre + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                clientes.add(mapearResultSetACliente(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar clientes por nombre: " + e.getMessage());
            e.printStackTrace();
        }
        
        return clientes;
    }
    
    /**
     * Obtener clientes por estado
     * @param estado el estado a filtrar
     * @return lista de clientes con el estado especificado
     */
    public List<Cliente> obtenerClientesPorEstado(Cliente.Estado estado) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE estado = ? ORDER BY nombre_completo";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, estado.name());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                clientes.add(mapearResultSetACliente(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener clientes por estado: " + e.getMessage());
            e.printStackTrace();
        }
        
        return clientes;
    }
    
    /**
     * Verificar si existe un cliente con el número de identificación dado
     * @param numeroIdentificacion el número de identificación a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existeCliente(String numeroIdentificacion) {
        String sql = "SELECT COUNT(*) FROM clientes WHERE numero_identificacion = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, numeroIdentificacion);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de cliente: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Mapear un ResultSet a un objeto Cliente
     * @param rs el ResultSet de la consulta
     * @return el objeto Cliente mapeado
     * @throws SQLException si hay error en el mapeo
     */
    private Cliente mapearResultSetACliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        
        cliente.setId(rs.getInt("id"));
        cliente.setNombreCompleto(rs.getString("nombre_completo"));
        cliente.setTipoIdentificacion(Cliente.TipoIdentificacion.valueOf(rs.getString("tipo_identificacion")));
        cliente.setTipoPersona(Cliente.TipoPersona.valueOf(rs.getString("tipo_persona")));
        cliente.setNumeroIdentificacion(rs.getString("numero_identificacion"));
        cliente.setDireccion(rs.getString("direccion"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setCorreoElectronico(rs.getString("correo_electronico"));
        cliente.setEstado(Cliente.Estado.valueOf(rs.getString("estado")));
        cliente.setFechaRegistro(LocalDate.parse(rs.getString("fecha_registro")));
        cliente.setEstadoCivil(rs.getString("estado_civil"));
        cliente.setRepresentanteLegal(rs.getString("representante_legal"));
        cliente.setDireccionFiscal(rs.getString("direccion_fiscal"));
        
        String createdAtStr = rs.getString("created_at");
        if (createdAtStr != null && !createdAtStr.isEmpty()) {
            // SQLite puede guardar fechas en formato ISO con microsegundos
            try {
                // Intentar parsear como ISO formato completo
                cliente.setCreatedAt(LocalDateTime.parse(createdAtStr));
            } catch (Exception e) {
                // Si falla, intentar formato simple
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                cliente.setCreatedAt(LocalDateTime.parse(createdAtStr, formatter));
            }
        }
        
        String updatedAtStr = rs.getString("updated_at");
        if (updatedAtStr != null && !updatedAtStr.isEmpty()) {
            // SQLite puede guardar fechas en formato ISO con microsegundos
            try {
                // Intentar parsear como ISO formato completo
                cliente.setUpdatedAt(LocalDateTime.parse(updatedAtStr));
            } catch (Exception e) {
                // Si falla, intentar formato simple
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                cliente.setUpdatedAt(LocalDateTime.parse(updatedAtStr, formatter));
            }
        }
        
        return cliente;
    }
}
