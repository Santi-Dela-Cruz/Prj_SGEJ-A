package application.controllers.factura;

import application.controllers.DialogUtil;
import application.model.Factura;
import application.model.FacturaDetalle;
import application.service.FacturaService;
import application.service.ParametroService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import application.model.Cliente;
import application.model.Caso;
import application.model.AbogadoCaso;
import application.service.ClienteService;
import application.service.CasoService;
import java.util.Optional;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FormFacturaController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(FormFacturaController.class.getName());

    // Servicios
    private final ParametroService parametroService;
    private final FacturaService facturaService;

    // Modelo
    private Factura factura;
    private ObservableList<FacturaDetalle> detallesFactura;

    // Indicadores de modo
    private boolean modoEdicion = false;

    @FXML
    private Button btn_Guardar, btn_Cancelar;

    // Datos del Emisor
    @FXML
    private TextField txtf_RucEmisor, txtf_RazonSocialEmisor, txtf_DireccionMatriz,
            txtf_DireccionSucursal, txtf_CodEstablecimiento, txtf_CodPuntoEmision, txtf_Secuencial,
            txtf_CodigoDocumento;
    @FXML
    private DatePicker dtp_FechaEmision;

    // Datos del Documento
    @FXML
    private ComboBox<String> cbx_TipoDocumento, cbx_Ambiente, cbx_Emision;
    @FXML
    private ComboBox<String> cbx_ObligadoContabilidad;

    // Cliente
    @FXML
    private TextField txtf_NombreCliente, txtf_IdCliente, txtf_DirCliente,
            txtf_EmailCliente, txtf_TelefonoCliente;
    @FXML
    private ComboBox<String> cbx_TipoIdCliente;
    @FXML
    private Button btn_BuscarCliente;

    // Servicios
    @FXML
    private TextField txtf_CodigoServicio, txtf_DescripcionServicio, txtf_Cantidad,
            txtf_Tarifa, txtf_Descuento, txtf_SubtotalServicio, txtf_CodigoAuxiliar;
    @FXML
    private Button btn_AgregarProducto, btn_EliminarProducto;
    @FXML
    private TableView<FacturaDetalle> tbl_Productos;
    @FXML
    private TableColumn<FacturaDetalle, String> col_Cantidad, col_Codigo, col_Descripcion,
            col_PrecioUnitario, col_Descuento, col_PrecioTotal;

    // Caso
    @FXML
    private TextField txtf_NumExpediente, txtf_NombreCaso, txtf_Abogado;
    @FXML
    private Button btn_BuscarCaso;

    // Impuestos y Totales
    @FXML
    private TextField txtf_Subtotal12, txtf_Subtotal0, txtf_SubtotalNoObjetoIva,
            txtf_SubtotalExentoIva, txtf_TotalDescuento, txtf_Subtotal,
            txtf_Iva, txtf_Propina, txtf_TotalFactura, txtf_DevolucionIva;

    // Subsidio
    @FXML
    private TextField txtf_ValorSinSubsidio, txtf_AhorroSubsidio;

    // Pago
    @FXML
    private ComboBox<String> cbx_FormaPago, cbx_EstadoFactura;
    @FXML
    private TextField txtf_MontoPago, txtf_Plazo;
    @FXML
    private CheckBox chk_PagoRealizado;

    @FXML
    private Text txt_TituloForm;

    private Runnable onGuardar, onCancelar;

    // Constructor
    public FormFacturaController() {
        this.parametroService = ParametroService.getInstance();
        this.facturaService = new FacturaService();
        this.detallesFactura = FXCollections.observableArrayList();
    }

    public void setOnGuardar(Runnable handler) {
        this.onGuardar = handler;
    }

    public void setOnCancelar(Runnable handler) {
        this.onCancelar = handler;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("Inicializando formulario de factura");

        // Inicializar factura
        factura = new Factura();

        // Configurar componentes
        configurarComponentes();

        // Cargar parámetros del sistema
        cargarParametros();

        // Configurar validadores y listeners
        configurarValidadores();

        // Configurar tabla de servicios
        configurarTablaServicios();

        // Configurar cálculos automáticos
        configurarCalculos();

        // Establecer fecha actual
        dtp_FechaEmision.setValue(LocalDate.now());
    }

    /**
     * Configura los componentes iniciales
     */
    private void configurarComponentes() {
        // Configurar combos
        cbx_TipoDocumento.setItems(FXCollections.observableArrayList("FACTURA", "NOTA DE VENTA"));
        cbx_TipoDocumento.getSelectionModel().selectFirst();

        cbx_Ambiente.setItems(FXCollections.observableArrayList("PRUEBAS", "PRODUCCIÓN"));
        cbx_Ambiente.getSelectionModel().selectFirst();

        cbx_Emision.setItems(FXCollections.observableArrayList("NORMAL"));
        cbx_Emision.getSelectionModel().selectFirst();

        cbx_TipoIdCliente.setItems(FXCollections.observableArrayList("RUC", "CÉDULA", "PASAPORTE"));
        cbx_TipoIdCliente.getSelectionModel().selectFirst();

        cbx_FormaPago.setItems(FXCollections.observableArrayList(
                "EFECTIVO", "TRANSFERENCIA", "CHEQUE", "TARJETA DE CRÉDITO", "CRÉDITO DIRECTO"));
        cbx_FormaPago.getSelectionModel().selectFirst();

        cbx_EstadoFactura.setItems(FXCollections.observableArrayList(
                "PENDIENTE", "PAGADO", "ANULADO", "AUTORIZADO", "RECHAZADO"));
        cbx_EstadoFactura.getSelectionModel().selectFirst();

        cbx_ObligadoContabilidad.setItems(FXCollections.observableArrayList("SI", "NO"));
        cbx_ObligadoContabilidad.getSelectionModel().selectFirst();

        // Campos numéricos
        configurarCampoNumerico(txtf_Cantidad);
        configurarCampoNumerico(txtf_Tarifa);
        configurarCampoNumerico(txtf_Descuento);
        configurarCampoNumerico(txtf_MontoPago);
        configurarCampoNumerico(txtf_Plazo);
        configurarCampoNumerico(txtf_Propina);
        configurarCampoNumerico(txtf_ValorSinSubsidio);

        // Campos no editables
        txtf_SubtotalServicio.setEditable(false);
        txtf_Subtotal12.setEditable(false);
        txtf_Subtotal0.setEditable(false);
        txtf_SubtotalNoObjetoIva.setEditable(false);
        txtf_SubtotalExentoIva.setEditable(false);
        txtf_TotalDescuento.setEditable(false);
        txtf_Subtotal.setEditable(false);
        txtf_Iva.setEditable(false);
        txtf_AhorroSubsidio.setEditable(false);
        txtf_TotalFactura.setEditable(false);

        // Configurar DatePicker
        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            final java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter
                    .ofPattern("dd/MM/yyyy");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };
        dtp_FechaEmision.setConverter(converter);
        dtp_FechaEmision.setPromptText("dd/mm/aaaa");
    }

    /**
     * Configura un campo para aceptar solo números y decimales
     */
    private void configurarCampoNumerico(TextField campo) {
        campo.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d*(\\.\\d*)?")) {
                campo.setText(oldValue);
            }
        });
    }

    /**
     * Carga los parámetros del sistema
     */
    private void cargarParametros() {
        try {
            // Log para diagnostico
            System.out.println("Cargando parámetros del emisor para la factura...");

            // Emisor - Usando parámetros actualizados (todos en minúsculas para
            // consistencia)
            String rucInst = parametroService.getValor("ruc_institucional", "");
            System.out.println("RUC Institucional cargado: " + rucInst);
            txtf_RucEmisor.setText(rucInst);

            String razonSocial = parametroService.getValor("razon_social", "");
            System.out.println("Razón Social cargada: " + razonSocial);
            txtf_RazonSocialEmisor.setText(razonSocial);

            String direccionMatriz = parametroService.getValor("direccion_matriz", "");
            System.out.println("Dirección Matriz cargada: " + direccionMatriz);
            txtf_DireccionMatriz.setText(direccionMatriz);

            String direccionSucursal = parametroService.getValor("direccion_sucursal", direccionMatriz);
            System.out.println("Dirección Sucursal cargada: " + direccionSucursal);
            txtf_DireccionSucursal.setText(direccionSucursal);

            // Códigos de establecimiento y punto de emisión (convertidos a minúsculas)
            String codEstablecimiento = parametroService.getValor("codigo_establecimiento", "001");
            System.out.println("Código Establecimiento cargado: " + codEstablecimiento);
            txtf_CodEstablecimiento.setText(codEstablecimiento);

            String codPuntoEmision = parametroService.getValor("codigo_punto_emision", "001");
            System.out.println("Código Punto Emisión cargado: " + codPuntoEmision);
            txtf_CodPuntoEmision.setText(codPuntoEmision);

            // Documento (convertido a minúsculas)
            String tipoDoc = parametroService.getValor("tipo_documento_factura", "FACTURA");
            System.out.println("Tipo Documento cargado: " + tipoDoc);
            cbx_TipoDocumento.getSelectionModel().select(tipoDoc);

            String ambiente = parametroService.getValor("ambiente_facturacion", "1");
            System.out.println("Ambiente cargado: " + ambiente);
            cbx_Ambiente.getSelectionModel().select(ambiente.equals("1") ? "PRUEBAS" : "PRODUCCIÓN");

            String tipoEmision = parametroService.getValor("tipo_emision", "1");
            System.out.println("Tipo Emisión cargado: " + tipoEmision);
            cbx_Emision.getSelectionModel().select(tipoEmision.equals("1") ? "NORMAL" : "");

            boolean obligadoContabilidad = parametroService.getValorBoolean("obligado_contabilidad", false);
            System.out.println("Obligado Contabilidad cargado: " + obligadoContabilidad);
            System.out.println("Valor directo del parametro obligado_contabilidad: "
                    + parametroService.getValor("obligado_contabilidad", "false"));
            cbx_ObligadoContabilidad.getSelectionModel().select(obligadoContabilidad ? "SI" : "NO");

            // Código del documento
            String codigoDocumento = parametroService.getValor("codigo_documento_factura", "01");
            System.out.println("Código Documento cargado: " + codigoDocumento);
            txtf_CodigoDocumento.setText(codigoDocumento);

            // Secuencial
            if (!modoEdicion) {
                try {
                    List<Factura> facturas = facturaService.obtenerTodasLasFacturas();
                    txtf_Secuencial
                            .setText(facturas.isEmpty() ? "000000001" : facturaService.generarProximoSecuencial());
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING,
                            "Error al obtener facturas para generar secuencial, usando valor por defecto", e);
                    txtf_Secuencial.setText("000000001");
                }
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cargar parámetros", e);
            DialogUtil.mostrarMensajeError("Error", "Error al cargar parámetros del sistema: " + e.getMessage());
        }
    }

    /**
     * Configura los validadores de campos
     */
    private void configurarValidadores() {
        // Validar cliente obligatorio
        txtf_NombreCliente.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && txtf_NombreCliente.getText().trim().isEmpty()) {
                txtf_NombreCliente.setStyle("-fx-border-color: red");
            } else {
                txtf_NombreCliente.setStyle("");
            }
        });

        // Validar ID cliente obligatorio
        txtf_IdCliente.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && txtf_IdCliente.getText().trim().isEmpty()) {
                txtf_IdCliente.setStyle("-fx-border-color: red");
            } else {
                txtf_IdCliente.setStyle("");
            }
        });

        // Calcular subtotal de servicio
        txtf_Cantidad.textProperty().addListener((observable, oldValue, newValue) -> calcularSubtotalServicio());
        txtf_Tarifa.textProperty().addListener((observable, oldValue, newValue) -> calcularSubtotalServicio());
        txtf_Descuento.textProperty().addListener((observable, oldValue, newValue) -> calcularSubtotalServicio());

        // Calcular subsidio
        txtf_ValorSinSubsidio.textProperty().addListener((observable, oldValue, newValue) -> calcularSubsidio());
        txtf_Propina.textProperty().addListener((observable, oldValue, newValue) -> actualizarTotales());
    }

    /**
     * Configura la tabla de servicios
     */
    private void configurarTablaServicios() {
        // Configurar columnas
        col_Codigo.setCellValueFactory(
                cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getCodigoServicio())));

        col_Descripcion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescripcion()));

        col_Cantidad.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getCantidad().toString()));

        col_PrecioUnitario.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getPrecioUnitario().toString()));

        col_Descuento.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getDescuento().toString()));

        col_PrecioTotal.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().calcularSubtotal().toString()));

        // Establecer datos
        tbl_Productos.setItems(detallesFactura);

        // Selección de fila
        tbl_Productos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarDetalleServicio(newSelection);
            }
        });
    }

    /**
     * Devuelve el texto legible del tipo de impuesto
     */
    private String getTipoImpuestoTexto(String codigo) {
        if (codigo == null)
            return "IVA 12%";

        switch (codigo) {
            case "IVA_12":
                return "IVA 12%";
            case "IVA_0":
                return "IVA 0%";
            case "NO_OBJETO_IVA":
                return "NO OBJETO DE IVA";
            case "EXENTO_IVA":
                return "EXENTO DE IVA";
            default:
                return "IVA 12%";
        }
    }

    /**
     * Obtiene el código del tipo de impuesto desde el texto del combo
     */
    private String getTipoImpuestoCodigo(String texto) {
        if (texto == null)
            return "IVA_12";

        switch (texto) {
            case "IVA 12%":
                return "IVA_12";
            case "IVA 0%":
                return "IVA_0";
            case "NO OBJETO DE IVA":
                return "NO_OBJETO_IVA";
            case "EXENTO DE IVA":
                return "EXENTO_IVA";
            default:
                return "IVA_12";
        }
    }

    /**
     * Muestra el detalle de un servicio seleccionado
     */
    private void mostrarDetalleServicio(FacturaDetalle detalle) {
        txtf_CodigoServicio.setText(String.valueOf(detalle.getCodigoServicio()));
        txtf_DescripcionServicio.setText(detalle.getDescripcion());
        txtf_Cantidad.setText(detalle.getCantidad().toString());
        txtf_Tarifa.setText(detalle.getPrecioUnitario().toString());
        txtf_Descuento.setText(detalle.getDescuento().toString());
        txtf_SubtotalServicio.setText(detalle.calcularSubtotal().toString());
        // Tipo impuesto se maneja internamente en la clase FacturaDetalle
    }

    /**
     * Configura los cálculos automáticos
     */
    private void configurarCalculos() {
        // Tecla Enter en servicios para agregar
        txtf_SubtotalServicio.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                agregarServicio();
            }
        });

        // Botones
        if (btn_AgregarProducto != null) {
            btn_AgregarProducto.setOnAction(event -> agregarServicio());
        }

        if (btn_EliminarProducto != null) {
            btn_EliminarProducto.setOnAction(event -> eliminarServicio());
        }

        // Botones de búsqueda para cliente y caso
        if (btn_BuscarCliente != null) {
            btn_BuscarCliente.setOnAction(event -> buscarClienteDialogo());
        }

        if (btn_BuscarCaso != null) {
            btn_BuscarCaso.setOnAction(event -> buscarCasoDialogo());
        }

        if (btn_Guardar != null) {
            btn_Guardar.setOnAction(event -> guardarFactura());
        }

        if (btn_Cancelar != null) {
            btn_Cancelar.setOnAction(event -> cancelar());
        }
    }

    /**
     * Calcula el subtotal del servicio actual
     */
    private void calcularSubtotalServicio() {
        try {
            BigDecimal cantidad = new BigDecimal(txtf_Cantidad.getText().isEmpty() ? "1" : txtf_Cantidad.getText());
            BigDecimal tarifa = new BigDecimal(txtf_Tarifa.getText().isEmpty() ? "0" : txtf_Tarifa.getText());
            BigDecimal descuento = new BigDecimal(txtf_Descuento.getText().isEmpty() ? "0" : txtf_Descuento.getText());

            BigDecimal subtotal = cantidad.multiply(tarifa).subtract(descuento);
            txtf_SubtotalServicio.setText(subtotal.setScale(2, RoundingMode.HALF_UP).toString());
        } catch (NumberFormatException e) {
            txtf_SubtotalServicio.setText("0.00");
        }
    }

    /**
     * Calcula el ahorro por subsidio
     */
    private void calcularSubsidio() {
        try {
            if (!txtf_ValorSinSubsidio.getText().isEmpty() && !txtf_TotalFactura.getText().isEmpty()) {
                BigDecimal valorSinSubsidio = new BigDecimal(txtf_ValorSinSubsidio.getText());
                BigDecimal valorTotal = new BigDecimal(
                        txtf_TotalFactura.getText().isEmpty() ? "0" : txtf_TotalFactura.getText());

                if (valorSinSubsidio.compareTo(valorTotal) > 0) {
                    BigDecimal ahorro = valorSinSubsidio.subtract(valorTotal);
                    txtf_AhorroSubsidio.setText(ahorro.setScale(2, RoundingMode.HALF_UP).toString());
                } else {
                    txtf_AhorroSubsidio.setText("0.00");
                }
            }
        } catch (NumberFormatException e) {
            txtf_AhorroSubsidio.setText("0.00");
        }
    }

    /**
     * Agrega un servicio a la factura, donde el servicio está asociado a un caso
     * (expediente)
     */
    @FXML
    private void agregarServicio() {
        // Verificar que se haya seleccionado un cliente
        if (txtf_IdCliente.getText().isEmpty()) {
            DialogUtil.mostrarMensajeAdvertencia("Validación", "Debe seleccionar un cliente primero");
            return;
        }

        // Verificar que se haya seleccionado un caso (expediente)
        if (txtf_NumExpediente.getText().isEmpty()) {
            DialogUtil.mostrarMensajeAdvertencia("Validación", "Debe seleccionar un caso (expediente) primero");
            return;
        }

        if (txtf_DescripcionServicio.getText().isEmpty() || txtf_Tarifa.getText().isEmpty()) {
            DialogUtil.mostrarMensajeAdvertencia("Validación",
                    "Debe ingresar la descripción y tarifa del servicio asociado al caso");
            return;
        }

        try {
            // Validar que el caso pertenezca al cliente seleccionado
            try {
                CasoService casoService = new CasoService();
                Cliente cliente = new ClienteService().obtenerClientePorIdentificacion(txtf_IdCliente.getText());

                if (cliente != null && !txtf_NumExpediente.getText().isEmpty()) {
                    boolean pertenece = casoService.casoPertenecaACliente(txtf_NumExpediente.getText(),
                            cliente.getId());
                    if (!pertenece) {
                        DialogUtil.mostrarMensajeAdvertencia("Validación",
                                "El caso seleccionado no pertenece al cliente actual");
                        return;
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error al verificar relación cliente-caso", e);
            }

            // Obtener datos del formulario
            int codigo = 0;
            try {
                codigo = Integer
                        .parseInt(txtf_CodigoServicio.getText().isEmpty() ? "0" : txtf_CodigoServicio.getText());
            } catch (NumberFormatException e) {
                codigo = 0;
            }

            // Usar el número de expediente como código auxiliar si está vacío
            String codigoAuxiliar = txtf_CodigoAuxiliar.getText();
            if (codigoAuxiliar == null || codigoAuxiliar.isEmpty()) {
                codigoAuxiliar = txtf_NumExpediente.getText();
            }

            String descripcion = txtf_DescripcionServicio.getText();
            BigDecimal cantidad = new BigDecimal(txtf_Cantidad.getText().isEmpty() ? "1" : txtf_Cantidad.getText());
            BigDecimal tarifa = new BigDecimal(txtf_Tarifa.getText().isEmpty() ? "0" : txtf_Tarifa.getText());
            BigDecimal descuento = new BigDecimal(txtf_Descuento.getText().isEmpty() ? "0" : txtf_Descuento.getText());

            // Obtener el tipo de impuesto desde parámetros
            String tipoImpuesto = "IVA_0"; // Por defecto IVA 0%
            try {
                tipoImpuesto = parametroService.getValor("TIPO_IMPUESTO_DEFAULT", "IVA_0");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error al obtener tipo de impuesto por defecto, usando IVA 0%", e);
            }

            // Crear el detalle vinculado al caso
            FacturaDetalle detalle = new FacturaDetalle(codigo, descripcion, cantidad, tarifa, descuento);
            detalle.setTipoImpuesto(tipoImpuesto);
            detalle.setCodigoAuxiliar(codigoAuxiliar); // Asignar el número de expediente como código auxiliar

            // Calculamos y actualizamos el valor subtotal
            detalle.calcularSubtotal();

            // Agregar información adicional en descripción si es necesario
            if (!descripcion.contains(txtf_NumExpediente.getText())) {
                detalle.setDescripcion(descripcion + " (Exp: " + txtf_NumExpediente.getText() + ")");
            }

            // Agregar a la lista
            detallesFactura.add(detalle);

            // Actualizar totales
            actualizarTotales();

            // Limpiar campos de servicio pero mantener datos del caso
            limpiarCamposServicio();

        } catch (NumberFormatException e) {
            DialogUtil.mostrarMensajeError("Error", "Error al agregar servicio: " + e.getMessage());
        }
    }

    /**
     * Busca cliente por nombre o ID y muestra diálogo de selección
     */
    @FXML
    private void buscarClienteDialogo() {
        String termino = txtf_NombreCliente.getText();
        if (termino != null && !termino.trim().isEmpty()) {
            mostrarDialogoSeleccionCliente(termino);
        } else {
            termino = txtf_IdCliente.getText();
            if (termino != null && !termino.trim().isEmpty()) {
                mostrarDialogoSeleccionCliente(termino);
            } else {
                DialogUtil.mostrarMensajeAdvertencia("Búsqueda de Cliente",
                        "Por favor ingrese un nombre o identificación para buscar");
            }
        }
    }

    /**
     * Muestra un diálogo para seleccionar un cliente de los resultados de búsqueda
     */
    private void mostrarDialogoSeleccionCliente(String termino) {
        try {
            ClienteService clienteService = new ClienteService();
            List<Cliente> clientes = clienteService.buscarClientesPorTermino(termino);

            if (clientes.isEmpty()) {
                DialogUtil.mostrarMensajeInformacion("Búsqueda de Cliente",
                        "No se encontraron clientes con el término: " + termino);
                return;
            }

            // Crear diálogo
            Dialog<Cliente> dialog = new Dialog<>();
            dialog.setTitle("Seleccionar Cliente");
            dialog.setHeaderText("Seleccione un cliente de la lista");

            // Configurar botones
            ButtonType seleccionarButtonType = new ButtonType("Seleccionar", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelarButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(seleccionarButtonType, cancelarButtonType);

            // Crear tabla
            TableView<Cliente> tablaClientes = new TableView<>();
            tablaClientes.setPrefWidth(500);
            tablaClientes.setPrefHeight(300);

            // Configurar columnas
            TableColumn<Cliente, String> idCol = new TableColumn<>("Identificación");
            idCol.setCellValueFactory(
                    cellData -> new SimpleStringProperty(cellData.getValue().getNumeroIdentificacion()));
            idCol.setPrefWidth(120);

            TableColumn<Cliente, String> nombreCol = new TableColumn<>("Nombre");
            nombreCol
                    .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombreCompleto()));
            nombreCol.setPrefWidth(220);

            TableColumn<Cliente, String> telefonoCol = new TableColumn<>("Teléfono");
            telefonoCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTelefono()));
            telefonoCol.setPrefWidth(100);

            tablaClientes.getColumns().addAll(idCol, nombreCol, telefonoCol);
            tablaClientes.setItems(FXCollections.observableArrayList(clientes));

            // Configurar selección doble clic
            tablaClientes.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    Button okButton = (Button) dialog.getDialogPane().lookupButton(seleccionarButtonType);
                    okButton.fire();
                }
            });

            // Configurar contenido del diálogo
            dialog.getDialogPane().setContent(tablaClientes);

            // Configurar resultado
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == seleccionarButtonType) {
                    return tablaClientes.getSelectionModel().getSelectedItem();
                }
                return null;
            });

            // Mostrar diálogo y procesar resultado
            Optional<Cliente> resultado = dialog.showAndWait();
            resultado.ifPresent(this::actualizarDatosCliente);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al buscar clientes", e);
            DialogUtil.mostrarMensajeError("Error", "Error al buscar clientes: " + e.getMessage());
        }
    }

    /**
     * Actualiza los campos del formulario con los datos del cliente seleccionado
     */
    private void actualizarDatosCliente(Cliente cliente) {
        if (cliente != null) {
            txtf_IdCliente.setText(cliente.getNumeroIdentificacion());
            txtf_NombreCliente.setText(cliente.getNombreCompleto());
            txtf_DirCliente.setText(cliente.getDireccion());
            txtf_TelefonoCliente.setText(cliente.getTelefono());
            txtf_EmailCliente.setText(cliente.getCorreoElectronico());

            // Seleccionar el tipo de identificación correcto
            if (cliente.getNumeroIdentificacion() != null) {
                String tipoIdentificacion = "CEDULA";
                if (cliente.getNumeroIdentificacion().length() == 13) {
                    tipoIdentificacion = "RUC";
                } else if (cliente.getNumeroIdentificacion().length() < 10) {
                    tipoIdentificacion = "PASAPORTE";
                }
                cbx_TipoIdCliente.getSelectionModel().select(tipoIdentificacion);
            }
        }
    }

    /**
     * Busca casos por número o nombre y muestra diálogo de selección
     */
    @FXML
    private void buscarCasoDialogo() {
        String termino = txtf_NumExpediente.getText();
        if (termino == null || termino.trim().isEmpty()) {
            termino = txtf_NombreCaso.getText();
        }

        if (termino != null && !termino.trim().isEmpty()) {
            mostrarDialogoSeleccionCaso(termino);
        } else {
            DialogUtil.mostrarMensajeAdvertencia("Búsqueda de Caso",
                    "Por favor ingrese un número de expediente o nombre de caso para buscar");
        }
    }

    /**
     * Muestra un diálogo para seleccionar un caso de los resultados de búsqueda
     */
    private void mostrarDialogoSeleccionCaso(String termino) {
        try {
            CasoService casoService = new CasoService();
            List<Caso> casos = casoService.buscarCasosPorTermino(termino);

            if (casos.isEmpty()) {
                DialogUtil.mostrarMensajeInformacion("Búsqueda de Caso",
                        "No se encontraron casos con el término: " + termino);
                return;
            }

            // Crear diálogo
            Dialog<Caso> dialog = new Dialog<>();
            dialog.setTitle("Seleccionar Caso");
            dialog.setHeaderText("Seleccione un caso de la lista");

            // Configurar botones
            ButtonType seleccionarButtonType = new ButtonType("Seleccionar", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelarButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(seleccionarButtonType, cancelarButtonType);

            // Crear tabla
            TableView<Caso> tablaCasos = new TableView<>();
            tablaCasos.setPrefWidth(500);
            tablaCasos.setPrefHeight(300);

            // Configurar columnas
            TableColumn<Caso, String> numeroCol = new TableColumn<>("Número");
            numeroCol.setCellValueFactory(
                    cellData -> new SimpleStringProperty(cellData.getValue().getNumeroExpediente()));
            numeroCol.setPrefWidth(100);

            TableColumn<Caso, String> tituloCol = new TableColumn<>("Título");
            tituloCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitulo()));
            tituloCol.setPrefWidth(220);

            TableColumn<Caso, String> clienteCol = new TableColumn<>("Cliente");
            clienteCol.setCellValueFactory(cellData -> {
                Cliente cliente = cellData.getValue().getCliente();
                return new SimpleStringProperty(cliente != null ? cliente.getNombreCompleto() : "");
            });
            clienteCol.setPrefWidth(180);

            tablaCasos.getColumns().addAll(numeroCol, tituloCol, clienteCol);
            tablaCasos.setItems(FXCollections.observableArrayList(casos));

            // Configurar selección doble clic
            tablaCasos.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    Button okButton = (Button) dialog.getDialogPane().lookupButton(seleccionarButtonType);
                    okButton.fire();
                }
            });

            // Configurar contenido del diálogo
            dialog.getDialogPane().setContent(tablaCasos);

            // Configurar resultado
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == seleccionarButtonType) {
                    return tablaCasos.getSelectionModel().getSelectedItem();
                }
                return null;
            });

            // Mostrar diálogo y procesar resultado
            Optional<Caso> resultado = dialog.showAndWait();
            resultado.ifPresent(this::actualizarDatosCaso);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al buscar casos", e);
            DialogUtil.mostrarMensajeError("Error", "Error al buscar casos: " + e.getMessage());
        }
    }

    /**
     * Actualiza los campos del formulario con los datos del caso seleccionado
     */
    private void actualizarDatosCaso(Caso caso) {
        if (caso != null) {
            // Si el caso tiene cliente y no se ha seleccionado un cliente, mostrar sus
            // datos primero
            if (caso.getCliente() != null && (txtf_IdCliente.getText() == null || txtf_IdCliente.getText().isEmpty())) {
                actualizarDatosCliente(caso.getCliente());
            }

            // Verificar que el caso pertenezca al cliente seleccionado
            if (txtf_IdCliente.getText() != null && !txtf_IdCliente.getText().isEmpty()) {
                try {
                    CasoService casoService = new CasoService();
                    Cliente cliente = new ClienteService().obtenerClientePorIdentificacion(txtf_IdCliente.getText());

                    if (cliente != null) {
                        // Si el cliente del caso y el cliente seleccionado son los mismos, no
                        // necesitamos validar más
                        if (caso.getCliente() != null && caso.getCliente().getId() == cliente.getId()) {
                            // Son el mismo cliente, continuar
                        } else {
                            boolean pertenece = casoService.casoPertenecaACliente(caso.getNumeroExpediente(),
                                    cliente.getId());
                            if (!pertenece) {
                                DialogUtil.mostrarMensajeAdvertencia("Validación",
                                        "El caso seleccionado no pertenece al cliente actual");
                                return;
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error al verificar relación cliente-caso", e);
                    // Si hay error en la validación, permitimos continuar
                }
            }

            txtf_NumExpediente.setText(caso.getNumeroExpediente());
            txtf_NombreCaso.setText(caso.getTitulo());

            // Obtener información del abogado si está disponible
            List<AbogadoCaso> abogadosDelCaso = caso.getAbogados();
            if (abogadosDelCaso != null && !abogadosDelCaso.isEmpty()) {
                AbogadoCaso primerAbogado = abogadosDelCaso.get(0);
                txtf_Abogado.setText(primerAbogado.getNombre());
            } else {
                txtf_Abogado.setText("No especificado");
            }

            // Actualizar campos del servicio con datos del caso
            try {
                int casoId = caso.getId();
                txtf_CodigoServicio.setText(String.valueOf(casoId));
                txtf_CodigoAuxiliar.setText(caso.getNumeroExpediente());

                // Generar descripción del servicio basada en el caso
                String descripcionServicio = "Servicios jurídicos - " + caso.getTitulo() + " (Exp: "
                        + caso.getNumeroExpediente() + ")";
                txtf_DescripcionServicio.setText(descripcionServicio);

                // Establecer valores predeterminados para el servicio
                txtf_Cantidad.setText("1");

                // Mostrar mensaje indicando que se ha vinculado el caso con el servicio
                DialogUtil.mostrarMensajeInformacion("Caso seleccionado",
                        "Se ha vinculado el caso al servicio. Complete los detalles del precio y cantidad para agregar a la factura.");

            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error al configurar datos del servicio", e);
                txtf_CodigoServicio.setText("0");
            }

            // Usar el título del caso como descripción del servicio
            String descripcionServicio = "Servicios legales: " + caso.getTitulo();
            if (caso.getTipo() != null && !caso.getTipo().isEmpty()) {
                descripcionServicio += " (" + caso.getTipo() + ")";
            }
            txtf_DescripcionServicio.setText(descripcionServicio);

            // Sugerir tarifa si está vacía (opcional, se puede ajustar)
            if (txtf_Tarifa.getText() == null || txtf_Tarifa.getText().isEmpty() || "0".equals(txtf_Tarifa.getText())) {
                txtf_Tarifa.setText("100.00"); // Valor predeterminado para nuevos servicios
                calcularSubtotalServicio(); // Actualizar subtotal
            }

            // Si el caso tiene abogados asignados, mostrar el nombre del primero
            List<AbogadoCaso> abogados = caso.getAbogados();
            if (abogados != null && !abogados.isEmpty()) {
                AbogadoCaso abogadoPrincipal = abogados.stream()
                        .filter(a -> "PRINCIPAL".equalsIgnoreCase(a.getRol()))
                        .findFirst()
                        .orElse(abogados.get(0));

                txtf_Abogado.setText(abogadoPrincipal.getNombre());
            }
        }
    }

    /**
     * Elimina un servicio de la factura
     */
    @FXML
    private void eliminarServicio() {
        FacturaDetalle detalle = tbl_Productos.getSelectionModel().getSelectedItem();
        if (detalle != null) {
            detallesFactura.remove(detalle);
            actualizarTotales();
            limpiarCamposServicio();
        } else {
            DialogUtil.mostrarMensajeAdvertencia("Selección", "Debe seleccionar un servicio para eliminar");
        }
    }

    /**
     * Limpia los campos del servicio manteniendo los datos del caso seleccionado
     */
    private void limpiarCamposServicio() {
        txtf_CodigoServicio.setText("");
        txtf_DescripcionServicio.setText("");
        // No limpiamos el código auxiliar que puede contener el número de expediente
        // txtf_CodigoAuxiliar.setText("");
        txtf_Cantidad.setText("1");
        txtf_Tarifa.setText("0");
        txtf_Descuento.setText("0");
        txtf_SubtotalServicio.setText("0.00");
    }

    /**
     * Actualiza los totales de la factura
     */
    private void actualizarTotales() {
        BigDecimal subtotal12 = BigDecimal.ZERO;
        BigDecimal subtotal0 = BigDecimal.ZERO;
        BigDecimal subtotalNoObjetoIva = BigDecimal.ZERO;
        BigDecimal subtotalExentoIva = BigDecimal.ZERO;
        BigDecimal totalDescuento = BigDecimal.ZERO;

        for (FacturaDetalle detalle : detallesFactura) {
            BigDecimal subtotalDetalle = detalle.calcularSubtotal();

            // Acumular por tipo de impuesto
            switch (detalle.getTipoImpuesto()) {
                case "IVA_12":
                    subtotal12 = subtotal12.add(subtotalDetalle);
                    break;
                case "IVA_0":
                    subtotal0 = subtotal0.add(subtotalDetalle);
                    break;
                case "NO_OBJETO_IVA":
                    subtotalNoObjetoIva = subtotalNoObjetoIva.add(subtotalDetalle);
                    break;
                case "EXENTO_IVA":
                    subtotalExentoIva = subtotalExentoIva.add(subtotalDetalle);
                    break;
            }

            totalDescuento = totalDescuento.add(detalle.getDescuento());
        }

        // Actualizar campos
        txtf_Subtotal12.setText(subtotal12.setScale(2, RoundingMode.HALF_UP).toString());
        txtf_Subtotal0.setText(subtotal0.setScale(2, RoundingMode.HALF_UP).toString());
        txtf_SubtotalNoObjetoIva.setText(subtotalNoObjetoIva.setScale(2, RoundingMode.HALF_UP).toString());
        txtf_SubtotalExentoIva.setText(subtotalExentoIva.setScale(2, RoundingMode.HALF_UP).toString());
        txtf_TotalDescuento.setText(totalDescuento.setScale(2, RoundingMode.HALF_UP).toString());

        // Subtotal sin impuestos
        BigDecimal subtotalSinImpuestos = subtotal12.add(subtotal0).add(subtotalNoObjetoIva).add(subtotalExentoIva);

        // Obtener porcentaje del subtotal para mostrar en la etiqueta
        BigDecimal porcentajeSubtotal = parametroService.getValorBigDecimal("subtotal_porcentaje",
                new BigDecimal("12"));
        System.out.println("Porcentaje Subtotal cargado: " + porcentajeSubtotal);

        // Actualizar el texto del campo de Subtotal para mostrar también el porcentaje
        txtf_Subtotal.setText(subtotalSinImpuestos.setScale(2, RoundingMode.HALF_UP).toString());

        // Buscar el Label correspondiente a Subtotal (12%) y actualizarlo
        Label labelSubtotal = (Label) txtf_Subtotal.getParent().getChildrenUnmodifiable().stream()
                .filter(node -> node instanceof Label && ((Label) node).getText().contains("Subtotal"))
                .findFirst().orElse(null);

        if (labelSubtotal != null) {
            labelSubtotal.setText("Subtotal (" + porcentajeSubtotal.intValue() + "%):");
            System.out.println(
                    "Etiqueta de subtotal actualizada con: " + "Subtotal (" + porcentajeSubtotal.intValue() + "%):");
        } else {
            System.out.println("No se encontró la etiqueta de subtotal para actualizar");
        }

        // Calcular IVA
        BigDecimal porcentajeIva = parametroService.getValorBigDecimal("porcentaje_iva", new BigDecimal("12"));
        System.out.println("Porcentaje IVA cargado: " + porcentajeIva);
        BigDecimal valorIva = subtotal12.multiply(porcentajeIva.divide(new BigDecimal("100")));

        // Actualizar el texto del campo de IVA para mostrar el valor calculado
        txtf_Iva.setText(valorIva.setScale(2, RoundingMode.HALF_UP).toString());

        // Buscar el Label correspondiente a IVA y actualizarlo para mostrar el
        // porcentaje
        Label labelIva = (Label) txtf_Iva.getParent().getChildrenUnmodifiable().stream()
                .filter(node -> node instanceof Label && ((Label) node).getText().contains("IVA"))
                .findFirst().orElse(null);

        if (labelIva != null) {
            labelIva.setText("IVA (" + porcentajeIva.intValue() + "%):");
            System.out.println("Etiqueta de IVA actualizada con: " + "IVA (" + porcentajeIva.intValue() + "%):");
        } else {
            System.out.println("No se encontró la etiqueta de IVA para actualizar");
        }

        // Propina
        BigDecimal propina = BigDecimal.ZERO;
        try {
            propina = new BigDecimal(txtf_Propina.getText().isEmpty() ? "0" : txtf_Propina.getText());
        } catch (NumberFormatException e) {
            propina = BigDecimal.ZERO;
        }

        // Total
        BigDecimal valorTotal = subtotalSinImpuestos.add(valorIva).add(propina);
        txtf_TotalFactura.setText(valorTotal.setScale(2, RoundingMode.HALF_UP).toString());

        // Recalcular subsidio
        calcularSubsidio();
    }

    /**
     * Abre el buscador de clientes
     */
    private void buscarCliente() {
        String termino = txtf_NombreCliente.getText().trim();
        if (termino.isEmpty() || termino.length() < 3) {
            DialogUtil.mostrarMensajeAdvertencia("Validación",
                    "Ingrese al menos 3 caracteres para buscar el cliente");
            return;
        }

        ClienteService clienteService = new ClienteService();
        List<Cliente> clientesEncontrados = clienteService.buscarClientesPorTermino(termino);

        if (clientesEncontrados.isEmpty()) {
            DialogUtil.mostrarMensajeInformacion("Búsqueda", "No se encontraron clientes con ese término");
            return;
        }

        // Si solo hay un cliente, seleccionarlo automáticamente
        if (clientesEncontrados.size() == 1) {
            seleccionarCliente(clientesEncontrados.get(0));
            return;
        }

        // Si hay varios, mostrar diálogo para seleccionar
        Cliente clienteSeleccionado = mostrarDialogoSeleccionCliente(clientesEncontrados);
        if (clienteSeleccionado != null) {
            seleccionarCliente(clienteSeleccionado);
        }
    }

    /**
     * Muestra un diálogo para seleccionar un cliente de la lista
     */
    private Cliente mostrarDialogoSeleccionCliente(List<Cliente> clientes) {
        // Aquí normalmente se mostraría un diálogo personalizado
        // Como solución rápida, mostraremos un diálogo de selección simple
        ChoiceDialog<Cliente> dialog = new ChoiceDialog<>(clientes.get(0), clientes);
        dialog.setTitle("Seleccionar Cliente");
        dialog.setHeaderText("Seleccione un cliente de la lista");
        dialog.setContentText("Cliente:");

        // Personalizar la visualización de los clientes en el diálogo
        dialog.getItems().setAll(clientes);

        // Configurar el conversor para mostrar el nombre y número de identificación
        dialog.getDialogPane().setGraphic(null);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return dialog.getSelectedItem();
            }
            return null;
        });

        Optional<Cliente> resultado = dialog.showAndWait();
        return resultado.orElse(null);
    }

    /**
     * Selecciona un cliente y rellena los campos correspondientes
     */
    private void seleccionarCliente(Cliente cliente) {
        txtf_NombreCliente.setText(cliente.getNombreCompleto());
        txtf_IdCliente.setText(cliente.getNumeroIdentificacion());
        cbx_TipoIdCliente.getSelectionModel().select(cliente.getTipoIdentificacion().name());
        txtf_DirCliente.setText(cliente.getDireccion());
        txtf_TelefonoCliente.setText(cliente.getTelefono());
        txtf_EmailCliente.setText(cliente.getCorreoElectronico());
    }

    /**
     * Abre el buscador de casos
     */
    private void buscarCaso() {
        // Implementar búsqueda de casos
        DialogUtil.mostrarMensajeInformacion("Búsqueda de casos",
                "Esta funcionalidad será implementada próximamente");
    }

    /**
     * Guarda la factura
     */
    private void guardarFactura() {
        // Validar campos obligatorios
        if (!validarCamposObligatorios()) {
            return;
        }

        try {
            // Crear la factura desde el formulario
            Factura factura = crearFacturaDesdeFormulario();

            // Guardar la factura
            boolean resultado = facturaService.guardarFactura(factura);

            if (resultado) {
                DialogUtil.mostrarMensajeInformacion("Éxito", "Factura guardada correctamente");

                if (onGuardar != null) {
                    onGuardar.run();
                }
            } else {
                DialogUtil.mostrarMensajeError("Error", "No se pudo guardar la factura");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar factura", e);
            DialogUtil.mostrarMensajeError("Error", "Error al guardar la factura: " + e.getMessage());
        }
    }

    /**
     * Valida los campos obligatorios
     */
    private boolean validarCamposObligatorios() {
        if (txtf_NombreCliente.getText().trim().isEmpty()) {
            DialogUtil.mostrarMensajeAdvertencia("Validación", "Debe ingresar el nombre del cliente");
            txtf_NombreCliente.requestFocus();
            return false;
        }

        if (txtf_IdCliente.getText().trim().isEmpty()) {
            DialogUtil.mostrarMensajeAdvertencia("Validación", "Debe ingresar el ID del cliente");
            txtf_IdCliente.requestFocus();
            return false;
        }

        if (detallesFactura.isEmpty()) {
            DialogUtil.mostrarMensajeAdvertencia("Validación", "Debe agregar al menos un servicio a la factura");
            txtf_DescripcionServicio.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Crea una factura desde los datos del formulario
     */
    private Factura crearFacturaDesdeFormulario() {
        Factura factura = new Factura();

        // Datos del emisor
        factura.setTipoDocumento(cbx_TipoDocumento.getSelectionModel().getSelectedItem());
        factura.setRucEmisor(txtf_RucEmisor.getText());
        factura.setRazonSocialEmisor(txtf_RazonSocialEmisor.getText());
        factura.setDireccionMatriz(txtf_DireccionMatriz.getText());
        factura.setDireccionSucursal(txtf_DireccionSucursal.getText());
        factura.setObligadoContabilidad("SI".equals(cbx_ObligadoContabilidad.getSelectionModel().getSelectedItem()));

        // Datos del documento
        factura.setCodigoEstablecimiento(txtf_CodEstablecimiento.getText());
        factura.setCodigoPuntoEmision(txtf_CodPuntoEmision.getText());
        factura.setSecuencial(txtf_Secuencial.getText());
        factura.setCodigoDocumento(txtf_CodigoDocumento.getText());
        factura.setFechaEmision(dtp_FechaEmision.getValue());
        factura.setAmbiente(cbx_Ambiente.getSelectionModel().getSelectedItem().equals("PRUEBAS") ? "1" : "2");
        factura.setEmision(cbx_Emision.getSelectionModel().getSelectedItem().equals("NORMAL") ? "1" : "2");

        // Datos del cliente
        factura.setNombreCliente(txtf_NombreCliente.getText());
        factura.setIdCliente(txtf_IdCliente.getText());
        factura.setTipoIdentificacion(cbx_TipoIdCliente.getSelectionModel().getSelectedItem());
        factura.setDireccionCliente(txtf_DirCliente.getText());
        factura.setTelefonoCliente(txtf_TelefonoCliente.getText());
        factura.setEmailCliente(txtf_EmailCliente.getText());

        // Datos del caso
        factura.setNumeroExpediente(txtf_NumExpediente.getText());
        factura.setNombreCaso(txtf_NombreCaso.getText());
        factura.setAbogadoResponsable(txtf_Abogado.getText());

        // Totales
        try {
            factura.setSubtotal12(new BigDecimal(txtf_Subtotal12.getText()));
            factura.setSubtotal0(new BigDecimal(txtf_Subtotal0.getText()));
            factura.setSubtotalNoObjetoIva(new BigDecimal(txtf_SubtotalNoObjetoIva.getText()));
            factura.setSubtotalExentoIva(new BigDecimal(txtf_SubtotalExentoIva.getText()));
            factura.setTotalDescuento(new BigDecimal(txtf_TotalDescuento.getText()));
            factura.setSubtotalSinImpuestos(new BigDecimal(txtf_Subtotal.getText()));
            factura.setValorIva(new BigDecimal(txtf_Iva.getText()));
            // Usar nombre de parámetro en minúsculas consistentemente
            factura.setPorcentajeIva(parametroService.getValorBigDecimal("porcentaje_iva", new BigDecimal("12")));

            if (!txtf_Propina.getText().isEmpty()) {
                factura.setPropina(new BigDecimal(txtf_Propina.getText()));
            }

            factura.setValorTotal(new BigDecimal(txtf_TotalFactura.getText()));

            if (!txtf_ValorSinSubsidio.getText().isEmpty()) {
                factura.setValorSinSubsidio(new BigDecimal(txtf_ValorSinSubsidio.getText()));
            }

            if (!txtf_AhorroSubsidio.getText().isEmpty()) {
                factura.setAhorroSubsidio(new BigDecimal(txtf_AhorroSubsidio.getText()));
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Error al convertir valores numéricos", e);
        }

        // Pago
        factura.setFormaPago(cbx_FormaPago.getSelectionModel().getSelectedItem());

        if (!txtf_MontoPago.getText().isEmpty()) {
            try {
                factura.setMontoPago(new BigDecimal(txtf_MontoPago.getText()));
            } catch (NumberFormatException e) {
                factura.setMontoPago(BigDecimal.ZERO);
            }
        }

        if (!txtf_Plazo.getText().isEmpty()) {
            try {
                factura.setPlazo(Integer.parseInt(txtf_Plazo.getText()));
            } catch (NumberFormatException e) {
                factura.setPlazo(0);
            }
        }

        factura.setEstadoFactura(cbx_EstadoFactura.getSelectionModel().getSelectedItem());
        factura.setPagoRealizado(chk_PagoRealizado.isSelected());

        // Detalles
        for (FacturaDetalle detalle : detallesFactura) {
            // Aseguramos que el valor subtotal esté correctamente calculado
            detalle.calcularSubtotal();
            factura.agregarDetalle(detalle);
        }

        // Auditoría
        factura.setUsuarioCreacion("SISTEMA");
        factura.setUsuarioModificacion("SISTEMA");

        return factura;
    }

    /**
     * Carga una factura existente en el formulario
     */
    public void cargarFactura(Factura factura) {
        if (factura == null) {
            return;
        }

        this.factura = factura;
        this.modoEdicion = true;
        txt_TituloForm.setText("Editar Factura");

        // Cargar datos del emisor
        txtf_RucEmisor.setText(factura.getRucEmisor());
        txtf_RazonSocialEmisor.setText(factura.getRazonSocialEmisor());
        txtf_DireccionMatriz.setText(factura.getDireccionMatriz());
        txtf_DireccionSucursal.setText(factura.getDireccionSucursal());
        txtf_CodEstablecimiento.setText(factura.getCodigoEstablecimiento());
        txtf_CodPuntoEmision.setText(factura.getCodigoPuntoEmision());
        txtf_Secuencial.setText(factura.getSecuencial());
        txtf_CodigoDocumento.setText(factura.getCodigoDocumento());
        dtp_FechaEmision.setValue(factura.getFechaEmision());

        // Datos del documento
        cbx_TipoDocumento.getSelectionModel().select(factura.getTipoDocumento());
        cbx_Ambiente.getSelectionModel().select(factura.getAmbiente().equals("1") ? "PRUEBAS" : "PRODUCCIÓN");
        cbx_Emision.getSelectionModel().select("NORMAL");
        cbx_ObligadoContabilidad.getSelectionModel().select(factura.isObligadoContabilidad() ? "SI" : "NO");

        // Cliente
        txtf_NombreCliente.setText(factura.getNombreCliente());
        txtf_IdCliente.setText(factura.getIdCliente());
        cbx_TipoIdCliente.getSelectionModel().select(factura.getTipoIdentificacion());
        txtf_DirCliente.setText(factura.getDireccionCliente());
        txtf_TelefonoCliente.setText(factura.getTelefonoCliente());
        txtf_EmailCliente.setText(factura.getEmailCliente());

        // Caso
        txtf_NumExpediente.setText(factura.getNumeroExpediente());
        txtf_NombreCaso.setText(factura.getNombreCaso());
        txtf_Abogado.setText(factura.getAbogadoResponsable());

        // Pago
        cbx_FormaPago.getSelectionModel().select(factura.getFormaPago());
        txtf_MontoPago.setText(factura.getMontoPago().toString());
        txtf_Plazo.setText(String.valueOf(factura.getPlazo()));
        cbx_EstadoFactura.getSelectionModel().select(factura.getEstadoFactura());
        chk_PagoRealizado.setSelected(factura.isPagoRealizado());

        // Totales
        txtf_Subtotal12.setText(factura.getSubtotal12().toString());
        txtf_Subtotal0.setText(factura.getSubtotal0().toString());
        txtf_SubtotalNoObjetoIva.setText(factura.getSubtotalNoObjetoIva().toString());
        txtf_SubtotalExentoIva.setText(factura.getSubtotalExentoIva().toString());
        txtf_TotalDescuento.setText(factura.getTotalDescuento().toString());
        txtf_Subtotal.setText(factura.getSubtotalSinImpuestos().toString());
        txtf_Iva.setText(factura.getValorIva().toString());
        txtf_Propina.setText(factura.getPropina().toString());
        txtf_TotalFactura.setText(factura.getValorTotal().toString());

        if (factura.getValorSinSubsidio() != null) {
            txtf_ValorSinSubsidio.setText(factura.getValorSinSubsidio().toString());
        }

        if (factura.getAhorroSubsidio() != null) {
            txtf_AhorroSubsidio.setText(factura.getAhorroSubsidio().toString());
        }

        // Detalles
        detallesFactura.clear();
        if (factura.getDetalles() != null) {
            detallesFactura.addAll(factura.getDetalles());
        }
    }

    /**
     * Cancela la operación
     */
    private void cancelar() {
        if (onCancelar != null) {
            onCancelar.run();
        }
    }
}
