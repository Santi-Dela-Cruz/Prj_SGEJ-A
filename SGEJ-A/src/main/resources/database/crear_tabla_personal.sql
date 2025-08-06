-- Script para crear la tabla de personal si no existe
CREATE TABLE IF NOT EXISTS personal (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombres TEXT NOT NULL,
    apellidos TEXT NOT NULL,
    numero_identificacion TEXT NOT NULL UNIQUE,
    tipo_identificacion TEXT DEFAULT 'Cédula',
    telefono TEXT,
    correo TEXT,
    direccion TEXT,
    fecha_ingreso TEXT,
    rol TEXT NOT NULL,
    estado TEXT DEFAULT 'Activo',
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Crear índices para búsquedas más rápidas
CREATE INDEX IF NOT EXISTS idx_personal_numero_identificacion ON personal(numero_identificacion);
CREATE INDEX IF NOT EXISTS idx_personal_rol ON personal(rol);

-- Insertar algunos datos de ejemplo de abogados
INSERT OR IGNORE INTO personal (nombres, apellidos, numero_identificacion, tipo_identificacion, telefono, correo, direccion, fecha_ingreso, rol, estado)
VALUES 
    ('Juan Carlos', 'Pérez Mendoza', '1709876543', 'Cédula', '0998765432', 'juan.perez@estudio.com', 'Av. 10 de Agosto y Colón', '2024-01-15', 'Abogado', 'Activo'),
    ('María Elena', 'Gómez Torres', '1712345678', 'Cédula', '0987654321', 'maria.gomez@estudio.com', 'Av. República y Eloy Alfaro', '2023-08-10', 'Abogado', 'Activo'),
    ('Roberto', 'Suárez Vargas', '1798765432', 'Cédula', '0976543210', 'roberto.suarez@estudio.com', 'Calle Amazonas y Naciones Unidas', '2024-03-05', 'Abogado', 'Activo');
