-- Script para asegurar que los datos de institución estén correctamente establecidos
-- Este script actualiza directamente los valores en la tabla de parámetros

-- Actualizar RUC institucional
UPDATE parametro 
SET valor = '1790011119001', 
    valor_defecto = '1790011119001'
WHERE codigo = 'ruc_institucional';

-- Actualizar Razón Social
UPDATE parametro 
SET valor = 'ESTUDIO JURÍDICO INTEGRAL S.A.', 
    valor_defecto = 'ESTUDIO JURÍDICO INTEGRAL S.A.'
WHERE codigo = 'razon_social';

-- Actualizar Dirección Matriz
UPDATE parametro 
SET valor = 'AV. AMAZONAS N36-152 Y NACIONES UNIDAS', 
    valor_defecto = 'AV. AMAZONAS N36-152 Y NACIONES UNIDAS'
WHERE codigo = 'direccion_matriz';

-- Actualizar Dirección Sucursal
UPDATE parametro 
SET valor = 'CALLE PORTUGAL E9-138 Y AV. ELOY ALFARO', 
    valor_defecto = 'CALLE PORTUGAL E9-138 Y AV. ELOY ALFARO'
WHERE codigo = 'direccion_sucursal';

-- Actualizar Obligado a Contabilidad
UPDATE parametro 
SET valor = 'true', 
    valor_defecto = 'true'
WHERE codigo = 'obligado_contabilidad';

-- Insertar estos parámetros si no existen
INSERT OR IGNORE INTO parametro (codigo, nombre, descripcion, valor, tipo, estado, categoria)
VALUES 
('ruc_institucional', 'RUC Institucional', 'RUC que aparecerá en las facturas', 
  '1790011119001', 'TEXTO', 'ACTIVO', 'Legal/Fiscal'),
('razon_social', 'Razón Social', 'Nombre de la empresa', 
  'ESTUDIO JURÍDICO INTEGRAL S.A.', 'TEXTO', 'ACTIVO', 'Legal/Fiscal'),
('direccion_matriz', 'Dirección Matriz', 'Dirección de la matriz', 
  'AV. AMAZONAS N36-152 Y NACIONES UNIDAS', 'TEXTO', 'ACTIVO', 'Legal/Fiscal'),
('direccion_sucursal', 'Dirección Sucursal', 'Dirección de la sucursal', 
  'CALLE PORTUGAL E9-138 Y AV. ELOY ALFARO', 'TEXTO', 'ACTIVO', 'Legal/Fiscal'),
('obligado_contabilidad', 'Obligado a Llevar Contabilidad', 'Indica si está obligado a llevar contabilidad', 
  'true', 'BOOLEANO', 'ACTIVO', 'Legal/Fiscal');

-- Asegurarse de que estos parámetros sean visibles y en la categoría correcta
UPDATE parametro SET estado = 'ACTIVO', categoria = 'Legal/Fiscal'
WHERE codigo IN ('ruc_institucional', 'razon_social', 'direccion_matriz', 'direccion_sucursal', 'obligado_contabilidad');
