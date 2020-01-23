package org.voc5.javafxclient.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import org.jetbrains.annotations.Nullable;
import org.voc5.javafxclient.JavaFxMain;
import org.voc5.javafxclient.dto.Vocabulary;
import org.voc5.javafxclient.services.ApiService;

import java.io.IOException;
import java.util.function.Predicate;

public class VocabularyTableController {
    public TableView<Vocabulary> tableView;
    public TableColumn<Vocabulary, String> questionColumn;
    public TableColumn<Vocabulary, String> answerColumn;
    public TableColumn<Vocabulary, String> languageColumn;
    public TableColumn<Vocabulary, Integer> phaseColumn;
    public TextField filterField;
    private FilteredList<Vocabulary> vocabulariesFiltered;
    private Predicate<Vocabulary> predicate;

    public void initialize() {
        questionColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getQuestion()));
        answerColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAnswer()));
        languageColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLanguage()));
        phaseColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getPhase()));

        questionColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        answerColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        languageColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        phaseColumn.setCellFactory(TextAreaTableCell.forTableColumn(new IntegerStringConverter()));

        questionColumn.setEditable(true);
        answerColumn.setEditable(true);
        languageColumn.setEditable(true);
        phaseColumn.setEditable(true);

        questionColumn.setOnEditCommit(event -> {
            Vocabulary rowValue = event.getRowValue();
            Vocabulary vocabulary = new Vocabulary();
            vocabulary.setQuestion(event.getNewValue());
            vocabulary.setId(rowValue.getId());
            ApiService.getInstance().updateVocabulary(vocabulary)
                    .thenRun(this::updateTable)
                    .exceptionally(throwable -> onError(throwable, "Error updating vocabulary question"));
        });

        answerColumn.setOnEditCommit(event -> {
            Vocabulary rowValue = event.getRowValue();
            Vocabulary vocabulary = new Vocabulary();
            vocabulary.setAnswer(event.getNewValue());
            vocabulary.setId(rowValue.getId());
            ApiService.getInstance().updateVocabulary(vocabulary)
                    .thenRun(this::updateTable)
                    .exceptionally(throwable -> onError(throwable, "Error updating vocabulary answer"));
        });

        languageColumn.setOnEditCommit(event -> {
            Vocabulary rowValue = event.getRowValue();
            Vocabulary vocabulary = new Vocabulary();
            vocabulary.setLanguage(event.getNewValue());
            vocabulary.setId(rowValue.getId());
            ApiService.getInstance().updateVocabulary(vocabulary)
                    .thenRun(this::updateTable)
                    .exceptionally(throwable -> onError(throwable, "Error updating vocabulary language"));
        });

        phaseColumn.setOnEditCommit(event -> {
            Vocabulary rowValue = event.getRowValue();
            Vocabulary vocabulary = new Vocabulary();
            vocabulary.setPhase(event.getNewValue());
            vocabulary.setId(rowValue.getId());
            ApiService.getInstance().updateVocabulary(vocabulary)
                    .thenRun(this::updateTable)
                    .exceptionally(throwable -> onError(throwable, "Error updating vocabulary phase"));
        });

        ContextMenu contextMenu = tableView.getContextMenu() == null ? new ContextMenu() : tableView.getContextMenu();
        MenuItem deleteSelectedMenuItem = new MenuItem("Delete Selected");
        contextMenu.getItems().add(deleteSelectedMenuItem);
        deleteSelectedMenuItem.setOnAction(actionEvent -> {
            Vocabulary selectedItem = tableView.getSelectionModel().getSelectedItem();
            ApiService.getInstance().delete(selectedItem.getId())
                    .thenRun(this::updateTable)
                    .exceptionally(throwable -> onError(throwable, "Something went wrong deleting item"));
        });
        tableView.setContextMenu(contextMenu);

        updateTable();

        filterField.textProperty()
                .addListener((observableValue, newText, oldText) -> {
                    String text = filterField.getText();
                    if (text == null || text.isEmpty()) {
                        predicate = vocabulary -> true;
                        vocabulariesFiltered.setPredicate(predicate);
                        return;
                    }
                    Predicate<Vocabulary> newPredicate = vocabulary -> {
                        if (vocabulary.getAnswer() != null && vocabulary.getAnswer().toLowerCase().contains(text.toLowerCase())) {
                            return true;
                        }
                        if (vocabulary.getQuestion() != null && vocabulary.getQuestion().toLowerCase().contains(text.toLowerCase())) {
                            return true;
                        }
                        return vocabulary.getLanguage() != null && vocabulary.getLanguage().toLowerCase().contains(text.toLowerCase());
                    };
                    if (vocabulariesFiltered != null) {
                        vocabulariesFiltered.setPredicate(newPredicate);
                    }
                    predicate = newPredicate;
                });
    }

    private void updateTable() {
        ApiService.getInstance().getAllVocabulary()
                .thenAccept(vocabularies -> Platform.runLater(() -> {
                    ObservableList<Vocabulary> observableList = FXCollections.observableArrayList(vocabularies);
                    vocabulariesFiltered = new FilteredList<>(observableList);
                    if (predicate != null) {
                        vocabulariesFiltered.setPredicate(predicate);
                    }
                    tableView.setItems(vocabulariesFiltered);
                }))
                .exceptionally(throwable -> onError(throwable, "Error getting vocabulary from server"));
    }

    @Nullable
    private Void onError(Throwable throwable, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            alert.show();
        });
        throwable.printStackTrace();
        return null;
    }

    public void back(ActionEvent actionEvent) throws IOException {
        JavaFxMain.loadMain();
    }

    public void addNew(ActionEvent actionEvent) throws IOException {
        Stage newDialog = new Stage();
        newDialog.setTitle("Add new tutorial");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layout/addNewDialog.fxml"));
        Parent load = fxmlLoader.load();
        AddNewDialogController controller = fxmlLoader.getController();
        controller.setOnSave(this::updateTable);
        newDialog.setScene(new Scene(load));
        newDialog.showAndWait();
    }
}
