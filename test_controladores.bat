@echo off
echo Ejecutando prueba de controladores...
cd /d "d:\CHEEMYS\EPN\SEMESTRE IV\ISR\Proyecto\SGEJ-A\Prj_SGEJ-A\SGEJ-A"

echo Compilando clases de prueba...
javac -cp "D:\CHEEMYS\PATHS\sqlite-jdbc-3.45.0.0.jar;target/classes" -d target/classes src/main/java/application/test/TestControladores.java

if %errorlevel% neq 0 (
    echo Error en la compilación de pruebas
    pause
    exit /b 1
)

echo Ejecutando prueba de controladores...
java -cp "target/classes;D:\CHEEMYS\PATHS\sqlite-jdbc-3.45.0.0.jar" application.test.TestControladores
pause
