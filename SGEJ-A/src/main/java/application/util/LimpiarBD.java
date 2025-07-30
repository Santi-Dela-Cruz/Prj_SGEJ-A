package application.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import application.controllers.DialogUtil;
import application.database.DatabaseConnection;
import javafx.scene.control.ButtonType;

/**
 * Utilidad para limpiar la base de datos
 */
public class LimpiarBD {

    // Lista de tablas que no se limpiarán
    private static final List<String> TABLAS_PRESERVADAS = Arrays.asList(
            "usuarios", "sqlite_sequence", "sqlite_stat1");

    /**
     * Punto de entrada principal para ejecución directa
     */
    public static void main(String[] args) {
        try {
            boolean exito = limpiarBaseDatosExceptoUsuarios();
            System.exit(exito ? 0 : 1);
        } catch (Exception e) {
            System.err.println("Error al limpiar la base de datos: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Limpia todas las tablas de la base de datos excepto las de usuarios
     * 
     * @return true si se limpió correctamente, false si hubo error
     */
    public static boolean limpiarBaseDatosExceptoUsuarios() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();

            // Desactivar restricciones de clave externa temporalmente
            try (Statement pragmaStmt = conn.createStatement()) {
                pragmaStmt.execute("PRAGMA foreign_keys = OFF");
            }

            // Obtener todas las tablas
            List<String> tablas = new ArrayList<>();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table'");

            while (rs.next()) {
                String nombreTabla = rs.getString("name");
                if (!TABLAS_PRESERVADAS.contains(nombreTabla)) {
                    tablas.add(nombreTabla);
                }
            }

            // Eliminar datos de cada tabla
            for (String tabla : tablas) {
                try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM " + tabla)) {
                    pstmt.executeUpdate();
                    System.out.println("Datos eliminados de la tabla: " + tabla);
                } catch (SQLException e) {
                    System.err.println("Error al limpiar la tabla " + tabla + ": " + e.getMessage());
                }
            }

            // Reiniciar secuencias de autoincremento
            try (Statement seqStmt = conn.createStatement()) {
                seqStmt.execute("UPDATE sqlite_sequence SET seq = 0 WHERE name NOT IN ('usuarios')");
            }

            // Reactivar restricciones de clave externa
            try (Statement pragmaStmt = conn.createStatement()) {
                pragmaStmt.execute("PRAGMA foreign_keys = ON");
            }

            // Si se ejecuta desde línea de comandos, no mostrar diálogo gráfico
            if (java.awt.GraphicsEnvironment.isHeadless()) {
                System.out.println("\nLimpieza completada exitosamente.");
                System.out.println("Se han eliminado todos los datos excepto los usuarios.");
                System.out.println("Tablas limpiadas: " + String.join(", ", tablas));
            } else {
                // Mostrar mensaje de éxito con diálogo gráfico
                DialogUtil.mostrarDialogo(
                        "Limpieza de Base de Datos",
                        "Se han eliminado todos los datos excepto los usuarios.\n" +
                                "Tablas limpiadas: " + String.join(", ", tablas),
                        "info",
                        List.of(ButtonType.OK));
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();

            // Si se ejecuta desde línea de comandos, no mostrar diálogo gráfico
            if (java.awt.GraphicsEnvironment.isHeadless()) {
                System.err.println("\nError al limpiar la base de datos: " + e.getMessage());
            } else {
                // Mostrar mensaje de error con diálogo gráfico
                DialogUtil.mostrarDialogo(
                        "Error al Limpiar Base de Datos",
                        "Ocurrió un error al limpiar la base de datos: " + e.getMessage(),
                        "error",
                        List.of(ButtonType.OK));
            }

            return false;
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
