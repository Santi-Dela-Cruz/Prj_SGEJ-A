-- Script para activar el usuario administrador en la base de datos
-- Ejecuta este script directamente en la base de datos SQLite

-- Actualiza el estado del usuario administrador a ACTIVO
UPDATE usuarios 
SET estado_usuario = 'ACTIVO', 
    updated_at = datetime('now', 'localtime')
WHERE nombre_usuario = 'admin';

-- Verifica que se haya actualizado correctamente
SELECT id, nombres, apellidos, identificacion, email, nombre_usuario, tipo_usuario, estado_usuario, created_at, updated_at 
FROM usuarios 
WHERE nombre_usuario = 'admin';
