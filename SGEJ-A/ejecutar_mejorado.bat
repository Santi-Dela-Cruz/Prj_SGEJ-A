@echo off
echo ====================================
echo 🚀 EJECUTANDO SISTEMA JURIDICO
echo ====================================
echo.

echo 📋 Compilando todas las clases...
javac -cp "target\classes;C:\Program Files\Java\javafx-sdk-22.0.1\lib\*;C:\Program Files\Java\sqlite-jdbc-3.45.0.0.jar" -d target\classes src\main\java\application\*.java src\main\java\application\model\*.java src\main\java\application\dao\*.java src\main\java\application\service\*.java src\main\java\application\controllers\*.java src\main\java\application\controllers\cliente\*.java

echo.
echo 📋 Copiando recursos...
xcopy "src\main\resources\*" "target\classes\" /E /Y /I > nul 2>&1

echo.
echo 🚀 Iniciando aplicación...
echo.
java --module-path "C:\Program Files\Java\javafx-sdk-22.0.1\lib" --add-modules javafx.controls,javafx.fxml -cp "target\classes;C:\Program Files\Java\sqlite-jdbc-3.45.0.0.jar" application.App

echo.
echo 📋 Aplicación finalizada.
pause
