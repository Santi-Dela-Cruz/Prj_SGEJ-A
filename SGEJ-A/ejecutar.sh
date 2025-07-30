#!/bin/bash
# Script para ejecutar la aplicación SGEJ-A

echo "==========================================="
echo "EJECUTANDO SISTEMA SGEJ-A (Versión Mejorada)"
echo "==========================================="

# Compilar el proyecto con Maven
echo "Compilando el proyecto..."
mvn clean compile

# Verificar la base de datos
echo "Verificando base de datos..."
if [ ! -f sgej_database.db ]; then
    echo "Creando base de datos nueva..."
    touch sgej_database.db
fi

# Ejecutar la aplicación
echo "Ejecutando la aplicacion JavaFX..."
mvn javafx:run
