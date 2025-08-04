package application.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase para inicializar la base de datos con scripts SQL
 */
public class DatabaseInitializer {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

    /**
     * Inicializa la base de datos ejecutando scripts SQL
     */
    public static void initialize() {
        try {
            // Control para ejecutar scripts solo si es necesario
            boolean tablaExiste = checkIfTableExists("parametro");

            // Primero eliminar todas las tablas temporales o de respaldo
            executeScript("/database/eliminar_tablas_temp.sql");

            // Crear tabla de usuarios si no existe
            executeScript("/db/01_crear_tabla_usuarios.sql");

            // Crear o recrear la tabla factura_detalle con la estructura correcta
            executeScript("/database/crear_tabla_factura_detalle.sql");

            if (!tablaExiste) {
                LOGGER.info("Tabla de parámetros no existe, ejecutando scripts de inicialización completa");
                // Solo ejecutar estos scripts si la tabla no existe o es la primera vez
                executeScript("/database/actualizar_tabla_parametros.sql");
                executeScript("/database/insertar_parametros_predefinidos.sql");
                executeScript("/database/eliminar_columna_visible.sql");
                executeScript("/database/corregir_categorias_y_eliminar_visible.sql");
            } else {
                LOGGER.info("Tabla de parámetros ya existe, solo actualizando categorías");
            }

            // Estos scripts se ejecutan siempre para asegurar datos correctos
            executeScript("/database/actualizar_categorias_parametros.sql");
            executeScript("/database/actualizar_idioma_sistema.sql");

            // Actualizar estructura de la tabla factura para corregir nombres de columnas
            executeScript("/database/actualizar_campos_factura.sql");

            // Actualizar estructura de la tabla factura_detalle para añadir columna
            // valor_subtotal
            executeScript("/database/actualizar_estructura_factura_detalle.sql");

            // Completar estructura de la tabla factura_detalle con todas las columnas
            // necesarias
            executeScript("/database/completar_estructura_factura_detalle.sql");

            // Corregir nombres de columnas en la tabla factura (subtotal12 -> subtotal_12,
            // etc.)
            executeScript("/database/actualizar_columnas_factura.sql");

            // Corregir estructura completa de la tabla factura para asegurar todas las
            // columnas necesarias
            executeScript("/database/corregir_estructura_factura.sql");

            // Agregar la columna devolucion_iva que falta en la tabla factura
            executeScript("/database/add_devolucion_iva_column.sql");

            // Actualizar los parámetros legales y fiscales para la facturación
            executeScript("/database/actualizar_parametros_legales_fiscales.sql");

            // Actualizar directamente los datos de la institución para asegurar que estén
            // correctos
            executeScript("/database/actualizar_datos_institucion.sql");

            // Actualizar categorías de los parámetros institucionales y eliminar duplicados
            executeScript("/database/actualizar_categoria_parametros_institucionales.sql");

            // Limpiar tablas de respaldo y temporales siempre al final
            executeScript("/database/limpiar_tablas_backup.sql");

            // Agrega más scripts según sea necesario

            LOGGER.info("Base de datos inicializada correctamente");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al inicializar la base de datos", e);
        }
    }

    /**
     * Ejecuta un script SQL desde los recursos
     * 
     * @param scriptPath Ruta al script en los recursos
     */
    private static void executeScript(String scriptPath) throws IOException, SQLException {
        String script = loadScriptFromResources(scriptPath);
        if (script.isEmpty()) {
            LOGGER.warning("Script vacío o no encontrado: " + scriptPath);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Usar autocommit para evitar problemas de transacciones
            conn.setAutoCommit(true);

            try (Statement stmt = conn.createStatement()) {
                // Dividir el script por punto y coma y ejecutar cada sentencia
                for (String statement : script.split(";")) {
                    if (!statement.trim().isEmpty()) {
                        try {
                            stmt.execute(statement.trim());
                            System.out.println("DEPURACIÓN SQL: Ejecutada sentencia: " + statement.trim());
                        } catch (SQLException e) {
                            System.err.println("Error en sentencia SQL: " + statement.trim());
                            System.err.println("Mensaje de error: " + e.getMessage());
                            // Continuamos con las demás sentencias aunque una falle
                        }
                    }
                }
            }

            LOGGER.info("Script ejecutado correctamente: " + scriptPath);
        }
    }

    /**
     * Carga un script SQL desde los recursos
     * 
     * @param scriptPath Ruta al script en los recursos
     * @return Contenido del script
     */
    private static String loadScriptFromResources(String scriptPath) throws IOException {
        StringBuilder scriptContent = new StringBuilder();

        try (InputStream is = DatabaseInitializer.class.getResourceAsStream(scriptPath);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = reader.readLine()) != null) {
                scriptContent.append(line).append("\n");
            }
        }

        return scriptContent.toString();
    }

    /**
     * Verifica si una tabla existe en la base de datos
     * 
     * @param tableName Nombre de la tabla a verificar
     * @return true si la tabla existe, false si no
     */
    private static boolean checkIfTableExists(String tableName) {
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            // En SQLite podemos consultar la tabla sqlite_master para verificar si existe
            // una tabla
            String query = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'";

            try (var rs = stmt.executeQuery(query)) {
                return rs.next(); // Si hay al menos un resultado, la tabla existe
            }

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error al verificar la existencia de la tabla " + tableName, e);
            // Si hay error, asumimos que la tabla no existe para recrearla
            return false;
        }
    }
}
