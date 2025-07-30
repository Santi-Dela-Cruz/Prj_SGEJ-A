package application.service;

import application.dao.UsuarioDAO;
import application.model.Usuario;

/**
 * Servicio para gestionar la autenticación de usuarios
 */
public class AutenticacionService {
    private static AutenticacionService instancia;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Usuario actualmente autenticado
    private Usuario usuarioActual = null;

    private AutenticacionService() {
    }

    public static AutenticacionService getInstancia() {
        if (instancia == null) {
            instancia = new AutenticacionService();
        }
        return instancia;
    }

    /**
     * Autentica un usuario por su nombre de usuario y contraseña
     * 
     * @param nombreUsuario El nombre de usuario
     * @param clave         La contraseña
     * @return true si la autenticación fue exitosa, false si no
     */
    public boolean autenticar(String nombreUsuario, String clave) {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty() || clave == null || clave.trim().isEmpty()) {
            return false;
        }

        try {
            Usuario usuario = usuarioDAO.obtenerUsuarioPorNombreUsuario(nombreUsuario);

            if (usuario != null && usuario.getClave().equals(clave) &&
                    usuario.getEstadoUsuario() == Usuario.EstadoUsuario.ACTIVO) {
                usuarioActual = usuario;
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error en autenticación: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Cierra la sesión del usuario actual
     */
    public void cerrarSesion() {
        usuarioActual = null;
    }

    /**
     * Obtiene el usuario actualmente autenticado
     * 
     * @return El usuario autenticado o null si no hay usuario
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Verifica si hay un usuario autenticado
     * 
     * @return true si hay un usuario autenticado, false si no
     */
    public boolean hayUsuarioAutenticado() {
        return usuarioActual != null;
    }

    /**
     * Obtiene el rol del usuario actual basado en su tipo
     * 
     * @return El rol del usuario actual
     */
    public String getRolUsuarioActual() {
        if (usuarioActual == null) {
            return "Invitado";
        }

        return switch (usuarioActual.getTipoUsuario()) {
            case INTERNO -> "Administrador";
            case EXTERNO -> "Asistente Legal";
            case JURIDICA -> "Contador";
            case NATURAL -> "Abogado";
            default -> "Usuario";
        };
    }

    /**
     * Verifica si el usuario actual tiene el rol especificado
     * 
     * @param rol El rol a verificar
     * @return true si el usuario tiene el rol, false si no
     */
    public boolean tieneRol(String rol) {
        return usuarioActual != null && getRolUsuarioActual().equals(rol);
    }
}
