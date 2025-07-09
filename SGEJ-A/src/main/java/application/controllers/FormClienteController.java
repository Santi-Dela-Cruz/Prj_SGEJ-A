package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import java.util.List;
import java.util.Optional;

public class FormClienteController {

    @FXML private Button btn_Guardar, btn_Cancelar;
    @FXML private TextField txtf_Nombres, txtf_NumeroIdentificacion, txtf_Direccion, txtf_Correo, txtf_Telefono;
    @FXML private ComboBox<String> cbx_TipoCliente, cbx_TipoIdentificacion, cbx_Estado;
    @FXML private DatePicker dt_FechaIngreso;
    @FXML private Text txt_TituloForm;

    // Campos condicionales
    @FXML private ComboBox<String> cbx_EstadoCivil;
    @FXML private TextField txtf_RepresentanteLegal, txtf_DireccionFiscal;
    @FXML private Label lbl_EstadoCivil, lbl_RepresentanteLegal, lbl_DireccionFiscal;

    private Runnable onGuardar, onCancelar;

    public void setOnGuardar(Runnable handler) {
        this.onGuardar = handler;
    }

    public void setOnCancelar(Runnable handler) {
        this.onCancelar = handler;
    }

    @FXML
    private void initialize() {
        cbx_Estado.getItems().addAll("Activo", "Inactivo");
        cbx_TipoIdentificacion.getItems().addAll("Cédula", "RUC", "Pasaporte");
        cbx_TipoCliente.getItems().addAll("Natural", "Jurídica");
        cbx_EstadoCivil.getItems().addAll("Soltero/a", "Casado/a", "Divorciado/a", "Viudo/a");

        cbx_TipoCliente.setOnAction(e -> actualizarCamposCondicionales());
        ocultarCamposCondicionales();

        btn_Guardar.setOnAction(e -> {
            if (txtf_Nombres.getText().isEmpty()) {
                DialogUtil.mostrarDialogo(
                        "Campos requeridos",
                        "Por favor, complete los campos obligatorios: \n - Nombres Completo \n - Tipo de Identificación \n - Número de Identificación",
                        "warning",
                        List.of(ButtonType.OK)
                );
                return;
            }

            Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                    "Confirmación",
                    "¿Está seguro que desea guardar este cliente?",
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

    private void actualizarCamposCondicionales() {
        String tipo = cbx_TipoCliente.getValue();
        boolean esNatural = "Natural".equalsIgnoreCase(tipo);
        boolean esJuridica = "Jurídica".equalsIgnoreCase(tipo);

        // Natural
        lbl_EstadoCivil.setVisible(esNatural);
        cbx_EstadoCivil.setVisible(esNatural);
        cbx_EstadoCivil.setManaged(esNatural);

        // Jurídica
        lbl_RepresentanteLegal.setVisible(esJuridica);
        txtf_RepresentanteLegal.setVisible(esJuridica);
        txtf_RepresentanteLegal.setManaged(esJuridica);

        lbl_DireccionFiscal.setVisible(esJuridica);
        txtf_DireccionFiscal.setVisible(esJuridica);
        txtf_DireccionFiscal.setManaged(esJuridica);
    }

    private void ocultarCamposCondicionales() {
        lbl_EstadoCivil.setVisible(false);
        cbx_EstadoCivil.setVisible(false);
        cbx_EstadoCivil.setManaged(false);

        lbl_RepresentanteLegal.setVisible(false);
        txtf_RepresentanteLegal.setVisible(false);
        txtf_RepresentanteLegal.setManaged(false);

        lbl_DireccionFiscal.setVisible(false);
        txtf_DireccionFiscal.setVisible(false);
        txtf_DireccionFiscal.setManaged(false);
    }

    public void cargarCliente(ModuloClienteController.ClienteDemo cliente) {
        txtf_Nombres.setText(cliente.nombres());
        txtf_NumeroIdentificacion.setText(cliente.numeroIdentificacion());
        txtf_Direccion.setText(cliente.direccion());
        dt_FechaIngreso.setValue(cliente.fechaIngreso());
        txtf_Telefono.setText(cliente.telefono());
        txtf_Correo.setText(cliente.correo());
        cbx_TipoCliente.setValue(cliente.tipoCliente());
        cbx_TipoIdentificacion.setValue(cliente.tipoIdentificacion());
        cbx_Estado.setValue(cliente.estado());
        cbx_EstadoCivil.setValue(cliente.estadoCivil());
        txtf_RepresentanteLegal.setText(cliente.representanteLegal());
        txtf_DireccionFiscal.setText(cliente.direccionFiscal());

        actualizarCamposCondicionales();
    }

    public void setModo(String modo) {
        boolean esEditar = "EDITAR".equalsIgnoreCase(modo);
        boolean esVer = "VER".equalsIgnoreCase(modo);
        boolean esRegistrar = !esEditar && !esVer;

        txt_TituloForm.setText(esEditar ? "Editar Cliente" : esVer ? "Ver Cliente" : "Registrar nuevo Cliente");

        boolean editable = !esVer;
        txtf_Nombres.setEditable(editable);
        txtf_NumeroIdentificacion.setEditable(esRegistrar);
        cbx_TipoIdentificacion.setDisable(esVer);
        txtf_Telefono.setEditable(editable);
        txtf_Correo.setEditable(editable);
        cbx_Estado.setDisable(esVer);
        txtf_Direccion.setEditable(editable);
        cbx_TipoCliente.setDisable(esVer);
        dt_FechaIngreso.setDisable(esVer);
        btn_Guardar.setDisable(esVer);

        cbx_EstadoCivil.setDisable(esVer);
        txtf_RepresentanteLegal.setEditable(editable);
        txtf_DireccionFiscal.setEditable(editable);
    }
}
