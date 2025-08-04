-- Verificar y actualizar la tabla de parámetros (usando transacción para mayor seguridad)
PRAGMA foreign_keys = OFF;
BEGIN TRANSACTION;

-- Crear una tabla temporal con la estructura actualizada
CREATE TABLE IF NOT EXISTS parametro_temp (
    codigo TEXT PRIMARY KEY,
    nombre TEXT NOT NULL,
    descripcion TEXT,
    valor TEXT,
    valor_defecto TEXT,
    tipo TEXT,
    estado TEXT,
    categoria TEXT DEFAULT 'General',
    visible INTEGER DEFAULT 1,
    predefinido INTEGER DEFAULT 0,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Copiar datos de la tabla original (si existe)
INSERT OR IGNORE INTO parametro_temp(codigo, nombre, descripcion, valor, tipo, estado, created_at, updated_at)
SELECT codigo, nombre, descripcion, valor, tipo, estado, 
       COALESCE(created_at, CURRENT_TIMESTAMP), COALESCE(updated_at, CURRENT_TIMESTAMP)
FROM parametro;

-- Eliminar tabla original
DROP TABLE IF EXISTS parametro;

-- Renombrar la tabla temporal a la original
ALTER TABLE parametro_temp RENAME TO parametro;

COMMIT;
PRAGMA foreign_keys = ON;
