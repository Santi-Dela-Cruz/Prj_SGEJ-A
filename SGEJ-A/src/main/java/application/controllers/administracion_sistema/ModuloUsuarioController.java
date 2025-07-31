package application.controllers.administracion_sistema;

import application.controllers.DialogUtil;
import application.dao.UsuarioDAO;
import application.model.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ModuloUsuarioController {

    @FXML
    private TableView<Usuario> tb_Usuarios;
    @FXML
    private TableColumn<Usuario, String> tbc_NombresCompletos, tbc_NumeroI, tbc_NombreUsuario, tbc_Rol, tbc_Estado;
    @FXML
    private TableColumn<Usuario, Void> tbc_BotonEditar, tbc_BotonVer, tbc_BotonReset, tbc_BotonCambiarClave;
    @FXML
    private TextField txt_Busqueda;
    @FXML
    private Button btn_Nuevo, btn_Buscar, btn_Refrescar;
    @FXML
    private AnchorPane containerForm;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

    private FormUsuarioController formUsuarioController;
    private FormCambioClaveController formCambioClaveController;
    private AnchorPane formularioContainer;

    /**
     * Establece el contenedor para mostrar los formularios
     * 
     * @param container Contenedor donde se mostrarán los formularios
     */
    public void setFormularioContainer(AnchorPane container) {
        this.formularioContainer = container;
        System.out.println("FormularioContainer configurado: " + (container != null ? "SI" : "NO"));
    }

    @FXML
    private void initialize() {
        inicializarTabla();
        inicializarColumnasDeBotones();
        configurarBotones();
        cargarUsuarios();

        // Configurar búsqueda
        txt_Busqueda.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarUsuarios(newValue);
        });
    }

    // Inicializa las columnas de botones de acción en la tabla de usuarios
    private void inicializarColumnasDeBotones() {
        agregarBotonPorColumna(tbc_BotonEditar, "✏️", "Editar", "#f59e0b", "#d97706");
        agregarBotonPorColumna(tbc_BotonVer, "👁️", "Ver", "#3b82f6", "#2563eb");
        agregarBotonPorColumna(tbc_BotonReset, "🔄", "Reset", "#10b981", "#059669");
        agregarBotonPorColumna(tbc_BotonCambiarClave, "🔑", "Clave", "#6366f1", "#4338ca");

        tbc_BotonEditar.setText("");
        tbc_BotonVer.setText("");
        tbc_BotonReset.setText("");
        tbc_BotonCambiarClave.setText("");

        tbc_BotonEditar.setPrefWidth(55);
        tbc_BotonVer.setPrefWidth(55);
        tbc_BotonReset.setPrefWidth(55);
        tbc_BotonCambiarClave.setPrefWidth(55);
    }

    // Lógica para agregar un botón personalizado por columna, con estilos y
    // acciones diferenciadas
    private void agregarBotonPorColumna(TableColumn<Usuario, Void> columna, String texto, String tooltip, String color,
            String hoverColor) {
        columna.setCellFactory(tc -> new TableCell<>() {
            private final Button btn = new Button(texto);
            {
                btn.setTooltip(new Tooltip(tooltip));
                setStyle("-fx-alignment: CENTER; -fx-padding: 2;");
                btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 4; " +
                        "-fx-font-size: 10px; -fx-font-weight: bold; -fx-min-width: 55; -fx-max-width: 55; " +
                        "-fx-min-height: 25; -fx-max-height: 25; -fx-cursor: hand; -fx-padding: 0;");

                btn.setOnMouseEntered(_ -> btn.setStyle(
                        "-fx-background-color: " + hoverColor + "; -fx-text-fill: white; -fx-background-radius: 4; " +
                                "-fx-font-size: 10px; -fx-font-weight: bold; -fx-min-width: 55; -fx-max-width: 55; " +
                                "-fx-min-height: 25; -fx-max-height: 25; -fx-cursor: hand; -fx-padding: 0;"));
                btn.setOnMouseExited(_ -> btn.setStyle(
                        "-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 4; " +
                                "-fx-font-size: 10px; -fx-font-weight: bold; -fx-min-width: 55; -fx-max-width: 55; " +
                                "-fx-min-height: 25; -fx-max-height: 25; -fx-cursor: hand; -fx-padding: 0;"));

                btn.setOnAction(_ -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    switch (tooltip) {
                        case "Editar" -> mostrarFormularioUsuario(usuario);
                        case "Ver" -> mostrarFormularioUsuario(usuario); // Puedes cambiar por solo lectura
                        case "Reset" -> {
                            tb_Usuarios.getSelectionModel().select(usuario);
                            mostrarFormularioCambioClave();
                        }
                        case "Clave" -> {
                            tb_Usuarios.getSelectionModel().select(usuario);
                            mostrarFormularioCambioClave();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || getTableRow() == null || getTableRow().getItem() == null ? null : btn);
            }
        });
    }

    /**
     * Inicializa la tabla y sus columnas
     */
    private void inicializarTabla() {
        // Configurar columnas
        tbc_NombresCompletos.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        tbc_NumeroI.setCellValueFactory(new PropertyValueFactory<>("identificacion"));
        tbc_NombreUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        tbc_Rol.setCellValueFactory(new PropertyValueFactory<>("tipoUsuarioString"));
        tbc_Estado.setCellValueFactory(new PropertyValueFactory<>("estadoUsuarioString"));

        // Configurar selección de tabla
        tb_Usuarios.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> actualizarEstadoBotones(newValue != null));
    }

    /**
     * Configura los botones de acción
     */
    private void configurarBotones() {
        // Configurar botones
        btn_Nuevo.setOnAction(e -> mostrarFormularioUsuario(null));
        btn_Buscar.setOnAction(e -> filtrarUsuarios(txt_Busqueda.getText()));
        btn_Refrescar.setOnAction(e -> cargarUsuarios());
    }

    /**
     * Actualiza el estado de los botones según si hay un usuario seleccionado
     * 
     * @param haySeleccion true si hay un usuario seleccionado
     */
    private void actualizarEstadoBotones(boolean haySeleccion) {
        // Este método se mantiene para futuras actualizaciones
    }

    /**
     * Carga los usuarios desde la base de datos
     */
    private void cargarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDAO.obtenerTodosLosUsuarios();
            listaUsuarios.clear();
            listaUsuarios.addAll(usuarios);
            tb_Usuarios.setItems(listaUsuarios);
        } catch (Exception e) {
            DialogUtil.mostrarDialogo(
                    "Error",
                    "Error al cargar usuarios: " + e.getMessage(),
                    "error",
                    List.of(ButtonType.OK));
        }
    }

    /**
     * Filtra los usuarios según el texto de búsqueda
     * 
     * @param texto Texto a buscar
     */
    private void filtrarUsuarios(String texto) {
        if (texto == null || texto.isEmpty()) {
            tb_Usuarios.setItems(listaUsuarios);
            return;
        }

        String textoLower = texto.toLowerCase();
        ObservableList<Usuario> listaFiltrada = FXCollections.observableArrayList();

        for (Usuario usuario : listaUsuarios) {
            if (usuario.getNombreCompleto().toLowerCase().contains(textoLower) ||
                    usuario.getIdentificacion().toLowerCase().contains(textoLower) ||
                    usuario.getNombreUsuario().toLowerCase().contains(textoLower)) {
                listaFiltrada.add(usuario);
            }
        }

        tb_Usuarios.setItems(listaFiltrada);
    }

    /**
     * Muestra el formulario de usuario para crear o editar
     * 
     * @param usuario Usuario a editar, null para crear uno nuevo
     */
    private void mostrarFormularioUsuario(Usuario usuario) {
        // Mostrar el panel flotante independiente
        FormUsuarioModalLauncher.mostrarPanelUsuarioIndependiente(
                tb_Usuarios.getScene(), usuario, this::cargarUsuarios);
    }

    /**
     * Muestra el formulario de cambio de clave
     */
    private void mostrarFormularioCambioClave() {
        Usuario usuarioSeleccionado = tb_Usuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            return;
        }
        // Mostrar como panel flotante independiente
        FormUsuarioModalLauncher.mostrarPanelCambioClaveIndependiente(
                tb_Usuarios.getScene(),
                usuarioSeleccionado,
                "RESET",
                () -> cargarUsuarios());
    }

    /**
     * Elimina el usuario seleccionado
     */
    private void eliminarUsuario() {
        Usuario usuarioSeleccionado = tb_Usuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            return;
        }

        // Confirmar con el usuario
        Optional<ButtonType> respuesta = DialogUtil.mostrarDialogo(
                "Confirmación",
                "¿Está seguro de eliminar el usuario " + usuarioSeleccionado.getNombreUsuario() + "?",
                "confirm",
                List.of(ButtonType.YES, ButtonType.NO));

        if (respuesta.orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                boolean exito = usuarioDAO.eliminarUsuario(usuarioSeleccionado.getId());

                if (exito) {
                    cargarUsuarios();
                    DialogUtil.mostrarDialogo(
                            "Éxito",
                            "Usuario eliminado correctamente",
                            "info",
                            List.of(ButtonType.OK));
                } else {
                    DialogUtil.mostrarDialogo(
                            "Error",
                            "No se pudo eliminar el usuario",
                            "error",
                            List.of(ButtonType.OK));
                }
            } catch (Exception e) {
                DialogUtil.mostrarDialogo(
                        "Error",
                        "Error al eliminar usuario: " + e.getMessage(),
                        "error",
                        List.of(ButtonType.OK));
            }
        }
    }
}
