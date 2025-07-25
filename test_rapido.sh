#!/bin/bash

# Script de pruebas rápidas para SGEJ-A
echo "🚀 ===== PRUEBAS RÁPIDAS SGEJ-A ====="

echo "1. Verificando compilación..."
javac -cp "target/classes:target/dependency/*" src/main/java/application/App.java

if [ $? -eq 0 ]; then
    echo "   ✅ Compilación exitosa"
else
    echo "   ❌ Error en compilación"
    exit 1
fi

echo "2. Verificando estructura de archivos..."
if [ -f "src/main/java/application/App.java" ]; then
    echo "   ✅ App.java encontrado"
fi

if [ -f "src/main/java/application/controllers/cliente/ModuloClienteController.java" ]; then
    echo "   ✅ ModuloClienteController.java encontrado"
fi

if [ -f "src/main/java/application/controllers/cliente/FormClienteController.java" ]; then
    echo "   ✅ FormClienteController.java encontrado"
fi

if [ -f "src/main/resources/views/cliente/form_cliente.fxml" ]; then
    echo "   ✅ form_cliente.fxml encontrado"
fi

if [ -f "src/main/resources/styles/app.css" ]; then
    echo "   ✅ app.css encontrado"
fi

echo "3. Verificando base de datos..."
if [ -d "target/database" ]; then
    echo "   ✅ Directorio de base de datos existe"
else
    echo "   ⚠️  Directorio de base de datos se creará al ejecutar"
fi

echo ""
echo "🎯 ===== INSTRUCCIONES DE EJECUCIÓN ====="
echo "Para ejecutar la aplicación:"
echo "   mvn javafx:run"
echo ""
echo "Para ejecutar pruebas:"
echo "   mvn exec:java -Dexec.mainClass=\"application.test.TestControladores\""
echo ""
echo "Para compilar solamente:"
echo "   mvn clean compile"
echo ""
echo "✅ Todas las pruebas rápidas completadas exitosamente!"
