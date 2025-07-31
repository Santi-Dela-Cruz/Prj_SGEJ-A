-- Script para modificar la estructura de la tabla bitacora_caso
-- Este script hará lo siguiente:
-- 1. Crear una tabla temporal con la nueva estructura
-- 2. Copiar los datos de la tabla original a la temporal (sin la columna usuario)
-- 3. Eliminar la tabla original
-- 4. Renombrar la tabla temporal

-- Crear tabla temporal con la nueva estructura (sin el campo usuario)
CREATE TABLE bitacora_caso_temp (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    caso_id INTEGER NOT NULL,
    fecha_entrada DATE NOT NULL,
    tipo_accion TEXT NOT NULL,
    descripcion TEXT,
    FOREIGN KEY (caso_id) REFERENCES casos(id)
);

-- Copiar datos de la tabla original a la tabla temporal (omitiendo el campo usuario)
INSERT INTO bitacora_caso_temp (id, caso_id, fecha_entrada, tipo_accion, descripcion)
SELECT id, caso_id, fecha_entrada, tipo_accion, descripcion
FROM bitacora_caso;

-- Eliminar la tabla original
DROP TABLE bitacora_caso;

-- Renombrar la tabla temporal a la original
ALTER TABLE bitacora_caso_temp RENAME TO bitacora_caso;

-- Crear índice para mejorar el rendimiento de las consultas
CREATE INDEX idx_bitacora_caso_id ON bitacora_caso(caso_id);
