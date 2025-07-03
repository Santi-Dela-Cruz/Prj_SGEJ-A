// src/main/java/application/controllers/FormCasosController.java
package application.controllers;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.time.LocalDate;
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
    @FXML private TableColumn<AbogadoDemo, String> tbc_Rol_ChekBox;

    @FXML private Button btn_Guardar;
    @FXML private Button btn_Cancelar;

    private Consumer<Void> onCancelar;
    private Consumer<Void> onGuardar;

    @FXML
    private void initialize() {
        cbx_TipoCaso.getItems().addAll("Civil", "Laboral", "Penal", "Familiar");
        cbx_Estado.getItems().addAll("Abierto", "En proceso", "Archivado");

        configurarTabla();
        cargarAbogadosEjemplo();

        btn_Cancelar.setOnAction(e -> {
            if (onCancelar != null) onCancelar.accept(null);
        });

        btn_Guardar.setOnAction(e -> {
            guardarCaso();
            if (onGuardar != null) onGuardar.accept(null);
        });
    }

    public void setOnCancelar(Consumer<Void> callback) {
        this.onCancelar = callback;
    }

    public void setOnGuardar(Consumer<Void> callback) {
        this.onGuardar = callback;
    }

    public void cargarDatosCaso(String numeroExpediente, String titulo, String tipo, String fecha, String estado, String abogado) {
        txtf_NumeroExpediente.setText(numeroExpediente);
        txtf_TituloCaso.setText(titulo);
        cbx_TipoCaso.setValue(tipo);
        cbx_Estado.setValue(estado);
        if (fecha != null && !fecha.isEmpty()) {
            dt_FechaInicio.setValue(LocalDate.parse(fecha));
        } else {
            dt_FechaInicio.setValue(null);
        }
        // Optionally set abogado info if needed
    }

    public void setModo(String modo) {
        boolean editable = "EDITAR".equals(modo) || "NUEVO".equals(modo);
        boolean ver = "VER".equals(modo);

        txtf_NumeroExpediente.setEditable(editable && !"EDITAR".equals(modo)); // Only editable in NUEVO
        txtf_TituloCaso.setEditable(editable);
        cbx_TipoCaso.setDisable(!editable);
        cbx_Estado.setDisable(!editable);
        dt_FechaInicio.setDisable(!editable);
        txtb_DescripcionCaso.setEditable(editable);
        tb_Abogados.setDisable(!editable);

        btn_Guardar.setDisable(ver);
    }

    private void configurarTabla() {
        tbc_CheckBoton.setCellFactory(CheckBoxTableCell.forTableColumn(tbc_CheckBoton));
        tbc_CheckBoton.setCellValueFactory(data -> data.getValue().asignadoProperty());

        tbc_Nombres.setCellValueFactory(data -> data.getValue().nombresProperty());
        tbc_Apellidos.setCellValueFactory(data -> data.getValue().apellidosProperty());
        tbc_Rol_ChekBox.setCellValueFactory(data -> data.getValue().rolProperty());
    }

    private void cargarAbogadosEjemplo() {
        tb_Abogados.getItems().addAll(
                new AbogadoDemo("Andrea", "Salinas", "Principal", true),
                new AbogadoDemo("José", "Ruiz", "Asistente", false),
                new AbogadoDemo("María", "León", "Apoderada", true)
        );
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
                System.out.println("Abogado asignado: " + abogado.nombresProperty().get() + " " + abogado.apellidosProperty().get() + " - " + abogado.rolProperty().get());
            }
        }
    }

    // Clase interna AbogadoDemo
    public static class AbogadoDemo {
        private final StringProperty nombres;
        private final StringProperty apellidos;
        private final StringProperty rol;
        private final BooleanProperty asignado;

        public AbogadoDemo(String nombres, String apellidos, String rol, boolean asignado) {
            this.nombres = new SimpleStringProperty(nombres);
            this.apellidos = new SimpleStringProperty(apellidos);
            this.rol = new SimpleStringProperty(rol);
            this.asignado = new SimpleBooleanProperty(asignado);
        }

        public StringProperty nombresProperty() { return nombres; }
        public StringProperty apellidosProperty() { return apellidos; }
        public StringProperty rolProperty() { return rol; }
        public BooleanProperty asignadoProperty() { return asignado; }
    }
}