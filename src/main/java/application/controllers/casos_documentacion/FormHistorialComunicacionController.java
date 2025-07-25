package application.controllers.casos_documentacion;

import application.controllers.DialogUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class FormHistorialComunicacionController {

    @FXML private DatePicker dtp_Fecha;
    @FXML private TextField txtf_Usuario;
    @FXML private ComboBox<String> cbx_TipoAccion;
    @FXML private TextField txtf_Expediente;
    @FXML private TextArea txta_Descripcion;
    @FXML private Button btn_Guardar;
    @FXML private Button btn_Cancelar;
    @FXML private Text txt_TituloForm;

    private Runnable onGuardar, onCancelar;

    @FXML
    private void initialize() {
        cbx_TipoAccion.getItems().addAll("Llamada", "Correo", "Mensaje", "Visita", "Otro");

        btn_Guardar.setOnAction(e -> {
            if (dtp_Fecha.getValue() == null ||
                    txtf_Usuario.getText().isEmpty() ||
                    cbx_TipoAccion.getValue() == null ||
                    txtf_Expediente.getText().isEmpty() ||
                    txta_Descripcion.getText().isEmpty()) {

                DialogUtil.mostrarDialogo(
                        "Campos requeridos",
                        "Por favor, complete los campos obligatorios:\n" +
                                " - Fecha\n" +
                                " - Usuario\n" +
                                " - Tipo de Acción\n" +
                                " - Número de Expediente\n" +
                                " - Descripción",
                        "warning",
                        List.of(ButtonType.OK)
                );
                return;
            }

            Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                    "Confirmación",
                    "¿Está seguro que desea guardar este historial de comunicación?",
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

    public void setOnGuardar(Runnable handler) {
        this.onGuardar = handler;
    }

    public void setOnCancelar(Runnable handler) {
        this.onCancelar = handler;
    }

    public void setModo(String modo) {
        boolean esNuevo = "NUEVO".equals(modo);
        boolean esEditar = "EDITAR".equals(modo);
        boolean esVer = "VER".equals(modo);

        if (esNuevo) {
            txt_TituloForm.setText("Registrar comunicación");
        } else if (esEditar) {
            txt_TituloForm.setText("Editar comunicación");
        } else if (esVer) {
            txt_TituloForm.setText("Visualizar comunicación");
        }

        dtp_Fecha.setDisable(esVer);
        txtf_Usuario.setEditable(esNuevo || esEditar);
        cbx_TipoAccion.setDisable(esVer);
        txtf_Expediente.setEditable(esNuevo || esEditar);
        txta_Descripcion.setEditable(esNuevo || esEditar);

        btn_Guardar.setVisible(!esVer);
        btn_Guardar.setDisable(esVer);
    }

    public void cargarDatos(String fecha, String usuario, String tipo, String descripcion, String expediente) {
        if (fecha != null && !fecha.isEmpty()) dtp_Fecha.setValue(LocalDate.parse(fecha));
        txtf_Usuario.setText(usuario);
        cbx_TipoAccion.setValue(tipo);
        txta_Descripcion.setText(descripcion);
        txtf_Expediente.setText(expediente);
    }

    private void guardarComunicacion() {
        String fecha = dtp_Fecha.getValue() != null ? dtp_Fecha.getValue().toString() : "";
        String usuario = txtf_Usuario.getText();
        String tipo = cbx_TipoAccion.getValue();
        String descripcion = txta_Descripcion.getText();
        String expediente = txtf_Expediente.getText();

        System.out.println("Guardando comunicación:");
        System.out.println("Fecha: " + fecha);
        System.out.println("Usuario: " + usuario);
        System.out.println("Tipo de acción: " + tipo);
        System.out.println("Descripción: " + descripcion);
        System.out.println("Expediente: " + expediente);
    }

    public static Node cargarFormulario(String modo, ComunicacionDemo comunicacion, Consumer<Void> onGuardar, Consumer<Void> onCancelar) {
        try {
            FXMLLoader loader = new FXMLLoader(FormHistorialComunicacionController.class.getResource("/views/casos_documentos/form_historial_comunicacion.fxml"));
            Node form = loader.load();

            FormHistorialComunicacionController controller = loader.getController();
            controller.setModo(modo);
            controller.setOnGuardar(() -> onGuardar.accept(null));
            controller.setOnCancelar(() -> onCancelar.accept(null));

            if (comunicacion != null && !modo.equals("NUEVO")) {
                controller.cargarDatos(
                        comunicacion.fecha(),
                        comunicacion.usuario(),
                        comunicacion.accion(),
                        comunicacion.descripcion(),
                        comunicacion.expediente()
                );
            }

            return form;
        } catch (IOException e) {
            e.printStackTrace();
            return new Label("Error al cargar el formulario");
        }
    }

    public record ComunicacionDemo(String fecha, String usuario, String accion, String descripcion, String expediente) {}
}
