
package application.controllers.casos_documentacion;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import application.model.Caso;
import java.util.List;

public class ModuloCasosController {
    // Bandera para saber si ya se cargó el contexto desde el cliente
    private boolean contextoClienteYaCargado = false;
    private application.model.Cliente clienteActual;
    @FXML
    private Label lbl_NombreCliente;
    @FXML
    private Label lbl_IdentificacionCliente;

    /**
     * Permite establecer el cliente actual y filtrar los casos por ese cliente.
     */
    public void mostrarCasosDeCliente(application.model.Cliente cliente) {
        this.clienteActual = cliente;
        contextoClienteYaCargado = true;
        if (lbl_NombreCliente != null) {
            lbl_NombreCliente.setText(cliente.getNombreCompleto());
        }
        if (lbl_IdentificacionCliente != null) {
            lbl_IdentificacionCliente.setText(cliente.getNumeroIdentificacion());
        }
        // Filtrar solo los casos del cliente seleccionado
        cargarCasosPorCliente(cliente.getId());
        actualizarBotonRegresar();
    }
    // ...existing code...

    @FXML
    private Button btn_Regresar;

    /**
     * Carga los casos filtrados por el id del cliente.
     */
    private void cargarCasosPorCliente(int clienteId) {
        try {
            tb_Casos.getItems().clear();
            java.sql.Connection conn = application.database.DatabaseConnection.getConnection();
            if (conn == null)
                return;
            String sql = "SELECT * FROM caso WHERE cliente_id = ?";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, clienteId);
            java.sql.ResultSet rs = stmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                Caso caso = new Caso();
                caso.setNumeroExpediente(rs.getString("numero_expediente"));
                caso.setTitulo(rs.getString("titulo"));
                caso.setTipo(rs.getString("tipo"));
                caso.setEstado(rs.getString("estado"));
                java.sql.Date fechaSQL = rs.getDate("fecha_inicio");
                if (fechaSQL != null) {
                    caso.setFechaInicio(new java.util.Date(fechaSQL.getTime()));
                }
                caso.setDescripcion(rs.getString("descripcion"));
                caso.setCliente(clienteActual);
                caso.setClienteId(clienteActual.getId());
                tb_Casos.getItems().add(caso);
            }
            lbl_TotalCasos.setText("Total: " + count + (count == 1 ? " caso" : " casos"));
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            lbl_TotalCasos.setText("Error al cargar casos del cliente");
        }
    }

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
    private TableColumn<Caso, String> tbc_NumeroExpediente, tbc_TituloCaso, tbc_TipoCaso, tbc_FechaInicio,
            tbc_AbogadoAsignado, tbc_Estado;
    @FXML
    private TableColumn<Caso, Void> tbc_BotonEditar, tbc_ButonVisualizar, tbc_BotonDocumentos;
    @FXML
    private AnchorPane pnl_ListView;
    @FXML
    private AnchorPane pnl_DetalleView;
    @FXML
    private TableView<Caso> tb_Casos;

    // Muestra la vista de detalle y la bitácora del caso seleccionado
    private void mostrarDetalleYBitacora(Caso caso) {
        try {
            System.out.println("DEBUG: Cargando vista de bitácora para caso: " + caso.getNumeroExpediente());
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/casos_documentos/detalle_caso_bitacora.fxml"));
            Node detalle = loader.load();
            System.out.println("DEBUG: Vista de bitácora cargada exitosamente");

            DetalleCasoBitacoraController controller = loader.getController();
            if (controller != null) {
                System.out.println("DEBUG: Controlador DetalleCasoBitacoraController obtenido exitosamente");

                // Primero establecemos los datos del caso
                controller.setCaso(caso);
                System.out.println("DEBUG: Datos del caso establecidos en DetalleCasoBitacoraController");

                // Luego configuramos el callback de regresar con una referencia directa al
                // método
                final Runnable regresarCallback = this::cerrarDetalleYMostrarCasos;
                System.out.println("DEBUG: Creado callback de regreso, configurando...");
                controller.setOnRegresar(regresarCallback);
                System.out.println("DEBUG: Callback de regreso configurado en DetalleCasoBitacoraController");
            } else {
                System.err.println("ERROR: No se pudo obtener el controlador DetalleCasoBitacoraController");
            }

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
            actualizarBotonRegresar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cierra la vista de detalle y muestra la lista de casos
     * Este método es llamado desde el controlador DetalleCasoBitacoraController
     * cuando se presiona el botón Regresar
     */
    private void cerrarDetalleYMostrarCasos() {
        try {
            System.out.println("\n===== DEBUG: cerrarDetalleYMostrarCasos INICIANDO =====");
            Thread.dumpStack(); // Imprime el stack trace para ver quién llamó a este método

            // IMPORTANTE: verificar todos los paneles relevantes
            if (pnl_ListView != null) {
                System.out.println("DEBUG: pnl_ListView encontrado, visible = " + pnl_ListView.isVisible());
            } else {
                System.out.println("DEBUG: pnl_ListView es null");
            }

            if (pnl_DetalleView != null) {
                System.out.println("DEBUG: pnl_DetalleView encontrado, visible = " + pnl_DetalleView.isVisible() +
                        ", número de hijos = " + pnl_DetalleView.getChildren().size());
            } else {
                System.out.println("DEBUG: pnl_DetalleView es null");
            }

            if (pnl_Modulos != null) {
                System.out.println("DEBUG: pnl_Modulos encontrado, visible = " + pnl_Modulos.isVisible() +
                        ", número de hijos = " + pnl_Modulos.getChildren().size());
            } else {
                System.out.println("DEBUG: pnl_Modulos es null");
            }

            if (pnl_DetalleView != null && pnl_ListView != null) {
                System.out.println("DEBUG: Cerrando panel de detalle y mostrando lista");

                // Primero mostrar el panel de lista
                pnl_ListView.setVisible(true);
                pnl_ListView.setManaged(true);

                // Luego limpiar y ocultar el panel de detalle
                pnl_DetalleView.getChildren().clear();
                pnl_DetalleView.setVisible(false);
                pnl_DetalleView.setManaged(false);

                // Actualizar la lista de casos
                if (hayClienteSeleccionado()) {
                    cargarCasosPorCliente(clienteActual.getId());
                } else {
                    cargarCasosDesdeBD();
                }

                System.out.println("DEBUG: Vista de lista visible: " + pnl_ListView.isVisible());
            } else if (pnl_Modulos != null) {
                // Volver a cargar el módulo de casos usando MainController para mayor seguridad
                try {
                    System.out.println("DEBUG: Recargando módulo de casos mediante MainController");
                    application.controllers.MainController mainController = application.controllers.MainController
                            .getInstance();
                    if (mainController != null) {
                        System.out.println("DEBUG: MainController encontrado, cargando módulo de casos");
                        mainController.cargarModulo("/views/casos_documentos/modulo_casos.fxml");
                        System.out.println("DEBUG: Módulo de casos recargado con éxito mediante MainController");
                        return; // Importante: retornamos para evitar la ejecución del código siguiente
                    } else {
                        System.err.println("ERROR: No se pudo obtener instancia de MainController");
                        // Continuamos con el método alternativo si MainController falla
                    }
                } catch (Exception e) {
                    System.err
                            .println("ERROR: Error al recargar módulo de casos con MainController: " + e.getMessage());
                    e.printStackTrace();
                }

                // Método alternativo si MainController falla
                try {
                    System.out.println("DEBUG: Recargando módulo de casos mediante método alternativo");
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/views/casos_documentos/modulo_casos.fxml"));
                    Node casosView = loader.load();
                    ModuloCasosController controller = loader.getController();
                    controller.setPanelModulos(pnl_Modulos);
                    if (clienteActual != null) {
                        controller.mostrarCasosDeCliente(clienteActual);
                    }
                    pnl_Modulos.getChildren().setAll(casosView);
                    System.out.println("DEBUG: Módulo de casos recargado con éxito mediante método alternativo");
                } catch (IOException e) {
                    System.err.println("ERROR: Error al recargar módulo de casos: " + e.getMessage());
                    e.printStackTrace();

                    // Último intento de recuperación
                    try {
                        System.out.println("DEBUG: Intentando cargar vista básica como último recurso");
                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/views/casos_documentos/modulo_casos.fxml"));
                        Node alternativeView = loader.load();
                        pnl_Modulos.getChildren().setAll(alternativeView);
                    } catch (Exception ex) {
                        System.err.println("ERROR: No se pudo cargar vista alternativa: " + ex.getMessage());
                    }
                }
            } else {
                System.err.println(
                        "ERROR: No se pudo cerrar el detalle porque pnl_DetalleView, pnl_ListView y pnl_Modulos son null");

                // Intento de recuperación usando MainController
                try {
                    System.out.println("DEBUG: Intentando usar MainController para recuperación");
                    application.controllers.MainController mainController = application.controllers.MainController
                            .getInstance();
                    if (mainController != null) {
                        mainController.cargarModulo("/views/casos_documentos/modulo_casos.fxml");
                        System.out.println("DEBUG: Recuperación exitosa usando MainController");
                    } else {
                        System.err.println("ERROR: No se pudo obtener instancia de MainController");
                    }
                } catch (Exception ex) {
                    System.err.println("ERROR en recuperación con MainController: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO en cerrarDetalleYMostrarCasos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Actualiza la visibilidad y gestión del botón Regresar según si hay cliente
     * seleccionado.
     */
    private void actualizarBotonRegresar() {
        // Usar btn_Nuevo como botón de regreso si hay cliente seleccionado
        if (btn_Nuevo != null) {
            boolean mostrar = hayClienteSeleccionado();
            if (mostrar) {
                btn_Nuevo.setText("⏪ Regresar");
                btn_Nuevo.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #f59e0b, #fbbf24); -fx-text-fill: #1e3a8a; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 8 18; -fx-cursor: hand;");
                btn_Nuevo.setOnAction(_ -> {
                    application.controllers.MainController mainController = application.controllers.MainController
                            .getInstance();
                    if (mainController != null) {
                        mainController.cargarModulo("/views/cliente/modulo_cliente.fxml");
                    }
                });
            } else {
                btn_Nuevo.setText("➕ Nuevo Caso");
                btn_Nuevo.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #10b981, #059669); -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 14px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(16, 185, 129, 0.4), 4, 0, 0, 2);");
                btn_Nuevo.setOnAction(_ -> mostrarFormulario(null, "NUEVO"));
            }
        }
    }

    // Cambiar la lógica para que solo se muestren los casos del cliente
    // seleccionado al navegar desde el módulo de clientes
    // --- NUEVA LÓGICA DE INICIALIZACIÓN ---
    /**
     * Devuelve true si hay un cliente seleccionado
     */
    private boolean hayClienteSeleccionado() {
        return clienteActual != null && clienteActual.getId() > 0;
    }

    /**
     * Carga los casos según el contexto: si hay cliente, solo sus casos; si no,
     * todos
     */
    private void cargarCasosContexto() {
        if (hayClienteSeleccionado()) {
            System.out.println("DEBUG: Cliente seleccionado: " + clienteActual.getNombreCompleto() + " (ID: "
                    + clienteActual.getId() + ")");
            cargarCasosPorCliente(clienteActual.getId());
        } else {
            System.out.println("DEBUG: No hay cliente seleccionado, mostrando todos los casos");
            cargarCasosDesdeBD();
        }
    }

    @FXML
    private void initialize() {
        lbl_TotalCasos.setText("Total: 0 casos");
        tb_Casos.setRowFactory(_ -> {
            TableRow<Caso> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Caso caso = row.getItem();
                    mostrarDetalleYBitacora(caso);
                }
            });
            return row;
        });
        configurarColumnas();
        inicializarColumnasDeBotones();
        if (!contextoClienteYaCargado) {
            cargarCasosContexto();
        }
        actualizarBotonRegresar();
        btn_Nuevo.setOnAction(_ -> mostrarFormulario(null, "NUEVO"));
        btn_Buscar.setOnAction(_ -> buscarCasos());
        btn_Limpiar.setOnAction(_ -> {
            txtf_Buscar.clear();
            cargarCasosContexto();
            actualizarBotonRegresar();
        });
        if (btn_Regresar != null) {
            btn_Regresar.setOnAction(_ -> {
                application.controllers.MainController mainController = application.controllers.MainController
                        .getInstance();
                if (mainController != null) {
                    mainController.cargarModulo("/views/cliente/modulo_cliente.fxml");
                }
            });
        }
    }

    /**
     * Busca casos según el texto introducido en el campo de búsqueda
     */
    // --- MODIFICAR BUSQUEDA PARA RESPETAR EL CONTEXTO ---
    private void buscarCasos() {
        String termino = txtf_Buscar.getText().trim();
        if (termino == null || termino.isEmpty()) {
            cargarCasosContexto();
            actualizarBotonRegresar();
            return;
        }
        try {
            tb_Casos.getItems().clear();
            java.sql.Connection conn = application.database.DatabaseConnection.getConnection();
            if (conn == null) {
                throw new Exception("Conexión a base de datos nula");
            }
            String sql;
            java.sql.PreparedStatement stmt;
            if (hayClienteSeleccionado()) {
                System.out.println("DEBUG: Buscando casos para cliente: " + clienteActual.getNombreCompleto() + " (ID: "
                        + clienteActual.getId() + ")");
                sql = "SELECT * FROM caso WHERE cliente_id = ? AND (numero_expediente LIKE ? OR titulo LIKE ? OR tipo LIKE ? OR estado LIKE ? OR descripcion LIKE ?)";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, clienteActual.getId());
                String busqueda = "%" + termino + "%";
                for (int i = 2; i <= 6; i++) {
                    stmt.setString(i, busqueda);
                }
            } else {
                System.out.println("DEBUG: Buscando casos para todos los clientes");
                sql = "SELECT * FROM caso WHERE numero_expediente LIKE ? OR titulo LIKE ? OR tipo LIKE ? OR estado LIKE ? OR descripcion LIKE ?";
                stmt = conn.prepareStatement(sql);
                String busqueda = "%" + termino + "%";
                for (int i = 1; i <= 5; i++) {
                    stmt.setString(i, busqueda);
                }
            }
            java.sql.ResultSet rs = stmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                Caso caso = new Caso();
                caso.setNumeroExpediente(rs.getString("numero_expediente"));
                caso.setTitulo(rs.getString("titulo"));
                caso.setTipo(rs.getString("tipo"));
                caso.setEstado(rs.getString("estado"));
                java.sql.Date fechaSQL = rs.getDate("fecha_inicio");
                if (fechaSQL != null) {
                    caso.setFechaInicio(new java.util.Date(fechaSQL.getTime()));
                }
                caso.setDescripcion(rs.getString("descripcion"));
                if (hayClienteSeleccionado()) {
                    caso.setCliente(clienteActual);
                    caso.setClienteId(clienteActual.getId());
                }
                tb_Casos.getItems().add(caso);
            }
            System.out.println("DEBUG: Casos encontrados: " + count);
            lbl_TotalCasos.setText((hayClienteSeleccionado() ? "Encontrados: " : "Total: ") + count
                    + (count == 1 ? " caso" : " casos"));
            rs.close();
            stmt.close();
            conn.close();
            actualizarBotonRegresar();
            if (count == 0) {
                Label lblNoData = new Label("No se encontraron casos con el término: '" + termino + "'");
                lblNoData.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
                tb_Casos.setPlaceholder(lblNoData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Label lblError = new Label("Error al buscar casos: " + e.getMessage());
            lblError.setStyle("-fx-font-size: 14px; -fx-text-fill: #c0392b;");
            tb_Casos.setPlaceholder(lblError);
        }
    }

    // --- CORREGIR: Solo cargar todos los casos si NO hay cliente seleccionado ---
    private void cargarCasosDesdeBD() {
        try {
            tb_Casos.getItems().clear();
            java.sql.Connection conn = application.database.DatabaseConnection.getConnection();
            if (conn == null) {
                throw new Exception("Conexión a base de datos nula");
            }
            String sql;
            java.sql.PreparedStatement stmt;
            if (clienteActual == null) {
                sql = "SELECT * FROM caso";
                stmt = conn.prepareStatement(sql);
            } else {
                sql = "SELECT * FROM caso WHERE cliente_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, clienteActual.getId());
            }
            java.sql.ResultSet rs = stmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                Caso caso = new Caso();
                caso.setId(rs.getInt("id"));
                String numeroExpediente = rs.getString("numero_expediente");
                if (numeroExpediente == null || numeroExpediente.isEmpty()) {
                    numeroExpediente = "EXP-" + caso.getId();
                }
                caso.setNumeroExpediente(numeroExpediente);
                caso.setTitulo(rs.getString("titulo"));
                caso.setTipo(rs.getString("tipo"));
                caso.setEstado(rs.getString("estado"));
                java.sql.Date fechaSQL = rs.getDate("fecha_inicio");
                if (fechaSQL != null) {
                    caso.setFechaInicio(new java.util.Date(fechaSQL.getTime()));
                }
                caso.setDescripcion(rs.getString("descripcion"));
                if (clienteActual != null) {
                    caso.setCliente(clienteActual);
                    caso.setClienteId(clienteActual.getId());
                }
                tb_Casos.getItems().add(caso);
            }
            lbl_TotalCasos.setText("Total: " + count + (count == 1 ? " caso" : " casos"));
            rs.close();
            stmt.close();
            conn.close();
            if (count == 0) {
                Label lblNoData = new Label("No hay casos registrados en la base de datos");
                lblNoData.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
                tb_Casos.setPlaceholder(lblNoData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Label lblError = new Label("Error al cargar casos. Verifique la conexión a la base de datos.");
            lblError.setStyle("-fx-font-size: 14px; -fx-text-fill: #d32f2f;");
            tb_Casos.setPlaceholder(lblError);
        }
    }

    private void configurarColumnas() {
        tbc_NumeroExpediente.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNumeroExpediente()));
        tbc_TituloCaso.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitulo()));
        tbc_TipoCaso.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipo()));
        tbc_FechaInicio.setCellValueFactory(d -> {
            java.util.Date fecha = d.getValue().getFechaInicio();
            String fechaStr = (fecha != null) ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(fecha)
                    : "Sin fecha";
            return new SimpleStringProperty(fechaStr);
        });
        tbc_AbogadoAsignado.setCellValueFactory(d -> {
            List<application.model.AbogadoCaso> abogados = d.getValue().getAbogados();
            String nombreAbogado = (abogados != null && !abogados.isEmpty() && abogados.get(0) != null
                    && abogados.get(0).getNombre() != null) ? abogados.get(0).getNombre() : "";
            return new SimpleStringProperty(nombreAbogado);
        });
        tbc_Estado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEstado()));
    }

    private void inicializarColumnasDeBotones() {
        agregarBoton(tbc_BotonEditar, "✎", "Editar");
        agregarBoton(tbc_ButonVisualizar, "👁", "Ver");
        agregarBotonDocumentos(tbc_BotonDocumentos, "📄", "Ver Documentos");
    }

    private void agregarBoton(TableColumn<Caso, Void> columna, String texto, String tooltip) {
        columna.setCellFactory(tc -> new TableCell<Caso, Void>() {
            final Button btn = new Button(texto);
            {
                btn.setTooltip(new Tooltip(tooltip));
                btn.setOnAction(_ -> {
                    int idx = getIndex();
                    if (idx >= 0 && idx < getTableView().getItems().size()) {
                        Caso caso = getTableView().getItems().get(idx);
                        if ("✎".equals(texto)) {
                            System.out.println(
                                    "DEBUG: Abriendo formulario de edición para caso: " + caso.getNumeroExpediente());
                            mostrarFormulario(caso, "EDITAR");
                        } else {
                            System.out.println(
                                    "DEBUG: Abriendo vista de detalle para caso: " + caso.getNumeroExpediente());
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

    private void agregarBotonDocumentos(TableColumn<Caso, Void> columna, String texto, String tooltip) {
        columna.setCellFactory(tc -> new TableCell<Caso, Void>() {
            final Button btn = new Button(texto);
            {
                btn.setTooltip(new Tooltip(tooltip));
                btn.setOnAction(_ -> {
                    int idx = getIndex();
                    if (idx >= 0 && idx < getTableView().getItems().size()) {
                        Caso caso = getTableView().getItems().get(idx);
                        System.out
                                .println("DEBUG: Botón documentos presionado para caso: " + caso.getNumeroExpediente());
                        mostrarDocumentosCaso(caso);
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

    private void mostrarDocumentosCaso(Caso caso) {
        try {
            System.out.println(
                    "\n===== DEBUG: Iniciando mostrarDocumentosCaso para: " + caso.getNumeroExpediente() + " =====");

            // Cargar la vista de documentos
            System.out.println("DEBUG: Cargando vista de documentos...");
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/casos_documentos/modulo_casos_documentacion_documentos.fxml"));

            System.out.println("DEBUG: Antes de loader.load()");
            Node documentosView = loader.load();
            System.out.println("DEBUG: Vista de documentos cargada exitosamente");

            // Configurar el controlador
            System.out.println("DEBUG: Obteniendo controlador de documentos...");
            ModuloDocumentosController controller = loader.getController();

            if (controller == null) {
                System.err.println("ERROR CRÍTICO: No se pudo obtener ModuloDocumentosController");
                throw new RuntimeException("No se pudo obtener el controlador ModuloDocumentosController");
            }

            System.out.println("DEBUG: Controlador ModuloDocumentosController obtenido exitosamente");

            // Establecer el número de expediente del caso seleccionado
            System.out
                    .println("DEBUG: Estableciendo número de expediente en controlador: " + caso.getNumeroExpediente());
            controller.setNumeroExpediente(caso.getNumeroExpediente());
            System.out.println("DEBUG: Número de expediente establecido correctamente");

            // Ya no configuramos el callback para el regreso, porque el
            // ModuloDocumentosController
            // ahora usa directamente MainController para navegar, lo cual es más confiable

            if (pnl_Modulos != null) {
                // Configurar anclajes para la vista en el panel principal
                AnchorPane.setTopAnchor(documentosView, 0.0);
                AnchorPane.setBottomAnchor(documentosView, 0.0);
                AnchorPane.setLeftAnchor(documentosView, 0.0);
                AnchorPane.setRightAnchor(documentosView, 0.0);

                // Limpiar y mostrar la vista
                pnl_Modulos.getChildren().clear();
                pnl_Modulos.getChildren().add(documentosView);
                System.out.println("DEBUG: Vista de documentos cargada para el caso: " + caso.getNumeroExpediente());
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
                        "DEBUG: Vista de documentos cargada en panel de detalle para caso: "
                                + caso.getNumeroExpediente());
            } else {
                System.err.println("ERROR: No se encontró un panel para mostrar la vista de documentos");
            }
        } catch (Exception ex) {
            System.err.println("ERROR al mostrar documentos del caso: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void mostrarFormulario(Caso caso, String modo) {
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
                System.out.println("DEBUG: Cargando datos del caso para " + modo + ": " + caso.getNumeroExpediente());
                String fechaStr = "Sin fecha";
                if (caso.getFechaInicio() != null) {
                    fechaStr = new java.text.SimpleDateFormat("dd/MM/yyyy").format(caso.getFechaInicio());
                }
                controller.cargarDatosCaso(
                        caso.getNumeroExpediente(),
                        caso.getTitulo(),
                        caso.getTipo(),
                        fechaStr,
                        caso.getEstado(),
                        caso.getDescripcion());
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

            parallelTransition.setOnFinished(_ -> {
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

    // Método cargarDatosEjemplo() comentado porque no se usa
    // private void cargarDatosEjemplo() {
    // // En lugar de cargar datos de ejemplo, mostramos la tabla vacía o un mensaje
    // System.out.println("INFO: No se pudieron cargar datos desde la base de
    // datos");
    // // Mostrar un mensaje en la tabla si está vacía
    // if (tb_Casos.getItems().isEmpty()) {
    // Label lblNoData = new Label("No hay casos disponibles");
    // lblNoData.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
    // tb_Casos.setPlaceholder(lblNoData);
    // }
    // // Opcionalmente, podrías mostrar un diálogo al usuario
    // /*
    // * javafx.application.Platform.runLater(() -> {
    // * Alert alert = new Alert(Alert.AlertType.INFORMATION);
    // * alert.setTitle("Información");
    // * alert.setHeaderText("No hay datos disponibles");
    // * alert.
    // * setContentText("No se pudieron cargar los casos desde la base de datos.");
    // * alert.showAndWait();
    // * });
    // */
    // }
}