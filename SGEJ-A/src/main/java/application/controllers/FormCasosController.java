package application.controllers;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class FormCasosController {

    @FXML private TextField txtf_NumeroExpediente;
    @FXML private TextField txtf_TituloCaso;
    @FXML private ComboBox<String> cbx_TipoCaso;
    @FXML private ComboBox<String> cbx_Estado;
    @FXML private DatePicker dt_FechaInicio;
    @FXML private TextArea txtb_DescripcionCaso;

    @FXML private TableView<AbogadoDemo> tb_Abogados;
    @FXML private TableColumn<AbogadoDemo, Boolean> tbc_CheckBoton;
    @FXML private TableColumn<AbogadoDemo, String> tbc_Nombres;
    @FXML private TableColumn<AbogadoDemo, String> tbc_Apellidos;
    @FXML private TableColumn<AbogadoDemo, String> tbc_Cedula;
    @FXML private TableColumn<AbogadoDemo, String> tbc_Rol_ChekBox;
    @FXML private Text txt_TituloForm;

    @FXML private Button btn_Guardar;
    @FXML private Button btn_Cancelar;



    private Runnable onGuardar, onCancelar;

    private final ObservableList<String> roles = FXCollections.observableArrayList(
            "Principal", "Asistente", "Apoderado", "Consultor"
    );



    @FXML
    private void initialize() {
        cbx_TipoCaso.getItems().addAll("Civil", "Laboral", "Penal", "Familiar");
        cbx_Estado.getItems().addAll("Abierto", "En proceso", "Archivado");

        configurarTabla();
        cargarAbogadosEjemplo();

        btn_Guardar.setOnAction(e -> {
            if (txtf_NumeroExpediente.getText().isEmpty() || txtf_TituloCaso.getText().isEmpty() || cbx_TipoCaso.getItems().isEmpty() || cbx_Estado.getItems().isEmpty()) {
                DialogUtil.mostrarDialogo(
                        "Campos requeridos",
                        "Por favor, complete los campos obligatorios: \n - Número Expediente \n - Título del Caso \n - Tipo de Caso de Identificación \n - Estado del Caso",
                        "warning",
                        List.of(ButtonType.OK)
                );
                return;
            }

            Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                    "Confirmación",
                    "¿Está seguro que desea guardar este caso?",
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

    public void setOnGuardar(Runnable handler) {
        this.onGuardar = handler;
    }

    public void setOnCancelar(Runnable handler) {
        this.onCancelar = handler;
    }

    public void cargarDatosCaso(String numeroExpediente, String titulo, String tipo, String fecha, String estado) {
        txtf_NumeroExpediente.setText(numeroExpediente);
        txtf_TituloCaso.setText(titulo);
        cbx_TipoCaso.setValue(tipo);
        cbx_Estado.setValue(estado);
        if (fecha != null && !fecha.isEmpty()) {
            dt_FechaInicio.setValue(LocalDate.parse(fecha));
        } else {
            dt_FechaInicio.setValue(null);
        }
    }
    public void setModo(String modo) {
        boolean esNuevo = "NUEVO".equals(modo);
        boolean esEditar = "EDITAR".equals(modo);
        boolean esVer = "VER".equals(modo);

        if (esNuevo) {
            txt_TituloForm.setText("Registrar nuevo Caso");
        } else if (esEditar) {
            txt_TituloForm.setText("Editar Caso");
        } else if (esVer) {
            txt_TituloForm.setText("Visualizar Caso");
        }

        txtf_NumeroExpediente.setEditable(esNuevo); // Only editable in NUEVO
        txtf_TituloCaso.setEditable(esNuevo || esEditar);
        cbx_TipoCaso.setDisable(esVer);
        cbx_Estado.setDisable(esVer);
        dt_FechaInicio.setDisable(esVer);
        txtb_DescripcionCaso.setEditable(esNuevo || esEditar);
        tb_Abogados.setDisable(esVer);

        btn_Guardar.setVisible(!esVer);
        btn_Guardar.setDisable(esVer);
    }

    private void configurarTabla() {
        tbc_CheckBoton.setCellFactory(CheckBoxTableCell.forTableColumn(tbc_CheckBoton));
        tbc_CheckBoton.setCellValueFactory(data -> data.getValue().asignadoProperty());

        tbc_Nombres.setCellValueFactory(data -> data.getValue().nombresProperty());
        tbc_Apellidos.setCellValueFactory(data -> data.getValue().apellidosProperty());
        tbc_Cedula.setCellValueFactory(data -> data.getValue().cedulaProperty());
        tbc_Cedula.setStyle("-fx-alignment: CENTER-LEFT;"); // Align cedula left

        tbc_Rol_ChekBox.setCellFactory(ComboBoxTableCell.forTableColumn(roles));
        tbc_Rol_ChekBox.setCellValueFactory(data -> data.getValue().rolProperty());
        tbc_Rol_ChekBox.setOnEditCommit(event -> {
            event.getRowValue().rolProperty().set(event.getNewValue());
        });

        tb_Abogados.setEditable(true);
    }

    private void cargarAbogadosEjemplo() {
        tb_Abogados.setItems(FXCollections.observableArrayList(
                new AbogadoDemo("Andrea", "Salinas", "12345678", "Elegir rol", false),
                new AbogadoDemo("José", "Ruiz", "87654321", "Elegir rol", false),
                new AbogadoDemo("María", "León", "11223344", "Elegir rol", false)
        ));
    }

    private void guardarCaso() {
        String expediente = txtf_NumeroExpediente.getText();
        String titulo = txtf_TituloCaso.getText();
        String tipo = cbx_TipoCaso.getValue();
        String estado = cbx_Estado.getValue();
        String descripcion = txtb_DescripcionCaso.getText();
        String fecha = (dt_FechaInicio.getValue() != null) ? dt_FechaInicio.getValue().toString() : "";

        System.out.println("Guardando caso:");
        System.out.println("Expediente: " + expediente);
        System.out.println("Título: " + titulo);
        System.out.println("Tipo: " + tipo);
        System.out.println("Estado: " + estado);
        System.out.println("Fecha inicio: " + fecha);
        System.out.println("Descripción: " + descripcion);

        for (AbogadoDemo abogado : tb_Abogados.getItems()) {
            if (abogado.asignadoProperty().get()) {
                System.out.println("Abogado asignado: " + abogado.nombresProperty().get() + " " +
                        abogado.apellidosProperty().get() + " - " + abogado.rolProperty().get());
            }
        }
    }

    // Inner class for demo purposes
    public static class AbogadoDemo {
        private final StringProperty nombres;
        private final StringProperty apellidos;
        private final StringProperty cedula;
        private final StringProperty rol;
        private final BooleanProperty asignado;

        public AbogadoDemo(String nombres, String apellidos, String cedula, String rol, boolean asignado) {
            this.nombres = new SimpleStringProperty(nombres);
            this.apellidos = new SimpleStringProperty(apellidos);
            this.cedula = new SimpleStringProperty(cedula);
            this.rol = new SimpleStringProperty(rol);
            this.asignado = new SimpleBooleanProperty(asignado);
        }

        public StringProperty nombresProperty() { return nombres; }
        public StringProperty apellidosProperty() { return apellidos; }
        public StringProperty cedulaProperty() { return cedula; }
        public StringProperty rolProperty() { return rol; }
        public BooleanProperty asignadoProperty() { return asignado; }
    }
}