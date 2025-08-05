-- Script para activar el usuario administrador
-- Cambia el estado del usuario 'admin' de INACTIVO a ACTIVO

UPDATE usuarios 
SET estado_usuario = 'ACTIVO', 
    updated_at = datetime('now') 
WHERE nombre_usuario = 'admin';

-- Consulta para verificar que se haya actualizado correctamente
SELECT id, nombre_usuario, estado_usuario, updated_at 
FROM usuarios 
WHERE nombre_usuario = 'admin';
