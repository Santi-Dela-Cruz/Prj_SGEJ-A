package application;

import javafx.application.Application;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(javafx.stage.Stage primaryStage) {
        primaryStage.setTitle("My JavaFX Application");
        primaryStage.show();
    }


}
