@echo off
echo ==========================================
echo    EJECUTANDO SISTEMA SGEJ-A
echo ==========================================

echo Compilando el proyecto...
call mvn clean compile

echo.
echo Ejecutando la aplicacion JavaFX...
call mvn javafx:run

echo.
echo Presiona cualquier tecla para salir...
pause > nul
