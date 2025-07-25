@echo off
echo ==========================================
echo    LIMPIAR BASE DE DATOS SGEJ-A
echo ==========================================

echo.
echo ADVERTENCIA: Esto eliminara todos los datos existentes
echo Presiona cualquier tecla para continuar o Ctrl+C para cancelar
pause > nul

echo.
echo Eliminando base de datos existente...
if exist "target\database" (
    rmdir /s /q "target\database"
    echo    ✓ Base de datos eliminada
) else (
    echo    ⚠ No se encontro base de datos existente
)

echo.
echo Compilando el proyecto...
call mvn clean compile

echo.
echo Ejecutando la aplicacion (se creara nueva base de datos)...
call mvn javafx:run

echo.
echo Presiona cualquier tecla para salir...
pause > nul
