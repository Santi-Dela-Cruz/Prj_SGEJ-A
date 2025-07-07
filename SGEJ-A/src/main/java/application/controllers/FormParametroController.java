package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FormParametroController {

    @FXML private Label lbl_Titulo;
    @FXML private TextField txt_Codigo;
    @FXML private TextField txt_Nombre;
    @FXML private TextArea txt_Descripcion;
    @FXML private TextField txt_Valor;
    @FXML private ComboBox<String> cmb_Tipo;
    @FXML private ComboBox<String> cmb_Categoria;
    @FXML private RadioButton rb_Activo;
    @FXML private RadioButton rb_Inactivo;
    @FXML private Button btn_Guardar;
    @FXML private Button btn_Cancelar;
    
    // Componentes para upload de archivos
    @FXML private VBox vbox_Valor;
    @FXML private VBox vbox_Upload;
    @FXML private Button btn_SeleccionarArchivo;
    @FXML private Label lbl_NombreArchivo;
    @FXML private VBox vbox_Preview;
    @FXML private ImageView img_Preview;

    private ModuloParametrosController moduloParametrosController;
    private ModuloParametrosController.ParametroDemo parametroActual;
    private String accionActual;
    private File archivoSeleccionado;
    private String rutaArchivoDestino;

    public void setModuloParametrosController(ModuloParametrosController controller) {
        this.moduloParametrosController = controller;
    }

    @FXML
    private void initialize() {
        inicializarComboTipo();
        inicializarComboCategorias();
        configurarBotones();
        configurarValidaciones();
        configurarUploadArchivos();
    }

    private void inicializarComboTipo() {
        cmb_Tipo.getItems().addAll("Texto", "Numérico", "Booleano", "Tiempo", "Fecha", "Email", "URL", "Archivo");
        cmb_Tipo.setValue("Texto");
        
        // Listener para mostrar/ocultar componentes de upload
        cmb_Tipo.valueProperty().addListener((observable, oldValue, newValue) -> {
            boolean esArchivo = "Archivo".equals(newValue);
            vbox_Upload.setVisible(esArchivo);
            vbox_Upload.setManaged(esArchivo);
            vbox_Valor.setVisible(!esArchivo);
            vbox_Valor.setManaged(!esArchivo);
        });
    }
    
    private void inicializarComboCategorias() {
        cmb_Categoria.getItems().addAll(
            "General", 
            "Sistema", 
            "Institucional", 
            "Legal/Fiscal", 
            "Contable", 
            "Seguridad"
        );
        cmb_Categoria.setValue("General");
    }

    private void configurarBotones() {
        btn_Guardar.setOnAction(e -> guardarParametro());
        btn_Cancelar.setOnAction(e -> cancelarOperacion());
    }

    private void configurarValidaciones() {
        // Validar que el código sea alfanumérico
        txt_Codigo.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("[A-Za-z0-9]*")) {
                txt_Codigo.setText(oldValue);
            }
        });

        // Validar campos numéricos según el tipo seleccionado
        cmb_Tipo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ("Numérico".equals(newValue)) {
                txt_Valor.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null && !newVal.matches("\\d*\\.?\\d*")) {
                        txt_Valor.setText(oldVal);
                    }
                });
            } else if ("Booleano".equals(newValue)) {
                txt_Valor.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null && !newVal.matches("true|false|")) {
                        txt_Valor.setText(oldVal);
                    }
                });
            }
        });
    }

    public void inicializarFormulario(ModuloParametrosController.ParametroDemo parametro, String accion) {
        this.parametroActual = parametro;
        this.accionActual = accion;

        if ("NUEVO".equals(accion)) {
            lbl_Titulo.setText("Nuevo Parámetro");
            limpiarFormulario();
        } else if ("EDITAR".equals(accion) && parametro != null) {
            lbl_Titulo.setText("Editar Parámetro");
            cargarDatosParametro(parametro);
        }
    }

    private void limpiarFormulario() {
        txt_Codigo.clear();
        txt_Nombre.clear();
        txt_Descripcion.clear();
        txt_Valor.clear();
        cmb_Tipo.setValue("Texto");
        rb_Activo.setSelected(true);
    }

    private void cargarDatosParametro(ModuloParametrosController.ParametroDemo parametro) {
        txt_Codigo.setText(parametro.getCodigo());
        txt_Nombre.setText(parametro.getNombre());
        txt_Descripcion.setText(parametro.getDescripcion());
        txt_Valor.setText(parametro.getValor());
        cmb_Tipo.setValue(parametro.getTipo());
        
        // Como eliminamos el campo Estado, siempre seleccionamos Activo por defecto
        rb_Activo.setSelected(true);

        // En modo edición, el código no se puede cambiar
        txt_Codigo.setEditable(false);
        txt_Codigo.setDisable(true);
    }

    private void guardarParametro() {
        if (!validarFormulario()) {
            return;
        }

        try {
            // Si es un archivo, copiarlo al destino
            if ("Archivo".equals(cmb_Tipo.getValue()) && archivoSeleccionado != null) {
                copiarArchivoADestino(archivoSeleccionado);
            }
            
            // Aquí se guardaría en la base de datos
            // Por ahora solo mostramos un mensaje de confirmación
            System.out.println("Guardando parámetro:");
            System.out.println("Código: " + txt_Codigo.getText());
            System.out.println("Nombre: " + txt_Nombre.getText());
            System.out.println("Descripción: " + txt_Descripcion.getText());
            System.out.println("Valor: " + txt_Valor.getText());
            System.out.println("Tipo: " + cmb_Tipo.getValue());
            System.out.println("Categoría: " + cmb_Categoria.getValue());
            System.out.println("Estado: " + (rb_Activo.isSelected() ? "Activo" : "Inactivo"));
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Éxito");
            alert.setHeaderText(null);
            alert.setContentText("El parámetro se guardó exitosamente");
            alert.showAndWait();

            moduloParametrosController.actualizarTabla();
            moduloParametrosController.cerrarFormulario();

        } catch (Exception e) {
            mostrarError("Error al guardar el parámetro: " + e.getMessage());
        }
    }

    private boolean validarFormulario() {
        StringBuilder errores = new StringBuilder();

        if (txt_Codigo.getText() == null || txt_Codigo.getText().trim().isEmpty()) {
            errores.append("- El código es obligatorio\n");
        }

        if (txt_Nombre.getText() == null || txt_Nombre.getText().trim().isEmpty()) {
            errores.append("- El nombre es obligatorio\n");
        }

        // Validar valor solo si no es tipo archivo
        if (!"Archivo".equals(cmb_Tipo.getValue())) {
            if (txt_Valor.getText() == null || txt_Valor.getText().trim().isEmpty()) {
                errores.append("- El valor es obligatorio\n");
            }
        } else {
            // Para archivos, validar que se haya seleccionado un archivo
            if (archivoSeleccionado == null) {
                errores.append("- Debe seleccionar un archivo\n");
            }
        }

        if (cmb_Tipo.getValue() == null) {
            errores.append("- El tipo es obligatorio\n");
        }

        // Validaciones específicas por tipo
        if (cmb_Tipo.getValue() != null && txt_Valor.getText() != null) {
            String tipo = cmb_Tipo.getValue();
            String valor = txt_Valor.getText().trim();

            if ("Numérico".equals(tipo) && !valor.matches("\\d+(\\.\\d+)?")) {
                errores.append("- El valor debe ser numérico\n");
            } else if ("Booleano".equals(tipo) && !valor.matches("true|false")) {
                errores.append("- El valor debe ser 'true' o 'false'\n");
            } else if ("Email".equals(tipo) && !valor.matches("^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$")) {
                errores.append("- El valor debe ser un email válido\n");
            } else if ("URL".equals(tipo) && !valor.matches("^https?://.*")) {
                errores.append("- El valor debe ser una URL válida\n");
            }
        }

        if (errores.length() > 0) {
            mostrarError("Por favor corrija los siguientes errores:\n\n" + errores.toString());
            return false;
        }

        return true;
    }

    private void cancelarOperacion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar");
        alert.setHeaderText("¿Desea cancelar la operación?");
        alert.setContentText("Se perderán todos los cambios realizados");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            limpiarComponentesUpload();
            moduloParametrosController.cerrarFormulario();
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void configurarUploadArchivos() {
        btn_SeleccionarArchivo.setOnAction(e -> seleccionarArchivo());
        btn_SeleccionarArchivo.getStyleClass().add("upload-button");
    }
    
    private void seleccionarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo");
        
        // Configurar filtros según el tipo de parámetro
        String categoria = cmb_Categoria.getValue();
        if ("Institucional".equals(categoria)) {
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("Documentos", "*.pdf", "*.doc", "*.docx"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
            );
        } else {
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*"),
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("Documentos", "*.pdf", "*.doc", "*.docx", "*.txt"),
                new FileChooser.ExtensionFilter("Archivos de configuración", "*.xml", "*.json", "*.properties")
            );
        }
        
        // Obtener la ventana padre
        Stage stage = (Stage) btn_SeleccionarArchivo.getScene().getWindow();
        File archivo = fileChooser.showOpenDialog(stage);
        
        if (archivo != null) {
            archivoSeleccionado = archivo;
            lbl_NombreArchivo.setText(archivo.getName());
            lbl_NombreArchivo.getStyleClass().add("file-selected-label");
            
            // Mostrar preview si es imagen
            if (esImagen(archivo)) {
                mostrarPreviewImagen(archivo);
            }
            
            // Establecer el valor del campo como la ruta del archivo
            txt_Valor.setText(archivo.getAbsolutePath());
        }
    }
    
    private boolean esImagen(File archivo) {
        String nombre = archivo.getName().toLowerCase();
        return nombre.endsWith(".png") || nombre.endsWith(".jpg") || 
               nombre.endsWith(".jpeg") || nombre.endsWith(".gif") || 
               nombre.endsWith(".bmp");
    }
    
    private void mostrarPreviewImagen(File archivo) {
        try {
            FileInputStream fis = new FileInputStream(archivo);
            Image image = new Image(fis);
            img_Preview.setImage(image);
            vbox_Preview.setVisible(true);
            vbox_Preview.setManaged(true);
            fis.close();
        } catch (IOException e) {
            System.err.println("Error al cargar la imagen: " + e.getMessage());
        }
    }
    
    private void copiarArchivoADestino(File archivoOrigen) throws IOException {
        if (archivoOrigen == null) return;
        
        // Crear directorio de destino si no existe
        String directorioDestino = "src/main/resources/uploads/";
        Path rutaDestino = Paths.get(directorioDestino);
        if (!Files.exists(rutaDestino)) {
            Files.createDirectories(rutaDestino);
        }
        
        // Generar nombre único para el archivo
        String nombreArchivo = txt_Codigo.getText() + "_" + archivoOrigen.getName();
        Path archivoDestino = rutaDestino.resolve(nombreArchivo);
        
        // Copiar archivo
        Files.copy(archivoOrigen.toPath(), archivoDestino, StandardCopyOption.REPLACE_EXISTING);
        
        // Actualizar la ruta de destino
        rutaArchivoDestino = archivoDestino.toString();
        txt_Valor.setText(rutaArchivoDestino);
    }
    
    private void limpiarComponentesUpload() {
        archivoSeleccionado = null;
        rutaArchivoDestino = null;
        lbl_NombreArchivo.setText("Ningún archivo seleccionado");
        lbl_NombreArchivo.getStyleClass().remove("file-selected-label");
        img_Preview.setImage(null);
        vbox_Preview.setVisible(false);
        vbox_Preview.setManaged(false);
    }
}
