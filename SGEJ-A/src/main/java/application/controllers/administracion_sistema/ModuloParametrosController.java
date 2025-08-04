package application.controllers.administracion_sistema;

import application.controllers.DialogoPersonalizadoController;
import application.controllers.DialogUtil;
import application.dao.ParametroDAO;
import application.model.Parametro;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

// Importaciones de los controladores adicionales necesarios
import application.controllers.administracion_sistema.FormParametroController;
import application.controllers.administracion_sistema.FormSeleccionarParametroController;

/**
 * Controlador para el módulo de gestión de parámetros del sistema
 */
public class ModuloParametrosController {

    @FXML
    private TextField txt_Busqueda;
    @FXML
    private Button btn_Buscar;
    @FXML
    private Button btn_Refrescar;
    @FXML
    private Button btn_Nuevo;
    @FXML
    private Button btn_LimpiarBD;
    @FXML
    private Label lbl_TotalParametros;

    @FXML
    private Button btnCategoriaGeneral;
    @FXML
    private Button btnCategoriaSeguridad;
    @FXML
    private Button btnCategoriaLegalFiscal;
    @FXML
    private Button btnCategoriaNotificaciones;
    @FXML
    private Button btnCategoriaSistema;

    @FXML
    private TilePane tp_Parametros;

    // Lista observable para mantener los parámetros
    private ObservableList<Parametro> parametros = FXCollections.observableArrayList();
    private String categoriaActual = "Todas";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    private void initialize() {
        // Configurar botones de acciones principales
        configurarBotonesAcciones();

        // Configurar botones de categorías
        configurarBotonesCategorias();

        // Cargar parámetros iniciales
        cargarParametros();
    }

    /**
     * Configura los botones principales de acción
     */
    private void configurarBotonesAcciones() {
        // Botón de búsqueda
        btn_Buscar.setOnAction(e -> buscarParametros());

        // Botón de refrescar
        btn_Refrescar.setOnAction(e -> {
            cargarParametros();
            txt_Busqueda.clear();
        });

        // Botón de nuevo parámetro
        btn_Nuevo.setOnAction(e -> {
            // Primero intentamos abrir el formulario para seleccionar parámetros
            // predefinidos
            try {
                abrirFormSeleccionarParametro();
            } catch (Exception ex) {
                // Si ocurre un error, mostramos el mensaje
                mostrarMensaje("Error", "No se pudo abrir el formulario para seleccionar parámetros predefinidos.",
                        Alert.AlertType.ERROR);
                ex.printStackTrace();
            }
        });

        // Botón de limpiar base de datos
        btn_LimpiarBD.setOnAction(e -> {
            try {
                abrirFormLimpiarBD();
            } catch (Exception ex) {
                mostrarMensaje("Error", "No se pudo abrir el formulario para limpiar la base de datos.",
                        Alert.AlertType.ERROR);
                ex.printStackTrace();
            }
        });

        // Búsqueda en tiempo real mientras escriben
        txt_Busqueda.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                cargarParametros();
            } else if (newVal.length() >= 3) {
                buscarParametros();
            }
        });
    }

    /**
     * Configura los botones de filtrado por categorías
     */
    private void configurarBotonesCategorias() {
        // Estilo activo e inactivo
        String estiloActivo = "-fx-background-color: linear-gradient(to bottom, #3b82f6, #2563eb); -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 13px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.4), 4, 0, 0, 2);";
        String estiloInactivo = "-fx-background-color: linear-gradient(to bottom, #6b7280, #4b5563); -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 13px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-cursor: hand;";

        // Configurar acción para cada botón
        btnCategoriaGeneral.setOnAction(e -> {
            categoriaActual = "General";
            cargarParametrosPorCategoria(categoriaActual);
            actualizarEstiloBotonesCategorias(btnCategoriaGeneral, estiloActivo, estiloInactivo);
        });

        btnCategoriaSeguridad.setOnAction(e -> {
            categoriaActual = "Seguridad";
            cargarParametrosPorCategoria(categoriaActual);
            actualizarEstiloBotonesCategorias(btnCategoriaSeguridad, estiloActivo, estiloInactivo);
        });

        btnCategoriaLegalFiscal.setOnAction(e -> {
            categoriaActual = "Legal/Fiscal";
            cargarParametrosPorCategoria(categoriaActual);
            actualizarEstiloBotonesCategorias(btnCategoriaLegalFiscal, estiloActivo, estiloInactivo);
        });

        btnCategoriaNotificaciones.setOnAction(e -> {
            categoriaActual = "Notificaciones";
            cargarParametrosPorCategoria(categoriaActual);
            actualizarEstiloBotonesCategorias(btnCategoriaNotificaciones, estiloActivo, estiloInactivo);
        });

        btnCategoriaSistema.setOnAction(e -> {
            categoriaActual = "Sistema";
            cargarParametrosPorCategoria(categoriaActual);
            actualizarEstiloBotonesCategorias(btnCategoriaSistema, estiloActivo, estiloInactivo);
        });

        // Establecer botón "General" como activo inicialmente
        categoriaActual = "General";
        actualizarEstiloBotonesCategorias(btnCategoriaGeneral, estiloActivo, estiloInactivo);
        cargarParametrosPorCategoria(categoriaActual);
    }

    /**
     * Actualiza el estilo de los botones de categorías
     * 
     * @param botonActivo    Botón que debe estar activo
     * @param estiloActivo   Estilo para el botón activo
     * @param estiloInactivo Estilo para los botones inactivos
     */
    private void actualizarEstiloBotonesCategorias(Button botonActivo, String estiloActivo, String estiloInactivo) {
        // Establecer todos como inactivos
        btnCategoriaGeneral.setStyle(estiloInactivo);
        btnCategoriaSeguridad.setStyle(estiloInactivo);
        btnCategoriaLegalFiscal.setStyle(estiloInactivo);
        btnCategoriaNotificaciones.setStyle(estiloInactivo);
        btnCategoriaSistema.setStyle(estiloInactivo);

        // Activar el botón seleccionado
        botonActivo.setStyle(estiloActivo);
    }

    /**
     * Carga todos los parámetros visibles
     */
    private void cargarParametros() {
        // Limpiar el TilePane antes de agregar nuevas tarjetas
        tp_Parametros.getChildren().clear();
        parametros.clear();

        // Obtener parámetros de la base de datos
        ParametroDAO dao = new ParametroDAO();
        List<Parametro> listaParametros = dao.obtenerParametrosVisibles();
        parametros.addAll(listaParametros);

        // Actualizar el contador de parámetros
        lbl_TotalParametros.setText("Total: " + parametros.size() + " parámetros");

        // Crear las tarjetas para cada parámetro
        for (Parametro param : parametros) {
            tp_Parametros.getChildren().add(crearTarjetaParametro(param));
        }
    }

    /**
     * Carga parámetros filtrados por categoría
     */
    private void cargarParametrosPorCategoria(String categoria) {
        // Si es "Todas", cargar todos los parámetros
        if ("Todas".equals(categoria)) {
            cargarParametros();
            return;
        }

        // Limpiar el TilePane antes de agregar nuevas tarjetas
        tp_Parametros.getChildren().clear();
        parametros.clear();

        // Obtener parámetros de la base de datos
        ParametroDAO dao = new ParametroDAO();
        List<Parametro> listaParametros = dao.obtenerPorCategoria(categoria);
        parametros.addAll(listaParametros);

        // Actualizar el contador de parámetros
        lbl_TotalParametros.setText("Total: " + parametros.size() + " parámetros (" + categoria + ")");

        // Crear las tarjetas para cada parámetro
        for (Parametro param : parametros) {
            tp_Parametros.getChildren().add(crearTarjetaParametro(param));
        }
    }

    /**
     * Busca parámetros por texto
     */
    private void buscarParametros() {
        String textoBusqueda = txt_Busqueda.getText().trim().toLowerCase();
        if (textoBusqueda.isEmpty()) {
            cargarParametros();
            return;
        }

        // Limpiar el TilePane antes de agregar nuevas tarjetas
        tp_Parametros.getChildren().clear();

        // Filtrar parámetros que coincidan con la búsqueda
        for (Parametro param : parametros) {
            if (param.getNombre().toLowerCase().contains(textoBusqueda) ||
                    param.getCodigo().toLowerCase().contains(textoBusqueda) ||
                    param.getDescripcion().toLowerCase().contains(textoBusqueda) ||
                    param.getCategoria().toLowerCase().contains(textoBusqueda)) {

                tp_Parametros.getChildren().add(crearTarjetaParametro(param));
            }
        }

        // Actualizar contador con resultados
        int resultados = tp_Parametros.getChildren().size();
        lbl_TotalParametros.setText("Resultados: " + resultados + " parámetros");
    }

    /**
     * Crea una tarjeta visual para un parámetro
     * 
     * @param param El parámetro a mostrar
     * @return El AnchorPane con la tarjeta
     */
    private AnchorPane crearTarjetaParametro(Parametro param) {
        // Crear el contenedor principal de la tarjeta
        AnchorPane tarjeta = new AnchorPane();
        tarjeta.setPrefWidth(230);
        tarjeta.setPrefHeight(200);
        tarjeta.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.10), 6, 0, 0, 2); " +
                "-fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 15;");

        // Crear el contenido de la tarjeta
        VBox contenido = new VBox(6);
        contenido.setLayoutX(15);
        contenido.setLayoutY(12);

        // Determinar el emoji según el tipo
        String emoji = obtenerEmojiPorTipo(param.getTipo());

        // Crear las etiquetas con la información
        Label lblNombre = new Label(emoji + " " + param.getNombre());
        lblNombre.setStyle(
                "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1e40af; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        Label lblCodigo = new Label("Código: " + param.getCodigo());
        lblCodigo.setStyle(
                "-fx-font-size: 12px; -fx-text-fill: #6c757d; -fx-font-style: italic; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        Label lblValor = new Label("Valor: " + param.getValor());
        lblValor.setStyle(
                "-fx-font-size: 13px; -fx-text-fill: #059669; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        Label lblDescripcion = new Label(
                "ℹ️ " + (param.getDescripcion().length() > 30 ? param.getDescripcion().substring(0, 30) + "..."
                        : param.getDescripcion()));
        lblDescripcion.setStyle(
                "-fx-font-size: 12px; -fx-text-fill: #6b7280; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        Label lblTipo = new Label("Tipo: " + param.getTipo().name());
        lblTipo.setStyle(
                "-fx-font-size: 12px; -fx-text-fill: #2563eb; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        String fechaModificacion = param.getUpdatedAt() != null ? param.getUpdatedAt().format(formatter) : "N/A";
        Label lblFecha = new Label("Últ. mod: " + fechaModificacion);
        lblFecha.setStyle(
                "-fx-font-size: 11px; -fx-text-fill: #b91c1c; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // Botones de acción
        Button btnRestablecer = new Button("🔄 Restablecer");
        btnRestablecer.setStyle("-fx-background-color: linear-gradient(to bottom, #f59e0b, #d97706); " +
                "-fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 13px; " +
                "-fx-font-weight: bold; -fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-cursor: hand;");

        Button btnEditar = new Button("✏️ Editar");
        btnEditar.setStyle("-fx-background-color: linear-gradient(to bottom, #3b82f6, #2563eb); " +
                "-fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 13px; " +
                "-fx-font-weight: bold; -fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-cursor: hand;");

        // Contenedor para los botones
        javafx.scene.layout.HBox botonesHBox = new javafx.scene.layout.HBox(8);
        botonesHBox.getChildren().addAll(btnRestablecer, btnEditar);
        botonesHBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        // Agregar todo al contenedor vertical
        contenido.getChildren().addAll(lblNombre, lblCodigo, lblValor, lblDescripcion, lblTipo, lblFecha, botonesHBox);

        // Configurar acciones de botones
        btnRestablecer.setOnAction(e -> {
            restablecerValorDefecto(param);
        });

        btnEditar.setOnAction(e -> {
            abrirFormularioEdicion(param);
        });

        // Agregar el contenido a la tarjeta
        tarjeta.getChildren().add(contenido);

        return tarjeta;
    }

    /**
     * Determina el emoji a mostrar según el tipo de parámetro
     * 
     * @param tipo El tipo de parámetro
     * @return El emoji correspondiente
     */
    private String obtenerEmojiPorTipo(Parametro.Tipo tipo) {
        switch (tipo) {
            case TEXTO:
                return "🔤";
            case NUMERICO:
            case ENTERO:
            case DECIMAL:
                return "🔢";
            case TIEMPO:
                return "⏱️";
            case BOOLEANO:
                return "✅";
            case IMAGEN:
                return "🖼️";
            case CLAVE:
                return "🔑";
            default:
                return "⚙️";
        }
    }

    /**
     * Restablece el valor de un parámetro a su valor por defecto
     * 
     * @param parametro El parámetro a restablecer
     */
    private void restablecerValorDefecto(Parametro parametro) {
        // Confirmación antes de restablecer
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmación");
        confirmacion.setHeaderText("Restablecer valor por defecto");
        confirmacion.setContentText("¿Está seguro que desea restablecer el parámetro '" +
                parametro.getNombre() + "' a su valor por defecto?\n\n" +
                "Valor actual: " + parametro.getValor() + "\n" +
                "Valor por defecto: " + parametro.getValorDefecto());

        // Si el usuario confirma, proceder con el restablecimiento
        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == javafx.scene.control.ButtonType.OK) {
                ParametroDAO dao = new ParametroDAO();
                boolean exito = dao.restablecerValorDefecto(parametro.getCodigo());

                if (exito) {
                    // Mostrar mensaje de éxito
                    mostrarMensaje("Éxito", "Parámetro restablecido correctamente.", Alert.AlertType.INFORMATION);

                    // Actualizar la vista
                    actualizarTabla();
                } else {
                    // Mostrar mensaje de error
                    mostrarMensaje("Error", "No se pudo restablecer el parámetro.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    /**
     * Abre el formulario para editar un parámetro
     * 
     * @param parametro El parámetro a editar
     */
    private void abrirFormularioEdicion(Parametro parametro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/sistema/form_parametro.fxml"));
            Parent root = loader.load();

            // Obtener el controlador
            FormParametroController controller = loader.getController();
            controller.setParametro(parametro);
            controller.setActualizarCallback(() -> {
                actualizarTabla();
            });

            // Crear la escena y el stage
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Editar Parámetro");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo abrir el formulario de edición.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Abre el formulario para seleccionar un parámetro predefinido
     */
    private void abrirFormSeleccionarParametro() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/sistema/form_seleccionar_parametro.fxml"));
            Parent root = loader.load();

            // Obtener el controlador
            FormSeleccionarParametroController controller = loader.getController();
            controller.setModuloParametrosController(this);

            // Forzar que el formulario use la categoría actual
            System.out.println("Abriendo selector con categoría: " + categoriaActual);

            // Crear la escena y el stage
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Seleccionar Parámetro Predefinido");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo abrir el formulario para seleccionar parámetros.",
                    Alert.AlertType.ERROR);
        }
    }

    /**
     * Abre el formulario para limpiar la base de datos
     */
    private void abrirFormLimpiarBD() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/sistema/form_limpiar_bd.fxml"));
            Parent root = loader.load();

            // Crear la escena y el stage
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Limpiar Base de Datos");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo abrir el formulario para limpiar la base de datos.",
                    Alert.AlertType.ERROR);
        }
    }

    /**
     * Muestra un mensaje en un cuadro de diálogo
     */
    private void mostrarMensaje(String titulo, String mensaje, Alert.AlertType tipo) {
        DialogUtil.mostrarMensaje(titulo, mensaje, tipo);
    }

    /**
     * Actualiza la tabla de parámetros
     * Este método puede ser llamado desde otros controladores
     */
    public void actualizarTabla() {
        cargarParametrosPorCategoria(categoriaActual);
    }

    /**
     * Establece el contenedor de formularios para este controlador
     * Este método es necesario para compatibilidad con el MainController
     */
    public void setFormularioContainer(javafx.scene.layout.AnchorPane container) {
        // No requiere implementación específica para este controlador
    }

    /**
     * Método para cerrar el formulario
     * Este método es necesario para compatibilidad con FormParametroController
     */
    public void cerrarFormulario() {
        // No requiere implementación específica para este controlador
    }

    /**
     * Retorna el contexto actual del módulo
     * 
     * @return El nombre del módulo o contexto actual (por ejemplo, "Sistema",
     *         "Facturación")
     */
    public String getContextoActual() {
        return "Sistema"; // Este módulo es específicamente para parámetros del sistema
    }

    /**
     * Retorna la categoría actual de parámetros seleccionada
     * 
     * @return La categoría actualmente seleccionada
     */
    public String getCategoriaActual() {
        return this.categoriaActual;
    }
}
