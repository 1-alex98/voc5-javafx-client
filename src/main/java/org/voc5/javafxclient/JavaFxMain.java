package org.voc5.javafxclient;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class JavaFxMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Button button = new Button("Hello World");
        Scene scene = new Scene(new HBox(button));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}