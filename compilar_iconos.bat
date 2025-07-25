@echo off
echo Compilando componentes del proyecto...

rem Compilar IconUtil
javac -cp "target\classes;C:\Program Files\Java\javafx-sdk-22.0.1\lib\*;C:\Program Files\Java\sqlite-jdbc-3.45.0.0.jar" -d target\classes src\main\java\application\util\IconUtil.java

rem Compilar FormClienteController
javac -cp "target\classes;C:\Program Files\Java\javafx-sdk-22.0.1\lib\*;C:\Program Files\Java\sqlite-jdbc-3.45.0.0.jar" -d target\classes src\main\java\application\controllers\cliente\FormClienteController.java

rem Compilar ModuloClienteController
javac -cp "target\classes;C:\Program Files\Java\javafx-sdk-22.0.1\lib\*;C:\Program Files\Java\sqlite-jdbc-3.45.0.0.jar" -d target\classes src\main\java\application\controllers\cliente\ModuloClienteController.java

echo Compilación completada!
pause
