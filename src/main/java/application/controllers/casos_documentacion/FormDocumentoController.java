package application.controllers.casos_documentacion;

import application.controllers.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class FormDocumentoController {

    @FXML private TextField txtf_NombreDocumento;
    @FXML private ComboBox<String> cbx_TipoDocumento;
    @FXML private TextArea txta_Descripcion;
    @FXML private DatePicker dtp_FechaCarga;
    @FXML private TextField txtf_NumeroExpediente;
    @FXML private TextField txtf_RutaArchivo;

    @FXML private Button btn_SeleccionarArchivo;
    @FXML private Button btn_Guardar;
    @FXML private Button btn_Cancelar;
    @FXML private Text txt_TituloForm;

    private Runnable onCancelar;
    private Runnable onGuardar;

    @FXML
    private void initialize() {
        cbx_TipoDocumento.getItems().addAll("PDF", "Word", "Imagen", "Texto", "Otro");

        btn_SeleccionarArchivo.setOnAction(e -> seleccionarArchivo());

        btn_Guardar.setOnAction(e -> {
            if (txtf_NumeroExpediente.getText().isEmpty() ||
                    txtf_NombreDocumento.getText().isEmpty() ||
                    cbx_TipoDocumento.getValue() == null ||
                    dtp_FechaCarga.getValue() == null) {

                DialogUtil.mostrarDialogo(
                        "Campos requeridos",
                        "Por favor, complete los campos obligatorios:\n" +
                                " - Número de Expediente\n" +
                                " - Nombre del Documento\n" +
                                " - Tipo de Documento\n" +
                                " - Fecha de Carga",
                        "warning",
                        List.of(ButtonType.OK)
                );
                return;
            }

            Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                    "Confirmación",
                    "¿Está seguro que desea guardar este documento?",
                    "confirm",
                    List.of(ButtonType.YES, ButtonType.NO)
            );

            if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
                if (onGuardar != null) onGuardar.run();
            }
        });

        btn_Cancelar.setOnAction(e -> {
            Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                    "Confirmación",
                    "¿Está seguro que desea cancelar el formulario?\nSe perderán los cambios no guardados.",
                    "confirm",
                    List.of(ButtonType.YES, ButtonType.NO)
            );

            if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
                if (onCancelar != null) onCancelar.run();
            }
        });

    }

    private void seleccionarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*"),
                new FileChooser.ExtensionFilter("Documentos PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Documentos Word", "*.docx"),
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Textos", "*.txt")
        );

        Window window = btn_SeleccionarArchivo.getScene().getWindow();
        File archivo = fileChooser.showOpenDialog(window);

        if (archivo != null) {
            txtf_RutaArchivo.setText(archivo.getAbsolutePath());
        }
    }

    public void setOnCancelar(Runnable callback) {
        this.onCancelar = callback;
    }

    public void setOnGuardar(Runnable callback) {
        this.onGuardar = callback;
    }

    public void setModo(String modo) {
        boolean esNuevo = "NUEVO".equals(modo);
        boolean esEditar = "EDITAR".equals(modo);
        boolean esVer = "VER".equals(modo);

        if (esNuevo) {
            txt_TituloForm.setText("Registrar nuevo Documento");
        } else if (esEditar) {
            txt_TituloForm.setText("Editar Documento");
        } else if (esVer) {
            txt_TituloForm.setText("Visualizar Documento");
        }

        txtf_NombreDocumento.setEditable(esNuevo || esEditar);
        cbx_TipoDocumento.setDisable(esVer);
        dtp_FechaCarga.setDisable(esVer);
        txta_Descripcion.setEditable(esNuevo || esEditar);
        txtf_NumeroExpediente.setEditable(esNuevo || esEditar);
        txtf_RutaArchivo.setEditable(false);
        btn_SeleccionarArchivo.setDisable(esVer);

        btn_Guardar.setVisible(!esVer);
        btn_Guardar.setDisable(esVer);
    }

    public void cargarDatosDocumento(String nombre, String tipo, String fecha, String descripcion, String expediente, String archivo) {
        txtf_NombreDocumento.setText(nombre);
        cbx_TipoDocumento.setValue(tipo);
        txtf_RutaArchivo.setText(archivo);
        txtf_NumeroExpediente.setText(expediente);
        txta_Descripcion.setText(descripcion);
        if (fecha != null && !fecha.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                dtp_FechaCarga.setValue(LocalDate.parse(fecha, formatter));
            } catch (DateTimeParseException e) {
                dtp_FechaCarga.setValue(null); // or handle error
            }
        }
    }

    private void guardarDocumento() {
        String nombre = txtf_NombreDocumento.getText();
        String tipo = cbx_TipoDocumento.getValue();
        String descripcion = txta_Descripcion.getText();
        String expediente = txtf_NumeroExpediente.getText();
        String archivo = txtf_RutaArchivo.getText();
        String fecha = (dtp_FechaCarga.getValue() != null) ? dtp_FechaCarga.getValue().toString() : "";

        System.out.println("Guardando documento:");
        System.out.println("Nombre: " + nombre);
        System.out.println("Tipo: " + tipo);
        System.out.println("Expediente: " + expediente);
        System.out.println("Archivo: " + archivo);
        System.out.println("Fecha: " + fecha);
        System.out.println("Descripción: " + descripcion);
    }
}
