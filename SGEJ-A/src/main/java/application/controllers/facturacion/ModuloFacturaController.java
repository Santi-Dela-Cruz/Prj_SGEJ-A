package application.controllers.facturacion;

import application.controllers.factura.FormFacturaController;
import application.model.Factura;
import application.service.FacturaService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModuloFacturaController {

    @FXML
    private Button btn_Nuevo;
    @FXML
    private Button btn_Buscar;
    @FXML
    private TextField txt_Busqueda;

    @FXML
    private TableView<Factura> tb_Facturas;
    @FXML
    private TableColumn<Factura, String> tbc_NumeroFactura;
    @FXML
    private TableColumn<Factura, String> tbc_FechaEmision;
    @FXML
    private TableColumn<Factura, String> tbc_NombreCliente;
    @FXML
    private TableColumn<Factura, String> tbc_NumExpediente;
    @FXML
    private TableColumn<Factura, String> tbc_Total;
    @FXML
    private TableColumn<Factura, String> tbc_EstadoFactura;
    @FXML
    private TableColumn<Factura, String> tbc_PagoRealizado;

    @FXML
    private TableColumn<Factura, Void> tbc_BotonEditar;
    @FXML
    private TableColumn<Factura, Void> tbc_BotonVer;
    @FXML
    private TableColumn<Factura, Void> tbc_BotonDescargar;
    @FXML
    private TableColumn<Factura, Void> tbc_BotonEmail;

    private Pane pnl_Forms;

    public void setFormularioContainer(Pane pnl_Forms) {
        this.pnl_Forms = pnl_Forms;
    }

    private FacturaService facturaService;

    @FXML
    @SuppressWarnings("unused")
    private void initialize() {
        facturaService = new FacturaService();

        btn_Nuevo.setOnAction(__ -> mostrarFormulario(null, "NUEVO"));
        btn_Buscar.setOnAction(__ -> buscarFacturas());

        configurarColumnasTexto();
        inicializarColumnasDeBotones();
        cargarDatosDesdeBD();
        ocultarEncabezadosColumnasDeAccion();
    }

    private void buscarFacturas() {
        String termino = txt_Busqueda.getText().trim();
        List<Factura> facturas = facturaService.buscarFacturas(termino);

        tb_Facturas.setItems(FXCollections.observableArrayList(facturas));
    }

    @SuppressWarnings("unused")
    private void ocultarEncabezadosColumnasDeAccion() {
        tb_Facturas.widthProperty().addListener((__, ___, ____) -> {
            Node header = tb_Facturas.lookup("TableHeaderRow");
            if (header != null)
                header.setVisible(true);
        });
    }

    private void mostrarFormulario(Factura factura, String modo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/factura/form_factura.fxml"));
            Node form = loader.load();

            FormFacturaController controller = loader.getController();
            if (controller != null) {
                if (factura != null) {
                    controller.cargarFactura(factura);
                }
                // El modo se puede manejar con un método adicional si es necesario
                // Por ahora, el controller determina el modo según si la factura es nueva o
                // existente
            }

            // Configuramos la vista en el contenedor
            AnchorPane.setTopAnchor(form, 0.0);
            AnchorPane.setBottomAnchor(form, 0.0);
            AnchorPane.setLeftAnchor(form, 0.0);
            AnchorPane.setRightAnchor(form, 0.0);

            pnl_Forms.getChildren().setAll(form);
            pnl_Forms.setVisible(true);
            pnl_Forms.setManaged(true);

        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al cargar formulario de factura", e);
        }
    }

    // Method removed as it was unused

    private void configurarColumnasTexto() {
        // Formato para las fechas
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Formato para valores monetarios
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-EC"));

        tbc_NumeroFactura.setCellValueFactory(data -> {
            Factura factura = data.getValue();
            if (factura.getCodigoEstablecimiento() != null && factura.getCodigoPuntoEmision() != null
                    && factura.getSecuencial() != null) {
                String numero = factura.getCodigoEstablecimiento() + "-" +
                        factura.getCodigoPuntoEmision() + "-" +
                        factura.getSecuencial();
                return new SimpleStringProperty(numero);
            } else {
                return new SimpleStringProperty("N/A");
            }
        });

        tbc_FechaEmision.setCellValueFactory(data -> {
            Factura factura = data.getValue();
            if (factura.getFechaEmision() != null) {
                return new SimpleStringProperty(factura.getFechaEmision().format(dateFormatter));
            }
            return new SimpleStringProperty("");
        });

        tbc_NombreCliente.setCellValueFactory(data -> {
            String nombre = data.getValue().getNombreCliente();
            return new SimpleStringProperty(nombre != null ? nombre : "");
        });

        tbc_NumExpediente.setCellValueFactory(data -> {
            String expediente = data.getValue().getNumeroExpediente();
            return new SimpleStringProperty(expediente != null ? expediente : "");
        });

        tbc_Total.setCellValueFactory(data -> {
            Factura factura = data.getValue();
            if (factura.getValorTotal() != null) {
                return new SimpleStringProperty(currencyFormat.format(factura.getValorTotal()));
            }
            return new SimpleStringProperty("$0.00");
        });

        tbc_EstadoFactura.setCellValueFactory(data -> {
            String estado = data.getValue().getEstadoFactura();
            return new SimpleStringProperty(estado != null ? estado : "Pendiente");
        });

        tbc_PagoRealizado
                .setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isPagoRealizado() ? "Sí" : "No"));
    }

    private void inicializarColumnasDeBotones() {
        agregarBotonPorColumna(tbc_BotonEditar, "✎", "Editar");
        agregarBotonPorColumna(tbc_BotonVer, "👁", "Ver");
        agregarBotonPorColumna(tbc_BotonDescargar, "⬇", "Descargar");
        agregarBotonPorColumna(tbc_BotonEmail, "📧", "Email");

        tbc_BotonEditar.setPrefWidth(40);
        tbc_BotonVer.setPrefWidth(40);
        tbc_BotonEmail.setPrefWidth(40);
    }

    @SuppressWarnings("unused")
    private void agregarBotonPorColumna(TableColumn<Factura, Void> columna, String texto, String tooltip) {
        columna.getStyleClass().add("column-action");

        columna.setCellFactory(__ -> new TableCell<>() {
            private final Button btn = new Button(texto);

            {
                btn.getStyleClass().add("table-button");
                setStyle("-fx-alignment: CENTER;");
                btn.setTooltip(new Tooltip(tooltip));
                btn.setOnAction(__ -> {
                    Factura factura = getTableView().getItems().get(getIndex());
                    if ("Editar".equals(tooltip)) {
                        mostrarFormulario(factura, "EDITAR");
                    } else if ("Ver".equals(tooltip)) {
                        mostrarFormulario(factura, "VER");
                    } else if ("Descargar".equals(tooltip)) {
                        descargarFactura(factura);
                    } else if ("Email".equals(tooltip)) {
                        enviarFacturaPorEmail(factura);
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

    private void descargarFactura(Factura factura) {
        // Implementar lógica para descargar factura
        System.out.println("Descargando factura: " + factura.getId());

        try {
            String rutaPDF = application.service.FacturaPDFService.generarPDF(factura);
            if (rutaPDF == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al generar PDF",
                        "No se pudo generar el archivo PDF de la factura.");
                return;
            }

            // Abrir el archivo en el visualizador predeterminado del sistema
            java.awt.Desktop.getDesktop().open(new java.io.File(rutaPDF));

            mostrarAlerta(Alert.AlertType.INFORMATION, "PDF Generado", "PDF generado correctamente",
                    "El PDF de la factura ha sido generado y abierto correctamente.");
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al generar PDF", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al generar PDF",
                    "Ocurrió un error al generar el archivo PDF: " + e.getMessage());
        }
    }

    private void enviarFacturaPorEmail(Factura factura) {
        // Verificar si el cliente tiene correo electrónico y solicitar uno si es
        // necesario
        String emailDestino = factura.getEmailCliente();

        // Si no tiene correo, mostrar diálogo para ingresar un correo manualmente
        if (emailDestino == null || emailDestino.trim().isEmpty()) {
            try {
                // Crear un diálogo personalizado para solicitar el correo
                TextInputDialog dialog = new TextInputDialog("");
                dialog.setTitle("Correo electrónico requerido");
                dialog.setHeaderText("Cliente sin correo electrónico");
                dialog.setContentText("Por favor, ingrese una dirección de correo para enviar la factura:");

                // Opcional: configurar el botón OK para validar el correo
                Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
                TextField inputField = dialog.getEditor();

                // Validar el formato de correo electrónico
                inputField.textProperty().addListener((unused1, unused2, newValue) -> {
                    // Validación simple de email
                    boolean esValido = newValue != null &&
                            newValue.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
                    okButton.setDisable(!esValido);
                });

                // Establecer validación inicial
                okButton.setDisable(true);

                // Mostrar el diálogo y esperar la respuesta
                var result = dialog.showAndWait();

                // Si el usuario proporcionó un email, usarlo
                if (result.isPresent() && !result.get().trim().isEmpty()) {
                    String email = result.get().trim();
                    factura.setEmailCliente(email); // Actualizar para referencia futura

                    // Continuar con el envío usando el email proporcionado
                    confirmarYEnviarEmail(factura, email);
                } else {
                    // Usuario canceló o no proporcionó correo
                    mostrarAlerta(Alert.AlertType.WARNING, "Acción cancelada",
                            "Envío cancelado",
                            "No se proporcionó una dirección de correo electrónico.");
                }
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al solicitar correo electrónico", e);
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al procesar",
                        "Ocurrió un error al solicitar el correo electrónico: " + e.getMessage());
            }
        } else {
            // Si ya tiene correo, continuar con el proceso normal
            confirmarYEnviarEmail(factura, emailDestino);
        }
    }

    /**
     * Confirma y envía el correo electrónico al destinatario especificado
     */
    private void confirmarYEnviarEmail(Factura factura, String emailDestino) {
        try {
            // Confirmar el envío
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar envío");
            confirmacion.setHeaderText("Enviar factura por correo");
            confirmacion.setContentText("¿Desea enviar la factura por correo electrónico a " +
                    factura.getNombreCliente() + " (" + emailDestino + ")?");

            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        // Mostrar indicador de progreso
                        showProgress("Enviando factura por correo...");

                        // Ejecutar el envío en un hilo separado para no bloquear la interfaz
                        Thread envioThread = new Thread(() -> {
                            boolean enviado = false;
                            String mensajeError = "";
                            try {
                                // Llamamos al método con el email explícito para mayor seguridad
                                enviado = new application.dao.FacturaDAO().enviarFacturaPorCorreoA(factura.getId(),
                                        emailDestino);
                            } catch (RuntimeException e) {
                                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al enviar correo", e);
                                mensajeError = e.getMessage();
                                // Aseguramos que enviado permanezca falso
                                enviado = false;
                            } catch (Exception e) {
                                Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                                        "Error inesperado al enviar correo", e);
                                mensajeError = "Error inesperado: " + e.getMessage();
                                enviado = false;
                            } finally {
                                // Capturamos el resultado final para usarlo en runLater
                                final boolean resultadoFinal = enviado;
                                final String errorFinal = mensajeError;

                                // Actualizar la interfaz en el hilo de JavaFX
                                javafx.application.Platform.runLater(() -> {
                                    try {
                                        hideProgress();
                                        if (resultadoFinal) {
                                            mostrarAlerta(Alert.AlertType.INFORMATION, "Correo enviado",
                                                    "Factura enviada correctamente",
                                                    "La factura ha sido enviada correctamente a " + emailDestino);
                                        } else {
                                            // Mensaje de error más detallado
                                            String mensaje = "No se pudo enviar la factura por correo electrónico.";
                                            if (!errorFinal.isEmpty()) {
                                                mensaje += "\n\nDetalle del error: " + errorFinal;
                                            }
                                            mensaje += "\n\nPosibles soluciones:" +
                                                    "\n- Verifique su conexión a Internet" +
                                                    "\n- Compruebe la configuración SMTP en el sistema" +
                                                    "\n- Confirme que el servidor de correo no está bloqueado por un firewall"
                                                    +
                                                    "\n- Si usa Gmail, debe generar una contraseña de aplicación específica:"
                                                    +
                                                    "\n  1. Vaya a su cuenta de Google > Seguridad > Contraseñas de aplicaciones"
                                                    +
                                                    "\n  2. Genere una nueva contraseña para esta aplicación" +
                                                    "\n  3. Use esa contraseña en los parámetros del sistema";

                                            mostrarAlerta(Alert.AlertType.ERROR, "Error al enviar correo",
                                                    "No se pudo completar el envío", mensaje);
                                        }
                                    } catch (Exception e) {
                                        Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                                                "Error al mostrar alerta después de enviar correo", e);
                                    }
                                });
                            }
                        });

                        envioThread.setDaemon(true);
                        envioThread.start();
                    } catch (Exception e) {
                        hideProgress();
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al iniciar proceso de envío",
                                e);
                        mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al iniciar envío",
                                "Ocurrió un error al iniciar el proceso de envío: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error en confirmación de correo", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error en el diálogo de confirmación",
                    "Ocurrió un error al mostrar el diálogo de confirmación: " + e.getMessage());
        }
    }

    // Variables para el indicador de progreso
    private Stage progressStage;

    private void showProgress(String message) {
        try {
            // Asegurarse de que estamos en el hilo de JavaFX
            if (!javafx.application.Platform.isFxApplicationThread()) {
                javafx.application.Platform.runLater(() -> showProgress(message));
                return;
            }

            if (progressStage == null) {
                progressStage = new Stage();
                progressStage.initStyle(StageStyle.UNDECORATED);
                progressStage.initModality(Modality.APPLICATION_MODAL);

                VBox root = new VBox(10);
                root.setAlignment(Pos.CENTER);
                root.setPadding(new Insets(20));
                root.setStyle("-fx-background-color: white; -fx-border-color: #2563eb; -fx-border-width: 2;");

                javafx.scene.control.ProgressIndicator progress = new javafx.scene.control.ProgressIndicator();
                progress.setPrefSize(50, 50);

                Label lblMessage = new Label(message != null ? message : "Procesando...");
                lblMessage.setStyle("-fx-font-size: 14px;");

                root.getChildren().addAll(progress, lblMessage);

                Scene scene = new Scene(root);
                progressStage.setScene(scene);
                progressStage.setResizable(false);
            }

            progressStage.show();

            // Centrar en pantalla
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            progressStage.setX((screenBounds.getWidth() - progressStage.getWidth()) / 2);
            progressStage.setY((screenBounds.getHeight() - progressStage.getHeight()) / 2);

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al mostrar progreso", e);
            // No mostramos alertas aquí para evitar recursión o bloqueos
        }
    }

    private void hideProgress() {
        try {
            // Asegurarse de que estamos en el hilo de JavaFX
            if (!javafx.application.Platform.isFxApplicationThread()) {
                javafx.application.Platform.runLater(this::hideProgress);
                return;
            }

            if (progressStage != null) {
                progressStage.close();
                progressStage = null; // Liberamos la referencia
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al ocultar progreso", e);
            // No mostramos alertas aquí para evitar recursión o bloqueos
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String header, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cargarDatosDesdeBD() {
        try {
            // Verificar si el facturaService está inicializado
            if (facturaService == null) {
                facturaService = new FacturaService();
            }

            List<Factura> facturas = facturaService.obtenerTodasLasFacturas();

            // Verificamos que la tabla no sea nula antes de asignar items
            if (tb_Facturas != null) {
                tb_Facturas.setItems(FXCollections.observableArrayList(facturas));

                // Si no hay facturas, mostrar un mensaje
                if (facturas.isEmpty()) {
                    Logger.getLogger(getClass().getName()).info("No hay facturas registradas en el sistema.");
                }
            } else {
                Logger.getLogger(getClass().getName()).warning("La tabla de facturas no está inicializada.");
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al cargar facturas desde la base de datos",
                    e);

            // Evitar mostrar diálogo si la UI no está lista
            if (tb_Facturas != null && tb_Facturas.getScene() != null) {
                // Mostrar un diálogo de error
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "Error al cargar facturas",
                        "No se pudieron cargar las facturas desde la base de datos: " + e.getMessage());
            }
        }
    }

}
