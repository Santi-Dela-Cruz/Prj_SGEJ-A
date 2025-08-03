-- Script para actualizar las categorías de los parámetros según la nueva estructura completa
-- Categorías: General, Seguridad, Legal/Fiscal, Notificaciones, Sistema

-- Actualizar parámetros de la categoría GENERAL
UPDATE parametro SET categoria = 'General' WHERE codigo IN (
    'EMPRESA_NOMBRE',
    'EMPRESA_LOGO',
    'EMPRESA_DIRECCION',
    'EMPRESA_EMAIL',
    'EMPRESA_TELEFONO',
    'EMPRESA_WEBSITE',
    'DEFAULT_LANG',
    'nombre_sistema',
    'nombre_institucion',
    'logo_sistema',
    'idioma_sistema'
);

-- Actualizar parámetros de la categoría SEGURIDAD
UPDATE parametro SET categoria = 'Seguridad' WHERE codigo IN (
    'LOGIN_MAX_ATTEMPTS',
    'LOGIN_LOCKOUT_TIME',
    'PASSWORD_EXPIRE_DAYS',
    'PASSWORD_MIN_LENGTH',
    'PASSWORD_REQUIRE_ALPHA',
    'PASSWORD_REQUIRE_NUMBERS',
    'PASSWORD_REQUIRE_SYMBOLS',
    'BACKUP_FREQUENCY',
    'SESSION_TIMEOUT',
    'tiempo_sesion',
    'max_intentos_fallidos',
    'forzar_cambio_clave'
);

-- Actualizar parámetros de la categoría LEGAL/FISCAL
UPDATE parametro SET categoria = 'Legal/Fiscal' WHERE codigo IN (
    'FACTURA_SERIE',
    'FACTURA_CORRELATIVO',
    'FACTURA_IGV',
    'RUC_EMPRESA',
    'RAZON_SOCIAL',
    'REGIMEN_TRIBUTARIO',
    'MONEDA_DEFECTO',
    'COMPROBANTE_PATH',
    'FIRMA_DIGITAL_PATH',
    'CERTIFICADO_DIGITAL',
    'ruc_institucion',
    'direccion_fiscal',
    'correo_institucional',
    'iva',
    'retencion'
);

-- Actualizar parámetros de la categoría NOTIFICACIONES
UPDATE parametro SET categoria = 'Notificaciones' WHERE codigo IN (
    'EMAIL_SERVIDOR_SMTP',
    'EMAIL_PUERTO',
    'EMAIL_USUARIO',
    'EMAIL_PASSWORD',
    'EMAIL_SSL',
    'EMAIL_REMITENTE',
    'EMAIL_NOTIFICACIONES',
    'NOTIFICACION_AUDIENCIA',
    'NOTIFICACION_VENCIMIENTO',
    'NOTIFICACION_DOCUMENTOS',
    'activar_notificaciones',
    'correo_remitente',
    'smtp_servidor',
    'smtp_puerto',
    'smtp_usuario',
    'smtp_clave'
);

-- Actualizar parámetros de la categoría SISTEMA
UPDATE parametro SET categoria = 'Sistema' WHERE codigo IN (
    'LOG_LEVEL',
    'UPLOAD_PATH',
    'DOCUMENTO_PATH',
    'AUDIENCIA_PATH',
    'PERSONAL_PATH',
    'CACHE_TIEMPO',
    'PAGINATOR_ITEMS',
    'DEBUG_MODE',
    'MANTENIMIENTO_MODE',
    'AUTO_UPDATE',
    'ruta_archivos',
    'tamaño_maximo_archivo',
    'formatos_permitidos'
);

-- Actualizar cualquier otro parámetro que no se haya actualizado a la categoría General
UPDATE parametro SET categoria = 'General' WHERE categoria IS NULL OR categoria = '' OR categoria = 'Avanzados';
UPDATE parametro SET tipo = 'ENTERO'
WHERE codigo IN (
    'tiempo_sesion',
    'max_intentos_fallidos',
    'smtp_puerto',
    'tamaño_maximo_archivo'
);

UPDATE parametro SET tipo = 'BOOLEANO'
WHERE codigo IN (
    'forzar_cambio_clave',
    'activar_notificaciones'
);

UPDATE parametro SET tipo = 'DECIMAL'
WHERE codigo IN (
    'iva',
    'retencion'
);

UPDATE parametro SET tipo = 'CLAVE'
WHERE codigo IN (
    'smtp_clave'
);

UPDATE parametro SET tipo = 'IMAGEN'
WHERE codigo IN (
    'logo_sistema'
);
