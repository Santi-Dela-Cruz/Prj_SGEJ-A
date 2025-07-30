-- Script para eliminar todos los datos excepto los usuarios
-- Primero obtenemos una lista de todas las tablas
.output tablas_temp.txt
.tables
.output stdout

-- Leemos el archivo para obtener todas las tablas y luego eliminamos datos de todas excepto USUARIOS
.read tablas_temp.txt

-- Desactivamos las restricciones de clave externa temporalmente
PRAGMA foreign_keys = OFF;

-- Aquí eliminaremos datos de todas las tablas excepto USUARIOS
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

-- Reactivamos las restricciones de clave externa
PRAGMA foreign_keys = ON;

-- Limpiamos secuencias de autoincremento
UPDATE sqlite_sequence SET seq = 0 WHERE name != 'usuarios';

-- Verificación
SELECT 'Limpieza completada. Las siguientes tablas han sido vaciadas:';
SELECT name FROM sqlite_master WHERE type='table' AND name != 'usuarios' AND name != 'sqlite_sequence';
SELECT 'La tabla usuarios se ha conservado con ' || COUNT(*) || ' registros.' FROM usuarios;
