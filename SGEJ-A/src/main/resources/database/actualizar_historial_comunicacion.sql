-- Actualizar la tabla historial_comunicacion para agregar el campo abogado_id
ALTER TABLE historial_comunicacion ADD COLUMN abogado_id INTEGER;

-- Crear un índice para el campo abogado_id
CREATE INDEX IF NOT EXISTS idx_historial_comunicacion_abogado ON historial_comunicacion(abogado_id);

-- Agregar clave foránea al campo abogado_id que referencia a la tabla personal
ALTER TABLE historial_comunicacion 
ADD CONSTRAINT fk_historial_abogado 
FOREIGN KEY (abogado_id) REFERENCES personal(id);
