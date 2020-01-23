package org.voc5.javafxclient.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.voc5.javafxclient.dto.Vocabulary;
import org.voc5.javafxclient.services.ApiService;

import java.util.ArrayList;
import java.util.List;

public class AddNewDialogController {
    public TextField languageField;
    public TextField answerField;
    public TextField questionField;
    private Runnable onSaveRunnable;

    private static void errorDialog(String title, String detail) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setContentText(detail);
            alert.showAndWait();
        });
    }

    public void setOnSave(Runnable onSaveRunnable) {
        this.onSaveRunnable = onSaveRunnable;
    }

    public void cancel(ActionEvent actionEvent) {
        close();
    }

    public void save(ActionEvent actionEvent) {
        if (!validate()) {
            return;
        }
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setLanguage(languageField.getText());
        vocabulary.setAnswer(answerField.getText());
        vocabulary.setQuestion(questionField.getText());
        ApiService.getInstance().saveVocabulary(vocabulary)
                .thenRun(() -> {
                    Platform.runLater(() -> {
                        onSaveRunnable.run();
                        close();
                    });
                })
                .exceptionally(throwable -> {
                    errorDialog("Error", "Could not save: " + throwable.getMessage());
                    return null;
                });
    }

    private boolean validate() {
        List<String> validationErrors = new ArrayList<>();
        if (answerField.getText().isEmpty()) {
            validationErrors.add("Answer can not be empty");
        }
        if (questionField.getText().isEmpty()) {
            validationErrors.add("Question name must be set");
        }
        if (languageField.getText().isEmpty()) {
            validationErrors.add("Language can not be empty");
        }
        if (validationErrors.size() > 0) {
            errorDialog("Validation failed",
                    String.join("\n", validationErrors));
            return false;
        }
        return true;
    }

    private void close() {
        Stage stage = (Stage) languageField.getScene().getWindow();
        stage.close();
    }

}
