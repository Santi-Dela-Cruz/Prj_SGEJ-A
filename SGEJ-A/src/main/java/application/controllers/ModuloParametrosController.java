package application.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class ModuloParametrosController {

    @FXML private Button btn_Nuevo;
    @FXML private Button btn_Buscar;
    @FXML private Button btn_Refrescar;
    @FXML private TextField txt_Busqueda;

    @FXML private TableView<ParametroDemo> tb_Parametros;
    @FXML private TableColumn<ParametroDemo, String> tbc_Codigo;
    @FXML private TableColumn<ParametroDemo, String> tbc_Nombre;
    @FXML private TableColumn<ParametroDemo, String> tbc_Descripcion;
    @FXML private TableColumn<ParametroDemo, String> tbc_Valor;
    @FXML private TableColumn<ParametroDemo, String> tbc_Tipo;
    @FXML private TableColumn<ParametroDemo, String> tbc_Estado;
    @FXML private TableColumn<ParametroDemo, Void> tbc_BotonEditar;
    @FXML private TableColumn<ParametroDemo, Void> tbc_BotonEliminar;

    private Pane pnl_Forms;

    public void setFormularioContainer(Pane pnl_Forms) {
        this.pnl_Forms = pnl_Forms;
    }

    @FXML
    private void initialize() {
        btn_Nuevo.setOnAction(e -> mostrarFormulario(null, "NUEVO"));
        btn_Refrescar.setOnAction(e -> cargarDatos());
        btn_Buscar.setOnAction(e -> buscarParametros());

        configurarColumnasTexto();
        inicializarColumnasDeBotones();
        cargarDatosEjemplo();
        ocultarEncabezadosColumnasDeAccion();

        tbc_BotonEditar.getStyleClass().add("column-action");
        tbc_BotonEliminar.getStyleClass().add("column-action");
    }

    private void configurarColumnasTexto() {
        tbc_Codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        tbc_Nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tbc_Descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        tbc_Valor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        tbc_Tipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        tbc_Estado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void inicializarColumnasDeBotones() {
        // Botón Editar
        tbc_BotonEditar.setCellFactory(column -> new TableCell<ParametroDemo, Void>() {
            private final Button btn = new Button("Editar");
            {
                btn.getStyleClass().add("table-button");
                btn.setOnAction(e -> {
                    ParametroDemo parametro = getTableView().getItems().get(getIndex());
                    mostrarFormulario(parametro, "EDITAR");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Botón Eliminar
        tbc_BotonEliminar.setCellFactory(column -> new TableCell<ParametroDemo, Void>() {
            private final Button btn = new Button("Eliminar");
            {
                btn.getStyleClass().add("table-button-danger");
                btn.setOnAction(e -> {
                    ParametroDemo parametro = getTableView().getItems().get(getIndex());
                    eliminarParametro(parametro);
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
        tb_Parametros.getItems().clear();
        
        // Datos de ejemplo
        tb_Parametros.getItems().addAll(
            new ParametroDemo("SYS001", "Timeout Session", "Tiempo de sesión en minutos", "30", "Numérico", "Activo"),
            new ParametroDemo("SYS002", "Max File Size", "Tamaño máximo de archivo en MB", "10", "Numérico", "Activo"),
            new ParametroDemo("SYS003", "Email Server", "Servidor de correo electrónico", "smtp.gmail.com", "Texto", "Activo"),
            new ParametroDemo("SYS004", "Email Port", "Puerto del servidor de correo", "587", "Numérico", "Activo"),
            new ParametroDemo("SYS005", "Backup Schedule", "Horario de respaldo automático", "02:00", "Tiempo", "Activo"),
            new ParametroDemo("SYS006", "Currency Symbol", "Símbolo de moneda", "$", "Texto", "Activo"),
            new ParametroDemo("SYS007", "Date Format", "Formato de fecha", "dd/MM/yyyy", "Texto", "Activo"),
            new ParametroDemo("SYS008", "Max Login Attempts", "Intentos máximos de login", "3", "Numérico", "Activo"),
            new ParametroDemo("SYS009", "System Language", "Idioma del sistema", "es-ES", "Texto", "Activo"),
            new ParametroDemo("SYS010", "Debug Mode", "Modo de depuración", "false", "Booleano", "Inactivo")
        );
    }

    private void cargarDatos() {
        // Aquí se cargarían los datos desde la base de datos
        cargarDatosEjemplo();
    }

    private void buscarParametros() {
        String criterio = txt_Busqueda.getText().trim();
        if (criterio.isEmpty()) {
            cargarDatos();
            return;
        }

        // Filtrar los datos existentes
        tb_Parametros.getItems().removeIf(parametro -> 
            !parametro.getCodigo().toLowerCase().contains(criterio.toLowerCase()) &&
            !parametro.getNombre().toLowerCase().contains(criterio.toLowerCase()) &&
            !parametro.getDescripcion().toLowerCase().contains(criterio.toLowerCase())
        );
    }

    private void eliminarParametro(ParametroDemo parametro) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Está seguro que desea eliminar este parámetro?");
        alert.setContentText("Parámetro: " + parametro.getNombre());

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            tb_Parametros.getItems().remove(parametro);
            mostrarMensaje("Parámetro eliminado exitosamente", "success");
        }
    }

    private void mostrarFormulario(ParametroDemo parametro, String accion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/sistema/form_parametro.fxml"));
            Node formulario = loader.load();

            FormParametroController controller = loader.getController();
            controller.setModuloParametrosController(this);
            controller.inicializarFormulario(parametro, accion);

            pnl_Forms.getChildren().setAll(formulario);
            pnl_Forms.setVisible(true);
            pnl_Forms.setManaged(true);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al cargar el formulario", "error");
        }
    }

    public void cerrarFormulario() {
        pnl_Forms.setVisible(false);
        pnl_Forms.setManaged(false);
        pnl_Forms.getChildren().clear();
    }

    public void actualizarTabla() {
        cargarDatos();
    }

    private void mostrarMensaje(String mensaje, String tipo) {
        Alert alert = new Alert(
            "error".equals(tipo) ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION
        );
        alert.setTitle("error".equals(tipo) ? "Error" : "Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void ocultarEncabezadosColumnasDeAccion() {
        tbc_BotonEditar.setText("");
        tbc_BotonEliminar.setText("");
    }

    // Clase demo para representar un parámetro
    public static class ParametroDemo {
        private final SimpleStringProperty codigo;
        private final SimpleStringProperty nombre;
        private final SimpleStringProperty descripcion;
        private final SimpleStringProperty valor;
        private final SimpleStringProperty tipo;
        private final SimpleStringProperty estado;

        public ParametroDemo(String codigo, String nombre, String descripcion, String valor, String tipo, String estado) {
            this.codigo = new SimpleStringProperty(codigo);
            this.nombre = new SimpleStringProperty(nombre);
            this.descripcion = new SimpleStringProperty(descripcion);
            this.valor = new SimpleStringProperty(valor);
            this.tipo = new SimpleStringProperty(tipo);
            this.estado = new SimpleStringProperty(estado);
        }

        // Getters
        public String getCodigo() { return codigo.get(); }
        public String getNombre() { return nombre.get(); }
        public String getDescripcion() { return descripcion.get(); }
        public String getValor() { return valor.get(); }
        public String getTipo() { return tipo.get(); }
        public String getEstado() { return estado.get(); }

        // Property getters para TableView
        public SimpleStringProperty codigoProperty() { return codigo; }
        public SimpleStringProperty nombreProperty() { return nombre; }
        public SimpleStringProperty descripcionProperty() { return descripcion; }
        public SimpleStringProperty valorProperty() { return valor; }
        public SimpleStringProperty tipoProperty() { return tipo; }
        public SimpleStringProperty estadoProperty() { return estado; }
    }
}
