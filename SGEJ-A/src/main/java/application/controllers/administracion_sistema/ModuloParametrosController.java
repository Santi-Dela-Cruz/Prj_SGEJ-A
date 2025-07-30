package application.controllers.administracion_sistema;

import application.dao.ParametroDAO;
import application.model.Parametro;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.List;

public class ModuloParametrosController {

    @FXML
    private Button btn_Nuevo;
    @FXML
    private Button btn_Buscar;
    @FXML
    private Button btn_Refrescar;
    @FXML
    private Button btn_LimpiarBD;
    @FXML
    private TextField txt_Busqueda;
    @FXML
    private Label lbl_TotalParametros;

    @FXML
    private TableView<ParametroDemo> tb_Parametros;
    @FXML
    private TableColumn<ParametroDemo, String> tbc_Codigo;
    @FXML
    private TableColumn<ParametroDemo, String> tbc_Nombre;
    @FXML
    private TableColumn<ParametroDemo, String> tbc_Descripcion;
    @FXML
    private TableColumn<ParametroDemo, String> tbc_Valor;
    @FXML
    private TableColumn<ParametroDemo, String> tbc_Tipo;
    @FXML
    private TableColumn<ParametroDemo, String> tbc_Estado;
    @FXML
    private TableColumn<ParametroDemo, Void> tbc_BotonEditar;
    @FXML
    private TableColumn<ParametroDemo, Void> tbc_BotonEliminar;

    private Pane pnl_Forms;
    private ObservableList<ParametroDemo> parametros = FXCollections.observableArrayList();

    public void setFormularioContainer(Pane pnl_Forms) {
        this.pnl_Forms = pnl_Forms;
    }

    @FXML
    private void initialize() {
        btn_Nuevo.setOnAction(e -> mostrarFormulario(null, "NUEVO"));
        btn_Buscar.setOnAction(e -> buscarParametros());
        btn_Refrescar.setOnAction(e -> cargarParametrosDesdeBaseDatos());
        btn_LimpiarBD.setOnAction(e -> mostrarFormularioLimpiarBD());

        configurarColumnasTexto();
        inicializarColumnasDeBotones();
        cargarParametrosDesdeBaseDatos();
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
        agregarBotonPorColumna(tbc_BotonEditar, "✎", "Editar");
        agregarBotonPorColumna(tbc_BotonEliminar, "🗑", "Eliminar");

        tbc_BotonEditar.setPrefWidth(40);
        tbc_BotonEliminar.setPrefWidth(40);
    }

    private void agregarBotonPorColumna(TableColumn<ParametroDemo, Void> columna, String texto, String tooltip) {
        columna.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button(texto);

            {
                btn.getStyleClass().add("table-button");
                btn.setTooltip(new Tooltip(tooltip));
                btn.setOnAction(event -> {
                    ParametroDemo parametro = getTableView().getItems().get(getIndex());
                    if ("Editar".equals(tooltip)) {
                        mostrarFormulario(parametro, "EDITAR");
                    } else if ("Eliminar".equals(tooltip)) {
                        eliminarParametro(parametro);
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

    private void cargarParametrosDesdeBaseDatos() {
        parametros.clear();
        ParametroDAO dao = new ParametroDAO();
        List<Parametro> lista = dao.obtenerTodos();
        for (Parametro p : lista) {
            parametros.add(new ParametroDemo(
                    p.getCodigo(),
                    p.getNombre(),
                    p.getDescripcion(),
                    p.getValor(),
                    p.getTipo().name(),
                    p.getEstado().name().equals("ACTIVO") ? "Activo" : "Inactivo"));
        }
        tb_Parametros.setItems(parametros);

        // Actualizar contador de parámetros
        int totalParametros = parametros.size();
        lbl_TotalParametros
                .setText("Total: " + totalParametros + (totalParametros == 1 ? " parámetro" : " parámetros"));
    }

    private void buscarParametros() {
        String termino = txt_Busqueda.getText();
        if (termino == null || termino.trim().isEmpty()) {
            cargarParametrosDesdeBaseDatos();
            return;
        }
        ParametroDAO dao = new ParametroDAO();
        List<Parametro> filtrados = dao.buscarPorTexto(termino.trim());
        ObservableList<ParametroDemo> resultado = FXCollections.observableArrayList();
        for (Parametro p : filtrados) {
            resultado.add(new ParametroDemo(
                    p.getCodigo(),
                    p.getNombre(),
                    p.getDescripcion(),
                    p.getValor(),
                    p.getTipo().name(),
                    p.getEstado().name().equals("ACTIVO") ? "Activo" : "Inactivo"));
        }
        tb_Parametros.setItems(resultado);

        // Actualizar contador de parámetros encontrados
        int totalParametros = resultado.size();
        lbl_TotalParametros
                .setText("Encontrados: " + totalParametros + (totalParametros == 1 ? " parámetro" : " parámetros"));
    }

    private void eliminarParametro(ParametroDemo parametro) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Está seguro que desea eliminar este parámetro?");
        alert.setContentText("Parámetro: " + parametro.getNombre());

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            ParametroDAO dao = new ParametroDAO();
            boolean exito = dao.eliminarParametro(parametro.getCodigo());
            if (exito) {
                mostrarMensaje("Parámetro eliminado exitosamente", "success");
                cargarParametrosDesdeBaseDatos();
            } else {
                mostrarMensaje("No se pudo eliminar el parámetro", "error");
            }
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
        cargarParametrosDesdeBaseDatos();
    }

    private void mostrarMensaje(String mensaje, String tipo) {
        Alert alert = new Alert(
                "error".equals(tipo) ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle("error".equals(tipo) ? "Error" : "Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra el formulario para limpiar la base de datos
     */
    private void mostrarFormularioLimpiarBD() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/sistema/form_limpiar_bd.fxml"));
            Node formulario = loader.load();

            FormLimpiarBDController controller = loader.getController();
            controller.setOnCancelar(() -> {
                pnl_Forms.getChildren().clear();
                pnl_Forms.setVisible(false);
                pnl_Forms.setManaged(false);
            });

            controller.setOnLimpiar(() -> {
                pnl_Forms.getChildren().clear();
                pnl_Forms.setVisible(false);
                pnl_Forms.setManaged(false);
                cargarParametrosDesdeBaseDatos();
            });

            pnl_Forms.getChildren().setAll(formulario);
            pnl_Forms.setVisible(true);
            pnl_Forms.setManaged(true);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al cargar el formulario de limpieza", "error");
        }
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

        public ParametroDemo(String codigo, String nombre, String descripcion, String valor, String tipo,
                String estado) {
            this.codigo = new SimpleStringProperty(codigo);
            this.nombre = new SimpleStringProperty(nombre);
            this.descripcion = new SimpleStringProperty(descripcion);
            this.valor = new SimpleStringProperty(valor);
            this.tipo = new SimpleStringProperty(tipo);
            this.estado = new SimpleStringProperty(estado);
        }

        public ParametroDemo(String codigo, String nombre, String descripcion, String valor, String tipo) {
            this(codigo, nombre, descripcion, valor, tipo, "");
        }

        // Getters
        public String getCodigo() {
            return codigo.get();
        }

        public String getNombre() {
            return nombre.get();
        }

        public String getDescripcion() {
            return descripcion.get();
        }

        public String getValor() {
            return valor.get();
        }

        public String getTipo() {
            return tipo.get();
        }

        public String getEstado() {
            return estado.get();
        }

        // Property getters para TableView
        public SimpleStringProperty codigoProperty() {
            return codigo;
        }

        public SimpleStringProperty nombreProperty() {
            return nombre;
        }

        public SimpleStringProperty descripcionProperty() {
            return descripcion;
        }

        public SimpleStringProperty valorProperty() {
            return valor;
        }

        public SimpleStringProperty tipoProperty() {
            return tipo;
        }

        public SimpleStringProperty estadoProperty() {
            return estado;
        }
    }
}