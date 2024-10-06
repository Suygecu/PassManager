package com.suygecu.testpepsa.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class TaskMangerApp extends Application {

    private TaskManager taskManager = new TaskManager();


    private ListView<Task> taskListView;


    public static void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);

            // Добавляем кнопку "ОК"
            alert.getButtonTypes().setAll(ButtonType.OK);

            // Отображаем окно и ждём нажатия
            alert.showAndWait();
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        ClientHandler.connectToServer();
        loadTasksFromDatabase(DatabaseConnection.getConnection());


        Button button = new Button("Добавить задачу");


        taskListView = new ListView<>(taskManager.getObservableTasks());


        taskListView.setCellFactory(param -> new ListCell<>() {
            private final Button editButton = new Button("Редактировать");

            {

                editButton.setOnAction(event -> {
                    Task selectedTask = getItem();
                    if (selectedTask != null) {
                        openEditTaskWindow(selectedTask);
                    }
                });
            }


            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(task.toString());
                    setGraphic(editButton);
                }
            }
        });


        button.setOnAction((actionEvent -> {
            openAddTaskWindow();
        }));


        VBox layout = new VBox(5);
        layout.getChildren().addAll(taskListView, button);


        Scene scene = new Scene(layout, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Мой задачник");
        primaryStage.show();

    }


    private void loadTasksFromDatabase(Connection connection) {
        String selectTasksSQL = "SELECT title, description, date FROM tasks";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectTasksSQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDate date = resultSet.getDate("date") != null
                        ? resultSet.getDate("date").toLocalDate() : null;

                Task task = new Task(title, description, date);

                if(!taskManager.getObservableTasks().contains(task)){
                    taskManager.addTask(task);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить задачи из базы данных.");
        }
    }
    public static void saveNewTaskToDatabase(Task task, Connection connection) throws SQLException {
        System.out.println("Сохраняем новую задачу в базу данных: " + task);
        String insertTaskSQL = "INSERT INTO tasks (title, description, date) VALUES (?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insertTaskSQL);
            preparedStatement.setString(1, task.getTitle());
            preparedStatement.setString(2, task.getDescription());
            if (task.getDate() != null) {
                preparedStatement.setDate(3, java.sql.Date.valueOf(task.getDate()));
            } else {
                preparedStatement.setNull(3, java.sql.Types.DATE);
            }

            preparedStatement.executeUpdate();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }


    }


    private void openEditTaskWindow(Task task) {

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Редактировать задачу");
        window.setResizable(true);


        TextField taskNameField = new TextField(task.getTitle());
        TextArea taskDescriptionField = new TextArea(task.getDescription());
        DatePicker datePicker = new DatePicker(task.getDate());

        Button submitButton = new Button("Сохранить изменения");


        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);


        gridPane.add(new Label("Название задачи"), 0, 0);
        gridPane.add(taskNameField, 1, 0);
        gridPane.add(new Label("Описание задачи"), 0, 1);
        gridPane.add(taskDescriptionField, 1, 1);
        gridPane.add(new Label("Дата задачи"), 0, 2);
        gridPane.add(datePicker, 1, 2);


        VBox dialogLayout = new VBox(10);
        dialogLayout.getChildren().addAll(gridPane, submitButton);

        Scene dialogScene = new Scene(dialogLayout, 600, 450);
        window.setScene(dialogScene);
        window.showAndWait();
    }




        private void openAddTaskWindow() {
            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Добавить новую задачу");
            window.setResizable(true);

            // Поля для добавления новой задачи
            TextField taskNameField = new TextField();
            taskNameField.setPromptText("Название задачи");

            TextArea taskDescriptionField = new TextArea();
            taskDescriptionField.setPromptText("Описание задачи");
            taskDescriptionField.setPrefHeight(100);
            taskDescriptionField.setWrapText(true);

            DatePicker datePicker = new DatePicker();

            Button submitButton = new Button("Добавить");


            GridPane gridPane = new GridPane();
            gridPane.setHgap(10);
            gridPane.setVgap(10);


            GridPane.setHgrow(taskNameField, Priority.ALWAYS);
            GridPane.setHgrow(taskDescriptionField, Priority.ALWAYS);
            GridPane.setVgrow(taskDescriptionField, Priority.ALWAYS);

            gridPane.add(new Label("Название задачи"), 0, 0);
            gridPane.add(taskNameField, 1, 0);
            gridPane.add(new Label("Описание задачи"), 0, 1);
            gridPane.add(taskDescriptionField, 1, 1);
            gridPane.add(new Label("Дата задачи"), 0, 2);
            gridPane.add(datePicker, 1, 2);


            submitButton.setOnAction(e -> {
                String taskName = taskNameField.getText();
                String taskDescription = taskDescriptionField.getText();
                LocalDate taskDate = datePicker.getValue();

                if (taskName.isEmpty() || taskDescription.isEmpty()) {
                    showAlert("Ошибка", "Пожалуйста, заполните все поля");
                } else {
                    Task newTask = new Task(taskName, taskDescription, taskDate);


                    new Thread(() -> {
                        try {
                            ClientHandler.clientHandler.sendTask(newTask);


                            Platform.runLater(() -> {
                                taskManager.addTask(newTask);
                                taskListView.setItems(taskManager.getObservableTasks());
                                window.close();


                                showAlert("Задача добавлена", "Задача: " + taskName + "\nОписание: " + taskDescription);
                            });

                        } catch (IOException ex) {
                            ex.printStackTrace();
                            Platform.runLater(() -> showAlert("Ошибка", "Не удалось отправить задачу на сервер"));
                        }
                    }).start();
                }
            });

            VBox dialogLayout = new VBox(10);
            dialogLayout.getChildren().addAll(gridPane, submitButton);


            Scene dialogScene = new Scene(dialogLayout, 600, 450);
            window.setScene(dialogScene);
            window.showAndWait();
        }

    }

