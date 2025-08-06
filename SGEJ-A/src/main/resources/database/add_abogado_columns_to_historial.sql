-- Añade la columna abogado_id a la tabla historial_comunicacion si no existe
ALTER TABLE historial_comunicacion ADD COLUMN abogado_id INTEGER;

-- Añade la columna abogado_nombre a la tabla historial_comunicacion si no existe
ALTER TABLE historial_comunicacion ADD COLUMN abogado_nombre TEXT;
