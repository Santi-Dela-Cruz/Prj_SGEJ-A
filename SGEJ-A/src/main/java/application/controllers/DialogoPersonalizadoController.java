package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.List;

public class DialogoPersonalizadoController {

    @FXML private Label lbl_Titulo, lbl_Mensaje;
    @FXML private ImageView img_IconoContenido;
    @FXML private HBox contenedorBotones;
    @FXML private Button btn_Cerrar;
    @FXML private AnchorPane rootPane;
    @FXML private HBox titleBar;


    private Stage stage;

    private double xOffset = 0;
    private double yOffset = 0;


    public void inicializar(String titulo, String mensaje, String tipo, Stage stage,
                            List<ButtonType> botones) {
        this.stage = stage;
        lbl_Titulo.setText(titulo);
        lbl_Mensaje.setText(mensaje);
        btn_Cerrar.setVisible(true);

        Rectangle clip = new Rectangle();
        clip.setArcWidth(36);
        clip.setArcHeight(36);
        rootPane.setClip(clip);

        rootPane.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            clip.setWidth(newVal.getWidth());
            clip.setHeight(newVal.getHeight());
        });



        // Cargar iconos
        String icono = switch (tipo.toLowerCase()) {
            case "info" -> "/icons/info.png";
            case "error" -> "/icons/error.png";
            case "warning" -> "/icons/warning.png";
            case "confirm" -> "/icons/confirm.png";
            default -> "/icons/info.png";
        };
        img_IconoContenido.setImage(new Image(getClass().getResourceAsStream(icono)));

        // Botones
        contenedorBotones.getChildren().clear();
        for (ButtonType bt : botones) {
            Button b = new Button(bt.getText());
            b.getStyleClass().addAll("button-windows", getButtonColorClass(bt));
            b.setOnAction(e -> {
                stage.setUserData(bt);
                stage.close();
            });
            contenedorBotones.getChildren().add(b);
        }

        titleBar.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        titleBar.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });
    }

    private String getButtonColorClass(ButtonType bt) {
        if (bt == ButtonType.OK) return "button-windows-blue";
        if (bt == ButtonType.CANCEL) return "button-windows-gray";
        if (bt == ButtonType.YES) return "button-windows-green";
        if (bt == ButtonType.NO) return "button-windows-red";
        return "button-windows-blue";
    }

    @FXML
    private void cerrarDialogo() {
        stage.setUserData(ButtonType.CLOSE);
        stage.close();
    }
}