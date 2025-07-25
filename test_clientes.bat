@echo off
echo Ejecutando prueba del módulo de clientes...
cd /d "d:\CHEEMYS\EPN\SEMESTRE IV\ISR\Proyecto\SGEJ-A\Prj_SGEJ-A\SGEJ-A"

echo Compilando clases de prueba...
javac -cp "D:\CHEEMYS\PATHS\sqlite-jdbc-3.45.0.0.jar;target/classes" -d target/classes src/main/java/application/test/*.java

echo Ejecutando prueba...
java -cp "target/classes;D:\CHEEMYS\PATHS\sqlite-jdbc-3.45.0.0.jar" application.test.TestClientes
pause
