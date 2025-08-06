package application.utils;

import application.dao.UsuarioDAO;
import application.model.Usuario;
import java.sql.SQLException;

/**
 * Utilidad para activar el usuario administrador en la base de datos
 * Esta clase se puede ejecutar directamente para activar el usuario
 * administrador
 * que haya sido desactivado por error.
 */
public class ActivarUsuarioAdmin {

    public static void main(String[] args) {
        // Nombre de usuario administrador que queremos activar
        final String nombreUsuarioAdmin = "admin";

        System.out.println("===== ACTIVACIÓN DE USUARIO ADMINISTRADOR =====");
        System.out.println("Intentando activar usuario: " + nombreUsuarioAdmin);

        UsuarioDAO usuarioDAO = new UsuarioDAO();

        try {
            // Primero verificamos si el usuario existe
            Usuario usuario = usuarioDAO.obtenerUsuarioPorNombreUsuario(nombreUsuarioAdmin);

            if (usuario != null) {
                System.out.println("Usuario encontrado: " + usuario.getNombres() + " " + usuario.getApellidos());
                System.out.println("Estado actual: " + usuario.getEstadoUsuario().name());

                // Intentamos activar el usuario
                boolean activado = usuarioDAO.activarUsuario(nombreUsuarioAdmin);

                if (activado) {
                    System.out.println("✅ Usuario activado correctamente");

                    // Verificamos el estado actual después de activar
                    usuario = usuarioDAO.obtenerUsuarioPorNombreUsuario(nombreUsuarioAdmin);
                    System.out.println("Estado actual: " + usuario.getEstadoUsuario().name());
                    mostrarResumenUsuario(usuario);
                } else {
                    System.out.println("⚠️ No se requirió activación o hubo un problema al activar el usuario");
                }
            } else {
                System.out.println("❌ No se encontró el usuario administrador: " + nombreUsuarioAdmin);
                System.out.println("Verifique que el nombre de usuario sea correcto.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error de SQL al activar el usuario: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Error general al activar el usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Muestra información resumida del usuario
     */
    private static void mostrarResumenUsuario(Usuario usuario) {
        System.out.println("\n===== INFORMACIÓN DEL USUARIO =====");
        System.out.println("ID: " + usuario.getId());
        System.out.println("Nombre completo: " + usuario.getNombres() + " " + usuario.getApellidos());
        System.out.println("Nombre de usuario: " + usuario.getNombreUsuario());
        System.out.println("Email: " + usuario.getEmail());
        System.out.println("Tipo: " + usuario.getTipoUsuario().name());
        System.out.println("Estado: " + usuario.getEstadoUsuario().name());
        System.out.println("Última actualización: " +
                (usuario.getUpdatedAt() != null ? usuario.getUpdatedAt() : "No disponible"));
        System.out.println("================================");
    }
}
