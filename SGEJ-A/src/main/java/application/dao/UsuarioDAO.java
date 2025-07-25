package application.dao;

import application.database.DatabaseConnection;
import application.model.Usuario;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la gestión de usuarios
 */
public class UsuarioDAO {

    /**
     * Insertar un nuevo usuario en la base de datos
     * @param usuario el usuario a insertar
     * @return true si se insertó correctamente, false en caso contrario
     */
    public boolean insertarUsuario(Usuario usuario) {
        String sql = """
            INSERT INTO usuarios (nombres_completos, nombre_usuario, numero_identificacion, tipo_identificacion,
                                  telefono, correo, estado, direccion, fecha_ingreso, tipo_usuario, rol, clave, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, usuario.getNombresCompletos());
            pstmt.setString(2, usuario.getNombreUsuario());
            pstmt.setString(3, usuario.getNumeroIdentificacion());
            pstmt.setString(4, usuario.getTipoIdentificacion().name());
            pstmt.setString(5, usuario.getTelefono());
            pstmt.setString(6, usuario.getCorreo());
            pstmt.setString(7, usuario.getEstado().name());
            pstmt.setString(8, usuario.getDireccion());
            pstmt.setString(9, usuario.getFechaIngreso().toString());
            pstmt.setString(10, usuario.getTipoUsuario().name());
            pstmt.setString(11, usuario.getRol());
            pstmt.setString(12, usuario.getClave());
            pstmt.setString(13, LocalDateTime.now().toString());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Actualizar un usuario existente
     * @param usuario el usuario con los datos actualizados
     * @return true si se actualizó correctamente, false en caso contrario
     */
    public boolean actualizarUsuario(Usuario usuario) {
        String sql = """
            UPDATE usuarios SET 
                telefono = ?, correo = ?, estado = ?, direccion = ?, fecha_ingreso = ?, tipo_usuario = ?, rol = ?, clave = ?, updated_at = ?
            WHERE id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getTelefono());
            pstmt.setString(2, usuario.getCorreo());
            pstmt.setString(3, usuario.getEstado().name());
            pstmt.setString(4, usuario.getDireccion());
            pstmt.setString(5, usuario.getFechaIngreso().toString());
            pstmt.setString(6, usuario.getTipoUsuario().name());
            pstmt.setString(7, usuario.getRol());
            pstmt.setString(8, usuario.getClave());
            pstmt.setString(9, LocalDateTime.now().toString());
            pstmt.setInt(10, usuario.getId());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtener un usuario por su ID
     * @param id el ID del usuario
     * @return el usuario encontrado o null si no existe
     */
    public Usuario obtenerUsuarioPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearResultSetAUsuario(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Obtener un usuario por su nombre de usuario
     * @param nombreUsuario el nombre de usuario
     * @return el usuario encontrado o null si no existe
     */
    public Usuario obtenerUsuarioPorNombre(String nombreUsuario) {
        String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreUsuario);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearResultSetAUsuario(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por nombre: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Obtener todos los usuarios
     * @return lista de todos los usuarios
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombres_completos";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(mapearResultSetAUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener todos los usuarios: " + e.getMessage());
            e.printStackTrace();
        }

        return usuarios;
    }

    /**
     * Buscar usuarios por nombre, usuario o número de identificación
     * @param texto el texto a buscar
     * @return lista de usuarios que coinciden
     */
    public List<Usuario> buscarUsuarios(String texto) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = """
            SELECT * FROM usuarios
            WHERE nombres_completos LIKE ? OR nombre_usuario LIKE ? OR numero_identificacion LIKE ?
            ORDER BY nombres_completos
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String like = "%" + texto + "%";
            pstmt.setString(1, like);
            pstmt.setString(2, like);
            pstmt.setString(3, like);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                usuarios.add(mapearResultSetAUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar usuarios: " + e.getMessage());
            e.printStackTrace();
        }

        return usuarios;
    }

    /**
     * Obtener usuarios por estado
     * @param estado el estado a filtrar
     * @return lista de usuarios con el estado especificado
     */
    public List<Usuario> obtenerUsuariosPorEstado(Usuario.Estado estado) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE estado = ? ORDER BY nombres_completos";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, estado.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                usuarios.add(mapearResultSetAUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios por estado: " + e.getMessage());
            e.printStackTrace();
        }

        return usuarios;
    }

    /**
     * Verificar si existe un usuario con el número de identificación dado
     * @param numeroIdentificacion el número de identificación a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existeUsuario(String numeroIdentificacion) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE numero_identificacion = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, numeroIdentificacion);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de usuario: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Mapear un ResultSet a un objeto Usuario
     * @param rs el ResultSet de la consulta
     * @return el objeto Usuario mapeado
     * @throws SQLException si hay error en el mapeo
     */
    private Usuario mapearResultSetAUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();

        usuario.setId(rs.getInt("id"));
        usuario.setNombresCompletos(rs.getString("nombres_completos"));
        usuario.setNombreUsuario(rs.getString("nombre_usuario"));
        usuario.setNumeroIdentificacion(rs.getString("numero_identificacion"));
        usuario.setTipoIdentificacion(Usuario.TipoIdentificacion.valueOf(rs.getString("tipo_identificacion")));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setCorreo(rs.getString("correo"));
        usuario.setEstado(Usuario.Estado.valueOf(rs.getString("estado")));
        usuario.setDireccion(rs.getString("direccion"));
        usuario.setFechaIngreso(LocalDate.parse(rs.getString("fecha_ingreso")));
        usuario.setTipoUsuario(Usuario.TipoUsuario.valueOf(rs.getString("tipo_usuario")));
        usuario.setRol(rs.getString("rol"));
        usuario.setClave(rs.getString("clave"));

        String createdAtStr = rs.getString("created_at");
        if (createdAtStr != null && !createdAtStr.isEmpty()) {
            try {
                usuario.setCreatedAt(LocalDateTime.parse(createdAtStr));
            } catch (Exception e) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                usuario.setCreatedAt(LocalDateTime.parse(createdAtStr, formatter));
            }
        }

        String updatedAtStr = rs.getString("updated_at");
        if (updatedAtStr != null && !updatedAtStr.isEmpty()) {
            try {
                usuario.setUpdatedAt(LocalDateTime.parse(updatedAtStr));
            } catch (Exception e) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                usuario.setUpdatedAt(LocalDateTime.parse(updatedAtStr, formatter));
            }
        }

        return usuario;
    }
}