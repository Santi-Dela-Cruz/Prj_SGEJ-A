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
        configurarBotones();
        cargarUsuarios();

        // Configurar búsqueda
        txt_Busqueda.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarUsuarios(newValue);
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
        try {
            // Usar el contenedor externo si está disponible
            AnchorPane container = formularioContainer != null ? formularioContainer : containerForm;

            System.out.println("Usando contenedor: " + (container == formularioContainer ? "externo" : "interno"));
            System.out.println("Contenedor nulo: " + (container == null ? "SI" : "NO"));

            if (container == null) {
                System.err.println("Error: No hay contenedor disponible para mostrar el formulario");
                return;
            }

            // Limpiar contenedor
            container.getChildren().clear();

            // Mostrar el contenedor
            container.setVisible(true);
            container.setManaged(true);

            // Cargar formulario
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/views/usuario/form_usuario.fxml"));
            AnchorPane formPane = loader.load();
            formUsuarioController = loader.getController();

            // Configurar formulario
            formUsuarioController.setUsuario(usuario);
            formUsuarioController.setOnGuardar(() -> {
                cargarUsuarios();
                container.getChildren().clear();
                container.setVisible(false);
                container.setManaged(false);
            });
            formUsuarioController.setOnCancelar(() -> {
                container.getChildren().clear();
                container.setVisible(false);
                container.setManaged(false);
            });

            // Mostrar formulario
            AnchorPane.setTopAnchor(formPane, 0.0);
            AnchorPane.setRightAnchor(formPane, 0.0);
            AnchorPane.setBottomAnchor(formPane, 0.0);
            AnchorPane.setLeftAnchor(formPane, 0.0);
            container.getChildren().add(formPane);

            System.out.println("Formulario de usuario cargado correctamente");

        } catch (IOException e) {
            System.err.println("Error al cargar formulario: " + e.getMessage());
            e.printStackTrace();
            DialogUtil.mostrarDialogo(
                    "Error",
                    "Error al cargar formulario: " + e.getMessage(),
                    "error",
                    List.of(ButtonType.OK));
        }
    }

    /**
     * Muestra el formulario de cambio de clave
     */
    private void mostrarFormularioCambioClave() {
        Usuario usuarioSeleccionado = tb_Usuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            return;
        }

        try {
            // Usar el contenedor externo si está disponible
            AnchorPane container = formularioContainer != null ? formularioContainer : containerForm;

            // Limpiar contenedor
            container.getChildren().clear();

            // Mostrar el contenedor
            container.setVisible(true);
            container.setManaged(true);

            // Cargar formulario
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/views/usuario/form_cambio_clave.fxml"));
            AnchorPane formPane = loader.load();
            formCambioClaveController = loader.getController();

            // Configurar formulario
            formCambioClaveController.setUsuario(usuarioSeleccionado);
            formCambioClaveController.setModo("RESET"); // Modo RESET (no solicita clave actual)
            formCambioClaveController.setOnGuardar(() -> {
                cargarUsuarios();
                container.getChildren().clear();
                container.setVisible(false);
                container.setManaged(false);
            });
            formCambioClaveController.setOnCancelar(() -> {
                container.getChildren().clear();
                container.setVisible(false);
                container.setManaged(false);
            });

            // Mostrar formulario
            AnchorPane.setTopAnchor(formPane, 0.0);
            AnchorPane.setRightAnchor(formPane, 0.0);
            AnchorPane.setBottomAnchor(formPane, 0.0);
            AnchorPane.setLeftAnchor(formPane, 0.0);
            container.getChildren().add(formPane);

            // Mostrar formulario
            AnchorPane.setTopAnchor(formPane, 0.0);
            AnchorPane.setRightAnchor(formPane, 0.0);
            AnchorPane.setBottomAnchor(formPane, 0.0);
            AnchorPane.setLeftAnchor(formPane, 0.0);
            container.getChildren().add(formPane);

        } catch (IOException e) {
            DialogUtil.mostrarDialogo(
                    "Error",
                    "Error al cargar formulario: " + e.getMessage(),
                    "error",
                    List.of(ButtonType.OK));
        }
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
