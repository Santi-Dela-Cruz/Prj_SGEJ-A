package application.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase para gestionar la conexión a la base de datos SQLite
 */
public class DatabaseConnection {
    private static final String DATABASE_URL = "jdbc:sqlite:src/main/resources/database/sgej_database.db";
    private static Connection connection = null;

    /**
     * Obtiene la conexión a la base de datos
     * 
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
            String createClienteTable = """
                    CREATE TABLE IF NOT EXISTS cliente (
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

            stmt.execute(createClienteTable);

            String createUsuariosTable = """
                        CREATE TABLE IF NOT EXISTS usuarios (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            nombres_completos TEXT NOT NULL,
                            nombre_usuario TEXT NOT NULL UNIQUE,
                            numero_identificacion TEXT NOT NULL UNIQUE,
                            tipo_identificacion TEXT NOT NULL CHECK (tipo_identificacion IN ('CEDULA', 'RUC', 'PASAPORTE')),
                        telefono TEXT NOT NULL,
                        correo TEXT NOT NULL,
                        estado TEXT NOT NULL CHECK (estado IN ('ACTIVO', 'INACTIVO')),
                        direccion TEXT,
                        fecha_ingreso TEXT,
                        tipo_usuario TEXT NOT NULL CHECK (tipo_usuario IN ('NATURAL',   'JURIDICA')),
                        rol TEXT NOT NULL,
                        clave TEXT NOT NULL,
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
                    )
                    """;
            stmt.execute(createUsuariosTable);

            // Crear tabla de parámetros del sistema
            String createParametrosTable = """
                    CREATE TABLE IF NOT EXISTS parametros (
                        codigo TEXT PRIMARY KEY,
                        nombre TEXT NOT NULL,
                        descripcion TEXT,
                        valor TEXT NOT NULL,
                        tipo TEXT NOT NULL CHECK (tipo IN ('NUMERICO', 'TEXTO', 'TIEMPO')),
                        estado TEXT NOT NULL CHECK (estado IN ('ACTIVO', 'INACTIVO')),
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
                    )
                    """;
            stmt.execute(createParametrosTable);

            // Crear índices para mejorar el rendimiento
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_numero_identificacion ON cliente(numero_identificacion)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_nombre_completo ON cliente(nombre_completo)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_estado ON cliente(estado)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_usuario_identificacion ON usuarios(numero_identificacion)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_usuario_nombre ON          usuarios(nombre_usuario)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_usuario_estado ON          usuarios(estado)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_parametro_nombre ON parametros(nombre)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_parametro_estado ON            parametros(estado)");

            // Crear tabla de casos legales
            String createCasoTable = """
                    CREATE TABLE IF NOT EXISTS caso (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        cliente_id INTEGER,
                        numero_expediente TEXT,
                        titulo TEXT,
                        tipo TEXT,
                        fecha_inicio DATE,
                        descripcion TEXT,
                        estado TEXT,
                        FOREIGN KEY (cliente_id) REFERENCES cliente(id)
                    )
                    """;
            stmt.execute(createCasoTable);

            // Crear tabla de bitácoras de caso
            String createBitacoraTable = """
                    CREATE TABLE IF NOT EXISTS bitacora_caso (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        caso_id INTEGER,
                        fecha_entrada DATE,
                        usuario TEXT,
                        tipo_accion TEXT,
                        descripcion TEXT,
                        FOREIGN KEY (caso_id) REFERENCES caso(id)
                    )
                    """;
            stmt.execute(createBitacoraTable);

            // Crear tabla de abogados asignados a caso
            String createAbogadoCasoTable = """
                    CREATE TABLE IF NOT EXISTS abogado_caso (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        caso_id INTEGER,
                        abogado_id INTEGER,
                        rol TEXT,
                        fecha_asignacion DATE,
                        FOREIGN KEY (caso_id) REFERENCES caso(id)
                    )
                    """;
            stmt.execute(createAbogadoCasoTable);

            // Crear tabla de documentos de caso
            String createDocumentoCasoTable = """
                    CREATE TABLE IF NOT EXISTS documento_caso (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        caso_id INTEGER,
                        nombre TEXT,
                        ruta TEXT,
                        fecha_subida DATE,
                        FOREIGN KEY (caso_id) REFERENCES caso(id)
                    )
                    """;
            stmt.execute(createDocumentoCasoTable);

            // Crear tabla de historial de comunicaciones
            String createHistorialComTable = """
                    CREATE TABLE IF NOT EXISTS historial_comunicacion (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        caso_id INTEGER,
                        tipo TEXT,
                        fecha DATE,
                        descripcion TEXT,
                        FOREIGN KEY (caso_id) REFERENCES caso(id)
                    )
                    """;
            stmt.execute(createHistorialComTable);

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
