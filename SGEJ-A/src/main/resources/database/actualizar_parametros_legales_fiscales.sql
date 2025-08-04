-- Script para actualizar los parámetros legales/fiscales del sistema
-- Creado: 4 de agosto de 2025

-- Eliminar parámetros obsoletos (marcándolos como inactivos)
UPDATE parametro SET estado = 'INACTIVO' WHERE codigo IN ('correo_institucional', 'direccion_fiscal', 'retencion');

-- Asegurar que las categorías existan
INSERT OR IGNORE INTO categoria_parametro (nombre, descripcion) 
VALUES 
('Facturación', 'Parámetros relacionados con el cálculo y generación de facturas'),
('Institución', 'Datos institucionales de la empresa');

-- Actualizar o crear parámetros necesarios
INSERT OR REPLACE INTO parametro (codigo, nombre, descripcion, valor, tipo, estado, categoria, valor_defecto, orden, visible)
VALUES 
-- Parámetros de facturación
('porcentaje_iva', 'Porcentaje de IVA', 'Porcentaje de IVA aplicable a las facturas', '12', 'DECIMAL', 'ACTIVO', 'Facturación', '12', 1, 1),
('subtotal_porcentaje', 'Porcentaje para Subtotal', 'Porcentaje aplicable al subtotal de facturas', '12', 'DECIMAL', 'ACTIVO', 'Facturación', '12', 2, 1),
('codigo_establecimiento', 'Código de Establecimiento', 'Código de establecimiento para facturación', '001', 'TEXTO', 'ACTIVO', 'Facturación', '001', 3, 1),
('codigo_punto_emision', 'Código de Punto de Emisión', 'Código del punto de emisión para facturación', '001', 'TEXTO', 'ACTIVO', 'Facturación', '001', 4, 1),
('ambiente_facturacion', 'Ambiente de Facturación', 'Ambiente para facturación electrónica (1=PRUEBAS, 2=PRODUCCIÓN)', '1', 'TEXTO', 'ACTIVO', 'Facturación', '1', 5, 1),
('tipo_emision', 'Tipo de Emisión', 'Tipo de emisión para facturación (1=NORMAL)', '1', 'TEXTO', 'ACTIVO', 'Facturación', '1', 6, 1),
('codigo_documento_factura', 'Código Documento Factura', 'Código del tipo de documento factura', '01', 'TEXTO', 'ACTIVO', 'Facturación', '01', 7, 1),
('tipo_documento_factura', 'Tipo de Documento', 'Tipo de documento para facturación', 'FACTURA', 'TEXTO', 'ACTIVO', 'Facturación', 'FACTURA', 8, 1),

-- Parámetros institucionales
('ruc_institucional', 'RUC Institucional', 'RUC que aparecerá en las facturas', '1790011119001', 'TEXTO', 'ACTIVO', 'Institución', '1790011119001', 1, 1),
('razon_social', 'Razón Social', 'Nombre de la empresa', 'ESTUDIO JURÍDICO INTEGRAL S.A.', 'TEXTO', 'ACTIVO', 'Institución', 'ESTUDIO JURÍDICO INTEGRAL S.A.', 2, 1),
('direccion_matriz', 'Dirección Matriz', 'Dirección de la matriz', 'AV. AMAZONAS N36-152 Y NACIONES UNIDAS', 'TEXTO', 'ACTIVO', 'Institución', 'AV. AMAZONAS N36-152', 3, 1),
('direccion_sucursal', 'Dirección Sucursal', 'Dirección de la sucursal', 'CALLE PORTUGAL E9-138 Y AV. ELOY ALFARO', 'TEXTO', 'ACTIVO', 'Institución', 'CALLE PORTUGAL E9-138', 4, 1),
('obligado_contabilidad', 'Obligado a Llevar Contabilidad', 'Indica si está obligado a llevar contabilidad', 'true', 'BOOLEANO', 'ACTIVO', 'Institución', 'true', 5, 1);

-- Actualizar parámetros existentes si cambiaron de nombre
UPDATE parametro SET codigo = 'ruc_institucional' WHERE codigo = 'ruc_empresa';

-- Asegurar que los parámetros de facturación estén en la categoría correcta
UPDATE parametro SET categoria = 'Facturación' 
WHERE codigo IN ('porcentaje_iva', 'subtotal_porcentaje', 'codigo_establecimiento', 'codigo_punto_emision', 
               'ambiente_facturacion', 'tipo_emision', 'codigo_documento_factura', 'tipo_documento_factura');

-- Asegurar que los parámetros institucionales estén en la categoría correcta
UPDATE parametro SET categoria = 'Institución' 
WHERE codigo IN ('ruc_institucional', 'razon_social', 'direccion_matriz', 'direccion_sucursal', 'obligado_contabilidad');

-- Invalidar la caché para que los cambios tengan efecto inmediato
-- (Para sistemas que usan caché de parámetros)
