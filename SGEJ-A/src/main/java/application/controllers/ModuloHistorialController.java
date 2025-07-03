package application.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ModuloHistorialController {

    @FXML private TextField txtf_Buscar;
    @FXML private Button btn_Buscar, btn_Anadir;
    @FXML private TableView<ComunicacionDemo> tb_Comunicaciones;
    @FXML private TableColumn<ComunicacionDemo, String> tbc_FechaEntrada, tbc_Usuario, tbc_TipoAccion, tbc_Descripcion;

    @FXML
    private void initialize() {
        configurarColumnas();
        cargarDatosEjemplo();
    }

    private void configurarColumnas() {
        tbc_FechaEntrada.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fecha()));
        tbc_Usuario.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().usuario()));
        tbc_TipoAccion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().accion()));
        tbc_Descripcion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().descripcion()));
    }

    private void cargarDatosEjemplo() {
        tb_Comunicaciones.getItems().addAll(
                new ComunicacionDemo("02/07/2024", "Andrea Ruiz", "Llamada", "Se notificó audiencia."),
                new ComunicacionDemo("03/07/2024", "Carlos Gómez", "Correo", "Se envió copia del contrato.")
        );
    }

    public record ComunicacionDemo(String fecha, String usuario, String accion, String descripcion) {}
}
