-- Script para crear tabla de facturas
PRAGMA foreign_keys=ON;

-- Tabla de facturas
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
    usuario_creacion TEXT,
    fecha_creacion TEXT DEFAULT CURRENT_TIMESTAMP,
    usuario_modificacion TEXT,
    fecha_modificacion TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Tabla para detalles de factura
CREATE TABLE IF NOT EXISTS factura_detalle (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    factura_id INTEGER NOT NULL,
    codigo_servicio INTEGER,
    codigo_auxiliar TEXT,
    descripcion TEXT NOT NULL,
    cantidad REAL NOT NULL,
    precio_unitario REAL NOT NULL,
    descuento REAL DEFAULT 0,
    tipo_impuesto TEXT DEFAULT 'IVA_12',
    FOREIGN KEY (factura_id) REFERENCES factura(id) ON DELETE CASCADE
);

-- Índices para mejorar rendimiento
CREATE INDEX IF NOT EXISTS idx_factura_secuencial ON factura(secuencial);
CREATE INDEX IF NOT EXISTS idx_factura_cliente ON factura(id_cliente);
CREATE INDEX IF NOT EXISTS idx_factura_estado ON factura(estado_factura);
CREATE INDEX IF NOT EXISTS idx_factura_detalle ON factura_detalle(factura_id);
