@echo off
REM Batch para ejecutar la aplicación SGEJ-A de manera más robusta

echo ===========================================
echo EJECUTANDO SISTEMA SGEJ-A (Versión Mejorada)
echo ===========================================

REM Compilar el proyecto con Maven
echo Compilando el proyecto...
call mvn clean compile

REM Crear la base de datos o verificar que existe
echo Verificando base de datos...
sqlite3 sgej_database.db ".tables"

REM Ejecutar la aplicación
echo Ejecutando la aplicacion JavaFX...
call mvn javafx:run

echo.
echo Presiona cualquier tecla para salir...
pause > nul
