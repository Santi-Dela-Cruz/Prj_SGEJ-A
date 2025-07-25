@echo off
echo ==========================================
echo    VERIFICACION RAPIDA SGEJ-A
echo ==========================================

echo.
echo 1. Verificando estructura de archivos...
if exist "src\main\java\application\App.java" (
    echo    ✓ App.java encontrado
) else (
    echo    ✗ App.java NO encontrado
)

if exist "src\main\java\application\controllers\cliente\ModuloClienteController.java" (
    echo    ✓ ModuloClienteController.java encontrado
) else (
    echo    ✗ ModuloClienteController.java NO encontrado
)

if exist "src\main\java\application\controllers\cliente\FormClienteController.java" (
    echo    ✓ FormClienteController.java encontrado
) else (
    echo    ✗ FormClienteController.java NO encontrado
)

if exist "src\main\resources\views\cliente\form_cliente.fxml" (
    echo    ✓ form_cliente.fxml encontrado
) else (
    echo    ✗ form_cliente.fxml NO encontrado
)

if exist "src\main\resources\styles\app.css" (
    echo    ✓ app.css encontrado
) else (
    echo    ✗ app.css NO encontrado
)

echo.
echo 2. Verificando configuracion Maven...
if exist "pom.xml" (
    echo    ✓ pom.xml encontrado
) else (
    echo    ✗ pom.xml NO encontrado
)

if exist "target\classes" (
    echo    ✓ Directorio de clases compiladas existe
) else (
    echo    ⚠ Directorio de clases no existe - ejecutar 'mvn compile'
)

echo.
echo ==========================================
echo    INSTRUCCIONES DE EJECUCION
echo ==========================================
echo.
echo Para ejecutar la aplicacion:
echo    mvn javafx:run
echo.
echo Para ejecutar pruebas:
echo    mvn exec:java -Dexec.mainClass="application.test.TestControladores"
echo.
echo Para compilar solamente:
echo    mvn clean compile
echo.
echo ✓ Verificacion completada
echo.
pause
