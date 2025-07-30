
PRAGMA foreign_keys = OFF;

-- Obtener todas las tablas excepto usuarios y tablas del sistema
DELETE FROM clientes;
DELETE FROM casos;
DELETE FROM documentos;
DELETE FROM audiencias;
DELETE FROM facturas;
DELETE FROM detalles_factura;
DELETE FROM empleados;
DELETE FROM parametros;
DELETE FROM comunicaciones;
DELETE FROM bitacora;

-- Reiniciar secuencias de autoincremento
UPDATE sqlite_sequence SET seq = 0 WHERE name != 'usuarios';

PRAGMA foreign_keys = ON;

-- Mensaje de confirmación
SELECT 'Se han eliminado todos los datos excepto los usuarios. Las tablas han sido limpiadas.';

