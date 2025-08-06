@echo off
:: Script para activar el usuario administrador en la base de datos SQLite

:: Configuración
SET DB_PATH=src\main\resources\database\sgej_database.db

:: Verificar que el archivo de la base de datos existe
IF NOT EXIST "%DB_PATH%" (
    echo Error: No se encontro la base de datos en %DB_PATH%
    exit /b 1
)

:: Mensaje inicial
echo =====================================================
echo       Activacion de Usuario Administrador
echo =====================================================

:: Mostrar estado actual del usuario admin
echo Estado actual del usuario administrador:
sqlite3 "%DB_PATH%" "SELECT id, nombre_usuario, estado_usuario FROM usuarios WHERE nombre_usuario = 'admin';"
if %ERRORLEVEL% NEQ 0 (
    echo Error: No se pudo consultar la base de datos.
    exit /b 1
)

:: Preguntar confirmación
echo.
set /p respuesta=Desea activar el usuario 'admin'? (s/n): 

if /i "%respuesta%"=="s" (
    :: Ejecutar la consulta para activar el usuario
    echo Activando usuario administrador...
    sqlite3 "%DB_PATH%" "UPDATE usuarios SET estado_usuario = 'ACTIVO', updated_at = datetime('now') WHERE nombre_usuario = 'admin';"
    
    :: Verificar si se realizó el cambio correctamente
    if %ERRORLEVEL% EQU 0 (
        echo Usuario administrador activado correctamente.
        echo.
        echo Estado actual del usuario administrador:
        sqlite3 "%DB_PATH%" "SELECT id, nombre_usuario, estado_usuario, updated_at FROM usuarios WHERE nombre_usuario = 'admin';"
    ) else (
        echo Error: No se pudo activar el usuario administrador.
        exit /b 1
    )
) else (
    echo Operacion cancelada.
    exit /b 0
)

echo.
echo =====================================================
echo               Proceso completado
echo =====================================================

pause
