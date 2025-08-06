-- Agrega la columna abogado_id a la tabla caso si no existe
ALTER TABLE caso ADD COLUMN abogado_id INTEGER REFERENCES personal(id);

-- Crea la tabla abogado_caso si no existe (tabla de relación muchos a muchos)
CREATE TABLE IF NOT EXISTS abogado_caso (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    caso_id INTEGER NOT NULL,
    abogado_id INTEGER NOT NULL,
    rol TEXT,
    fecha_asignacion DATE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (caso_id) REFERENCES caso(id) ON DELETE CASCADE,
    FOREIGN KEY (abogado_id) REFERENCES personal(id) ON DELETE CASCADE,
    UNIQUE(caso_id, abogado_id)
);
