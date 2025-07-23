package application.controllers.casos_documentacion;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class ModuloBitacoraController {

    @FXML private StackPane root;
    @FXML private AnchorPane pnl_ListView;
    @FXML private TableView<BitacoraResumen> tb_BitacorasList;
    @FXML private TableColumn<BitacoraResumen, String> tbc_Expediente, tbc_Responsable, tbc_FechaCreacion;
    @FXML private Button btn_AnadirBitacora;
    @FXML private AnchorPane pnl_EntriesView;
    @FXML private Button btn_Back;
    @FXML private Label lbl_NumeroBitacora;
    @FXML private TableView<BitacoraDemo> tb_BitacoraEntries;
    @FXML private TableColumn<BitacoraDemo, String> tbc_Fecha, tbc_Usuario, tbc_TipoAccion, tbc_Descripcion;
    @FXML private Button btn_AnadirEntrada;

    private Pane pnl_Forms;
    private BitacoraResumen bitacoraSeleccionada;

    public void setFormularioContainer(Pane pnl_Forms) { this.pnl_Forms = pnl_Forms; }

    @FXML
    private void initialize() {
        tbc_Expediente.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().numeroExpediente()));
        tbc_Responsable.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().responsable()));
        tbc_FechaCreacion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fechaCreacion()));
        tbc_Fecha.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fecha()));
        tbc_Usuario.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().usuario()));
        tbc_TipoAccion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().accion()));
        tbc_Descripcion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().descripcion()));

        tb_BitacorasList.getItems().addAll(
                new BitacoraResumen("EXP-001", "Laura Ortiz", "01/07/2024"),
                new BitacoraResumen("EXP-002", "Carlos Pérez", "02/07/2024")
        );

        tb_BitacorasList.setRowFactory(tv -> {
            TableRow<BitacoraResumen> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    mostrarEntradasBitacora(row.getItem());
                }
            });
            return row;
        });

        btn_Back.setOnAction(e -> mostrarListaBitacoras());
        if (btn_AnadirBitacora != null) btn_AnadirBitacora.setOnAction(e -> mostrarFormularioBitacora());
        if (btn_AnadirEntrada != null) btn_AnadirEntrada.setOnAction(e -> mostrarFormularioEntrada());

        mostrarListaBitacoras();
    }

    private void mostrarListaBitacoras() {
        pnl_ListView.setVisible(true); pnl_ListView.setManaged(true);
        pnl_EntriesView.setVisible(false); pnl_EntriesView.setManaged(false);
        bitacoraSeleccionada = null;
    }

    private void mostrarEntradasBitacora(BitacoraResumen resumen) {
        bitacoraSeleccionada = resumen;
        lbl_NumeroBitacora.setText("Expediente N° " + resumen.numeroExpediente());
        tb_BitacoraEntries.getItems().clear();

        if (resumen.numeroExpediente().equalsIgnoreCase("EXP-001")) {
            tb_BitacoraEntries.getItems().addAll(
                    new BitacoraDemo("01/07/2024", "Laura Ortiz", "Reunión", "Revisión inicial del caso."),
                    new BitacoraDemo("03/07/2024", "Laura Ortiz", "Correo", "Envió informe al cliente.")
            );
        } else if (resumen.numeroExpediente().equalsIgnoreCase("EXP-002")) {
            tb_BitacoraEntries.getItems().addAll(
                    new BitacoraDemo("02/07/2024", "Carlos Pérez", "Llamada", "Cliente solicitó nueva audiencia."),
                    new BitacoraDemo("04/07/2024", "Carlos Pérez", "Visita", "Firma de documentos en oficina.")
            );
        }

        pnl_ListView.setVisible(false); pnl_ListView.setManaged(false);
        pnl_EntriesView.setVisible(true); pnl_EntriesView.setManaged(true);
    }

    private void mostrarFormularioBitacora() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/casos_documentos/form_entrada_bitacora.fxml"));
            Node form = loader.load();
            FormBitacoraController controller = loader.getController();
            controller.setModoFormulario("BITACORA");
            controller.setOnCancelar(v -> cerrarFormulario());
            controller.setOnGuardar(v -> { cerrarFormulario(); /* refresh list if needed */ });
            mostrarFormulario(form);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void mostrarFormularioEntrada() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/casos_documentos/form_nueva_bitacora.fxml"));
            Node form = loader.load();
            FormBitacoraController controller = loader.getController();
            controller.setModoFormulario("ENTRADA");
            controller.setOnCancelar(v -> cerrarFormulario());
            controller.setOnGuardar(v -> { cerrarFormulario(); /* refresh entries if needed */ });
            mostrarFormulario(form);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void mostrarFormulario(Node form) {
        pnl_Forms.getChildren().setAll(form);
        pnl_Forms.setVisible(true);
        pnl_Forms.setManaged(true);
    }

    private void cerrarFormulario() {
        pnl_Forms.getChildren().clear();
        pnl_Forms.setVisible(false);
        pnl_Forms.setManaged(false);
    }

    public record BitacoraResumen(String numeroExpediente, String responsable, String fechaCreacion) {}
    public record BitacoraDemo(String fecha, String usuario, String accion, String descripcion) {}
}