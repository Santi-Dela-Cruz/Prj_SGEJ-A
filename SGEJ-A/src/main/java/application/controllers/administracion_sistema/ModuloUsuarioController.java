package application.controllers.administracion_sistema;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.time.LocalDate;

public class ModuloUsuarioController {

    // Botones
    @FXML private Button btn_Nuevo;
    @FXML private Button btn_Buscar;
    @FXML private TextField txt_Busqueda;

    // Tabla y columnas
    @FXML private TableView<UsuarioDemo> tb_Usuarios;
    @FXML private TableColumn<UsuarioDemo, String> tbc_NombresCompletos;
    @FXML private TableColumn<UsuarioDemo, String> tbc_NombreUsuario;
    @FXML private TableColumn<UsuarioDemo, String> tbc_NumeroI;
    @FXML private TableColumn<UsuarioDemo, String> tbc_TipoIdentificacion;
    @FXML private TableColumn<UsuarioDemo, String> tbc_Telefono;
    @FXML private TableColumn<UsuarioDemo, String> tbc_Correo;
    @FXML private TableColumn<UsuarioDemo, String> tbc_Estado;
    @FXML private TableColumn<UsuarioDemo, String> tbc_Rol;

    @FXML private TableColumn<UsuarioDemo, Void> tbc_BotonEditar;
    @FXML private TableColumn<UsuarioDemo, Void> tbc_BotonVer;
    @FXML private TableColumn<UsuarioDemo, Void> tbc_BotonReset;
    @FXML private TableColumn<UsuarioDemo, Void> tbc_BotonCambiarClave;

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
    }

    private void mostrarFormulario(UsuarioDemo usuario, String modo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/usuario/form_usuario.fxml"));
            Node form = loader.load();

            FormUsuarioController controller = loader.getController();
            controller.setOnCancelar(this::cerrarFormulario);
            controller.setOnGuardar(this::cerrarFormulario);

            if (usuario != null) {
                controller.cargarUsuario(usuario);
                controller.setModo(modo);
            } else {
                controller.setModo("NUEVO");
            }

            mostrarEnPanel(form);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarFormularioCambioClave(UsuarioDemo usuario, String modo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/usuario/form_cambio_clave.fxml"));
            Node form = loader.load();

            FormCambioClaveController controller = loader.getController();
            controller.setOnCancelar(this::cerrarFormulario);
            controller.setModo(modo); // "CAMBIO" o "RESET"

            mostrarEnPanel(form);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarEnPanel(Node form) {
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

    private void configurarColumnasTexto() {
        tbc_NombresCompletos.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().nombresCompletos()));
        tbc_NombreUsuario.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().nombreUsuario()));
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
        agregarBotonPorColumna(tbc_BotonReset, "🔁", "Restablecer");
        agregarBotonPorColumna(tbc_BotonCambiarClave, "🔒", "CambiarClave");

        tbc_BotonEditar.setPrefWidth(40);
        tbc_BotonVer.setPrefWidth(40);
        tbc_BotonReset.setPrefWidth(40);
        tbc_BotonCambiarClave.setPrefWidth(40);
    }

    private void agregarBotonPorColumna(TableColumn<UsuarioDemo, Void> columna, String texto, String tooltip) {
        columna.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button(texto);

            {
                btn.getStyleClass().add("table-button");
                btn.setTooltip(new Tooltip(tooltip));
                btn.setOnAction(event -> {
                    UsuarioDemo usuario = getTableView().getItems().get(getIndex());
                    switch (tooltip) {
                        case "Editar" -> mostrarFormulario(usuario, "EDITAR");
                        case "Ver" -> mostrarFormulario(usuario, "VER");
                        case "CambiarClave" -> mostrarFormularioCambioClave(usuario, "CAMBIO");
                        case "Restablecer" -> mostrarFormularioCambioClave(usuario, "RESET");
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

    private void cargarDatosEjemplo() {
        tb_Usuarios.getItems().addAll(
                new UsuarioDemo("Ana Mora", "anaMora", "0102030405", "Cédula", "0991234567", "ana@correo.com", "Activo", "Av. Siempre Viva 123", LocalDate.of(2023, 1, 10), "Natural", "Administrador"),
                new UsuarioDemo("Luis Pérez", "luisPerez", "1102233445", "Pasaporte", "0987654321", "luis@correo.com", "Activo", "Calle Falsa 456", LocalDate.of(2022, 5, 20), "Jurídica", "Usuario"),
                new UsuarioDemo("María Salas", "mariaSalas", "2223334445", "RUC", "0970001122", "maria@correo.com", "Inactivo", "Calle Real 789", LocalDate.of(2021, 8, 15), "Natural", "Administrador")
        );
    }

    public record UsuarioDemo(
            String nombresCompletos,
            String nombreUsuario,
            String numeroIdentificacion,
            String tipoIdentificacion,
            String telefono,
            String correo,
            String estado,
            String direccion,
            LocalDate fechaIngreso,
            String tipoUsuario,
            String rol
    ) {}
}