
package application.controllers.casos_documentacion;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class ModuloBitacoraController {
    @FXML
    private TableView<application.model.Caso> tb_Casos;
    @FXML
    private TableColumn<application.model.Caso, String> tbc_NumExpediente, tbc_Cliente, tbc_Estado, tbc_Titulo;
    @FXML
    private Label lbl_InfoCaso;

    @FXML
    private StackPane root;
    // Eliminados: tabla de bitácoras independientes y botones relacionados
    @FXML
    private TableView<BitacoraDemo> tb_BitacoraEntries;
    @FXML
    private TableColumn<BitacoraDemo, String> tbc_Fecha, tbc_Usuario, tbc_TipoAccion, tbc_Descripcion;
    @FXML
    private Button btn_AnadirEntrada;

    private Pane pnl_Forms;
    private application.model.Caso casoSeleccionado;

    public void setFormularioContainer(Pane pnl_Forms) {
        this.pnl_Forms = pnl_Forms;
    }

    @FXML
    private void initialize() {
        // Configurar tabla de casos
        tbc_NumExpediente.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNumeroExpediente()));
        tbc_Cliente.setCellValueFactory(d -> {
            application.model.Cliente cliente = d.getValue().getCliente();
            return new SimpleStringProperty(cliente != null ? cliente.getNombreCompleto() : "Desconocido");
        });
        tbc_Estado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEstado()));
        tbc_Titulo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitulo()));

        tb_Casos.setRowFactory(tv -> {
            TableRow<application.model.Caso> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    casoSeleccionado = row.getItem();
                    mostrarVistaDetalleCaso(casoSeleccionado);
                }
            });
            return row;
        });
    }

    // Muestra la vista de detalle de caso y su bitácora
    private void mostrarVistaDetalleCaso(application.model.Caso caso) {
        // Mostrar info del caso
        String info = "Expediente: " + caso.getNumeroExpediente() + " | Cliente: ";
        application.model.Cliente cliente = caso.getCliente();
        info += (cliente != null ? cliente.getNombreCompleto() : "Desconocido");
        info += " | Estado: " + caso.getEstado() + " | Título: " + caso.getTitulo();
        lbl_InfoCaso.setText(info);

        // Mostrar entradas de bitácora
        tb_BitacoraEntries.getItems().clear();
        try {
            java.sql.Connection conn = application.database.DatabaseConnection.getConnection();
            application.dao.BitacoraCasoDAO bitacoraDAO = new application.dao.BitacoraCasoDAO(conn);
            java.util.List<application.model.BitacoraCaso> entradas = bitacoraDAO
                    .consultarBitacorasPorCaso(caso.getId());
            for (application.model.BitacoraCaso entrada : entradas) {
                tb_BitacoraEntries.getItems().add(new BitacoraDemo(
                        entrada.getFechaEntrada().toString(),
                        entrada.getUsuario(),
                        entrada.getTipoAccion(),
                        entrada.getDescripcion()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarInfoCasoYBitacora(application.model.Caso caso) {
        // Mostrar info del caso
        String info = "Expediente: " + caso.getNumeroExpediente() + " | Cliente: ";
        application.model.Cliente cliente = caso.getCliente();
        info += (cliente != null ? cliente.getNombreCompleto() : "Desconocido");
        info += " | Estado: " + caso.getEstado() + " | Título: " + caso.getTitulo();
        lbl_InfoCaso.setText(info);

        // Mostrar entradas de bitácora
        tb_BitacoraEntries.getItems().clear();
        try {
            java.sql.Connection conn = application.database.DatabaseConnection.getConnection();
            application.dao.BitacoraCasoDAO bitacoraDAO = new application.dao.BitacoraCasoDAO(conn);
            java.util.List<application.model.BitacoraCaso> entradas = bitacoraDAO
                    .consultarBitacorasPorCaso(caso.getId());
            for (application.model.BitacoraCaso entrada : entradas) {
                tb_BitacoraEntries.getItems().add(new BitacoraDemo(
                        entrada.getFechaEntrada().toString(),
                        entrada.getUsuario(),
                        entrada.getTipoAccion(),
                        entrada.getDescripcion()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cargarCasosEnTabla() {
        try {
            java.sql.Connection conn = application.database.DatabaseConnection.getConnection();
            application.service.CasoService casoService = new application.service.CasoService(conn);
            java.util.List<application.model.Caso> casos = casoService.consultarCasos("");
            tb_Casos.getItems().setAll(casos);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarFormularioEntrada() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/casos_documentos/form_nueva_bitacora.fxml"));
            Node form = loader.load();
            FormBitacoraController controller = loader.getController();
            controller.setModoFormulario("ENTRADA");
            controller.setOnCancelar(v -> cerrarFormulario());
            controller.setOnGuardar(v -> {
                // Guardar nueva entrada en la BD
                try {
                    java.sql.Connection conn = application.database.DatabaseConnection.getConnection();
                    application.dao.BitacoraCasoDAO bitacoraDAO = new application.dao.BitacoraCasoDAO(conn);
                    int casoId = casoSeleccionado != null ? casoSeleccionado.getId() : -1;
                    if (casoId != -1) {
                        application.model.BitacoraCaso nueva = controller.obtenerBitacoraDesdeFormulario(casoId);
                        bitacoraDAO.insertarBitacora(nueva);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                cerrarFormulario();
                // Refrescar la tabla de bitácora
                if (casoSeleccionado != null)
                    mostrarInfoCasoYBitacora(casoSeleccionado);
            });
            mostrarFormulario(form);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarFormulario(Node form) {
        pnl_Forms.getChildren().setAll(form);
        pnl_Forms.setVisible(true);
        pnl_Forms.setManaged(true);
    }

    private void cerrarFormulario() {
        pnl_Forms.getChildren().clear();
        pnl_Forms.setVisible(false);
        pnl_Forms.setManaged(false);
    }

    public record BitacoraDemo(String fecha, String usuario, String accion, String descripcion) {
    }
}