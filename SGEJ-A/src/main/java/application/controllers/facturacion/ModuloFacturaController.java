package application.controllers.facturacion;

import application.controllers.factura.FormFacturaController;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import java.io.IOException;

public class ModuloFacturaController {

    @FXML
    private Button btn_Nuevo;
    @FXML
    private Button btn_Buscar;
    @FXML
    private TextField txt_Busqueda;

    @FXML
    private TableView<FacturaDemo> tb_Facturas;
    @FXML
    private TableColumn<FacturaDemo, String> tbc_NumeroFactura;
    @FXML
    private TableColumn<FacturaDemo, String> tbc_FechaEmision;
    @FXML
    private TableColumn<FacturaDemo, String> tbc_NombreCliente;
    @FXML
    private TableColumn<FacturaDemo, String> tbc_NumExpediente;
    @FXML
    private TableColumn<FacturaDemo, String> tbc_Total;
    @FXML
    private TableColumn<FacturaDemo, String> tbc_EstadoFactura;
    @FXML
    private TableColumn<FacturaDemo, String> tbc_PagoRealizado;

    @FXML
    private TableColumn<FacturaDemo, Void> tbc_BotonEditar;
    @FXML
    private TableColumn<FacturaDemo, Void> tbc_BotonVer;
    @FXML
    private TableColumn<FacturaDemo, Void> tbc_BotonDescargar;

    private Pane pnl_Forms;

    public void setFormularioContainer(Pane pnl_Forms) {
        this.pnl_Forms = pnl_Forms;
    }

    @FXML
    private void initialize() {
        btn_Nuevo.setOnAction(e -> mostrarFormulario(null, "NUEVO"));

        configurarColumnasTexto();
        inicializarColumnasDeBotones();
        cargarDatosEjemplo();
        ocultarEncabezadosColumnasDeAccion();
    }

    private void ocultarEncabezadosColumnasDeAccion() {
        tb_Facturas.widthProperty().addListener((obs, oldVal, newVal) -> {
            Node header = tb_Facturas.lookup("TableHeaderRow");
            if (header != null)
                header.setVisible(true);
        });
    }

    private void mostrarFormulario(FacturaDemo factura, String modo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/factura/form_factura.fxml"));
            Node form = loader.load();

            // Configuramos la vista en el contenedor
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

    private void configurarColumnasTexto() {
        tbc_NumeroFactura.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().numeroFactura()));
        tbc_FechaEmision.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().fechaEmision()));
        tbc_NombreCliente.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().nombreCliente()));
        tbc_NumExpediente.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().numeroExpediente()));
        tbc_Total.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().totalFactura()));
        tbc_EstadoFactura.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().estadoFactura()));
        tbc_PagoRealizado
                .setCellValueFactory(data -> new SimpleStringProperty(data.getValue().pagoRealizado() ? "Sí" : "No"));
    }

    private void inicializarColumnasDeBotones() {
        agregarBotonPorColumna(tbc_BotonEditar, "✎", "Editar");
        agregarBotonPorColumna(tbc_BotonVer, "👁", "Ver");
        agregarBotonPorColumna(tbc_BotonDescargar, "⬇", "Descargar");

        tbc_BotonEditar.setPrefWidth(40);
        tbc_BotonVer.setPrefWidth(40);
    }

    private void agregarBotonPorColumna(TableColumn<FacturaDemo, Void> columna, String texto, String tooltip) {
        columna.getStyleClass().add("column-action");

        columna.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button(texto);

            {
                btn.getStyleClass().add("table-button");
                setStyle("-fx-alignment: CENTER;");
                btn.setTooltip(new Tooltip(tooltip));
                btn.setOnAction(event -> {
                    FacturaDemo factura = getTableView().getItems().get(getIndex());
                    if ("Editar".equals(tooltip)) {
                        mostrarFormulario(factura, "EDITAR");
                    } else if ("Ver".equals(tooltip)) {
                        mostrarFormulario(factura, "VER");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void cargarDatosEjemplo() {
        tb_Facturas.getItems().addAll(
                new FacturaDemo(
                        "F001-000001", "2025-07-06", "Ana Mora", "EXP-123", "313.60", "Abierto", true,
                        "0999999999001", "ABMECUADOR ESTUDIO JURÍDICO", "Av. Amazonas y NNUU, Quito",
                        "001", "002", "01", "Cédula", "0102030405", "Calle Siempre Viva 123", "cliente@correo.com",
                        "SVC-001", "Asesoría Legal Completa", "3", "100.00", "20.00", "280.00",
                        "Caso Martínez vs López", "Dra. Carolina Montalvo", "300.00", "20.00", "33.60",
                        "Transferencia", "313.60", "30 días"),
                new FacturaDemo(
                        "F001-000002", "2025-07-01", "Luis Pérez", "EXP-124", "201.60", "Registrado", true,
                        "0998888888002", "LEGALGROUP S.A.", "Calle Bolívar y Olmedo",
                        "002", "005", "01", "RUC", "1102233445", "Av. Patria E5-10", "luis@legal.com",
                        "SVC-003", "Redacción de documentos legales", "2", "80.00", "0.00", "160.00",
                        "Caso Herencia Familia Pérez", "Dr. Esteban Castro", "160.00", "0.00", "19.20",
                        "Tarjeta", "179.20", "15 días"),
                new FacturaDemo(
                        "F001-000003", "2025-06-28", "María Salas", "EXP-125", "278.00", "Rechazado", false,
                        "0997777777003", "JUSTICIA Y LEY", "Av. República y 10 de Agosto",
                        "003", "007", "01", "Pasaporte", "P1234567", "Calle 10 N-22", "maria@justicia.com",
                        "SVC-005", "Auditoría legal", "4", "75.00", "22.00", "278.00",
                        "Caso Empresa XY Audit", "Dra. Paulina Sánchez", "300.00", "22.00", "33.60",
                        "Efectivo", "278.00", "Contado"));

    }

    public record FacturaDemo(
            String numeroFactura,
            String fechaEmision,
            String nombreCliente,
            String numeroExpediente,
            String totalFactura,
            String estadoFactura,
            boolean pagoRealizado,

            // Campos adicionales para demo completo
            String rucEmisor,
            String razonSocialEmisor,
            String direccionEmisor,
            String codigoEstablecimiento,
            String codigoPuntoEmision,
            String codigoDocumento,
            String tipoIdCliente,
            String idCliente,
            String dirCliente,
            String emailCliente,
            String codigoServicio,
            String descripcionServicio,
            String cantidad,
            String tarifa,
            String descuento,
            String subtotalServicio,
            String nombreCaso,
            String abogadoResponsable,
            String subtotal,
            String totalDescuento,
            String iva,
            String formaPago,
            String montoPago,
            String plazo) {
    }

}
