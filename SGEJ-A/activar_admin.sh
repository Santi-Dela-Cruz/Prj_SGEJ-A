#!/bin/bash
# Script para activar el usuario administrador en la base de datos SQLite

# Ruta de la base de datos
DB_PATH="src/main/resources/database/sgej_database.db"

# Verificar que el archivo de la base de datos existe
if [ ! -f "$DB_PATH" ]; then
    echo "Error: No se encontró la base de datos en $DB_PATH"
    exit 1
fi

# Mensaje inicial
echo "====================================================="
echo "      Activación de Usuario Administrador"
echo "====================================================="

# Mostrar estado actual del usuario admin
echo "Estado actual del usuario administrador:"
sqlite3 "$DB_PATH" "SELECT id, nombre_usuario, estado_usuario FROM usuarios WHERE nombre_usuario = 'admin';" || {
    echo "Error: No se pudo consultar la base de datos."
    exit 1
}

# Preguntar confirmación
echo ""
echo "¿Desea activar el usuario 'admin'? (s/n)"
read -r respuesta

if [[ "$respuesta" == "s" || "$respuesta" == "S" ]]; then
    # Ejecutar la consulta para activar el usuario
    echo "Activando usuario administrador..."
    sqlite3 "$DB_PATH" "UPDATE usuarios SET estado_usuario = 'ACTIVO', updated_at = datetime('now') WHERE nombre_usuario = 'admin';"
    
    # Verificar si se realizó el cambio correctamente
    if [ $? -eq 0 ]; then
        echo "Usuario administrador activado correctamente."
        echo ""
        echo "Estado actual del usuario administrador:"
        sqlite3 "$DB_PATH" "SELECT id, nombre_usuario, estado_usuario, updated_at FROM usuarios WHERE nombre_usuario = 'admin';"
    else
        echo "Error: No se pudo activar el usuario administrador."
        exit 1
    fi
else
    echo "Operación cancelada."
    exit 0
fi

echo ""
echo "====================================================="
echo "              Proceso completado"
echo "====================================================="
