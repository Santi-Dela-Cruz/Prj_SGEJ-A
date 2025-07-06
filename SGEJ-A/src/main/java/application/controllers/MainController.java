package application.controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class MainController {

    @FXML private HBox titleBar;
    @FXML private Button closeButton, minimizeButton;
    @FXML private Button md_Clientes, md_CasosDocumentacion, md_Facturacion, md_Personal, md_Sistema, dashboard;
    @FXML private Button btn_modo, md_Usuario;

    @FXML private Button btn_Casos, btn_Documentos, btn_HistorialComunicaciones, btn_Bitacora, btn_GenerarReporte;

    @FXML private VBox vpnl_DesplegableCasosDocumentacion;
    @FXML private AnchorPane pnl_Modulos, pnl_Forms;

    private double xOffset = 0;
    private double yOffset = 0;

    private String tipoUsuario;

    @FXML
    private void initialize() {
        // Mover ventana
        titleBar.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        titleBar.setOnMouseDragged((MouseEvent event) -> {
            Stage stage = (Stage) titleBar.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        // Botones cerrar/minimizar
        closeButton.setOnAction(e -> ((Stage) closeButton.getScene().getWindow()).close());
        minimizeButton.setOnAction(e -> ((Stage) minimizeButton.getScene().getWindow()).setIconified(true));

        // Ocultar menú desplegable al iniciar
        hideDropdown();

        // Mostrar menú desplegable al hacer hover
        md_CasosDocumentacion.setOnMouseEntered(e -> showDropdown());
        md_CasosDocumentacion.setOnMouseExited(e -> hideDropdownWithDelay());
        vpnl_DesplegableCasosDocumentacion.setOnMouseEntered(e -> showDropdown());
        vpnl_DesplegableCasosDocumentacion.setOnMouseExited(e -> hideDropdown());

        // Módulo inicial
        dashboard.setOnAction(e -> cargarModulo("/views/dashboard.fxml"));

        md_Clientes.setOnAction(e -> cargarModulo("/views/cliente/modulo_cliente.fxml"));



        // Submódulos Casos y Documentación
        btn_Casos.setOnAction(e -> cargarModulo("/views/casos_documentos/modulo_casos_documentacion_casos.fxml"));
        btn_Documentos.setOnAction(e -> cargarModulo("/views/casos_documentos/modulo_casos_documentacion_documentos.fxml"));
        btn_HistorialComunicaciones.setOnAction(e -> cargarModulo("/views/casos_documentos/modulo_casos_documentacion_historial_comunicaciones.fxml"));
        btn_Bitacora.setOnAction(e -> cargarModulo("/views/casos_documentos/modulo_casos_documentacion_bitacora_caso.fxml"));
        btn_GenerarReporte.setOnAction(e -> cargarModulo("/views/casos_documentos/modulo_casos_documentacion_generar_reportes.fxml"));



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
        pause.setOnFinished(e -> {
            if (!vpnl_DesplegableCasosDocumentacion.isHover() && !md_CasosDocumentacion.isHover()) {
                hideDropdown();
            }
        });
        pause.play();
    }

    public void setUserType(String userType) {
        this.tipoUsuario = userType;
        switch (userType) {
            case "Administrador":
                break;
            case "Asistente Legal":
                md_Facturacion.setVisible(false); md_Facturacion.setManaged(false);
                md_Personal.setVisible(false); md_Personal.setManaged(false);
                md_Sistema.setVisible(false); md_Sistema.setManaged(false);
                break;
            case "Contador":
                md_CasosDocumentacion.setVisible(false); md_CasosDocumentacion.setManaged(false);
                md_Personal.setVisible(false); md_Personal.setManaged(false);
                md_Sistema.setVisible(false); md_Sistema.setManaged(false);
                break;
        }

        // Cargar dashboard después de ocultar módulos
        cargarModulo("/views/dashboard.fxml");
    }

    void cargarModulo(String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Node modulo = loader.load();

            Object controller = loader.getController();
            if (controller instanceof ModuloClienteController c) c.setFormularioContainer(pnl_Forms);
            if (controller instanceof ModuloCasosController c) c.setFormularioContainer(pnl_Forms);
            if (controller instanceof ModuloDocumentosController c) c.setFormularioContainer(pnl_Forms);
            if (controller instanceof ModuloHistorialController c) c.setFormularioContainer(pnl_Forms);
            if (controller instanceof ModuloBitacoraController c) c.setFormularioContainer(pnl_Forms);
            if (controller instanceof DashboardController c) {
                c.setFormularioContainer(pnl_Forms);
                c.setMainController(this);
                if (tipoUsuario != null) c.configurarPorRol(tipoUsuario);
            }

            AnchorPane.setTopAnchor(modulo, 0.0);
            AnchorPane.setBottomAnchor(modulo, 0.0);
            AnchorPane.setLeftAnchor(modulo, 0.0);
            AnchorPane.setRightAnchor(modulo, 0.0);

            pnl_Modulos.getChildren().setAll(modulo);

            pnl_Forms.getChildren().clear();
            pnl_Forms.setVisible(false);
            pnl_Forms.setManaged(false);

            hideDropdown();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
