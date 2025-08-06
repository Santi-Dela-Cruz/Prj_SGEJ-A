package application.controllers.casos_documentacion;

import application.model.HistorialComunicacion;
import application.service.HistorialComunicacionService;
import application.database.DatabaseConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;

public class ModuloHistorialController {

    @FXML private TextField txtf_Buscar;
    @FXML private Label lbl_NumeroExpediente;
    @FXML private Button btn_Buscar, btn_Anadir;
    @FXML private TableView<HistorialComunicacion> tb_Comunicaciones;
    @FXML private TableColumn<HistorialComunicacion, String> tbc_FechaEntrada, tbc_Usuario, tbc_TipoAccion, tbc_Descripcion, tbc_Expediente;
    @FXML private TableColumn<HistorialComunicacion, Void> tbc_BotonEliminar;

    private Pane pnl_Forms;
    private HistorialComunicacionService service;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public void setFormularioContainer(Pane pnl_Forms) {
        this.pnl_Forms = pnl_Forms;
    }

    @FXML
    private void initialize() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            service = new HistorialComunicacionService(conn);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        configurarColumnas();
        cargarComunicaciones();

        btn_Anadir.setOnAction(e -> mostrarFormulario(null, "NUEVO"));
    }

    private void configurarColumnas() {
        tbc_FechaEntrada.setCellValueFactory(d -> {
            if (d.getValue().getFecha() != null) {
                return new SimpleStringProperty(dateFormat.format(d.getValue().getFecha()));
            }
            return new SimpleStringProperty("N/A");
        });
        tbc_Usuario.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAbogadoNombre() != null ? 
                                                                     d.getValue().getAbogadoNombre() : "Sin asignar"));
        tbc_TipoAccion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipo()));
        tbc_Descripcion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDescripcion()));
        // El modelo no tiene un campo específico para expediente, usamos el ID del caso como referencia
        tbc_Expediente.setCellValueFactory(d -> new SimpleStringProperty("EXP-" + d.getValue().getCasoId()));

        tbc_BotonEliminar.setCellFactory(tc -> new TableCell<>() {
            private final Button btn = new Button("🗑");
            {
                btn.getStyleClass().add("table-button");
                btn.setTooltip(new Tooltip("Eliminar"));
                btn.setOnAction(event -> {
                    if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                        HistorialComunicacion comm = getTableView().getItems().get(getIndex());
                        System.out.println("Se eliminó simbólicamente la comunicación de: " + comm.getAbogadoNombre() + ", tipo: " + comm.getTipo());
                        // Aquí implementar la eliminación real de la comunicación de la base de datos
                        // Y luego recargar la tabla
                        ModuloHistorialController.this.cargarComunicaciones();
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
                setStyle("-fx-alignment: CENTER;");
            }
        });
    }

    private void mostrarFormulario(HistorialComunicacion comunicacion, String modo) {
        // Aquí deberíamos adaptar el FormHistorialComunicacionController para que trabaje con HistorialComunicacion
        // en lugar de ComunicacionDemo, pero por ahora simplemente mostraremos un formulario vacío
        Node form = FormHistorialComunicacionController.cargarFormulario(
                modo,
                null,
                result -> {
                    cerrarFormulario();
                    cargarComunicaciones(); // Recargar datos después de guardar
                },
                result -> cerrarFormulario()
        );

        AnchorPane.setTopAnchor(form, 0.0);
        AnchorPane.setBottomAnchor(form, 0.0);
        AnchorPane.setLeftAnchor(form, 0.0);
        AnchorPane.setRightAnchor(form, 0.0);

        pnl_Forms.getChildren().setAll(form);
        pnl_Forms.setVisible(true);
        pnl_Forms.setManaged(true);
    }

    private void cerrarFormulario() {
        pnl_Forms.getChildren().clear();
        pnl_Forms.setVisible(false);
        pnl_Forms.setManaged(false);
    }

    /**
     * Carga las comunicaciones desde la base de datos
     */
    private void cargarComunicaciones() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (service == null) {
                service = new HistorialComunicacionService(conn);
            }
            
            // Limpiar tabla
            tb_Comunicaciones.getItems().clear();
            
            // Obtener comunicaciones de la base de datos
            List<HistorialComunicacion> lista = service.obtenerTodasLasComunicaciones();
            
            if (lista != null && !lista.isEmpty()) {
                tb_Comunicaciones.getItems().addAll(lista);
                System.out.println("INFO: Se cargaron " + lista.size() + " comunicaciones desde la base de datos");
            } else {
                System.out.println("INFO: No se encontraron comunicaciones en la base de datos");
                // Establecer un mensaje cuando la tabla está vacía
                Label lblNoData = new Label("No hay comunicaciones registradas en la base de datos");
                lblNoData.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
                tb_Comunicaciones.setPlaceholder(lblNoData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Mostrar mensaje de error en la tabla
            Label lblError = new Label("Error al cargar las comunicaciones: " + e.getMessage());
            lblError.setStyle("-fx-font-size: 14px; -fx-text-fill: #d32f2f;");
            tb_Comunicaciones.setPlaceholder(lblError);
        }
    }
}
