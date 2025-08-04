package application.controllers.administracion_sistema;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.layout.AnchorPane;

import application.model.Parametro;

public class FormParametroModalLauncher {

    /**
     * Muestra el formulario de parámetro como un panel flotante
     * 
     * @param scene     La escena donde mostrar el panel flotante
     * @param parametro El parámetro a editar (null para crear nuevo)
     * @param onGuardar Acción a ejecutar cuando se guarde el parámetro
     */
    public static void mostrarPanelParametroFlotante(javafx.scene.Scene scene,
            Parametro parametro,
            Runnable onGuardar) {
        mostrarPanelFlotanteGenerico(
                scene,
                "/views/sistema/form_parametro.fxml",
                440, // ancho
                520, // alto
                controllerObj -> {
                    FormParametroController controller = (FormParametroController) controllerObj;

                    // Configurar el controlador con el parámetro y callbacks
                    if (parametro != null) {
                        controller.setParametro(parametro);
                    }

                    AnchorPane rootPane = (AnchorPane) scene.getRoot();
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

    /**
     * Muestra el formulario de parámetro como un diálogo modal
     * 
     * @param parametro El parámetro a editar (null para crear nuevo)
     * @param onGuardar Acción a ejecutar cuando se guarde el parámetro
     */
    public static void mostrarFormularioParametroModal(Parametro parametro,
            Runnable onGuardar) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    FormParametroModalLauncher.class.getResource("/views/sistema/form_parametro.fxml"));
            Parent root = loader.load();

            FormParametroController controller = loader.getController();

            // Configurar el controlador con el parámetro
            if (parametro != null) {
                System.out.println("======== MODAL LAUNCHER - DEPURACIÓN ========");
                System.out.println("Enviando parámetro al controlador");
                System.out.println("CÓDIGO: " + parametro.getCodigo());
                System.out.println("NOMBRE: " + parametro.getNombre());
                System.out.println("=========================================");
                controller.setParametro(parametro);
            }

            // Configurar acciones de guardado y cancelación
            Stage stage = new Stage();

            controller.setOnGuardar(() -> {
                if (onGuardar != null) {
                    onGuardar.run();
                }
                stage.close();
            });

            controller.setOnCancelar(() -> {
                stage.close();
            });

            // Configurar el stage
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle(parametro != null ? "Editar Parámetro" : "Nuevo Parámetro");
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método genérico para mostrar un panel flotante
     */
    private static void mostrarPanelFlotanteGenerico(Scene scene, String fxmlPath,
            double width, double height,
            java.util.function.Consumer<Object> setupController) {
        try {
            // Cargar el FXML
            FXMLLoader loader = new FXMLLoader(FormParametroModalLauncher.class.getResource(fxmlPath));
            final AnchorPane formPane = loader.load();

            // Configurar tamaño
            formPane.setPrefWidth(width);
            formPane.setMaxWidth(width);
            formPane.setPrefHeight(height);
            formPane.setMaxHeight(height);
            formPane.setStyle(
                    formPane.getStyle() + "; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0.5, 0.0, 0.0);");

            // Añadir ID para poder localizarlo después
            formPane.setId("panel_flotante");

            // Configurar el controlador
            Object controller = loader.getController();
            setupController.accept(controller);

            // Añadir a la escena principal
            AnchorPane rootPane = (AnchorPane) scene.getRoot();

            // Calcular posición (centrado)
            double centerX = rootPane.getWidth() / 2;
            double centerY = rootPane.getHeight() / 2;
            AnchorPane.setTopAnchor(formPane, centerY - (height / 2));
            AnchorPane.setLeftAnchor(formPane, centerX - (width / 2));

            rootPane.getChildren().add(formPane);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
