-- Script para corregir las categorías de los parámetros y establecer todos como inactivos

-- Actualizar categorías de parámetros según su funcionalidad
UPDATE parametro SET categoria = 'Seguridad' 
WHERE codigo IN ('max_intentos_fallidos', 'forzar_cambio_clave', 'tiempo_sesion');

UPDATE parametro SET categoria = 'Sistema' 
WHERE codigo IN ('ruta_archivos', 'formatos_permitidos', 'tamaño_maximo_archivo', 'nombre_sistema');

UPDATE parametro SET categoria = 'Institucional' 
WHERE codigo IN ('nombre_institucion', 'ruc_institucion', 'direccion_fiscal');

UPDATE parametro SET categoria = 'Notificaciones' 
WHERE codigo IN ('smtp_servidor', 'smtp_puerto', 'smtp_usuario', 'correo_remitente', 'correo_institucional', 'activar_notificaciones');

UPDATE parametro SET categoria = 'Legal/Fiscal' 
WHERE codigo IN ('iva');

-- Establecer todos los parámetros como inactivos inicialmente
UPDATE parametro SET estado = 'INACTIVO';
