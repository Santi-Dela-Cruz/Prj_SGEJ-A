@echo off
echo Compilando proyecto completo...
cd /d "d:\CHEEMYS\EPN\SEMESTRE IV\ISR\Proyecto\SGEJ-A\Prj_SGEJ-A\SGEJ-A"

echo Descargando SQLite JDBC si no existe...
if not exist "D:\CHEEMYS\PATHS\sqlite-jdbc-3.45.0.0.jar" (
    echo No se encontró sqlite-jdbc-3.45.0.0.jar en D:\CHEEMYS\PATHS\
    echo Por favor descarga el archivo desde: https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.0.0/sqlite-jdbc-3.45.0.0.jar
    echo y colócalo en D:\CHEEMYS\PATHS\
    pause
    exit /b 1
)

echo Compilando código fuente...
javac -cp "D:\CHEEMYS\PATHS\JavaFX\javafx-sdk-24.0.1\lib\*;D:\CHEEMYS\PATHS\sqlite-jdbc-3.45.0.0.jar" -d target/classes src/main/java/application/*.java src/main/java/application/model/*.java src/main/java/application/database/*.java src/main/java/application/dao/*.java src/main/java/application/service/*.java src/main/java/application/controllers/*.java src/main/java/application/controllers/cliente/*.java src/main/java/application/test/*.java

if %errorlevel% neq 0 (
    echo Error en la compilación
    pause
    exit /b 1
)

echo Copiando recursos...
xcopy /E /Y /Q src\main\resources\* target\classes\

echo Compilación exitosa!
echo.
echo Puedes ejecutar:
echo   run.bat - Para ejecutar la aplicación
echo   test_clientes.bat - Para ejecutar las pruebas
echo.
pause
