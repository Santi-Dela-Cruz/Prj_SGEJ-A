-- Script para actualizar la estructura de la tabla factura_detalle
-- Añade las columnas necesarias si no existen

BEGIN TRANSACTION;

-- Verificar si existe la columna valor_subtotal
SELECT CASE 
    WHEN COUNT(*) = 0 THEN
        'ALTER TABLE factura_detalle ADD COLUMN valor_subtotal DECIMAL(10,2) DEFAULT 0.00;'
    ELSE
        'SELECT 1;' -- Consulta inofensiva si la columna ya existe
END AS sql_to_run
FROM pragma_table_info('factura_detalle') 
WHERE name='valor_subtotal';

-- Añadir la columna directamente (SQLite ignorará los errores si la columna ya existe)
ALTER TABLE factura_detalle ADD COLUMN valor_subtotal DECIMAL(10,2) DEFAULT 0.00;

COMMIT;
