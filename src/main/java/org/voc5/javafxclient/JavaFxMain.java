package org.voc5.javafxclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;


public class JavaFxMain extends Application {
    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    public static void loadMain() throws IOException {
        loadFxml("/layout/main.fxml");
    }

    public static void loadLearn() throws IOException {
        loadFxml("/layout/learn.fxml");
    }

    public static void loadLogin() throws IOException {
        loadFxml("/layout/login.fxml");
    }

    public static void loadTable() throws IOException {
        loadFxml("/layout/vocabularyTable.fxml");
    }

    private static void loadFxml(String s) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(JavaFxMain.class.getResource(s));
        Parent parent = fxmlLoader.load();
        primaryStage.getScene().setRoot(parent);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        JavaFxMain.primaryStage = primaryStage;
        Scene scene = new Scene(new AnchorPane());
        primaryStage.setScene(scene);
        loadLogin();
        primaryStage.show();
    }
}