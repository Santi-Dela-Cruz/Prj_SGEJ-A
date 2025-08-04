package application.controllers.facturacion;

import application.controllers.factura.FormFacturaController;
import application.model.Factura;
import application.service.FacturaService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModuloFacturaController {

    @FXML
    private Button btn_Nuevo;
    @FXML
    private Button btn_Buscar;
    @FXML
    private TextField txt_Busqueda;

    @FXML
    private TableView<Factura> tb_Facturas;
    @FXML
    private TableColumn<Factura, String> tbc_NumeroFactura;
    @FXML
    private TableColumn<Factura, String> tbc_FechaEmision;
    @FXML
    private TableColumn<Factura, String> tbc_NombreCliente;
    @FXML
    private TableColumn<Factura, String> tbc_NumExpediente;
    @FXML
    private TableColumn<Factura, String> tbc_Total;
    @FXML
    private TableColumn<Factura, String> tbc_EstadoFactura;
    @FXML
    private TableColumn<Factura, String> tbc_PagoRealizado;

    @FXML
    private TableColumn<Factura, Void> tbc_BotonEditar;
    @FXML
    private TableColumn<Factura, Void> tbc_BotonVer;
    @FXML
    private TableColumn<Factura, Void> tbc_BotonDescargar;

    private Pane pnl_Forms;

    public void setFormularioContainer(Pane pnl_Forms) {
        this.pnl_Forms = pnl_Forms;
    }

    private FacturaService facturaService;
    
    @FXML
    @SuppressWarnings("unused")
    private void initialize() {
        facturaService = new FacturaService();
        
        btn_Nuevo.setOnAction(__ -> mostrarFormulario(null, "NUEVO"));
        btn_Buscar.setOnAction(__ -> buscarFacturas());

        configurarColumnasTexto();
        inicializarColumnasDeBotones();
        cargarDatosDesdeBD();
        ocultarEncabezadosColumnasDeAccion();
    }
    
    private void buscarFacturas() {
        String termino = txt_Busqueda.getText().trim();
        List<Factura> facturas = facturaService.buscarFacturas(termino);
        
        tb_Facturas.setItems(FXCollections.observableArrayList(facturas));
    }

    @SuppressWarnings("unused")
    private void ocultarEncabezadosColumnasDeAccion() {
        tb_Facturas.widthProperty().addListener((__, ___, ____) -> {
            Node header = tb_Facturas.lookup("TableHeaderRow");
            if (header != null)
                header.setVisible(true);
        });
    }

    private void mostrarFormulario(Factura factura, String modo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/factura/form_factura.fxml"));
            Node form = loader.load();
            
            FormFacturaController controller = loader.getController();
            if (controller != null) {
                if (factura != null) {
                    controller.cargarFactura(factura);
                }
                // El modo se puede manejar con un método adicional si es necesario
                // Por ahora, el controller determina el modo según si la factura es nueva o existente
            }

            // Configuramos la vista en el contenedor
            AnchorPane.setTopAnchor(form, 0.0);
            AnchorPane.setBottomAnchor(form, 0.0);
            AnchorPane.setLeftAnchor(form, 0.0);
            AnchorPane.setRightAnchor(form, 0.0);

            pnl_Forms.getChildren().setAll(form);
            pnl_Forms.setVisible(true);
            pnl_Forms.setManaged(true);

        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al cargar formulario de factura", e);
        }
    }

    // Method removed as it was unused

    private void configurarColumnasTexto() {
        // Formato para las fechas
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        // Formato para valores monetarios
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-EC"));
        
        tbc_NumeroFactura.setCellValueFactory(data -> {
            Factura factura = data.getValue();
            if (factura.getCodigoEstablecimiento() != null && factura.getCodigoPuntoEmision() != null && factura.getSecuencial() != null) {
                String numero = factura.getCodigoEstablecimiento() + "-" + 
                               factura.getCodigoPuntoEmision() + "-" + 
                               factura.getSecuencial();
                return new SimpleStringProperty(numero);
            } else {
                return new SimpleStringProperty("N/A");
            }
        });
        
        tbc_FechaEmision.setCellValueFactory(data -> {
            Factura factura = data.getValue();
            if (factura.getFechaEmision() != null) {
                return new SimpleStringProperty(factura.getFechaEmision().format(dateFormatter));
            }
            return new SimpleStringProperty("");
        });
        
        tbc_NombreCliente.setCellValueFactory(data -> {
            String nombre = data.getValue().getNombreCliente();
            return new SimpleStringProperty(nombre != null ? nombre : "");
        });
        
        tbc_NumExpediente.setCellValueFactory(data -> {
            String expediente = data.getValue().getNumeroExpediente();
            return new SimpleStringProperty(expediente != null ? expediente : "");
        });
        
        tbc_Total.setCellValueFactory(data -> {
            Factura factura = data.getValue();
            if (factura.getValorTotal() != null) {
                return new SimpleStringProperty(currencyFormat.format(factura.getValorTotal()));
            }
            return new SimpleStringProperty("$0.00");
        });
        
        tbc_EstadoFactura.setCellValueFactory(data -> {
            String estado = data.getValue().getEstadoFactura();
            return new SimpleStringProperty(estado != null ? estado : "Pendiente");
        });
        
        tbc_PagoRealizado.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().isPagoRealizado() ? "Sí" : "No"));
    }

    private void inicializarColumnasDeBotones() {
        agregarBotonPorColumna(tbc_BotonEditar, "✎", "Editar");
        agregarBotonPorColumna(tbc_BotonVer, "👁", "Ver");
        agregarBotonPorColumna(tbc_BotonDescargar, "⬇", "Descargar");

        tbc_BotonEditar.setPrefWidth(40);
        tbc_BotonVer.setPrefWidth(40);
    }

    @SuppressWarnings("unused")
    private void agregarBotonPorColumna(TableColumn<Factura, Void> columna, String texto, String tooltip) {
        columna.getStyleClass().add("column-action");

        columna.setCellFactory(__ -> new TableCell<>() {
            private final Button btn = new Button(texto);

            {
                btn.getStyleClass().add("table-button");
                setStyle("-fx-alignment: CENTER;");
                btn.setTooltip(new Tooltip(tooltip));
                btn.setOnAction(__ -> {
                    Factura factura = getTableView().getItems().get(getIndex());
                    if ("Editar".equals(tooltip)) {
                        mostrarFormulario(factura, "EDITAR");
                    } else if ("Ver".equals(tooltip)) {
                        mostrarFormulario(factura, "VER");
                    } else if ("Descargar".equals(tooltip)) {
                        descargarFactura(factura);
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
    
    private void descargarFactura(Factura factura) {
        // Implementar lógica para descargar factura
        System.out.println("Descargando factura: " + factura.getId());
        // Aquí se podría generar un PDF o XML de la factura
    }

    private void cargarDatosDesdeBD() {
        try {
            List<Factura> facturas = facturaService.obtenerTodasLasFacturas();
            tb_Facturas.setItems(FXCollections.observableArrayList(facturas));
            
            // Si no hay facturas, mostrar un mensaje
            if (facturas.isEmpty()) {
                // Podríamos mostrar un mensaje en la tabla o en un label
                System.out.println("No hay facturas registradas en el sistema.");
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al cargar facturas desde la base de datos", e);
            // Mostrar un diálogo de error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al cargar facturas");
            alert.setContentText("No se pudieron cargar las facturas desde la base de datos: " + e.getMessage());
            alert.showAndWait();
        }
    }

}
