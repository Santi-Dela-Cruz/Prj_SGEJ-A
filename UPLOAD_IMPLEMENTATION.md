# Módulo de Parámetros del Sistema - Funcionalidad de Upload de Archivos

## Resumen de la Implementación

Se ha implementado exitosamente la funcionalidad de upload de archivos en el módulo de parámetros del sistema JavaFX, agregando las siguientes características:

## ✅ Funcionalidades Implementadas

### 1. **Componentes de Upload de Archivos**
- **Botón de selección de archivos**: Permite al usuario seleccionar archivos desde su sistema
- **Vista previa de imágenes**: Muestra automáticamente una vista previa cuando se selecciona una imagen
- **Etiqueta de nombre de archivo**: Muestra el nombre del archivo seleccionado
- **Validación de tipos de archivo**: Filtros personalizados según la categoría del parámetro

### 2. **Categorías de Parámetros**
- **General**: Parámetros básicos del sistema
- **Sistema**: Configuraciones técnicas (timeout, tamaños de archivo, etc.)
- **Institucional**: Información de la institución (logos, nombres, direcciones)
- **Legal/Fiscal**: Datos legales y fiscales (RUC, dirección fiscal, períodos)
- **Contable**: Parámetros contables (períodos fiscales, monedas)
- **Seguridad**: Configuraciones de seguridad (políticas de contraseñas, intentos de login)

### 3. **Filtros y Búsqueda**
- **Filtro por categoría**: ComboBox para filtrar parámetros por categoría
- **Búsqueda por texto**: Campo de búsqueda que filtra por código, nombre, descripción o valor
- **Botón de refrescar**: Actualiza la tabla con todos los parámetros

### 4. **Gestión de Archivos**
- **Directorio de upload**: `src/main/resources/uploads/`
- **Nomenclatura automática**: Los archivos se renombran como `{codigo_parametro}_{nombre_original}`
- **Soporte para múltiples formatos**: Imágenes, documentos, archivos de configuración
- **Copia segura**: Los archivos se copian al directorio de destino sin sobrescribir los originales

## 🎨 Mejoras en la Interfaz

### 1. **Estilos CSS Agregados**
```css
/* Componentes de upload */
.file-upload-container { /* Contenedor con borde punteado */ }
.image-preview { /* Vista previa de imágenes */ }
.file-selected-label { /* Etiqueta de archivo seleccionado */ }
.upload-button { /* Botón de upload personalizado */ }
```

### 2. **Columnas de la Tabla**
- **Código**: Identificador único del parámetro
- **Categoría**: Categoría del parámetro (nuevo)
- **Nombre**: Nombre descriptivo
- **Descripción**: Descripción detallada
- **Valor**: Valor del parámetro (puede ser ruta de archivo)
- **Tipo**: Tipo de dato (incluye "Archivo" como nuevo tipo)
- **Estado**: Activo/Inactivo
- **Acciones**: Botones de editar y eliminar

## 🔧 Archivos Modificados

### 1. **Vista Principal** (`modulo_parametros.fxml`)
- Agregado ComboBox para categorías
- Redimensionadas las columnas para incluir categoría
- Actualizado el import de ComboBox

### 2. **Formulario de Parámetros** (`form_parametro.fxml`)
- Agregado ComboBox para categorías
- Agregada sección de upload de archivos (inicialmente oculta)
- Agregado ImageView para vista previa
- Agregados botones y etiquetas para selección de archivos

### 3. **Controlador Principal** (`ModuloParametrosController.java`)
- Agregado ComboBox de categorías con listener
- Implementado filtro por categoría
- Mejorada la búsqueda de parámetros
- Actualizada la clase ParametroDemo para incluir categoría
- Agregados datos de ejemplo con diferentes categorías

### 4. **Controlador de Formulario** (`FormParametroController.java`)
- Agregados componentes FXML para upload
- Implementada funcionalidad de selección de archivos
- Agregada vista previa de imágenes
- Implementada validación de archivos
- Agregada lógica de copia de archivos
- Integrada limpieza de componentes al cancelar

### 5. **Estilos CSS** (`app.css`)
- Agregados estilos para componentes de upload
- Estilos para vista previa de imágenes
- Estilos para botones de upload
- Estilos para etiquetas de archivos

## 📂 Estructura de Directorios

```
src/main/resources/
├── uploads/
│   ├── README.md
│   ├── sistema_config.properties
│   └── [archivos subidos por usuarios]
├── views/sistema/
│   ├── modulo_parametros.fxml
│   └── form_parametro.fxml
└── styles/
    └── app.css
```

## 🚀 Funcionalidades Avanzadas Preparadas

### 1. **Parámetros Institucionales**
- Upload de logotipos con vista previa
- Gestión de documentos institucionales
- Soporte para múltiples formatos de imagen

### 2. **Parámetros Legales/Fiscales**
- Upload de documentos legales
- Gestión de certificados
- Archivos de configuración fiscal

### 3. **Configuraciones de Seguridad**
- Archivos de políticas de seguridad
- Certificados digitales
- Configuraciones de acceso

## 🎯 Uso de la Funcionalidad

### Para Parámetros de Tipo "Archivo":
1. Seleccionar "Archivo" en el ComboBox de Tipo
2. Aparecerá automáticamente la sección de upload
3. Hacer clic en "Seleccionar Archivo"
4. Elegir el archivo desde el sistema
5. Ver la vista previa (si es imagen)
6. Guardar el parámetro

### Para Filtrar Parámetros:
1. Usar el ComboBox de categorías para filtrar
2. Usar el campo de búsqueda para texto específico
3. Hacer clic en "Refrescar" para ver todos los parámetros

## 📋 Datos de Ejemplo Incluidos

Se han agregado parámetros de ejemplo para cada categoría:
- **Sistema**: Timeout, tamaños de archivo, configuración de email
- **Institucional**: Logo principal, nombre de institución
- **Legal/Fiscal**: RUC, dirección fiscal
- **Contable**: Período fiscal
- **Seguridad**: Políticas de contraseñas

## 🔮 Extensiones Futuras

La arquitectura implementada permite fácilmente:
- Conectar con base de datos real
- Agregar validaciones específicas por tipo de archivo
- Implementar versionado de archivos
- Agregar más tipos de vista previa (PDF, documentos)
- Implementar compresión de imágenes
- Agregar logs de cambios en parámetros

## ✅ Estado del Proyecto

**COMPLETADO**: La funcionalidad de upload de archivos está completamente implementada y lista para uso. El sistema puede manejar archivos de diferentes tipos, proporciona vista previa para imágenes, y mantiene una estructura organizada para la gestión de parámetros categorizados.

La implementación es robusta, escalable y sigue las mejores prácticas de JavaFX para interfaces de usuario modernas.
