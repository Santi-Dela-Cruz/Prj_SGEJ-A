package application.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase para gestionar la conexión a la base de datos SQLite
 */
public class DatabaseConnection {
    private static final String DATABASE_URL = "jdbc:sqlite:sgej_database.db";
    private static Connection connection = null;
    
    /**
     * Obtiene la conexión a la base de datos
     * @return Connection objeto de conexión
     * @throws SQLException si hay error en la conexión
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DATABASE_URL);
        }
        return connection;
    }
    
    /**
     * Inicializa la base de datos y crea las tablas necesarias
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Crear tabla de clientes
            String createClientesTable = """
                CREATE TABLE IF NOT EXISTS clientes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre_completo TEXT NOT NULL,
                    tipo_identificacion TEXT NOT NULL CHECK (tipo_identificacion IN ('CEDULA', 'RUC', 'PASAPORTE')),
                    tipo_persona TEXT NOT NULL CHECK (tipo_persona IN ('NATURAL', 'JURIDICA')),
                    numero_identificacion TEXT NOT NULL UNIQUE,
                    direccion TEXT NOT NULL,
                    telefono TEXT NOT NULL,
                    correo_electronico TEXT NOT NULL,
                    estado TEXT NOT NULL CHECK (estado IN ('ACTIVO', 'INACTIVO')),
                    fecha_registro TEXT NOT NULL,
                    estado_civil TEXT,
                    representante_legal TEXT,
                    direccion_fiscal TEXT,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
                """;
            
            stmt.execute(createClientesTable);
            
            // Crear índices para mejorar el rendimiento
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_numero_identificacion ON clientes(numero_identificacion)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_nombre_completo ON clientes(nombre_completo)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_estado ON clientes(estado)");
            
            System.out.println("Base de datos inicializada correctamente");
            
        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Cierra la conexión a la base de datos
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
