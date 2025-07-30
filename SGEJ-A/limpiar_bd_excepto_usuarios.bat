@echo off
echo Limpiando la base de datos, manteniendo solo usuarios...
echo.

REM Verificar si sqlite3.exe existe en el PATH
where sqlite3 > nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: No se encuentra sqlite3.exe en el PATH
    echo Por favor, asegúrate de tener SQLite instalado y agregado al PATH
    echo Descarga SQLite desde https://www.sqlite.org/download.html
    pause
    exit /b 1
)

REM Ejecutar el script SQL
sqlite3 sgej_database.db < limpiar_bd_excepto_usuarios.sql

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Hubo un problema al limpiar la base de datos.
    pause
    exit /b 1
)

echo.
echo Base de datos limpiada correctamente. 
echo Solo se conservaron los datos de usuarios.
echo.

pause
