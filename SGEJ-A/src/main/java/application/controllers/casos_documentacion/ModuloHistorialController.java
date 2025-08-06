package application.controllers.casos_documentacion;

import application.model.HistorialComunicacion;
import application.service.HistorialComunicacionService;
import application.database.DatabaseConnection;
import application.controllers.DialogUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import java.util.List;
import java.util.Optional;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;

public class ModuloHistorialController {

    @FXML
    private TextField txtf_Buscar;
    @FXML
    private Label lbl_NumeroExpediente;
    @FXML
    private Button btn_Buscar, btn_Anadir;
    @FXML
    private TableView<HistorialComunicacion> tb_Comunicaciones;
    @FXML
    private TableColumn<HistorialComunicacion, String> tbc_FechaEntrada, tbc_Usuario, tbc_TipoAccion, tbc_Descripcion,
            tbc_Expediente;
    @FXML
    private TableColumn<HistorialComunicacion, Void> tbc_BotonEliminar;
    @FXML
    private ComboBox<String> cmb_CriterioBusqueda;

    private Pane pnl_Forms;
    private HistorialComunicacionService service;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public void setFormularioContainer(Pane pnl_Forms) {
        this.pnl_Forms = pnl_Forms;
    }

    @FXML
    private void initialize() {
        System.out.println("INFO: Inicializando ModuloHistorialController");

        // Configurar ComboBox de criterio de búsqueda
        cmb_CriterioBusqueda.getItems().addAll(
                "Número de Expediente",
                "Abogado Responsable");
        cmb_CriterioBusqueda.setValue("Número de Expediente"); // Valor por defecto

        configurarColumnas();

        // No inicializamos service aquí, lo haremos en cada método que lo necesite
        // para asegurar conexiones frescas
        cargarComunicaciones();

        btn_Anadir.setOnAction(event -> {
            System.out.println("INFO: Iniciando creación de nueva comunicación");
            mostrarFormulario(null, "NUEVO");
        });

        btn_Buscar.setOnAction(event -> buscarComunicaciones());
    }

    private void configurarColumnas() {
        tbc_FechaEntrada.setCellValueFactory(d -> {
            if (d.getValue().getFecha() != null) {
                return new SimpleStringProperty(dateFormat.format(d.getValue().getFecha()));
            }
            return new SimpleStringProperty("N/A");
        });
        tbc_Usuario.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getAbogadoNombre() != null ? d.getValue().getAbogadoNombre() : "Sin asignar"));
        tbc_TipoAccion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipo()));
        tbc_Descripcion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDescripcion()));
        // Usar el número de expediente completo
        tbc_Expediente.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNumeroExpediente()));

        tbc_BotonEliminar.setCellFactory(tc -> new TableCell<>() {
            private final Button btn = new Button("🗑");
            {
                btn.getStyleClass().add("table-button");
                btn.setTooltip(new Tooltip("Eliminar Registro"));
                btn.setOnAction(actionEvent -> {
                    if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                        HistorialComunicacion comm = getTableView().getItems().get(getIndex());

                        // Construir el mensaje para el diálogo
                        String mensaje = "Se eliminará la comunicación de tipo: " + comm.getTipo() +
                                "\nRealizada por: " + comm.getAbogadoNombre() +
                                "\nExpediente: " + comm.getNumeroExpediente();

                        // Mostrar diálogo de confirmación personalizado
                        Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                                "Confirmar eliminación",
                                mensaje,
                                "confirm",
                                List.of(ButtonType.OK, ButtonType.CANCEL));

                        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
                            // Crear una nueva conexión específicamente para la eliminación
                            Connection conn = null;
                            try {
                                // Obtener una nueva conexión para esta operación
                                conn = DatabaseConnection.getConnection();
                                // Crear un nuevo servicio con esta conexión
                                HistorialComunicacionService deleteService = new HistorialComunicacionService(conn);

                                boolean eliminado = deleteService.eliminarComunicacion(comm.getId());
                                if (eliminado) {
                                    System.out.println("Comunicación eliminada correctamente. ID: " + comm.getId());
                                    // Recargar la tabla
                                    ModuloHistorialController.this.cargarComunicaciones();

                                    // Mostrar mensaje de éxito
                                    DialogUtil.mostrarDialogo("Éxito", "Comunicación eliminada correctamente", "info",
                                            List.of(ButtonType.OK));
                                } else {
                                    System.out.println("No se pudo eliminar la comunicación");
                                    // Usar diálogo personalizado
                                    DialogUtil.mostrarDialogo("Error", "No se pudo eliminar la comunicación", "error",
                                            List.of(ButtonType.OK));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                // Usar diálogo personalizado
                                DialogUtil.mostrarDialogo("Error",
                                        "Error al eliminar la comunicación: " + e.getMessage(), "error",
                                        List.of(ButtonType.OK));
                            } finally {
                                // Cerrar la conexión después de usarla
                                if (conn != null) {
                                    try {
                                        conn.close();
                                        System.out.println("Conexión cerrada después de eliminar comunicación");
                                    } catch (Exception e) {
                                        System.err.println("ERROR: No se pudo cerrar la conexión: " + e.getMessage());
                                    }
                                }
                            }
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
                setStyle("-fx-alignment: CENTER;");
            }
        });
    }

    private void mostrarFormulario(HistorialComunicacion comunicacion, String modo) {
        // Aquí deberíamos adaptar el FormHistorialComunicacionController para que
        // trabaje con HistorialComunicacion
        // en lugar de ComunicacionDemo, pero por ahora simplemente mostraremos un
        // formulario vacío
        Node form = FormHistorialComunicacionController.cargarFormulario(
                modo,
                null,
                result -> {
                    System.out.println("INFO: Callback onGuardar ejecutado - Cerrando formulario y recargando datos");
                    cerrarFormulario();

                    // Pequeña pausa para asegurar que la transacción en la BD se complete
                    try {
                        Thread.sleep(200); // Esperar 200ms
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    // Recargar datos después de guardar
                    cargarComunicaciones();
                },
                result -> cerrarFormulario());

        AnchorPane.setTopAnchor(form, 0.0);
        AnchorPane.setBottomAnchor(form, 0.0);
        AnchorPane.setLeftAnchor(form, 0.0);
        AnchorPane.setRightAnchor(form, 0.0);

        pnl_Forms.getChildren().setAll(form);
        pnl_Forms.setVisible(true);
        pnl_Forms.setManaged(true);
    }

    private void cerrarFormulario() {
        pnl_Forms.getChildren().clear();
        pnl_Forms.setVisible(false);
        pnl_Forms.setManaged(false);
    }

    // El método mostrarDialogo fue reemplazado por llamadas directas a
    // DialogUtil.mostrarDialogo

    /**
     * Carga las comunicaciones desde la base de datos
     * Este método asegura que siempre se carguen los datos más recientes
     */
    private void cargarComunicaciones() {
        Connection conn = null;
        try {
            // Crear una nueva conexión cada vez para asegurar que se obtengan datos frescos
            conn = DatabaseConnection.getConnection();

            // Crear una nueva instancia del servicio con la conexión fresca
            service = new HistorialComunicacionService(conn);

            // Limpiar tabla
            tb_Comunicaciones.getItems().clear();

            // Obtener comunicaciones de la base de datos
            List<HistorialComunicacion> lista = service.obtenerTodasLasComunicaciones();

            if (lista != null && !lista.isEmpty()) {
                tb_Comunicaciones.getItems().addAll(lista);
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
        } finally {
            // Cerrar la conexión después de usarla para liberar recursos
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("INFO: Conexión cerrada después de cargar comunicaciones");
                } catch (Exception e) {
                    System.err.println("ERROR: No se pudo cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Busca comunicaciones según el criterio seleccionado en el ComboBox
     */
    private void buscarComunicaciones() {
        String criterioBusqueda = cmb_CriterioBusqueda.getValue();
        String textoBusqueda = txtf_Buscar.getText().trim();

        if (textoBusqueda.isEmpty()) {
            // Si el campo de búsqueda está vacío, cargar todas las comunicaciones
            cargarComunicaciones();
            return;
        }

        Connection conn = null;
        try {
            // Crear una nueva conexión cada vez para asegurar datos frescos
            conn = DatabaseConnection.getConnection();
            service = new HistorialComunicacionService(conn);

            System.out.println(
                    "INFO: Realizando búsqueda con criterio: " + criterioBusqueda + ", texto: '" + textoBusqueda + "'");

            // Limpiar tabla
            tb_Comunicaciones.getItems().clear();

            List<HistorialComunicacion> resultados = null;

            // Buscar según el criterio seleccionado
            switch (criterioBusqueda) {
                case "Número de Expediente":
                    resultados = service.buscarComunicacionesPorExpediente(textoBusqueda);
                    break;
                case "Abogado Responsable":
                    resultados = service.buscarComunicacionesPorAbogado(textoBusqueda);
                    break;
                default:
                    // Por defecto, buscar por todos los campos
                    resultados = service.buscarComunicaciones(textoBusqueda);
                    break;
            }

            if (resultados != null && !resultados.isEmpty()) {
                tb_Comunicaciones.getItems().addAll(resultados);
                System.out.println("INFO: Se encontraron " + resultados.size() + " comunicaciones con el criterio: "
                        + criterioBusqueda);
            } else {
                System.out.println("INFO: No se encontraron comunicaciones para el criterio: " + criterioBusqueda);

                // Mostrar diálogo de error cuando no se encuentran resultados
                DialogUtil.mostrarDialogo(
                        "Sin resultados",
                        "No se encontraron comunicaciones para la búsqueda: '" + textoBusqueda + "'",
                        "warning",
                        List.of(ButtonType.OK));

                // Establecer un mensaje cuando la búsqueda no tiene resultados
                Label lblNoData = new Label(
                        "No se encontraron comunicaciones para la búsqueda: '" + textoBusqueda + "'");
                lblNoData.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
                tb_Comunicaciones.setPlaceholder(lblNoData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Mostrar mensaje de error en la tabla
            Label lblError = new Label("Error al buscar comunicaciones: " + e.getMessage());
            lblError.setStyle("-fx-font-size: 14px; -fx-text-fill: #d32f2f;");
            tb_Comunicaciones.setPlaceholder(lblError);
        } finally {
            // Cerrar la conexión después de usarla para liberar recursos
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("INFO: Conexión cerrada después de buscar comunicaciones");
                } catch (Exception e) {
                    System.err.println("ERROR: No se pudo cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }
}
