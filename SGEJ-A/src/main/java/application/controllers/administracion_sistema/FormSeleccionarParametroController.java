package application.controllers.administracion_sistema;

import application.dao.ParametroDAO;
import application.model.Parametro;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FormSeleccionarParametroController {

    @FXML
    private ComboBox<String> cmb_FiltroCategoria;
    @FXML
    private ListView<Parametro> lv_Parametros;
    @FXML
    private Label lbl_Descripcion;
    @FXML
    private Button btn_Activar;
    @FXML
    private Button btn_Cancelar;

    private ModuloParametrosController moduloParametrosController;
    private ObservableList<Parametro> parametrosDisponibles = FXCollections.observableArrayList();
    private Runnable onActivarCallback;
    private Runnable onCancelarCallback;

    @FXML
    private void initialize() {
        // Configurar lista de parámetros
        configurarListView();

        // Ocultar el combobox de filtrado por categoría, ya que usaremos directamente
        // la categoría actual
        if (cmb_FiltroCategoria != null) {
            cmb_FiltroCategoria.getParent().setVisible(false);
            cmb_FiltroCategoria.getParent().setManaged(false);
        }

        // Configurar botones
        btn_Activar.setOnAction(e -> activarParametro());
        btn_Cancelar.setOnAction(e -> cancelar());

        // Los parámetros se cargarán cuando se llame a setModuloParametrosController
    }

    /**
     * Configura el ListView para mostrar los parámetros de forma simple
     */
    private void configurarListView() {
        // Permitir selección múltiple
        lv_Parametros.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Configurar la celda para mostrar el código y nombre del parámetro
        lv_Parametros.setCellFactory(param -> new ListCell<Parametro>() {
            @Override
            protected void updateItem(Parametro parametro, boolean empty) {
                super.updateItem(parametro, empty);

                if (empty || parametro == null) {
                    setText(null);
                } else {
                    // Mostrar el código y nombre claramente
                    setText(parametro.getCodigo() + " - " + parametro.getNombre());
                }
            }
        });

        // Mostrar descripción al seleccionar un parámetro
        lv_Parametros.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lbl_Descripcion.setText(newVal.getDescripcion() + "\n\n" +
                        "Tipo: " + newVal.getTipo().name() +
                        (newVal.getValorDefecto() != null ? "\nValor predeterminado: " + newVal.getValorDefecto()
                                : ""));

                // Habilitar el botón cuando hay selección
                btn_Activar.setDisable(false);
            } else {
                lbl_Descripcion.setText("Seleccione un parámetro para ver su descripción.");
            }
        });

        // Asignar el ObservableList como fuente de datos
        lv_Parametros.setItems(parametrosDisponibles);

        // Inicialmente el botón Activar está deshabilitado hasta que se seleccione un
        // parámetro
        btn_Activar.setDisable(true);

        // Cambiar el texto del botón para reflejar su función
        btn_Activar.setText("Añadir Seleccionado");
    }

    /**
     * Carga las categorías disponibles en el combo box
     */
    private void cargarCategorias() {
        // Cargar primero los parámetros inactivos
        ParametroDAO dao = new ParametroDAO();
        List<Parametro> parametros = dao.obtenerParametrosInactivos();

        // Extraer las categorías únicas
        Set<String> categorias = parametros.stream()
                .map(Parametro::getCategoria)
                .collect(Collectors.toSet());

        // Convertir a lista ordenada - NO agregamos "Todas"
        ObservableList<String> items = FXCollections.observableArrayList();
        items.addAll(categorias.stream().sorted().collect(Collectors.toList()));

        // Si hay contexto actual, preseleccionar esa categoría
        String categoriaPreseleccionada = null;

        // Si el módulo principal tiene una categoría seleccionada, usarla directamente
        if (moduloParametrosController != null && moduloParametrosController.getCategoriaActual() != null) {
            categoriaPreseleccionada = moduloParametrosController.getCategoriaActual();
            System.out.println("Usando categoría del controlador principal: " + categoriaPreseleccionada);
        }

        // Asignar al combo box
        cmb_FiltroCategoria.setItems(items);

        // Seleccionar la categoría relacionada con el contexto actual o la primera
        // disponible
        if (categoriaPreseleccionada != null && items.contains(categoriaPreseleccionada)) {
            cmb_FiltroCategoria.setValue(categoriaPreseleccionada);
            System.out.println("Seleccionando categoría: " + categoriaPreseleccionada);
        } else if (!items.isEmpty()) {
            cmb_FiltroCategoria.setValue(items.get(0));
            System.out.println("Usando primera categoría disponible: " + items.get(0));
        }

        // Aplicar el filtro con la categoría seleccionada
        if (cmb_FiltroCategoria.getValue() != null) {
            filtrarPorCategoria(cmb_FiltroCategoria.getValue());
        }
    }

    // El método cargarParametrosInactivos ha sido eliminado ya que ahora cargamos
    // parámetros filtrados por categoría directamente

    /**
     * Filtra los parámetros por categoría
     */
    private void filtrarPorCategoria(String categoria) {
        if (categoria == null || categoria.isEmpty()) {
            System.out.println("DEPURACIÓN: No hay categoría seleccionada para filtrar");
            return; // No hacer nada si no hay categoría seleccionada
        }

        System.out.println("DEPURACIÓN: Filtrando parámetros por categoría: " + categoria);
        ParametroDAO dao = new ParametroDAO();

        // Obtener parámetros inactivos directamente filtrados por categoría
        List<Parametro> parametros = dao.obtenerParametrosInactivosPorCategoria(categoria);
        System.out.println(
                "DEPURACIÓN: Encontrados " + parametros.size() + " parámetros inactivos en la categoría " + categoria);

        // Mostrar los parámetros encontrados para depuración
        for (Parametro p : parametros) {
            System.out.println("DEPURACIÓN: Parámetro encontrado - Código: " + p.getCodigo() +
                    ", Nombre: " + p.getNombre() +
                    ", Categoría: " + p.getCategoria());
        }

        parametrosDisponibles.clear();
        parametrosDisponibles.addAll(parametros);
    }

    // Los métodos de filtrado por relevancia han sido eliminados
    // ya que ahora filtramos exclusivamente por categoría

    /**
     * Obtiene el contexto actual basado en el módulo donde se abrió el selector
     * 
     * @return El nombre del módulo o contexto actual
     */
    private String obtenerContextoActual() {
        // Obtener el contexto del módulo principal
        if (moduloParametrosController != null) {
            String categoriaActual = moduloParametrosController.getCategoriaActual();
            if (categoriaActual != null && !categoriaActual.isEmpty() && !categoriaActual.equals("Todas")) {
                return categoriaActual;
            }
            return moduloParametrosController.getContextoActual();
        }
        return "Sistema"; // Por defecto, asumimos que estamos en el módulo de sistema
    }

    /**
     * Activa los parámetros seleccionados
     */
    private void activarParametro() {
        // Siempre obtener todos los elementos seleccionados
        List<Parametro> seleccionados = new ArrayList<>(lv_Parametros.getSelectionModel().getSelectedItems());

        if (!seleccionados.isEmpty()) {
            ParametroDAO dao = new ParametroDAO();
            int exitosos = 0;

            // Activar cada parámetro seleccionado
            for (Parametro parametro : seleccionados) {
                if (dao.activarParametro(parametro.getCodigo())) {
                    exitosos++;
                    // Eliminar de la lista de disponibles
                    parametrosDisponibles.remove(parametro);
                }
            }

            // Notificar al controlador principal
            if (moduloParametrosController != null) {
                moduloParametrosController.actualizarTabla();
            }

            // Ejecutar callback si existe
            if (onActivarCallback != null) {
                onActivarCallback.run();
            }

            // Mostrar mensaje de éxito
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Parámetros Activados");
            alert.setHeaderText(null);
            alert.setContentText("Se han activado " + exitosos + " de " + seleccionados.size() + " parámetros.");
            alert.showAndWait();

            // Cerrar si no hay más parámetros
            if (parametrosDisponibles.isEmpty()) {
                cerrarFormulario();
            }

        } else {
            // Mostrar advertencia si no hay selección
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sin selección");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, seleccione al menos un parámetro para activar.");
            alert.showAndWait();
        }
    }

    /**
     * Cancela la operación y cierra el formulario
     */
    private void cancelar() {
        // Si hay un callback, ejecutarlo
        if (onCancelarCallback != null) {
            onCancelarCallback.run();
        } else {
            cerrarFormulario();
        }
    }

    /**
     * Cierra el formulario
     */
    private void cerrarFormulario() {
        // Si estamos en un Stage (ventana modal)
        Stage stage = (Stage) btn_Cancelar.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
        // Si hay un callback, ejecutarlo
        else if (onCancelarCallback != null) {
            onCancelarCallback.run();
        }
    }

    /**
     * Establece el controlador del módulo de parámetros y carga los parámetros de
     * la categoría actual
     */
    public void setModuloParametrosController(ModuloParametrosController controller) {
        this.moduloParametrosController = controller;

        // Cargar parámetros inactivos de la categoría actual
        if (controller != null) {
            String categoriaActual = controller.getCategoriaActual();
            if (categoriaActual != null && !categoriaActual.isEmpty() && !categoriaActual.equals("Todas")) {
                System.out.println("DEPURACIÓN: Cargando parámetros inactivos de categoría: " + categoriaActual);
                filtrarPorCategoria(categoriaActual);

                // Actualizar el título para mostrar la categoría
                try {
                    // Buscar el título en el archivo FXML
                    Label titulo = (Label) lv_Parametros.getScene().lookup(".stack-pane Label");
                    if (titulo != null) {
                        titulo.setText("Añadir Parámetro - Categoría: " + categoriaActual);
                    }
                } catch (Exception e) {
                    System.out.println("No se pudo actualizar el título: " + e.getMessage());
                }
            } else {
                System.out.println("DEPURACIÓN: Categoría actual no válida o es 'Todas', usando 'General'");
                filtrarPorCategoria("General");
            }
        }
    }

    /**
     * Establece el callback para cuando se active un parámetro
     */
    public void setOnActivarCallback(Runnable callback) {
        this.onActivarCallback = callback;
    }

    /**
     * Establece el callback para cuando se cancele la operación
     */
    public void setOnCancelar(Runnable callback) {
        this.onCancelarCallback = callback;
    }

    /**
     * Establece el callback para cuando se activen parámetros
     */
    public void setOnActivar(java.util.function.Consumer<List<Parametro>> callback) {
        this.onActivarCallback = () -> {
            if (callback != null) {
                callback.accept(parametrosDisponibles);
            }
        };
    }
}
