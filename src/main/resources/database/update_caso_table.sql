-- Script para actualizar la tabla caso y asegurar la relación con cliente
PRAGMA foreign_keys=off;

CREATE TABLE caso_new (
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

INSERT INTO caso_new (id, cliente_id, numero_expediente, titulo, tipo, fecha_inicio, descripcion, estado)
SELECT id, cliente_id, numero_expediente, titulo, tipo, fecha_inicio, descripcion, estado FROM caso;

DROP TABLE caso;
ALTER TABLE caso_new RENAME TO caso;

PRAGMA foreign_keys=on;
