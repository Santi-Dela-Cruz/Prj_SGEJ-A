package application.controllers.casos_documentacion;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import application.controllers.DialogUtil;

public class DetalleCasoBitacoraController {
    private application.model.Caso caso;
    @FXML
    private Label lblExpediente, lblTitulo, lblTipo, lblFecha, lblAbogado, lblEstado;
    @FXML
    private Label lblTotalEntradas;
    @FXML
    private TableView<application.model.BitacoraCaso> tbBitacora;
    // Eliminamos la columna tbcUsuario
    @FXML
    private TableColumn<application.model.BitacoraCaso, String> tbcFecha, tbcTipoAccion, tbcDescripcion;
    @FXML
    private Button btnAgregarEntrada;
    @FXML
    private Button btnRegresar;
    @FXML
    private AnchorPane panelPrincipal;

    private int casoId;
    private AnchorPane panelSobrepuesto;

    @FXML
    public void initialize() {
        // Inicializar el contador de entradas
        lblTotalEntradas.setText("Total: 0 entradas");

        // Configurar las columnas de la tabla
        tbcFecha.setCellValueFactory(cellData -> {
            Date fecha = cellData.getValue().getFechaEntrada();
            String fechaStr = fecha != null ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(fecha) : "";
            return new javafx.beans.property.SimpleStringProperty(fechaStr);
        });

        tbcTipoAccion.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipoAccion()));

        tbcDescripcion.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescripcion()));

        // Configurar los botones
        btnAgregarEntrada.setOnAction(e -> mostrarFormularioBitacora());
        configurarBotonRegresar();
    }

    /**
     * Configura el botón regresar para usar MainController directamente
     */
    private void configurarBotonRegresar() {
        btnRegresar.setOnAction(e -> {
            System.out.println("DEBUG: Botón regresar presionado en DetalleCasoBitacoraController");

            // Usar MainController exclusivamente para navegación
            try {
                System.out.println("DEBUG: Navegando con MainController");
                application.controllers.MainController mainController = application.controllers.MainController
                        .getInstance();
                if (mainController != null) {
                    // Cambiado para usar la ruta correcta del FXML
                    mainController.cargarModulo("/views/casos_documentos/modulo_casos_documentacion_casos.fxml");
                    System.out.println("DEBUG: Navegación exitosa con MainController");
                } else {
                    System.err.println("ERROR: No se pudo obtener instancia de MainController");
                }
            } catch (Exception ex) {
                System.err.println("ERROR: Falló navegación con MainController: " + ex.getMessage());
                ex.printStackTrace();
                // Intentar cargar con una ruta alternativa si la primera falló
                try {
                    application.controllers.MainController mainController = application.controllers.MainController
                            .getInstance();
                    if (mainController != null) {
                        mainController.cargarModulo("views/casos_documentos/modulo_casos_documentacion_casos.fxml");
                        System.out.println("DEBUG: Navegación exitosa con ruta alternativa");
                    }
                } catch (Exception e2) {
                    System.err.println("ERROR: También falló la navegación con ruta alternativa: " + e2.getMessage());
                }
            }
        });
    }

    private void mostrarFormularioBitacora() {
        try {
            // Creamos un panel sobrepuesto si no existe
            if (panelSobrepuesto == null) {
                panelSobrepuesto = new AnchorPane();
                panelSobrepuesto.setPrefWidth(400);
                panelSobrepuesto.setStyle("-fx-background-color: transparent;");

                // Añadimos el panel sobrepuesto al panel principal
                AnchorPane.setTopAnchor(panelSobrepuesto, 0.0);
                AnchorPane.setBottomAnchor(panelSobrepuesto, 0.0);
                AnchorPane.setRightAnchor(panelSobrepuesto, 0.0);

                // Lo hacemos visible inicialmente
                panelSobrepuesto.setVisible(false);

                // Añadimos al panel principal
                panelPrincipal.getChildren().add(panelSobrepuesto);
            }

            // Cargamos el formulario
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/casos_documentos/form_nueva_bitacora.fxml"));
            AnchorPane form = loader.load();
            FormBitacoraController controller = loader.getController();

            if (controller != null) {
                controller.setCasoId(casoId);

                // Aplicamos efecto de sombra para destacar el panel
                DropShadow shadow = new DropShadow();
                shadow.setRadius(10.0);
                shadow.setOffsetX(-5.0);
                shadow.setOffsetY(5.0);
                shadow.setColor(Color.rgb(0, 0, 0, 0.3));
                form.setEffect(shadow);

                // Configuramos el formulario dentro del panel sobrepuesto
                panelSobrepuesto.getChildren().setAll(form);

                // Posicionamos el formulario dentro del panel
                AnchorPane.setTopAnchor(form, 60.0); // Espacio desde arriba
                AnchorPane.setRightAnchor(form, 30.0); // Espacio desde la derecha

                // Hacemos visible el panel
                panelSobrepuesto.setVisible(true);

                // Configuramos los eventos
                controller.setOnGuardar(v -> {
                    panelSobrepuesto.setVisible(false);
                    panelSobrepuesto.getChildren().clear();
                    cargarBitacoraDesdeBD();
                });

                controller.setOnCancelar(v -> {
                    panelSobrepuesto.setVisible(false);
                    panelSobrepuesto.getChildren().clear();
                });
            } else {
                System.err.println("Error: No se pudo cargar el controlador FormBitacoraController");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setCaso(application.model.Caso caso) {
        this.caso = caso;
        lblExpediente.setText(caso.getNumeroExpediente());
        lblTitulo.setText(caso.getTitulo());
        lblTipo.setText(caso.getTipo());
        String fechaStr = "";
        if (caso.getFechaInicio() != null) {
            fechaStr = new java.text.SimpleDateFormat("dd/MM/yyyy").format(caso.getFechaInicio());
        }
        lblFecha.setText(fechaStr);
        String abogadoStr = "";
        if (caso.getAbogados() != null && !caso.getAbogados().isEmpty()) {
            abogadoStr = caso.getAbogados().get(0).getNombre();
        }
        lblAbogado.setText(abogadoStr);
        lblEstado.setText(caso.getEstado());
        try {
            casoId = Integer.parseInt(caso.getNumeroExpediente().replaceAll("[^0-9]", ""));
        } catch (Exception ex) {
            casoId = -1;
        }
        cargarBitacoraDesdeBD();
    }

    private void cargarBitacoraDesdeBD() {
        try {
            java.sql.Connection conn = application.database.DatabaseConnection.getConnection();
            application.dao.BitacoraCasoDAO dao = new application.dao.BitacoraCasoDAO(conn);
            java.util.List<application.model.BitacoraCaso> lista = dao.consultarBitacorasPorCaso(casoId);
            tbBitacora.getItems().setAll(lista);

            // Actualizar el contador de entradas
            int totalEntradas = lista.size();
            lblTotalEntradas.setText("Total: " + totalEntradas + (totalEntradas == 1 ? " entrada" : " entradas"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Este método ahora no hace nada excepto registrar que fue llamado.
     * La navegación se realiza exclusivamente a través de MainController.
     */
    public void setOnRegresar(Runnable r) {
        System.out.println("DEBUG: setOnRegresar llamado en DetalleCasoBitacoraController - ignorando callback");

        // Ya no usamos callbacks para navegación, utilizamos MainController
        // directamente
        // El botón de regreso ya está configurado en el método
        // configurarBotonRegresar()
    }
}
