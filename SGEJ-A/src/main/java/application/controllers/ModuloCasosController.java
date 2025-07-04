package application.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class ModuloCasosController {

    @FXML private TextField txtf_Buscar;
    @FXML private Button btn_Buscar, btn_Nuevo;
    @FXML private TableView<CasoDemo> tb_Casos;
    @FXML private TableColumn<CasoDemo, String> tbc_NumeroExpediente, tbc_TituloCaso, tbc_TipoCaso,
            tbc_FechaInicio, tbc_AbogadoAsignado, tbc_Estado;
    @FXML private TableColumn<CasoDemo, Void> tbc_BotonEditar, tbc_ButonVisualizar;

    private Pane pnl_Forms;

    public void setFormularioContainer(Pane pnl_Forms) {
        this.pnl_Forms = pnl_Forms;
    }

    @FXML
    private void initialize() {
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
                btn.getStyleClass().add("table-button");
                btn.setTooltip(new Tooltip(tooltip));
                btn.setOnAction(e -> {
                    CasoDemo caso = getTableView().getItems().get(getIndex());
                    if (tooltip.equals("Editar")) mostrarFormulario(caso, "EDITAR");
                    else mostrarFormulario(caso, "VER");
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
            controller.setOnCancelar(v -> cerrarFormulario());
            controller.setOnGuardar(v -> cerrarFormulario());
            controller.setModo(modo);

            if (caso != null && !modo.equals("NUEVO")) {
                controller.cargarDatosCaso(
                        caso.numeroExpediente(),
                        caso.titulo(),
                        caso.tipo(),
                        caso.fecha(),
                        caso.estado()
                );
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

    private void cargarDatosEjemplo() {
        tb_Casos.getItems().addAll(
                new CasoDemo("EXP001", "Caso Morales", "Civil", "2024-05-01", "Dra. Paredes", "Abierto"),
                new CasoDemo("EXP002", "Caso Rivera", "Penal", "2024-04-03", "Dr. López", "Archivado")
        );
    }

    public record CasoDemo(
            String numeroExpediente,
            String titulo,
            String tipo,
            String fecha,
            String abogado,
            String estado
    ) {}
}