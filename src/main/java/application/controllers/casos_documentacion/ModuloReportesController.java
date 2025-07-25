package application.controllers.casos_documentacion;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ModuloReportesController {

    @FXML private ComboBox<String> cbx_EstadoCaso, cbx_TipoCaso, cbx_AbogadoAsignado;
    @FXML private TextField txtf_FechaDesde, txtf_FechaHasta;
    @FXML private MenuButton btnc_Generar;
    @FXML private TableView<ReporteDemo> tb_Reportes;

    @FXML private TableColumn<ReporteDemo, String> tbc_NombreReporte, tbc_TituloCaso, tbc_AbogadoAsignado,
            tbc_FechaDesde, tbc_FechaHasta, tbc_TipoDocumento, tbc_Tamano;
    @FXML private TableColumn<ReporteDemo, Void> tbc_ButonVisualizar;

    @FXML
    private void initialize() {
        configurarColumnas();
        inicializarBotonVisualizar();
        cargarDatosEjemplo();
    }

    private void configurarColumnas() {
        tbc_NombreReporte.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nombre()));
        tbc_TituloCaso.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tipoCaso()));
        tbc_AbogadoAsignado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().abogado()));
        tbc_FechaDesde.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fechaDesde()));
        tbc_FechaHasta.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fechaHasta()));
        tbc_TipoDocumento.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tipoDocumento()));
        tbc_Tamano.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tamano()));
    }

    private void inicializarBotonVisualizar() {
        tbc_ButonVisualizar.setCellFactory(param -> new TableCell<>() {
            final Button btn = new Button("👁");

            {
                btn.getStyleClass().add("table-button");
                btn.setTooltip(new Tooltip("Visualizar reporte"));
                btn.setOnAction(e -> {
                    ReporteDemo reporte = getTableView().getItems().get(getIndex());
                    System.out.println("Visualizar reporte: " + reporte.nombre());
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
        tb_Reportes.getItems().addAll(
                new ReporteDemo("Reporte 1", "Civil", "Dra. Torres", "01/06/24", "30/06/24", "PDF", "256KB"),
                new ReporteDemo("Reporte 2", "Penal", "Dr. Méndez", "15/05/24", "15/06/24", "Excel", "320KB")
        );
    }

    public record ReporteDemo(
            String nombre,
            String tipoCaso,
            String abogado,
            String fechaDesde,
            String fechaHasta,
            String tipoDocumento,
            String tamano
    ) {}
}
