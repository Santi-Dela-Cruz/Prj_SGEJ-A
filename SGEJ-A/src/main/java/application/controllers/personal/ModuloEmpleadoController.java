package application.controllers.personal;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class ModuloEmpleadoController {

    @FXML private Button btn_Nuevo;
    @FXML private Button btn_Buscar;
    @FXML private TextField txt_Busqueda;

    @FXML private TableView<EmpleadoDemo> tb_Empleados;
    @FXML private TableColumn<EmpleadoDemo, String> tbc_Nombres;
    @FXML private TableColumn<EmpleadoDemo, String> tbc_Apellidos;
    @FXML private TableColumn<EmpleadoDemo, String> tbc_NumeroI;
    @FXML private TableColumn<EmpleadoDemo, String> tbc_TipoIdentificacion;
    @FXML private TableColumn<EmpleadoDemo, String> tbc_Telefono;
    @FXML private TableColumn<EmpleadoDemo, String> tbc_Correo;
    @FXML private TableColumn<EmpleadoDemo, String> tbc_Estado;
    @FXML private TableColumn<EmpleadoDemo, String> tbc_Rol;

    @FXML private TableColumn<EmpleadoDemo, Void> tbc_BotonEditar;
    @FXML private TableColumn<EmpleadoDemo, Void> tbc_BotonVer;

    private Pane pnl_Forms;

    public void setFormularioContainer(Pane pnl_Forms) {
        this.pnl_Forms = pnl_Forms;
    }

    @FXML
    private void initialize() {
        btn_Nuevo.setOnAction(e -> mostrarFormulario(null, "NUEVO"));

        configurarColumnasTexto();
        inicializarColumnasDeBotones();
        cargarDatosEjemplo();
        ocultarEncabezadosColumnasDeAccion();

        tbc_BotonEditar.getStyleClass().add("column-action");
        tbc_BotonVer.getStyleClass().add("column-action");
    }

    private void ocultarEncabezadosColumnasDeAccion() {
        tb_Empleados.widthProperty().addListener((obs, oldVal, newVal) -> {
            Node header = tb_Empleados.lookup("TableHeaderRow");
            if (header != null) {
                header.setVisible(true);
            }
        });
    }

    private void mostrarFormulario(EmpleadoDemo empleado, String modo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/personal/form_empleado.fxml"));
            Node form = loader.load();

            FormEmpleadoController controller = loader.getController();
            controller.setOnCancelar(this::cerrarFormulario);
            controller.setOnGuardar(this::cerrarFormulario);

            if (empleado != null) {
                controller.cargarEmpleado(empleado);
                controller.setModo(modo);
            } else {
                controller.setModo("NUEVO");
            }

            AnchorPane.setTopAnchor(form, 0.0);
            AnchorPane.setBottomAnchor(form, 0.0);
            AnchorPane.setLeftAnchor(form, 0.0);
            AnchorPane.setRightAnchor(form, 0.0);

            pnl_Forms.getChildren().setAll(form);
            pnl_Forms.setVisible(true);
            pnl_Forms.setManaged(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cerrarFormulario() {
        pnl_Forms.getChildren().clear();
        pnl_Forms.setVisible(false);
        pnl_Forms.setManaged(false);
    }

    private void configurarColumnasTexto() {
        tbc_Nombres.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().nombres()));
        tbc_Apellidos.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().apellidos()));
        tbc_NumeroI.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().numeroIdentificacion()));
        tbc_TipoIdentificacion.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().tipoIdentificacion()));
        tbc_Telefono.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().telefono()));
        tbc_Correo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().correo()));
        tbc_Estado.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().estado()));
        tbc_Rol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().rol()));
    }

    private void inicializarColumnasDeBotones() {
        agregarBotonPorColumna(tbc_BotonEditar, "✎", "Editar");
        agregarBotonPorColumna(tbc_BotonVer, "👁", "Ver");

        tbc_BotonEditar.setPrefWidth(40);
        tbc_BotonVer.setPrefWidth(40);
    }

    private void agregarBotonPorColumna(TableColumn<EmpleadoDemo, Void> columna, String texto, String tooltip) {
        columna.getStyleClass().add("column-action");

        columna.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button(texto);

            {
                btn.getStyleClass().add("table-button");
                setStyle("-fx-alignment: CENTER;");
                btn.setTooltip(new Tooltip(tooltip));
                btn.setOnAction(event -> {
                    EmpleadoDemo empleado = getTableView().getItems().get(getIndex());
                    if ("Editar".equals(tooltip)) {
                        mostrarFormulario(empleado, "EDITAR");
                    } else if ("Ver".equals(tooltip)) {
                        mostrarFormulario(empleado, "VER");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void cargarDatosEjemplo() {
        tb_Empleados.getItems().addAll(
                new EmpleadoDemo(
                        "Ana", "Mora", "0102030405", "Cédula", "0991234567", "ana@correo.com", "Activo",
                        "Av. Siempre Viva 123", "Referencia 1", java.time.LocalDate.of(2023, 1, 10), "Natural", "Administrador"
                ),
                new EmpleadoDemo(
                        "Luis", "Pérez", "1102233445", "Pasaporte", "0987654321", "luis@correo.com", "Activo",
                        "Calle Falsa 456", "Referencia 2", java.time.LocalDate.of(2022, 5, 20), "Jurídica", "Gerente"
                ),
                new EmpleadoDemo(
                        "María", "Salas", "2223334445", "RUC", "0970001122", "maria@correo.com", "Inactivo",
                        "Calle Real 789", "Referencia 3", java.time.LocalDate.of(2021, 8, 15), "Natural", "Asistente"
                )
        );
    }
    // Example record (replace with your real Empleado class if needed)
    public record EmpleadoDemo(
            String nombres,
            String apellidos,
            String numeroIdentificacion,
            String tipoIdentificacion,
            String telefono,
            String correo,
            String estado,
            String direccion,
            String adicional,
            java.time.LocalDate fechaIngreso,
            String tipoEmpleado,
            String rol
    ) {}
}