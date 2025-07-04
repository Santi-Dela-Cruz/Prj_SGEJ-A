package application.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class ModuloDocumentosController {

    @FXML private TextField txtf_Buscar;
    @FXML private Button btn_Buscar, btn_Subir;
    @FXML private TableView<DocumentoDemo> tb_Documentos;
    @FXML private TableColumn<DocumentoDemo, String> tbc_Nombre, tbc_Tipo, tbc_Fecha, tbc_Tamano, tbc_Expediente;
    @FXML private TableColumn<DocumentoDemo, Void> tbc_BotonVer, tbc_BotonEliminar, tbc_BotonDescargar;

    private Pane pnl_Forms;

    public void setFormularioContainer(Pane pnl_Forms) {
        this.pnl_Forms = pnl_Forms;
    }

    @FXML
    private void initialize() {
        btn_Subir.setOnAction(e -> mostrarFormulario(null, "NUEVO"));

        configurarColumnas();
        inicializarColumnasDeBotones();
        cargarDatosEjemplo();
    }

    private void mostrarFormulario(DocumentoDemo doc, String modo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/casos_documentos/form_documento.fxml"));
            Node form = loader.load();

            FormDocumentoController controller = loader.getController();
            controller.setOnCancelar(() -> cerrarFormulario());
            controller.setOnGuardar(() -> cerrarFormulario());

            if (doc != null) {
                controller.cargarDatosDocumento(doc.nombre(), doc.tipo(), doc.fecha(), "", doc.numeroExpediente(), "");
                controller.setModo(modo);
            } else {
                controller.setModo("NUEVO");
            }

            AnchorPane.setTopAnchor(form, 0.0);
            AnchorPane.setBottomAnchor(form, 0.0);
            AnchorPane.setLeftAnchor(form, 0.0);
            AnchorPane.setRightAnchor(form, 0.0);

            pnl_Forms.getChildren().setAll(form);
            pnl_Forms.setVisible(true);
            pnl_Forms.setManaged(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cerrarFormulario() {
        pnl_Forms.getChildren().clear();
        pnl_Forms.setVisible(false);
        pnl_Forms.setManaged(false);
    }

    private void configurarColumnas() {
        tbc_Expediente.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().numeroExpediente()));
        tbc_Nombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nombre()));
        tbc_Tipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tipo()));
        tbc_Fecha.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fecha()));
        tbc_Tamano.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tamano()));
    }

    private void inicializarColumnasDeBotones() {
        agregarBoton(tbc_BotonVer, "👁", "Ver");
        agregarBoton(tbc_BotonEliminar, "🗑", "Eliminar");
        agregarBoton(tbc_BotonDescargar, "⬇", "Descargar");
    }

    private void agregarBoton(TableColumn<DocumentoDemo, Void> columna, String texto, String tooltip) {
        columna.setCellFactory(param -> new TableCell<>() {
            final Button btn = new Button(texto);

            {
                btn.getStyleClass().add("table-button");
                btn.setTooltip(new Tooltip(tooltip));
                btn.setOnAction(e -> {
                    DocumentoDemo doc = getTableView().getItems().get(getIndex());
                    if ("Ver".equals(tooltip)) {
                        mostrarFormulario(doc, "VER");
                    } else {
                        System.out.println(tooltip + " documento: " + doc.nombre());
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

    private void cargarDatosEjemplo() {
        tb_Documentos.getItems().addAll(
                new DocumentoDemo("EXP001", "Contrato_ABC.pdf", "PDF", "01/06/2024", "230 KB"),
                new DocumentoDemo("EXP002","Evidencia_Julio.docx", "Word", "03/06/2024", "114 KB")
        );
    }

    public record DocumentoDemo(
            String numeroExpediente,
            String nombre,
            String tipo,
            String fecha,
            String tamano
    ) {}
}
