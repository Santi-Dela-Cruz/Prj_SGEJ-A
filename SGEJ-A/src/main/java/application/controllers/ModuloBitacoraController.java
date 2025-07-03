package application.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ModuloBitacoraController {

    @FXML private TextField txtf_Buscar;
    @FXML private Button btn_Buscar, btn_Anadir;
    @FXML private TableView<BitacoraDemo> tb_Bitacoras;
    @FXML private TableColumn<BitacoraDemo, String> tbc_Fecha, tbc_Usuario, tbc_TipoAccion, tbc_Descripcion;

    @FXML
    private void initialize() {
        configurarColumnas();
        cargarDatosEjemplo();
    }

    private void configurarColumnas() {
        tbc_Fecha.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fecha()));
        tbc_Usuario.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().usuario()));
        tbc_TipoAccion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().accion()));
        tbc_Descripcion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().descripcion()));
    }

    private void cargarDatosEjemplo() {
        tb_Bitacoras.getItems().addAll(
                new BitacoraDemo("25/06/2024", "Laura Ortiz", "Visita", "Cliente visitó la oficina."),
                new BitacoraDemo("28/06/2024", "Mario Torres", "Llamada", "Se coordinó audiencia.")
        );
    }

    public record BitacoraDemo(String fecha, String usuario, String accion, String descripcion) {}
}
