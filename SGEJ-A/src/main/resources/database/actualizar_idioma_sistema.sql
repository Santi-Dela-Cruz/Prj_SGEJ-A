-- Asegurarnos de que el idioma esté configurado como español
UPDATE parametro SET valor = 'es', valor_defecto = 'es'
WHERE codigo = 'idioma_sistema';
