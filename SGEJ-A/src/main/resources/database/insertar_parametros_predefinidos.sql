-- Script para insertar parámetros predefinidos en la base de datos

-- Parámetros Generales
INSERT OR IGNORE INTO parametro (codigo, nombre, descripcion, valor, tipo, estado, categoria, valor_defecto, visible, predefinido)
VALUES 
('nombre_sistema', 'Nombre del Sistema', 'Nombre visible del sistema en la interfaz principal (ej. en la barra superior o en los títulos de pantalla).', 'SGEJ-A', 'TEXTO', 'INACTIVO', 'General', 'SGEJ-A', 0, 1),
('nombre_institucion', 'Nombre de la Institución', 'Nombre de la entidad o estudio jurídico propietario del sistema. Se usa en documentos, reportes y pantallas.', 'Estudio Jurídico', 'TEXTO', 'INACTIVO', 'General', 'Estudio Jurídico', 0, 1),
('logo_sistema', 'Logo del Sistema', 'Ruta o referencia al logo institucional que se muestra en la interfaz principal y en reportes PDF.', '/icons/firmLogo.jpg', 'IMAGEN', 'INACTIVO', 'General', '/icons/firmLogo.jpg', 0, 1),
('idioma_sistema', 'Idioma del Sistema', 'Idioma por defecto de la interfaz del sistema. Ejemplo: es (español), en (inglés).', 'es', 'TEXTO', 'INACTIVO', 'General', 'es', 0, 1);

-- Parámetros de Sesión y Seguridad
INSERT OR IGNORE INTO parametro (codigo, nombre, descripcion, valor, tipo, estado, categoria, valor_defecto, visible, predefinido)
VALUES 
('tiempo_sesion', 'Tiempo de Sesión', 'Tiempo de inactividad (en minutos) tras el cual la sesión del usuario se cierra automáticamente.', '5', 'ENTERO', 'INACTIVO', 'Seguridad', '5', 0, 1),
('max_intentos_fallidos', 'Máximo de Intentos Fallidos', 'Número máximo de intentos fallidos de inicio de sesión antes de bloquear automáticamente la cuenta del usuario.', '3', 'ENTERO', 'INACTIVO', 'Seguridad', '3', 0, 1),
('forzar_cambio_clave', 'Forzar Cambio de Contraseña', 'Indica si los usuarios deben cambiar su contraseña al iniciar sesión por primera vez o después de un restablecimiento.', 'true', 'BOOLEANO', 'INACTIVO', 'Seguridad', 'true', 0, 1);

-- Parámetros Legales y Fiscales
INSERT OR IGNORE INTO parametro (codigo, nombre, descripcion, valor, tipo, estado, categoria, valor_defecto, visible, predefinido)
VALUES 
('ruc_institucion', 'RUC de la Institución', 'Número de RUC de la institución, utilizado en documentos fiscales y facturación.', '1790112233001', 'TEXTO', 'INACTIVO', 'Facturación', '1790112233001', 0, 1),
('direccion_fiscal', 'Dirección Fiscal', 'Dirección legal registrada ante el SRI, mostrada en encabezados de facturas.', 'Av. 10 de Agosto y Colón, Quito, Ecuador', 'TEXTO', 'INACTIVO', 'Facturación', 'Av. 10 de Agosto y Colón, Quito, Ecuador', 0, 1),
('correo_institucional', 'Correo Institucional', 'Correo electrónico oficial de contacto institucional, también usado como remitente en algunos correos.', 'contacto@estudiojuridico.com', 'TEXTO', 'INACTIVO', 'Facturación', 'contacto@estudiojuridico.com', 0, 1),
('iva', 'IVA', 'Porcentaje de IVA aplicable a los servicios facturados.', '0.12', 'DECIMAL', 'INACTIVO', 'Facturación', '0.12', 0, 1),
('retencion', 'Retención', 'Porcentaje de retención estándar aplicable en operaciones fiscales.', '0.10', 'DECIMAL', 'INACTIVO', 'Facturación', '0.10', 0, 1);

-- Parámetros de Notificaciones
INSERT OR IGNORE INTO parametro (codigo, nombre, descripcion, valor, tipo, estado, categoria, valor_defecto, visible, predefinido)
VALUES 
('activar_notificaciones', 'Activar Notificaciones', 'Activa o desactiva el envío automático de correos electrónicos desde el sistema.', 'true', 'BOOLEANO', 'INACTIVO', 'Notificaciones', 'true', 0, 1),
('correo_remitente', 'Correo Remitente', 'Dirección de correo desde la cual se enviarán las notificaciones automáticas del sistema.', 'notificaciones@estudiojuridico.com', 'TEXTO', 'INACTIVO', 'Notificaciones', 'notificaciones@estudiojuridico.com', 0, 1),
('smtp_servidor', 'Servidor SMTP', 'Dirección del servidor SMTP usado para enviar correos.', 'smtp.estudiojuridico.com', 'TEXTO', 'INACTIVO', 'Notificaciones', 'smtp.estudiojuridico.com', 0, 1),
('smtp_puerto', 'Puerto SMTP', 'Puerto utilizado para la conexión SMTP, comúnmente 587 o 465.', '587', 'ENTERO', 'INACTIVO', 'Notificaciones', '587', 0, 1),
('smtp_usuario', 'Usuario SMTP', 'Usuario o correo asociado al servidor SMTP que se usará para autenticarse.', 'notificaciones@estudiojuridico.com', 'TEXTO', 'INACTIVO', 'Notificaciones', 'notificaciones@estudiojuridico.com', 0, 1),
('smtp_clave', 'Contraseña SMTP', 'Contraseña o token de autenticación del usuario SMTP. Se debe mantener oculta o cifrada.', 'password123', 'CLAVE', 'INACTIVO', 'Notificaciones', 'password123', 0, 1);

-- Otros parámetros del sistema (Avanzados)
INSERT OR IGNORE INTO parametro (codigo, nombre, descripcion, valor, tipo, estado, categoria, valor_defecto, visible, predefinido)
VALUES 
('ruta_archivos', 'Ruta de Archivos', 'Ruta donde se guardan los archivos subidos al sistema.', 'uploads/documentos/', 'TEXTO', 'INACTIVO', 'Avanzado', 'uploads/documentos/', 0, 1),
('tamaño_maximo_archivo', 'Tamaño Máximo de Archivo', 'Tamaño máximo permitido para archivos subidos (en MB).', '10', 'ENTERO', 'INACTIVO', 'Avanzado', '10', 0, 1),
('formatos_permitidos', 'Formatos Permitidos', 'Formatos de archivo permitidos para subir al sistema.', 'pdf,doc,docx,jpg,png', 'TEXTO', 'INACTIVO', 'Avanzado', 'pdf,doc,docx,jpg,png', 0, 1);
