-- ESTRUCTURA PRINCIPAL DE LA BASE DE DATOS SGEJ-A
-- Todas las tablas y relaciones necesarias para el sistema

PRAGMA foreign_keys=ON;

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombres TEXT NOT NULL,
    apellidos TEXT NOT NULL,
    identificacion TEXT,
    email TEXT,
    nombre_usuario TEXT NOT NULL UNIQUE,
    clave TEXT NOT NULL,
    tipo_usuario TEXT DEFAULT 'INTERNO',
    estado_usuario TEXT DEFAULT 'ACTIVO',
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de clientes
CREATE TABLE IF NOT EXISTS cliente (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_completo TEXT NOT NULL,
    tipo_identificacion TEXT NOT NULL,
    tipo_persona TEXT NOT NULL,
    numero_identificacion TEXT NOT NULL,
    direccion TEXT,
    telefono TEXT,
    correo_electronico TEXT,
    estado TEXT DEFAULT 'ACTIVO',
    fecha_registro TEXT DEFAULT CURRENT_TIMESTAMP,
    estado_civil TEXT,
    representante_legal TEXT,
    direccion_fiscal TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de casos
CREATE TABLE IF NOT EXISTS caso (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    cliente_id INTEGER NOT NULL,
    numero_expediente TEXT NOT NULL,
    titulo TEXT,
    tipo TEXT,
    fecha_inicio DATE,
    descripcion TEXT,
    estado TEXT,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

-- Tabla de bitácora de casos
CREATE TABLE IF NOT EXISTS bitacora_caso (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    caso_id INTEGER NOT NULL,
    fecha_entrada DATE NOT NULL,
    tipo_accion TEXT NOT NULL,
    descripcion TEXT,
    FOREIGN KEY (caso_id) REFERENCES caso(id)
);
CREATE INDEX IF NOT EXISTS idx_bitacora_caso_id ON bitacora_caso(caso_id);

-- Tabla de abogados asignados a casos
CREATE TABLE IF NOT EXISTS abogado_caso (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    caso_id INTEGER NOT NULL,
    abogado_id INTEGER NOT NULL,
    rol TEXT,
    fecha_asignacion DATE,
    FOREIGN KEY (caso_id) REFERENCES caso(id)
);

-- Tabla de documentos de casos
CREATE TABLE IF NOT EXISTS documento_caso (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    caso_id INTEGER NOT NULL,
    nombre TEXT,
    ruta TEXT,
    fecha_subida DATE,
    FOREIGN KEY (caso_id) REFERENCES caso(id)
);

-- Tabla de historial de comunicación
CREATE TABLE IF NOT EXISTS historial_comunicacion (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    caso_id INTEGER NOT NULL,
    tipo TEXT,
    fecha DATE,
    descripcion TEXT,
    FOREIGN KEY (caso_id) REFERENCES caso(id)
);

-- Tabla de parámetros del sistema
CREATE TABLE IF NOT EXISTS parametro (
    codigo TEXT PRIMARY KEY,
    nombre TEXT NOT NULL,
    descripcion TEXT,
    valor TEXT,
    tipo TEXT,
    estado TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Usuario administrador por defecto
INSERT OR IGNORE INTO usuarios (
    nombres, apellidos, identificacion, email, nombre_usuario, clave, tipo_usuario, estado_usuario
) VALUES (
    'Administrador', 'Sistema', '1700000000', 'admin@sistemajuridico.com', 'admin', 'root1234', 'INTERNO', 'ACTIVO'
);
