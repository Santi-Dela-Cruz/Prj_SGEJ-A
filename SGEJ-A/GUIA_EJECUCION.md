# 🚀 GUÍA PARA EJECUTAR EL SISTEMA SGEJ-A

## ⚙️ Configuración Previa

### 1. **Instalar Maven** (si no está instalado)
- Descargar desde: https://maven.apache.org/download.cgi
- Agregar al PATH de Windows: `C:\apache-maven-3.x.x\bin`

### 2. **Verificar Java y JavaFX**
```bash
java --version
```

## 🎯 Métodos para Ejecutar el Sistema

### **Método 1: Con Maven (RECOMENDADO)**
```bash
# Navegar al directorio del proyecto
cd "d:\CHEEMYS\EPN\SEMESTRE IV\ISR\Proyecto\SGEJ-A\Prj_SGEJ-A\SGEJ-A"

# Compilar el proyecto
mvn clean compile

# Ejecutar la aplicación
mvn javafx:run
```

### **Método 2: Usando VS Code**
1. Abrir el proyecto en VS Code
2. Presionar `Ctrl+Shift+P`
3. Buscar: "Java: Run Java"
4. Seleccionar `App.java`

### **Método 3: Con archivos batch**
```bash
# Ejecutar aplicación
.\ejecutar_sgej.bat

# Ejecutar pruebas
.\ejecutar_pruebas.bat
```

### **Método 4: Ejecución manual con JavaFX**
```bash
java --module-path "C:\path\to\javafx\lib" --add-modules javafx.controls,javafx.graphics,javafx.base,javafx.fxml -cp target\classes application.App
```

## 🧪 Ejecutar Pruebas

### **¿Para qué sirven las pruebas?**
Las pruebas verifican que el sistema funcione correctamente y detectan errores antes de que los usuarios finales los encuentren.

### **Pruebas de Controladores**
```bash
mvn exec:java -Dexec.mainClass="application.test.TestControladores"
```
**Propósito**: Verificar que los controladores se inicialicen correctamente
- ✅ FormClienteController se crea sin errores
- ✅ ModuloClienteController se crea sin errores
- ✅ ClienteService funciona correctamente
- ✅ Base de datos se conecta sin problemas

### **Pruebas de Guardado de Clientes**
```bash
mvn exec:java -Dexec.mainClass="application.test.TestGuardarCliente"
```
**Propósito**: Verificar que los clientes se guarden correctamente en la base de datos
- ✅ Muestra clientes existentes
- ✅ Crea un cliente de prueba
- ✅ Verifica que se guardó correctamente
- ✅ Confirma que se puede recuperar de la base de datos

### **Pruebas Unitarias**
```bash
mvn test
```
**Propósito**: Ejecutar pruebas automáticas de unidades individuales del código
- ✅ Valida funciones específicas
- ✅ Detecta regresiones (errores nuevos)
- ✅ Asegura calidad del código

### **Verificación del Sistema**
```bash
# Windows
.\verificar_sistema.bat

# Linux/Mac
./test_rapido.sh
```
**Propósito**: Verificar que todos los archivos necesarios estén presentes
- ✅ Estructura de directorios correcta
- ✅ Archivos FXML y CSS en su lugar
- ✅ Configuración Maven válida
- ✅ Compilación sin errores

### **¿Cuándo ejecutar las pruebas?**
- **Antes de ejecutar la aplicación**: Para asegurar que todo esté funcionando
- **Después de hacer cambios**: Para verificar que no se rompió nada
- **Antes de entregar**: Para garantizar calidad del producto
- **Cuando hay errores**: Para diagnosticar problemas específicos

## 🔧 Solución de Problemas

### **Error: "JavaFX runtime components are missing"**
- Asegurarse de que JavaFX esté en el classpath
- Usar el comando `mvn javafx:run` en lugar de `java` directamente

### **Error: "mvn command not found"**
- Instalar Maven y agregarlo al PATH de Windows
- Reiniciar la terminal después de instalar Maven

### **Error: "Cannot find module"**
- Ejecutar `mvn clean compile` antes de ejecutar la aplicación
- Verificar que todas las dependencias estén correctas en `pom.xml`

### **Warning: "sun.misc.Unsafe::allocateMemory will be removed"**
- Este es un warning normal en Java 24 con JavaFX
- No afecta la funcionalidad del sistema
- Se puede ignorar de manera segura

### **El formulario no se cierra después de guardar/cancelar**
- Los callbacks ahora están implementados correctamente
- El formulario se cerrará automáticamente al guardar o cancelar
- Si persiste el problema, revisar los logs de consola

### **Los nuevos registros no aparecen en la tabla**
- El sistema refrescará automáticamente la tabla después de guardar
- Para verificar manualmente: `mvn exec:java -Dexec.mainClass="application.test.TestGuardarCliente"`
- Revisar que no haya errores de base de datos en la consola

### **Error: "DateTimeParseException" al cargar clientes**
- Problema de formato de fecha en la base de datos
- Ya está corregido en la versión actual
- Si persiste, eliminar la carpeta `target/database/` y reiniciar la aplicación

### **El formulario no se abre al hacer clic en "Nuevo"**
- Verificar que el panel de formularios esté visible
- Revisar los logs de consola para errores específicos
- Si persiste, el formulario se abrirá en una ventana nueva

## 📁 Estructura del Proyecto

```
SGEJ-A/
├── src/main/java/application/
│   ├── App.java                    # Clase principal
│   ├── controllers/                # Controladores
│   ├── model/                      # Modelos de datos
│   ├── service/                    # Servicios de negocio
│   ├── database/                   # Conexión a BD
│   └── test/                       # Pruebas
├── src/main/resources/
│   ├── views/                      # Archivos FXML
│   ├── styles/                     # Archivos CSS
│   └── icons/                      # Iconos
├── pom.xml                         # Configuración Maven
└── ejecutar_sgej.bat              # Script de ejecución
```

## 🎮 Uso del Sistema

1. **Login**: Pantalla inicial del sistema
2. **Dashboard**: Panel principal con módulos
3. **Módulo Cliente**: Gestión de clientes
4. **Formularios**: Crear, editar y visualizar registros
5. **Búsqueda**: Filtrar y buscar información

## 🔐 Credenciales de Prueba

- **Usuario**: admin
- **Contraseña**: admin123

## 📊 Base de Datos

- **Tipo**: SQLite
- **Ubicación**: Se crea automáticamente en `target/database/`
- **Inicialización**: Automática al ejecutar la aplicación

### **Resolución de Problemas de Fechas**

Si encuentras errores con fechas como:
```
DateTimeParseException: Text '2025-07-16T01:35:03.768674100' could not be parsed
```

**Solución**: El sistema ahora maneja automáticamente los diferentes formatos de fecha que SQLite puede generar:
- Formato ISO con microsegundos: `2025-07-16T01:35:03.768674100`
- Formato simple: `2025-07-16 01:35:03`

**Si persisten problemas**, usa el script de limpieza:
```bash
.\limpiar_bd.bat
```

## 🛠️ Tecnologías Utilizadas

- **JavaFX 24**: Interfaz gráfica
- **SQLite**: Base de datos
- **Maven**: Gestión de dependencias
- **CSS**: Estilos personalizados
- **FXML**: Diseño de interfaces
