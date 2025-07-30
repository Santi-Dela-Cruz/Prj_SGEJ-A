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
            executeScript("/db/01_crear_tabla_usuarios.sql");
            // Agrega más scripts según sea necesario
            
            LOGGER.info("Base de datos inicializada correctamente");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al inicializar la base de datos", e);
        }
    }
    
    /**
     * Ejecuta un script SQL desde los recursos
     * @param scriptPath Ruta al script en los recursos
     */
    private static void executeScript(String scriptPath) throws IOException, SQLException {
        String script = loadScriptFromResources(scriptPath);
        if (script.isEmpty()) {
            LOGGER.warning("Script vacío o no encontrado: " + scriptPath);
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Dividir el script por punto y coma y ejecutar cada sentencia
            for (String statement : script.split(";")) {
                if (!statement.trim().isEmpty()) {
                    stmt.execute(statement);
                }
            }
            
            LOGGER.info("Script ejecutado correctamente: " + scriptPath);
        }
    }
    
    /**
     * Carga un script SQL desde los recursos
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
}
