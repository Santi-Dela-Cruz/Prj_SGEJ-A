package application.database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Clase utilitaria para ejecutar la migración que añade las relaciones entre
 * abogados y casos.
 */
public class MigracionAbogadoCaso {

    /**
     * Ejecuta el script de migración para agregar el campo abogado_id a la tabla
     * caso
     * y crear la tabla abogado_caso si no existe.
     * 
     * @return true si la migración se completa exitosamente, false en caso
     *         contrario
     */
    public static boolean ejecutarMigracion() {
        Connection conn = null;
        Statement stmt = null;

        try {
            // Obtener la conexión a la base de datos
            conn = DatabaseConnection.getConnection();

            // Cargar el script SQL desde los recursos
            String sqlScript;
            try (InputStream inputStream = MigracionAbogadoCaso.class
                    .getResourceAsStream("/database/add_abogado_to_caso.sql");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                sqlScript = reader.lines().collect(Collectors.joining("\n"));
            }

            // Dividir el script en comandos individuales
            String[] commands = sqlScript.split(";");

            // Ejecutar cada comando
            stmt = conn.createStatement();
            for (String command : commands) {
                if (!command.trim().isEmpty()) {
                    try {
                        stmt.execute(command);
                        System.out.println("Ejecutado comando SQL: " + command);
                    } catch (SQLException e) {
                        // Si la columna ya existe, continuamos con el siguiente comando
                        if (e.getMessage().contains("duplicate column name") ||
                                e.getMessage().contains("table abogado_caso already exists")) {
                            System.out.println("Nota: " + e.getMessage());
                        } else {
                            throw e;
                        }
                    }
                }
            }

            System.out.println("Migración completada exitosamente");
            return true;

        } catch (Exception e) {
            System.err.println("Error durante la migración: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Cerrar el statement
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            // No cerramos la conexión aquí, ya que es gestionada por DatabaseConnection
        }
    }
}
