package application.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXMLTestLoader extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            System.out.println("🔧 Intentando cargar el FXML...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/cliente/form_cliente.fxml"));
            Scene scene = new Scene(loader.load());
            
            primaryStage.setTitle("Test Form Cliente");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            System.out.println("✅ FXML cargado exitosamente!");
        } catch (Exception e) {
            System.err.println("❌ Error al cargar FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
