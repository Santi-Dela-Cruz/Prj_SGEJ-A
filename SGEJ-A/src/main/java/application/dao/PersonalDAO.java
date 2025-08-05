package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import application.database.DatabaseConnection;
import application.model.Personal;

/**
 * Clase DAO para operaciones CRUD en la tabla personal
 */
public class PersonalDAO {

    /**
     * Registra un nuevo empleado en la base de datos.
     * 
     * @param personal El objeto Personal con los datos a guardar
     * @return El ID del personal registrado, o -1 si hubo un error
     */
    public int registrarPersonal(Personal personal) {
        String sql = "INSERT INTO personal (nombres, apellidos, numero_identificacion, tipo_identificacion, " +
                    "telefono, correo, direccion, fecha_ingreso, rol, estado) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, personal.getNombres());
            stmt.setString(2, personal.getApellidos());
            stmt.setString(3, personal.getNumeroIdentificacion());
            stmt.setString(4, personal.getTipoIdentificacion());
            stmt.setString(5, personal.getTelefono());
            stmt.setString(6, personal.getCorreo());
            stmt.setString(7, personal.getDireccion());
            stmt.setString(8, personal.getFechaIngreso() != null ? personal.getFechaIngreso().toString() : null);
            stmt.setString(9, personal.getRol());
            stmt.setString(10, personal.getEstado());
            
            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al registrar personal: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    /**
     * Actualiza los datos de un empleado existente.
     * 
     * @param personal El objeto Personal con los datos actualizados
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public boolean actualizarPersonal(Personal personal) {
        String sql = "UPDATE personal SET nombres = ?, apellidos = ?, numero_identificacion = ?, " +
                    "tipo_identificacion = ?, telefono = ?, correo = ?, direccion = ?, " +
                    "fecha_ingreso = ?, rol = ?, estado = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, personal.getNombres());
            stmt.setString(2, personal.getApellidos());
            stmt.setString(3, personal.getNumeroIdentificacion());
            stmt.setString(4, personal.getTipoIdentificacion());
            stmt.setString(5, personal.getTelefono());
            stmt.setString(6, personal.getCorreo());
            stmt.setString(7, personal.getDireccion());
            stmt.setString(8, personal.getFechaIngreso() != null ? personal.getFechaIngreso().toString() : null);
            stmt.setString(9, personal.getRol());
            stmt.setString(10, personal.getEstado());
            stmt.setInt(11, personal.getId());
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar personal: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Elimina un empleado de la base de datos.
     * 
     * @param id El ID del empleado a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    public boolean eliminarPersonal(int id) {
        String sql = "DELETE FROM personal WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar personal: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Obtiene un empleado por su ID.
     * 
     * @param id El ID del empleado a buscar
     * @return El objeto Personal encontrado, o null si no existe
     */
    public Personal obtenerPersonalPorId(int id) {
        String sql = "SELECT * FROM personal WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extraerPersonalDeResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener personal por ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Obtiene un empleado por su número de identificación.
     * 
     * @param numeroIdentificacion El número de identificación a buscar
     * @return El objeto Personal encontrado, o null si no existe
     */
    public Personal obtenerPersonalPorIdentificacion(String numeroIdentificacion) {
        String sql = "SELECT * FROM personal WHERE numero_identificacion = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, numeroIdentificacion);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extraerPersonalDeResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener personal por identificación: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Obtiene todos los empleados registrados.
     * 
     * @return Lista de objetos Personal
     */
    public List<Personal> obtenerTodosLosEmpleados() {
        String sql = "SELECT * FROM personal ORDER BY id DESC";
        List<Personal> empleados = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                empleados.add(extraerPersonalDeResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los empleados: " + e.getMessage());
            e.printStackTrace();
        }
        
        return empleados;
    }
    
    /**
     * Obtiene todos los empleados que tienen un rol específico.
     * 
     * @param rol El rol por el cual filtrar
     * @return Lista de objetos Personal con el rol especificado
     */
    public List<Personal> obtenerPersonalPorRol(String rol) {
        String sql = "SELECT * FROM personal WHERE LOWER(rol) = LOWER(?) OR LOWER(rol) LIKE LOWER(?) ORDER BY id DESC";
        List<Personal> empleados = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, rol);
            stmt.setString(2, "%" + rol + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                empleados.add(extraerPersonalDeResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener personal por rol: " + e.getMessage());
            e.printStackTrace();
        }
        
        return empleados;
    }
    
    /**
     * Método auxiliar para extraer un objeto Personal de un ResultSet.
     */
    private Personal extraerPersonalDeResultSet(ResultSet rs) throws SQLException {
        Personal personal = new Personal();
        
        personal.setId(rs.getInt("id"));
        personal.setNombres(rs.getString("nombres"));
        personal.setApellidos(rs.getString("apellidos"));
        personal.setNumeroIdentificacion(rs.getString("numero_identificacion"));
        personal.setTipoIdentificacion(rs.getString("tipo_identificacion"));
        personal.setTelefono(rs.getString("telefono"));
        personal.setCorreo(rs.getString("correo"));
        personal.setDireccion(rs.getString("direccion"));
        
        // Convertir fecha de string a LocalDate
        String fechaStr = rs.getString("fecha_ingreso");
        if (fechaStr != null && !fechaStr.isEmpty()) {
            try {
                personal.setFechaIngreso(LocalDate.parse(fechaStr));
            } catch (Exception e) {
                System.err.println("Error al parsear fecha: " + fechaStr);
            }
        }
        
        personal.setRol(rs.getString("rol"));
        personal.setEstado(rs.getString("estado"));
        
        return personal;
    }
}
