package application.controllers.casos_documentacion;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class ModuloHistorialController {

    @FXML private TextField txtf_Buscar;
    @FXML private Label lbl_NumeroExpediente;
    @FXML private Button btn_Buscar, btn_Anadir;
    @FXML private TableView<FormHistorialComunicacionController.ComunicacionDemo> tb_Comunicaciones;
    @FXML private TableColumn<FormHistorialComunicacionController.ComunicacionDemo, String> tbc_FechaEntrada, tbc_Usuario, tbc_TipoAccion, tbc_Descripcion, tbc_Expediente;
    @FXML private TableColumn<FormHistorialComunicacionController.ComunicacionDemo, Void> tbc_BotonEliminar;

    private Pane pnl_Forms;

    public void setFormularioContainer(Pane pnl_Forms) {
        this.pnl_Forms = pnl_Forms;
    }

    @FXML
    private void initialize() {
        configurarColumnas();
        cargarDatosEjemplo();

        btn_Anadir.setOnAction(e -> mostrarFormulario(null, "NUEVO"));
    }

    private void configurarColumnas() {
        tbc_FechaEntrada.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fecha()));
        tbc_Usuario.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().usuario()));
        tbc_TipoAccion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().accion()));
        tbc_Descripcion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().descripcion()));
        tbc_Expediente.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().expediente()));

        tbc_BotonEliminar.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("🗑");
            {
                btn.getStyleClass().add("table-button");
                btn.setTooltip(new Tooltip("Eliminar"));
                btn.setOnAction(e -> {
                    FormHistorialComunicacionController.ComunicacionDemo comm = getTableView().getItems().get(getIndex());
                    System.out.println("Se eliminó simbólicamente la comunicación de: " + comm.usuario() + ", tipo: " + comm.accion());
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

    private void mostrarFormulario(FormHistorialComunicacionController.ComunicacionDemo comunicacion, String modo) {
        Node form = FormHistorialComunicacionController.cargarFormulario(
                modo,
                comunicacion,
                v -> cerrarFormulario(),
                v -> cerrarFormulario()
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

    private void cargarDatosEjemplo() {
        tb_Comunicaciones.getItems().addAll(
                new FormHistorialComunicacionController.ComunicacionDemo("02/07/2024", "Andrea Ruiz", "Llamada", "Se notificó audiencia.", "EXP-001"),
                new FormHistorialComunicacionController.ComunicacionDemo("03/07/2024", "Carlos Gómez", "Correo", "Se envió copia del contrato.", "EXP-002")
        );
    }
}
