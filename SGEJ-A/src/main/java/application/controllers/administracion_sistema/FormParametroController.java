package application.controllers.administracion_sistema;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.function.UnaryOperator;

import application.dao.ParametroDAO;
import application.model.Parametro;
import application.controllers.DialogUtil;

public class FormParametroController {

    @FXML
    private StackPane pnl_Title;
    @FXML
    private ToggleGroup toggleGroupEstado;
    @FXML
    private Label lbl_Titulo;
    @FXML
    private Label lbl_Error;
    @FXML
    private TextField txt_Codigo; // Volvemos a TextField
    @FXML
    private TextField txt_Nombre;
    @FXML
    private TextArea txt_Descripcion;
    @FXML
    private TextField txt_Valor;
    @FXML
    private ComboBox<String> cmb_Tipo;
    @FXML
    private ComboBox<String> cmb_Categoria;
    @FXML
    private RadioButton rb_Activo;
    @FXML
    private RadioButton rb_Inactivo;
    @FXML
    private Button btn_Guardar;
    @FXML
    private Button btn_Cancelar;

    // Componentes para upload de archivos
    @FXML
    private VBox vbox_Valor;
    @FXML
    private VBox vbox_Upload;
    @FXML
    private Button btn_SeleccionarArchivo;
    @FXML
    private Label lbl_NombreArchivo;
    @FXML
    private VBox vbox_Preview;
    @FXML
    private ImageView img_Preview;

    private ModuloParametrosController moduloParametrosController;
    private Parametro parametroActual;
    private String accionActual;
    private File archivoSeleccionado;
    private String rutaArchivoDestino;

    // Variable para almacenar el código original del parámetro como respaldo
    private String codigoOriginal;

    // Callbacks para los botones
    private Runnable onGuardarCallback;
    private Runnable onCancelarCallback;

    public void setModuloParametrosController(ModuloParametrosController controller) {
        this.moduloParametrosController = controller;
    }

    @FXML
    private void initialize() {
        System.out.println("Inicializando FormParametroController");

        // Configurar campo de código para que siempre muestre el texto correctamente
        configurarCampoCodigo();

        inicializarComboTipo();
        inicializarComboCategorias();
        configurarBotones();
        configurarValidaciones();
        configurarUploadArchivos();

        // Verificar visibilidad del campo de código después de un breve retraso
        javafx.application.Platform.runLater(() -> {
            // Ejecutar después de que todo esté inicializado
            System.out.println("⭐ VERIFICACIÓN FINAL DE INICIALIZACIÓN:");
            System.out.println("   - Campo código visible: " + txt_Codigo.isVisible());
            System.out.println("   - Campo código habilitado: " + !txt_Codigo.isDisable());
            System.out.println("   - Campo código editable: " + txt_Codigo.isEditable());
            System.out.println("   - Valor actual: [" + txt_Codigo.getText() + "]");

            // Mostrar en título para referencia
            if (parametroActual != null && parametroActual.getCodigo() != null) {
                lbl_Titulo.setText("Editar Parámetro: " + parametroActual.getCodigo());
            }
        });
    }

    /**
     * Configura el campo de código para asegurar su visibilidad y restaurar el
     * valor
     * si se borra accidentalmente
     */
    private void configurarCampoCodigo() {
        // Asegurarse de que el campo de código esté correctamente configurado
        txt_Codigo.setStyle("-fx-font-weight: bold; -fx-text-fill: #0000CC; -fx-background-color: #E8F5FE;");

        // Agregar un listener para depuración y restauración
        txt_Codigo.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("⚠️ CAMPO CÓDIGO CAMBIÓ: [" + oldValue + "] -> [" + newValue + "]");

            // Si estamos en modo EDITAR y el campo se vacía, restaurar el código original
            if ("EDITAR".equals(accionActual) && (newValue == null || newValue.isEmpty()) &&
                    codigoOriginal != null && !codigoOriginal.isEmpty()) {

                System.out.println("⚠️⚠️⚠️ RESTAURANDO CÓDIGO ORIGINAL: [" + codigoOriginal + "]");

                // Restaurar el valor original sin invocar este listener nuevamente
                javafx.application.Platform.runLater(() -> {
                    txt_Codigo.setText(codigoOriginal);

                    // Forzar la visibilidad y configuración correcta
                    txt_Codigo.setEditable(false);
                    txt_Codigo.setDisable(false);
                    txt_Codigo
                            .setStyle("-fx-font-weight: bold; -fx-text-fill: #0000CC; -fx-background-color: #E8F5FE;");

                    // Mostrar respaldo en el label
                    lbl_Error.setText("CÓDIGO RESTAURADO: " + codigoOriginal);
                    lbl_Error.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
                    lbl_Error.setVisible(true);
                });
            }
        });

        System.out.println("Campo de código configurado para visualización óptima y auto-restauración");
    }

    private void inicializarComboTipo() {
        cmb_Tipo.getItems().clear();
        cmb_Tipo.getItems().addAll("TEXTO", "NUMERICO", "TIEMPO", "PORCENTAJE");
        cmb_Tipo.setValue("TEXTO");

        // Listener para mostrar/ocultar componentes de upload (si decides usar
        // archivos)
        cmb_Tipo.valueProperty().addListener((observable, oldValue, newValue) -> {
            boolean esArchivo = "ARCHIVO".equalsIgnoreCase(newValue);
            boolean esPorcentaje = "PORCENTAJE".equalsIgnoreCase(newValue);

            // Configurar visibilidad y manejo de componentes
            vbox_Upload.setVisible(esArchivo);
            vbox_Upload.setManaged(esArchivo);
            vbox_Valor.setVisible(!esArchivo);
            vbox_Valor.setManaged(!esArchivo);

            // Configurar el campo de valor según el tipo
            if (esPorcentaje) {
                configurarCampoPorcentaje();
            } else {
                // Eliminar formatter personalizado si existe
                txt_Valor.setTextFormatter(null);
            }
        });
    }

    private void inicializarComboCategorias() {
        cmb_Categoria.getItems().clear();
        cmb_Categoria.getItems().addAll(
                "General",
                "Seguridad",
                "Legal/Fiscal",
                "Notificaciones",
                "Sistema");
        cmb_Categoria.setValue("General");
    }

    /**
     * Configura el campo de valor para mostrar y editar porcentajes
     * Formatea el valor como porcentaje y permite ingresar valores entre 0-100%
     */
    private void configurarCampoPorcentaje() {
        DecimalFormat percentFormat = new DecimalFormat("#0.##");

        // Crear un filtro para solo permitir números y punto decimal
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            // Permite dígitos, punto decimal y asegura formato válido de número
            if (newText.matches("^\\d*\\.?\\d*$")) {
                try {
                    // Si está vacío, permitir (para poder borrar)
                    if (newText.isEmpty()) {
                        return change;
                    }

                    // Convertir a número y validar rango (0-100)
                    double valor = Double.parseDouble(newText);
                    if (valor >= 0 && valor <= 100) {
                        return change;
                    }
                } catch (NumberFormatException e) {
                    // Si no es un número válido, rechazar
                    return null;
                }
            }
            // Rechazar otros caracteres
            return null;
        };

        // Conversor para formatear como porcentaje
        StringConverter<Double> converter = new StringConverter<>() {
            @Override
            public String toString(Double value) {
                if (value == null) {
                    return "";
                }
                return percentFormat.format(value) + "%";
            }

            @Override
            public Double fromString(String string) {
                if (string == null || string.isEmpty() || string.equals("%")) {
                    return 0.0;
                }
                // Quitar el símbolo de porcentaje si existe
                String cleaned = string.replace("%", "").trim();
                try {
                    return percentFormat.parse(cleaned).doubleValue();
                } catch (ParseException e) {
                    return 0.0;
                }
            }
        };

        // Aplicar el TextFormatter al campo de valor
        TextFormatter<Double> textFormatter = new TextFormatter<>(converter, 0.0, filter);
        txt_Valor.setTextFormatter(textFormatter);
        txt_Valor.setPromptText("Ingrese un porcentaje (0-100%)");
    }

    private void configurarBotones() {
        btn_Guardar.setOnAction(e -> guardarParametro());
        btn_Cancelar.setOnAction(e -> cancelarOperacion());
    }

    /**
     * Establece la acción a ejecutar cuando se guarda el parámetro
     */
    public void setOnGuardar(Runnable callback) {
        this.onGuardarCallback = callback;
    }

    /**
     * Establece la acción a ejecutar cuando se cancela la operación
     */
    public void setOnCancelar(Runnable callback) {
        this.onCancelarCallback = callback;
    }

    /**
     * Establece la acción a ejecutar cuando se actualiza el parámetro
     */
    public void setActualizarCallback(Runnable callback) {
        this.onGuardarCallback = callback;
    }

    /**
     * Establece el parámetro a editar
     */
    public void setParametro(Parametro parametro) {
        this.parametroActual = parametro;

        if (parametro != null) {
            // Configuración simple - sin estilos ni depuraciones excesivas
            lbl_Titulo.setText("Editar Parámetro");

            // Cargar los datos del parámetro
            cargarDatosParametro(parametro);

            // Verificar que el código se haya establecido correctamente
            System.out.println("Código del parámetro establecido: " + txt_Codigo.getText());
        } else {
            lbl_Titulo.setText("Nuevo Parámetro");
            limpiarFormulario();
        }
    }

    private void configurarValidaciones() {
        // Validar que el código sea alfanumérico - PERO SIN BORRAR SI ES MODO EDITAR
        txt_Codigo.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("⚠️ LISTENER CÓDIGO DETECTÓ CAMBIO: [" + oldValue + "] -> [" + newValue + "]");

            // Solo validamos si estamos en modo NUEVO, no en EDITAR
            if ("NUEVO".equals(accionActual)) {
                if (newValue != null && !newValue.matches("[A-Za-z0-9_]*")) {
                    System.out.println("⚠️ CORRIGIENDO CÓDIGO INVÁLIDO");
                    txt_Codigo.setText(oldValue);
                }
            } else if ("EDITAR".equals(accionActual) && (newValue == null || newValue.isEmpty()) && oldValue != null
                    && !oldValue.isEmpty()) {
                // Evitar que se borre el código en modo edición
                System.out.println("⚠️⚠️⚠️ DETECTADO INTENTO DE BORRAR CÓDIGO EN MODO EDITAR - RESTAURANDO");
                txt_Codigo.setText(oldValue);
            }
        });

        // Validar campos numéricos según el tipo seleccionado
        cmb_Tipo.valueProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("⚠️ LISTENER TIPO DETECTÓ CAMBIO: [" + oldValue + "] -> [" + newValue + "]");
            System.out.println("⚠️ CÓDIGO ACTUAL EN CAMBIO DE TIPO: [" + txt_Codigo.getText() + "]");

            // Primero reiniciamos los listeners del valor
            txt_Valor.textProperty().removeListener((obs, oldVal, newVal) -> {
            });

            if ("NUMERICO".equalsIgnoreCase(newValue)) {
                txt_Valor.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null && !newVal.matches("\\d*\\.?\\d*")) {
                        txt_Valor.setText(oldVal);
                    }
                });
            }
        });
    }

    public void inicializarFormulario(Parametro parametro, String accion) {
        this.parametroActual = parametro;
        this.accionActual = accion;

        if ("NUEVO".equals(accion)) {
            lbl_Titulo.setText("Nuevo Parámetro");
            limpiarFormulario();
        } else if ("EDITAR".equals(accion) && parametro != null) {
            lbl_Titulo.setText("Editar Parámetro");

            // Asignar el código directamente aquí también como respaldo
            if (parametro.getCodigo() != null) {
                System.out.println("PRE-ASIGNANDO CÓDIGO: [" + parametro.getCodigo() + "]");
                txt_Codigo.setText(parametro.getCodigo());
                txt_Codigo.setEditable(false);
                lbl_Error.setText("Código: " + parametro.getCodigo());
                lbl_Error.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
                lbl_Error.setVisible(true);
            }

            // Luego cargar todos los datos
            cargarDatosParametro(parametro);

            // Verificar que el código se haya establecido
            System.out.println("VERIFICACIÓN POST-CARGA: Código en campo = [" + txt_Codigo.getText() + "]");
        }
    }

    private void limpiarFormulario() {
        txt_Codigo.clear();
        txt_Nombre.clear();
        txt_Descripcion.clear();
        txt_Valor.clear();
        cmb_Tipo.setValue("TEXTO");
        rb_Activo.setSelected(true);

        // Configuración especial para el campo de código
        txt_Codigo.setEditable(true);
        txt_Codigo.setDisable(false);
        txt_Codigo.setStyle("-fx-background-color: white; -fx-text-fill: black;");

        // Limpiar label de error
        lbl_Error.setText("");
        lbl_Error.setVisible(false);

        limpiarComponentesUpload();
    }

    private void cargarDatosParametro(Parametro parametro) {
        if (parametro == null) {
            System.err.println("ERROR: ¡El parámetro es NULL en cargarDatosParametro!");
            return;
        }

        // Depuración completa del parámetro
        debugParametro("ANTES DE ASIGNAR", parametro);

        // Asignar el código primero y verificar que no sea nulo
        final String codigo = parametro.getCodigo();
        if (codigo == null || codigo.isEmpty()) {
            System.err.println("ERROR: ¡El código del parámetro es NULL o vacío!");
            lbl_Error.setText("ERROR: El código del parámetro es nulo o vacío");
            lbl_Error.setVisible(true);
            return;
        }

        // Guardar el código original para restaurarlo si es necesario
        this.codigoOriginal = codigo;

        // No borrar el campo antes de asignar el nuevo valor
        System.out.println("⚠️⚠️⚠️ ASIGNANDO CÓDIGO: [" + codigo + "] ⚠️⚠️⚠️");
        txt_Codigo.setText(codigo);

        // Verificación inmediata
        System.out.println("⚠️⚠️⚠️ VERIFICACIÓN DIRECTA: [" + txt_Codigo.getText() + "] ⚠️⚠️⚠️");

        // Usar Platform.runLater para garantizar que UI se actualice correctamente
        javafx.application.Platform.runLater(() -> {
            // Configurar TextField sin usar clear() que podría estar causando problemas
            txt_Codigo.setEditable(false);
            txt_Codigo.setDisable(false);
            // txt_Codigo.setStyle("-fx-font-weight: bold; -fx-text-fill: #0000CC;
            // -fx-background-color: #E8F5FE;");

            // Asignar el código después de configurar el campo
            txt_Codigo.setText(codigo);

            // Verificación inmediata después de asignar en runLater
            System.out.println("🔍 VERIFICACIÓN EN RUNLATER: [" + txt_Codigo.getText() + "]");

            // Actualizar el título para mostrar el código
            lbl_Titulo.setText("Editar Parámetro: " + codigo);

            // Respaldo visible siempre
            // lbl_Error.setText("CÓDIGO: " + codigo);
            // lbl_Error.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
            // lbl_Error.setVisible(true);
        });

        // Resto de campos
        txt_Nombre.setText(parametro.getNombre());
        txt_Descripcion.setText(parametro.getDescripcion());
        txt_Valor.setText(parametro.getValor());

        // Depuración después de asignar todos los valores básicos
        debugParametro("DESPUÉS DE ASIGNAR VALORES BÁSICOS", parametro); // Ajusta el tipo para coincidir con el enum
        String tipo = parametro.getTipo().name();
        if (tipo.equals("NUMERICO"))
            cmb_Tipo.setValue("NUMERICO");
        else if (tipo.equals("TEXTO"))
            cmb_Tipo.setValue("TEXTO");
        else if (tipo.equals("TIEMPO"))
            cmb_Tipo.setValue("TIEMPO");
        else if (tipo.equals("PORCENTAJE"))
            cmb_Tipo.setValue("PORCENTAJE");
        else
            cmb_Tipo.setValue("TEXTO");

        // Verificar que el código siga en el campo después de cambiar el tipo
        System.out.println("⚠️ VERIFICACIÓN DESPUÉS DE CAMBIAR TIPO: [" + txt_Codigo.getText() + "]");

        // Ajustar la categoría seleccionada - asegurarse de que se muestre
        // correctamente
        if (parametro.getCategoria() != null && !parametro.getCategoria().isEmpty()) {
            cmb_Categoria.setValue(parametro.getCategoria());
            System.out.println("DEPURACIÓN: Cargando parámetro con categoría: " + parametro.getCategoria());
        } else {
            cmb_Categoria.setValue("General");
            System.out.println("DEPURACIÓN: Parámetro sin categoría, usando General");
        }

        // Seleccionar estado según el parámetro
        rb_Activo.setSelected(parametro.getEstado() == Parametro.Estado.ACTIVO);
        rb_Inactivo.setSelected(parametro.getEstado() == Parametro.Estado.INACTIVO);

        // El código no debe ser editable pero debe verse claramente
        // txt_Codigo.setEditable(false);
        // txt_Codigo.setDisable(false);

        // txt_Codigo.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #444;
        // -fx-opacity: 1;");
        System.out.println("DEBUG >> CODIGO ASIGNADO: [" + txt_Codigo.getText() + "]");

        // Imprimir información de depuración
        System.out.println("DEPURACIÓN: Cargando parámetro " + parametro.getCodigo() +
                ", Categoría: " + parametro.getCategoria() +
                ", Estado: " + parametro.getEstado());

        // Verificación final después de una pequeña espera para asegurar que todo se ha
        // actualizado
        javafx.application.Platform.runLater(() -> {
            try {
                // Simular un pequeño retraso para dar tiempo a que la interfaz se actualice
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Ignorar
            }

            // Verificación final para asegurar que el código se muestre correctamente
            System.out.println("\n🔎🔎🔎 VERIFICACIÓN FINAL DEL CAMPO DE CÓDIGO 🔎🔎🔎");
            System.out.println("   - Campo código visible: " + txt_Codigo.isVisible());
            System.out.println("   - Campo código habilitado: " + !txt_Codigo.isDisable());
            System.out.println("   - Campo código editable: " + txt_Codigo.isEditable());
            System.out.println("   - Valor actual en campo: [" + txt_Codigo.getText() + "]");
            System.out.println("   - Valor original guardado: [" + codigoOriginal + "]");
            System.out.println("   - Valor en parámetro: [" + parametro.getCodigo() + "]");

            // Última verificación - si el campo está vacío pero tenemos el código,
            // restaurarlo
            if ((txt_Codigo.getText() == null || txt_Codigo.getText().isEmpty()) && codigoOriginal != null
                    && !codigoOriginal.isEmpty()) {
                System.out.println(
                        "⚠️⚠️⚠️ CAMPO VACÍO EN VERIFICACIÓN FINAL. RESTAURANDO CÓDIGO: [" + codigoOriginal + "]");
                txt_Codigo.setText(codigoOriginal);

                // Mostrar en el título y error para referencia
                lbl_Titulo.setText("Editar Parámetro: " + codigoOriginal);
                lbl_Error.setText("CÓDIGO RESTAURADO: " + codigoOriginal);
                lbl_Error.setVisible(true);
            }
        });
    }

    private void guardarParametro() {
        if (!validarFormulario()) {
            return;
        }

        try {
            // Si es un archivo, copiarlo al destino (si decides usar archivos)
            if ("ARCHIVO".equalsIgnoreCase(cmb_Tipo.getValue()) && archivoSeleccionado != null) {
                copiarArchivoADestino(archivoSeleccionado);
            }

            // Construir objeto Parametro
            String tipo = cmb_Tipo.getValue().toUpperCase(); // Debe ser NUMERICO, TEXTO, TIEMPO
            String estado = rb_Activo.isSelected() ? "ACTIVO" : "INACTIVO";

            // Asegurarnos de obtener el código correcto
            String codigo;
            if (txt_Codigo.getText() != null && !txt_Codigo.getText().trim().isEmpty()) {
                codigo = txt_Codigo.getText().trim();
                System.out.println("DEPURACIÓN: Usando código del TextField: " + codigo);
            } else if (parametroActual != null && parametroActual.getCodigo() != null) {
                codigo = parametroActual.getCodigo();
                System.out.println("DEPURACIÓN: Usando código del parámetro actual: " + codigo);

                // IMPORTANTE: También actualizar el TextField para mostrar el código
                txt_Codigo.setText(codigo);
            } else if (lbl_Error.getText() != null && lbl_Error.getText().contains("Código del parámetro:")) {
                // Intentar recuperar el código desde la etiqueta de error
                String textoLabel = lbl_Error.getText();
                codigo = textoLabel.substring(textoLabel.indexOf(":") + 1).trim();
                System.out.println("DEPURACIÓN: Recuperando código desde label: " + codigo);
            } else {
                mostrarError("No se pudo obtener el código del parámetro");
                return;
            }

            System.out.println("DEPURACIÓN: Guardando parámetro con código: " + codigo);

            Parametro parametro = new Parametro(
                    codigo,
                    txt_Nombre.getText().trim(),
                    txt_Descripcion.getText().trim(),
                    txt_Valor.getText().trim(),
                    tipo,
                    estado);

            // Agregar la categoría si está seleccionada
            if (cmb_Categoria.getValue() != null && !cmb_Categoria.getValue().isEmpty()) {
                // Asumimos que el modelo Parametro tiene un campo para categoría
                // Si no lo tiene, comentar o eliminar estas líneas
                try {
                    java.lang.reflect.Method setCategoriaMethod = parametro.getClass().getMethod("setCategoria",
                            String.class);
                    setCategoriaMethod.invoke(parametro, cmb_Categoria.getValue());
                } catch (Exception e) {
                    // La categoría no está implementada en el modelo, se ignora silenciosamente
                    System.out.println(
                            "Aviso: No se pudo establecer la categoría, el método no está implementado en el modelo");
                }
            }

            ParametroDAO dao = new ParametroDAO();
            boolean exito;
            if ("NUEVO".equals(accionActual) || parametroActual == null) {
                exito = dao.insertarParametro(parametro);
            } else {
                exito = dao.actualizarParametro(parametro);
            }

            if (exito) {
                mostrarInfo("El parámetro se guardó exitosamente");

                // Siempre invalidar la caché para asegurar que los cambios se reflejen inmediatamente
                try {
                    // Invalidar la caché del servicio de parámetros para asegurar que se cargue el nuevo valor
                    application.service.ParametroService.getInstance().invalidarCache();
                    System.out.println("Caché de parámetros invalidada después de guardar " + codigo);
                    
                    // Verificar si es un parámetro que afecta la interfaz visual
                    boolean esParametroInterfaz = codigo.equals("nombre_institucion") || codigo.equals("logo_sistema");
                    
                    // Actualizar la interfaz principal si es necesario
                    if (esParametroInterfaz) {
                        // Notificar al controlador principal para actualizar la interfaz
                        application.controllers.MainController mainController = application.controllers.MainController
                                .getInstance();
                        if (mainController != null) {
                            mainController.actualizarParametrosUI();
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error al actualizar caché o interfaz: " + ex.getMessage());
                }

                // Actualizar la tabla si es posible
                if (moduloParametrosController != null) {
                    moduloParametrosController.actualizarTabla();
                    moduloParametrosController.cerrarFormulario();
                }

                // Ejecutar callback si existe
                if (onGuardarCallback != null) {
                    onGuardarCallback.run();
                }
            } else {
                lbl_Error.setText("No se pudo guardar el parámetro. Verifique que el código no esté repetido.");
            }
        } catch (Exception e) {
            lbl_Error.setText("Error al guardar el parámetro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validarFormulario() {
        StringBuilder errores = new StringBuilder();

        // Validación mejorada para el código
        boolean esEdicion = "EDITAR".equals(accionActual) ||
                (parametroActual != null && parametroActual.getCodigo() != null);

        // En edición, verificamos que el código está presente aunque esté deshabilitado
        if (esEdicion) {
            // En modo edición, el código debe existir
            if (txt_Codigo.getText() == null || txt_Codigo.getText().trim().isEmpty()) {
                // Si no existe, intentamos recuperarlo del parámetro actual
                if (parametroActual != null && parametroActual.getCodigo() != null) {
                    // Forzamos el texto del código desde el parametro actual
                    txt_Codigo.setText(parametroActual.getCodigo());
                    System.out.println("DEPURACIÓN: Se restauró el código desde el parámetro actual: "
                            + parametroActual.getCodigo());
                } else {
                    errores.append("- No se pudo recuperar el código del parámetro\n");
                }
            }
        } else if (txt_Codigo.getText() == null || txt_Codigo.getText().trim().isEmpty()) {
            errores.append("- El código es obligatorio\n");
        }

        // Debugging detallado para ver el valor del código
        System.out.println("DEPURACIÓN validarFormulario - Código: [" + txt_Codigo.getText() + "]" +
                ", Edición: " + esEdicion +
                ", Deshabilitado: " + txt_Codigo.isDisabled() +
                ", ¿Código vacío?: " + (txt_Codigo.getText() == null || txt_Codigo.getText().trim().isEmpty()));

        if (txt_Nombre.getText() == null || txt_Nombre.getText().trim().isEmpty()) {
            errores.append("- El nombre es obligatorio\n");
        }

        // Validar valor solo si no es tipo archivo
        if (!"ARCHIVO".equalsIgnoreCase(cmb_Tipo.getValue())) {
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
            String tipo = cmb_Tipo.getValue().toUpperCase();
            String valor = txt_Valor.getText().trim();

            if ("NUMERICO".equals(tipo) && !valor.matches("\\d+(\\.\\d+)?")) {
                errores.append("- El valor debe ser numérico\n");
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

            // Si hay un callback, ejecutarlo
            if (onCancelarCallback != null) {
                onCancelarCallback.run();
            }
            // Si hay una referencia al controlador principal, cerrar formulario
            else if (moduloParametrosController != null) {
                moduloParametrosController.cerrarFormulario();
            }
            // Si no hay ninguna de las anteriores, cerrar la ventana
            else {
                Stage stage = (Stage) btn_Cancelar.getScene().getWindow();
                if (stage != null) {
                    stage.close();
                }
            }
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void configurarUploadArchivos() {
        // Usar referencia de método directa
        btn_SeleccionarArchivo.setOnAction(event -> seleccionarArchivo());
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
                    new FileChooser.ExtensionFilter("Todos los archivos", "*.*"));
        } else {
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Todos los archivos", "*.*"),
                    new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                    new FileChooser.ExtensionFilter("Documentos", "*.pdf", "*.doc", "*.docx", "*.txt"),
                    new FileChooser.ExtensionFilter("Archivos de configuración", "*.xml", "*.json", "*.properties"));
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
        if (archivoOrigen == null)
            return;

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

    // Método forzarVisualizacionCodigo eliminado para simplificar el código
    // y evitar confusión. Ahora usamos un enfoque estándar.

    /**
     * Método de depuración que muestra todos los valores del parámetro y los campos
     * del formulario
     * 
     * @param etiqueta  Una etiqueta para identificar el punto de la ejecución
     * @param parametro El objeto Parametro a depurar
     */
    private void debugParametro(String etiqueta, Parametro parametro) {
        System.out.println("\n==================================================================");
        System.out.println("🔍 DEPURACIÓN [" + etiqueta + "]");
        System.out.println("==================================================================");

        // Valores del objeto Parametro
        System.out.println("📋 VALORES DEL OBJETO PARÁMETRO:");
        System.out.println("   - Código: [" + (parametro != null ? parametro.getCodigo() : "NULL") + "]");
        System.out.println("   - Nombre: [" + (parametro != null ? parametro.getNombre() : "NULL") + "]");
        System.out.println("   - Descripción: [" + (parametro != null ? parametro.getDescripcion() : "NULL") + "]");
        System.out.println("   - Valor: [" + (parametro != null ? parametro.getValor() : "NULL") + "]");
        System.out.println("   - Tipo: ["
                + (parametro != null && parametro.getTipo() != null ? parametro.getTipo().name() : "NULL") + "]");
        System.out.println("   - Estado: ["
                + (parametro != null && parametro.getEstado() != null ? parametro.getEstado().name() : "NULL") + "]");
        System.out.println("   - Categoría: [" + (parametro != null ? parametro.getCategoria() : "NULL") + "]");

        // Valores actuales de los campos del formulario
        System.out.println("\n📝 VALORES ACTUALES DE LOS CAMPOS DEL FORMULARIO:");
        System.out.println("   - txt_Codigo: [" + (txt_Codigo != null ? txt_Codigo.getText() : "NULL") + "]");
        System.out.println(
                "   - txt_Codigo.isDisabled: [" + (txt_Codigo != null ? txt_Codigo.isDisabled() : "NULL") + "]");
        System.out.println(
                "   - txt_Codigo.isEditable: [" + (txt_Codigo != null ? txt_Codigo.isEditable() : "NULL") + "]");
        System.out.println("   - txt_Nombre: [" + (txt_Nombre != null ? txt_Nombre.getText() : "NULL") + "]");
        System.out.println(
                "   - txt_Descripcion: [" + (txt_Descripcion != null ? txt_Descripcion.getText() : "NULL") + "]");
        System.out.println("   - txt_Valor: [" + (txt_Valor != null ? txt_Valor.getText() : "NULL") + "]");
        System.out.println("   - cmb_Tipo: [" + (cmb_Tipo != null ? cmb_Tipo.getValue() : "NULL") + "]");
        System.out.println("   - cmb_Categoria: [" + (cmb_Categoria != null ? cmb_Categoria.getValue() : "NULL") + "]");

        System.out.println("==================================================================\n");
    }
}