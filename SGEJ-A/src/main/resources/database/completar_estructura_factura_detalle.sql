-- Script para completar la estructura de la tabla factura_detalle
-- Añade todas las columnas necesarias para los detalles de factura

BEGIN TRANSACTION;

-- Verificar si existe la tabla factura_detalle, si no crearla
CREATE TABLE IF NOT EXISTS factura_detalle (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    factura_id INTEGER NOT NULL,
    codigo_servicio INTEGER NOT NULL DEFAULT 0,
    codigo_auxiliar TEXT,
    descripcion TEXT NOT NULL,
    cantidad DECIMAL(10,2) NOT NULL DEFAULT 1.00,
    precio_unitario DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    descuento DECIMAL(10,2) DEFAULT 0.00,
    valor_subtotal DECIMAL(10,2) DEFAULT 0.00,
    codigo_impuesto TEXT,
    codigo_tarifa_iva TEXT,
    porcentaje_iva DECIMAL(5,2) DEFAULT 0.00,
    valor_iva DECIMAL(10,2) DEFAULT 0.00,
    tipo_impuesto TEXT,
    usuario_creacion TEXT,
    usuario_modificacion TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (factura_id) REFERENCES factura(id)
);

-- Añadir las columnas una por una (SQLite ignorará si ya existen)
ALTER TABLE factura_detalle ADD COLUMN valor_subtotal DECIMAL(10,2) DEFAULT 0.00;
ALTER TABLE factura_detalle ADD COLUMN codigo_impuesto TEXT;
ALTER TABLE factura_detalle ADD COLUMN codigo_tarifa_iva TEXT;
ALTER TABLE factura_detalle ADD COLUMN porcentaje_iva DECIMAL(5,2) DEFAULT 0.00;
ALTER TABLE factura_detalle ADD COLUMN valor_iva DECIMAL(10,2) DEFAULT 0.00;
ALTER TABLE factura_detalle ADD COLUMN tipo_impuesto TEXT;
ALTER TABLE factura_detalle ADD COLUMN usuario_creacion TEXT;
ALTER TABLE factura_detalle ADD COLUMN usuario_modificacion TEXT;
ALTER TABLE factura_detalle ADD COLUMN created_at TIMESTAMP;
ALTER TABLE factura_detalle ADD COLUMN updated_at TIMESTAMP;

COMMIT;
