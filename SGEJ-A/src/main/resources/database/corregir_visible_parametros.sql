-- Script para corregir la inconsistencia entre estado y visible
UPDATE parametro SET visible = 0 WHERE estado = 'INACTIVO';
UPDATE parametro SET visible = 1 WHERE estado = 'ACTIVO';
