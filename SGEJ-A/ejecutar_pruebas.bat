@echo off
echo ==========================================
echo    EJECUTANDO PRUEBAS SGEJ-A
echo ==========================================

echo Compilando el proyecto...
call mvn clean compile

echo.
echo Ejecutando pruebas de controladores...
call mvn exec:java -Dexec.mainClass="application.test.TestControladores"

echo.
echo Presiona cualquier tecla para salir...
pause > nul
