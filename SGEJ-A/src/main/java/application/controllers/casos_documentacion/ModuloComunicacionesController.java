package application.controllers.casos_documentacion;

import application.model.HistorialComunicacion;
import application.service.HistorialComunicacionService;
import application.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import java.sql.Connection;
import java.util.List;

public class ModuloComunicacionesController {
    @FXML
    private TableView<HistorialComunicacion> tb_Comunicaciones;
    @FXML
    private TableColumn<HistorialComunicacion, String> tbc_Tipo, tbc_Fecha, tbc_Descripcion;
    @FXML
    private Button btn_Registrar;
    private Pane pnl_Forms;
    private HistorialComunicacionService service;

    public void setFormularioContainer(Pane pnl_Forms) {
        this.pnl_Forms = pnl_Forms;
    }

    @FXML
    private void initialize() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            service = new HistorialComunicacionService(conn);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        btn_Registrar.setOnAction(e -> mostrarFormulario());
        configurarColumnas();
        cargarComunicaciones();
    }

    private void configurarColumnas() {
        tbc_Tipo.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTipo()));
        tbc_Fecha.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getFecha().toString()));
        tbc_Descripcion.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getDescripcion()));
    }

    private void cargarComunicaciones() {
        try {
            // Suponiendo que el caso actual tiene id 1
            List<HistorialComunicacion> lista = service.obtenerPorCaso(1);
            tb_Comunicaciones.getItems().setAll(lista);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarFormulario() {
        // Implementar mostrar formulario de registro de comunicación
    }
}
