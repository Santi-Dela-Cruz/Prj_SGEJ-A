-- Script para eliminar la columna visible de la tabla parametro

-- Eliminar tabla de respaldo anterior si existe
DROP TABLE IF EXISTS parametro_backup;

-- Recrear la tabla parametro sin la columna visible
CREATE TABLE parametro_temp (
    codigo TEXT PRIMARY KEY,
    nombre TEXT NOT NULL,
    descripcion TEXT,
    valor TEXT,
    valor_defecto TEXT,
    tipo TEXT NOT NULL,
    categoria TEXT DEFAULT 'General',
    predefinido BOOLEAN DEFAULT 0,
    estado TEXT CHECK(estado IN ('ACTIVO', 'INACTIVO')) DEFAULT 'ACTIVO',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Copiar datos excluyendo la columna visible
INSERT INTO parametro_temp (
    codigo, nombre, descripcion, valor, valor_defecto, 
    tipo, categoria, predefinido, estado, created_at, updated_at
)
SELECT 
    codigo, nombre, descripcion, valor, valor_defecto, 
    tipo, categoria, predefinido, estado, created_at, updated_at
FROM parametro;

-- Eliminar tabla original y renombrar la nueva
DROP TABLE parametro;
ALTER TABLE parametro_temp RENAME TO parametro;
