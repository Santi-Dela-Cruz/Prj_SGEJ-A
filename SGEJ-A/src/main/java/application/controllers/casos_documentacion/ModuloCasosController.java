package application.controllers.casos_documentacion;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.event.ActionEvent;

import java.io.IOException;

public class ModuloCasosController {
    // Referencia al panel principal de módulos
    private AnchorPane pnl_Modulos;

    // Permite inyectar el panel desde MainController
    public void setPanelModulos(AnchorPane panel) {
        this.pnl_Modulos = panel;
    }

    @FXML
    private Button btn_Nuevo;
    @FXML
    private Button btn_Buscar;
    @FXML
    private Button btn_Limpiar;
    @FXML
    private TextField txtf_Buscar;
    @FXML
    private Label lbl_TotalCasos;
    @FXML
    private TableColumn<CasoDemo, String> tbc_NumeroExpediente, tbc_TituloCaso, tbc_TipoCaso, tbc_FechaInicio,
            tbc_AbogadoAsignado, tbc_Estado;
    @FXML
    private TableColumn<CasoDemo, Void> tbc_BotonEditar, tbc_ButonVisualizar, tbc_BotonDocumentos;
    @FXML
    private AnchorPane pnl_ListView;
    @FXML
    private AnchorPane pnl_DetalleView;
    @FXML
    private TableView<CasoDemo> tb_Casos;

    // Muestra la vista de detalle y la bitácora del caso seleccionado
    private void mostrarDetalleYBitacora(CasoDemo caso) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/casos_documentos/detalle_caso_bitacora.fxml"));
            Node detalle = loader.load();
            DetalleCasoBitacoraController controller = loader.getController();
            controller.setCaso(caso);
            controller.setOnRegresar(() -> cerrarDetalleYMostrarCasos());

            if (pnl_Modulos != null) {
                AnchorPane.setTopAnchor(detalle, 0.0);
                AnchorPane.setBottomAnchor(detalle, 0.0);
                AnchorPane.setLeftAnchor(detalle, 0.0);
                AnchorPane.setRightAnchor(detalle, 0.0);
                pnl_Modulos.getChildren().setAll(detalle);
            } else {
                pnl_DetalleView.getChildren().setAll(detalle);
                pnl_DetalleView.setVisible(true);
                pnl_DetalleView.setManaged(true);
                pnl_ListView.setVisible(false);
                pnl_ListView.setManaged(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cerrarDetalleYMostrarCasos() {
        System.out.println("DEBUG: Iniciando cerrarDetalleYMostrarCasos()");
        try {
            if (pnl_Modulos != null) {
                System.out.println("DEBUG: pnl_Modulos encontrado, navegando a módulo principal...");
                try {
                    // Enfoque simple: limpiamos el panel
                    pnl_Modulos.getChildren().clear();

                    // Cargamos el módulo de casos directamente
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(
                            getClass().getResource("/views/casos_documentos/modulo_casos_documentacion_casos.fxml"));
                    Node vista = loader.load();

                    // Configurar el nuevo controlador si es necesario
                    ModuloCasosController controller = loader.getController();
                    if (controller != null) {
                        controller.setPanelModulos(pnl_Modulos);
                    }

                    // Configuración crucial: establecer los constraints de anclaje para que la
                    // vista ocupe todo el panel
                    AnchorPane.setTopAnchor(vista, 0.0);
                    AnchorPane.setBottomAnchor(vista, 0.0);
                    AnchorPane.setLeftAnchor(vista, 0.0);
                    AnchorPane.setRightAnchor(vista, 0.0);

                    // Mostrar la vista
                    pnl_Modulos.getChildren().add(vista);
                    System.out.println("DEBUG: Vista principal cargada exitosamente");

                    // Forzar actualización de la UI
                    pnl_Modulos.requestLayout();
                } catch (Exception ex) {
                    System.err.println("ERROR al cargar módulo de casos: " + ex.getMessage());
                    ex.printStackTrace();

                    // Intento de recuperación mediante carga de vista básica
                    try {
                        System.out.println("DEBUG: Intentando cargar una vista de recuperación simple");
                        javafx.scene.layout.VBox vistaSencilla = new javafx.scene.layout.VBox();
                        vistaSencilla.setStyle("-fx-background-color: white; -fx-padding: 20;");

                        Label lblInfo = new Label("Se ha regresado al módulo de casos");
                        lblInfo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                        Button btnRecargar = new Button("Recargar módulo");
                        btnRecargar.setOnAction(e -> cerrarDetalleYMostrarCasos());

                        vistaSencilla.getChildren().addAll(lblInfo, new javafx.scene.control.Separator(), btnRecargar);
                        vistaSencilla.setSpacing(15);

                        AnchorPane.setTopAnchor(vistaSencilla, 0.0);
                        AnchorPane.setBottomAnchor(vistaSencilla, 0.0);
                        AnchorPane.setLeftAnchor(vistaSencilla, 0.0);
                        AnchorPane.setRightAnchor(vistaSencilla, 0.0);

                        pnl_Modulos.getChildren().add(vistaSencilla);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (pnl_DetalleView != null && pnl_ListView != null) {
                System.out.println("DEBUG: Usando pnl_DetalleView/pnl_ListView para navegación");
                pnl_DetalleView.getChildren().clear();
                pnl_DetalleView.setVisible(false);
                pnl_DetalleView.setManaged(false);
                pnl_ListView.setVisible(true);
                pnl_ListView.setManaged(true);
            } else {
                System.err.println("ERROR: No se encontraron paneles para navegación");
            }
            System.out.println("DEBUG: cerrarDetalleYMostrarCasos completado");
        } catch (Exception ex) {
            System.err.println("ERROR en cerrarDetalleYMostrarCasos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        // Inicializar contador de casos
        lbl_TotalCasos.setText("Total: 0 casos");

        // Configurar doble clic en fila para ver detalles
        tb_Casos.setRowFactory(tv -> {
            TableRow<CasoDemo> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    CasoDemo caso = row.getItem();
                    mostrarDetalleYBitacora(caso);
                }
            });
            return row;
        });

        // Configurar columnas y botones
        configurarColumnas();
        inicializarColumnasDeBotones();

        // Cargar datos iniciales
        cargarCasosDesdeBD();

        // Configurar acciones de botones
        btn_Nuevo.setOnAction(e -> mostrarFormulario(null, "NUEVO"));
        btn_Buscar.setOnAction(e -> buscarCasos());
        btn_Limpiar.setOnAction(e -> {
            txtf_Buscar.clear();
            cargarCasosDesdeBD();
        });
    }

    /**
     * Busca casos según el texto introducido en el campo de búsqueda
     */
    private void buscarCasos() {
        String termino = txtf_Buscar.getText().trim();
        if (termino == null || termino.isEmpty()) {
            cargarCasosDesdeBD();
            return;
        }

        try {
            System.out.println("INFO: Buscando casos con término: '" + termino + "'");

            // Limpiar la tabla primero
            tb_Casos.getItems().clear();

            try {
                // Conectar a la base de datos
                java.sql.Connection conn = application.database.DatabaseConnection.getConnection();
                if (conn == null) {
                    System.err.println("ERROR: No se pudo conectar a la base de datos");
                    throw new Exception("Conexión a base de datos nula");
                }

                // Consulta SQL con búsqueda
                String sql = "SELECT * FROM caso WHERE " +
                        "numero_expediente LIKE ? OR " +
                        "titulo LIKE ? OR " +
                        "tipo LIKE ? OR " +
                        "estado LIKE ? OR " +
                        "descripcion LIKE ?";

                String busqueda = "%" + termino + "%";
                java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
                for (int i = 1; i <= 5; i++) {
                    stmt.setString(i, busqueda);
                }

                java.sql.ResultSet rs = stmt.executeQuery();

                int count = 0;
                while (rs.next()) {
                    count++;
                    int id = rs.getInt("id");
                    String numeroExpediente = rs.getString("numero_expediente");
                    String titulo = rs.getString("titulo");
                    String tipo = rs.getString("tipo");
                    String estado = rs.getString("estado");

                    // Si no hay número de expediente, usar el ID
                    if (numeroExpediente == null || numeroExpediente.isEmpty()) {
                        numeroExpediente = "EXP-" + id;
                    }

                    // Formatear la fecha
                    String fecha = "Sin fecha";
                    java.sql.Date fechaSQL = rs.getDate("fecha_inicio");
                    if (fechaSQL != null) {
                        fecha = new java.text.SimpleDateFormat("dd/MM/yyyy").format(fechaSQL);
                    }

                    // Intentar obtener la descripción
                    String descripcion = rs.getString("descripcion");

                    // Añadir a la tabla
                    tb_Casos.getItems().add(new CasoDemo(
                            numeroExpediente,
                            titulo,
                            tipo,
                            fecha,
                            "Cliente " + id, // Placeholder para el cliente
                            estado,
                            descripcion));
                }

                // Actualizar contador de resultados
                lbl_TotalCasos.setText("Encontrados: " + count + (count == 1 ? " caso" : " casos"));

                System.out.println("INFO: Se encontraron " + count + " casos con el término '" + termino + "'");

                // Cerrar recursos
                rs.close();
                stmt.close();
                conn.close();

                if (count == 0) {
                    Label lblNoData = new Label("No se encontraron casos con el término: '" + termino + "'");
                    lblNoData.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
                    tb_Casos.setPlaceholder(lblNoData);
                }
            } catch (Exception e) {
                System.err.println("ERROR en búsqueda: " + e.getMessage());
                e.printStackTrace();

                // Mostrar mensaje de error
                Label lblError = new Label("Error al buscar casos: " + e.getMessage());
                lblError.setStyle("-fx-font-size: 14px; -fx-text-fill: #c0392b;");
                tb_Casos.setPlaceholder(lblError);
            }
        } catch (Exception ex) {
            System.err.println("ERROR general en búsqueda: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void cargarCasosDesdeBD() {
        try {
            System.out.println("INFO: Iniciando carga de casos desde la base de datos...");

            // Limpiar la tabla primero
            tb_Casos.getItems().clear();

            try {
                // Conectar a la base de datos
                java.sql.Connection conn = application.database.DatabaseConnection.getConnection();
                if (conn == null) {
                    System.err.println("ERROR: No se pudo conectar a la base de datos");
                    throw new Exception("Conexión a base de datos nula");
                }

                System.out.println("INFO: Conexión a base de datos establecida");

                // Consulta SQL simplificada
                String sql = "SELECT * FROM caso";

                java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
                java.sql.ResultSet rs = stmt.executeQuery();

                int count = 0;
                while (rs.next()) {
                    count++;
                    int id = rs.getInt("id");
                    String numeroExpediente = rs.getString("numero_expediente");
                    String titulo = rs.getString("titulo");
                    String tipo = rs.getString("tipo");
                    String estado = rs.getString("estado");

                    // Si no hay número de expediente, usar el ID
                    if (numeroExpediente == null || numeroExpediente.isEmpty()) {
                        numeroExpediente = "EXP-" + id;
                    }

                    // Formatear la fecha
                    String fecha = "Sin fecha";
                    java.sql.Date fechaSQL = rs.getDate("fecha_inicio");
                    if (fechaSQL != null) {
                        fecha = new java.text.SimpleDateFormat("dd/MM/yyyy").format(fechaSQL);
                    }

                    System.out.println("INFO: Cargando caso #" + id + ": " + numeroExpediente + " - " + titulo);

                    // Intentar obtener la descripción (puede ser null en algunos casos)
                    String descripcion = rs.getString("descripcion");

                    // Añadir a la tabla
                    tb_Casos.getItems().add(new CasoDemo(
                            numeroExpediente,
                            titulo,
                            tipo,
                            fecha,
                            "Cliente " + id, // Placeholder para el cliente
                            estado,
                            descripcion)); // Agregar descripción
                }

                System.out.println("INFO: Se cargaron " + count + " casos desde la base de datos");

                // Cerrar recursos
                rs.close();
                stmt.close();
                conn.close();

                if (count == 0) {
                    System.out.println("ADVERTENCIA: No se encontraron casos en la base de datos");
                    // Ya no cargamos datos de ejemplo si la base de datos está vacía
                    Label lblNoData = new Label("No hay casos registrados en la base de datos");
                    lblNoData.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
                    tb_Casos.setPlaceholder(lblNoData);
                }

                // Actualizar contador de casos
                lbl_TotalCasos.setText("Total: " + count + (count == 1 ? " caso" : " casos"));

            } catch (Exception e) {
                System.err.println("ERROR en conexión o consulta SQL: " + e.getMessage());
                e.printStackTrace();

                // Configurar un mensaje de error en la tabla
                Label lblError = new Label("Error al conectar con la base de datos");
                lblError.setStyle("-fx-font-size: 14px; -fx-text-fill: #d32f2f;");
                tb_Casos.setPlaceholder(lblError);
            }

            System.out.println("INFO: Cargados " + tb_Casos.getItems().size() + " casos desde la base de datos");

            // Si no hay casos, mostrar un mensaje informativo
            if (tb_Casos.getItems().isEmpty()) {
                System.out.println("INFO: No se encontraron casos en la base de datos");
                // Configurar un mensaje en la tabla vacía
                Label lblNoData = new Label("No hay casos registrados en el sistema");
                lblNoData.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
                tb_Casos.setPlaceholder(lblNoData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR al cargar casos desde la BD: " + e.getMessage());

            // Mostrar mensaje de error en la tabla
            Label lblError = new Label("Error al cargar casos. Verifique la conexión a la base de datos.");
            lblError.setStyle("-fx-font-size: 14px; -fx-text-fill: #d32f2f;");
            tb_Casos.setPlaceholder(lblError);
        }
    }

    private void configurarColumnas() {
        tbc_NumeroExpediente.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().numeroExpediente()));
        tbc_TituloCaso.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().titulo()));
        tbc_TipoCaso.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tipo()));
        tbc_FechaInicio.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fecha()));
        tbc_AbogadoAsignado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().abogado()));
        tbc_Estado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().estado()));
    }

    private void inicializarColumnasDeBotones() {
        agregarBoton(tbc_BotonEditar, "✎", "Editar");
        agregarBoton(tbc_ButonVisualizar, "👁", "Ver");
        agregarBotonDocumentos(tbc_BotonDocumentos, "📄", "Ver Documentos");
    }

    private void agregarBoton(TableColumn<CasoDemo, Void> columna, String texto, String tooltip) {
        columna.setCellFactory(param -> new TableCell<>() {
            final Button btn = new Button(texto);

            {
                btn.setTooltip(new Tooltip(tooltip));
                btn.setOnAction(e -> {
                    if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                        CasoDemo caso = getTableView().getItems().get(getIndex());
                        if ("✎".equals(texto)) {
                            // Si es el botón de editar, abrir el formulario de edición
                            System.out.println(
                                    "DEBUG: Abriendo formulario de edición para caso: " + caso.numeroExpediente());
                            mostrarFormulario(caso, "EDITAR");
                        } else {
                            // Si es cualquier otro botón, mostrar el detalle
                            System.out
                                    .println("DEBUG: Abriendo vista de detalle para caso: " + caso.numeroExpediente());
                            mostrarDetalleYBitacora(caso);
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

    private void agregarBotonDocumentos(TableColumn<CasoDemo, Void> columna, String texto, String tooltip) {
        columna.setCellFactory(param -> new TableCell<>() {
            final Button btn = new Button(texto);

            {
                btn.setTooltip(new Tooltip(tooltip));
                btn.setOnAction(e -> {
                    CasoDemo caso = getTableView().getItems().get(getIndex());
                    mostrarDocumentosCaso(caso);
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

    private void mostrarDocumentosCaso(CasoDemo caso) {
        try {
            // Cargar la vista de documentos
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/casos_documentos/modulo_casos_documentacion_documentos.fxml"));
            Node documentosView = loader.load();

            // Configurar el controlador
            ModuloDocumentosController controller = loader.getController();

            // Establecer el número de expediente del caso seleccionado
            controller.setNumeroExpediente(caso.numeroExpediente());

            // Configurar la acción para regresar a la lista de casos
            controller.setOnRegresar(() -> cerrarDetalleYMostrarCasos());

            if (pnl_Modulos != null) {
                // Configurar anclajes para la vista en el panel principal
                AnchorPane.setTopAnchor(documentosView, 0.0);
                AnchorPane.setBottomAnchor(documentosView, 0.0);
                AnchorPane.setLeftAnchor(documentosView, 0.0);
                AnchorPane.setRightAnchor(documentosView, 0.0);

                // Limpiar y mostrar la vista
                pnl_Modulos.getChildren().clear();
                pnl_Modulos.getChildren().add(documentosView);
                System.out.println("DEBUG: Vista de documentos cargada para el caso: " + caso.numeroExpediente());
            } else if (pnl_DetalleView != null) {
                // Alternativa si estamos usando el panel de detalle
                pnl_ListView.setVisible(false);
                pnl_ListView.setManaged(false);

                // Limpiar y configurar el panel de detalle
                pnl_DetalleView.getChildren().clear();
                pnl_DetalleView.getChildren().add(documentosView);
                pnl_DetalleView.setVisible(true);
                pnl_DetalleView.setManaged(true);

                // Establecer anclajes
                AnchorPane.setTopAnchor(documentosView, 0.0);
                AnchorPane.setBottomAnchor(documentosView, 0.0);
                AnchorPane.setLeftAnchor(documentosView, 0.0);
                AnchorPane.setRightAnchor(documentosView, 0.0);

                System.out.println(
                        "DEBUG: Vista de documentos cargada en panel de detalle para caso: " + caso.numeroExpediente());
            } else {
                System.err.println("ERROR: No se encontró un panel para mostrar la vista de documentos");
            }
        } catch (Exception ex) {
            System.err.println("ERROR al mostrar documentos del caso: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void mostrarFormulario(CasoDemo caso, String modo) {
        try {
            System.out.println("DEBUG: Iniciando mostrarFormulario con modo: " + modo);

            // Crear o reutilizar el panel para el formulario
            if (pnl_DetalleView == null) {
                pnl_DetalleView = new AnchorPane();

                // Añadimos el panel sobrepuesto al panel principal
                AnchorPane panelPrincipal = (AnchorPane) tb_Casos.getParent();
                panelPrincipal.getChildren().add(pnl_DetalleView);

                // Inicialmente oculto
                pnl_DetalleView.setVisible(false);
                pnl_DetalleView.setManaged(false);
            }

            System.out.println("DEBUG: Cargando formulario de casos...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/casos_documentos/form_casos.fxml"));
            AnchorPane form = loader.load();
            System.out.println("DEBUG: Formulario cargado exitosamente");

            FormCasosController controller = loader.getController();
            controller.setOnCancelar(this::cerrarFormulario);
            controller.setOnGuardar(this::cerrarFormulario);
            controller.setModo(modo);
            System.out.println("DEBUG: Modo establecido en el controlador: " + modo);

            if (caso != null && !modo.equals("NUEVO")) {
                System.out.println("DEBUG: Cargando datos del caso para " + modo + ": " + caso.numeroExpediente());
                controller.cargarDatosCaso(
                        caso.numeroExpediente(),
                        caso.titulo(),
                        caso.tipo(),
                        caso.fecha(),
                        caso.estado(),
                        caso.descripcion());
                System.out.println("DEBUG: Datos del caso cargados exitosamente");
            }

            // Posicionar el formulario en el panel
            pnl_DetalleView.getChildren().setAll(form);

            // Posicionamos el formulario en el borde derecho
            AnchorPane.setTopAnchor(pnl_DetalleView, 60.0); // Espacio para el título
            AnchorPane.setRightAnchor(pnl_DetalleView, 30.0); // Margen desde la derecha
            AnchorPane.setLeftAnchor(pnl_DetalleView, null); // Importante: quitar el anclaje izquierdo para que
                                                             // aparezca desde la derecha
            AnchorPane.setBottomAnchor(pnl_DetalleView, 30.0); // Margen desde abajo

            // Asegurarnos que el formulario se muestre desde la derecha
            form.setTranslateX(550); // Inicialmente fuera de la pantalla
            form.setOpacity(0);

            // Animación para deslizar desde la derecha
            javafx.animation.TranslateTransition translateTransition = new javafx.animation.TranslateTransition(
                    javafx.util.Duration.millis(300), form);
            translateTransition.setToX(0);
            translateTransition.play();

            // Animación para aparecer gradualmente
            javafx.animation.FadeTransition fadeTransition = new javafx.animation.FadeTransition(
                    javafx.util.Duration.millis(300), form);
            fadeTransition.setToValue(1.0);
            fadeTransition.play();

            // Hacer el panel visible
            pnl_DetalleView.setVisible(true);
            pnl_DetalleView.setManaged(true);

            // Efecto de animación (desplazamiento desde la derecha)
            javafx.animation.TranslateTransition tt = new javafx.animation.TranslateTransition(
                    javafx.util.Duration.millis(250), form);
            form.setTranslateX(400); // Comienza fuera de la pantalla
            tt.setToX(0);
            tt.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cerrarFormulario() {
        if (pnl_DetalleView != null && !pnl_DetalleView.getChildren().isEmpty()) {
            Node form = pnl_DetalleView.getChildren().get(0);

            // Animación de salida - deslizar hacia la derecha
            javafx.animation.TranslateTransition translateOut = new javafx.animation.TranslateTransition(
                    javafx.util.Duration.millis(300), form);
            translateOut.setToX(550); // Sale por la derecha

            // Animación de desvanecimiento
            javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(
                    javafx.util.Duration.millis(300), form);
            fadeOut.setToValue(0.0);

            // Reproducir ambas animaciones en paralelo
            javafx.animation.ParallelTransition parallelTransition = new javafx.animation.ParallelTransition(
                    translateOut, fadeOut);

            parallelTransition.setOnFinished(event -> {
                pnl_DetalleView.getChildren().clear();
                pnl_DetalleView.setVisible(false);
                pnl_DetalleView.setManaged(false);

                // Actualizar la lista de casos desde la BD
                cargarCasosDesdeBD();
            });
            parallelTransition.play();
        } else {
            // Por si acaso no hay animación
            if (pnl_DetalleView != null) {
                pnl_DetalleView.getChildren().clear();
                pnl_DetalleView.setVisible(false);
                pnl_DetalleView.setManaged(false);

                // Actualizar la lista de casos desde la BD
                cargarCasosDesdeBD();
            }
        }
    }

    private void cargarDatosEjemplo() {
        // En lugar de cargar datos de ejemplo, mostramos la tabla vacía o un mensaje
        System.out.println("INFO: No se pudieron cargar datos desde la base de datos");

        // Mostrar un mensaje en la tabla si está vacía
        if (tb_Casos.getItems().isEmpty()) {
            Label lblNoData = new Label("No hay casos disponibles");
            lblNoData.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
            tb_Casos.setPlaceholder(lblNoData);
        }

        // Opcionalmente, podrías mostrar un diálogo al usuario
        /*
         * javafx.application.Platform.runLater(() -> {
         * Alert alert = new Alert(Alert.AlertType.INFORMATION);
         * alert.setTitle("Información");
         * alert.setHeaderText("No hay datos disponibles");
         * alert.
         * setContentText("No se pudieron cargar los casos desde la base de datos.");
         * alert.showAndWait();
         * });
         */
    }

    public record CasoDemo(
            String numeroExpediente,
            String titulo,
            String tipo,
            String fecha,
            String abogado,
            String estado,
            String descripcion) {
    }
}