package application.controllers.casos_documentacion;

import application.controllers.DialogUtil;
import application.database.DatabaseConnection;
import application.model.Caso;
import application.model.HistorialComunicacion;
import application.model.Personal;
import application.service.AbogadoService;
import application.service.CasoService;
import application.service.HistorialComunicacionService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class FormHistorialComunicacionController {

    @FXML
    private DatePicker dtp_Fecha;
    @FXML
    private ComboBox<Personal> cbx_Abogado;
    @FXML
    private ComboBox<String> cbx_TipoAccion;
    @FXML
    private TextField txtf_Expediente;
    @FXML
    private TextArea txta_Descripcion;
    @FXML
    private Button btn_Guardar;
    @FXML
    private Button btn_Cancelar;
    @FXML
    private Text txt_TituloForm;

    private Runnable onGuardar, onCancelar;

    @FXML
    private void initialize() {
        // Configurar los tipos de acción
        cbx_TipoAccion.getItems().addAll("Llamada", "Correo", "Mensaje", "Visita", "Otro");

        // Establecer la fecha actual
        dtp_Fecha.setValue(LocalDate.now());
        dtp_Fecha.setEditable(false);

        // Cargar la lista de abogados
        cargarAbogados();

        // Configurar cómo se muestran los abogados en el combobox
        cbx_Abogado.setConverter(new StringConverter<Personal>() {
            @Override
            public String toString(Personal abogado) {
                if (abogado == null) {
                    return "";
                }
                return abogado.getNombres() + " " + abogado.getApellidos();
            }

            @Override
            public Personal fromString(String string) {
                return null; // No se utiliza para la conversión en este caso
            }
        });

        btn_Guardar.setOnAction(event -> {
            if (dtp_Fecha.getValue() == null ||
                    cbx_Abogado.getValue() == null ||
                    cbx_TipoAccion.getValue() == null ||
                    txtf_Expediente.getText().isEmpty() ||
                    txta_Descripcion.getText().isEmpty()) {

                DialogUtil.mostrarDialogo(
                        "Campos requeridos",
                        "Por favor, complete los campos obligatorios:\n" +
                                " - Fecha\n" +
                                " - Abogado Responsable\n" +
                                " - Tipo de Acción\n" +
                                " - Número de Expediente\n" +
                                " - Descripción",
                        "warning",
                        List.of(ButtonType.OK));
                return;
            }

            // Validar que el número de expediente exista
            String numeroExpediente = txtf_Expediente.getText().trim();
            CasoService casoService = new CasoService();

            if (!casoService.existeCasoPorNumero(numeroExpediente)) {
                DialogUtil.mostrarDialogo(
                        "Caso no encontrado",
                        "El número de expediente '" + numeroExpediente + "' no existe en el sistema.\n" +
                                "Por favor, verifique el número de expediente.",
                        "warning",
                        List.of(ButtonType.OK));
                return;
            }

            // Intentar guardar la comunicación
            try {
                // Confirmar antes de guardar
                Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                        "Confirmación",
                        "¿Está seguro que desea guardar este historial de comunicación?",
                        "confirm",
                        List.of(ButtonType.YES, ButtonType.NO));

                if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
                    // Preparar los datos
                    HistorialComunicacion comunicacion = new HistorialComunicacion();
                    comunicacion.setFecha(java.sql.Date.valueOf(dtp_Fecha.getValue()));
                    comunicacion.setTipo(cbx_TipoAccion.getValue());
                    comunicacion.setDescripcion(txta_Descripcion.getText());

                    // Obtener el ID del caso a partir del número de expediente
                    Caso caso = casoService.obtenerCasoPorNumero(numeroExpediente);
                    if (caso != null) {
                        comunicacion.setCasoId(caso.getId());
                    } else {
                        throw new Exception("No se pudo encontrar el caso con el expediente: " + numeroExpediente);
                    }

                    // Obtener el abogado seleccionado
                    Personal abogado = cbx_Abogado.getValue();
                    comunicacion.setAbogadoId(abogado.getId());
                    comunicacion.setAbogadoNombre(abogado.getNombres() + " " + abogado.getApellidos());

                    // Guardar en la base de datos
                    Connection conn = DatabaseConnection.getConnection();
                    HistorialComunicacionService service = new HistorialComunicacionService(conn);
                    String resultado = service.registrarComunicacion(comunicacion);

                    if (resultado.isEmpty()) {
                        DialogUtil.mostrarDialogo(
                                "Guardado exitoso",
                                "La comunicación ha sido registrada correctamente.",
                                "info",
                                List.of(ButtonType.OK));

                        if (onGuardar != null)
                            onGuardar.run();
                    } else {
                        DialogUtil.mostrarDialogo(
                                "Error al guardar",
                                "No se pudo guardar la comunicación: " + resultado,
                                "error",
                                List.of(ButtonType.OK));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                DialogUtil.mostrarDialogo(
                        "Error en la base de datos",
                        "Ocurrió un error al guardar la comunicación: " + e.getMessage(),
                        "error",
                        List.of(ButtonType.OK));
            } catch (Exception e) {
                e.printStackTrace();
                DialogUtil.mostrarDialogo(
                        "Error inesperado",
                        "Ocurrió un error inesperado: " + e.getMessage(),
                        "error",
                        List.of(ButtonType.OK));
            }
        });

        btn_Cancelar.setOnAction(event -> {
            Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                    "Confirmación",
                    "¿Está seguro que desea cancelar el formulario?\nSe perderán los cambios no guardados.",
                    "confirm",
                    List.of(ButtonType.YES, ButtonType.NO));

            if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
                if (onCancelar != null)
                    onCancelar.run();
            }
        });

    }

    /**
     * Carga la lista de abogados en el combobox
     */
    private void cargarAbogados() {
        try {
            List<Personal> abogados = AbogadoService.obtenerAbogados();
            cbx_Abogado.setItems(FXCollections.observableArrayList(abogados));
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.mostrarDialogo(
                    "Error al cargar abogados",
                    "No se pudo cargar la lista de abogados: " + e.getMessage(),
                    "error",
                    List.of(ButtonType.OK));
        }
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
        cbx_Abogado.setDisable(esVer);
        cbx_TipoAccion.setDisable(esVer);
        txtf_Expediente.setEditable(esNuevo || esEditar);
        txta_Descripcion.setEditable(esNuevo || esEditar);

        btn_Guardar.setVisible(!esVer);
        btn_Guardar.setDisable(esVer);
    }

    public void cargarDatos(String fecha, String usuario, String tipo, String descripcion, String expediente) {
        if (fecha != null && !fecha.isEmpty()) {
            try {
                dtp_Fecha.setValue(LocalDate.parse(fecha));
            } catch (Exception e) {
                // Si no se puede parsear la fecha, usar la actual
                dtp_Fecha.setValue(LocalDate.now());
            }
        }

        // Buscar el abogado por nombre y seleccionarlo
        if (usuario != null && !usuario.isEmpty()) {
            for (Personal abogado : cbx_Abogado.getItems()) {
                String nombreCompleto = abogado.getNombres() + " " + abogado.getApellidos();
                if (nombreCompleto.equalsIgnoreCase(usuario)) {
                    cbx_Abogado.setValue(abogado);
                    break;
                }
            }
        }

        cbx_TipoAccion.setValue(tipo);
        txta_Descripcion.setText(descripcion);
        txtf_Expediente.setText(expediente);
    }

    public static Node cargarFormulario(String modo, ComunicacionDemo comunicacion, Consumer<Void> onGuardar,
            Consumer<Void> onCancelar) {
        try {
            FXMLLoader loader = new FXMLLoader(FormHistorialComunicacionController.class
                    .getResource("/views/casos_documentos/form_historial_comunicacion.fxml"));
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
                        comunicacion.expediente());
            }

            return form;
        } catch (IOException e) {
            e.printStackTrace();
            return new Label("Error al cargar el formulario");
        }
    }

    public record ComunicacionDemo(String fecha, String usuario, String accion, String descripcion, String expediente) {
    }
}
