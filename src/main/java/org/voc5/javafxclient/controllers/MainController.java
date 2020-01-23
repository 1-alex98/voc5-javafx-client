package org.voc5.javafxclient.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import org.voc5.javafxclient.JavaFxMain;
import org.voc5.javafxclient.services.ApiService;
import org.voc5.javafxclient.services.PreferenceService;

import java.io.IOException;

public class MainController {
    public Label loggedInAs;

    public void initialize() {
        loggedInAs.setText("Logged in as: " + ApiService.getInstance().getEmail());
    }

    public void learn(ActionEvent actionEvent) throws IOException {
        JavaFxMain.loadLearn();
    }

    public void edit(ActionEvent actionEvent) throws IOException {
        JavaFxMain.loadTable();
    }

    public void logOut(ActionEvent actionEvent) throws IOException {
        ApiService.getInstance().reset();
        PreferenceService.getInstance().getPreferences().setRemember(false);
        JavaFxMain.loadLogin();
    }
}
