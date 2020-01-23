package org.voc5.javafxclient.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.voc5.javafxclient.JavaFxMain;
import org.voc5.javafxclient.dto.Preferences;
import org.voc5.javafxclient.services.ApiService;
import org.voc5.javafxclient.services.PreferenceService;

import java.io.IOException;

public class LoginController {
    public TextField emailField;
    public PasswordField passwordField;
    public Label errorLabel;
    public CheckBox rememberMe;

    public void initialize() {
        errorLabel.managedProperty().bind(errorLabel.visibleProperty());
        Preferences preferences = PreferenceService.getInstance().getPreferences();
        if (preferences.isRemember()) {
            login(preferences.getEmail(), preferences.getPassword(), true);
        }
        if (preferences.getEmail() != null) {
            emailField.setText(preferences.getEmail());
        }
        if (preferences.getPassword() != null) {
            passwordField.setText(preferences.getPassword());
        }
    }

    public void register(ActionEvent actionEvent) {

    }

    public void login(ActionEvent actionEvent) throws IOException {
        login(emailField.getText(), passwordField.getText(), rememberMe.isSelected());
    }

    private void login(String email, String password, boolean auto) {
        ApiService.getInstance().login(email, password)
                .thenAccept(s -> {
                    Platform.runLater(() -> {
                        if (s != null && !s.isEmpty()) {
                            errorLabel.setVisible(true);
                            errorLabel.setText(s);
                            return;
                        }
                        try {
                            Preferences preferences = PreferenceService.getInstance().getPreferences();
                            if (auto) {
                                preferences.setEmail(email);
                                preferences.setRemember(true);
                                preferences.setPassword(password);
                            } else {
                                preferences.setEmail(null);
                                preferences.setRemember(false);
                                preferences.setPassword(null);
                            }
                            PreferenceService.getInstance().storeInBackGround();
                            JavaFxMain.loadMain();
                        } catch (IOException e) {
                            errorLabel.setVisible(true);
                            errorLabel.setText(e.getMessage());
                            e.printStackTrace();
                        }
                    });
                }).exceptionally(throwable -> {
            errorLabel.setVisible(true);
            errorLabel.setText(throwable.getMessage());
            throwable.printStackTrace();
            return null;
        });
    }
}
