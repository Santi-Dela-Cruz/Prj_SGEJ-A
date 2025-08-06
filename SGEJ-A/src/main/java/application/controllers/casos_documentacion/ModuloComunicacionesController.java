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
            Connection conn = DatabaseConnection.getConnection();
            service = new HistorialComunicacionService(conn);
            
            // Consulta todas las comunicaciones disponibles en la base de datos
            List<HistorialComunicacion> lista = service.obtenerTodasLasComunicaciones();
            
            if (lista != null && !lista.isEmpty()) {
                tb_Comunicaciones.getItems().setAll(lista);
                System.out.println("INFO: Se cargaron " + lista.size() + " comunicaciones desde la base de datos");
            } else {
                System.out.println("INFO: No se encontraron comunicaciones en la base de datos");
                // Establecer un mensaje cuando la tabla está vacía
                Label lblNoData = new Label("No hay comunicaciones registradas en la base de datos");
                lblNoData.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
                tb_Comunicaciones.setPlaceholder(lblNoData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Mostrar mensaje de error en la tabla
            Label lblError = new Label("Error al cargar las comunicaciones: " + e.getMessage());
            lblError.setStyle("-fx-font-size: 14px; -fx-text-fill: #d32f2f;");
            tb_Comunicaciones.setPlaceholder(lblError);
        }
    }

    private void mostrarFormulario() {
        // Implementar mostrar formulario de registro de comunicación
    }
}
