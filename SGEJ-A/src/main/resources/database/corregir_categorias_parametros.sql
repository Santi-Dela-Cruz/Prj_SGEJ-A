-- Script para corregir las categorías de los parámetros

-- Parámetros Generales (ya están en General, no necesitan actualización)
-- No actualizamos 'nombre_sistema', 'nombre_institucion', 'logo_sistema', 'idioma_sistema'

-- Parámetros de Sesión y Seguridad
UPDATE parametro SET categoria = 'Seguridad' WHERE codigo IN ('tiempo_sesion', 'max_intentos_fallidos', 'forzar_cambio_clave');

-- Parámetros Legales y Fiscales
UPDATE parametro SET categoria = 'Facturación' WHERE codigo IN ('ruc_institucion', 'direccion_fiscal', 'correo_institucional', 'iva', 'retencion');

-- Parámetros de Notificaciones
UPDATE parametro SET categoria = 'Notificaciones' WHERE codigo IN ('activar_notificaciones', 'correo_remitente', 'smtp_servidor', 'smtp_puerto', 'smtp_usuario', 'smtp_clave');

-- Otros parámetros del sistema (Avanzados)
UPDATE parametro SET categoria = 'Avanzado' WHERE codigo IN ('ruta_archivos', 'tamaño_maximo_archivo', 'formatos_permitidos');
