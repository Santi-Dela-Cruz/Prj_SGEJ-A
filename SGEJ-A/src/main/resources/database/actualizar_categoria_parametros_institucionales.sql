-- Script para actualizar categorías de parámetros institucionales y eliminar duplicados
-- Creado: 4 de agosto de 2025

-- Primero: Eliminar los parámetros especificados
DELETE FROM parametro WHERE codigo IN ('ruc_institucion', 'direccion_fiscal', 'retencion');

-- Segundo: Mover parámetros de "Institución" a "Legal/Fiscal"
UPDATE parametro SET categoria = 'Legal/Fiscal' 
WHERE codigo IN ('ruc_institucional', 'razon_social', 'direccion_matriz', 'direccion_sucursal', 'obligado_contabilidad');

-- Tercero: Asegurar que los parámetros estén activos
UPDATE parametro SET estado = 'ACTIVO' 
WHERE codigo IN ('ruc_institucional', 'razon_social', 'direccion_matriz', 'direccion_sucursal', 'obligado_contabilidad');

-- Cuarto: Actualizar los valores si es necesario
UPDATE parametro 
SET valor = '1790011119001', 
    valor_defecto = '1790011119001'
WHERE codigo = 'ruc_institucional';

UPDATE parametro 
SET valor = 'ESTUDIO JURÍDICO INTEGRAL S.A.', 
    valor_defecto = 'ESTUDIO JURÍDICO INTEGRAL S.A.'
WHERE codigo = 'razon_social';

UPDATE parametro 
SET valor = 'AV. AMAZONAS N36-152 Y NACIONES UNIDAS', 
    valor_defecto = 'AV. AMAZONAS N36-152 Y NACIONES UNIDAS'
WHERE codigo = 'direccion_matriz';

UPDATE parametro 
SET valor = 'CALLE PORTUGAL E9-138 Y AV. ELOY ALFARO', 
    valor_defecto = 'CALLE PORTUGAL E9-138 Y AV. ELOY ALFARO'
WHERE codigo = 'direccion_sucursal';

UPDATE parametro 
SET valor = 'true', 
    valor_defecto = 'true'
WHERE codigo = 'obligado_contabilidad';
