package application.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

public class ModuloDocumentosController {

    @FXML private TextField txtf_Buscar;
    @FXML private Button btn_Buscar, btn_Subir;
    @FXML private TableView<DocumentoDemo> tb_Documentos;
    @FXML private TableColumn<DocumentoDemo, String> tbc_Nombre, tbc_Tipo, tbc_Fecha, tbc_Tamano;
    @FXML private TableColumn<DocumentoDemo, Void> tbc_BotonVer, tbc_BotonEliminar, tbc_ButonDescargar;

    @FXML
    private void initialize() {
        configurarColumnas();
        inicializarColumnasDeBotones();
        cargarDatosEjemplo();
    }

    private void configurarColumnas() {
        tbc_Nombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nombre()));
        tbc_Tipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tipo()));
        tbc_Fecha.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fecha()));
        tbc_Tamano.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tamano()));
    }

    private void inicializarColumnasDeBotones() {
        agregarBoton(tbc_BotonVer, "👁", "Ver");
        agregarBoton(tbc_BotonEliminar, "🗑", "Eliminar");
        agregarBoton(tbc_ButonDescargar, "⬇", "Descargar");
    }

    private void agregarBoton(TableColumn<DocumentoDemo, Void> columna, String texto, String tooltip) {
        columna.setCellFactory(param -> new TableCell<>() {
            final Button btn = new Button(texto);

            {
                btn.getStyleClass().add("table-button");
                btn.setTooltip(new Tooltip(tooltip));
                btn.setOnAction(e -> {
                    DocumentoDemo doc = getTableView().getItems().get(getIndex());
                    System.out.println(tooltip + " documento: " + doc.nombre());
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
                new DocumentoDemo("Contrato_ABC.pdf", "PDF", "01/06/2024", "230 KB"),
                new DocumentoDemo("Evidencia_Julio.docx", "Word", "03/06/2024", "114 KB")
        );
    }

    public record DocumentoDemo(
            String nombre,
            String tipo,
            String fecha,
            String tamano
    ) {}
}
