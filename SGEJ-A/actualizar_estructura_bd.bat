@echo off
echo ================================================
echo  ACTUALIZANDO ESTRUCTURA DE LA BASE DE DATOS
echo ================================================
echo.

echo Aplicando cambios a la tabla bitacora_caso...
sqlite3 sgej_database.db < scripts\actualizar_bd_bitacora.sql

echo.
echo Actualizacion completada.
echo.
pause
