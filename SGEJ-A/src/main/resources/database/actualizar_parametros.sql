-- Script de migración para la tabla de parámetros
-- Este script actualiza la tabla de parámetros para incluir los nuevos campos necesarios

-- Iniciar una transacción para garantizar que todo el proceso se complete o se revierta completamente
BEGIN TRANSACTION;

-- Verificar si la tabla parametro existe
SELECT CASE 
    WHEN EXISTS (SELECT 1 FROM sqlite_master WHERE type='table' AND name='parametro') 
    THEN 'La tabla parametro existe, procediendo con la migración...'
    ELSE 'Error: La tabla parametro no existe!'
END;

-- 1. Crear una tabla temporal para respaldar los datos existentes
CREATE TABLE IF NOT EXISTS parametro_backup AS SELECT * FROM parametro;

-- 2. Eliminar la tabla antigua (pero ya tenemos un respaldo)
DROP TABLE IF EXISTS parametro;

-- 3. Crear la nueva tabla con la estructura actualizada
CREATE TABLE IF NOT EXISTS parametro (
    codigo TEXT PRIMARY KEY,
    nombre TEXT NOT NULL,
    descripcion TEXT,
    valor TEXT,
    valor_defecto TEXT, -- Nuevo: valor por defecto para restablecer
    tipo TEXT NOT NULL, -- Texto, Entero, Decimal, Booleano, Imagen, Clave
    categoria TEXT NOT NULL DEFAULT 'General', -- Nuevo: categoría para clasificar parámetros
    predefinido BOOLEAN NOT NULL DEFAULT 1, -- Nuevo: indica si es un parámetro del sistema
    visible BOOLEAN NOT NULL DEFAULT 1, -- Nuevo: indica si el parámetro está visible/activo
    estado TEXT DEFAULT 'ACTIVO',
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP
);

-- 4. Restaurar los datos del respaldo a la nueva tabla
INSERT INTO parametro (
    codigo, nombre, descripcion, valor, tipo, 
    estado, created_at, updated_at
)
SELECT 
    codigo, nombre, descripcion, valor, tipo, 
    estado, created_at, updated_at
FROM parametro_backup;

-- 5. Insertar los parámetros predefinidos del sistema
-- Solo si no existen ya en la tabla

-- Parámetros Generales
INSERT OR IGNORE INTO parametro (codigo, nombre, descripcion, valor, valor_defecto, tipo, categoria, predefinido, visible, estado)
VALUES 
('nombre_sistema', 'Nombre del Sistema', 'Nombre visible del sistema en la interfaz principal', 'SGEJ-A', 'SGEJ-A', 'TEXTO', 'General', 1, 1, 'ACTIVO'),
('nombre_institucion', 'Nombre Institución', 'Nombre de la entidad o estudio jurídico propietario del sistema', 'Estudio Jurídico', 'Estudio Jurídico', 'TEXTO', 'General', 1, 1, 'ACTIVO'),
('idioma_sistema', 'Idioma del Sistema', 'Idioma por defecto de la interfaz del sistema', 'es', 'es', 'TEXTO', 'General', 1, 1, 'ACTIVO');

-- Parámetros de Sesión y Seguridad
INSERT OR IGNORE INTO parametro (codigo, nombre, descripcion, valor, valor_defecto, tipo, categoria, predefinido, visible, estado)
VALUES 
('tiempo_sesion', 'Tiempo de Sesión', 'Tiempo de inactividad (en minutos) tras el cual la sesión del usuario se cierra automáticamente', '30', '30', 'ENTERO', 'Seguridad', 1, 0, 'ACTIVO'),
('max_intentos_fallidos', 'Máximo de Intentos', 'Número máximo de intentos fallidos de inicio de sesión antes de bloquear la cuenta', '3', '3', 'ENTERO', 'Seguridad', 1, 0, 'ACTIVO'),
('forzar_cambio_clave', 'Forzar Cambio de Clave', 'Indica si los usuarios deben cambiar su contraseña al iniciar sesión por primera vez', 'true', 'true', 'BOOLEANO', 'Seguridad', 1, 0, 'ACTIVO');

-- Parámetros Legales y Fiscales
INSERT OR IGNORE INTO parametro (codigo, nombre, descripcion, valor, valor_defecto, tipo, categoria, predefinido, visible, estado)
VALUES 
('ruc_institucion', 'RUC Institución', 'Número de RUC de la institución, utilizado en documentos fiscales', '9999999999001', '9999999999001', 'TEXTO', 'Fiscal', 1, 0, 'ACTIVO'),
('direccion_fiscal', 'Dirección Fiscal', 'Dirección legal registrada ante el SRI', 'Av. Principal 123', 'Av. Principal 123', 'TEXTO', 'Fiscal', 1, 0, 'ACTIVO'),
('correo_institucional', 'Correo Institucional', 'Correo electrónico oficial de contacto institucional', 'contacto@estudio.com', 'contacto@estudio.com', 'TEXTO', 'Fiscal', 1, 0, 'ACTIVO'),
('iva', 'IVA', 'Porcentaje de IVA aplicable a los servicios facturados', '0.12', '0.12', 'DECIMAL', 'Fiscal', 1, 0, 'ACTIVO'),
('retencion', 'Retención', 'Porcentaje de retención estándar aplicable en operaciones fiscales', '0.10', '0.10', 'DECIMAL', 'Fiscal', 1, 0, 'ACTIVO');

-- Parámetros de Notificaciones
INSERT OR IGNORE INTO parametro (codigo, nombre, descripcion, valor, valor_defecto, tipo, categoria, predefinido, visible, estado)
VALUES 
('activar_notificaciones', 'Activar Notificaciones', 'Activa o desactiva el envío automático de correos electrónicos desde el sistema', 'false', 'false', 'BOOLEANO', 'Notificaciones', 1, 0, 'ACTIVO'),
('correo_remitente', 'Correo Remitente', 'Dirección de correo desde la cual se enviarán las notificaciones automáticas', 'notificaciones@estudio.com', 'notificaciones@estudio.com', 'TEXTO', 'Notificaciones', 1, 0, 'ACTIVO'),
('smtp_servidor', 'Servidor SMTP', 'Dirección del servidor SMTP usado para enviar correos', 'smtp.gmail.com', 'smtp.gmail.com', 'TEXTO', 'Notificaciones', 1, 0, 'ACTIVO'),
('smtp_puerto', 'Puerto SMTP', 'Puerto utilizado para la conexión SMTP', '587', '587', 'ENTERO', 'Notificaciones', 1, 0, 'ACTIVO'),
('smtp_usuario', 'Usuario SMTP', 'Usuario o correo asociado al servidor SMTP que se usará para autenticarse', '', '', 'TEXTO', 'Notificaciones', 1, 0, 'ACTIVO'),
('smtp_clave', 'Clave SMTP', 'Contraseña o token de autenticación del usuario SMTP', '', '', 'CLAVE', 'Notificaciones', 1, 0, 'ACTIVO');

-- 6. Eliminar la tabla de respaldo
DROP TABLE IF EXISTS parametro_backup;

-- Confirmar la transacción
COMMIT;

-- Verificar la estructura de la tabla
PRAGMA table_info(parametro);

-- Mostrar los parámetros insertados
SELECT codigo, nombre, categoria, visible FROM parametro ORDER BY categoria, nombre;
