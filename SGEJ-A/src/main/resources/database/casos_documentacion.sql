-- Tabla de casos legales
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
);

-- Tabla de bitácoras de caso
CREATE TABLE IF NOT EXISTS bitacora_caso (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    caso_id INTEGER,
    fecha_entrada DATE,
    usuario TEXT,
    tipo_accion TEXT,
    descripcion TEXT,
    FOREIGN KEY (caso_id) REFERENCES caso(id)
);

-- Tabla de abogados asignados a caso
CREATE TABLE IF NOT EXISTS abogado_caso (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    caso_id INTEGER,
    abogado_id INTEGER,
    rol TEXT,
    fecha_asignacion DATE,
    FOREIGN KEY (caso_id) REFERENCES caso(id)
);

-- Tabla de documentos de caso
CREATE TABLE IF NOT EXISTS documento_caso (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    caso_id INTEGER,
    nombre TEXT,
    ruta TEXT,
    fecha_subida DATE,
    FOREIGN KEY (caso_id) REFERENCES caso(id)
);

-- Tabla de historial de comunicaciones
CREATE TABLE IF NOT EXISTS historial_comunicacion (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    caso_id INTEGER,
    tipo TEXT,
    fecha DATE,
    descripcion TEXT,
    FOREIGN KEY (caso_id) REFERENCES caso(id)
);
