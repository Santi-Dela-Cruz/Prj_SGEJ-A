-- Script para crear la tabla factura_detalle si no existe
-- con toda la estructura necesaria de columnas

DROP TABLE IF EXISTS factura_detalle_temp;

-- Crear tabla temporal para almacenar datos existentes si la tabla ya existe
CREATE TABLE IF NOT EXISTS factura_detalle_temp AS 
SELECT * FROM factura_detalle WHERE 0=1;

-- Intentar copiar datos existentes (si los hay) a la tabla temporal
INSERT OR IGNORE INTO factura_detalle_temp 
SELECT * FROM factura_detalle WHERE 1=1;

-- Eliminar la tabla original
DROP TABLE IF EXISTS factura_detalle;

-- Crear la tabla con la estructura completa
CREATE TABLE factura_detalle (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    factura_id INTEGER NOT NULL,
    codigo_servicio INTEGER NOT NULL DEFAULT 0,
    codigo_auxiliar TEXT,
    descripcion TEXT NOT NULL,
    cantidad DECIMAL(10,2) NOT NULL DEFAULT 1.00,
    precio_unitario DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    descuento DECIMAL(10,2) DEFAULT 0.00,
    valor_subtotal DECIMAL(10,2) DEFAULT 0.00,
    codigo_impuesto TEXT DEFAULT '',
    codigo_tarifa_iva TEXT DEFAULT '',
    porcentaje_iva DECIMAL(5,2) DEFAULT 0.00,
    valor_iva DECIMAL(10,2) DEFAULT 0.00,
    tipo_impuesto TEXT DEFAULT 'IVA_0',
    usuario_creacion TEXT,
    usuario_modificacion TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (factura_id) REFERENCES factura(id)
);

-- Intentar restaurar los datos (si había alguno)
INSERT OR IGNORE INTO factura_detalle (
    id, factura_id, codigo_servicio, codigo_auxiliar, descripcion, 
    cantidad, precio_unitario, descuento, valor_subtotal, 
    codigo_impuesto, codigo_tarifa_iva, porcentaje_iva, valor_iva, tipo_impuesto,
    usuario_creacion, usuario_modificacion, created_at, updated_at
) 
SELECT 
    id, factura_id, codigo_servicio, codigo_auxiliar, descripcion,
    cantidad, precio_unitario, descuento, valor_subtotal,
    codigo_impuesto, codigo_tarifa_iva, porcentaje_iva, valor_iva, tipo_impuesto,
    usuario_creacion, usuario_modificacion, created_at, updated_at
FROM factura_detalle_temp;

-- Eliminar la tabla temporal
DROP TABLE IF EXISTS factura_detalle_temp;

-- Crear índice para mejorar rendimiento en búsquedas por factura_id
CREATE INDEX IF NOT EXISTS idx_factura_detalle_factura_id ON factura_detalle(factura_id);
