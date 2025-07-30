@echo off
echo -----------------------------------------------------
echo  LIMPIEZA DE DATOS - MANTENER SOLO USUARIOS
echo -----------------------------------------------------
echo.
echo Esta operacion eliminara TODOS los datos excepto los usuarios.
echo.
echo Se eliminaran:
echo - Clientes
echo - Casos
echo - Documentos
echo - Audiencias
echo - Facturas
echo - Comunicaciones
echo - Empleados
echo - Bitacora
echo - Parametros
echo.
echo El sistema seguira funcionando con los usuarios actuales.
echo.

set /p CONFIRMAR="¿Esta seguro de continuar? (S/N): "
if /i "%CONFIRMAR%" NEQ "S" (
    echo Operacion cancelada.
    goto :EOF
)

echo.
echo Ejecutando limpieza de datos...

java -cp target/classes;target/dependency/* application.util.LimpiarBD

if %ERRORLEVEL% NEQ 0 (
    echo Error al limpiar la base de datos.
) else (
    echo.
    echo ¡Base de datos limpiada exitosamente!
    echo Solo se conservaron los usuarios.
)

echo.
pause
