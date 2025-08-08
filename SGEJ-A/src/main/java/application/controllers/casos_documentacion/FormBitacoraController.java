package application.controllers.casos_documentacion;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.function.Consumer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import application.controllers.DialogUtil;

public class FormBitacoraController {
    public application.model.BitacoraCaso obtenerBitacoraDesdeFormulario(int casoId) {
        application.model.BitacoraCaso entrada = new application.model.BitacoraCaso();
        entrada.setCasoId(casoId);
        // Siempre asignamos la fecha actual
        entrada.setFechaEntrada(new java.sql.Date(System.currentTimeMillis()));
        // El usuario ya no se incluye
        if (txtf_Accion != null)
            entrada.setTipoAccion(txtf_Accion.getText());
        if (txta_Descripcion != null)
            entrada.setDescripcion(txta_Descripcion.getText());
        return entrada;
    }

    private int casoId = -1;

    public void setCasoId(int casoId) {
        this.casoId = casoId;
    }

    @FXML
    private Label lbl_TituloFormulario;
    @FXML
    private Label lbl_FechaActual;
    // Bitácora fields
    @FXML
    private Label lbl_Expediente, lbl_Responsable, lbl_FechaCreacion;
    @FXML
    private TextField txtf_Expediente, txtf_Responsable;
    @FXML
    private DatePicker dp_FechaCreacion;
    // Entrada fields
    @FXML
    private Label lbl_Accion, lbl_Descripcion;
    @FXML
    private TextField txtf_Accion;
    @FXML
    private TextArea txta_Descripcion;
    // Labels de error para validación
    @FXML
    private Label lbl_ErrorAccion, lbl_ErrorDescripcion;
    // Buttons
    @FXML
    private Button btn_Guardar, btn_Cancelar;

    private Consumer<Void> onGuardar;
    private Consumer<Void> onCancelar;

    public void setModoFormulario(String modo) {
        boolean esBitacora = "BITACORA".equalsIgnoreCase(modo);
        boolean esEntrada = "ENTRADA".equalsIgnoreCase(modo);

        if (lbl_TituloFormulario != null)
            lbl_TituloFormulario.setText(esBitacora ? "Registrar nueva Bitácora" : "Añadir entrada de Bitácora");

        // Only set visibility for fields that exist in the current FXML
        if (lbl_Expediente != null)
            lbl_Expediente.setVisible(true);
        if (txtf_Expediente != null)
            txtf_Expediente.setVisible(true);
        if (lbl_Responsable != null)
            lbl_Responsable.setVisible(true);
        if (txtf_Responsable != null)
            txtf_Responsable.setVisible(true);
        if (lbl_FechaCreacion != null)
            lbl_FechaCreacion.setVisible(true);
        if (dp_FechaCreacion != null)
            dp_FechaCreacion.setVisible(true);

        if (lbl_Accion != null)
            lbl_Accion.setVisible(true);
        if (txtf_Accion != null)
            txtf_Accion.setVisible(true);
        if (lbl_Descripcion != null)
            lbl_Descripcion.setVisible(true);
        if (txta_Descripcion != null)
            txta_Descripcion.setVisible(true);
    }

    public void setOnGuardar(Consumer<Void> callback) {
        this.onGuardar = callback;
    }

    public void setOnCancelar(Consumer<Void> callback) {
        this.onCancelar = callback;
    }

    @FXML
    private void initialize() {
        // Mostrar la fecha actual formateada
        if (lbl_FechaActual != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            lbl_FechaActual.setText(LocalDate.now().format(formatter));
        }

        // Configurar validación en tiempo real
        if (txtf_Accion != null) {
            txtf_Accion.textProperty().addListener((observable, oldValue, newValue) -> {
                lbl_ErrorAccion.setVisible(newValue.trim().isEmpty());
            });
        }

        if (txta_Descripcion != null) {
            txta_Descripcion.textProperty().addListener((observable, oldValue, newValue) -> {
                lbl_ErrorDescripcion.setVisible(newValue.trim().isEmpty());
            });
        }

        // Configurar acciones de botones
        if (btn_Guardar != null)
            btn_Guardar.setOnAction(e -> {
                guardarEntradaBitacora();
            });
        if (btn_Cancelar != null)
            btn_Cancelar.setOnAction(e -> {
                if (onCancelar != null)
                    onCancelar.accept(null);
            });
    }

    private void guardarEntradaBitacora() {
        try {
            // Reiniciar mensajes de error
            lbl_ErrorAccion.setVisible(false);
            lbl_ErrorDescripcion.setVisible(false);

            boolean hayErrores = false;

            // Validar que el caso ID sea válido
            if (casoId == -1) {
                DialogUtil.mostrarDialogo("Error", "No se ha seleccionado un caso válido.", "error",
                        List.of(ButtonType.OK));
                return;
            }

            // Validar que los campos obligatorios estén completos
            if (txtf_Accion.getText().trim().isEmpty()) {
                lbl_ErrorAccion.setVisible(true);
                txtf_Accion.requestFocus();
                hayErrores = true;
            }

            if (txta_Descripcion.getText().trim().isEmpty()) {
                lbl_ErrorDescripcion.setVisible(true);
                if (!hayErrores) {
                    txta_Descripcion.requestFocus();
                }
                hayErrores = true;
            }

            if (hayErrores) {
                // No continuamos con el proceso de guardado
                return;
            }

            // Crear botones personalizados en español
            ButtonType btnSi = new ButtonType("SI", ButtonBar.ButtonData.YES);
            ButtonType btnNo = new ButtonType("NO", ButtonBar.ButtonData.NO);
            
            // Mostrar diálogo de confirmación antes de guardar
            Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                    "Confirmar guardado",
                    "¿Está seguro que desea guardar esta entrada en la bitácora?",
                    "confirm",
                    List.of(btnSi, btnNo));

            // Si el usuario no confirma, cancelamos la operación
            if (!respuesta.isPresent() || respuesta.get().getButtonData() != ButtonBar.ButtonData.YES) {
                return;
            }

            // Crear el objeto bitácora
            application.model.BitacoraCaso entrada = new application.model.BitacoraCaso();
            entrada.setCasoId(casoId);
            // Asignar fecha actual automáticamente
            entrada.setFechaEntrada(new java.sql.Date(System.currentTimeMillis()));
            entrada.setTipoAccion(txtf_Accion.getText().trim());
            entrada.setDescripcion(txta_Descripcion.getText().trim());

            // Guardar en la base de datos
            java.sql.Connection conn = application.database.DatabaseConnection.getConnection();
            if (conn == null) {
                DialogUtil.mostrarDialogo("Error de conexión", "No se pudo conectar a la base de datos.", "error",
                        List.of(ButtonType.OK));
                return;
            }

            application.dao.BitacoraCasoDAO dao = new application.dao.BitacoraCasoDAO(conn);
            dao.insertarBitacora(entrada);

            // Notificar al usuario que se guardó correctamente
            DialogUtil.mostrarDialogo("Éxito", "La entrada de bitácora se guardó correctamente.", "confirm",
                    List.of(ButtonType.OK));

            // Limpiar los campos del formulario para una nueva entrada
            limpiarFormulario();

            // Notificar al controlador padre que se guardó correctamente
            if (onGuardar != null) {
                onGuardar.accept(null);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            DialogUtil.mostrarDialogo("Error", "Ocurrió un error al guardar: " + ex.getMessage(), "error",
                    List.of(ButtonType.OK));
        }
    }

    private void limpiarFormulario() {
        // Limpiar los campos de texto
        if (txtf_Accion != null)
            txtf_Accion.clear();
        if (txta_Descripcion != null)
            txta_Descripcion.clear();

        // Ocultar mensajes de error
        if (lbl_ErrorAccion != null)
            lbl_ErrorAccion.setVisible(false);
        if (lbl_ErrorDescripcion != null)
            lbl_ErrorDescripcion.setVisible(false);

        // Volver a poner el foco en el campo de acción
        if (txtf_Accion != null)
            txtf_Accion.requestFocus();
    }
}