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

-- Insertar un usuario administrador maestro 
INSERT OR IGNORE INTO usuarios (
    nombres, 
    apellidos, 
    identificacion, 
    email, 
    nombre_usuario, 
    clave, 
    tipo_usuario, 
    estado_usuario
) VALUES (
    'Administrador', 
    'Sistema', 
    '1700000000', 
    'admin@sistemajuridico.com', 
    'admin', 
    'root1234', 
    'INTERNO', 
    'ACTIVO'
);
