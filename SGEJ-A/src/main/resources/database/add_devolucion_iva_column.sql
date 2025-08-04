-- Script para corregir columnas faltantes en la tabla factura
PRAGMA foreign_keys=OFF;

-- Verificar si la tabla factura existe
CREATE TABLE IF NOT EXISTS factura (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    tipo_documento TEXT NOT NULL,
    ruc_emisor TEXT NOT NULL,
    razon_social_emisor TEXT NOT NULL,
    direccion_matriz TEXT,
    direccion_sucursal TEXT,
    obligado_contabilidad INTEGER DEFAULT 0,
    codigo_establecimiento TEXT NOT NULL,
    codigo_punto_emision TEXT NOT NULL,
    secuencial TEXT NOT NULL,
    codigo_documento TEXT,
    fecha_emision TEXT,
    ambiente TEXT,
    emision TEXT,
    nombre_cliente TEXT NOT NULL,
    id_cliente TEXT NOT NULL,
    tipo_identificacion TEXT,
    direccion_cliente TEXT,
    telefono_cliente TEXT,
    email_cliente TEXT,
    numero_expediente TEXT,
    nombre_caso TEXT,
    abogado_responsable TEXT,
    subtotal12 REAL DEFAULT 0,
    subtotal0 REAL DEFAULT 0,
    subtotal_no_objeto_iva REAL DEFAULT 0,
    subtotal_exento_iva REAL DEFAULT 0,
    total_descuento REAL DEFAULT 0,
    subtotal_sin_impuestos REAL DEFAULT 0,
    valor_iva REAL DEFAULT 0,
    porcentaje_iva REAL DEFAULT 12,
    devolucion_iva REAL DEFAULT 0,
    propina REAL DEFAULT 0,
    valor_total REAL DEFAULT 0,
    valor_sin_subsidio REAL,
    ahorro_subsidio REAL,
    forma_pago TEXT,
    monto_pago REAL,
    plazo INTEGER,
    tiempo_plazo TEXT,
    estado_factura TEXT DEFAULT 'PENDIENTE',
    estado_sri TEXT,
    respuesta_sri TEXT,
    fecha_respuesta_sri TEXT,
    pago_realizado INTEGER DEFAULT 0,
    estado_pago TEXT,
    clave_acceso TEXT,
    numero_autorizacion TEXT,
    fecha_autorizacion TEXT,
    usuario_creacion TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP,
    usuario_modificacion TEXT
);

-- Crear una tabla temporal con la estructura correcta
CREATE TABLE IF NOT EXISTS factura_temp (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    tipo_documento TEXT NOT NULL,
    ruc_emisor TEXT NOT NULL,
    razon_social_emisor TEXT NOT NULL,
    direccion_matriz TEXT,
    direccion_sucursal TEXT,
    obligado_contabilidad INTEGER DEFAULT 0,
    codigo_establecimiento TEXT NOT NULL,
    codigo_punto_emision TEXT NOT NULL,
    secuencial TEXT NOT NULL,
    codigo_documento TEXT,
    fecha_emision TEXT,
    ambiente TEXT,
    emision TEXT,
    nombre_cliente TEXT NOT NULL,
    id_cliente TEXT NOT NULL,
    tipo_identificacion TEXT,
    direccion_cliente TEXT,
    telefono_cliente TEXT,
    email_cliente TEXT,
    numero_expediente TEXT,
    nombre_caso TEXT,
    abogado_responsable TEXT,
    subtotal12 REAL DEFAULT 0,
    subtotal0 REAL DEFAULT 0,
    subtotal_no_objeto_iva REAL DEFAULT 0,
    subtotal_exento_iva REAL DEFAULT 0,
    total_descuento REAL DEFAULT 0,
    subtotal_sin_impuestos REAL DEFAULT 0,
    valor_iva REAL DEFAULT 0,
    porcentaje_iva REAL DEFAULT 12,
    devolucion_iva REAL DEFAULT 0,
    propina REAL DEFAULT 0,
    valor_total REAL DEFAULT 0,
    valor_sin_subsidio REAL,
    ahorro_subsidio REAL,
    forma_pago TEXT,
    monto_pago REAL,
    plazo INTEGER,
    tiempo_plazo TEXT,
    estado_factura TEXT DEFAULT 'PENDIENTE',
    estado_sri TEXT,
    respuesta_sri TEXT,
    fecha_respuesta_sri TEXT,
    pago_realizado INTEGER DEFAULT 0,
    estado_pago TEXT,
    clave_acceso TEXT,
    numero_autorizacion TEXT,
    fecha_autorizacion TEXT,
    usuario_creacion TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP,
    usuario_modificacion TEXT
);

-- Insertar datos de la tabla original a la temporal
INSERT OR IGNORE INTO factura_temp 
SELECT 
    id, tipo_documento, ruc_emisor, razon_social_emisor, direccion_matriz, direccion_sucursal,
    obligado_contabilidad, codigo_establecimiento, codigo_punto_emision, secuencial, codigo_documento,
    fecha_emision, ambiente, emision, nombre_cliente, id_cliente, tipo_identificacion,
    direccion_cliente, telefono_cliente, email_cliente, numero_expediente, nombre_caso, abogado_responsable,
    subtotal12, subtotal0, subtotal_no_objeto_iva, subtotal_exento_iva, total_descuento,
    subtotal_sin_impuestos, valor_iva, porcentaje_iva, 0 as devolucion_iva, propina, valor_total, valor_sin_subsidio,
    ahorro_subsidio, forma_pago, monto_pago, plazo, tiempo_plazo, estado_factura, estado_sri,
    respuesta_sri, fecha_respuesta_sri, pago_realizado, estado_pago, clave_acceso, numero_autorizacion,
    fecha_autorizacion, usuario_creacion, created_at, updated_at, usuario_modificacion
FROM factura;

-- Eliminar la tabla original
DROP TABLE IF EXISTS factura;

-- Renombrar la tabla temporal a factura
ALTER TABLE factura_temp RENAME TO factura;

-- Recrear los índices
CREATE INDEX IF NOT EXISTS idx_factura_secuencial ON factura(secuencial);
CREATE INDEX IF NOT EXISTS idx_factura_cliente ON factura(id_cliente);
CREATE INDEX IF NOT EXISTS idx_factura_estado ON factura(estado_factura);

-- Verificar la estructura final
PRAGMA table_info(factura);

PRAGMA foreign_keys=ON;
