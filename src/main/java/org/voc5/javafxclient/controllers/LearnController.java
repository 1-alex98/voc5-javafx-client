package org.voc5.javafxclient.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.voc5.javafxclient.JavaFxMain;
import org.voc5.javafxclient.dto.Vocabulary;
import org.voc5.javafxclient.services.ApiService;

import java.io.IOException;

public class LearnController {
    public Label phaseLabel;
    public TextArea questionTextArea;
    public TextArea answerTextArea;
    public TextArea solutionTextArea;
    public Button revealButton;
    public Button inCorrectButton;
    public Button correctButton;
    public Label languageLabel;
    private Vocabulary vocabulary;

    private static void errorDialog(String title, String detail) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setContentText(detail);
            alert.showAndWait();
        });
    }

    public void initialize() {
        ApiService.getInstance().getRandomVocabulary()
                .thenAccept(vocabulary -> Platform.runLater(() -> setVocabulary(vocabulary)))
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    errorDialog("Loading failed", "The error was: " + throwable.getMessage());
                    return null;
                });
        revealButton.managedProperty().bind(revealButton.visibleProperty());
    }

    private void setVocabulary(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
        if (vocabulary == null) {
            errorDialog("No more vocabularies", "Go add some new...");
            return;
        }
        if (vocabulary.getPhase() != null) {
            phaseLabel.setText("Phase: " + vocabulary.getPhase());
        }
        if (vocabulary.getLanguage() != null) {
            languageLabel.setText("Language: " + vocabulary.getLanguage());
        }
        questionTextArea.setText(vocabulary.getQuestion());
        answerTextArea.requestFocus();
        solutionTextArea.setText(vocabulary.getAnswer());
    }

    public void inCorrect(ActionEvent actionEvent) {
        Vocabulary patch = new Vocabulary();
        patch.setId(vocabulary.getId());
        patch.setPhase(Math.max(0, vocabulary.getPhase() - 1));
        ApiService.getInstance().updateVocabulary(patch)
                .thenRun(() -> {
                    try {
                        JavaFxMain.loadLearn();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    errorDialog("Could not update vocabulary", "Error was: " + throwable.getMessage());
                    return null;
                });
    }

    public void correct(ActionEvent actionEvent) {
        Vocabulary patch = new Vocabulary();
        patch.setId(vocabulary.getId());
        patch.setPhase(Math.min(5, vocabulary.getPhase() + 1));
        ApiService.getInstance().updateVocabulary(patch)
                .thenRun(() -> {
                    try {
                        JavaFxMain.loadLearn();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    errorDialog("Could not update vocabulary", "Error was: " + throwable.getMessage());
                    return null;
                });
    }

    public void back(ActionEvent actionEvent) throws IOException {
        JavaFxMain.loadMain();
    }

    public void reveal(ActionEvent actionEvent) {
        revealButton.setVisible(false);
        inCorrectButton.setVisible(true);
        correctButton.setVisible(true);
        solutionTextArea.setVisible(true);
    }
}
