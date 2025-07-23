package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.List;

public class DialogoPersonalizadoController {

    @FXML private Label lbl_Titulo, lbl_Subtitulo, lbl_TituloMensaje, lbl_Mensaje;
    @FXML private ImageView img_IconoContenido;
    @FXML private HBox contenedorBotones;
    @FXML private Button btn_Cerrar;
    @FXML private AnchorPane rootPane;
    @FXML private HBox titleBar;
    @FXML private VBox mainContainer;
    @FXML private Pane iconContainer;

    private Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;

    public void inicializar(String titulo, String mensaje, String tipo, Stage stage,
                            List<ButtonType> botones) {
        this.stage = stage;
        lbl_Titulo.setText(titulo);
        lbl_Mensaje.setText(mensaje);
        btn_Cerrar.setVisible(true);

        // Configurar títulos según el tipo
        configurarTitulosSegunTipo(tipo);

        // Cargar icono
        String icono = switch (tipo.toLowerCase()) {
            case "info" -> "/icons/info.png";
            case "error" -> "/icons/error.png";
            case "warning" -> "/icons/warning.png";
            case "confirm" -> "/icons/confirm.png";
            default -> "/icons/info.png";
        };
        try {
            Image iconoImage = new Image(getClass().getResourceAsStream(icono));
            img_IconoContenido.setImage(iconoImage);
            img_IconoContenido.getStyleClass().setAll("dialog-icon-image");
        } catch (Exception e) {
            System.err.println("Error cargando icono: " + icono);
        }

        // Configurar colores y clases según el tipo
        configurarColoresSegunTipo(tipo);
        // Siempre agregar la clase base al contenedor
        if (!iconContainer.getStyleClass().contains("dialog-icon-container")) {
            iconContainer.getStyleClass().add(0, "dialog-icon-container");
        }

        // Configurar botones con estilo mejorado
        contenedorBotones.getChildren().clear();
        for (ButtonType bt : botones) {
            Button b = new Button(bt.getText());
            b.setStyle(getButtonStyle(bt));
            b.setOnAction(e -> {
                stage.setUserData(bt);
                stage.close();
            });
            
            // Efectos hover para botones
            b.setOnMouseEntered(e -> b.setStyle(getButtonHoverStyle(bt)));
            b.setOnMouseExited(e -> b.setStyle(getButtonStyle(bt)));
            
            contenedorBotones.getChildren().add(b);
        }

        // Configurar dragging
        titleBar.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        titleBar.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });
    }

    private void configurarTitulosSegunTipo(String tipo) {
        switch (tipo.toLowerCase()) {
            case "info":
                lbl_TituloMensaje.setText("Información");
                break;
            case "error":
                lbl_TituloMensaje.setText("Error");
                break;
            case "warning":
                lbl_TituloMensaje.setText("Advertencia");
                break;
            case "confirm":
                lbl_TituloMensaje.setText("Confirmación");
                break;
            default:
                lbl_TituloMensaje.setText("Información");
        }
    }

    private void configurarColoresSegunTipo(String tipo) {
        String colorTitulo;
        switch (tipo.toLowerCase()) {
            case "error":
                colorTitulo = "#c53030";
                iconContainer.getStyleClass().removeAll("dialog-icon-warning", "dialog-icon-confirm", "dialog-icon-info", "dialog-icon-error");
                if (!iconContainer.getStyleClass().contains("dialog-icon-error")) iconContainer.getStyleClass().add("dialog-icon-error");
                break;
            case "warning":
                colorTitulo = "#d69e2e";
                iconContainer.getStyleClass().removeAll("dialog-icon-error", "dialog-icon-confirm", "dialog-icon-info", "dialog-icon-warning");
                if (!iconContainer.getStyleClass().contains("dialog-icon-warning")) iconContainer.getStyleClass().add("dialog-icon-warning");
                break;
            case "confirm":
                colorTitulo = "#2f855a";
                iconContainer.getStyleClass().removeAll("dialog-icon-error", "dialog-icon-warning", "dialog-icon-info", "dialog-icon-confirm");
                if (!iconContainer.getStyleClass().contains("dialog-icon-confirm")) iconContainer.getStyleClass().add("dialog-icon-confirm");
                break;
            default: // info
                colorTitulo = "#475569";
                iconContainer.getStyleClass().removeAll("dialog-icon-error", "dialog-icon-warning", "dialog-icon-confirm", "dialog-icon-info");
                if (!iconContainer.getStyleClass().contains("dialog-icon-info")) iconContainer.getStyleClass().add("dialog-icon-info");
        }
        lbl_TituloMensaje.setStyle("-fx-text-fill: " + colorTitulo + "; -fx-font-size: 14px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI', Arial, sans-serif;");
    }

    private String getButtonStyle(ButtonType bt) {
        String baseStyle = "-fx-font-size: 12px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-cursor: hand; -fx-background-radius: 6; -fx-border-radius: 6; -fx-padding: 6 14; -fx-min-width: 75; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 2, 0, 0, 1);";
        
        if (bt == ButtonType.OK || bt == ButtonType.YES) {
            return baseStyle + " -fx-background-color: linear-gradient(to bottom, #64748b, #475569); -fx-text-fill: white; -fx-border-color: transparent;";
        } else if (bt == ButtonType.NO) {
            return baseStyle + " -fx-background-color: linear-gradient(to bottom, #e53e3e, #c53030); -fx-text-fill: white; -fx-border-color: transparent;";
        } else {
            return baseStyle + " -fx-background-color: linear-gradient(to bottom, #f8fafc, #f1f5f9); -fx-text-fill: #475569; -fx-border-color: #cbd5e1; -fx-border-width: 1;";
        }
    }

    private String getButtonHoverStyle(ButtonType bt) {
        String baseStyle = "-fx-font-size: 12px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-cursor: hand; -fx-background-radius: 6; -fx-border-radius: 6; -fx-padding: 6 14; -fx-min-width: 75; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 4, 0, 0, 2);";
        
        if (bt == ButtonType.OK || bt == ButtonType.YES) {
            return baseStyle + " -fx-background-color: linear-gradient(to bottom, #475569, #334155); -fx-text-fill: white; -fx-border-color: transparent;";
        } else if (bt == ButtonType.NO) {
            return baseStyle + " -fx-background-color: linear-gradient(to bottom, #c53030, #9c2626); -fx-text-fill: white; -fx-border-color: transparent;";
        } else {
            return baseStyle + " -fx-background-color: linear-gradient(to bottom, #f1f5f9, #e2e8f0); -fx-text-fill: #475569; -fx-border-color: #94a3b8; -fx-border-width: 1;";
        }
    }

    @FXML
    private void cerrarDialogo() {
        stage.setUserData(ButtonType.CLOSE);
        stage.close();
    }

    @FXML
    private void onCerrarHover() {
        btn_Cerrar.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: #fff; -fx-font-size: 22px; -fx-font-weight: bold; -fx-background-radius: 16; -fx-border-color: #c53030; -fx-border-width: 2; -fx-border-radius: 16; -fx-cursor: hand; -fx-min-width: 36; -fx-min-height: 36; -fx-max-width: 36; -fx-max-height: 36;");
    }

    @FXML
    private void onCerrarExit() {
        btn_Cerrar.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: #fff; -fx-font-size: 22px; -fx-font-weight: bold; -fx-background-radius: 16; -fx-border-color: #c53030; -fx-border-width: 2; -fx-border-radius: 16; -fx-cursor: hand; -fx-min-width: 36; -fx-min-height: 36; -fx-max-width: 36; -fx-max-height: 36;");
    }
}