package application.controllers.facturacion;

import application.controllers.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class FormFacturaController {

    @FXML private Button btn_Guardar, btn_Cancelar;

    // Datos del Emisor
    @FXML private TextField txtf_RucEmisor, txtf_RazonSocialEmisor, txtf_DireccionEmisor,
            txtf_CodEstablecimiento, txtf_CodPuntoEmision, txtf_Secuencial,
            txtf_CodigoDocumento;
    @FXML private DatePicker dtp_FechaEmision;

    // Cliente
    @FXML private TextField txtf_NombreCliente, txtf_IdCliente, txtf_DirCliente, txtf_EmailCliente;
    @FXML private ComboBox<String> cbx_TipoIdCliente;

    // Servicios
    @FXML private TextField txtf_CodigoServicio, txtf_DescripcionServicio, txtf_Cantidad,
            txtf_Tarifa, txtf_Descuento, txtf_SubtotalServicio;

    // Caso
    @FXML private TextField txtf_NumExpediente, txtf_NombreCaso, txtf_Abogado;

    // Totales
    @FXML private TextField txtf_Subtotal, txtf_TotalDescuento, txtf_Iva, txtf_TotalFactura;

    // Pago
    @FXML private ComboBox<String> cbx_FormaPago, cbx_EstadoFactura;
    @FXML private TextField txtf_MontoPago, txtf_Plazo;
    @FXML private CheckBox chk_PagoRealizado;

    @FXML private Text txt_TituloForm;

    private Runnable onGuardar, onCancelar;

    public void setOnGuardar(Runnable handler) {
        this.onGuardar = handler;
    }

    public void setOnCancelar(Runnable handler) {
        this.onCancelar = handler;
    }

    @FXML
    private void initialize() {
        cbx_TipoIdCliente.getItems().addAll("Cédula", "RUC", "Pasaporte");
        cbx_FormaPago.getItems().addAll("Efectivo", "Transferencia", "Tarjeta");
        cbx_EstadoFactura.getItems().addAll("Abierto", "Registrado", "Rechazado");

        btn_Guardar.setOnAction(e -> {
            if (dtp_FechaEmision.getValue() == null ||
                    txtf_RucEmisor.getText().isEmpty() ||
                    txtf_NombreCliente.getText().isEmpty() ||
                    cbx_TipoIdCliente.getValue() == null ||
                    txtf_CodigoServicio.getText().isEmpty() ||
                    txtf_Cantidad.getText().isEmpty() ||
                    txtf_Tarifa.getText().isEmpty() ||
                    cbx_FormaPago.getValue() == null ||
                    cbx_EstadoFactura.getValue() == null) {

                DialogUtil.mostrarDialogo(
                        "Campos requeridos",
                        "Por favor, complete los campos obligatorios:\n" +
                                " - Fecha de Emisión\n" +
                                " - RUC del Emisor\n" +
                                " - Nombre del Cliente\n" +
                                " - Tipo de Identificación del Cliente\n" +
                                " - Código del Servicio\n" +
                                " - Cantidad\n" +
                                " - Tarifa\n" +
                                " - Forma de Pago\n" +
                                " - Estado de la Factura",
                        "warning",
                        List.of(ButtonType.OK)
                );
                return;
            }

            Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                    "Confirmación",
                    "¿Está seguro que desea guardar esta factura?",
                    "confirm",
                    List.of(ButtonType.YES, ButtonType.NO)
            );

            if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
                if (onGuardar != null) onGuardar.run();
            }
        });

        btn_Cancelar.setOnAction(e -> {
            Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                    "Confirmación",
                    "¿Está seguro que desea cancelar el formulario?\nSe perderán los cambios no guardados.",
                    "confirm",
                    List.of(ButtonType.YES, ButtonType.NO)
            );

            if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
                if (onCancelar != null) onCancelar.run();
            }
        });

    }

    public void setModo(String modo) {
        boolean esEditar = "EDITAR".equalsIgnoreCase(modo);
        boolean esVer = "VER".equalsIgnoreCase(modo);
        boolean esRegistrar = !esEditar && !esVer;

        txt_TituloForm.setText(switch (modo.toUpperCase()) {
            case "EDITAR" -> "Editar Factura";
            case "VER" -> "Ver Factura";
            default -> "Registrar nueva Factura";
        });

        boolean editable = esRegistrar || (esEditar && cbx_EstadoFactura.getValue() != null && cbx_EstadoFactura.getValue().equals("Abierto"));

        setEditableCampos(editable);
        btn_Guardar.setDisable(esVer || !editable);
    }

    private void setEditableCampos(boolean editable) {
        txtf_RucEmisor.setEditable(editable);
        txtf_RazonSocialEmisor.setEditable(editable);
        txtf_DireccionEmisor.setEditable(editable);
        txtf_CodEstablecimiento.setEditable(editable);
        txtf_CodPuntoEmision.setEditable(editable);
        txtf_Secuencial.setEditable(editable);
        txtf_CodigoDocumento.setEditable(editable);
        dtp_FechaEmision.setDisable(!editable);

        txtf_NombreCliente.setEditable(editable);
        txtf_IdCliente.setEditable(editable);
        txtf_DirCliente.setEditable(editable);
        txtf_EmailCliente.setEditable(editable);
        cbx_TipoIdCliente.setDisable(!editable);

        txtf_CodigoServicio.setEditable(editable);
        txtf_DescripcionServicio.setEditable(editable);
        txtf_Cantidad.setEditable(editable);
        txtf_Tarifa.setEditable(editable);
        txtf_Descuento.setEditable(editable);
        txtf_SubtotalServicio.setEditable(false);

        txtf_NumExpediente.setEditable(editable);
        txtf_NombreCaso.setEditable(editable);
        txtf_Abogado.setEditable(editable);

        txtf_Subtotal.setEditable(false);
        txtf_TotalDescuento.setEditable(false);
        txtf_Iva.setEditable(false);
        txtf_TotalFactura.setEditable(false);

        cbx_FormaPago.setDisable(!editable);
        txtf_MontoPago.setEditable(editable);
        txtf_Plazo.setEditable(editable);
        cbx_EstadoFactura.setDisable(!editable);
        chk_PagoRealizado.setDisable(!editable);
    }

    public void cargarFactura(ModuloFacturaController.FacturaDemo factura) {
        txtf_RucEmisor.setText(factura.rucEmisor());
        txtf_RazonSocialEmisor.setText(factura.razonSocialEmisor());
        txtf_DireccionEmisor.setText(factura.direccionEmisor());
        txtf_CodEstablecimiento.setText(factura.codigoEstablecimiento());
        txtf_CodPuntoEmision.setText(factura.codigoPuntoEmision());
        txtf_Secuencial.setText(factura.numeroFactura());
        txtf_CodigoDocumento.setText(factura.codigoDocumento());
        dtp_FechaEmision.setValue(LocalDate.parse(factura.fechaEmision()));

        txtf_NombreCliente.setText(factura.nombreCliente());
        txtf_IdCliente.setText(factura.idCliente());
        txtf_DirCliente.setText(factura.dirCliente());
        txtf_EmailCliente.setText(factura.emailCliente());
        cbx_TipoIdCliente.setValue(factura.tipoIdCliente());

        txtf_CodigoServicio.setText(factura.codigoServicio());
        txtf_DescripcionServicio.setText(factura.descripcionServicio());
        txtf_Cantidad.setText(factura.cantidad());
        txtf_Tarifa.setText(factura.tarifa());
        txtf_Descuento.setText(factura.descuento());
        txtf_SubtotalServicio.setText(factura.subtotalServicio());

        txtf_NumExpediente.setText(factura.numeroExpediente());
        txtf_NombreCaso.setText(factura.nombreCaso());
        txtf_Abogado.setText(factura.abogadoResponsable());

        txtf_Subtotal.setText(factura.subtotal());
        txtf_TotalDescuento.setText(factura.totalDescuento());
        txtf_Iva.setText(factura.iva());
        txtf_TotalFactura.setText(factura.totalFactura());

        cbx_FormaPago.setValue(factura.formaPago());
        txtf_MontoPago.setText(factura.montoPago());
        txtf_Plazo.setText(factura.plazo());
        cbx_EstadoFactura.setValue(factura.estadoFactura());
        chk_PagoRealizado.setSelected(factura.pagoRealizado());
    }
}
