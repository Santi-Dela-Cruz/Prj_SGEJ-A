package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class FormParametroController {

    @FXML private Label lbl_Titulo;
    @FXML private TextField txt_Codigo;
    @FXML private TextField txt_Nombre;
    @FXML private TextArea txt_Descripcion;
    @FXML private TextField txt_Valor;
    @FXML private ComboBox<String> cmb_Tipo;
    @FXML private RadioButton rb_Activo;
    @FXML private RadioButton rb_Inactivo;
    @FXML private Button btn_Guardar;
    @FXML private Button btn_Cancelar;

    private ModuloParametrosController moduloParametrosController;
    private ModuloParametrosController.ParametroDemo parametroActual;
    private String accionActual;

    public void setModuloParametrosController(ModuloParametrosController controller) {
        this.moduloParametrosController = controller;
    }

    @FXML
    private void initialize() {
        inicializarComboTipo();
        configurarBotones();
        configurarValidaciones();
    }

    private void inicializarComboTipo() {
        cmb_Tipo.getItems().addAll("Texto", "Numérico", "Booleano", "Tiempo", "Fecha", "Email", "URL");
        cmb_Tipo.setValue("Texto");
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
        
        if ("Activo".equals(parametro.getEstado())) {
            rb_Activo.setSelected(true);
        } else {
            rb_Inactivo.setSelected(true);
        }

        // En modo edición, el código no se puede cambiar
        txt_Codigo.setEditable(false);
        txt_Codigo.setDisable(true);
    }

    private void guardarParametro() {
        if (!validarFormulario()) {
            return;
        }

        try {
            // Aquí se guardaría en la base de datos
            // Por ahora solo mostramos un mensaje de confirmación
            
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

        if (txt_Valor.getText() == null || txt_Valor.getText().trim().isEmpty()) {
            errores.append("- El valor es obligatorio\n");
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
}
