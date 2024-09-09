package com.suygecu.testpepsa;

import com.suygecu.testpepsa.EncryptionUtils;
import com.suygecu.testpepsa.PasswordEntry;
import com.suygecu.testpepsa.PasswordManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class Main  extends Application {

    private PasswordManager passwordManager;
    private ListView<String> listView;
    private TextField siteField;
    private TextField usernameField;
    private PasswordField passwordField;

    @Override
    public void start(Stage primaryStage) {
        String localFilePath = "passwords.dat"; // Путь к локальному файлу
        this.passwordManager = new PasswordManager(localFilePath);

        primaryStage.setTitle("Password Manager");

        listView = new ListView<>();

        siteField = new TextField();
        siteField.setPromptText("Site");

        usernameField = new TextField();
        usernameField.setPromptText("Username");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button addButton = new Button("Add");
        addButton.setOnAction(e -> addEntry());

        Button removeButton = new Button("Remove");
        removeButton.setOnAction(e -> removeEntry());

        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> updateEntry());

        Button loadButton = new Button("Load from Local");
        loadButton.setOnAction(e -> {
            passwordManager.loadFromLocal();
            displayEntries(passwordManager.viewEntries());
        });

        Button saveButton = new Button("Save to Local");
        saveButton.setOnAction(e -> passwordManager.saveToLocal());

        VBox vbox = new VBox(10, listView, siteField, usernameField, passwordField, addButton, removeButton, updateButton, loadButton, saveButton);

        Scene scene = new Scene(vbox, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addEntry() {
        String site = siteField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        passwordManager.addEntry(site, username, password);
        displayEntries(passwordManager.viewEntries());
        clearFields();
    }

    private void removeEntry() {
        String selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String site = selected.split(" - ")[0];
            passwordManager.removeEntry(site);
            displayEntries(passwordManager.viewEntries());
        }
    }

    private void updateEntry() {
        String site = siteField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        passwordManager.updateEntry(site, username, password);
        displayEntries(passwordManager.viewEntries());
        clearFields();
    }

    private void displayEntries(List<PasswordEntry> entries) {
        listView.getItems().clear();
        for (PasswordEntry entry : entries) {
            String decryptedPassword = EncryptionUtils.decrypt(entry.getEncryptedPassword());
            listView.getItems().add(entry.getSite() + " - " + entry.getUsername() + " - " + decryptedPassword);
        }
    }

    private void clearFields() {
        siteField.clear();
        usernameField.clear();
        passwordField.clear();
    }


}