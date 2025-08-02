package application.controllers.administracion_sistema;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FormUsuarioModalLauncher {
    /**
     * Muestra el panel de cambio/reset de contraseña como panel flotante cuadrado e
     * independiente,
     * anclado al lado derecho de la ventana principal, sin overlay ni modal.
     */
    public static void mostrarPanelCambioClaveIndependiente(javafx.scene.Scene scene, application.model.Usuario usuario,
            String modo, Runnable onGuardar) {
        mostrarPanelFlotanteGenerico(
                scene,
                "/views/usuario/form_cambio_clave.fxml",
                320,
                320,
                controllerObj -> {
                    FormCambioClaveController controller = (FormCambioClaveController) controllerObj;
                    controller.setUsuario(usuario);
                    controller.setModo(modo);
                    javafx.scene.layout.AnchorPane rootPane = (javafx.scene.layout.AnchorPane) scene.getRoot();
                    if (onGuardar != null) {
                        controller.setOnGuardar(() -> {
                            onGuardar.run();
                            javafx.scene.Node panel = rootPane.lookup("#panel_flotante");
                            if (panel != null)
                                rootPane.getChildren().remove(panel);
                        });
                    } else {
                        controller.setOnGuardar(() -> {
                            javafx.scene.Node panel = rootPane.lookup("#panel_flotante");
                            if (panel != null)
                                rootPane.getChildren().remove(panel);
                        });
                    }
                    controller.setOnCancelar(() -> {
                        javafx.scene.Node panel = rootPane.lookup("#panel_flotante");
                        if (panel != null)
                            rootPane.getChildren().remove(panel);
                    });
                });
    }

    public static void mostrarFormularioCambioClave(javafx.scene.layout.AnchorPane parentPane,
            application.model.Usuario usuario, String modo, Runnable onGuardar) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    FormUsuarioModalLauncher.class.getResource("/views/usuario/form_cambio_clave.fxml"));
            final javafx.scene.layout.AnchorPane formPane = loader.load();

            // Estilo moderno y tamaño cuadrado
            formPane.setPrefWidth(320);
            formPane.setMaxWidth(320);
            formPane.setPrefHeight(320);
            formPane.setMaxHeight(320);
            formPane.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.98);" +
                            "-fx-background-radius: 18 0 0 18;" +
                            "-fx-effect: dropshadow(gaussian, #222, 32, 0.18, -8, 0);");

            // Crear overlay semitransparente sobre la ventana principal
            javafx.scene.Scene scene = parentPane.getScene();
            javafx.scene.layout.AnchorPane rootPane = (javafx.scene.layout.AnchorPane) scene.getRoot();
            javafx.scene.layout.AnchorPane overlay = new javafx.scene.layout.AnchorPane();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.35);");
            javafx.scene.layout.AnchorPane.setTopAnchor(overlay, 0.0);
            javafx.scene.layout.AnchorPane.setRightAnchor(overlay, 0.0);
            javafx.scene.layout.AnchorPane.setBottomAnchor(overlay, 0.0);
            javafx.scene.layout.AnchorPane.setLeftAnchor(overlay, 0.0);

            // Panel cuadrado anclado al lado derecho del overlay
            formPane.setPrefWidth(320);
            formPane.setMaxWidth(320);
            formPane.setPrefHeight(320);
            formPane.setMaxHeight(320);
            javafx.scene.layout.AnchorPane.setTopAnchor(formPane, 0.0);
            javafx.scene.layout.AnchorPane.setRightAnchor(formPane, 0.0);
            javafx.scene.layout.AnchorPane.setBottomAnchor(formPane, 0.0);

            // Animación de entrada deslizante
            formPane.setTranslateX(320);
            javafx.animation.TranslateTransition tt = new javafx.animation.TranslateTransition(
                    javafx.util.Duration.millis(320), formPane);
            tt.setFromX(320);
            tt.setToX(0);
            tt.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
            tt.play();

            overlay.getChildren().add(formPane);
            rootPane.getChildren().add(overlay);

            FormCambioClaveController controller = loader.getController();
            controller.setUsuario(usuario);
            controller.setModo(modo);
            if (onGuardar != null) {
                controller.setOnGuardar(() -> {
                    onGuardar.run();
                    rootPane.getChildren().remove(overlay);
                });
            } else {
                controller.setOnGuardar(() -> rootPane.getChildren().remove(overlay));
            }
            controller.setOnCancelar(() -> rootPane.getChildren().remove(overlay));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Muestra el formulario de usuario como panel flotante cuadrado e
     * independiente,
     * anclado al lado derecho de la ventana principal, sin overlay ni modal.
     */
    public static void mostrarPanelUsuarioIndependiente(javafx.scene.Scene scene, application.model.Usuario usuario,
            Runnable onGuardar) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    FormUsuarioModalLauncher.class.getResource("/views/usuario/form_usuario.fxml"));
            final javafx.scene.layout.AnchorPane formPane = loader.load();

            formPane.setId("panel_flotante");
            formPane.setPrefWidth(420);
            formPane.setMaxWidth(420);
            formPane.setPrefHeight(650);
            formPane.setMaxHeight(650);
            formPane.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.98);" +
                            "-fx-background-radius: 18 0 0 18;" +
                            "-fx-effect: dropshadow(gaussian, #222, 32, 0.18, -8, 0);");

            javafx.scene.layout.AnchorPane rootPane = (javafx.scene.layout.AnchorPane) scene.getRoot();
            double rootHeight = rootPane.getHeight();
            double y = (rootHeight - 650) / 2;
            javafx.scene.layout.AnchorPane.setTopAnchor(formPane, y);
            javafx.scene.layout.AnchorPane.setRightAnchor(formPane, 0.0);

            // Eliminar solo el panel flotante previo si existe
            javafx.scene.Node panelPrevio = null;
            for (javafx.scene.Node child : rootPane.getChildren()) {
                if ("panel_flotante".equals(child.getId())) {
                    panelPrevio = child;
                    break;
                }
            }
            if (panelPrevio != null) {
                rootPane.getChildren().remove(panelPrevio);
            }

            formPane.setTranslateX(420);
            javafx.animation.TranslateTransition tt = new javafx.animation.TranslateTransition(
                    javafx.util.Duration.millis(320), formPane);
            tt.setFromX(420);
            tt.setToX(0);
            tt.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
            tt.play();

            rootPane.getChildren().add(formPane);

            FormUsuarioController controller = loader.getController();
            controller.setUsuario(usuario);
            if (onGuardar != null) {
                controller.setOnGuardar(() -> {
                    onGuardar.run();
                    rootPane.getChildren().remove(formPane);
                });
            } else {
                controller.setOnGuardar(() -> rootPane.getChildren().remove(formPane));
            }
            controller.setOnCancelar(() -> rootPane.getChildren().remove(formPane));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Muestra cualquier formulario como panel flotante independiente, anclado al
     * lado derecho y centrado verticalmente.
     * 
     * @param scene            Escena principal
     * @param fxmlPath         Ruta del archivo FXML
     * @param width            Ancho del panel
     * @param height           Alto del panel
     * @param controllerSetter Función para configurar el controlador (puede ser
     *                         null)
     */
    public static void mostrarPanelFlotanteGenerico(javafx.scene.Scene scene, String fxmlPath, double width,
            double height, java.util.function.Consumer<Object> controllerSetter) {
        try {
            FXMLLoader loader = new FXMLLoader(FormUsuarioModalLauncher.class.getResource(fxmlPath));
            javafx.scene.Parent loadedForm = loader.load();

            // El panel flotante debe ser un Node, no necesariamente un Pane
            javafx.scene.Node formPanel = loadedForm;
            formPanel.setId("panel_flotante");
            formPanel.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.98);" +
                            "-fx-background-radius: 18 0 0 18;" +
                            "-fx-effect: dropshadow(gaussian, #222, 32, 0.18, -8, 0);");
            if (formPanel instanceof javafx.scene.layout.Region) {
                ((javafx.scene.layout.Region) formPanel).setPrefWidth(width);
                ((javafx.scene.layout.Region) formPanel).setMaxWidth(width);
                ((javafx.scene.layout.Region) formPanel).setPrefHeight(height);
                ((javafx.scene.layout.Region) formPanel).setMaxHeight(height);
            }

            javafx.scene.Parent root = scene.getRoot();
            double rootHeight = 0;
            javafx.collections.ObservableList<javafx.scene.Node> children = null;
            if (root instanceof javafx.scene.layout.Pane) {
                children = ((javafx.scene.layout.Pane) root).getChildren();
                rootHeight = ((javafx.scene.layout.Pane) root).getHeight();
            } else if (root instanceof javafx.scene.control.ScrollPane) {
                javafx.scene.control.ScrollPane scrollPane = (javafx.scene.control.ScrollPane) root;
                javafx.scene.Node content = scrollPane.getContent();
                if (content instanceof javafx.scene.layout.Pane) {
                    children = ((javafx.scene.layout.Pane) content).getChildren();
                    rootHeight = ((javafx.scene.layout.Pane) content).getHeight();
                } else if (content instanceof javafx.scene.layout.VBox) {
                    children = ((javafx.scene.layout.VBox) content).getChildren();
                    rootHeight = ((javafx.scene.layout.VBox) content).getHeight();
                } else {
                    System.err.println(
                            "El contenido del ScrollPane no es un Pane ni VBox. No se puede mostrar el panel flotante.");
                    return;
                }
            } else {
                System.err.println("No se encontró un contenedor adecuado para el panel flotante");
                return;
            }

            double y = (rootHeight - height) / 2;
            if (root instanceof javafx.scene.layout.AnchorPane) {
                javafx.scene.layout.AnchorPane.setTopAnchor(formPanel, y);
                javafx.scene.layout.AnchorPane.setRightAnchor(formPanel, 0.0);
            }

            // Eliminar panel flotante previo si existe
            javafx.scene.Node panelPrevio = null;
            for (javafx.scene.Node child : children) {
                if ("panel_flotante".equals(child.getId())) {
                    panelPrevio = child;
                    break;
                }
            }
            if (panelPrevio != null) {
                children.remove(panelPrevio);
            }

            formPanel.setTranslateX(width);
            javafx.animation.TranslateTransition tt = new javafx.animation.TranslateTransition(
                    javafx.util.Duration.millis(320), formPanel);
            tt.setFromX(width);
            tt.setToX(0);
            tt.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
            tt.play();

            // Agregar el panel flotante al contenedor adecuado
            children.add(formPanel);

            Object controller = loader.getController();
            if (controllerSetter != null) {
                controllerSetter.accept(controller);
            }
            // Se recomienda que el controllerSetter configure los eventos de
            // guardar/cancelar para cerrar el panel
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
