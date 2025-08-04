-- Script para crear o actualizar la tabla de facturación

-- Verificar si la tabla existe, y si no, crearla
CREATE TABLE IF NOT EXISTS factura (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    -- Datos generales
    tipo_documento TEXT NOT NULL,
    clave_acceso TEXT,
    numero_autorizacion TEXT,
    fecha_autorizacion TIMESTAMP,
    ambiente TEXT DEFAULT 'PRUEBAS',
    emision TEXT DEFAULT 'NORMAL',
    
    -- Datos del emisor
    ruc_emisor TEXT NOT NULL,
    razon_social_emisor TEXT NOT NULL,
    direccion_matriz TEXT,
    direccion_sucursal TEXT,
    obligado_contabilidad BOOLEAN DEFAULT 0,
    
    -- Datos del establecimiento y secuencia
    codigo_establecimiento TEXT,
    codigo_punto_emision TEXT,
    secuencial TEXT,
    codigo_documento TEXT,
    
    -- Fecha de emisión
    fecha_emision DATE NOT NULL,
    
    -- Datos del cliente
    nombre_cliente TEXT NOT NULL,
    id_cliente TEXT NOT NULL,
    tipo_identificacion TEXT,
    direccion_cliente TEXT,
    telefono_cliente TEXT,
    email_cliente TEXT,
    
    -- Caso relacionado
    numero_expediente TEXT,
    nombre_caso TEXT,
    abogado_responsable TEXT,
    
    -- Totales
    subtotal_12 DECIMAL(12,2) DEFAULT 0,
    subtotal_0 DECIMAL(12,2) DEFAULT 0,
    subtotal_no_objeto_iva DECIMAL(12,2) DEFAULT 0,
    subtotal_exento_iva DECIMAL(12,2) DEFAULT 0,
    subtotal_sin_impuestos DECIMAL(12,2) NOT NULL,
    total_descuento DECIMAL(12,2) DEFAULT 0,
    valor_iva DECIMAL(12,2) DEFAULT 0,
    porcentaje_iva DECIMAL(5,2) DEFAULT 12,
    devolucion_iva DECIMAL(12,2) DEFAULT 0,
    propina DECIMAL(12,2) DEFAULT 0,
    valor_total DECIMAL(12,2) NOT NULL,
    
    -- Subsidio
    valor_sin_subsidio DECIMAL(12,2) DEFAULT 0,
    ahorro_subsidio DECIMAL(12,2) DEFAULT 0,
    
    -- Pago
    forma_pago TEXT,
    monto_pago DECIMAL(12,2) DEFAULT 0,
    plazo INTEGER DEFAULT 0,
    estado_factura TEXT DEFAULT 'ABIERTO',
    pago_realizado BOOLEAN DEFAULT 0,
    
    -- Auditoría
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion TEXT,
    usuario_modificacion TEXT,
    
    -- SRI
    estado_sri TEXT DEFAULT 'PENDIENTE',
    respuesta_sri TEXT,
    fecha_respuesta_sri TIMESTAMP
);

-- Crear tabla para los detalles de productos/servicios
CREATE TABLE IF NOT EXISTS factura_detalle (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    factura_id INTEGER NOT NULL,
    cantidad DECIMAL(10,2) NOT NULL,
    codigo_principal TEXT,
    codigo_auxiliar TEXT,
    descripcion TEXT NOT NULL,
    precio_unitario DECIMAL(12,2) NOT NULL,
    descuento DECIMAL(12,2) DEFAULT 0,
    precio_total DECIMAL(12,2) NOT NULL,
    
    -- Impuestos
    porcentaje_iva DECIMAL(5,2) DEFAULT 12,
    valor_iva DECIMAL(12,2) DEFAULT 0,
    
    -- Auditoría
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Relación con la factura
    FOREIGN KEY (factura_id) REFERENCES factura(id) ON DELETE CASCADE
);

-- Verificar si existen columnas adicionales necesarias y añadirlas si no existen
-- Esto es para asegurar compatibilidad con versiones anteriores

-- Primero, verificamos si ya existen las columnas más importantes,
-- y si no, las agregamos
BEGIN TRANSACTION;

-- Verificar y agregar columnas para datos generales
SELECT CASE 
    WHEN NOT EXISTS(SELECT 1 FROM pragma_table_info('factura') WHERE name = 'tipo_documento') 
    THEN 'ALTER TABLE factura ADD COLUMN tipo_documento TEXT DEFAULT "FACTURA"'
END AS sql_statement WHERE sql_statement IS NOT NULL;

SELECT CASE 
    WHEN NOT EXISTS(SELECT 1 FROM pragma_table_info('factura') WHERE name = 'clave_acceso') 
    THEN 'ALTER TABLE factura ADD COLUMN clave_acceso TEXT'
END AS sql_statement WHERE sql_statement IS NOT NULL;

SELECT CASE 
    WHEN NOT EXISTS(SELECT 1 FROM pragma_table_info('factura') WHERE name = 'ambiente') 
    THEN 'ALTER TABLE factura ADD COLUMN ambiente TEXT DEFAULT "PRUEBAS"'
END AS sql_statement WHERE sql_statement IS NOT NULL;

-- Columnas de subsidio
SELECT CASE 
    WHEN NOT EXISTS(SELECT 1 FROM pragma_table_info('factura') WHERE name = 'valor_sin_subsidio') 
    THEN 'ALTER TABLE factura ADD COLUMN valor_sin_subsidio DECIMAL(12,2) DEFAULT 0'
END AS sql_statement WHERE sql_statement IS NOT NULL;

SELECT CASE 
    WHEN NOT EXISTS(SELECT 1 FROM pragma_table_info('factura') WHERE name = 'ahorro_subsidio') 
    THEN 'ALTER TABLE factura ADD COLUMN ahorro_subsidio DECIMAL(12,2) DEFAULT 0'
END AS sql_statement WHERE sql_statement IS NOT NULL;

COMMIT;

-- Insertar o actualizar parámetros relacionados con facturación
INSERT OR REPLACE INTO parametro 
    (codigo, nombre, descripcion, valor, valor_defecto, tipo, categoria, predefinido, visible, estado) 
VALUES 
    ('iva', 'IVA', 'Porcentaje de IVA aplicable a los servicios facturados', '12', '12', 'DECIMAL', 'Facturación', 1, 1, 'ACTIVO'),
    ('porcentaje_descuento', 'Porcentaje de Descuento Predeterminado', 'Porcentaje de descuento que se aplica por defecto a los servicios', '5', '5', 'DECIMAL', 'Facturación', 1, 1, 'ACTIVO'),
    ('dias_pago_factura', 'Días Plazo para Pago', 'Número de días por defecto para el pago de facturas', '30', '30', 'ENTERO', 'Facturación', 1, 1, 'ACTIVO'),
    ('propina_sugerida', 'Porcentaje de Propina Sugerida', 'Porcentaje sugerido para propinas', '10', '10', 'DECIMAL', 'Facturación', 1, 1, 'ACTIVO'),
    ('ruc_institucion', 'RUC de la Institución', 'Número de RUC de la institución', '1790112233001', '1790112233001', 'TEXTO', 'Facturación', 1, 1, 'ACTIVO'),
    ('razon_social', 'Razón Social', 'Razón social completa de la empresa', 'ESTUDIO JURÍDICO LEGAL S.A.', 'ESTUDIO JURÍDICO LEGAL S.A.', 'TEXTO', 'Facturación', 1, 1, 'ACTIVO'),
    ('direccion_matriz', 'Dirección Matriz', 'Dirección de la oficina matriz', 'Av. 10 de Agosto y Colón, Quito, Ecuador', 'Av. 10 de Agosto y Colón, Quito, Ecuador', 'TEXTO', 'Facturación', 1, 1, 'ACTIVO'),
    ('direccion_sucursal', 'Dirección Sucursal', 'Dirección de la sucursal emisora', 'Av. Amazonas y Naciones Unidas, Quito, Ecuador', 'Av. Amazonas y Naciones Unidas, Quito, Ecuador', 'TEXTO', 'Facturación', 1, 1, 'ACTIVO'),
    ('obligado_contabilidad', 'Obligado a Llevar Contabilidad', 'Determina si la empresa está obligada a llevar contabilidad', 'SI', 'SI', 'TEXTO', 'Facturación', 1, 1, 'ACTIVO'),
    ('codigo_establecimiento', 'Código de Establecimiento', 'Código de establecimiento asignado por el SRI', '001', '001', 'TEXTO', 'Facturación', 1, 1, 'ACTIVO'),
    ('codigo_punto_emision', 'Código de Punto de Emisión', 'Código del punto de emisión asignado por el SRI', '001', '001', 'TEXTO', 'Facturación', 1, 1, 'ACTIVO'),
    ('ambiente_facturacion', 'Ambiente de Facturación', 'Ambiente de facturación electrónica (PRUEBAS o PRODUCCIÓN)', 'PRUEBAS', 'PRUEBAS', 'TEXTO', 'Facturación', 1, 1, 'ACTIVO'),
    ('secuencial_actual', 'Secuencial Actual', 'Número secuencial actual para emisión de facturas', '000000001', '000000001', 'TEXTO', 'Facturación', 1, 1, 'ACTIVO'),
    ('subsidio_activo', 'Subsidio Activo', 'Indica si se debe mostrar información de subsidios en las facturas', 'NO', 'NO', 'TEXTO', 'Facturación', 1, 1, 'ACTIVO');
