package application.controllers.casos_documentacion;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class ModuloCasosController {
    // Referencia al panel principal de módulos
    private AnchorPane pnl_Modulos;

    // Permite inyectar el panel desde MainController
    public void setPanelModulos(AnchorPane panel) {
        this.pnl_Modulos = panel;
    }

    @FXML
    private Button btn_Nuevo;
    @FXML
    private TableColumn<CasoDemo, String> tbc_NumeroExpediente, tbc_TituloCaso, tbc_TipoCaso, tbc_FechaInicio,
            tbc_AbogadoAsignado, tbc_Estado;
    @FXML
    private TableColumn<CasoDemo, Void> tbc_BotonEditar, tbc_ButonVisualizar;
    @FXML
    private AnchorPane pnl_ListView;
    @FXML
    private AnchorPane pnl_DetalleView;
    @FXML
    private TableView<CasoDemo> tb_Casos;

    // Muestra la vista de detalle y la bitácora del caso seleccionado
    private void mostrarDetalleYBitacora(CasoDemo caso) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/casos_documentos/detalle_caso_bitacora.fxml"));
            Node detalle = loader.load();
            DetalleCasoBitacoraController controller = loader.getController();
            controller.setCaso(caso);
            controller.setOnRegresar(() -> cerrarDetalleYMostrarCasos());

            if (pnl_Modulos != null) {
                AnchorPane.setTopAnchor(detalle, 0.0);
                AnchorPane.setBottomAnchor(detalle, 0.0);
                AnchorPane.setLeftAnchor(detalle, 0.0);
                AnchorPane.setRightAnchor(detalle, 0.0);
                pnl_Modulos.getChildren().setAll(detalle);
            } else {
                pnl_DetalleView.getChildren().setAll(detalle);
                pnl_DetalleView.setVisible(true);
                pnl_DetalleView.setManaged(true);
                pnl_ListView.setVisible(false);
                pnl_ListView.setManaged(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cerrarDetalleYMostrarCasos() {
        pnl_DetalleView.getChildren().clear();
        pnl_DetalleView.setVisible(false);
        pnl_DetalleView.setManaged(false);
        pnl_ListView.setVisible(true);
        pnl_ListView.setManaged(true);
    }

    @FXML
    private void initialize() {
        tb_Casos.setRowFactory(tv -> {
            TableRow<CasoDemo> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    CasoDemo caso = row.getItem();
                    mostrarDetalleYBitacora(caso);
                }
            });
            return row;
        });
        configurarColumnas();
        inicializarColumnasDeBotones();
        cargarDatosEjemplo();
        btn_Nuevo.setOnAction(e -> mostrarFormulario(null, "NUEVO"));
    }

    private void configurarColumnas() {
        tbc_NumeroExpediente.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().numeroExpediente()));
        tbc_TituloCaso.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().titulo()));
        tbc_TipoCaso.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tipo()));
        tbc_FechaInicio.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fecha()));
        tbc_AbogadoAsignado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().abogado()));
        tbc_Estado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().estado()));
    }

    private void inicializarColumnasDeBotones() {
        agregarBoton(tbc_BotonEditar, "✎", "Editar");
        agregarBoton(tbc_ButonVisualizar, "👁", "Ver");
    }

    private void agregarBoton(TableColumn<CasoDemo, Void> columna, String texto, String tooltip) {
        columna.setCellFactory(param -> new TableCell<>() {
            final Button btn = new Button(texto);

            {
                btn.setTooltip(new Tooltip(tooltip));
                btn.setOnAction(e -> {
                    CasoDemo caso = getTableView().getItems().get(getIndex());
                    mostrarDetalleYBitacora(caso);
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

    private void mostrarFormulario(CasoDemo caso, String modo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/casos_documentos/form_casos.fxml"));
            Node form = loader.load();

            FormCasosController controller = loader.getController();
            controller.setOnCancelar(this::cerrarFormulario);
            controller.setOnGuardar(this::cerrarFormulario);
            controller.setModo(modo);

            if (caso != null && !modo.equals("NUEVO")) {
                controller.cargarDatosCaso(
                        caso.numeroExpediente(),
                        caso.titulo(),
                        caso.tipo(),
                        caso.fecha(),
                        caso.estado());
            }

            pnl_DetalleView.getChildren().setAll(form);
            pnl_DetalleView.setVisible(true);
            pnl_DetalleView.setManaged(true);
            pnl_ListView.setVisible(false);
            pnl_ListView.setManaged(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cerrarFormulario() {
        pnl_DetalleView.getChildren().clear();
        pnl_DetalleView.setVisible(false);
        pnl_DetalleView.setManaged(false);
        pnl_ListView.setVisible(true);
        pnl_ListView.setManaged(true);
        if (tb_Casos != null) {
            tb_Casos.setVisible(true);
            tb_Casos.setManaged(true);
        }
    }

    private void cargarDatosEjemplo() {
        tb_Casos.getItems().addAll(
                new CasoDemo("EXP001", "Caso Morales", "Civil", "2024-05-01", "Dra. Paredes", "Abierto"),
                new CasoDemo("EXP002", "Caso Rivera", "Penal", "2024-04-03", "Dr. López", "Archivado"));
    }

    public record CasoDemo(
            String numeroExpediente,
            String titulo,
            String tipo,
            String fecha,
            String abogado,
            String estado) {
    }
}