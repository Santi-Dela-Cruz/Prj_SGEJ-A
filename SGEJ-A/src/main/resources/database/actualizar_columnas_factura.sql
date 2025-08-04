-- Actualización para tabla factura
-- Este script modifica la tabla factura para corregir los nombres de columna subtotal12 y subtotal0

-- Primero verificamos si las columnas incorrectas existen
PRAGMA table_info(factura);

-- Crear una tabla temporal con la estructura correcta
CREATE TABLE IF NOT EXISTS factura_temp (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    numero_secuencial TEXT,
    punto_emision TEXT,
    establecimiento TEXT,
    fecha_emision TEXT,
    id_cliente INTEGER,
    total_sin_impuestos REAL,
    subtotal_12 REAL,  -- Nombre corregido con guion bajo
    subtotal_0 REAL,   -- Nombre corregido con guion bajo
    iva REAL,
    total_descuento REAL,
    propina REAL,
    valor_total REAL,
    estado TEXT,
    forma_pago TEXT,
    plazo INTEGER,
    tiempo_plazo TEXT,
    estado_pago TEXT,
    numero_expediente TEXT,
    fecha_registro TEXT,
    usuario_registro TEXT,
    fecha_actualizacion TEXT,
    usuario_actualizacion TEXT,
    subtotal_no_iva REAL,
    subtotal_excento_iva REAL,
    devolucion_iva REAL,
    FOREIGN KEY(id_cliente) REFERENCES cliente(id)
);

-- Copiar datos de la tabla original a la temporal
INSERT INTO factura_temp (
    id, numero_secuencial, punto_emision, establecimiento, fecha_emision,
    id_cliente, total_sin_impuestos, 
    subtotal_12, subtotal_0,  -- Nombres correctos
    iva, total_descuento, propina, valor_total,
    estado, forma_pago, plazo, tiempo_plazo, estado_pago,
    numero_expediente, fecha_registro, usuario_registro,
    fecha_actualizacion, usuario_actualizacion,
    subtotal_no_iva, subtotal_excento_iva, devolucion_iva
)
SELECT 
    id, numero_secuencial, punto_emision, establecimiento, fecha_emision,
    id_cliente, total_sin_impuestos, 
    subtotal12, subtotal0,  -- Nombres incorrectos
    iva, total_descuento, propina, valor_total,
    estado, forma_pago, plazo, tiempo_plazo, estado_pago,
    numero_expediente, fecha_registro, usuario_registro,
    fecha_actualizacion, usuario_actualizacion,
    subtotal_no_iva, subtotal_excento_iva, devolucion_iva
FROM factura;

-- Eliminar la tabla original
DROP TABLE factura;

-- Renombrar la tabla temporal
ALTER TABLE factura_temp RENAME TO factura;

-- Crear los índices necesarios
CREATE INDEX IF NOT EXISTS idx_factura_id_cliente ON factura(id_cliente);
CREATE INDEX IF NOT EXISTS idx_factura_numero_secuencial ON factura(numero_secuencial);
