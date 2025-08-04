-- Script para actualizar la tabla factura y corregir nombres de columnas

-- Verificar si existe columna subtotal_12 (con guion bajo)
-- Si no existe, crearla y copiar datos de la columna subtotal12 (sin guion)
BEGIN TRANSACTION;

-- Renombrar la tabla factura a factura_temp
ALTER TABLE factura RENAME TO factura_temp;

-- Crear la tabla factura de nuevo con los campos correctos
CREATE TABLE factura (
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
    subtotal_12 REAL DEFAULT 0,  -- Nombre correcto con guion bajo
    subtotal_0 REAL DEFAULT 0,   -- Nombre correcto con guion bajo
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
    estado_factura TEXT DEFAULT 'PENDIENTE',
    pago_realizado INTEGER DEFAULT 0,
    clave_acceso TEXT,
    numero_autorizacion TEXT,
    fecha_autorizacion TEXT,
    estado_sri TEXT,
    respuesta_sri TEXT,
    fecha_respuesta_sri TEXT,
    usuario_creacion TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    usuario_modificacion TEXT,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Copiar datos de la tabla temporal a la nueva tabla con los nombres de columna corregidos
INSERT INTO factura (
    id, tipo_documento, ruc_emisor, razon_social_emisor, direccion_matriz, direccion_sucursal,
    obligado_contabilidad, codigo_establecimiento, codigo_punto_emision, secuencial, codigo_documento,
    fecha_emision, ambiente, emision, nombre_cliente, id_cliente, tipo_identificacion, direccion_cliente,
    telefono_cliente, email_cliente, numero_expediente, nombre_caso, abogado_responsable,
    subtotal_12, subtotal_0, subtotal_no_objeto_iva, subtotal_exento_iva, total_descuento,
    subtotal_sin_impuestos, valor_iva, porcentaje_iva, devolucion_iva, propina, valor_total,
    valor_sin_subsidio, ahorro_subsidio, forma_pago, monto_pago, plazo, estado_factura, pago_realizado,
    clave_acceso, numero_autorizacion, fecha_autorizacion, estado_sri, respuesta_sri, fecha_respuesta_sri,
    usuario_creacion, created_at, usuario_modificacion, updated_at
)
SELECT
    id, tipo_documento, ruc_emisor, razon_social_emisor, direccion_matriz, direccion_sucursal,
    obligado_contabilidad, codigo_establecimiento, codigo_punto_emision, secuencial, codigo_documento,
    fecha_emision, ambiente, emision, nombre_cliente, id_cliente, tipo_identificacion, direccion_cliente,
    telefono_cliente, email_cliente, numero_expediente, nombre_caso, abogado_responsable,
    subtotal12, subtotal0, subtotal_no_objeto_iva, subtotal_exento_iva, total_descuento,
    subtotal_sin_impuestos, valor_iva, porcentaje_iva, devolucion_iva, propina, valor_total,
    valor_sin_subsidio, ahorro_subsidio, forma_pago, monto_pago, plazo, estado_factura, pago_realizado,
    clave_acceso, numero_autorizacion, fecha_autorizacion, estado_sri, respuesta_sri, fecha_respuesta_sri,
    usuario_creacion, created_at, usuario_modificacion, updated_at
FROM factura_temp;

-- Eliminar la tabla temporal
DROP TABLE factura_temp;

-- Recrear índices
CREATE INDEX idx_factura_secuencial ON factura(secuencial);
CREATE INDEX idx_factura_cliente ON factura(id_cliente);
CREATE INDEX idx_factura_estado ON factura(estado_factura);

COMMIT;
