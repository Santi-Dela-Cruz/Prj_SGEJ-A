-- Script para eliminar la dependencia de la columna visible
-- Primero asegurémonos de que todos los registros tengan el estado correcto
UPDATE parametro SET estado = 'ACTIVO' WHERE visible = 1;
UPDATE parametro SET estado = 'INACTIVO' WHERE visible = 0;
