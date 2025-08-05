package application.controllers;

import application.controllers.administracion_sistema.ModuloParametrosController;
import application.controllers.administracion_sistema.ModuloUsuarioController;
import application.controllers.casos_documentacion.ModuloBitacoraController;
import application.controllers.casos_documentacion.ModuloCasosController;
import application.controllers.casos_documentacion.ModuloDocumentosController;
import application.controllers.casos_documentacion.ModuloHistorialController;
import application.controllers.cliente.ModuloClienteController;
import application.controllers.facturacion.ModuloFacturaController;
import application.controllers.personal.ModuloEmpleadoController;
import application.service.ParametroService;
import application.util.SessionManager;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class MainController {
    private static MainController instance;

    public MainController() {
        instance = this;
    }

    public static MainController getInstance() {
        return instance;
    }

    @FXML
    private HBox titleBar;
    @FXML
    private Label lblTitulo;
    @FXML
    private StackPane logoPanel;
    @FXML
    private ImageView logoImageView;
    @FXML
    private Button closeButton, minimizeButton;
    @FXML
    private Button md_Clientes, md_CasosDocumentacion, md_Facturacion, md_Personal, md_Sistema;
    @FXML
    private Button btn_modo, md_Usuario;

    @FXML
    private Button btn_Casos, btn_HistorialComunicaciones, btn_GenerarReporte;
    @FXML
    private Button btn_AdministracionUsuarios, btn_Parametros;

    @FXML
    private VBox vpnl_DesplegableCasosDocumentacion, vpnl_DesplegableSistema;
    @FXML
    private AnchorPane pnl_Modulos, pnl_Forms;

    private double xOffset = 0;
    private double yOffset = 0;

    private String tipoUsuario;

    /**
     * Método público para actualizar la interfaz cuando cambian los parámetros
     * Este método será llamado desde el controlador de parámetros
     */
    public void actualizarParametrosUI() {
        cargarNombreInstitucion();
        cargarLogo();
    }

    /**
     * Convierte el enum TipoUsuario a string para el switch de módulos.
     * No incluye el caso EXTERNO ("Asistente Legal").
     */
    public static String tipoUsuarioToString(application.model.Usuario.TipoUsuario tipoUsuario) {
        return switch (tipoUsuario) {
            case INTERNO -> "Administrador";
            case NATURAL -> "Abogado";
            case JURIDICA -> "Contador";
            default -> "Otro";
        };
    }

    @FXML
    private void initialize() {
        // Cargar el nombre de la institución y logo desde los parámetros
        cargarNombreInstitucion();
        cargarLogo();

        // Mover ventana
        titleBar.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
            // Registrar actividad del usuario para extender la sesión
            SessionManager.getInstance().resetSessionTimer();
        });

        titleBar.setOnMouseDragged((MouseEvent event) -> {
            Stage stage = (Stage) titleBar.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
            // Registrar actividad del usuario para extender la sesión
            SessionManager.getInstance().resetSessionTimer();
        });

        // Botones cerrar/minimizar
        closeButton.setOnAction(_ -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            // Detener el timer de sesión antes de cerrar
            SessionManager.getInstance().stopSessionTimer();
            stage.close();
        });

        minimizeButton.setOnAction(_ -> ((Stage) minimizeButton.getScene().getWindow()).setIconified(true));

        // Ocultar menú desplegable al iniciar
        hideDropdown();
        hideDropdownSistema();

        // Configurar detección de actividad del usuario en toda la interfaz
        AnchorPane rootPane = (AnchorPane) titleBar.getParent();

        // Detectar movimiento de ratón
        rootPane.addEventFilter(MouseEvent.ANY, _ -> {
            SessionManager.getInstance().resetSessionTimer();
        });

        // Detectar pulsaciones de teclas
        rootPane.addEventFilter(KeyEvent.ANY, _ -> {
            SessionManager.getInstance().resetSessionTimer();
        });

        // Mostrar menú desplegable al hacer hover - Casos y Documentación
        md_CasosDocumentacion.setOnMouseEntered(_ -> {
            showDropdown();
            SessionManager.getInstance().resetSessionTimer();
        });
        md_CasosDocumentacion.setOnMouseExited(_ -> hideDropdownWithDelay());
        vpnl_DesplegableCasosDocumentacion.setOnMouseEntered(_ -> showDropdown());
        vpnl_DesplegableCasosDocumentacion.setOnMouseExited(_ -> hideDropdown());

        // Mostrar menú desplegable al hacer hover - Sistema
        md_Sistema.setOnMouseEntered(_ -> {
            showDropdownSistema();
            SessionManager.getInstance().resetSessionTimer();
        });
        md_Sistema.setOnMouseExited(_ -> hideDropdownSistemaWithDelay());
        vpnl_DesplegableSistema.setOnMouseEntered(_ -> showDropdownSistema());
        vpnl_DesplegableSistema.setOnMouseExited(_ -> hideDropdownSistema());

        // Cargar dashboard al iniciar
        cargarModulo("/views/dashboard.fxml");

        md_Clientes.setOnAction(_ -> {
            SessionManager.getInstance().resetSessionTimer();
            cargarModulo("/views/cliente/modulo_cliente.fxml");
        });

        md_Personal.setOnAction(_ -> {
            SessionManager.getInstance().resetSessionTimer();
            cargarModulo("/views/personal/modulo_empleado.fxml");
        });

        md_Facturacion.setOnAction(_ -> {
            SessionManager.getInstance().resetSessionTimer();
            cargarModulo("/views/factura/modulo_factura.fxml");
        });

        // Submódulos Casos y Documentación
        btn_Casos.setOnAction(_ -> {
            SessionManager.getInstance().resetSessionTimer();
            cargarModulo("/views/casos_documentos/modulo_casos_documentacion_casos.fxml");
        });

        // btn_Documentos y btn_Bitacora fueron eliminados del FXML
        btn_HistorialComunicaciones.setOnAction(_ -> {
            SessionManager.getInstance().resetSessionTimer();
            cargarModulo("/views/casos_documentos/modulo_casos_documentacion_historial_comunicaciones.fxml");
        });

        btn_GenerarReporte.setOnAction(_ -> {
            SessionManager.getInstance().resetSessionTimer();
            cargarModulo("/views/casos_documentos/modulo_casos_documentacion_generar_reportes.fxml");
        });

        // Submódulos Sistema
        btn_AdministracionUsuarios.setOnAction(_ -> {
            SessionManager.getInstance().resetSessionTimer();
            cargarModulo("/views/usuario/modulo_usuario.fxml");
        });

        btn_Parametros.setOnAction(_ -> {
            SessionManager.getInstance().resetSessionTimer();
            cargarModulo("/views/sistema/modulo_parametros.fxml");
        });

        // Ocultar formulario lateral al cargar
        pnl_Forms.setVisible(false);
        pnl_Forms.setManaged(false);
    }

    private void showDropdown() {
        vpnl_DesplegableCasosDocumentacion.setVisible(true);
        vpnl_DesplegableCasosDocumentacion.setManaged(true);
    }

    private void hideDropdown() {
        vpnl_DesplegableCasosDocumentacion.setVisible(false);
        vpnl_DesplegableCasosDocumentacion.setManaged(false);
    }

    private void hideDropdownWithDelay() {
        PauseTransition pause = new PauseTransition(Duration.millis(150));
        pause.setOnFinished(_ -> {
            if (!vpnl_DesplegableCasosDocumentacion.isHover() && !md_CasosDocumentacion.isHover()) {
                hideDropdown();
            }
        });
        pause.play();
    }

    private void showDropdownSistema() {
        vpnl_DesplegableSistema.setVisible(true);
        vpnl_DesplegableSistema.setManaged(true);
    }

    private void hideDropdownSistema() {
        vpnl_DesplegableSistema.setVisible(false);
        vpnl_DesplegableSistema.setManaged(false);
    }

    private void hideDropdownSistemaWithDelay() {
        PauseTransition pause = new PauseTransition(Duration.millis(150));
        pause.setOnFinished(_ -> {
            if (!vpnl_DesplegableSistema.isHover() && !md_Sistema.isHover()) {
                hideDropdownSistema();
            }
        });
        pause.play();
    }

    /**
     * Carga el nombre de la institución desde los parámetros del sistema
     * y actualiza el título de la aplicación
     */
    private void cargarNombreInstitucion() {
        if (lblTitulo != null) {
            // Obtener el nombre de la institución desde los parámetros
            final String nombreInstitucion = ParametroService.getInstance().getValor("nombre_institucion");
            final String titulo = (nombreInstitucion != null && !nombreInstitucion.trim().isEmpty())
                    ? nombreInstitucion
                    : "SGEJ-A";

            // Actualizar el título
            lblTitulo.setText(titulo);

            // También actualizar el título de la ventana
            Platform.runLater(() -> {
                Stage stage = (Stage) lblTitulo.getScene().getWindow();
                if (stage != null) {
                    stage.setTitle(titulo);
                }
            });
        }
    }

    /**
     * Carga el logo desde los parámetros del sistema
     * y lo muestra en el panel correspondiente.
     * Soporta tanto rutas de recursos internos (/icons/logo.png)
     * como rutas absolutas del sistema de archivos (C:/ruta/a/logo.png)
     */
    private void cargarLogo() {
        if (logoImageView != null) {
            try {
                // Obtener la ruta del logo desde los parámetros
                String rutaLogo = ParametroService.getInstance().getValor("logo_sistema");
                if (rutaLogo == null || rutaLogo.trim().isEmpty()) {
                    rutaLogo = "/icons/firmLogo.jpg"; // Logo por defecto
                }

                Image logoImage;

                // Determinar si es una ruta interna (comienza con /) o una ruta externa del
                // sistema de archivos
                if (rutaLogo.startsWith("/")) {
                    // Es una ruta de recurso interno
                    logoImage = new Image(getClass().getResourceAsStream(rutaLogo));
                    System.out.println("Cargando logo desde recurso interno: " + rutaLogo);
                } else {
                    // Es una ruta del sistema de archivos
                    logoImage = new Image("file:" + rutaLogo);
                    System.out.println("Cargando logo desde sistema de archivos: " + rutaLogo);
                }

                logoImageView.setImage(logoImage);

                // Ajustar el tamaño de la imagen para la barra de navegación
                logoImageView.setPreserveRatio(true);
                logoImageView.setFitWidth(40);
                logoImageView.setFitHeight(40);

                // Aplicar estilo al panel del logo dentro de la barra de navegación
                logoPanel.setStyle(
                        "-fx-background-color: white; -fx-background-radius: 5; -fx-padding: 3; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 0);");
            } catch (Exception e) {
                System.err.println("Error al cargar el logo: " + e.getMessage());
                // En caso de error, intentar cargar el logo por defecto
                try {
                    Image defaultLogo = new Image(getClass().getResourceAsStream("/icons/firmLogo.jpg"));
                    logoImageView.setImage(defaultLogo);
                } catch (Exception ex) {
                    // Si también falla, no mostramos imagen
                    System.err.println("Error al cargar el logo por defecto: " + ex.getMessage());
                }
            }
        }
    }

    public void setUserType(String userType) {
        this.tipoUsuario = userType;
        switch (userType) {
            case "Administrador":
                // Administrador tiene acceso a todo, no necesita restricciones
                break;
            case "Contador":
                // Ocultar módulos no relevantes para Contador
                md_CasosDocumentacion.setVisible(false);
                md_CasosDocumentacion.setManaged(false);
                md_Usuario.setVisible(true);
                md_Usuario.setManaged(true);
                break;
            case "Abogado":
                // Ocultar módulos no relevantes para Abogado
                md_Facturacion.setVisible(false);
                md_Facturacion.setManaged(false);
                md_Sistema.setVisible(false);
                md_Sistema.setManaged(false);
                md_Personal.setVisible(false);
                md_Personal.setManaged(false);
                // Mantener acceso a la base de datos de usuarios

                md_Usuario.setVisible(true);
                md_Usuario.setManaged(true);
                md_CasosDocumentacion.setVisible(true);
                md_CasosDocumentacion.setManaged(true);

                break;
        }

        // Cargar dashboard después de ocultar módulos
        cargarModulo("/views/dashboard.fxml");
    }

    public void cargarModulo(String rutaFXML) {
        try {
            // Normalizar la ruta para asegurarnos que tiene el slash inicial
            if (!rutaFXML.startsWith("/")) {
                rutaFXML = "/" + rutaFXML;
            }
            
            System.out.println("DEBUG: MainController intentando cargar: " + rutaFXML);
            
            // Verificar que el recurso existe antes de intentar cargarlo
            if (getClass().getResource(rutaFXML) == null) {
                System.err.println("ERROR: El recurso " + rutaFXML + " no existe");
                // Intentar sin el slash inicial como alternativa
                String rutaAlternativa = rutaFXML.startsWith("/") ? rutaFXML.substring(1) : rutaFXML;
                if (getClass().getResource(rutaAlternativa) != null) {
                    System.out.println("DEBUG: Encontrado con ruta alternativa: " + rutaAlternativa);
                    rutaFXML = rutaAlternativa;
                } else {
                    throw new IllegalArgumentException("El recurso FXML no existe: " + rutaFXML);
                }
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Node modulo = loader.load();

            Object controller = loader.getController();
            if (controller instanceof ModuloClienteController c) {
                c.setFormularioContainer(pnl_Forms);
                if (tipoUsuario != null)
                    c.configurarPorRol(tipoUsuario);
            }
            if (controller instanceof ModuloCasosController c) {
                c.setPanelModulos(pnl_Modulos);
            }
            if (controller instanceof ModuloDocumentosController c)
                c.setFormularioContainer(pnl_Forms);
            if (controller instanceof ModuloHistorialController c)
                c.setFormularioContainer(pnl_Forms);
            if (controller instanceof ModuloBitacoraController c)
                c.setFormularioContainer(pnl_Forms);
            if (controller instanceof DashboardController c) {
                c.setFormularioContainer(pnl_Forms);
                c.setMainController(this);
                if (tipoUsuario != null)
                    c.configurarPorRol(tipoUsuario);
            }
            if (controller instanceof ModuloEmpleadoController c)
                c.setFormularioContainer(pnl_Forms);
            if (controller instanceof ModuloUsuarioController c) {
                c.setFormularioContainer(pnl_Forms);
                System.out.println("Se configuró el contenedor de formularios para ModuloUsuarioController");
            }
            if (controller instanceof ModuloFacturaController c)
                c.setFormularioContainer(pnl_Forms);
            if (controller instanceof ModuloParametrosController c)
                c.setFormularioContainer(pnl_Forms);

            AnchorPane.setTopAnchor(modulo, 0.0);
            AnchorPane.setBottomAnchor(modulo, 0.0);
            AnchorPane.setLeftAnchor(modulo, 0.0);
            AnchorPane.setRightAnchor(modulo, 0.0);

            modulo.setUserData(controller);
            pnl_Modulos.getChildren().setAll(modulo);

            pnl_Forms.getChildren().clear();
            pnl_Forms.setVisible(false);
            pnl_Forms.setManaged(false);

            hideDropdown();
            hideDropdownSistema();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
